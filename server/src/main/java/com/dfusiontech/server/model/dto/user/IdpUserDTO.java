package com.dfusiontech.server.model.dto.user;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.domain.IdpType;
import com.dfusiontech.server.model.jpa.entity.IdpUsers;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Idp User Entity Definition
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-05-14
 */
@Setter
@Getter
@ToString(of = {"id", "email"})
@EqualsAndHashCode(of = {"id", "email"}, callSuper = false)
@NoArgsConstructor
public class IdpUserDTO extends DTOBase<IdpUsers> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 2)
	private IdpType idpId;

	@ApiModelProperty(position = 4)
	private String userIdentity;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public IdpUserDTO(IdpUsers entity) {
		super(entity);
	}

	@Override
	public void fromEntity(IdpUsers entity) {
		id = entity.getId();
		idpId = entity.getIdpId();
		userIdentity = entity.getUserIdentity();
	}

}
