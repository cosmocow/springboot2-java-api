package com.dfusiontech.api.controller;

import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.UsersFilter;
import com.dfusiontech.server.model.dto.user.UserEditDTO;
import com.dfusiontech.server.model.dto.user.UserListDTO;
import com.dfusiontech.server.model.dto.user.UserRefDTO;
import com.dfusiontech.server.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * User management controller. Basic user CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@RestController
@RequestMapping(
	value = UserController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Users Management Controller"
)
@Api(tags = {"User Management", "Administration"})
public class UserController {

	static final String CONTROLLER_URI = "/api/users";

	@Autowired
	private UserService userService;


	/**
	 * Get Users List
	 *
	 * @return Users List
	 */
	@RequestMapping(method = RequestMethod.GET, value = "", name = "Get Users List")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_READ)")
	public List<UserListDTO> getList() {

		List<UserListDTO> result = userService.getList();

		return result;
	}

	/**
	 * Return Filtered list of Users
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/filter", name = "Return Filtered list of Users", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_READ) or " +
		"@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_SETTING_READ)")
	public FilteredResponse<UsersFilter, UserListDTO> filter(@ApiParam(value = "User Filtering", required = true) @RequestBody FilteredRequest<UsersFilter> filteredRequest) {

		FilteredResponse<UsersFilter, UserListDTO> result = userService.getListFiltered(filteredRequest);

		return result;
	}

	/**
	 * Get User details
	 *
	 * @return User Details
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/self", name = "Get User details")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public UserEditDTO getSelf() {

		UserEditDTO itemDTO = userService.getSelf();

		userService.updateLastLoginDate(itemDTO.getId());

		return itemDTO;
	}

	/**
	 * Get User details
	 *
	 * @return User Details
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{itemId}", name = "Get User details")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_UPDATE)")
	public UserEditDTO getDetails(@PathVariable("itemId") @NotNull @Size(min = 1) Long itemId) {

		UserEditDTO itemDTO = userService.getDetails(itemId);

		return itemDTO;
	}

	/**
	 * Create new User
	 *
	 * @return New User
	 */
	@RequestMapping(method = RequestMethod.POST, value = "", name = "Create new User", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_CREATE)")
	public UserEditDTO create(@ApiParam(value = "User Details", required = true) @RequestBody UserEditDTO newItemDTO) {

		UserEditDTO result = userService.create(newItemDTO);

		return result;
	}

	/**
	 * Update User
	 *
	 * @return New User
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "", name = "Update existing User", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_UPDATE) or " +
		"@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_SETTING_UPDATE)")
	public UserEditDTO update(@ApiParam(value = "User update Details", required = true) @RequestBody UserEditDTO itemDTO) {

		UserEditDTO result = userService.update(itemDTO);

		return result;
	}

	/**
	 * Deletes User
	 *
	 * @return ID of removed User
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "", name = "Delete existing User", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	@PreAuthorize("@apiSecurity.hasPermission(T(com.dfusiontech.api.config.APIAction).USER_DELETE)")
	public Long delete(@ApiParam(value = "Simple User Details", required = true) @RequestBody UserRefDTO itemDTO) {

		Long result = userService.delete(itemDTO);

		return result;
	}

}
