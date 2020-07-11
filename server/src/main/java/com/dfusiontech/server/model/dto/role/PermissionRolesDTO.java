package com.dfusiontech.server.model.dto.role;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Permissions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission with assigned Roles Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-06-19
 */
@Setter
@Getter
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class PermissionRolesDTO extends DTOBase<Permissions> {

	private Long id;
	private String name;
	private String description;
	private List<RoleRefDTO> roles = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public PermissionRolesDTO() {
		super();
	}

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public PermissionRolesDTO(Permissions entity) {
		super(entity);
	}

}
