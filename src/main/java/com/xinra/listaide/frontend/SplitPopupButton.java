package com.xinra.listaide.frontend;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

public class SplitPopupButton extends CustomComponent {
	
	private static final long serialVersionUID = 1L;
	
	public static final String STYLE_MAIN = "la-splitpopupbutton-main";
	public static final String STYLE_POPUP = "la-splitpopupbutton-popup";
	
	private PopupButton popupButton;
	private Button mainButton;
	
	public SplitPopupButton() {
		mainButton = new Button();
		mainButton.addStyleName(STYLE_MAIN);
		
		popupButton = new PopupButton();
		popupButton.addStyleName(STYLE_POPUP);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeUndefined();
		layout.addComponents(mainButton, popupButton);
		setCompositionRoot(layout);
	}
	
	@Override
	public void setCaption(String caption) {
		mainButton.setCaption(caption);
	}
	
	public SplitPopupButton(String caption) {
		this();
		mainButton.setCaption(caption);
	}
	
	public SplitPopupButton(String caption, ClickListener clickListener) {
		this(caption);
		mainButton.addClickListener(clickListener);
	}
	
	public SplitPopupButton(String caption, ClickListener clickListener, Resource icon) {
		this(caption, clickListener);
		mainButton.setIcon(icon);
	}
	
	public void addStyleName(String style) {
		mainButton.addStyleName(style);
		popupButton.addStyleName(style);
	}
	
	public void removeStyleName(String style) {
		mainButton.removeStyleName(style);
		popupButton.removeStyleName(style);
	}
	
	public PopupButton getPopupButton() {
		return popupButton;
	}

	public Button getMainButton() {
		return mainButton;
	}
}
