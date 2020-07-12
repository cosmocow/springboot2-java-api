package com.dfusiontech.server.model.data;

import com.dfusiontech.server.model.jpa.domain.LanguageConstantScopeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Implementation of Language Constant Filtering Object
 *
 * @author   Daniel A. Kolesnik <dkolesnik@dfusiontech.com>
 * @version  0.1.0
 * @since    2020-04-21
 */
@NoArgsConstructor
@Setter
@Getter
@ToString(of = {"name"})
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class LanguageConstantFilter extends NameFilter {

	@ApiModelProperty(position = 4)
    String value;

	@JsonIgnore
    String languageCode;

	@JsonIgnore
	LanguageConstantScopeType scope;

}
