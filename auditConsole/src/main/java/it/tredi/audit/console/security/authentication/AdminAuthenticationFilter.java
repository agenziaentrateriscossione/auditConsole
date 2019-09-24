package it.tredi.audit.console.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.audit.console.AppConstants;
import it.tredi.audit.console.security.service.AuditUserDetailsService;
import it.tredi.spring.security.entity.SimpleRemoteUser;
import it.tredi.spring.security.jwt.JwtAuthenticationFilter;

/**
 * Filtro di autenticazione in base al json {"username":"<name>","password":"<password>"} con successiva impostazione del token JWT nell'header
 */
public class AdminAuthenticationFilter extends JwtAuthenticationFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminAuthenticationFilter.class);
	
	public AdminAuthenticationFilter(AuditUserDetailsService auditUserDetailsService, String defaultFilterProcessesUrl, AuthenticationManager authManager, String forwardUrl) {
		super(auditUserDetailsService, defaultFilterProcessesUrl, authManager, forwardUrl);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		Authentication authentication = null;
		try {
			SimpleRemoteUser user = new ObjectMapper().readValue(request.getInputStream(), SimpleRemoteUser.class);
			
			if (user != null && user.getUsername() != null && user.getUsername().equals(AppConstants.DEFAULT_ADMIN_USERNAME)) {
				if (user.getPassword() != null)
					user.setPassword(DigestUtils.md5Hex(user.getPassword()).toLowerCase());
				
				final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
				authentication = getAuthenticationManager().authenticate(loginToken);
			}
			else {
				throw new BadCredentialsException("Unable to authenticate user by username: " + ((user != null) ? user.getUsername() : null));
			}
		}
		catch(Exception e) {
			logger.error("Got exception on authentication... " + e.getMessage());
			throw e;
		}
		return authentication;
	}

}
