package com.dfusiontech.server.model.dto.role;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Roles;
import com.dfusiontech.server.model.jpa.entity.Users;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Role List Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@Setter
@Getter
public class RoleListDTO extends RoleRefDTO {

	private String description;

	/**
	 * Default constructor
	 */
	public RoleListDTO() {
		super();
	}

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public RoleListDTO(Roles entity) {
		super(entity);
	}


	@Override
	public void fromEntity(Roles entity) {
		super.fromEntity(entity);

		description = entity.getDescription();
	}

}
