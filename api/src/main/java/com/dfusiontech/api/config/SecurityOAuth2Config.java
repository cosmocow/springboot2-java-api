package com.dfusiontech.api.config;

import com.dfusiontech.server.auth.OAuthClientDetailsService;
import com.dfusiontech.server.auth.mfa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Application OAuth2 Security Configuration.
 *
 * @author Eugene A. Kalosha <ekalosha@dfusiontech.com>
 */
@Configuration
@EnableAuthorizationServer
@Order(100)
public class SecurityOAuth2Config extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private OAuthClientDetailsService oAuthClientDetailsService;

	@Autowired
	private MultiFactorAuthenticationService multiFactorAuthenticationService;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	/**
	 * Configure Client Details service for oAuth
	 *
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(oAuthClientDetailsService);
	}

	/**
	 * Configure Tokens managers and Endpoints
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager);
		endpoints.tokenStore(tokenStore);
		endpoints.userDetailsService(userDetailsService);
		endpoints.tokenGranter(tokenGranter(endpoints));
	}

	/**
	 * Create Tokens Granter for the Application
	 *
	 * @param endpoints
	 * @return
	 */
	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {

		// List<TokenGranter> granters = new ArrayList<>(List.of(endpoints.getTokenGranter()));
		// Clear all Default Token Granters
		List<TokenGranter> granters = new ArrayList<>();
		granters.add(new PasswordTokenGranter(endpoints, authenticationManager, multiFactorAuthenticationService));
		granters.add(new MultiFactorAuthenticationTokenGranter(endpoints, authenticationManager, multiFactorAuthenticationService));
		granters.add(new RefreshTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
		granters.add(new GoogleTokenGranter(endpoints, authenticationManager, multiFactorAuthenticationService));
		granters.add(new MicrosoftTokenGranter(endpoints, authenticationManager, multiFactorAuthenticationService));

		return new CompositeTokenGranter(granters);
	}

}
