package com.dfusiontech.server.repository.jpa;

import com.dfusiontech.server.model.jpa.entity.AuditLogItemId;
import com.dfusiontech.server.repository.jpa.core.CoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogItemIdRepository extends CoreRepository<AuditLogItemId, Long> {
}
