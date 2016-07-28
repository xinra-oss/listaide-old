package com.xinra.listaide.frontend;

import javax.servlet.http.Cookie;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.service.AuthorizationDTO;
import com.xinra.listaide.service.AuthorizationService;

public class ConnectView extends ListaideView {

	private static final long serialVersionUID = 1L;

	public ConnectView(ListaideUI ui, String redirectUri) {
		super(ui);
		
		//Generate session id and auth url
		AuthorizationDTO auth = ui.getService(AuthorizationService.class).beginAuthorization(redirectUri);

		//Set session id cookie
		Cookie sessionCookie = new Cookie(ListaideUI.SESSION_COOKIE, auth.getSessionId());
		String ctxPath = VaadinService.getCurrentRequest().getContextPath();
		sessionCookie.setPath(ctxPath.isEmpty() ? "/" : ctxPath);
		VaadinService.getCurrentResponse().addCookie(sessionCookie);
		
		//Display connect button		
		Button button = new Button("Connect with Spotify");
		button.addStyleName(ValoTheme.BUTTON_HUGE);
		button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		button.addClickListener(e -> Page.getCurrent().setLocation(auth.getUrl()));
		this.addComponent(button);
		this.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
	}
	
	public ConnectView(ListaideUI ui, String redirectUri, String error) {
		this(ui, redirectUri);
		Label errorLabel = new Label(error);
		errorLabel.addStyleName(ValoTheme.LABEL_LARGE);
		errorLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		this.addComponentAsFirst(errorLabel);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}
}
