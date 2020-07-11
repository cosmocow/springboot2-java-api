package com.dfusiontech.server.model.dao;

import com.dfusiontech.server.model.data.BaseSort;
import com.dfusiontech.server.model.data.UsersFilter;
import com.dfusiontech.server.model.jpa.entity.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User DAO Model
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @since    2019-04-16
 */
@Service
public class UserModelDAO implements PageableModelDAO<Users, UsersFilter> {

	// @Autowired
	// private OrganizationService organizationService;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public PagedResult<Users> getItemsPageable(UsersFilter filter, Pageable pageable, BaseSort sort) {

		String nameFilter = Optional.ofNullable(filter.getName()).orElse("");
		List<Long> excludeIds = null;
		if (filter.getExcludeIds() != null && filter.getExcludeIds().size() > 0) {
			excludeIds = filter.getExcludeIds();
		}
		List<String> roles = null;
		if (filter.getRoles() != null && filter.getRoles().size() > 0) {
			roles = filter.getRoles();
		}

		String hqlQuery;
		String hqlQueryCount;

		// if organizationId isn't presented it means the request came from Admin App and we should return users without organization as well as with it
		hqlQuery = "SELECT distinct u, o.name FROM Users u LEFT JOIN u.organization o LEFT JOIN u.roles r ";

		hqlQueryCount = "SELECT count(distinct u) FROM Users u LEFT JOIN u.organization o LEFT JOIN u.roles r ";

		// Build Query String
		String whereString;
		if (filter.getIsDeleted() != null && filter.getIsDeleted()) {
			whereString = " WHERE (u.deleted IN (true, false) OR u.deleted IS NULL)";
		} else {
			whereString = " WHERE u.deleted = false";
		}
		// Base search
		if (StringUtils.isNotEmpty(nameFilter)) {
			whereString += " AND (UPPER(u.fullName) LIKE (CONCAT(UPPER(:name), '%')) OR UPPER(u.email) LIKE (CONCAT(UPPER(:name), '%')))";
		}
		if (excludeIds != null) {
			whereString += " AND u.id NOT IN :excludeIds";
		}
		if (roles != null) {
			whereString += " AND r.name IN :roles";
		}
		if (filter.getRoleId() != null) {
			whereString += " AND r.id = :roleId";
		}
		if (StringUtils.isNotEmpty(filter.getEmail())) {
			whereString += " AND UPPER(u.email) LIKE (CONCAT(UPPER(:email), '%'))";
		}
		if (StringUtils.isNotEmpty(filter.getFirstName())) {
			whereString += " AND UPPER(u.firstName) LIKE (CONCAT(UPPER(:firstName), '%'))";
		}
		if (StringUtils.isNotEmpty(filter.getLastName())) {
			whereString += " AND UPPER(u.lastName) LIKE (CONCAT(UPPER(:lastName), '%'))";
		}

		// Build Sort based on the mapping
		String searchQueryString = hqlQuery + whereString;
		Map<String, String> sortMapping = Map.ofEntries(
			Map.entry("id", "u.id"),
			Map.entry("firstName", "u.firstName"),
			Map.entry("lastName", "u.lastName"),
			Map.entry("email", "u.email"),
			Map.entry("enabled", "u.enabled"),
			Map.entry("organization", "o.name")
		);
		if (sort != null) searchQueryString += sort.toOrderString(sortMapping);

		// Build Query data
		// TypedQuery<Users> typedQuery = entityManager.createQuery(searchQueryString, Users.class);
		Query typedQuery = entityManager.createQuery(searchQueryString);
		applySearchFilterValues(filter, excludeIds, roles, typedQuery);
		typedQuery.setMaxResults(pageable.getPageSize());
		typedQuery.setFirstResult((int) pageable.getOffset());
		List<Object[]> queryResultList = typedQuery.getResultList();
		List<Users> resultList = queryResultList.stream().map(objects -> {
			Users result = (Users) objects[0];

			return result;
		}).collect(Collectors.toList());

		// Calculate count query
		Query queryCount = entityManager.createQuery(hqlQueryCount + whereString);
		applySearchFilterValues(filter, excludeIds, roles, queryCount);
		Long resultsCount = (Long) queryCount.getSingleResult();

		return new PagedResult<Users>(resultList, resultsCount);
	}

	/**
	 * Apply query data
	 *
	 * @param filter
	 * @param excludeIds
	 * @param roles
	 * @param query
	 */
	private void applySearchFilterValues(UsersFilter filter, List<Long> excludeIds, List<String> roles, Query query) {
		String nameFilter = Optional.ofNullable(filter.getName()).orElse("");

		// if (organizationId != null) query.setParameter("organizationId", organizationId);
		if (StringUtils.isNotEmpty(nameFilter)) query.setParameter("name", nameFilter);
		if (excludeIds != null) query.setParameter("excludeIds", excludeIds);
		if (roles != null) query.setParameter("roles", roles);
		if (StringUtils.isNotEmpty(filter.getEmail())) query.setParameter("email", filter.getEmail());
		if (StringUtils.isNotEmpty(filter.getFirstName())) query.setParameter("firstName", filter.getFirstName());
		if (StringUtils.isNotEmpty(filter.getLastName())) query.setParameter("lastName", filter.getLastName());
		if (filter.getRoleId() != null) query.setParameter("roleId", filter.getRoleId());
	}

}
