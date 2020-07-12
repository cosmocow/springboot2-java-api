package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.entity.LanguageConstantValues;
import com.dfusiontech.server.model.jpa.entity.SupportedLanguages;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageConstantValueRepository extends CoreRepository<LanguageConstantValues, Long> {

	Optional<LanguageConstantValues> findById(Long itemId);

	Optional<LanguageConstantValues> findFirstByLanguageIdAndLanguageConstantId(Long languageId, Long languageConstantId);

	@Query("SELECT new com.dfusiontech.server.model.jpa.entity.LanguageConstantValues(lc, lcv) FROM LanguageConstants lc " +
		"LEFT JOIN FETCH LanguageConstantValues lcv ON lcv.languageConstant = lc AND lcv.language = :language ORDER BY lc.name ASC")
	List<LanguageConstantValues> getListByLanguageForAllConstants(@Param("language") SupportedLanguages language);

	List<LanguageConstantValues> getListByLanguage(SupportedLanguages language);

}
