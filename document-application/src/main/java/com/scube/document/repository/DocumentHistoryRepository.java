package com.scube.document.repository;

import com.scube.audit.auditable.repositories.AuditableEntityRepository;
import com.scube.document.model.DocumentHistory;

public interface DocumentHistoryRepository extends AuditableEntityRepository<DocumentHistory, Long> {
}
