package com.xinra.listaide.frontend;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

public abstract class ListaideView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	
	protected final ListaideUI ui;
	
	public ListaideView(ListaideUI ui) {
		this.ui = ui;
	}

}
