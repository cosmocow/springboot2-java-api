package com.dfusiontech.server.model.dto.common;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.domain.LanguageConstantScopeType;
import com.dfusiontech.server.model.jpa.entity.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Language View Entity Definition
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-27
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class LanguageConstantViewDTO extends DTOBase<LanguageConstants> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 2)
	private String name;

	@ApiModelProperty(position = 4)
	private LanguageConstantScopeType scope;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public LanguageConstantViewDTO(LanguageConstants entity) {
		super(entity);
	}

	/**
	 * Static constructor
	 *
	 * @param entity
	 * @return
	 */
	public static LanguageConstantViewDTO of(LanguageConstants entity) {
		LanguageConstantViewDTO result = null;
		if (entity != null) {
			result = new LanguageConstantViewDTO(entity);
		}
		return result;
	}
}
