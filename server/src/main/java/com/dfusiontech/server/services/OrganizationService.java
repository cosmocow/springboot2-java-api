package com.dfusiontech.server.services;

import com.dfusiontech.server.auth.UserDetailsImpl;
import com.dfusiontech.server.context.ApplicationContextThreadLocal;
import com.dfusiontech.server.model.dto.organization.OrganizationEditDTO;
import com.dfusiontech.server.model.dto.organization.OrganizationViewDTO;
import com.dfusiontech.server.model.jpa.domain.AuditItemType;
import com.dfusiontech.server.model.jpa.domain.PermissionType;
import com.dfusiontech.server.model.jpa.entity.AuditLogItemId;
import com.dfusiontech.server.model.jpa.entity.Organizations;
import com.dfusiontech.server.repository.jpa.*;
import com.dfusiontech.server.rest.exception.ApplicationExceptionCodes;
import com.dfusiontech.server.rest.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.MessageFormat;
import java.util.*;

/**
 * Organization management Service. Implements basic Organization logic.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-08
 */
@Service("organizationService")
public class OrganizationService {

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private LanguageRepository languageRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private StateRepository stateRepository;

	// @Autowired
	// private StatusRepository statusRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SupportedLanguagesRepository supportedLanguagesRepository;

	@Autowired
	private PermissionService permissionService;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Get Current Organization
	 *
	 * @return
	 */
	public OrganizationViewDTO getCurrentOrganization() {
		UserDetailsImpl user = userService.getCurrentUser();

		if (user.getOrganizationId() != null) {
			Organizations organization = getOrganization(user.getOrganizationId());
			OrganizationViewDTO result = new OrganizationViewDTO(organization);

			return result;
		}

		return null;
	}

	/**
	 * Update Current Organization Details
	 *
	 * @return
	 */
	public OrganizationEditDTO updateCurrentOrganization(OrganizationEditDTO organizationEditDTO) {

		Organizations organization = getCurrentOrganizationEntity();
		OrganizationEditDTO existingItemDTO = new OrganizationEditDTO(organization);

		if (permissionService.checkCurrentUserPermission(PermissionType.ORGANIZATION_SUPPORTED_LANGUAGES_UPDATE)) {

			Optional.ofNullable(organizationEditDTO.getSupportedLanguages()).ifPresent(supportedLanguageEditDTOList -> {
				organization.setSupportedLanguages(new HashSet<>());
				organizationEditDTO.getSupportedLanguages().stream().forEach(supportedLanguageEditDTO -> {
					organization.getSupportedLanguages().add(supportedLanguagesRepository.findById(supportedLanguageEditDTO.getId()).get());
				});
			});

			if (organization.getSupportedLanguages().size() > 1) {
				organization.setIsMultiLanguage(true);
			} else {
				organization.setIsMultiLanguage(false);
			}
		}

		organizationRepository.save(organization);

		OrganizationEditDTO result = new OrganizationEditDTO(organization);

		// Save Audit Log UPDATE event
		auditLogService.update(
			AuditItemType.ORGANIZATION,
			result.getId(),
			existingItemDTO,
			result,
			collectAuditLogItems(result, (organization.getRootParent() != null ? organization.getRootParent().getId() : null))
		);

		return result;
	}

	/**
	 * Get Specific Organization
	 *
	 * @return
	 */
	public Organizations getOrganization(Long itemId) {
		Organizations organization = organizationRepository.findById(itemId)
			.orElseThrow(() -> new BadRequestException(MessageFormat.format("Organization not found [{0}]", itemId), ApplicationExceptionCodes.ORGANIZATION_NOT_EXISTS));

		return organization;
	}

	/**
	 * Get Current Organization Id
	 *
	 * @return
	 */
	public Long getCurrentOrganizationId() {
		UserDetailsImpl user = userService.getCurrentUser();

		Long organizationId = user.getOrganizationId();

		if (organizationId == null && userService.isSuperAdmin()) {
			organizationId = ApplicationContextThreadLocal.getContext().getOrganizationId();
		}

		return organizationId;
	}

	/**
	 * Get Current Organization Entity
	 *
	 * @return
	 */
	public Organizations getCurrentOrganizationEntity() {
		Organizations result = getOrganization(getCurrentOrganizationId());

		return result;
	}

	/**
	 * Get List of Organizations by Type and Filter
	 *
	 * @return Users List
	 */
	/*
	public FilteredResponse<NameFilter, OrganizationViewDTO> getListFiltered(OrganizationType organizationType, FilteredRequest<NameFilter> filteredRequest) {
		List<Organizations> items;
		Long count = 0l;
		FilteredResponse<NameFilter, OrganizationViewDTO> filteredResponse = new FilteredResponse<NameFilter, OrganizationViewDTO>(filteredRequest);

		String nameFilter = Optional.ofNullable(filteredRequest.getFilter().getName()).orElse("");
		List<Long> excludeIds = Arrays.asList(0L);
		if (filteredRequest.getFilter() != null && filteredRequest.getFilter().getExcludeIds() != null && filteredRequest.getFilter().getExcludeIds().size() > 0) {
			excludeIds = filteredRequest.getFilter().getExcludeIds();
		}

		items = organizationRepository.filterOrganizationsByType(organizationType, nameFilter, excludeIds, filteredRequest.toPageRequest());
		count = organizationRepository.getOrganizationsCountByType(organizationType, nameFilter, excludeIds);

		List<OrganizationViewDTO> itemsDTOList = DTOBase.fromEntitiesList(items, OrganizationViewDTO.class);

		filteredResponse.setItems(itemsDTOList);
		filteredResponse.setTotal(count.intValue());

		return filteredResponse;
	}
	*/

	/**
	 * Fill the set of entity relations
	 *
	 * @param itemDTO
	 * @param entity
	 */
	protected void applyEntityChanges(OrganizationEditDTO itemDTO, Organizations entity) {

		entity.setName(itemDTO.getName());
		entity.setDescription(itemDTO.getDescription());
		entity.setTaxId(itemDTO.getTaxId());
		entity.setVatId(itemDTO.getVatId());
		entity.setStreetAddress1(itemDTO.getStreetAddress1());
		entity.setStreetAddress2(itemDTO.getStreetAddress2());
		entity.setZip(itemDTO.getZip());
		entity.setPhone(itemDTO.getPhone());
		entity.setSite(itemDTO.getSite());
		entity.setIsPublicCompany(itemDTO.getIsPublicCompany());
		entity.setUseMultiFactorAuth(itemDTO.getUseMultiFactorAuth());

		if (itemDTO.getCountry() != null && itemDTO.getCountry().getId() != null) {
			entity.setCountry(countryRepository.findById(itemDTO.getCountry().getId()).orElse(null));
		} else {
			entity.setCountry(null);
		}
		if (itemDTO.getState() != null && itemDTO.getState().getId() != null) {
			entity.setState(stateRepository.findById(itemDTO.getState().getId()).orElse(null));
		} else {
			entity.setState(null);
		}
		if (itemDTO.getCity() != null && itemDTO.getCity().getId() != null) {
			entity.setCity(cityRepository.findById(itemDTO.getCity().getId()).orElse(null));
		} else {
			entity.setCity(null);
		}
		if (itemDTO.getCurrency() != null && itemDTO.getCurrency().getId() != null) {
			entity.setCurrency(currencyRepository.findById(itemDTO.getCurrency().getId()).orElse(null));
		} else {
			entity.setCurrency(null);
		}
		if (itemDTO.getLanguage() != null && itemDTO.getLanguage().getId() != null) {
			entity.setLanguage(languageRepository.findById(itemDTO.getLanguage().getId()).orElse(null));
		} else {
			entity.setLanguage(null);
		}
//		if (itemDTO.getStatus() != null && itemDTO.getStatus().getId() != null) {
//			entity.setStatus(statusRepository.findById(itemDTO.getStatus().getId()).orElse(null));
//		} else {
//			entity.setStatus(null);
//		}
		if (itemDTO.getParent() != null && itemDTO.getParent().getId() != null) {
			entity.setParent(organizationRepository.findById(itemDTO.getParent().getId()).orElse(null));
		} else {
			entity.setParent(null);
		}
		if (itemDTO.getOwner() != null && itemDTO.getOwner().getId() != null) {
			entity.setOwner(userService.getUser(itemDTO.getOwner().getId()));
		} else {
			entity.setOwner(null);
		}
	}


	/**
	 * Get Path for Parent and Organization
	 *
	 * @param parentPath
	 * @param organizationId
	 * @return
	 */
	/*
	public Organizations getParentByPath(String parentPath, Long organizationId) {
		Organizations result = null;

		if (StringUtils.isNotEmpty(parentPath)) {
			String[] parents = StringUtils.split(parentPath,";");
			List<String> parentsList = Arrays.stream(parents).map(parentName -> parentName.trim()).collect(Collectors.toList());
			Collections.reverse(parentsList);

			Optional<Organizations> currentParent = Optional.empty();
			if (parentsList.size() > 0) {
				currentParent = organizationRepository.getByNameAndNoParentForRootOrganization(parentsList.get(0), organizationId);

				if (currentParent.isPresent()) {
					for (int i = 1; i < parentsList.size(); i++) {
						String parentName = parentsList.get(i).trim();
						currentParent = organizationRepository.getByParentNameForRootOrganization(parentName, currentParent.get().getId(), organizationId);

						if (currentParent.isEmpty()) return null;
					}

					result = currentParent.get();
				}
			}
		}

		return result;
	}
	*/

	/**
	 * Collect items for Audit Log record
	 *
	 * @param existingItemDTO
	 * @param organizationId
	 * @return
	 */
	protected AuditLogItemId[] collectAuditLogItems(OrganizationEditDTO existingItemDTO, Long organizationId) {
		List<AuditLogItemId> logItems = new ArrayList<>(Arrays.asList(AuditLogItemId.of(AuditItemType.ORGANIZATION, organizationId)));

		return logItems.stream().toArray(AuditLogItemId[]::new);
	}

}
