package com.dfusiontech.server.model.dto.user;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * User Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-27
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "email"})
@EqualsAndHashCode(of = {"id", "email"}, callSuper = false)
public class UserRefDTO extends DTOBase<Users> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 1, readOnly = true)
	private String fullName;

	@ApiModelProperty(position = 1)
	private String firstName;

	@ApiModelProperty(position = 2)
	private String lastName;

	@ApiModelProperty(position = 3)
	private String email;

	/**
	 * Entity based constructor
	 *
	 * @param users
	 */
	public UserRefDTO(Users users) {
		super(users);
	}

	@Override
	public void fromEntity(Users entity) {
		id = entity.getId();
		fullName = entity.getFullName();
		firstName = entity.getFirstName();
		lastName = entity.getLastName();
		email = entity.getEmail();
	}
}
