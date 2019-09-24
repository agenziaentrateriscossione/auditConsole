package it.tredi.audit.console.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.tredi.audit.audit.entity.base.IAuditRecord;

/**
 * Record di audit applicativo registrato sull'archivio MongoDB
 * @author mbernardini
 */
@JsonInclude(value = Include.NON_NULL)
@Document(collection = AuditRecord.COLLECTION_NAME)
public class AuditRecord implements IAuditRecord {

	public static final String COLLECTION_NAME = "audit";

	public static final String FIELD_ARCHIVIO = "archivio";
	public static final String FIELD_TIPORECORD = "tipoRecord";
	public static final String FIELD_IDRECORD = "idRecord";
	public static final String FIELD_TIPOAZIONE = "tipoAzione";
	public static final String FIELD_DATA = "data";
	
	public static final String FIELD_USERNAME = "user.username";
	public static final String FIELD_DELEGA_USERNAME = "user.delegaUsername";
	public static final String FIELD_ACTUAL_USERNAME = "user.actualUsername";
	public static final String FIELD_CODUSER = "user.codUser";
	public static final String FIELD_DELEGA_CODUSER = "user.delegaCodUser";
	public static final String FIELD_ACTUAL_CODUSER = "user.actualCodUser";
	
	public static final String FIELD_CHANGES = "changes";
	public static final String FIELD_EXTRAINFO = "extraInfo";
	public static final String FIELD_CHECKSUM = "checksum";
	
	@Id 
	private String id = null;
	
	/**
	 * Nome del db tracciato
	 */
	@Indexed(name = "archivio_idx")
	@NotNull
	private String archivio = null;

	/**
	 * Identificativo del record
	 */
	@Indexed(name = "idRecord_idx")
	private String idRecord = null;
	
	/**
	 * Tipologia del record (documento, fascicolo, persona_interna, ecc.)
	 */
	@Indexed(name = "tipoRecord_idx")
	private String tipoRecord = null;
	
	/**
	 * Data e ora della registrazione sull'audit
	 */
	@Indexed(name = "data_idx")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date data = null;
	
	/**
	 * Tipologia di attivita' svolta
	 */
	@Indexed(name = "tipoAzione_idx")
	private String[] tipoAzione;

	/**
	 * Informazioni relative all'operatore che ha svolto l'attivita' registrata sull'audit
	 */
	private Actor user;
	
	/**
	 * Eventuale elenco di modifiche al record apportate da parte dell'operatore
	 */
	private List<Difference> changes;
	
	/**
	 * Informazioni supplementari sull'attività svolta
	 */
	private Map<String, Object> extraInfo;
	
	/**
	 * Codice di controllo calcolato sui dati riportati sull'audit. Necessario per poter accertare che il dato non e' stato alterato una volta
	 * salvato su MongoDB
	 */
	private String checksum;
	
	/**
	 * Corrisponde all'esito della validazione del checksum in base ai dati contenuti nel record di audit. Vale true se il codice di 
	 * controllo impostato sul record risulta validato, false altrimenti
	 */
	private Boolean validChecksum;
	
	/**
	 * Ritorna true se l'oggetto corrente e' nuovo, false altrimenti
	 * @return
	 */
	@JsonIgnore
	public boolean isNew() {
		return (id == null);
	}
	
	/**
	 * Getter dell'identificativo del record di audit
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter dell'identificativo del record di audit
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getArchivio() {
		return archivio;
	}

	/**
	 * Imposta l'archivio al quale fa riferimento l'audic corrente
	 * @param archivio
	 */
	public void setArchivio(String archivio) {
		this.archivio = archivio;
	}

	@Override
	public String getIdRecord() {
		return idRecord;
	}

	/**
	 * Imposta l'identificativo del record al quale l'audit fa riferimento
	 * @param nrecord
	 */
	public void setIdRecord(String id) {
		this.idRecord = id;
	}

	@Override
	public String getTipoRecord() {
		return tipoRecord;
	}

	/**
	 * Imposta la tipologia del record al quale l'audit fa riferimento
	 * @param tipoRecord
	 */
	public void setTipoRecord(String tipoRecord) {
		this.tipoRecord = tipoRecord;
	}

	@Override
	public Date getData() {
		return data;
	}

	/**
	 * Imposta la data e ora di registrazione dell'audit
	 * @param data
	 */
	public void setData(Date data) {
		this.data = data;
	}

	@Override
	public String[] getTipoAzione() {
		return tipoAzione;
	}

	/**
	 * Imposta le tipologie di azioni svolte dall'utente
	 * @param tipoAzione Elenco che identifica le tipologie di azioni svolte
	 */
	public void setTipoAzione(String[] tipoAzione) {
		this.tipoAzione = tipoAzione;
	}

	@Override
	public Actor getUser() {
		return user;
	}

	/**
	 * Imposta tutte le informazioni relative all'utente tracciato
	 * @param user
	 */
	public void setUser(Actor user) {
		this.user = user;
	}

	@Override
	public List<Difference> getChanges() {
		return changes;
	}

	/**
	 * Setter della lista di modifiche apportate dall'utente al record
	 * @param changes
	 */
	public void setChanges(List<Difference> changes) {
		this.changes = changes;
	}

	@Override
	public Map<String, Object> getExtraInfo() {
		return extraInfo;
	}

	/**
	 * Imposta le informazioni supplementari sull'attivita' svolta
	 * @param extraInfo
	 */
	public void setExtraInfo(Map<String, Object> extraInfo) {
		this.extraInfo = extraInfo;
	}

	/**
	 * Ritorna il codice di controllo calcolato sui dati riportati sull'audit
	 * @return
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * Imposta il codice di controllo calcolato sui dati riportati sull'audit
	 * @param checksum
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * Getter dell'esito della validazione del checksum
	 * @return
	 */
	public Boolean getValidChecksum() {
		return validChecksum;
	}

	/**
	 * Setter dell'esito della validazione del checksum
	 * @param validChecksum
	 */
	public void setValidChecksum(Boolean validChecksum) {
		this.validChecksum = validChecksum;
	}
	
}
