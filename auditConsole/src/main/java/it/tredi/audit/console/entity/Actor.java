package it.tredi.audit.console.entity;

import org.springframework.data.mongodb.core.index.Indexed;

import it.tredi.audit.audit.entity.base.IActor;

/**
 * Identifica l'operatore che ha compiuto un'attivita' registrata sul sisema di audit
 * @author mbernardini
 */
public class Actor implements IActor {

	/**
	 * Username dell'utente che risulta come esecutore dell'attivita'
	 */
	@Indexed(name = "username_idx")
	private String username = null;
	
	/**
	 * Matricola/Codice dell'utente che risulta come esecutore dell'attivita'
	 */
	@Indexed(name = "codUser_idx")
	private String codUser = null;
	
	/**
	 * Indirizzo IP di provenienza della richiesta
	 */
	private String ipAddress = null;
	
	/**
	 * Username dell'utente che ha svolto l'attivita' come delegato (opzionale, solo per azioni svolte in delega)
	 */
	@Indexed(name = "delegaUsername_idx")
	private String delegaUsername = null;
	
	/**
	 * Matricola/Codice dell'utente che ha svolto l'attivita' come delegato (opzionale, solo per azioni svolte in delega)
	 */
	@Indexed(name = "delegaCodUser_idx")
	private String delegaCodUser = null;
	
	/**
	 * Username del reale del reale utente che ha svolto l'attivita'
	 */
	@Indexed(name = "actualUsername_idx")
	private String actualUsername = null;
	
	/**
	 * Matricola/Codice del reale utente che ha svolto l'attivita'
	 */
	@Indexed(name = "actualCodUser_idx")
	private String actualCodUser = null;

	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * Setter dello username dell'utente che risulta come esecutore dell'attivita'
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getCodUser() {
		return codUser;
	}

	/**
	 * Setter del codice dell'utente che risulta come esecutore dell'attivita'
	 * @param codUser
	 */
	public void setCodUser(String codUser) {
		this.codUser = codUser;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Setter dell'indirizzo IP di provenienza della richiesta
	 * @param ipAddress
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getDelegaUsername() {
		return this.delegaUsername;
	}
	
	/**
	 * Setter dello username dell'utente che ha svolto l'attivita' come delegato (opzionale, solo per azioni svolte in delega)
	 * @param username
	 */
	public void setDelegaUsername(String username) {
		this.delegaUsername = username;
	}

	@Override
	public String getDelegaCodUser() {
		return this.delegaCodUser;
	}
	
	/**
	 * Setter del codice dell'utente che ha svolto l'attivita' come delegato (opzionale, solo per azioni svolte in delega)
	 * @param codUser
	 */
	public void setDelegaCodUser(String codUser) {
		this.delegaCodUser = codUser;
	}

	@Override
	public String getActualUsername() {
		return this.actualUsername;
	}
	
	/**
	 * Setter dello username del reale del reale utente che ha svolto l'attivita'
	 * @param username
	 */
	public void setActualUsername(String username) {
		this.actualUsername = username;
	}

	@Override
	public String getActualCodUser() {
		return this.actualCodUser;
	}

	/**
	 * Setter del codice del reale del reale utente che ha svolto l'attivita'
	 * @param codUser
	 */
	public void setActualCodUser(String codUser) {
		this.actualCodUser = codUser;
	}
	
}
