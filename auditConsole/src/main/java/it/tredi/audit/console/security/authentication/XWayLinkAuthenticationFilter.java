package it.tredi.audit.console.security.authentication;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import it.tredi.audit.audit.validation.AuthTokenValidation;
import it.tredi.audit.audit.validation.entity.AuthTokenData;
import it.tredi.audit.audit.validation.exception.InvalidTokenException;
import it.tredi.audit.console.security.service.AuditUserDetailsService;
import it.tredi.spring.security.entity.SimpleRemoteUser;
import it.tredi.spring.security.jwt.JwtAuthenticationFilter;

/**
 * Filtro di riconoscimento dell'utente in base ad un URL prodotto da un applicativo basato su eXtraWay
 */
public class XWayLinkAuthenticationFilter extends JwtAuthenticationFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(XWayLinkAuthenticationFilter.class);
	
	private final String AUTH_TOKEN_PARAMETER = "authkey";
	
	@Value("${xway.token.validateAddress:false}")
	private boolean validateAddress;
	
	@Value("${xway.3diws.url}")
	private String trediWsUrl;
	
	@Value("${xway.host}")
	private String xwayHost;
	
	@Value("${xway.port}")
	private String xwayPort;
	
	@Value("${xway.3diws.auth.username}")
	private String trediWsUsername;
	
	@Value("${xway.3diws.auth.password}")
	private String trediWsPassword;
	
	@Value("${xway.acl.auditConsole.right}")
	private String auditConsoleRight;
	
	public XWayLinkAuthenticationFilter(AuditUserDetailsService auditUserDetailsService, String defaultFilterProcessesUrl, AuthenticationManager authManager, String forwardUrl) {
		super(auditUserDetailsService, defaultFilterProcessesUrl, authManager, forwardUrl);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		Authentication authentication = null;
		
		String authToken = request.getParameter(AUTH_TOKEN_PARAMETER);
		try {
			if (authToken == null || authToken.isEmpty())
				throw new Exception("Parameter " + AUTH_TOKEN_PARAMETER + " not found! Impossible to login to Audit Console!");
			
			// Estrazione dei dati dell'utente da token di autenticazione
			AuthTokenData tokenData = AuthTokenValidation.extractTokenData(authToken);
			if (tokenData != null) {
				if (logger.isDebugEnabled())
					logger.debug("XWayLinkAuthenticationFilter.attemptAuthentication(): " + tokenData.getUsername() + " recovered from token " + authToken);
				
				// Verifica dell'ip di provenienza della richiesta (se non disabilitato dalle properties della console)
				if (!validateAddress || (tokenData.getIpAddress() != null && request.getRemoteAddr().equals(tokenData.getIpAddress()))) {
					
					// L'accesso e' consentito se non deve essere fatto alcun controllo sul diritto in acl (property 'xway.acl.auditConsole.right'
					// non valorizzata) o se l'utente specificato possiede il diritto di accesso alla console di audit
					if (auditConsoleRight == null || auditConsoleRight.isEmpty() || isUserAuthorized(tokenData.getUsername(), tokenData.getMatricola())) {
					
						// Istanzio l'oggetto RemoteUser in base ai parametri estratti dal token di autenticazione
						SimpleRemoteUser user = new SimpleRemoteUser();
						user.setUsername(tokenData.getUsername());
						authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
					}
				}
			}
		}
		catch (InvalidTokenException ite) {
			logger.error("Got exception on authentication... Unable to extract token data... " + ite.getMessage(), ite);
			throw new BadCredentialsException("Unable to authenticate user by " + AUTH_TOKEN_PARAMETER + " " + authToken, ite);
		}
		catch (Exception e) {
			logger.error("Got exception on authentication... " + e.getMessage());
			throw new AuthenticationServiceException(e.getMessage(), e);
		}
		return authentication;
	}
	
	/**
	 * Verifica (tramite chiamata ai 3diws) se l'utente specificato tramite token di autenticazione e' effettivamente autorizzato 
	 * ad accedere alla console di audit
	 * @param username Username dell'utente da autenticare
	 * @param matricola Matricola dell'utente da autenticare
	 * @return true se l'utente e' autorizzato ad accedere alla console di audit, false altrimenti
	 */
	private boolean isUserAuthorized(String username, String matricola) {
		boolean authorized = false;
		try {
			if (trediWsUrl == null || trediWsUrl.isEmpty() || auditConsoleRight == null || auditConsoleRight.isEmpty()) {
				// Se l'url di accesso ai 3diws o il codice del diritto di accesso alla console di audit non sono specificati
				// a livello di application.properties, si considera sempre autorizzato l'utente corrente (impossibile verificare
				// il diritto)
				authorized = true;
			}
			else {
				// Chiamata ai 3diWS per verifica del diritto dell'utente
				Service service = new Service();
				service.setMaintainSession(true);
				
				Call call = (Call) service.createCall();
				call.setTargetEndpointAddress(new URL(trediWsUrl));
				
				// Eventuali parametri di autenticazione
				if (trediWsUsername != null && !trediWsUsername.isEmpty())
					call.setUsername(trediWsUsername);
				if (trediWsPassword != null && !trediWsPassword.isEmpty())
					call.setPassword(trediWsPassword);
				
				// Init a 3diws (inizializzazione dell'utente)
				call.setOperationName(new QName("Acl", "init"));

		        Object[] init_params = new Object[5];
		        
		        init_params[0] = (xwayHost != null) ? xwayHost : ""; // host
		        init_params[1] = (xwayPort != null) ? xwayPort : ""; // port
		        init_params[2] = username; // user
		        init_params[3] = matricola; // pnumber
		        init_params[4] = ""; // password
		        call.invoke(init_params);
		        
		        // Chiamata a checkRight (verifica del diritto di accesso alla console di audit)
		        call.setOperationName(new QName("Acl", "checkRight"));
				
				Object[] query_params = new Object[1];
				query_params[0] = auditConsoleRight; // code
				
				Boolean value = (Boolean) call.invoke(query_params);
				if (value != null)
					authorized = value.booleanValue();
			}
		}
		catch (Exception e) {
			logger.error("Unable to load user from xway db... " + e.getMessage(), e);
		}
		return authorized;
	}

}
