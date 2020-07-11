package com.dfusiontech.server.model.dto.organization;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Organizations;
import lombok.*;

/**
 * Organization Reference Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-01-10
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class OrganizationRefDTO extends DTOBase<Organizations> {

	private Long id;
	private String name;
	private String description;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public OrganizationRefDTO(Organizations entity) {
		super(entity);
	}

	@Override
	public void fromEntity(Organizations entity) {
		// super.fromEntity(entity);
		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
	}
}
