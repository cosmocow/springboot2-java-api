package com.dfusiontech.server.model.dto.organization;

import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.jpa.domain.LanguageDirection;
import com.dfusiontech.server.model.jpa.entity.SupportedLanguages;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Supported Language View Entity Definition
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-03-16
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class SupportedLanguageViewDTO extends DTOBase<SupportedLanguages> {

	@ApiModelProperty(position = 0)
	private Long id;

	@ApiModelProperty(position = 2)
	private String name;

	@ApiModelProperty(position = 4)
	private String code;

	@ApiModelProperty(position = 6)
	private String charset;

	@ApiModelProperty(position = 8)
	private String locale;

	@ApiModelProperty(position = 9)
	private LanguageDirection direction;

	@ApiModelProperty(position = 10)
	private Boolean isPublic;

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public SupportedLanguageViewDTO(SupportedLanguages entity) {
		super(entity);
	}

	@Override
	public void fromEntity(SupportedLanguages entity) {
		id = entity.getId();
		name = entity.getName();
		code = entity.getCode();
		charset = entity.getCharset();
		locale = entity.getLocale();
		direction = entity.getDirection();
		isPublic = entity.getIsPublic();
	}
}
