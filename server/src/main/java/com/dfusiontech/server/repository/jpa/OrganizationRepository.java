package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.entity.Organizations;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends CoreRepository<Organizations, Long> {

	Optional<Organizations> findById(Long id);

	Optional<Organizations> findFirstByNameAndIdIsNotIn(String name, Collection<Long> excludeIds);

	Optional<Organizations> findFirstByNameAndRootParentAndIdIsNotIn(String name, Organizations root, Collection<Long> excludeIds);

	List<Organizations> findAllByNameIsStartingWith(String name, Pageable pageable);

	Long countAllByNameIsStartingWith(String name);

	@Query("SELECT o FROM Organizations o LEFT JOIN FETCH o.country ct " +
		"LEFT JOIN FETCH o.state st LEFT JOIN FETCH o.city ci " +
		"LEFT JOIN FETCH o.currency cu LEFT JOIN FETCH o.language ln LEFT JOIN FETCH o.status sa " +
		"WHERE UPPER(o.name) LIKE (CONCAT(UPPER(:name), '%')) AND o.id NOT IN :excludeIds")
	List<Organizations> filterOrganizations(@Param("name") String name, @Param("excludeIds") Collection<Long> excludeIds, Pageable pageable);

	@Query("SELECT COUNT(o) FROM Organizations o " +
		"WHERE UPPER(o.name) LIKE (CONCAT(UPPER(:name), '%')) AND o.id NOT IN :excludeIds")
	Long getOrganizationsCount(@Param("name") String name, @Param("excludeIds") Collection<Long> excludeIds);

}
