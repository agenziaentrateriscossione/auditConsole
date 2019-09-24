package it.tredi.audit.console.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.tredi.audit.console.entity.AuditRecord;

/**
 * Interfaccia repository per la gestione dell'entity auditRecord
 */
public interface AuditRecordRepository extends MongoRepository<AuditRecord, String>, AuditRecordCustomRepository {
	
}
