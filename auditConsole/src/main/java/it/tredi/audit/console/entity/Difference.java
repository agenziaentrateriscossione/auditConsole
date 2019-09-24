package it.tredi.audit.console.entity;

import java.util.Map;

import it.tredi.audit.audit.entity.base.IAuditDifference;

/**
 * Identifica una modifica apportata ad un record da parte di un operatore
 * @author mbernardini
 */
public class Difference implements IAuditDifference {

	/**
	 * Riferimento al campo aggiornato (es. xpath in caso di documenti xml)
	 */
	private String field;
	
	/**
	 * Mappa contenente le eventuali chiavi necessarie all'identificazione del campo all'interno del record (es. identificazione
	 * di una singola istanza all'interno di un campo ripetibile)
	 */
	private Map<String, String> keys;
	
	/**
	 * Valore registrato sul campo prima dell'intervento
	 */
	private String before;
	
	/**
	 * Valore registrato sul campo dopo l'intervento
	 */
	private String after;

	@Override
	public String getField() {
		return field;
	}

	/**
	 * Setter del riferimento al campo aggiornato (es. xpath in caso di documenti xml)
	 * @param field
	 */
	public void setField(String field) {
		this.field = field;
	}

	@Override
	public Map<String, String> getKeys() {
		return keys;
	}

	/**
	 * Assegna la mappa di chiavi necessarie all'identificazione dal campo. Se la mappa risulta gia' valorizzata sovrascrivera' l'intera mappa.
	 * @param keys Mappa di chiavi necessarie all'identificazione del campo
	 */
	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
	}

	@Override
	public String getBefore() {
		return before;
	}

	/**
	 * Imposta una stringa come valore registrato sul campo prima dell'intervento. Potrebbe trattarsi di una stringa semplice come di una
	 * struttura json
	 * @param before
	 */
	public void setBefore(String before) {
		this.before = before;
	}

	@Override
	public String getAfter() {
		return after;
	}

	/**
	 * Imposta una stringa come valore registrato sul campo dopo l'intervento. Potrebbe trattarsi di una stringa semplice come di una
	 * struttura json
	 * @param after
	 */
	public void setAfter(String after) {
		this.after = after;
	}
	
}
