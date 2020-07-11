package com.dfusiontech.server.model.data;

import com.dfusiontech.server.model.dto.audit.ItemTypeDTO;
import com.dfusiontech.server.model.dto.user.UserRefDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * Implementation of Audit Logs Filtering Logic
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-04-01
 */
@NoArgsConstructor
@Setter
@Getter
@ToString(of = {"itemTypes", "users"})
@EqualsAndHashCode()
public class AuditLogFilter extends NameFilter {

	@ApiModelProperty(position = 2)
	private List<ItemTypeDTO> itemTypes;

	@ApiModelProperty(position = 4)
	private Long itemId;

	@ApiModelProperty(position = 5)
	private Long organizationId;

	@ApiModelProperty(position = 6)
	private List<UserRefDTO> users;

	@ApiModelProperty(position = 7)
	private Date dateFrom;

	@ApiModelProperty(position = 7)
	private Date dateTo;

}
