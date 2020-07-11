package com.dfusiontech.server.model.dto.audit.items;

import com.dfusiontech.server.model.dto.organization.OrganizationRefDTO;
import com.dfusiontech.server.model.dto.user.UserListDTO;
import com.dfusiontech.server.model.jpa.entity.Roles;
import com.dfusiontech.server.model.jpa.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@Setter
@Getter
public class UserAuditDTO extends UserListDTO {

	private OrganizationRefDTO organization;

	private List<String> roleNames;

	/**
	 * Default constructor
	 */
	public UserAuditDTO() {
		super();
	}

	/**
	 * Entity based constructor
	 *
	 * @param users
	 */
	public UserAuditDTO(Users users) {
		super(users);
	}

	/**
	 * Converts from entity to DTO
	 *
	 * @param entity
	 */
	@Override
	public void fromEntity(Users entity) {
		super.fromEntity(entity);

		roleNames = Optional.ofNullable(entity.getRoles()).orElse(new HashSet<>()).stream().map(Roles::getName).collect(Collectors.toList());
		if (entity.getOrganization() != null) setOrganization(new OrganizationRefDTO(entity.getOrganization()));
	}

}
