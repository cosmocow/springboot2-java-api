package com.dfusiontech.server.model.jpa.domain;

/**
 * Basic Role Types
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-08
 */
public enum RoleType {

	/**
	 * Default system user
	 */
	USER("USER"),

	/**
	 * System Administrator
	 */
	ADMIN("ADMIN"),

	;

	private final String _role;

	private RoleType(String role) {
		this._role = role;
	}

	@Override
	public String toString() {
		return this.role();
	}

	/**
	 * Get Role Code Definition
	 *
	 * @return
	 */
	public String role() {
		return _role;
	}

}
