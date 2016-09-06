package com.xinra.listaide.service;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.xinra.listaide.frontend.Util;

public class DurationConverter implements Converter<String, Integer> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value == null ? "" : Util.formatDuration(value);
	}

	@Override
	public Class<Integer> getModelType() {
		return Integer.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
