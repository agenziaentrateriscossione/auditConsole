package it.tredi.audit.console.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.tredi.audit.console.AppConstants;
import it.tredi.audit.console.config.ApplicationUrlMapper;
import it.tredi.audit.console.config.ExportProperties;
import it.tredi.spring.security.entity.SimpleRemoteUser;

@RestController
@RequestMapping(value = AppConstants.API_URI_PREFIX)
public class GlobalController {

	@Autowired
	private SimpleRemoteUser remoteUser;

	@Autowired
	private ApplicationUrlMapper applicationUrlMapper;
	
	@Autowired
	private ExportProperties exportProperties;
	
	@Value("${audit.view.hideIpAddress:true}")
	private boolean hideIpAddress;
	
	/**
	 * Ritorno in caso di login tramite username e password
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login-success", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> login() throws Exception {
		if (remoteUser == null)
			throw new Exception("RemoteUser is NULL -> login failure!");

		Map<String, Object> map = new HashMap<>();
		map.put("username", remoteUser.getUsername());
		map.put("login", "done");

		map.put("app-url-mapper", applicationUrlMapper.getJsonAppUrlMap());
		
		// Aggiunta dei limiti su funzione di export
		Map<String, Object> settings = new HashMap<>();
		settings.put("export-max-size", exportProperties.getMaxSize());
		settings.put("export-validation-max-size", exportProperties.getValidationMaxSize());
		settings.put("hide-ip-address", hideIpAddress);
		
		map.put("app-settings", settings);

		return map;
	}

}
