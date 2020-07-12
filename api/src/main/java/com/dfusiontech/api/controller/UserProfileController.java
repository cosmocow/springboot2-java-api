package com.dfusiontech.api.controller;

import com.dfusiontech.server.auth.UserDetailsImpl;
import com.dfusiontech.server.model.dto.user.ChangePasswordDTO;
import com.dfusiontech.server.model.dto.user.UserEditDTO;
import com.dfusiontech.server.model.jpa.entity.Users;
import com.dfusiontech.server.rest.exception.ApplicationExceptionCodes;
import com.dfusiontech.server.rest.exception.InternalServerErrorException;
import com.dfusiontech.server.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 * User profile controller
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-01-18
 */
@RestController
@RequestMapping(
	value = UserProfileController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Users Profile Controller"
)
@Api(tags = {"User Profile"})
public class UserProfileController {

	static final String CONTROLLER_URI = "/api/user-profile";

	@Autowired
	private UserService userService;

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

		UserDetailsImpl user = userService.getCurrentUser();
		UserEditDTO itemDTO = userService.getDetails(user.getUserId());

		return itemDTO;
	}

	/**
	 * Change current User password
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/change-password", name = "Change current User password", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public Boolean changePassword(@ApiParam(value = "User Details", required = true) @RequestBody ChangePasswordDTO itemDTO) {

		Users currentUser = userService.getCurrentUserEntity();

		// Compare password
		if (!userService.comparePasswords(itemDTO.getOldPassword(), currentUser.getPassword())) {
			throw new InternalServerErrorException("Old password doesn't match stored password", ApplicationExceptionCodes.PASSWORD_DOESNT_MATCH);
		}

		userService.changePassword(currentUser, itemDTO.getPassword());

		return true;
	}

	/**
	 * Update current User details
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/update", name = "Update current User details", consumes = {MediaType.APPLICATION_JSON})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "oAuth Access token for API calls", defaultValue = "Bearer DF0310", required = true, dataType = "string", paramType = "header")
	})
	public UserEditDTO updateProfile(@ApiParam(value = "User Details", required = true) @RequestBody UserEditDTO itemDTO) {
		return userService.updateProfile(itemDTO);
	}

}
