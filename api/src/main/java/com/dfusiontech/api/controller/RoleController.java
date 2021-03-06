package com.dfusiontech.api.controller;

import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.NameFilter;
import com.dfusiontech.server.model.dto.role.RoleListDTO;
import com.dfusiontech.server.model.jpa.entity.Roles;
import com.dfusiontech.server.repository.jpa.RoleRepository;
import com.dfusiontech.server.services.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple Roles controller. Used for Roles Listing
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@RestController
@RequestMapping(
	value = RoleController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Roles Management Controller"
)
@Api(tags = {"Role Viewer", "Administration"})
public class RoleController {

	static final String CONTROLLER_URI = "/api/roles";

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoleService roleService;

	/**
	 * Get Roles List
	 *
	 * @return Roles List
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(method = RequestMethod.GET, value = "", name = "Get Roles List")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public List<RoleListDTO> getList() {
		List<Roles> items = roleRepository.findAll().stream().filter(roles -> roles.getId() > 8).collect(Collectors.toList());

		List<RoleListDTO> roleListDTOs = RoleListDTO.fromEntitiesList(items, RoleListDTO.class);

		return roleListDTOs;
	}

	/**
	 * Get Roles List for current Risk Model
	 *
	 * @return Roles List
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/filter", name = "Roles List for current Filters and Risk Model")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public FilteredResponse<NameFilter, RoleListDTO> getListFiltered(
		@ApiParam(value = "Data Filtering Object", required = true) @RequestBody FilteredRequest<NameFilter> filteredRequest
	) {

		FilteredResponse<NameFilter, RoleListDTO> result = roleService.getListFiltered(filteredRequest);

		return result;
	}

}
