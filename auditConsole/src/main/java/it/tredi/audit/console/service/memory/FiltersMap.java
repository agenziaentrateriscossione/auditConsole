package it.tredi.audit.console.service.memory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton di mantenimento in memoria della mappa delle progressbar
 */
public class FiltersMap {
	
	private static final Logger logger = LoggerFactory.getLogger(FiltersMap.class);
	
	private Map<String, List<String>> map;
	
	// Singleton
	private static FiltersMap instance = null;

	/**
	 * Istanzia la mappa di operazioni asincrone se non gia' istanziata in precedenza
	 * @return
	 */
	public static FiltersMap getInstance() {
		if (instance == null) {
			synchronized (FiltersMap.class) {
				if (instance == null) {
					if (logger.isInfoEnabled())
						logger.info("FiltersMap instance is null... create one");
					instance = new FiltersMap();
				}
			}
		}

		return instance;
	}
	
	/**
	 * Costruttore privato
	 */
	private FiltersMap() {
		this.map = new ConcurrentHashMap<>();
	}
	
	/**
	 * Costruzione della chiave per il mantenimento in memoria della lista
	 * @param db
	 * @param listType
	 * @return
	 */
	private String _getKey(String db, FilterListType listType) {
		return (db != null && !db.isEmpty() && listType != null) ? db + "_" + listType.toString() : null;
	}
	
	/**
	 * Verifica se la lista identificata dai parametri risulta gia' in memoria o meno
	 * @param db Nome dell'archivio
	 * @param listType Tipologia di lista
	 * @return true se la lista e' registrata in memoria, false altrimenti
	 */
	public boolean containsList(String db, FilterListType listType) {
		return (_getKey(db, listType) != null) ? this.map.containsKey(_getKey(db, listType)) : false;
	}
	
	/**
	 * Aggiunta di una lista alla mappa in memoria
	 * @param db Nome dell'archivio
	 * @param listType Tipologia di lista
	 * @param list Lista da settare in memoria
	 */
	public void addList(String db, FilterListType listType, List<String> list) {
		String key = _getKey(db, listType);
		if (key != null)
			this.map.put(key, list);
	}
	
	/**
	 * Recupero di una lista dalla memoria
	 * @param db Nome dell'archivio 
	 * @param listType Tipologia di lista da caricare
	 * @return
	 */
	public List<String> getList(String db, FilterListType listType) {
		return (containsList(db, listType)) ? this.map.get(_getKey(db, listType)) : null;
	}
	
	/**
	 * Eliminazione di una progressbar dalla mappa in memoria
	 * @param id Identificativo della progressbar da eliminare
	 */
	public void removeList(String db, FilterListType listType) {
		if (containsList(db, listType)) {
			this.map.remove(_getKey(db, listType));
		}
	}
	
}
