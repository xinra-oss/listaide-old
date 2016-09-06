package com.xinra.listaide.frontend;

import java.util.function.Function;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Link;

public abstract class LinkField<T> extends CustomField<T> {

	private static final long serialVersionUID = 1L;
	
	protected Link link;
	
	protected abstract String getLinkText(T value);

	protected abstract String getLinkUrl(T value);
	
	public LinkField() {
		link = new Link();
		link.setTargetName("_blank");
	}

	@Override
	protected Component initContent() {
		return link;
	}
	
	@Override
	protected void setInternalValue(T newValue) {
		super.setInternalValue(newValue);
		link.setCaption(getLinkText(newValue));
		link.setResource(new ExternalResource(getLinkUrl(newValue)));
	}
	
	public static <T> LinkField<T> withResolvers(Class<? extends T> type, Function<T, String> textResolver, Function<T, String> urlResolver) {
		return new ResolverLinkField<>(type, textResolver, urlResolver);
	}
	
	private static class ResolverLinkField<T> extends LinkField<T> {
		
		private static final long serialVersionUID = 1L;
		
		private Class<? extends T> type;
		private Function<T, String> textResolver;
		private Function<T, String> urlResolver;
		
		public ResolverLinkField(Class<? extends T> type, Function<T, String> textResolver, Function<T, String> urlResolver) {
			this.type = type;
			this.textResolver = textResolver;
			this.urlResolver = urlResolver;
		}

		@Override
		public Class<? extends T> getType() {
			return type;
		}

		@Override
		protected String getLinkText(T value) {
			return textResolver.apply(value);
		}

		@Override
		protected String getLinkUrl(T value) {
			return urlResolver.apply(value);
		}
		
	}
}
