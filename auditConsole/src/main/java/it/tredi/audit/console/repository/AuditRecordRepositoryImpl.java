package it.tredi.audit.console.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.client.DistinctIterable;

import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;

/**
 * Implementazione dell'interfaccia repository per la gestione dell'entity auditRecord
 */
@Repository
public class AuditRecordRepositoryImpl implements AuditRecordCustomRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public long count(AuditRecordFilter filter) {
		Query query = buildQueryByFilters(filter);
		return mongoTemplate.count(query, AuditRecord.class);
	}

	@Override
	public Page<AuditRecord> search(AuditRecordFilter filter, int page, int size, Sort sort) {
		Query query = buildQueryByFilters(filter);
		
		long total = mongoTemplate.count(query, AuditRecord.class);
		
		// projection (impostata tramite esclusione di campi)
		query.fields().exclude(AuditRecord.FIELD_CHANGES).exclude(AuditRecord.FIELD_EXTRAINFO).exclude(AuditRecord.FIELD_CHECKSUM);
		
		// limits e sort
		Pageable pageable = null;
		if (sort != null)
			pageable = PageRequest.of(page, size, sort);
		else
			pageable = PageRequest.of(page, size);
		query.with(pageable);
		
		return new PageImpl<AuditRecord>(mongoTemplate.find(query, AuditRecord.class), pageable, total);
	}
	
	/**
	 * Costruzione della query di ricerca su MongoDB in base ai filtri specificati
	 * @param filter
	 * @return
	 */
	private Query buildQueryByFilters(AuditRecordFilter filter) {
		List<Criteria> andCriterias = new ArrayList<>();
		if (filter != null) {
			// filtro su nome dell'archivio
			if (filter.getArchivio() != null && !filter.getArchivio().isEmpty())
				andCriterias.add(Criteria.where(AuditRecord.FIELD_ARCHIVIO).is(filter.getArchivio()));
			
			// filtro su tipologia di record
			if (filter.getTipoRecord() != null && !filter.getTipoRecord().isEmpty())
				andCriterias.add(Criteria.where(AuditRecord.FIELD_TIPORECORD).is(filter.getTipoRecord()));
			
			// filtro su identificativo del record
			if (filter.getIdRecord() != null && !filter.getIdRecord().isEmpty())
				andCriterias.add(Criteria.where(AuditRecord.FIELD_IDRECORD).is(filter.getIdRecord()));
			
			// filtro su tipologia di azione
			if (filter.getTipoAzione() != null && filter.getTipoAzione().length > 0) {
				List<Criteria> azioniCriteria = new ArrayList<>();
				for (String azione : filter.getTipoAzione()) {
					if (azione != null && !azione.isEmpty())
						azioniCriteria.add(Criteria.where(AuditRecord.FIELD_TIPOAZIONE).is(azione));
				}
				if (!azioniCriteria.isEmpty())
					andCriterias.add(new Criteria().orOperator(azioniCriteria.toArray(new Criteria[azioniCriteria.size()])));
			}
			
			// filtro su data di registrazione
			if (filter.getFrom() != null || filter.getTo() != null) {
				Criteria dateCriteria = Criteria.where(AuditRecord.FIELD_DATA);
				if (filter.getFrom() != null)
					dateCriteria.gte(filter.getFrom());
				if (filter.getTo() != null)
					dateCriteria.lte(filter.getTo());
				andCriterias.add(dateCriteria);
			}
			
			// fitro su username
			if (filter.getUsername() != null && !filter.getUsername().isEmpty()) {
				andCriterias.add(Criteria.where(AuditRecord.FIELD_ACTUAL_USERNAME).is(filter.getUsername()));
				//Criteria case1 = new Criteria();
				//case1.andOperator(Criteria.where(AuditRecord.FIELD_USERNAME).is(filter.getUsername()), Criteria.where(AuditRecord.FIELD_DELEGA_USERNAME).exists(false));
				//andCriterias.add(new Criteria().orOperator(case1, Criteria.where(AuditRecord.FIELD_DELEGA_USERNAME).is(filter.getUsername())));
			}
			
			// filtro su matricola/codice utente
			if (filter.getCodUser() != null && !filter.getCodUser().isEmpty()) {
				andCriterias.add(Criteria.where(AuditRecord.FIELD_ACTUAL_CODUSER).is(filter.getCodUser()));
				//Criteria case1 = new Criteria();
				//case1.andOperator(Criteria.where(AuditRecord.FIELD_CODUSER).is(filter.getCodUser()), Criteria.where(AuditRecord.FIELD_DELEGA_CODUSER).exists(false));
				//andCriterias.add(new Criteria().orOperator(case1, Criteria.where(AuditRecord.FIELD_DELEGA_CODUSER).is(filter.getCodUser())));
			}
		}
		Query query = new Query();
		if (!andCriterias.isEmpty())
			query.addCriteria(new Criteria().andOperator(andCriterias.toArray(new Criteria[andCriterias.size()])));
		return query;
	}
	
	@Override
	public List<String> loadDbs() {
		return distinctToList(mongoTemplate.getCollection(AuditRecord.COLLECTION_NAME).distinct(AuditRecord.FIELD_ARCHIVIO, String.class));
	}
	
	private List<String> distinctToList(DistinctIterable<String> iterable) {
		List<String> list = new ArrayList<>();
		if (iterable != null) {
			for (String item : iterable) {
				if (item != null)
					list.add(item);
		    }
		}
		return list;
	}
	
	/**
	 * Esecuzione di una query distinct con filtro su uno specifico archivio
	 * @param fieldName
	 * @param dbName
	 * @return
	 */
	private DistinctIterable<String> getDistinctOnArchivio(String fieldName, String dbName) {
		// filtro su nome dell'archivio
		BsonDocument filter = new BsonDocument(AuditRecord.FIELD_ARCHIVIO, new BsonString(dbName));
		return mongoTemplate.getCollection(AuditRecord.COLLECTION_NAME).distinct(fieldName, filter, String.class);
	}

	@Override
	public List<String> loadRecordTypes(String dbName) {
		return distinctToList(getDistinctOnArchivio(AuditRecord.FIELD_TIPORECORD, dbName));
	}

	@Override
	public List<String> loadActions(String dbName) {
		return distinctToList(getDistinctOnArchivio(AuditRecord.FIELD_TIPOAZIONE, dbName));
	}
	
}
