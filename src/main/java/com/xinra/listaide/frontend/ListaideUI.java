package com.xinra.listaide.frontend;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.service.AuthorizationService;
import com.xinra.listaide.service.Service;
import com.xinra.listaide.service.ServiceException;
import com.xinra.listaide.service.ServiceProvider;
import com.xinra.listaide.service.SpotifyService;
import com.xinra.listaide.service.UserDTO;

@SpringUI
@Theme("Listaide")
public class ListaideUI extends UI{
	
	private static final long serialVersionUID = 1L;
	
	public static final String SESSION_COOKIE = "listaide-session-id";
	public static final String VIEW_MAIN = "";
	
	@Autowired
	private ServiceProvider serviceProvider;
	
	private Navigator navigator;
	private Map<String, ListaideView> views;
	private UserDTO user;

	@Override
	protected void init(VaadinRequest request){
		views = new HashMap<>();
		String baseUrl = "http://localhost:8080";
		
		//Retrieve session id
		String sessionId = null;
		for(Cookie cookie : request.getCookies()) {
			if(cookie.getName().equals(SESSION_COOKIE)) sessionId = cookie.getValue();
		}
		
		if(sessionId != null) {
			String code = request.getParameter("code");
			if(code != null) { //if applicable complete authorization
				try {
					getService(AuthorizationService.class).completeAuthorization(sessionId, request.getParameter("state"), code, baseUrl);
					getPage().setLocation(baseUrl); //get rid of parameters and reload UI
					return;
				} catch (ServiceException e) {
					views.put(VIEW_MAIN, new ConnectView(this, baseUrl, e.getMessage()));
				}
			} else { //If there is a session, try to attach it
				try {
					user = getService(SpotifyService.class).attachSession(sessionId);
					views.put(VIEW_MAIN, new ManagerView(this));
				} catch (ServiceException e) {
					//Ignore error message
					views.put(VIEW_MAIN, new ConnectView(this, baseUrl));
				}
			}
		} else { //no session cookie
			views.put(VIEW_MAIN, new ConnectView(this, baseUrl));
		}
		
		//other views
		views = Collections.unmodifiableMap(views);
		
		//set up UI
		Page.getCurrent().setTitle("ListAide for Spotify");
		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		
		//header
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName(ListaideTheme.HEADER);
		header.setWidth("100%");
		Button appname = new Button("ListAide");
		appname.addClickListener(e -> navigator.navigateTo(VIEW_MAIN));
		appname.addStyleName(ListaideTheme.APPNAME);
		header.addComponent(appname);
		content.addComponent(header);
		
		//view port and navigator
		VerticalLayout viewport = new VerticalLayout();
		viewport.setWidth("100%");
		content.addComponent(viewport);
		content.setExpandRatio(viewport, 1); //use all available space -> sticky footer
		navigator = new Navigator(this, viewport);
		views.entrySet().forEach(e -> navigator.addView(e.getKey(), e.getValue()));
		
		//footer
		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidth("100%");
		footer.addStyleName(ListaideTheme.FOOTER);
		footer.addComponent(new Label("footer"));
		content.addComponent(footer);
		
		setContent(content);
	}
	
	/**
	 * Exposes {@link ServiceProvider#getService(Class)} to views as they are not Spring-configured.
	 */
	public <T extends Service> T getService(Class<T> type) {
		return serviceProvider.getService(type);
	}
}
