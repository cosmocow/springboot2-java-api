package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.entity.LanguageConstants;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageConstantRepository extends CoreRepository<LanguageConstants, Long> {

	Optional<LanguageConstants> findById(Long itemId);

	Optional<LanguageConstants> findFirstByName(String name);

}
