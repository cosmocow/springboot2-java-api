package com.dfusiontech.server.services;

import com.dfusiontech.server.model.dao.AuditLogModelDAO;
import com.dfusiontech.server.model.dao.PagedResult;
import com.dfusiontech.server.model.data.AuditLogFilter;
import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.dto.audit.AuditLogViewDTO;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.model.jpa.domain.AuditOperationType;
import com.dfusiontech.server.model.jpa.domain.RoleType;
import com.dfusiontech.server.model.jpa.entity.AuditLog;
import com.dfusiontech.server.model.jpa.entity.AuditLogItemId;
import com.dfusiontech.server.model.jpa.entity.Users;
import com.dfusiontech.server.repository.jpa.AuditLogItemIdRepository;
import com.dfusiontech.server.repository.jpa.AuditLogRepository;
import com.dfusiontech.server.rest.exception.ForbiddenException;
import com.dfusiontech.server.rest.exception.ItemNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Systems management Service. Implements basic user CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-13
 */
@Service
public class AuditLogService {

	@Autowired
	private AuditLogRepository auditLogRepository;

	@Autowired
	private AuditLogItemIdRepository auditLogItemIdRepository;

	@Autowired
	private AuditLogModelDAO auditLogModelDAO;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserService userService;

	/**
	 * Get Audit Logs List
	 *
	 * @return Audit Logs List
	 */
	public FilteredResponse<AuditLogFilter, AuditLogViewDTO> getListFiltered(FilteredRequest<AuditLogFilter> filteredRequest) {

		// Set current organization
		if (!userService.hasRole(RoleType.ADMIN)) {
			throw new ForbiddenException("Only Admin allowed to get audit logs.");
			// filteredRequest.getFilter().setOrganizationId(organizationService.getCurrentOrganizationId());
		}

		PagedResult<AuditLog> pagedResult = auditLogModelDAO.getItemsPageable(filteredRequest.getFilter(), filteredRequest.toPageRequest(), filteredRequest.getSort());

		// Convert to DTOs
		List<AuditLogViewDTO> usersDTOList = AuditLogViewDTO.fromEntitiesList(pagedResult.getItems(), AuditLogViewDTO.class);

		FilteredResponse<AuditLogFilter, AuditLogViewDTO> filteredResponse = new FilteredResponse<AuditLogFilter, AuditLogViewDTO>(filteredRequest);
		filteredResponse.setItems(usersDTOList);
		filteredResponse.setTotal(pagedResult.getCount().intValue());

		return filteredResponse;
	}

	/**
	 * Get Item details
	 *
	 * @return Item Details
	 */
	public AuditLog getItemForCurrentOrganization(Long itemId) {
		AuditLog itemDetails;

		try {
			itemDetails = auditLogRepository.findById(itemId).get();
		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("Audit Log not found in the database [{0}]", itemId));
		}

		// Verify System and Organization
		if (!userService.isSuperAdmin() && !organizationService.getCurrentOrganizationId().equals(itemDetails.getOrganizationId())) {
			throw new ForbiddenException(MessageFormat.format("Organization for Audit Log [{0}] doesn't match your organization [{1}]", itemDetails.getOrganizationId(), organizationService.getCurrentOrganizationId()));
		}

		return itemDetails;
	}

	/**
	 * Get Item DTO details
	 *
	 * @return Item Details
	 */
	public AuditLogViewDTO getDetails(Long itemId) {

		AuditLog itemDetails = getItemForCurrentOrganization(itemId);

		AuditLogViewDTO result = new AuditLogViewDTO(itemDetails);

		return result;
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @param itemType
	 * @param itemId
	 * @param newValue
	 * @param itemIds
	 * @return
	 */
	public AuditLog create(AuditItemType itemType, Long itemId, Object newValue, AuditLogItemId... itemIds) {
		return audit(AuditOperationType.CREATE, itemType, itemId, null, newValue, itemIds);
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @param itemType
	 * @param itemId
	 * @param oldValue
	 * @param newValue
	 * @param itemIds
	 * @return
	 */
	public AuditLog update(AuditItemType itemType, Long itemId, Object oldValue, Object newValue, AuditLogItemId... itemIds) {
		return audit(AuditOperationType.UPDATE, itemType, itemId, oldValue, newValue, itemIds);
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @param itemType
	 * @param itemId
	 * @param oldValue
	 * @param itemIds
	 * @return
	 */
	public AuditLog delete(AuditItemType itemType, Long itemId, Object oldValue, AuditLogItemId... itemIds) {
		return audit(AuditOperationType.DELETE, itemType, itemId, oldValue, null, itemIds);
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @param operationType
	 * @param itemType
	 * @param itemId
	 * @param oldValue
	 * @param newValue
	 * @param itemIds
	 * @return
	 */
	public AuditLog audit(AuditOperationType operationType, AuditItemType itemType, Long itemId, Object oldValue, Object newValue, AuditLogItemId... itemIds) {
		return audit(operationType, itemType, itemId, oldValue, newValue, null, itemIds);
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @param operationType
	 * @param itemType
	 * @param itemId
	 * @param oldValue
	 * @param newValue
	 * @param itemIds
	 * @return
	 */
	public AuditLog audit(AuditOperationType operationType, AuditItemType itemType, Long itemId, Object oldValue, Object newValue, Users auditUser, AuditLogItemId... itemIds) {

		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		jsonMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

		Users currentUser = auditUser != null ? auditUser : userService.getCurrentUserEntity();
		Long organizationId = auditUser != null && auditUser.getOrganization() != null ? auditUser.getOrganization().getId() : null;

		AuditLog result = new AuditLog();
		if (itemIds != null && itemIds.length > 0) {
			result.getAuditLogItemIds().addAll(Arrays.stream(itemIds)
				.peek(itemIdValue -> {
					itemIdValue.setOrganizationId(organizationId);
					auditLogItemIdRepository.save(itemIdValue);
				})
				.collect(Collectors.toList()));
		}
		result.setItemType(itemType.getId());
		result.setOperationType(operationType.getId());
		result.setOrganizationId(organizationId);
		result.setAuditItemId(itemId);
		result.setLogDate(new Date());
		if (currentUser != null) result.setAuditUserId(currentUser.getId());
		if (currentUser != null) result.setAuditUserName(currentUser.getFullName());
		if (currentUser != null) result.setAuditUserEmail(currentUser.getEmail());
		try {
			if (oldValue != null) {
				result.setOldValue(jsonMapper.writeValueAsString(oldValue));
			}
			if (newValue != null) {
				result.setNewValue(jsonMapper.writeValueAsString(newValue));
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// Save user to the database
		auditLogRepository.save(result);

		return result;
	}

	/**
	 * Create new Audit Log Item
	 *
	 * @return New Audit Log
	 */
	/*
	public SystemEditDTO create(SystemEditDTO newItemDTO) {
		// Throw Exception if ID is set in create mode
		if (newItemDTO.getId() != null) {
			throw new ConflictException(MessageFormat.format("Conflict while create item. ID is not allowed on create [{0}]", newItemDTO.getId()));
		}

		Systems newItem = newItemDTO.toEntity();
		newItem.setOrganizationId(organizationService.getCurrentOrganizationId());
		newItem.setIsEtl(false);
		newItem.setCreatedBy(userService.getCurrentUserEntity());
		newItem.setCreatedAt(new Date());
		applyEntityChanges(newItemDTO, newItem);
		Systems saveResult = systemRepository.save(newItem);

		// Send User Assignment Notification to the User
		if (saveResult.getOwner() != null) {
			sendUserAssignmentNotification(saveResult);
		}

		SystemEditDTO result = getDetails(saveResult.getId());

		return result;
	}
	*/

}
