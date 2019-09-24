package it.tredi.audit.console.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller specifico per la Single Page Application. Tutte le richieste che non si riferiscono a controller dell'API devono essere redirette
 * sulla pagina di index del framework di Single Page Application
 * @author mbernardini
 */
@Controller
public class SinglePageApplicationController {
	
	private static final Logger logger = LoggerFactory.getLogger(SinglePageApplicationController.class);
	
	@Autowired 
	private HttpServletRequest req;

	
	/**
	 * Controller di base. Si occupa di ridirezionare alla pagina index.html (si presuppone che si tratti del caricamento di una pagina gestita 
	 * tramite Single Page Application) tutte le richieste ad esclusione di:
	 * 1) richieste gestite tramite specifici controlli dell'API (/api/**)
	 * 2) richieste relative a risorse statiche (contenente un punto nell'URI) 
	 */
    @RequestMapping(value = { "/", "/**/{[path:[^\\.]*}" })       
    public String redirect() {
    	// Redirect a index.html (Single Page Application basata su framework JS)
		if (logger.isDebugEnabled())
			logger.debug("Load Page '" + (req != null ? req.getRequestURI() : "") + "' by Single Page Application JS Framework");
		
		return "index";
    }
	
}
