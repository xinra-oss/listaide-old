package com.xinra.listaide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The default implementation will delegate service retrieval to the Spring container.
 * 
 * @author erikhofer
 */
@Component
public class DefaultServiceProvider implements ServiceProvider {
	
	@Autowired
	ApplicationContext applicationContext;

	@Override
	public <T extends Service> T getService(Class<T> type) {
		return applicationContext.getBean(type);
	}
	
}
