package com.xinra.listaide.service;

/**
 * Used to access the service layer.
 */
public interface ServiceProvider {

	public <T extends Service> T getService(Class<T> type);
	
}
