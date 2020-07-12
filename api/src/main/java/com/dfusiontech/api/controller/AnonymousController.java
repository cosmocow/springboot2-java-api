package com.dfusiontech.api.controller;

import com.dfusiontech.server.model.dto.user.ForgetPasswordDTO;
import com.dfusiontech.server.model.dto.user.ResetPasswordDTO;
import com.dfusiontech.server.rest.exception.ApplicationExceptionCodes;
import com.dfusiontech.server.services.UserPasswordResetLinksService;
import com.dfusiontech.server.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Anonymous Users Controller
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-25
 */
@RestController
@RequestMapping(
	value = AnonymousController.CONTROLLER_URI,
	produces = MediaType.APPLICATION_JSON,
	name = "Anonymous Users Controller"
)
@Api(tags = {"Anonymous Users"})
public class AnonymousController {

	static final String CONTROLLER_URI = "/api/anonymous";

	@Autowired
	private UserService userService;

	@Autowired
	private UserPasswordResetLinksService userPasswordResetLinksService;

	/**
	 * Send "Forget password" request
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/reset-password/send-reset-email", name = "Send forget password email", consumes = {MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
		@ApiResponse(code = 404, message = "[ApplicationExceptionCodes.USER_WITH_EMAIL_NOT_EXISTS]: User with this email is not found [{0}]"),
		@ApiResponse(code = 500, message = "[ApplicationExceptionCodes.RESET_PASSWORD_LINK_EMAIL_FAILED]: Failed to send reset password email to [{0}]")
	})
	public boolean sendResetPasswordEmail(@ApiParam(value = "User Details to reset password", required = true) @RequestBody ForgetPasswordDTO forgetPasswordDTO) {
		boolean result = false;

		userService.sendResetPasswordEmail(forgetPasswordDTO);

		result = true;

		return result;
	}

	/**
	 * Verify reset password code
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/reset-password/verify-code/{resetCode}", name = "Verify user code for reset password")
	@ApiResponses(value = {
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_NOT_EXISTS, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_NOT_EXISTS], Reset password link not found [{0}]"),
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_EXPIRED, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_EXPIRED], Reset password link expired [{0}]"),
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_ALREADY_APPLIED, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_ALREADY_APPLIED], Reset password link already applied [{0}]")
	})
	public boolean verifyResetPasswordEmail(@ApiParam(value = "Reset user password code", example = "1ecb3d9c-1ebf-4152-8ae9-97d463beee6d", required = true) @PathVariable(name = "resetCode") String resetCode) {
		boolean result = true;

		userPasswordResetLinksService.verifyLinkByCode(resetCode);

		return result;
	}

	/**
	 * Apply new password by reset code
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/reset-password/apply", name = "Apply new password based on reset password code", consumes = {MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_NOT_EXISTS, message = "[HTTP-404], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_NOT_EXISTS], Reset password link not found [{0}]"),
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_EXPIRED, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_EXPIRED], Reset password link expired [{0}]"),
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_LINK_ALREADY_APPLIED, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_LINK_ALREADY_APPLIED], Reset password link already applied [{0}]"),
		@ApiResponse(code = ApplicationExceptionCodes.RESET_PASSWORD_TOO_WEAK, message = "[HTTP-400], [ApplicationExceptionCodes.RESET_PASSWORD_TOO_WEAK], Reset password is too weak")
	})
	public boolean changePassword(@ApiParam(value = "User Details to reset password", required = true) @RequestBody ResetPasswordDTO resetPasswordDTO) {
		boolean result = true;

		userPasswordResetLinksService.applyPassword(resetPasswordDTO.getCode(), resetPasswordDTO.getPassword());

		return result;
	}

}
