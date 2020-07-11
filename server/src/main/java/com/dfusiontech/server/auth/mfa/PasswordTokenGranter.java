package com.dfusiontech.server.auth.mfa;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordTokenGranter extends AbstractTokenGranter {

	public static final String GRANT_TYPE = "password";
	private static final GrantedAuthority PRE_AUTH = new SimpleGrantedAuthority("PRE_AUTH");

	private final AuthenticationManager authenticationManager;
	private final MultiFactorAuthenticationService multiFactorAuthenticationService;

	/*
	protected PasswordTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
	}
	*/

	public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer, AuthenticationManager authenticationManager, MultiFactorAuthenticationService multiFactorAuthenticationService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);

		this.authenticationManager = authenticationManager;
		this.multiFactorAuthenticationService = multiFactorAuthenticationService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		String username = parameters.get("username");
		String password = parameters.get("password");
		// Protect from downstream leaks of password
		parameters.remove("password");

		Authentication userAuthentication = new UsernamePasswordAuthenticationToken(username, password);
		((AbstractAuthenticationToken) userAuthentication).setDetails(parameters);

		try {
			userAuthentication = this.authenticationManager.authenticate(userAuthentication);
		}catch (AccountStatusException ase) {
			//covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
			throw new InvalidGrantException(ase.getMessage());
		} catch (BadCredentialsException e) {
			// If the username/password are wrong the spec says we should send 400/invalid grant
			throw new InvalidGrantException(e.getMessage());
		}
		if (userAuthentication == null || !userAuthentication.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate user: " + username);
		}

		// Proceed with the Authentication Flow
		if (userAuthentication != null && userAuthentication.isAuthenticated()) {

			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);

			// Save User Login Audit Log
			multiFactorAuthenticationService.auditUserPasswordLogin(userAuthentication);

			if (multiFactorAuthenticationService.isEnabled(username, client)) {
				userAuthentication = new UsernamePasswordAuthenticationToken(username, password, Collections.singleton(PRE_AUTH));
				OAuth2AccessToken accessToken = getTokenServices().createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuthentication));

				// Throw Multi Factor Exception in case if Additional STEP is required
				multiFactorAuthenticationService.generateCode(accessToken.getValue(), username);
				throw new MFARequiredException(accessToken.getValue());
			}

			// Return oAuth 2 Authentication
			return new OAuth2Authentication(storedOAuth2Request, userAuthentication);
		} else {
			throw new InvalidGrantException("Could not authenticate user: " + username);
		}
	}

}
