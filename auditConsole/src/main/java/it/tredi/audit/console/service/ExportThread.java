package it.tredi.audit.console.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import it.tredi.audit.audit.validation.ChecksumValidation;
import it.tredi.audit.console.config.ExportProperties;
import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.entity.AuditRecordFilter;
import it.tredi.audit.console.repository.AuditRecordRepository;
import it.tredi.spring.progressbar.entity.ActivityResult;
import it.tredi.spring.progressbar.entity.Progressbar;
import it.tredi.spring.progressbar.service.ProgressbarService;

/**
 * Servizio di esportazione dei record di audit tramite thread asincrono
 */
@Component
public class ExportThread {
	
	private static final Logger logger = LoggerFactory.getLogger(ExportThread.class);

	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private ProgressbarService progressbarService;
	
	@Autowired
	private AuditRecordRepository auditRecordRepository;
	
	@Autowired
	private ExportProperties exportProperties;
	
	private boolean validate = false;
	
	/**
	 * Avvio della procedura di esportazione dei record tramite thread separato
	 * @param filter Filtro di ricerca attraverso il quale recuperare i record da esportare
	 * @return Informazioni relativi al processo (identificativo associato alla progressbar)
	 */
	public Progressbar startExport(AuditRecordFilter filter) throws Exception {
		int exportMaxSize = this.exportProperties.getMaxSize(); 
		int validationMaxSize = this.exportProperties.getValidationMaxSize();
		
		long tot = auditRecordRepository.count(filter);
		if (tot == 0) 
			throw new Exception("No records to export");
		
		// Controllo della dimensione massima dei record esportabili
		if (exportMaxSize > 0 && tot > exportMaxSize)
			throw new Exception("Too many records: [" + tot + " > " + exportMaxSize + "]");
		
		if (logger.isInfoEnabled())
			logger.info("ExportThread.startExport() -> init progressbar [tot = " + tot + "]");
		
		Progressbar progressbar = progressbarService.init(tot);
		
		// La validazione dei record (verifica integrita') deve essere fatta solo se abilitata o se il numero di record
		// individuati dalla ricerca e' inferiore al massimo previsto per la validazione
		validate = false;
		if (validationMaxSize > 0 && tot <= validationMaxSize)
			validate = true;
		
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				CSVPrinter csv = null;
				try {
					progressbarService.start(progressbar.getId());
					
					Path file = Files.createTempFile(_getTempDir(), "aud", ".csv");
					BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
					
					List<String> header = new ArrayList<>();
					header.add("ID AUDIT");
					if (validate)
						header.add("VALIDO");
					header.add("ARCHIVIO");
					header.add("ID RECORD");
					header.add("TIPO RECORD");
					header.add("DATA");
					header.add("AZIONE");
					header.add("COD UTENTE");
					header.add("USERNAME");
					
					// TODO aggiunta di eventuali altri dati all'export
					//header.add("COD DELEGA");
					//header.add("USERNAME DELEGA");
					
					CSVFormat format = CSVFormat.DEFAULT.withHeader(header.toArray(new String[] {}));
					csv = new CSVPrinter(writer, format);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					Page<AuditRecord> results = null;
					int currentPage = 0;
					boolean hasNext = true;
					while (hasNext) {
						if (logger.isDebugEnabled())
							logger.debug("ExportThread.taskExecutor.execute() -> load page " + currentPage);
						
						results = auditRecordRepository.search(filter, currentPage, exportProperties.getPageSize(), Sort.by(Sort.Direction.DESC, AuditRecord.FIELD_DATA));
						if (results != null) {
							// ciclo su tutti i record di audit della pagina (inserimento su file CSV)
							for (AuditRecord auditRecord : results.getContent()) {
								
								// Non ha senso gestire eventuali eccezioni riscontrate sull'elaborazione di un singolo record. In caso di esportazione di dati 
								// settiamo come fallita l'intera lavorazione di export.
								
								List<Object> data = new ArrayList<>();
								data.add(auditRecord.getId());
								if (validate) {
									ChecksumValidation checksumValidation = new ChecksumValidation(auditRecord);
									boolean isValid = true;
									if (auditRecord.getChecksum() != null && !auditRecord.getChecksum().isEmpty())
										isValid = checksumValidation.isValid(auditRecord.getChecksum());
									data.add((isValid) ? "TRUE" : "FALSE");
								}
								data.add(auditRecord.getArchivio());
								data.add(auditRecord.getIdRecord());
								data.add(auditRecord.getTipoRecord());
								data.add(sdf.format(auditRecord.getData()));
								data.add((auditRecord.getTipoAzione() != null) ? String.join(", ", auditRecord.getTipoAzione()) : "");
								data.add(auditRecord.getUser().getCodUser());
								data.add(auditRecord.getUser().getUsername());
								
								// TODO aggiunta di eventuali altri dati all'export
								//data.add(auditRecord.getUser().getDelegaCodUser());
								//data.add(auditRecord.getUser().getDelegaUsername());
								
								csv.printRecord(data);
								
								progressbarService.progress(progressbar.getId(), ActivityResult.DONE);
							}
							
							if (results.isLast())
								hasNext = false;
						}
						else {
							hasNext = false;
						}
						currentPage++;
					}
					
					csv.close();
					
					progressbarService.endsSuccessfully(progressbar.getId(), file.toFile());
				}
				catch(Exception e) {
					logger.error("ExportThread.taskExecutor.execute() -> got exception " + e.getMessage(), e);
					
					if (csv != null) {
						try {
							csv.close();
						} catch (IOException e1) {
							logger.error("ExportThread.taskExecutor.execute() -> unable to close csv file " + e1.getMessage(), e1);
						}
					}
					
					// progressbar terminata con errore (registrazione del fallimento)
					progressbarService.endsWithFailure(progressbar.getId(), e);
				}
			}
		});
		
		return progressbar;
	}
	
	/**
	 * Restituisce la directory temporanea di gestione dei file di esportazione CSV
	 * @return
	 * @throws IOException
	 */
	private Path _getTempDir() throws IOException {
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "auditExport");
		if (!Files.exists(tempDir))
			Files.createDirectories(tempDir);
		return tempDir;
	}

}
