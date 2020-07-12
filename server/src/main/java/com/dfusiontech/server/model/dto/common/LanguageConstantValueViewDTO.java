package com.dfusiontech.server.model.dto.common;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.organization.SupportedLanguageViewDTO;
import com.dfusiontech.server.model.jpa.entity.LanguageConstantValues;
import com.dfusiontech.server.model.jpa.entity.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Language Constant Value View Entity Definition
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-04-17
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "value"})
@EqualsAndHashCode(of = {"id", "value"}, callSuper = false)
public class LanguageConstantValueViewDTO extends DTOBase<LanguageConstantValues> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 2)
	private String value;

	@ApiModelProperty(position = 4)
	private SupportedLanguageViewDTO language;

	@ApiModelProperty(position = 6)
	private LanguageConstantViewDTO languageConstant;

	@ApiModelProperty(position = 8)
	private String defaultValue;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public LanguageConstantValueViewDTO(LanguageConstantValues entity) {
		super(entity);
		defaultValue = "";
	}

	/**
	 * LanguageConstant based constructor
	 * @param entity
	 */
	public LanguageConstantValueViewDTO(LanguageConstants languageConstant, LanguageConstantValues entity, String defaultValue) {
		if (entity == null) {
			this.id = null;
			this.value = "";
			this.languageConstant = LanguageConstantViewDTO.of(languageConstant);
			this.language = null;
			this.defaultValue = defaultValue;
		} else {
			this.id = entity.getId();
			this.value = entity.getValue();
			this.language = new SupportedLanguageViewDTO(entity.getLanguage());
			this.languageConstant = LanguageConstantViewDTO.of(languageConstant);
			this.defaultValue = defaultValue;
		}
	}

	@Override
	public void fromEntity(LanguageConstantValues entity) {
		id = entity.getId();
		value = entity.getValue();
		language = new SupportedLanguageViewDTO(entity.getLanguage());
		languageConstant = LanguageConstantViewDTO.of(entity.getLanguageConstant());
		defaultValue = "";
	}
}
