package com.dfusiontech.api.config;


import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2WebMvc
@PropertySource(name = "swagger", value = "classpath:swagger.properties")
@Profile({"documentation", "swagger"})
public class SwaggerConfig implements WebMvcConfigurer {

	private String clientId = "username";

	private String clientSecret = "password";

	@Autowired
	public SwaggerConfig() {
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	/**
	 * Creates swagger configuration.
	 *
	 * @return swagger configuration docket
	 */
	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.dfusiontech.api.controller"))
			.paths(PathSelectors.any())
			.build()
			.pathMapping("/")
			.directModelSubstitute(DateTime.class, String.class)
			.directModelSubstitute(Enum.class, String.class)
			.securitySchemes(Collections.singletonList(securitySchema()))
			.apiInfo(
				new ApiInfoBuilder()
					.title("dFusion Tech, REST API")
					.description("Core API documentation")
					.version("0.1.1")
					.contact(new Contact("Eugene A. Kalosha", "https://dfusiontech.com", "ekalosha@dfusiontech.com"))
					.build()
			);
	}


	@Bean
	public SwaggerMapperConfig swaggerMapper() {
		return new SwaggerMapperConfig();
	}

	@Bean
	public SecurityConfiguration securityConfiguration() {
		return new SwaggerSecurityConfig(HttpHeaders.AUTHORIZATION);
	}

	/**
	 * OAuth Security Schema
	 *
	 * @return
	 */
	private OAuth securitySchema() {

		List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
		authorizationScopeList.add(new AuthorizationScope("global", "access all"));

		List<GrantType> grantTypes = new ArrayList();
		final TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("http://localhost:28180/oauth/token", clientId, clientSecret);
		final TokenEndpoint tokenEndpoint = new TokenEndpoint("http://localhost:28180/oauth/token", "access_token");
		AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
		grantTypes.add(authorizationCodeGrant);

		OAuth oAuth = new OAuth("oauth", authorizationScopeList, grantTypes);

		return oAuth;
	}

}
