package com.dfusiontech.server.services;

import com.dfusiontech.server.auth.UserDetailsImpl;
import com.dfusiontech.server.model.config.ApplicationProperties;
import com.dfusiontech.server.model.dao.PagedResult;
import com.dfusiontech.server.model.dao.UserModelDAO;
import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.UsersFilter;
import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.audit.items.UserAuditDTO;
import com.dfusiontech.server.model.dto.user.ForgetPasswordDTO;
import com.dfusiontech.server.model.dto.user.UserEditDTO;
import com.dfusiontech.server.model.dto.user.UserListDTO;
import com.dfusiontech.server.model.dto.user.UserRefDTO;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.model.jpa.domain.AuditOperationType;
import com.dfusiontech.server.model.jpa.domain.RoleType;
import com.dfusiontech.server.model.jpa.entity.AuditLogItemId;
import com.dfusiontech.server.model.jpa.entity.Users;
import com.dfusiontech.server.repository.jpa.CurrencyRepository;
import com.dfusiontech.server.repository.jpa.RoleRepository;
import com.dfusiontech.server.repository.jpa.UserRepository;
import com.dfusiontech.server.rest.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User management Service. Implements basic user CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-10-27
 */
@Service
public class UserService {

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserModelDAO userModelDAO;

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * Get Users List
	 *
	 * @return Users List
	 */
	public List<UserListDTO> getList() {
		List<Users> items;

		if (getCurrentUser().getOrganizationId() != null) {
			items = userRepository.getListByOrganization(getCurrentUser().getOrganizationId());
		} else {
			items = userRepository.findAll();
		}

		List<UserListDTO> usersDTOList = DTOBase.fromEntitiesList(items, UserListDTO.class);

		return usersDTOList;
	}

	/**
	 * Get Users List Filtered
	 *
	 * @return Users List
	 */
	public FilteredResponse<UsersFilter, UserListDTO> getListFiltered(FilteredRequest<UsersFilter> filteredRequest) {

		UsersFilter filter = filteredRequest.getFilter();
		// filter.setOrganizationId(organizationService.getCurrentOrganizationId());
		// to prevent showing deleted users (Admin-only feature)
		filter.setIsDeleted(false);

		PagedResult<Users> pagedResult = userModelDAO.getItemsPageable(filter, filteredRequest.toPageRequest(), filteredRequest.getSort());

		// Convert to DTOs
		List<UserListDTO> usersDTOList = DTOBase.fromEntitiesList(pagedResult.getItems(), UserListDTO.class);

		FilteredResponse<UsersFilter, UserListDTO> filteredResponse = new FilteredResponse<UsersFilter, UserListDTO>(filteredRequest);
		filteredResponse.setItems(usersDTOList);
		filteredResponse.setTotal(pagedResult.getCount().intValue());

		return filteredResponse;
	}

	/**
	 * Get User details
	 *
	 * @return User Details
	 */
	public UserEditDTO getDetails(Long itemId) {

		Users itemDetails = getOrganizationUser(itemId);
		UserEditDTO itemDTO = new UserEditDTO(itemDetails);

		return itemDTO;
	}

	/**
	 * Get self User details
	 *
	 * @return User Details
	 */
	public UserEditDTO getSelf() {

		UserDetailsImpl user = getCurrentUser();
		Users itemDetails = getOrganizationUser(user.getUserId());
		UserEditDTO itemDTO = new UserEditDTO(itemDetails);

		// Set User Permissions
		Set<String> permissions = permissionService.getUserPermissionNames(user.getUserId());
		itemDTO.setPermissions(permissions);

		return itemDTO;
	}

	/**
	 * Get User details
	 *
	 * @return User Details
	 */
	public Users getUser(Long itemId) {
		Users itemDetails;

		try {
			itemDetails = userRepository.findById(itemId).get();
		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("User not found in the database [{0}]", itemId), ApplicationExceptionCodes.USER_NOT_EXISTS);
		}

		return itemDetails;
	}

	/**
	 * Get details of User from current organization
	 *
	 * @return User Details
	 */
	public Users getOrganizationUser(Long itemId) {

		Users itemDetails = getUser(itemId);

		if (!organizationService.getCurrentOrganizationId().equals(itemDetails.getOrganization().getId())) {
			throw new BadRequestException(MessageFormat.format("User is not belongs to the current Organization.", organizationService.getCurrentOrganizationId()), ApplicationExceptionCodes.USER_NOT_BELONGS_TO_CURRENT_ORGANIZATION);
		}

		return itemDetails;
	}

	/**
	 * Create new User
	 *
	 * @return New User
	 */
	public UserEditDTO create(UserEditDTO newItemDTO) {

		// Verify user with such email exists
		if (userRepository.findFirstByEmailAndIdIsNotIn(newItemDTO.getEmail(), Arrays.asList(0l)).isPresent()) {
			throw new ConflictException(MessageFormat.format("User with this email already registered in the system [{0}]", newItemDTO.getEmail()), ApplicationExceptionCodes.USER_WITH_EMAIL_ALREADY_EXISTS);
		}

		Users newItem = new Users();

		newItem.setCreatedBy(getCurrentUserEntity());
		newItem.setCreatedAt(new Date());

		// Set Organization for new User
		if (organizationService.getCurrentOrganizationId() != null) {
			newItem.setOrganization(organizationService.getOrganization(organizationService.getCurrentOrganizationId()));
		}

		newItem.setMobilePhone(newItemDTO.getMobilePhone());

		applyEntityChanges(newItemDTO, newItem);

		Users saveResult = userRepository.save(newItem);

		// Send User Registration Email
		if (applicationProperties.isEmailNotificationsEnabled()) {
			emailService.sendUserRegistrationEmail(saveResult);
		}

		UserEditDTO result = new UserEditDTO(saveResult);

		// Save Audit Log CREATE event
		auditLogService.create(
			AuditItemType.USER,
			saveResult.getId(),
			new UserAuditDTO(saveResult),
			AuditLogItemId.of(AuditItemType.ORGANIZATION, saveResult.getOrganization().getId())
		);

		return result;
	}

	/**
	 * Update User
	 *
	 * @return New User
	 */
	public UserEditDTO update(UserEditDTO itemDTO) {

		UserEditDTO result;

		try {

			// Verify user with such email exists
			if (userRepository.findFirstByEmailAndIdIsNotIn(itemDTO.getEmail(), Arrays.asList(itemDTO.getId())).isPresent()) {
				throw new ConflictException(MessageFormat.format("User with this email already registered in the system [{0}]", itemDTO.getEmail()), ApplicationExceptionCodes.USER_WITH_EMAIL_ALREADY_EXISTS);
			}

			// Get Existing item from the database
			Users existingItem = getOrganizationUser(itemDTO.getId());
			UserAuditDTO oldAuditValue = new UserAuditDTO(existingItem);

			// Update item details
			Users updatedItem = existingItem;

			if(StringUtils.isNotEmpty(itemDTO.getMobilePhone()) && StringUtils.isEmpty(updatedItem.getMobilePhone())) {
				updatedItem.setMobilePhone(itemDTO.getMobilePhone());
			}

			applyEntityChanges(itemDTO, updatedItem);

			// Save to the database
			Users saveResult = userRepository.save(updatedItem);

			result = new UserEditDTO(saveResult);

			// Save Audit Log UPDATE event
			auditLogService.update(
				AuditItemType.USER,
				itemDTO.getId(),
				oldAuditValue,
				new UserAuditDTO(saveResult),
				AuditLogItemId.of(AuditItemType.ORGANIZATION, saveResult.getOrganization().getId())
			);

		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("User not found in the system [{0}]", itemDTO.getId()), ApplicationExceptionCodes.USER_NOT_EXISTS);
		}

		return result;
	}

	/**
	 * Update User Profile
	 *
	 * @return Updated User
	 */
	public UserEditDTO updateProfile(UserEditDTO itemDTO) {

		UserEditDTO result;

		// Verify user with such email exists
		if (userRepository.findFirstByEmailAndIdIsNotIn(itemDTO.getEmail(), Arrays.asList(itemDTO.getId())).isPresent()) {
			throw new ConflictException(MessageFormat.format("User with this email already registered in the system [{0}]", itemDTO.getEmail()), ApplicationExceptionCodes.USER_WITH_EMAIL_ALREADY_EXISTS);
		}

		// Get Existing item from the database
		Users currentUser = getCurrentUserEntity();
		UserAuditDTO oldAuditValue = new UserAuditDTO(currentUser);

		// Update item details
		Users updatedItem = currentUser;
		updatedItem.setFirstName(itemDTO.getFirstName());
		updatedItem.setLastName(itemDTO.getLastName());
		if (itemDTO.getCorporatePhone() != null) updatedItem.setCorporatePhone(itemDTO.getCorporatePhone());
		if (itemDTO.getMobilePhone() != null && StringUtils.isEmpty(updatedItem.getMobilePhone())) updatedItem.setMobilePhone(itemDTO.getMobilePhone());

		updatedItem.setUpdatedBy(getCurrentUserEntity());
		updatedItem.setUpdatedAt(new Date());

		// Save to the database
		Users saveResult = userRepository.save(updatedItem);

		result = new UserEditDTO(saveResult);

		// Save Audit Log UPDATE event
		auditLogService.update(
			AuditItemType.USER,
			itemDTO.getId(),
			oldAuditValue,
			new UserAuditDTO(saveResult),
			AuditLogItemId.of(AuditItemType.ORGANIZATION, saveResult.getOrganization().getId())
		);

		return result;
	}


	/**
	 * Apply entity changes and linkages
	 *
	 * @param itemDTO
	 * @param entity
	 */
	protected void applyEntityChanges(UserEditDTO itemDTO, Users entity) {

		entity.setFirstName(itemDTO.getFirstName());
		entity.setLastName(itemDTO.getLastName());
		entity.setEmail(itemDTO.getEmail());
		entity.setCorporatePhone(itemDTO.getCorporatePhone());
		entity.setExpired(itemDTO.getExpired());
		entity.setEnabled(itemDTO.getEnabled());
		entity.setTitle(itemDTO.getTitle());
		if (itemDTO.getCredentialsExpired() != null) entity.setCredentialsExpired(itemDTO.getCredentialsExpired());
		entity.setCredentialsExpirationDate(itemDTO.getCredentialsExpirationDate());
		entity.setLocked(itemDTO.getLocked());
		entity.setExpirationDate(itemDTO.getExpirationDate());
		entity.setUseMultiFactorAuth(itemDTO.getUseMultiFactorAuth());

		// Set Encoded Password if it is defined
		if (StringUtils.isNotEmpty(itemDTO.getPasswordPlain())) {
			entity.setPassword(passwordEncoder.encode(itemDTO.getPasswordPlain()));
			entity.setCredentialsExpired(false);
		}

		// Set Roles List from objects or names
		if (itemDTO.getRoles() != null) {
			Optional.ofNullable(itemDTO.getRoles()).ifPresent(rolesList -> {
				entity.setRoles(new HashSet<>());
				itemDTO.getRoles().stream().forEach(role -> {
					entity.getRoles().add(roleRepository.findById(role.getId()).get());
				});
			});
		} else {
			Optional.ofNullable(itemDTO.getRoleNames()).ifPresent(rolesList -> {
				entity.setRoles(new HashSet<>());
				itemDTO.getRoleNames().stream().filter(roleName -> !RoleType.ADMIN.role().equalsIgnoreCase(roleName.trim())).forEach(roleName -> {
					if (StringUtils.isNotEmpty(roleName)) {
						entity.getRoles().add(roleRepository.findOneByName(roleName));
					}
				});
			});
		}

		entity.setUpdatedBy(getCurrentUserEntity());
		entity.setUpdatedAt(new Date());
	}


	/**
	 * Send forget password email
	 *
	 * @param forgetPasswordDTO
	 * @return
	 */
	public UserEditDTO sendResetPasswordEmail(ForgetPasswordDTO forgetPasswordDTO) {
		Users user = userRepository.findFirstByEmailIgnoreCase(forgetPasswordDTO.getEmail())
			.orElseThrow(() -> new ItemNotFoundException(MessageFormat.format("User with this email is not found [{0}]", forgetPasswordDTO.getEmail()), ApplicationExceptionCodes.USER_WITH_EMAIL_NOT_EXISTS));
		emailService.sendResetPasswordEmail(user);

		return new UserEditDTO(user);
	}

	/**
	 * Expire credentials for all users with Default password
	 *
	 * @return
	 */
	@Transactional
	public List<UserRefDTO> expireUserCredentialsWithDefaultPassword() {
		Long organizationId = organizationService.getCurrentOrganizationId();

		List<UserRefDTO> result = new ArrayList<>();

		List<Users> usersList = userRepository.getListByOrganization(organizationId);
		for (Users user : usersList) {
			if (comparePasswords("password", user.getPassword())) {
				user.setCredentialsExpired(true);
				Users saveResult = userRepository.save(user);
				result.add(new UserRefDTO(user));
			}
		}

		return result;
	}

	/**
	 * Change password for user
	 *
	 * @param user
	 * @param password
	 * @return
	 */
	public Users changePassword(Users user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		user.setCredentialsExpired(false);
		user.setUpdatedAt(new Date());
		user.setUpdatedBy(user);
		Users result = userRepository.save(user);

		// There is no organization passed in case of call from ADMIN api
		// Save Audit Log UPDATE event
		if (user.getOrganization() != null) {
			auditLogService.audit(
				AuditOperationType.UPDATE,
				AuditItemType.USER_PASSWORD_CHANGED,
				user.getId(),
				new UserAuditDTO(user),
				new UserAuditDTO(user),
				user,
				AuditLogItemId.of(AuditItemType.ORGANIZATION, user.getOrganization().getId())
			);
		} else {
			auditLogService.audit(
				AuditOperationType.UPDATE,
				AuditItemType.USER_PASSWORD_CHANGED,
				user.getId(),
				new UserAuditDTO(user),
				new UserAuditDTO(user),
				user
			);
		}


		return result;
	}

	/**
	 * Update users last login date
	 *
	 * @param userId
	 * @return
	 */
	@Transactional
	public Users updateLastLoginDate(Long userId) {

		Users user = userRepository.findById(userId).get();
		user.setLastLoginDate(new Date());
		Users result = userRepository.save(user);

		return result;
	}

	/**
	 * Verify passwords
	 *
	 * @param rawPassword
	 * @param encodedPassword
	 * @return
	 */
	public boolean comparePasswords(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * Deletes User
	 *
	 * @return ID of removed User
	 */
	public Long delete(UserRefDTO itemDTO) {

		Long result = delete(itemDTO.getId());

		return result;
	}

	/**
	 * Deletes User
	 *
	 * @return ID of removed User
	 */
	public Long delete(Long itemId) {

		Long result;

		try {
			// Get Existing item from the database
			Users existingItem = getOrganizationUser(itemId);

			existingItem.setDeleted(true);

			// Delete item details
			userRepository.save(existingItem);

			// Save Audit Log DELETE event
			auditLogService.delete(
				AuditItemType.USER,
				itemId,
				new UserAuditDTO(existingItem),
				AuditLogItemId.of(AuditItemType.ORGANIZATION, existingItem.getOrganization().getId())
			);

			result = itemId;

		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("User not found in the system [{0}]", itemId), ApplicationExceptionCodes.USER_NOT_EXISTS);
		}

		return result;
	}

	/**
	 * Check is current user Authorized
	 *
	 * @return
	 */
	public boolean isAuthorized() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Boolean result = false;

		// Verify current User Details
		if (user != null && user instanceof UserDetailsImpl) {
			result = true;
		}

		return result;
	}

	/**
	 * Check is specified user has role
	 *
	 * @return
	 */
	public static boolean hasRole(String roleName, Users user) {
		if (user == null && roleName == null) {
			return false;
		}

		// Get all user names set
		Set<String> roleNamesLowerSet = user.getRoles().stream().map(role -> role.getName().toLowerCase()).collect(Collectors.toSet());

		// Check is defined role in the list
		boolean result = roleNamesLowerSet.contains(roleName.toLowerCase());

		return result;
	}

	/**
	 * Check is current user has role
	 *
	 * @return
	 */
	public boolean hasRole(String roleName) {
		UserDetailsImpl user = getCurrentUser();

		boolean result = user.getAuthorities().contains(new SimpleGrantedAuthority(roleName));

		return result;
	}

	/**
	 * Check is current user has role
	 *
	 * @return
	 */
	public boolean hasRole(RoleType role) {
		return hasRole(role.role());
	}

	/**
	 * Check is user Super Admin
	 *
	 * @return
	 */
	public boolean isSuperAdmin() {
		boolean result = false;

		if (hasRole(RoleType.ADMIN)) {
			result = true;
		}

		return result;
	}

	/**
	 * Get Current Security User
	 *
	 * @return
	 */
	public UserDetailsImpl getCurrentUser() {
		Object securityUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetailsImpl user = null;

		// Initialize current User Details
		if (securityUser != null && securityUser instanceof UserDetailsImpl) {
			user = (UserDetailsImpl) securityUser;
		}

		if (user == null) {
			throw new NotAuthenticatedException("User is not Authorized on this server.");
		}

		return user;
	}

	/**
	 * Get Current JPA User Entity
	 *
	 * @return
	 */
	public Users getCurrentUserEntity() {
		UserDetailsImpl user = getCurrentUser();

		return getUser(user.getUserId());
	}

}
