package com.dfusiontech.api.config;

import com.dfusiontech.server.rest.exception.APIErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Application Configuration. Override some BEANs.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-20
 */
@Configuration
public class WebApplicationConfig {

	/**
	 * Overriding Error Attributes
	 *
	 * @return
	 */
	@Bean
	public ErrorAttributes errorAttributes() {
		APIErrorAttributes result = new APIErrorAttributes();

		return result;
	}

}
