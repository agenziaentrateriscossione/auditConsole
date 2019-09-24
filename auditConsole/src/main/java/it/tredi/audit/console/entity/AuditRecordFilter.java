package it.tredi.audit.console.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Filtro in base al quale filtrare i risultati di una ricerca sui record di audit
 * @author mbernardini
 */
public class AuditRecordFilter {

	/**
	 * Filtro su nome del db tracciato
	 */
	private String archivio;
	
	/**
	 * Filtro su tipologia del record (documento, fascicolo, persona_interna, ecc.)
	 */
	private String tipoRecord;
	
	/**
	 * Filtro su identificativo del record
	 */
	private String idRecord;
	
	/**
	 * Filtro su tipologia di attivita' svolta
	 */
	private String[] tipoAzione;
	
	/**
	 * Range inferiore per il filtro su data e ora di registrazione dell'audit
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date from;
	
	/**
	 * Range superiore per il filtro su data e ora di registrazione dell'audit
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date to;
	
	/**
	 * Filtro su username dell'utente reale che ha svolto l'attivita'
	 */
	private String username;
	
	/**
	 * Filtro su matricola/codice dell'utente reale che ha svolto l'attivita'
	 */
	private String codUser;

	/**
	 * Getter del filtro su nome del db tracciato
	 * @return
	 */
	public String getArchivio() {
		return archivio;
	}

	/**
	 * Setter del filtro su nome del db tracciato
	 * @param archivio
	 */
	public void setArchivio(String archivio) {
		this.archivio = archivio;
	}

	/**
	 * Getter del filtro su tipologia del record
	 * @return
	 */
	public String getTipoRecord() {
		return tipoRecord;
	}

	/**
	 * Setter del filtro su tipologia del record
	 * @param tipoRecord
	 */
	public void setTipoRecord(String tipoRecord) {
		this.tipoRecord = tipoRecord;
	}

	/**
	 * Getter del filtro su identificativo del record
	 * @return
	 */
	public String getIdRecord() {
		return idRecord;
	}

	/**
	 * Setter del filtro su identificativo del record
	 * @param id
	 */
	public void setIdRecord(String id) {
		this.idRecord = id;
	}

	/**
	 * Getter del filtro su tipologia di attivita' svolta
	 * @return
	 */
	public String[] getTipoAzione() {
		return tipoAzione;
	}

	/**
	 * Setter del filtro su tipologia di attivita' svolta
	 * @param tipoAzione
	 */
	public void setTipoAzione(String[] tipoAzione) {
		this.tipoAzione = tipoAzione;
	}

	/**
	 * Getter del range inferiore per il filtro su data e ora di registrazione dell'audit
	 * @return
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * Setter del range inferiore per il filtro su data e ora di registrazione dell'audit
	 * @param from
	 */
	public void setFrom(Date from) {
		this.from = from;
	}

	/**
	 * Getter del range superiore per il filtro su data e ora di registrazione dell'audit
	 * @return
	 */
	public Date getTo() {
		return to;
	}

	/**
	 * Setter del range superiore per il filtro su data e ora di registrazione dell'audit
	 * @param to
	 */
	public void setTo(Date to) {
		this.to = to;
	}

	/**
	 * Getter del filtro su username dell'utente reale che ha svolto l'attivita'
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Setter del filtro su username dell'utente reale che ha svolto l'attivita'
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Getter del filtro su matricola/codice dell'utente reale che ha svolto l'attivita'
	 * @return
	 */
	public String getCodUser() {
		return codUser;
	}

	/**
	 * Setter del filtro su matricola/codice dell'utente reale che ha svolto l'attivita'
	 * @param codUser
	 */
	public void setCodUser(String codUser) {
		this.codUser = codUser;
	}
	
}
