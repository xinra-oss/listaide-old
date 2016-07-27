package com.xinra.listaide.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration of the service layer.
 * 
 * @author erikhofer
 */
@Configuration
public class ServiceConfig {

	@Bean
	@Primary
	public DTOFactory dtoFactory() {
		/*
		 * During early development we can use dynamic proxies.
		 */
		return new DynamicProxyDTOFactory();
		
		/*
		 * In production, when performance is a concern, we can switch to actual implementations.
		 */
		//return new UnderscoreImplDTOFactory();
	}
	
}
