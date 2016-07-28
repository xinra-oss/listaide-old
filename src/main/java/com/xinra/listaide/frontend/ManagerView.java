package com.xinra.listaide.frontend;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

/**
 * The main view that contains the actual playlist manager
 * 
 * @author erikhofer
 */
public class ManagerView extends ListaideView {

	private static final long serialVersionUID = 1L;

	public ManagerView(ListaideUI ui) {
		super(ui);
		this.addComponent(new Label("authorised"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
				
	}

}
