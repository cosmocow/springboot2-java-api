package com.dfusiontech.server.model.jpa.domain;

/**
 * Idp Type Relation
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-05-13
 */
public enum IdpType {

	GOOGLE, MICROSOFT, FACEBOOK;

	/**
	 * Get proper Idp Type from String
	 *
	 * @param value
	 * @return IdpType
	 */
	public static IdpType of(String value) {
		IdpType result = null;

		IdpType tmpValue = IdpType.valueOf(value);
		if (tmpValue != null) {
			result = tmpValue;
		}

		return result;
	}
}
