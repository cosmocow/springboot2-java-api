package com.dfusiontech.server.model.jpa.domain;

/**
 * Language Constant Scope Type
 *
 * CREATE TYPE LanguageConstantScopeType as ENUM ('WEB_UI', 'WEB_ADMIN', 'SERVER', 'IOS', 'ANDROID');
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-06-25
 */
public enum LanguageConstantScopeType {

	WEB_UI, WEB_ADMIN, SERVER, IOS, ANDROID;

	/**
	 * Get proper Language Constant Type from String
	 *
	 * @param value
	 * @return LanguageConstantScopeType
	 */
	public static LanguageConstantScopeType of(String value) {
		LanguageConstantScopeType result = null;

		LanguageConstantScopeType tmpValue = LanguageConstantScopeType.valueOf(value);
		if (tmpValue != null) {
			result = tmpValue;
		}

		return result;
	}
}
