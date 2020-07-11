package com.dfusiontech.server.model.dto.role;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Roles;
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
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class RoleRefDTO extends DTOBase<Roles> {

	private Long id;

	private String name;

	/**
	 * Default constructor
	 */
	public RoleRefDTO() {
		super();
	}

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public RoleRefDTO(Roles entity) {
		super(entity);
	}

	@Override
	public void fromEntity(Roles entity) {
		// super.fromEntity(entity);

		id = entity.getId();
		name = entity.getName();
	}

}
