package com.xinra.listaide.frontend;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

public abstract class ListaideView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	
	protected final ListaideUI ui;
	
	//ensure that the appropriate super constructor is called
	@SuppressWarnings("unused")
	private ListaideView() {
		ui = null;
	}
	
	public ListaideView(ListaideUI ui) {
		this.ui = ui;
		this.setWidth("100%");
		this.setMargin(true);
		this.setSpacing(true);
	}

}
