package it.tredi.audit.console.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import joptsimple.internal.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

/**
 * Singleton per l'estrapolazione del JSON di configurazione dei mapping degli URL dell'applicazione su cui viene fatto
 * audit; serve per poter creare link dai record di audit ai relativi record nell'applicazione.
 */
@Component
public class ApplicationUrlMapper {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationUrlMapper.class);

	private JsonNode appMapperNode;

	private ApplicationUrlMapper(@Value("${audit.console.configuration-folder}") String confFolder) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			// schema
			File schemaFile = ResourceUtils.getFile("classpath:app-url-mapper.schema.json");
			JsonNode schemaNode = mapper.readTree(schemaFile);
			JsonSchemaFactory schemafactory = JsonSchemaFactory.byDefault();
			JsonSchema schema = schemafactory.getJsonSchema(schemaNode);

			// conf
			File confFile;
			if (Strings.isNullOrEmpty(confFolder))
				confFile = ResourceUtils.getFile("classpath:app-url-mapper.json");
			else
				confFile = ResourceUtils.getFile("file:"+confFolder+"/app-url-mapper.json");
			this.appMapperNode = mapper.readTree(confFile);

			// validazione
			ProcessingReport validationReport = schema.validate(this.appMapperNode);
			if (validationReport.isSuccess())
				logger.info(validationReport.toString());
			else {
				this.appMapperNode = null;
				logger.warn(validationReport.toString());
			}

		} catch (IOException ex) {
			logger.error("Got exception getting app-url-mapper InputStream... " + ex.getMessage(), ex);
		} catch (ProcessingException ex) {
			logger.error("Got exception processing JSON schema... " + ex.getMessage(), ex);
		}

	}

	public JsonNode getJsonAppUrlMap() {
		return this.appMapperNode;
	}

}
