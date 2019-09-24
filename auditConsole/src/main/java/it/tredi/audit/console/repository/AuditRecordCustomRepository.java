package it.tredi.audit.console.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;

/**
 * Interfaccia repository CUSTOM per la gestione dell'entity auditRecord
 */
public interface AuditRecordCustomRepository {
	
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
	 * @param sort Criteri di ordinamento dei risultati della ricerca
	 * @return Elenco di record recuperati tramite la ricerca
	 */
	public Page<AuditRecord> search(AuditRecordFilter filter, int page, int size, Sort sort);
	
	/**
	 * Caricamento di tutti gli archivi registrati sul db di audit
	 * @return
	 */
	public List<String> loadDbs();
	
	/**
	 * Caricamento di tutte le tipologie di record (relative ad uno specifico archivio) registrate sul db di audit
	 * @param dbName Nome dell'archivio in base al quale ricercare le tipologie di record
	 * @return
	 */
	public List<String> loadRecordTypes(String dbName);
	
	/**
	 * Caricamento di tutte le tipologie di azione (relative ad uno specifico archivio) registrate sul db di audit
	 * @param dbName Nome dell'archivio in base al quale ricercare le tipologie di azione
	 * @return
	 */
	public List<String> loadActions(String dbName);
	
}
