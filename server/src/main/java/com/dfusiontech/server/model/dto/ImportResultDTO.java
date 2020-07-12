package com.dfusiontech.server.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of file import
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-04-12
 */
@Setter
@Getter
public class ImportResultDTO implements Serializable {

	private static final long serialVersionUID = 6223505590943578051L;

	@ApiModelProperty(position = 2)
	private List<String> messages;

	@ApiModelProperty(position = 4)
	private String status;

	private List<ItemViewDTO> created;

	private List<ItemViewDTO> updated;

	private List<ItemViewDTO> ignored;

	public ImportResultDTO() {
		messages = new ArrayList<>();
		created = new ArrayList<>();
		updated = new ArrayList<>();
		ignored = new ArrayList<>();
	}

}
