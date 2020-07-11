package com.dfusiontech.server.model.jpa.domain;

/**
 * Basic Permission Types
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-07-01
 */
public enum PermissionType {

	USER_UPDATE("user_update")
	, ORGANIZATION_SUPPORTED_LANGUAGES_UPDATE("organization_supported_languages_update")
	;

	private final String _permission;

	private PermissionType(String permission) {
		this._permission = permission;
	}

	@Override
	public String toString() {
		return this.getPermission();
	}

	/**
	 * Get Permission Code Definition
	 *
	 * @return
	 */
	public String getPermission() {
		return _permission;
	}

}
