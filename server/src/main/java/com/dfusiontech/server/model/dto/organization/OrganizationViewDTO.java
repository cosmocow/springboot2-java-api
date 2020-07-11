package com.dfusiontech.server.model.dto.organization;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.user.UserRefDTO;
import com.dfusiontech.server.model.jpa.entity.Organizations;
import lombok.*;

/**
 * Organization View Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-08
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class OrganizationViewDTO extends DTOBase<Organizations> {

	private Long id;
	private String name;
	private String description;
	private Double averageRevenue;
	private Double qualThreshold;
	private OrganizationRefDTO parent;
	private OrganizationRefDTO rootParent;
	private UserRefDTO owner;
	private Boolean isCloudVendor;
	private Boolean isServiceVendor;
	private Boolean isTechnologyVendor;
	private Boolean isSystemVendor;
	private Boolean useMultiFactorAuth;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public OrganizationViewDTO(Organizations entity) {
		super(entity);
	}

	/**
	 * Converts from entity to DTO
	 *
	 * @param entity
	 */
	@Override
	public void fromEntity(Organizations entity) {
//		super.fromEntity(entity);

		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
		isServiceVendor = entity.getIsServiceVendor();
		isTechnologyVendor = entity.getIsTechnologyVendor();
		isSystemVendor = entity.getIsSystemVendor();
		useMultiFactorAuth = entity.getUseMultiFactorAuth();

		if (entity.getParent() != null) setParent(new OrganizationRefDTO(entity.getParent()));
		if (entity.getRootParent() != null) setRootParent(new OrganizationRefDTO(entity.getRootParent()));
		if (entity.getOwner() != null) setOwner(new UserRefDTO(entity.getOwner()));
	}
}
