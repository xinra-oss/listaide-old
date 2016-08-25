package com.xinra.listaide.frontend;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.xinra.listaide.service.ServiceException;
import com.xinra.listaide.service.SpotifyService;

/**
 * The main view that contains the actual playlist manager
 * 
 * @author erikhofer
 */
public class ManagerView extends ListaideView {

	private static final long serialVersionUID = 1L;

	public ManagerView(ListaideUI ui) {
		super(ui);
		this.addComponent(new Button("sync", e -> {
			try {
				ui.getService(SpotifyService.class).synchronize();
			} catch (ServiceException ex) {
				ex.printStackTrace();
			}
		}));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
