package it.tredi.audit.console.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.tredi.audit.console.AppConstants;
import it.tredi.spring.security.entity.SimpleRemoteUser;

@Service
public class AuditUserDetailsService implements UserDetailsService {

	@Value("${audit.admin.password}")
	private String auditPassword;
	
	@Override
	public SimpleRemoteUser loadUserByUsername(String username) throws UsernameNotFoundException {
		SimpleRemoteUser remoteUser = new SimpleRemoteUser();
		remoteUser.setUsername(username);
		if (username.equals(AppConstants.DEFAULT_ADMIN_USERNAME))
			remoteUser.setPassword((this.auditPassword != null) ? this.auditPassword.toLowerCase() : "");
		return remoteUser;
	}

}
