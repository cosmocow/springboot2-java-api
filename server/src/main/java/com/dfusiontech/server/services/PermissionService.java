package com.dfusiontech.server.services;

import com.dfusiontech.server.auth.UserDetailsImpl;
import com.dfusiontech.server.model.dto.role.PermissionRefDTO;
import com.dfusiontech.server.model.jpa.domain.PermissionType;
import com.dfusiontech.server.model.jpa.entity.Permissions;
import com.dfusiontech.server.repository.jpa.PermissionRepository;
import com.dfusiontech.server.repository.jpa.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permissions Service
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2019-07-01
 */
@Service
public class PermissionService {

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	/**
	 * Get Permissions Set for current User
	 *
	 * @return
	 */
	public Set<PermissionRefDTO> getCurrentUserPermissions() {

		UserDetailsImpl currentUser = userService.getCurrentUser();

		return getUserPermissions(currentUser.getUserId());
	}

	/**
	 * Get Permissions Set for User
	 *
	 * @return
	 */
	public Set<PermissionRefDTO> getUserPermissions(Long userId) {

		Set<Permissions> permissionList = permissionRepository.getPermissionsByUser(userId);

		Set<PermissionRefDTO> result = permissionList.stream().map(PermissionRefDTO::new).collect(Collectors.toSet());

		return result;
	}

	/**
	 * Get Permission Names for current User
	 *
	 * @return
	 */
	public Set<String> getUserPermissionNames() {

		UserDetailsImpl currentUser = userService.getCurrentUser();

		return getUserPermissionNames(currentUser.getUserId());
	}

	/**
	 * Get Permission Names for User
	 *
	 * @return
	 */
	public Set<String> getUserPermissionNames(Long userId) {

		Set<Permissions> permissionList = permissionRepository.getPermissionsByUser(userId);

		Set<String> result = permissionList.stream().map(Permissions::getName).collect(Collectors.toSet());

		return result;
	}

	/**
	 * Check Permission for User
	 *
	 * @return
	 */
	public boolean checkCurrentUserPermission(PermissionType permissionType) {

		if (permissionType == null) return false;

		return checkCurrentUserPermission(permissionType.getPermission());
	}

	/**
	 * Check Permission for User
	 *
	 * @return
	 */
	public boolean checkCurrentUserPermission(String permissionName) {

		UserDetailsImpl currentUser = userService.getCurrentUser();

		boolean result = false;

		Long permissionsCount = permissionRepository.getCountPermissionsByUserAndName(currentUser.getUserId(), permissionName);
		if (permissionsCount > 0) {
			result = true;
		}

		return result;
	}

}
