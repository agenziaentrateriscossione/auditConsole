package it.tredi.audit.console.test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devskiller.jfairy.Fairy;
import com.github.javafaker.Faker;

import it.tredi.audit.audit.validation.ChecksumValidation;
import it.tredi.audit.console.AuditConsoleApplication;
import it.tredi.audit.console.entity.Actor;
import it.tredi.audit.console.entity.AuditRecord;
import it.tredi.audit.console.repository.AuditRecordRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuditConsoleApplication.class)
public class FakeInsertTest {
	
	@Autowired
	private AuditRecordRepository auditRecordRepository;
	
	private final int NUM_ENTRIES = 500000;
	
	private Fairy fairy = Fairy.create(new Locale("it"));
	private Faker faker = new Faker(new Locale("it"));
	
	private final String XDOCWAYDOC_DBNAME = "xdocwaydoc";
	private final String ACL_DBNAME = "acl";
	
	private String[] archivi;
	private Map<String, String[]> tipiRecordMap;
	private Map<String, String[]> actionsMap;
	
	private final String FAKE_ACTION = "FAKE";
	
	/**
	 * Inizializzazione delle strutture dati necessarie all'inserimento Fake dei record di audit
	 */
	@Before
	public void initData() {
		this.archivi = new String[] { XDOCWAYDOC_DBNAME, ACL_DBNAME };
		
		this.tipiRecordMap = new HashMap<>();
		this.tipiRecordMap.put(XDOCWAYDOC_DBNAME, new String[] { "doc", "fascicolo", "raccoglitore" });
		this.tipiRecordMap.put(ACL_DBNAME, new String[] { "persona_esterna", "struttura_esterna", "persona_interna", "struttura_interna" });
		
		this.actionsMap = new HashMap<>();
		this.actionsMap.put(XDOCWAYDOC_DBNAME, new String[] { "LOGIN",
			    "VISUALIZZAZIONE",
			    "ANNOTAZIONE",
			    "ASSEGNAZIONE_CC",
			    "ASSEGNAZIONE_RPA",
			    "CREAZIONE_RECORD",
			    "RIMOZIONE_INTERVENTO",
			    "PROTOCOLLAZIONE",
			    "RIMOZIONE_CC",
			    "MODIFICA_CLASSIFICAZIONE",
			    "MODIFICA_RECORD",
			    "VISTO",
			    "DOWNLOAD_FILE",
			    "LOGOUT",
			    "PRENOTAZIONE_FILE",
			    "RILASCIO_FILE",
			    "AGGIORNAMENTO_FILE",
			    "ASSEGNAZIONE_INTERVENTO",
			    "ASSEGNAZIONE_OP",
			    "RIMOZIONE_OP",
			    "ANNULLAMENTO",
			    "ASSEGNAZIONE_CDS",
			    "RIMOZIONE_CDS",
			    "ASSEGNAZIONE_RPAM",
			    "CHIUSURA_FASCIOLO",
			    "APERTURA_FASCICOLO",
			    "APERTURA_RACCOGLITORE",
			    "RIGETTO_AL_PROTOCOLLISTA",
			    "TRASFORMAZIONE_IN_REPERTORIO",
			    "ASSEGNAZIONE_NUM_REPERTORIO",
			    "RIPRISTINO_TRASFORMAZIONE_IN_REP",
			    "ELIMINAZIONE_RECORD",
			    "RIPRISTINO_DA_CESTINO",
			    "SPOSTAMENTO_CESTINO",
			    "ACQUISIZIONE_IMMAGINI",
			    "SCARTO_ASSEGNAZIONE",
			    "AGGIUNTA_DOC_IN_FASCICOLO",
			    "RIMOZIONE_DOC_DA_FASCICOLO",
			    "AGGIUNTA_IN_RACCOGLITORE",
			    "RIMOZIONE_DA_RACCOGLITORE",
			    "INCARICATO_TENUTA_FASCICOLO",
			    "TRASFERIMENTO_FASCICOLO",
			    "MODIFICA_THESAURO",
			    "ADM_UPLOAD_FILE_RISORSA",
			    "ADM_MODIFICA_PROPERTY",
			    "CONTROLLO_DI_GESTIONE_E_REPORTISTICA",
			    "REGISTRO_GIORNALIERO_DI_PROTOCOLLO",
			    "STAMPA_GENERICA",
			    "STAMPA_CONTROLLO_DI_GESTIONE_E_REPORTISTICA",
			    "STAMPA_REGISTRO_PROTOCOLLO",
			    "STAMPA_REGISTRO_REPERTORI",
			    "STAMPA_REPERTORIO_FASCICOLI",
			    "STAMPA_REGISTRO_FATTURE",
			    "SEGNATURA" });
		this.actionsMap.put(ACL_DBNAME, new String[] { "MODIFICA_RECORD",
			    "ELIMINAZIONE_DELEGA",
			    "AGGIUNTA_DELEGA",
			    "MODIFICA_DELEGA",
			    "MODIFICA_DIRITTI_DI_AMMINISTRAZIONE",
			    "MODIFICA_DIRITTI_SPECIALI",
			    "LOGOUT",
			    "LOGIN",
			    "VISUALIZZAZIONE",
			    "CREAZIONE_RECORD" });
	}
	
	
	/**
	 * Inserimento massivo di record sul DB di audit per seguente test di performance sulle ricerche
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void massiveEntryTest() throws Exception {
		
		long startTime = System.currentTimeMillis();
		for(int i = 1; i <= NUM_ENTRIES; i++) {
			
			String archivio = fairy.baseProducer().randomElement(archivi);
			
			AuditRecord record = new AuditRecord();
			
			record.setArchivio(archivio);
			record.setTipoRecord(fairy.baseProducer().randomElement(tipiRecordMap.get(archivio)));
			record.setIdRecord(String.valueOf(faker.number().randomNumber()));
			
			Actor actor = new Actor();
			String codUser = String.valueOf(faker.number().randomNumber());
			String username = faker.internet().emailAddress();
			actor.setCodUser(codUser);
			actor.setUsername(username);
			actor.setActualCodUser(codUser);
			actor.setActualUsername(username);
			record.setUser(actor);
			
			record.setData(faker.date().birthday(0, 1));
			
			String[] actions = new String[2];
			actions[0] = fairy.baseProducer().randomElement(actionsMap.get(archivio));
			actions[1] = FAKE_ACTION;
			record.setTipoAzione(actions);
			
			// calcolo del checksum (si spera che sia funzionante)
			ChecksumValidation checksumValidation = new ChecksumValidation(record);
			record.setChecksum(checksumValidation.calculateChecksum());
			
			auditRecordRepository.save(record);
		}
		
		System.out.println("Massive entry done in " + (System.currentTimeMillis() - startTime) + "... entries = " + NUM_ENTRIES);
	}

}
