package com.dfusiontech.server.model.dto.audit;

import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import lombok.*;

/**
 * Audit log Item Type View Entity Definition
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
public class ItemTypeDTO {

	private Long id;

	private AuditItemType itemType;

	private String name;

	/**
	 * Entity based constructor
	 */
	public ItemTypeDTO(AuditItemType itemType) {
		if (itemType != null) {
			this.id = itemType.getId();
			this.name = itemType.getName();
			this.itemType = itemType;
		}
	}

}
