package it.tredi.audit.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

// N.B.: La versione con l'exclude e' necessaria in caso di utilizzo di spring-data-elasticsearch in combinazione con con spring-data-jest
@SpringBootApplication

// N.B.: In caso di utilizzo combinato di repository differenti occorre definirli/abilitarli su package differenti per evitare errori in fase di avvio di Spring
@EnableMongoRepositories("it.tredi.audit.console.repository")
public class AuditConsoleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditConsoleApplication.class, args);
	}
	
}
