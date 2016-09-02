package com.xinra.listaide.frontend;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.xinra.listaide.service.UserDTO;

public class ListaideFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
	
	private static final ListaideFieldGroupFieldFactory INSTANCE = new ListaideFieldGroupFieldFactory();

	private static final long serialVersionUID = 1L;
	
	protected ListaideFieldGroupFieldFactory() {}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
		if(UserDTO.class.isAssignableFrom(type)) return fieldType.cast(createUserField());
		return super.createField(type, fieldType);
	}
	
	@Override
	protected <T extends AbstractTextField> T createAbstractTextField(Class<T> fieldType) {
		T textField = super.createAbstractTextField(fieldType);
		textField.setNullRepresentation("");
		return textField;
	}
	
	protected UserField createUserField() {
		return new UserField();
	}
	
	public static ListaideFieldGroupFieldFactory get() {
		return INSTANCE;
	}
	
}
