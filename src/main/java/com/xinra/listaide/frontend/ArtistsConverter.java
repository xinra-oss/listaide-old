package com.xinra.listaide.frontend;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.xinra.listaide.service.ArtistDTO;

public class ArtistsConverter implements Converter<Label, List<ArtistDTO>> {

	private static final long serialVersionUID = 1L;

	@Override
	public List<ArtistDTO> convertToModel(Label value, Class<? extends List<ArtistDTO>> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Label convertToPresentation(List<ArtistDTO> value, Class<? extends Label> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		String text = value.stream()
				.map(a -> "<a href='"+a.getUrl()+"'>"+a.getName()+"</a>")
				.collect(Collectors.joining(", "));
		return new Label(text.toString(), ContentMode.HTML);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<List<ArtistDTO>> getModelType() {
		return (Class<List<ArtistDTO>>) (Class<?>) List.class;
	}

	@Override
	public Class<Label> getPresentationType() {
		return Label.class;
	}

}
