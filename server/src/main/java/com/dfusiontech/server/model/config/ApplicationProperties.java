package com.dfusiontech.server.model.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

/**
 * Simple implementation of the Application properties helper. Used in API calls, etc.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-17
 */
@Getter
public class ApplicationProperties {

	@Value("${build.version}")
	private String buildVersion;

	@Value("${build.artifactId}")
	private String buildArtifact;

	@Value("${application.ui.url}")
	private String uiUrl;

	@Value("${application.admin-ui.url}")
	private String adminUiUrl;

	@Value("${application.api.url}")
	private String apiUrl;

	@Autowired
	private Environment environment;

	public boolean isEmailNotificationsEnabled() {
		boolean result = false;

		if ("true".equalsIgnoreCase(environment.getProperty("application.notifications.email.enabled"))) {
			result = true;
		}

		return result;
	}

	public String getEmailMessageFromAddress() {
		String result = "Do Not Reply <noreply@cyberinnovativetech.com>";

		if (environment.containsProperty("application.notifications.email.from")) {
			result = environment.getProperty("application.notifications.email.from");
		}

		return result;
	}

}
