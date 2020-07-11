package com.dfusiontech.server.model.dto.audit;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.model.jpa.domain.AuditOperationType;
import com.dfusiontech.server.model.jpa.entity.AuditLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * Audit Log View Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-07-25
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class AuditLogViewDTO extends DTOBase<AuditLog> {

	@ApiModelProperty(position = 0)
	private Long id;

	// private Long organizationId;

	@ApiModelProperty(position = 2)
	private ItemTypeDTO itemTypeInfo;

	@ApiModelProperty(position = 3)
	private AuditOperationType operationTypeInfo;

	@ApiModelProperty(position = 4)
	private String oldValue;

	@ApiModelProperty(position = 6)
	private String newValue;

	@ApiModelProperty(position = 8)
	private Date logDate;

	@ApiModelProperty(position = 9)
	private Long auditItemId;

	@ApiModelProperty(position = 10)
	private Long auditUserId;

	@ApiModelProperty(position = 12)
	private String auditUserName;

	@ApiModelProperty(position = 13)
	private String auditUserEmail;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public AuditLogViewDTO(AuditLog entity) {
		super(entity);
	}

	/**
	 * Converts from entity to DTO
	 *
	 * @param entity
	 */
	@Override
	public void fromEntity(AuditLog entity) {
		super.fromEntity(entity);

		itemTypeInfo = new ItemTypeDTO(AuditItemType.of(entity.getItemType()));
		operationTypeInfo = AuditOperationType.of(entity.getOperationType());
	}

}
