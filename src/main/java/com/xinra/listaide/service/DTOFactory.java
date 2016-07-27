package com.xinra.listaide.service;

/**
 * Used to create DTOs in the front-end and service layer.
 * 
 * @author erikhofer
 */
public interface DTOFactory {
	
	/**
	 * Creates a DTO instance of the desired type.
	 */
	public <T extends DTO> T createDTO(Class<T> type);
	
}
