package it.tredi.audit.console.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import it.tredi.audit.console.entity.ErrorLevel;
import it.tredi.audit.console.entity.ExceptionResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public Object handleStaticResourceNotFound(NoHandlerFoundException e, HttpServletRequest req) {
		if (logger.isWarnEnabled())
			logger.warn("No Handler found for '" + req.getRequestURI() + "' [WARN: URI not mapped by Controller!]");
		
		return handleException(e); // URL specificato non valido
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ExceptionResponse handleException(Exception e) {
		logger.error("Got exception... " + e.getMessage(), e);
		return new ExceptionResponse(e.getMessage(), null, ErrorLevel.ERROR, e.toString());
	}

}
