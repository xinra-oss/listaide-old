package com.xinra.listaide.frontend;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Non-hierarchical context menu which is used in connection with {@link PopupButton}.
 * 
 * @author erikhofer
 */

public class ContextMenu extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	public static class Action {
		String caption; 
		Button.ClickListener listener; 
		Resource icon;
		
		public Action(String caption, Button.ClickListener listener, Resource icon) {
			this.caption = caption;
			this.listener = listener;
			this.icon = icon;
		}
	}
	
	public static class Builder {
		
		private List<Object> components;
		
		public Builder() {
			components = new ArrayList<>();
		}
		
		public Builder action(String caption, Button.ClickListener listener) {
			action(caption, listener, null);
			return this;
		}
		
		public Builder action(String caption, Button.ClickListener listener, Resource icon) {
			components.add(new Action(caption, listener, icon));
			return this;
		}
		
		public Builder action(Action action) {
			components.add(action);
			return this;
		}
		
		public Builder label(String text) {
			components.add(new Label(text));
			return this;
		}
		
		public Builder label(String text, Resource icon) {
			Label label = new Label(text);
			label.setIcon(icon);
			components.add(label);
			return this;
		}
		
		public Builder seperator() {
			Label seperator = new Label("<hr />", ContentMode.HTML);
			components.add(seperator);
			return this;
		}
		
		public Builder component(Component component) {
			components.add(component);
			return this;
		}
		
		public ContextMenu build() {
			return new ContextMenu(components);
		}
		
	}
	
	private PopupButton button;
	
	private ContextMenu(List<Object> components) {
		components.forEach(c -> {
			if(c instanceof Action) {
				Action action = (Action) c;
				Button actionButton = new Button(action.caption, action.icon);
				actionButton.addStyleName(ValoTheme.BUTTON_LINK);
				actionButton.addStyleName(ValoTheme.BUTTON_SMALL);
				actionButton.addClickListener(e -> {
					this.button.setPopupVisible(false);
					action.listener.buttonClick(e);
				});
				addComponent(actionButton);
			} else {
				addComponent((Component) c);
			}
		});
	}
	
	public void detachFromButton() {
		if(button != null) button.setContent(null);
		button = null;
	}
	
	public void attachToButton(PopupButton button) {
		detachFromButton();
		this.button = button;
		button.setContent(this);
	}
}
