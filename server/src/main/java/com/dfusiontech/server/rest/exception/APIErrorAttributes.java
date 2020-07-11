package com.dfusiontech.server.rest.exception;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Overriding API Exception Attributes.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-22
 */
public class APIErrorAttributes extends DefaultErrorAttributes {

	/**
	 * Override Exception attributes
	 *
	 * @param webRequest
	 * @param includeStackTrace
	 * @return
	 */
	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		Map<String, Object> result = super.getErrorAttributes(webRequest, includeStackTrace);

		Throwable exception = this.getError(webRequest);
		if (exception instanceof ServerException) {
			Map<String, Object> errorDetails = new HashMap<>();
			errorDetails.put("id", ((ServerException) exception).getCode());
			errorDetails.put("message", ((ServerException) exception).getMessage());
			result.put("exception", errorDetails);
		}

		return result;
	}
}
