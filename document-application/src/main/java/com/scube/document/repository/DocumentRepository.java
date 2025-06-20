package com.scube.document.repository;

import com.scube.audit.auditable.repositories.AuditableEntityRepository;
import com.scube.document.model.Document;

public interface DocumentRepository extends AuditableEntityRepository<Document, Long> {
}