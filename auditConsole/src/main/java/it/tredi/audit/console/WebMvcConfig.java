package it.tredi.audit.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.tredi.audit.console.config.ExportProperties;
import it.tredi.spring.progressbar.service.CacheableProgressbarService;
import it.tredi.spring.progressbar.service.LocalOutFileService;
import it.tredi.spring.progressbar.service.OutFileService;
import it.tredi.spring.progressbar.service.ProgressbarService;
import it.tredi.spring.security.entity.SimpleRemoteUser;

@Configuration
@EnableSpringDataWebSupport
@EnableCaching
@ComponentScan(basePackages = {"it.tredi.spring"})
public class WebMvcConfig implements WebMvcConfigurer {

	private final String[] RESOURCES_PATTERNS = { "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.css.map", "/**/*.js.map", "/**/*.eot", "/**/*.svg", "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/favicon.ico", "/resources/**" };
	private final String[] ASSETS_PATTERNS = { "/assets/**" };
	
	private final String RESOURCE_LOCATOR = "/resources/";
	
	private final String ANONYMOUS_USER_PRINCIPAL = "anonymousUser"; // TODO potrebbe cambiare in base ad una configurazione?
	
	@Autowired
	private ExportProperties exportProperties;
	
	/**
	 * Definizione di tutte le risorse statiche gestite dall'applicazione
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		for (String resource : RESOURCES_PATTERNS) {
			if (resource != null && !resource.isEmpty())
				registry.addResourceHandler(resource).addResourceLocations(RESOURCE_LOCATOR);
		}
		for (String asset : ASSETS_PATTERNS) {
			if (asset != null && !asset.isEmpty())
				registry.addResourceHandler(asset).addResourceLocations(RESOURCE_LOCATOR + "assets/");
		}
	}
	
	private boolean isAnonymousUser(Authentication authentication) {
		return (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().toString().equals(ANONYMOUS_USER_PRINCIPAL));
	}
	
	/**
	 * Recupero del remoteUser (utente collegato all'applicativo) recuperandolo 
	 * dal security context di Spring
	 * @return
	 */
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public SimpleRemoteUser remoteUser() {
		SimpleRemoteUser user = null;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!isAnonymousUser(authentication) && authentication.getPrincipal() instanceof SimpleRemoteUser) 
			user = (SimpleRemoteUser) authentication.getPrincipal(); // TODO corretto? ... da approfondire
		
		return user;
	}
	
	/**
	 * ThreadPool di esportazione record (tramite processi asincroni). E' possibile definire il numero massimo di thread eseguiti contemporaneamente attraverso
	 * max-pool-size (n) e queue-capacity (0)
	 */
	@Bean
	public TaskExecutor exportPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		if (this.exportProperties.getMaxConcurrentJobs() > 0)
			executor.setMaxPoolSize(this.exportProperties.getMaxConcurrentJobs());
		executor.setQueueCapacity(0);
		executor.setThreadNamePrefix("export_thread");
		executor.initialize();
		return executor;
	}
	
	/**
	 * Istanzia il servizio di gestione delle progressbar (elaborazione delle operazioni asincrone)
	 * @return
	 */
	@Bean
	public ProgressbarService progressbarService() {
		return new CacheableProgressbarService();
	}
	
	/**
	 * Istanzia il bean di gestione dei file di output restituiti dalla lavorazione delle progressbar
	 * @return
	 */
	@Bean
	public OutFileService outFileService() {
		return new LocalOutFileService();
	}
	
}
