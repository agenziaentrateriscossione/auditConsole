package it.tredi.audit.console.exception;

public class NotFoundAuditException extends Exception {

	private static final long serialVersionUID = 1342987277008817961L;
	
	/**
	 * Identificativo del record di audit richiesto (e non trovato)
	 */
	private String id;

	public NotFoundAuditException(String message) {
        this(message, null);
    }
	
	public NotFoundAuditException(String message, String id) {
        super(message);
        this.id = id;
    }
	
    public NotFoundAuditException(String message, String id, Throwable cause) {
        super(message, cause);
        this.id = id;
    }

    /**
     * Ritorna l'identificativo del record di audit non trovato
     * @return
     */
    public String getId() {
    	return this.id;
    }
	
}
