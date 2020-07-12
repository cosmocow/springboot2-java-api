package com.dfusiontech.server.services;

import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.NameFilter;
import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.role.RoleListDTO;
import com.dfusiontech.server.model.jpa.entity.Roles;
import com.dfusiontech.server.repository.jpa.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Roles management Service. Implements basic user CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-11-13
 */
@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	/**
	 * Get Roles List
	 *
	 * @return Users List
	 */
	public FilteredResponse<NameFilter, RoleListDTO> getListFiltered(FilteredRequest<NameFilter> filteredRequest) {
		List<Roles> items = null;
		Long count = 0l;
		FilteredResponse<NameFilter, RoleListDTO> filteredResponse = new FilteredResponse<NameFilter, RoleListDTO>(filteredRequest);

		String namePattern = "";
		if (filteredRequest.getFilter() != null && filteredRequest.getFilter().getName() != null) {
			namePattern = filteredRequest.getFilter().getName();
		}

		items = roleRepository.getRolesByNameForNonAdmin(namePattern, filteredRequest.toPageRequest());
		count = roleRepository.getCountRolesByNameForNonAdmin(namePattern);

		List<RoleListDTO> itemsDTOList = DTOBase.fromEntitiesList(items, RoleListDTO.class);

		filteredResponse.setItems(itemsDTOList);
		filteredResponse.setTotal(count.intValue());

		return filteredResponse;
	}

}
