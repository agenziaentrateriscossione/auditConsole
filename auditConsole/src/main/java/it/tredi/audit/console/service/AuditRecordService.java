package it.tredi.audit.console.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;
import it.tredi.audit.console.exception.NotFoundAuditException;
import it.tredi.spring.progressbar.entity.Progressbar;

public interface AuditRecordService {
	
	/**
	 * Ricerca paginata di record di audit in base ai filtri specificati
	 * @param filter Insieme di filtri in base ai quali costruire la query di ricerca
	 * @return Numero di record risultanti dalla ricerca in base ai filtri specificati
	 */
	public long count(AuditRecordFilter filter);
	
	/**
	 * Ricerca paginata di record di audit in base ai filtri specificati
	 * @param filter Insieme di filtri in base ai quali costruire la query di ricerca
	 * @param page Indice della pagina da caricare
	 * @param size Numero di record da caricare per la pagina richiesta
	 * @return Elenco di record recuperati tramite la ricerca
	 */
	public Page<AuditRecord> search(AuditRecordFilter filter, int page, int size);
	
	/**
	 * Caricamento di una istanza di record di audit in base al proprio id
	 * @param Id Identificativo del record da caricare
	 * @return Record richiesto
	 * @throws NotFoundAuditException
	 */
	public AuditRecord load(String id) throws NotFoundAuditException;
	
	/**
	 * Caricamento di tutti gli archivi registrati sul db di audit
	 * @return
	 */
	public List<String> loadDbs();
	
	/**
	 * Caricamento di tutte le tipologie di record (relative ad uno specifico archivio) registrate sul db di audit
	 * @param dbName Nome dell'archivio in base al quale ricercare le tipologie di record
	 * @param reload true se occorre forzare il caricamento delle tipoligie di record, false se e' sufficiente caricarle dalla cache
	 * @return
	 */
	public List<String> loadRecordTypes(String dbName, boolean reload);
	
	/**
	 * Caricamento di tutte le tipologie di azione (relative ad uno specifico archivio) registrate sul db di audit
	 * @param dbName Nome dell'archivio in base al quale ricercare le tipologie di azione
	 * @param reload true se occorre forzare il caricamento delle tipoligie di azione, false se e' sufficiente caricarle dalla cache
	 * @return
	 */
	public List<String> loadActions(String dbName, boolean reload);
	
	/**
	 * Avvio del processo di esportazione record tramite specifico thread (avanzamento tramite progressbar)
	 * @param filter Filtro di ricerca in base al quale selezionare i record da estrarre
	 * @return Informazioni relative alla progressbar generata
	 * @throws Exception
	 */
	public Progressbar startExport(AuditRecordFilter filter) throws Exception;
	
}
