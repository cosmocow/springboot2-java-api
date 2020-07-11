package com.dfusiontech.server.auth.mfa;

import com.dfusiontech.server.model.jpa.domain.IdpType;
import com.dfusiontech.server.rest.exception.ForbiddenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Google Token Granter
 *
 * @author Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 */
public class GoogleTokenGranter extends AbstractTokenGranter {

	public static final String GRANT_TYPE = "google";

	private final AuthenticationManager authenticationManager;

	private final MultiFactorAuthenticationService multiFactorAuthenticationService;

	public GoogleTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer, AuthenticationManager authenticationManager, MultiFactorAuthenticationService multiFactorAuthenticationService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.authenticationManager = authenticationManager;
		this.multiFactorAuthenticationService = multiFactorAuthenticationService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		return getGoogleOAuth2Authentication(client, tokenRequest);
	}

	@Transactional
	public OAuth2Authentication getGoogleOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		String idTokenString = parameters.get("code");


		// Google API flow
		// (For more information please visit: https://developers.google.com/identity/sign-in/web/backend-auth#using-a-google-api-client-library)
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
			// Specify the CLIENT_ID of the app that accesses the backend:
			// Or, if multiple clients access the backend:
			//.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			.build();

		try {
			GoogleIdToken idToken = verifier.verify(idTokenString);

			if (idToken != null) {
				GoogleIdToken.Payload payload = idToken.getPayload();

				// User identifier
				String userId = payload.getSubject();

				// Get profile information from payload
				String email = payload.getEmail();
				boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());

				if(StringUtils.isEmpty(email) && !emailVerified) {
					throw new ForbiddenException(MessageFormat.format("Google Account email ({0}) is not verified.", email));
				}
				UserDetails userDetails = multiFactorAuthenticationService.getIDPUserByIdentityAndType(email, IdpType.GOOGLE);

				OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);

				Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				// audit log
				multiFactorAuthenticationService.auditUserGoogleLogin(userAuthentication);

				return new OAuth2Authentication(storedOAuth2Request, userAuthentication);

			} else {
				throw new InvalidGrantException("Could not authenticate user by given token: " + idTokenString);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new InvalidGrantException(ex.getMessage());

		} catch (GeneralSecurityException ex) {
			ex.printStackTrace();
			throw new InvalidGrantException(ex.getMessage());
		}
	}

}
