package com.dfusiontech.server.services;

import com.dfusiontech.server.rest.exception.ForbiddenException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Simple security service based on permission names
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-10-30
 */
@Service("apiSecurity")
public class CustomSecurityService {

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	public boolean hasPermission(String action) {
		boolean isUserAllowed = permissionService.checkCurrentUserPermission(action);

		if (!isUserAllowed) {
			// Allow ANY Permissions for SUPER ADMIN
			if (userService.isSuperAdmin()) {
				return true;
			}

			String username = userService.getCurrentUser() != null ? userService.getCurrentUser().getUsername() : "ANONYMOUS";
			throw new ForbiddenException("You are not allowed to access this resource: " + StringUtils.capitalize(action).replaceAll("_", " "), username);
		}

		return isUserAllowed;
	}

	public boolean hasPermission(Authentication authentication, String action) {
		return true;
	}
}
