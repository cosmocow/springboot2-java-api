package com.dfusiontech.api.config;

import com.dfusiontech.server.auth.CustomJdbcUserDetailsManager;
import com.dfusiontech.server.auth.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

/**
 * Spring Application Security Configuration.
 *
 * @author Eugene A. Kalosha <ekalosha@dfusiontech.com>
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Include DataSource dependency
	 */
	@Autowired
	private DataSource dataSource;

	/**
	 * Include JDBC User Details Manager
	 */
	@Autowired
	private CustomJdbcUserDetailsManager customJdbcUserDetailsManager;

	@Autowired
	public WebSecurityConfig(final CustomJdbcUserDetailsManager customJdbcUserDetailsManager) {
		this.customJdbcUserDetailsManager = customJdbcUserDetailsManager;
	}

	/**
	 * Since Spring Security 2.0.6 we must expose AuthenticationManager for oAuth manually
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * Configuring Authentication
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Set Authorization with Database Users with BCrypt password encoder.
		auth.apply(new JdbcUserDetailsManagerConfigurer<>(jdbcUserDetailsManager())).passwordEncoder(passwordEncoder());
	}

	/**
	 * Configuring HTTP Security for the Application
	 *
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Disable Sessions for API Application
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().none();

		http
			.antMatcher("/**")
			.authorizeRequests()
			.antMatchers(
				"/",
				"/login**",
				"/swagger**",
				"/swagger/**",
				"/webjars/springfox-swagger-ui/**",
				"/swagger-resources/**",
				"/csrf",
				"/oauth/**"
			)
			.permitAll();

		// Allow actuator enpoints. eg: /health, /info etc.
		http.authorizeRequests().antMatchers("/actuator/**", "/api/info/**", "/api/anonymous/**").permitAll();
	}


	/**
	 * PasswordEncoder Bean definition. Used to encode/match passwords.
	 *
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		/**
		 * Create DEFAULT BCRYPT Password Encoder from Spring Security
		 */
		return new BCryptPasswordEncoder();
	}

	/**
	 * Jdbc User Details Manager definition.
	 *
	 * @return
	 */
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() {
		return customJdbcUserDetailsManager;
	}

	/**
	 * Since Spring Security 2.0.6 we must instantiate UserDetailsService for oAuth manually
	 *
	 * @return
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	/**
	 * Token Store Implementation
	 *
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

}
