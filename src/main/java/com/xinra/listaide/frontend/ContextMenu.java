package com.xinra.listaide.frontend;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Non-hierarchical context menu which is uses in connection with {@link PopupButton}.
 * 
 * @author erikhofer
 */
public class ContextMenu extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	/**
	 * Entry in a {@link ContextMenu}.
	 * 
	 * @author erikhofer
	 */
	public static class Entry extends Button {
		private static final long serialVersionUID = 1L;
				
		public Entry() {
			addStyleName(ValoTheme.BUTTON_BORDERLESS);
			addStyleName(ValoTheme.BUTTON_SMALL);
		}
		
		public Entry(String caption, ClickListener listener) {
			this();
			setCaption(caption);
			addClickListener(listener);
		}
		
		public Entry(String caption, ClickListener listener, Resource icon) {
			this(caption, listener);
			setIcon(icon);
			
		}	
	}
	
	/**
	 * Separator in a {@link ContextMenu}.
	 * 
	 * @author erikhofer
	 */
	public static class Separator extends Label {
		private static final long serialVersionUID = 1L;
		
		public Separator() {
			super("<hr />", ContentMode.HTML);
		}
	}
	
	public ContextMenu() {
		super();
	}
	
	public ContextMenu(Component... components) {
		this();
		addComponents(components);
	}
}
