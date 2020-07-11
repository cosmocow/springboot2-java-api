package com.dfusiontech.server.model.dto.organization;

import com.dfusiontech.server.model.jpa.entity.SupportedLanguages;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Supported Language Edit Entity Definition
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-03-16
 */
@Setter
@Getter
@NoArgsConstructor
public class SupportedLanguageEditDTO extends SupportedLanguageViewDTO {

	/**
	 * Entity based constructor
	 *
	 * @param entity
	 */
	public SupportedLanguageEditDTO(SupportedLanguages entity) {
		super(entity);
	}

	@Override
	public void fromEntity(SupportedLanguages entity) {
		super.fromEntity(entity);
	}
}
