package com.dfusiontech.server.model.dto.user;

import com.dfusiontech.server.model.dto.DTOWithMetaData;
import com.dfusiontech.server.model.jpa.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * User Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "email"})
@EqualsAndHashCode(of = {"id", "email"}, callSuper = false)
public class UserListDTO extends DTOWithMetaData<Users> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 1, readOnly = true)
	private String fullName;

	@ApiModelProperty(position = 2)
	private String firstName;

	@ApiModelProperty(position = 3)
	private String lastName;

	@ApiModelProperty(position = 4)
	private String email;

	@ApiModelProperty(position = 5)
	private String title;

	@ApiModelProperty(position = 6)
	private String corporatePhone;

	@ApiModelProperty(position = 7)
	private String mobilePhone;

	@ApiModelProperty(position = 8)
	private Boolean enabled;

	@ApiModelProperty(position = 9)
	private Boolean expired;

	@ApiModelProperty(position = 10)
	private Boolean credentialsExpired;

	@ApiModelProperty(position = 11)
	private Boolean locked;

	@ApiModelProperty(position = 12)
	private Date expirationDate;

	@ApiModelProperty(position = 13)
	private Date credentialsExpirationDate;

	@ApiModelProperty(position = 14)
	private Boolean useMultiFactorAuth;

	/**
	 * Entity based constructor
	 *
	 * @param users
	 */
	public UserListDTO(Users users) {
		super(users);
	}

	@Override
	public void fromEntity(Users entity) {
//		super.fromEntity(entity);

		id = entity.getId();
		fullName = entity.getFullName();
		firstName = entity.getFirstName();
		lastName = entity.getLastName();
		email = entity.getEmail();
		title = entity.getTitle();
		corporatePhone = entity.getCorporatePhone();
		mobilePhone = entity.getMobilePhone();
		enabled = entity.getEnabled();
		expired = entity.getExpired();
		credentialsExpired = entity.getCredentialsExpired();
		locked = entity.getLocked();
		expirationDate = entity.getExpirationDate();
		credentialsExpirationDate = entity.getCredentialsExpirationDate();
		useMultiFactorAuth = entity.getUseMultiFactorAuth();
	}
}
