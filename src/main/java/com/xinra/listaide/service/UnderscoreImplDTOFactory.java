package com.xinra.listaide.service;

/**
 * For the interface {@code package.SomeDTO} the class {@code package._SomeDTOImpl} is instantiated.
 * 
 * @author erikhofer
 */
public class UnderscoreImplDTOFactory implements DTOFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DTO> T createDTO(Class<T> type) {
		String className = type.getPackage().getName() + "._" + type.getSimpleName() + "Impl";
		try {
			return (T) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("DTO could not be created: " + className);
		}
	}

}
