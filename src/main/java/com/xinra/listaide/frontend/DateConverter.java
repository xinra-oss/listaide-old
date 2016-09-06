package com.xinra.listaide.frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class DateConverter implements Converter<String, Date> {
	
	private static final long serialVersionUID = 1L;
	
	private DateFormat dateFormat;
	
	public DateConverter(String pattern) {
		dateFormat = new SimpleDateFormat(pattern);
	}
	
	public DateConverter(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value == null ? "" : dateFormat.format(value);
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
