package it.tredi.audit.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.tredi.audit.console.AppConstants;
import it.tredi.spring.security.entity.SimpleRemoteUser;

@Controller
@RequestMapping(value = AppConstants.API_URI_PREFIX)
public class XwayLoginController {
	
	@Autowired
	private SimpleRemoteUser remoteUser;
	
	/**
	 * Ritorno in caso di login tramite chiave di autenticazione da DocWay
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login-xway-success")
	public String xwayLogin() throws Exception {
		if (remoteUser == null)
			throw new Exception("RemoteUser is NULL -> xwayLogin failure!");

		return "index";
	}

}
