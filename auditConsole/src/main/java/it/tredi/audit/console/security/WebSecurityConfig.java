package it.tredi.audit.console.security;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.tredi.audit.console.AppConstants;
import it.tredi.audit.console.security.authentication.AdminAuthenticationFilter;
import it.tredi.audit.console.security.authentication.XWayLinkAuthenticationFilter;
import it.tredi.audit.console.security.service.AuditUserDetailsService;
import it.tredi.spring.security.entity.SimpleRemoteUser;
import it.tredi.spring.security.jwt.JwtRecognizeTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuditUserDetailsService auditUserDetailsService;

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.auditUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance(); // TODO deprecato perche' non sicuro, andrebbe cambiato con altra implementazione
	}
	
	// elenco di pattern pubblici (privi di autenticazione)
	private final String LOGIN_PATTERN = AppConstants.API_URI_PREFIX + "/login";
	
	private final String XWAY_LOGIN_PATTERN = AppConstants.API_URI_PREFIX + "/xwaylogin";
	
	private final String AFTER_LOGIN_PATTERN = AppConstants.API_URI_PREFIX + "/login-success";
	
	private final String AFTER_XWAY_LOGIN_PATTERN = AppConstants.API_URI_PREFIX + "/login-xway-success";
	
	private final String API_PATTERN = AppConstants.API_URI_PREFIX + "/**";
	
	@Bean
	public AdminAuthenticationFilter adminAuthenticationFilter() throws Exception {
		return new AdminAuthenticationFilter(auditUserDetailsService, LOGIN_PATTERN, authenticationManager(), AFTER_LOGIN_PATTERN);
	}

	@Bean
	public JwtRecognizeTokenFilter jwtRecognizeTokenFilter() {
		return new JwtRecognizeTokenFilter(SimpleRemoteUser.class, API_PATTERN);
	}
	
	@Bean
	public XWayLinkAuthenticationFilter xwayLinkAuthenticationFilter() throws Exception {
		return new XWayLinkAuthenticationFilter(auditUserDetailsService, XWAY_LOGIN_PATTERN, authenticationManager(), AFTER_LOGIN_PATTERN);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable() // attivo di default su Boot 2
			.csrf().disable()
			
			.exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint())
			
			.and()
				// non abbiamo bisogno di una sessione
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.cors()
			.and()
				.authorizeRequests()
					// N.B.: tutte le richieste sono abilitate (caricamento pagine tramite angular) ad eccezione di url 
					// relativi ad API (caricamento di dati)
					.antMatchers(AppConstants.API_URI_PREFIX).authenticated()
	                .anyRequest().permitAll()
	        .and()
	        	.addFilterBefore(adminAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // filtro custom di autenticazione spring security
	        	.addFilterBefore(xwayLinkAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // filtro custom di autenticazione da link xWay
	        	.addFilterBefore(jwtRecognizeTokenFilter(), UsernamePasswordAuthenticationFilter.class) // filtro custom JWT per analisi del token spedito dal client
		;
		
		http.headers().cacheControl();
	}
	
	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
	    return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}
	
	/**
	 * Configurazione Cors per poter consumare le api restful con richieste ajax
	 * @return
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");
		configuration.setAllowedMethods(Arrays.asList("POST, PUT, GET, OPTIONS, DELETE"));
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
