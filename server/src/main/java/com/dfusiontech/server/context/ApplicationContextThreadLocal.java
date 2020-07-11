package com.dfusiontech.server.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

/**
 * Holds Application context
 */
public abstract class ApplicationContextThreadLocal {

	private static final ThreadLocal<ApplicationContext> contextHolder = new ThreadLocal<ApplicationContext>();

	/**
	 * Set Application Context
	 *
	 * @param context
	 */
	public static void setContext(ApplicationContext context) {
		contextHolder.set(context);
	}

	/**
	 * Clear context
	 */
	public static void unsetContext() {
		contextHolder.remove();
	}

	/**
	 * Get Application Context
	 *
	 * @return
	 */
	public static ApplicationContext getContext() {
		return contextHolder.get();
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	public static class ApplicationContext {

		private Long organizationId;

		private String localeString;

		private Locale locale;

	}
}
