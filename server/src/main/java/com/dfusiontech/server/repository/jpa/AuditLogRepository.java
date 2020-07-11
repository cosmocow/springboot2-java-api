package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.entity.AuditLog;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditLogRepository extends CoreRepository<AuditLog, Long> {

	Optional<AuditLog> findById(Long id);

}
