package it.tredi.audit.console.entity;

/**
 * Classe utilizzata per semplificare il JSON di ritorno in caso di eccezione
 */
public class ExceptionResponse {

	private String message = "";
	private String description = "";
	private String stacktrace = "";
	private ErrorLevel level = ErrorLevel.ERROR;

	/**
	 * Costruttore
	 * @param message
	 */
	public ExceptionResponse(String message) {
		this(message, ErrorLevel.ERROR);
	}
	
	/**
	 * Costruttore
	 * @param message
	 * @param level
	 */
	public ExceptionResponse(String message, ErrorLevel level) {
		this(message, null, level, null);
	}
	
	/**
	 * Costruttore
	 * @param message
	 * @param description
	 * @param level
	 * @param stacktrace
	 */
	public ExceptionResponse(String message, String description, ErrorLevel level, String stacktrace) {
		this.message = message;
		this.description = description;
		this.level = level;
		this.stacktrace = stacktrace;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}

	public ErrorLevel getLevel() {
		return level;
	}

	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

}
