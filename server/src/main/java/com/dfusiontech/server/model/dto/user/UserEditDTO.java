package com.dfusiontech.server.model.dto.user;

import com.dfusiontech.server.model.dto.role.RoleRefDTO;
import com.dfusiontech.server.model.jpa.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
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
public class UserEditDTO extends UserListDTO {

	@ApiModelProperty(position = 3)
	private String passwordPlain;

	/**
	 * User role codes list
	 */
	@ApiModelProperty(dataType = "array", name = "List of User role objects", position = 5)
	private List<RoleRefDTO> roles;

	/**
	 * User role codes list
	 */
	@ApiModelProperty(dataType = "array", name = "List of User role codes", allowableValues = "USER, ADMIN", position = 5)
	private List<String> roleNames;

	/**
	 * User role permissions list
	 */
	@ApiModelProperty(dataType = "array", name = "Permission codes", position = 5)
	private Set<String> permissions;

	public UserEditDTO() {
		super();
	}

	public UserEditDTO(Users entity) {
		super(entity);
	}

	@Override
	public void fromEntity(Users users) {
		super.fromEntity(users);

		// Filling User Roles List
		roleNames = new ArrayList<>();
		Optional.ofNullable(users.getRoles()).orElse(new HashSet<>()).stream().forEach(role -> {
			roleNames.add(role.getName());
		});

		roles = Optional.ofNullable(users.getRoles()).orElse(new HashSet<>()).stream().map(RoleRefDTO::new).collect(Collectors.toList());

	}

}
