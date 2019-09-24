package it.tredi.audit.console.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import it.tredi.audit.audit.validation.ChecksumValidation;
import it.tredi.audit.console.AppConstants;
import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;
import it.tredi.audit.console.exception.NotFoundAuditException;
import it.tredi.audit.console.repository.AuditRecordRepository;
import it.tredi.audit.console.service.memory.FilterListType;
import it.tredi.audit.console.service.memory.FiltersMap;
import it.tredi.spring.progressbar.entity.Progressbar;

@Service
public class AuditRecordServiceImpl implements AuditRecordService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditRecordServiceImpl.class);
	
	@Autowired
	private AuditRecordRepository auditRecordRepository;
	
	@Autowired
	private ExportThread exportService;

	@Override
	public long count(AuditRecordFilter filter) {
		return auditRecordRepository.count(filter);
	}
	
	@Override
	public Page<AuditRecord> search(AuditRecordFilter filter, int page, int size) {
		// controllo sugli indici di pagina specificati
		if (page < 0) page = 0;
		if (size < 0) size = AppConstants.DEFAULT_SEARCH_PAGE_SIZE;
		
		long time = System.currentTimeMillis();
		Page<AuditRecord> results = auditRecordRepository.search(filter, page, size, Sort.by(Sort.Direction.DESC, AuditRecord.FIELD_DATA));
		time = System.currentTimeMillis() - time;
		
		// TODO inserire il tempo impiegato all'interno dell'oggetto Page (estendendolo) in modo da rendere il dato disponibile al client
		
		if (logger.isInfoEnabled()) 
			logger.info("AuditRecordServiceImpl.search(): Search tooks " + time + " ms.");
		
		return results;
	}
	
	@Override
	public AuditRecord load(String id) throws NotFoundAuditException {
		AuditRecord record = null;
		if (id != null && !id.isEmpty()) {
			Optional<AuditRecord> optRecord = auditRecordRepository.findById(id);
			if (optRecord.isPresent()) {
				record = optRecord.get();

				// Validazione del checksum del record di AUDIT
				try {
					ChecksumValidation checksumValidation = new ChecksumValidation(record);
					record.setValidChecksum(checksumValidation.isValid(record.getChecksum()));
				}
				catch (Exception e) {
					logger.error("AuditRecordServiceImpl.load(): Unable to validate checksum... " + e.getMessage(), e);
				}
			}
		}
		if (record == null) {
			throw new NotFoundAuditException("Audit record not Found by ID " + id + "!", id);
		}
		return record;
	}

	@Override
	public List<String> loadDbs() {
		return auditRecordRepository.loadDbs();
	}

	@Override
	public List<String> loadRecordTypes(String dbName, boolean reload) {
		List<String> recordTypes = null;
		if (!reload)
			recordTypes = FiltersMap.getInstance().getList(dbName, FilterListType.RECORD_TYPES);
		if (reload || recordTypes == null || recordTypes.isEmpty()) {
			recordTypes = auditRecordRepository.loadRecordTypes(dbName);
			FiltersMap.getInstance().addList(dbName, FilterListType.RECORD_TYPES, recordTypes);
		}
		return recordTypes;
	}

	@Override
	public List<String> loadActions(String dbName, boolean reload) {
		List<String> actions = null;
		if (!reload)
			actions = FiltersMap.getInstance().getList(dbName, FilterListType.ACTIONS);
		if (reload || actions == null || actions.isEmpty()) {
			actions = auditRecordRepository.loadActions(dbName);
			FiltersMap.getInstance().addList(dbName, FilterListType.ACTIONS, actions);
		}
		return actions;
	}

	@Override
	public Progressbar startExport(AuditRecordFilter filter) throws Exception {
		return exportService.startExport(filter);
	}
	
}

