package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.domain.IdpType;
import com.dfusiontech.server.model.jpa.entity.IdpUsers;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdpUserRepository extends CoreRepository<IdpUsers, Long> {

	Optional<IdpUsers> findById(Long id);

	IdpUsers findByUserIdentity(String email);

	Optional<IdpUsers> findFirstByUserIdentityIgnoreCase(String email);

	Optional<IdpUsers> findFirstByUserIdentityIgnoreCaseAndIdpId(String email, IdpType idpType);

}
