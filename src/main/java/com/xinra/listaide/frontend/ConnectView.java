package com.xinra.listaide.frontend;

import javax.servlet.http.Cookie;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
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
		Link link = new Link("Connect with Spotify", new ExternalResource(auth.getUrl()));
		this.addComponent(link);
	}
	
	public ConnectView(ListaideUI ui, String redirectUri, String error) {
		this(ui, redirectUri);
		this.addComponent(new Label("Error: " + error), 0);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}
}
