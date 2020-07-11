package com.dfusiontech.server.model.dto.role;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Permissions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Permission Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-06-19
 */
@Setter
@Getter
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class PermissionRefDTO extends DTOBase<Permissions> {

	private Long id;
	private String name;

	/**
	 * Default constructor
	 */
	public PermissionRefDTO() {
		super();
	}

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public PermissionRefDTO(Permissions entity) {
		super(entity);
	}

	@Override
	public void fromEntity(Permissions entity) {
		// super.fromEntity(entity);

		id = entity.getId();
		name = entity.getName();
	}

}
