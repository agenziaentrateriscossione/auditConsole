package it.tredi.audit.console.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import it.tredi.audit.console.AuditConsoleApplication;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuditConsoleApplication.class)
@Ignore
public class AppMapsTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void getConfResource() throws IOException {

		// given
		File confFile = ResourceUtils.getFile("classpath:good-app-url-mapper.json");

		// when
		JsonNode node = mapper.readTree(confFile);

		// then
		Assert.assertEquals("www.example.com", node.at("/maps/1/url").asText());

	}

	/**
	 * Test per verificare la validazione attraverso il JSON Schema (draft-v4) del mapping url dai record dell'audit alla
	 * pagina di visualizzazione di quelli dell'applicazione auditata
	 * @throws IOException
	 * @throws ProcessingException
	 */
	@Test
	public void testSchemaValidation() throws IOException, ProcessingException {

		// given
		File schemaFile = ResourceUtils.getFile("classpath:app-url-mapper.schema.json");
		JsonNode schemaNode = mapper.readTree(schemaFile);
		JsonSchemaFactory schemafactory = JsonSchemaFactory.byDefault();
		JsonSchema schema = schemafactory.getJsonSchema(schemaNode);

		// when - good
		File goodConfFile = ResourceUtils.getFile("classpath:good-app-url-mapper.json");
		JsonNode goodNode = mapper.readTree(goodConfFile);
		ProcessingReport goodReport = schema.validate(goodNode);
		// when - bad
		File badConfFile = ResourceUtils.getFile("classpath:bad-app-url-mapper.json");
		JsonNode badNode = mapper.readTree(badConfFile);
		ProcessingReport badReport = schema.validate(badNode);

		// then - good
		System.out.println("Testing schema validation success...");
		Assert.assertTrue(goodReport.isSuccess());
		System.out.println(goodReport);
		// then - bad
		System.out.println("Testing schema validation failure...");
		Assert.assertFalse(badReport.isSuccess());
		System.out.println(badReport);

	}
}
