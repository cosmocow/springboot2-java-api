package com.dfusiontech.server.auth;

import com.dfusiontech.server.auth.mfa.*;
import com.dfusiontech.server.model.config.ApplicationProperties;
import com.dfusiontech.server.model.config.CacheNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OAuth Client Service
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2020-07-10
 */
@Service
@Slf4j
public class OAuthClientDetailsService implements ClientDetailsService {

	private static final Map<String, ClientDetails> CLIENTS_MAP = new HashMap<>();

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Environment environment;

	@PostConstruct
	private void initialize() {
		CLIENTS_MAP.clear();

		registerClient("web", OAuthClientAuthority.MFA);
		registerClient("ios", OAuthClientAuthority.MFA_IOS);
		registerClient("android", OAuthClientAuthority.MFA_ANDROID);
	}

	/**
	 * Load Client by its client ID
	 *
	 * @param clientId
	 * @return
	 * @throws ClientRegistrationException
	 */
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		if (!CLIENTS_MAP.containsKey(clientId)) {
			throw new ClientRegistrationException("oAuth Client is not registered in the system: " + clientId);
		}

		return CLIENTS_MAP.get(clientId);
	}

	/**
	 * Registering client from the config
	 *
	 * @param clientType
	 * @param mfaType
	 */
	private void registerClient(String clientType, String mfaType) {
		if (environment.containsProperty("application.oauth.client." + clientType + ".id")) {
			ClientDetails clientDetails = buildOAuthClientDetails(
				environment.getProperty("application.oauth.client." + clientType + ".id"),
				environment.getProperty("application.oauth.client." + clientType + ".secret"),
				OAuthClientAuthority.USER, mfaType
			);
			CLIENTS_MAP.put(clientDetails.getClientId(), clientDetails);
			log.info("@@@@ Registering oAuth Client: " + clientDetails.getClientId());
		}
	}

	/**
	 * Build oAuth Client Details based on different id/secret/authorities
	 *
	 * @param oauthClientId
	 * @param oauthClientSecret
	 * @param grantedAuthorities
	 * @return
	 */
	private ClientDetails buildOAuthClientDetails(String oauthClientId, String oauthClientSecret, String... grantedAuthorities) {

		BaseClientDetails clientDetails = new BaseClientDetails();
		clientDetails.setClientId(oauthClientId);
		clientDetails.setClientSecret(passwordEncoder.encode(oauthClientSecret));
		clientDetails.setAccessTokenValiditySeconds(10800); // Access token to live 3 hours
		clientDetails.setRefreshTokenValiditySeconds(2592000); // Refresh token to be valid for 30 days
		clientDetails.setScope(Arrays.asList("read", "write")); // Scope related to resource
		clientDetails.setAuthorizedGrantTypes(Arrays.asList(
			PasswordTokenGranter.GRANT_TYPE, "refresh_token", MultiFactorAuthenticationTokenGranter.GRANT_TYPE, GoogleTokenGranter.GRANT_TYPE, MicrosoftTokenGranter.GRANT_TYPE)
		); // grant types
		clientDetails.setAuthorities(Arrays.stream(grantedAuthorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList())); // granted roles

		return clientDetails;
	}
}
