package it.tredi.audit.console.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.tredi.audit.console.AppConstants;
import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;
import it.tredi.audit.console.service.AuditRecordService;
import it.tredi.spring.progressbar.entity.Progressbar;

/**
 * Medoti REST esposti per la collection di audit
 * @author mbernardini
 */
@RestController
@RequestMapping(value = AppConstants.API_URI_PREFIX + "/audit")
public class AuditRecordController {

	private static final Logger logger = LoggerFactory.getLogger(AuditRecordController.class);
	
	@Autowired
	private AuditRecordService auditRecordService;
	
	@Value("${server.servlet.context-path}")
	private String appContext;
	
	/**
	 * Ricerca di record di audit in base ai filtri indicati tramite parametri della richiesta GET
	 * @param filter Insieme di filtri in base ai quali ricercare i record di audit
	 * @param page Indice della pagina da caricare
	 * @param size Numero di risultati per pagina da caricare
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	public Page<AuditRecord> search(AuditRecordFilter filter, 
													@RequestParam(value="page", required=false) Integer page,
													@RequestParam(value="size", required=false) Integer size) throws Exception {
		if (logger.isInfoEnabled()) {
			if (filter != null) {
				if (filter.getArchivio() != null)
					logger.info("AuditRecordController.search() -> archivio = " + filter.getArchivio());
				if (filter.getTipoRecord() != null)
					logger.info("AuditRecordController.search() -> tipoRecord = " + filter.getTipoRecord());
				if (filter.getIdRecord() != null)
					logger.info("AuditRecordController.search() -> idRecord = " + filter.getIdRecord());
				if (filter.getTipoAzione() != null && filter.getTipoAzione().length > 0)
					logger.info("AuditRecordController.search() -> tipoAzione = " + Arrays.toString(filter.getTipoAzione()));
				if (filter.getFrom() != null)
					logger.info("AuditRecordController.search() -> from = " + filter.getFrom());
				if (filter.getTo() != null)
					logger.info("AuditRecordController.search() -> to = " + filter.getTo());
				if (filter.getUsername() != null)
					logger.info("AuditRecordController.search() -> username = " + filter.getUsername());
				if (filter.getCodUser() != null)
					logger.info("AuditRecordController.search() -> codUser = " + filter.getCodUser());
			}
			logger.info("AuditRecordController.search() -> page = " + page);
			logger.info("AuditRecordController.search() -> size = " + size);
		}
		
		if (page == null) page = 0;
		if (size == null) size = AppConstants.DEFAULT_SEARCH_PAGE_SIZE;
		
		return auditRecordService.search(filter, page.intValue(), size.intValue());
	}
	
	/**
	 * Caricamento in visualizzazione di un record di audit dato il suo identificativo
	 * @param id Identificativo del record di audit da caricare
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}/view", method = RequestMethod.GET)
	@ResponseBody
	public AuditRecord viewRecord(@PathVariable("id") String id) throws Exception {
   		if (logger.isInfoEnabled())
			logger.info("AuditRecordController.viewRecord() -> id = " + id);
		
		return auditRecordService.load(id);
    }
	
	/**
	 * Ritorna l'elenco di nomi di archivi registrato sul db di audit
	 * @return Lista di nomi di archivio estratti dal db di audit
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbs", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadDbs() throws Exception {
		return auditRecordService.loadDbs();
	}
	
	/**
	 * Ritorna l'elenco di tipologie di record registrate sul db di audit e associate ad uno specifico archivio
	 * @param dbName Nome dell'archivio in base al quale filtrare le tipologie di record
	 * @param reload true se occorre forzare il caricamento delle tipoligie di record, false se e' sufficiente caricarle dalla cache
	 * @return Lista di tipologie di record estratte dal db di audit
	 * @throws Exception
	 */
	@RequestMapping(value = "/{db}/recordTypes", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadRecordTypes(@PathVariable("db") String dbName, @RequestParam(value="reload", required=false) boolean reload) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("AuditRecordController.loadRecordTypes() -> db = " + dbName);
			logger.info("AuditRecordController.loadRecordTypes() -> reload = " + reload);
		}
		
		return auditRecordService.loadRecordTypes(dbName, reload);
	}
	
	/**
	 * Ritorna l'elenco di tipologie di azione registrate sul db di audit e associate ad uno specifico archivio
	 * @param dbName Nome dell'archivio in base al quale filtrare le tipologie di azione
	 * @param reload true se occorre forzare il caricamento delle tipoligie di azione, false se e' sufficiente caricarle dalla cache
	 * @return Lista di tipologie di azione estratte dal db di audit
	 * @throws Exception
	 */
	@RequestMapping(value = "/{db}/actions", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadActions(@PathVariable("db") String dbName, @RequestParam(value="reload", required=false) boolean reload) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("AuditRecordController.loadActions() -> db = " + dbName);
			logger.info("AuditRecordController.loadActions() -> reload = " + reload);
		}
		
		return auditRecordService.loadActions(dbName, reload);
	}
	
	/**
	 * Avvio della procedura di esportazione CSV dei record di audit in base ad un filtro di ricerca indicato
	 * @param filter Filtro in base al quale filtrare i record da esportare
	 * @return Informazioni relative alla progressbar di esportazione
	 * @throws Exception
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Progressbar> startExport(AuditRecordFilter filter) throws Exception {
		if (logger.isInfoEnabled()) {
			if (filter != null) {
				if (filter.getArchivio() != null)
					logger.info("AuditRecordController.startExport() -> archivio = " + filter.getArchivio());
				if (filter.getTipoRecord() != null)
					logger.info("AuditRecordController.startExport() -> tipoRecord = " + filter.getTipoRecord());
				if (filter.getIdRecord() != null)
					logger.info("AuditRecordController.startExport() -> idRecord = " + filter.getIdRecord());
				if (filter.getTipoAzione() != null && filter.getTipoAzione().length > 0)
					logger.info("AuditRecordController.startExport() -> tipoAzione = " + Arrays.toString(filter.getTipoAzione()));
				if (filter.getFrom() != null)
					logger.info("AuditRecordController.startExport() -> from = " + filter.getFrom());
				if (filter.getTo() != null)
					logger.info("AuditRecordController.startExport() -> to = " + filter.getTo());
				if (filter.getUsername() != null)
					logger.info("AuditRecordController.startExport() -> username = " + filter.getUsername());
				if (filter.getCodUser() != null)
					logger.info("AuditRecordController.startExport() -> codUser = " + filter.getCodUser());
			}
		}
		
		Progressbar progressbar = auditRecordService.startExport(filter);
		HttpHeaders headers = new HttpHeaders();
		String location = (progressbar.getUri() != null) ? progressbar.getUri().getRefresh() : null;
		if (location != null && !location.isEmpty()) {
			if (appContext != null && !appContext.isEmpty())
				location = appContext + location;
			headers.set(HttpHeaders.LOCATION, location);
		}
		return ResponseEntity.accepted().headers(headers).body(progressbar);
	}
	
}
