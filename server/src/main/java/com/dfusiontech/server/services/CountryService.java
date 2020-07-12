package com.dfusiontech.server.services;

import com.dfusiontech.server.model.data.FilteredRequest;
import com.dfusiontech.server.model.data.FilteredResponse;
import com.dfusiontech.server.model.data.NameFilter;
import com.dfusiontech.server.model.dto.DTOBase;
import com.dfusiontech.server.model.dto.common.CountryViewDTO;
import com.dfusiontech.server.model.jpa.entity.Country;
import com.dfusiontech.server.repository.jpa.CountryRepository;
import com.dfusiontech.server.rest.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Country management Service. Implements basic user CRUD.
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-27
 */
@Service
public class CountryService {

	@Autowired
	private CountryRepository countryRepository;

	/**
	 * Get Country List
	 *
	 * @return Country List
	 */
	public FilteredResponse<NameFilter, CountryViewDTO> getListFiltered(FilteredRequest<NameFilter> filteredRequest) {
		List<Country> items = null;
		Long count = 0l;
		FilteredResponse<NameFilter, CountryViewDTO> filteredResponse = new FilteredResponse<NameFilter, CountryViewDTO>(filteredRequest);

		String namePattern = "";
		if (filteredRequest.getFilter() != null && filteredRequest.getFilter().getName() != null) {
			namePattern = filteredRequest.getFilter().getName();
		}

		items = countryRepository.getListByName(namePattern, filteredRequest.toPageRequest());
		count = countryRepository.getCountByName(namePattern);

		List<CountryViewDTO> itemsDTOList = DTOBase.fromEntitiesList(items, CountryViewDTO.class);

		filteredResponse.setItems(itemsDTOList);
		filteredResponse.setTotal(count.intValue());

		return filteredResponse;
	}

	/**
	 * Get Country Item Details
	 *
	 * @rerturn Country Item Details
	 */
	public Country getCountry(Long itemId) {
		Country itemDetails;

		try {
			itemDetails = countryRepository.findById(itemId).get();
		} catch (NoSuchElementException exception) {
			throw new ItemNotFoundException(MessageFormat.format("Country not found in the database [{0}]", itemId));
		}

		return itemDetails;
	}

}
