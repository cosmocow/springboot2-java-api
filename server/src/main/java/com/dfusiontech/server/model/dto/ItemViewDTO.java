package com.dfusiontech.server.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Simple Item View Definition. Contains id/name
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-22
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
public class ItemViewDTO<ENTITY> extends DTOBase<ENTITY> {

	@ApiModelProperty(position = 1)
	private Long id;

	@ApiModelProperty(position = 2)
	private String name;

	/**
	 * Entity DTO constructor
	 */
	public ItemViewDTO(ENTITY entity) {
		super(entity);
	}

	/**
	 * Full arguments constructor
	 */
	public ItemViewDTO(Long id, String name) {
		super();

		this.id = id;
		this.name = name;
	}

}
