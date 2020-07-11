package com.dfusiontech.server.model.dto.role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Permission and Role Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-06-19
 */
@Setter
@Getter
@ToString(of = {"permission", "role"})
@EqualsAndHashCode(of = {"permission", "role"}, callSuper = false)
public class RolePermissionUpdateDTO {

	private PermissionRefDTO permission;
	private RoleRefDTO role;
	private boolean isAllowed;

	/**
	 * Default constructor
	 */
	public RolePermissionUpdateDTO() {
	}

}
