package it.tredi.audit.console.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "audit.export")
public class ExportProperties {
	
	/**
	 * Dimensione del pool di thread di esportazione record (numero massimo di thread concorrenti supportati)
	 */
	private int maxConcurrentJobs;
	
	/**
	 * Dimensione della pagina di risultati in fase di esportazione di record
	 */
	private int pageSize;
	
	/**
	 * Numero massimo di record risultanti dalla ricerca per i quali e' supportato l'export CSV. Il valore 0 indica un resultset di qualsiasi
	 * dimensione
	 */
	private int maxSize;
	
	/**
	 * Numero massimo di record risultanti dalla ricerca per i quali e' supportata la validazione del checksum. Il valore 0 indica la disattivazione
	 * della funzione
	 */
	private int validationMaxSize;
	
	@PostConstruct
    public void init() {
		if (maxConcurrentJobs < 0)
			maxConcurrentJobs = 0;
		if (this.pageSize == 0)
			this.pageSize = 250;
		if (this.maxSize < 0)
			this.maxSize = 0;
		if (this.validationMaxSize < 0)
			this.validationMaxSize = 0;
    }

	/**
	 * Ritorna la dimensione del thread pool
	 * @return
	 */
	public int getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}

	/**
	 * Ritorna la dimensione di una pagina di risultati di una ricerca per la fase di esportazione CSV di record
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Ritorna la dimensione massima del resultset al di sopra della quale l'esportazione non deve essere possibile
	 * @return
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Ritorna la dimensione massima del resultset al di sopra della quale l'esportazione non viene svolta la validazione dei record esportati
	 * @return
	 */
	public int getValidationMaxSize() {
		return validationMaxSize;
	}

	/**
	 * Imposta la dimensione del thread pool
	 * @param maxConcurrentJobs
	 */
	public void setMaxConcurrentJobs(int maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}

	/**
	 * Imposta la dimensione di una pagina di risultati di una ricerca per la fase di esportazione CSV di record
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Imposta la dimensione massima del resultset al di sopra della quale l'esportazione non deve essere possibile
	 * @param maxSize
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * Imposta la dimensione massima del resultset al di sopra della quale l'esportazione non viene svolta la validazione dei record esportati
	 * @param validationMaxSize
	 */
	public void setValidationMaxSize(int validationMaxSize) {
		this.validationMaxSize = validationMaxSize;
	}
	
}
