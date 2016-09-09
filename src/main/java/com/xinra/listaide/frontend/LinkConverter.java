package com.xinra.listaide.frontend;

import java.util.Locale;
import java.util.function.Function;

import com.vaadin.data.util.converter.Converter;

/**
 * Creates HTML link from arbitrary objects
 *
 * @param <T> Model type
 */
public class LinkConverter<T> implements Converter<String, T> {
	
	private static final long serialVersionUID = 1L;
	
	private Class<T> type;
	private Function<T, String> captionResolver;
	private Function<T, String> urlResolver;

	public LinkConverter(Class<T> type, Function<T, String> captionResolver, Function<T, String> urlResolver) {
		this.type = type;
		this.captionResolver = captionResolver;
		this.urlResolver = urlResolver;
	}

	@Override
	public T convertToModel(String value, Class<? extends T> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return String.format("<a href='%s' target='_blank'>%s</a>", urlResolver.apply(value), captionResolver.apply(value));
	}

	@Override
	public Class<T> getModelType() {
		return type;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
