package com.xinra.listaide.frontend;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.data.util.converter.Converter;

/**
 * Creates HTML links from a collection of arbitrary objects separated by commas
 *
 * @param <T> Model type
 */
public class MultiLinkConverter<T> implements Converter<String, Collection<? extends T>> {

	private static final long serialVersionUID = 1L;
	
	LinkConverter<T> linkConverter;
	
	public MultiLinkConverter(Class<T> type, Function<T, String> captionResolver, Function<T, String> urlResolver) {
		linkConverter = new LinkConverter<>(type, captionResolver, urlResolver);
	}

	@Override
	public Collection<? extends T> convertToModel(String value, Class<? extends Collection<? extends T>> targetType,
			Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(Collection<? extends T> value, Class<? extends String> targetType,
			Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		return value.stream()
			.map(l -> linkConverter.convertToPresentation(l, String.class, locale))
			.collect(Collectors.joining(", "));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Collection<? extends T>> getModelType() {
		return (Class<Collection<? extends T>>) (Class<?>) Collection.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}



}
