package com.xinra.listaide.frontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

public final class Tags<T> extends CustomField<Set<T>> {

	private static final long serialVersionUID = 1L;
	
	public static enum SelectionMode {
		SINGLE,
		MULTI,
		NONE
	}
	
	private static class Action<T> {
		String caption; 
		Consumer<T> listener; 
		Resource icon;
		
		public Action(String caption, Consumer<T> listener, Resource icon) {
			this.caption = caption;
			this.listener = listener;
			this.icon = icon;
		}
	}
	
	public static class Builder<T> {
		private List<Action<T>> actions = new ArrayList<>();
		private List<Component> append = new ArrayList<>();
		private List<Component> prepend = new ArrayList<>();
		private String selectedStyle = ValoTheme.BUTTON_PRIMARY;
		private SelectionMode selectionMode = SelectionMode.SINGLE;
		private Function<T, String> captionResolver = t -> t.toString();
		private Consumer<T> selectHandler;
		private Consumer<T> deselectHandler;
		
		public Builder<T> selectHandler(Consumer<T> selectHandler) {
			this.selectHandler = selectHandler;
			return this;
		}
		
		public Builder<T> deselectHandler(Consumer<T> deselectHandler) {
			this.deselectHandler = deselectHandler;
			return this;
		}
		
		public Builder<T> action(String caption, Consumer<T> listener) {
			return action(caption, listener, null);
		}
		
		public Builder<T> action(String caption, Consumer<T> listener, Resource icon) {
			actions.add(new Action<>(caption, listener, icon));
			return this;
		}
		
		public Builder<T> append(Component component) {
			append.add(component);
			return this;
		}
		
		public Builder<T> prepend(Component component) {
			prepend.add(component);
			return this;
		}
		
		public Builder<T> selectedStyle(String selectedStyle) {
			this.selectedStyle = selectedStyle;
			return this;
		}
		
		public Builder<T> selectionMode(SelectionMode selectionMode) {
			this.selectionMode = selectionMode;
			return this;
		}
		
		public Builder<T> captionResolver(Function<T, String> captionResolver) {
			this.captionResolver = captionResolver;
			return this;
		}
		
		public Tags<T> build() {
			return new Tags<>(actions, append, prepend, selectedStyle, selectionMode, captionResolver, selectHandler, deselectHandler);
		}
	}
	
	private HorizontalLayout layout;
	private Map<T, Component> buttons;
	private Set<T> selection;
	
	private List<Action<T>> actions;
	private List<Component> append;
	private List<Component> prepend;
	private String selectedStyle;
	private SelectionMode selectionMode;
	private Function<T, String> captionResolver;
	private Consumer<T> selectHandler;
	private Consumer<T> deselectHandler;

	public Tags(List<Action<T>> actions, List<Component> append, List<Component> prepend, String selectedStyle,
			SelectionMode selectionMode, Function<T, String> captionResolver, Consumer<T> selectHandler, Consumer<T> deselectHandler) {
		this.actions = actions;
		this.append = append;
		this.prepend = prepend;
		this.selectedStyle = selectedStyle;
		this.selectionMode = selectionMode;
		this.captionResolver = captionResolver;
		this.selectHandler = selectHandler; 
		this.deselectHandler = deselectHandler; 
		
		buttons = new HashMap<>();
		selection = new HashSet<>();
		layout = new HorizontalLayout();
		layout.setSpacing(true);
	}
	
	public Set<T> getSelection() {
		return Collections.unmodifiableSet(selection);
	}
	
	public void setSelection(T selection) {
		Set<T> set = new HashSet<>();
		set.add(selection);
		setSelection(set);
	}
	
	public void setSelection(Set<T> selection) {
		if(selectionMode == SelectionMode.NONE && !selection.isEmpty())
			throw new IllegalArgumentException("Selection is not enabled!");
		if(selectionMode == SelectionMode.SINGLE && selection.size() > 1) 
			throw new IllegalArgumentException("Field is in single selection mode!");
		Set<T> processed = new HashSet<>(this.selection);
		for(T tag : selection) {
			if(!getInternalValue().contains(tag))
				throw new IllegalArgumentException("Tag to be selected does not exist!");
			if(!this.selection.contains(tag)) {
				select(tag);
			}
			processed.add(tag);
		}
		processed.stream()
			.filter(tag -> !selection.contains(tag))
			.forEach(this::deselect);
	}
	
	private void select(T tag) {
		selection.add(tag);
		buttons.get(tag).addStyleName(selectedStyle);
		if(selectHandler != null) selectHandler.accept(tag);
	}
	
	private void deselect(T tag) {
		selection.remove(tag);
		buttons.get(tag).removeStyleName(selectedStyle);
		if(deselectHandler != null) deselectHandler.accept(tag);
	}

	@Override
	protected Component initContent() {
		return layout;
	}
	
	@Override
	protected void setInternalValue(Set<T> newValue) {
		super.setInternalValue(newValue);
		layout.removeAllComponents(); //clear and re-add
		prepend.forEach(layout::addComponent);
		newValue.forEach(tag -> {
			Component button = buttons.get(tag); //get button from cache
			if(button == null) { //if not cached, create a new one
				try {
					button = getButtonClass().newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				button.setCaption(captionResolver.apply(tag));
				if(!actions.isEmpty()) { //if applicable, add action menu
					ContextMenu.Builder popupBuilder = new ContextMenu.Builder();
					actions.forEach(a -> popupBuilder.action(a.caption, e -> a.listener.accept(tag), a.icon));
					popupBuilder.build().attachToButton(getButtonClass().isAssignableFrom(PopupButton.class) ?
							(PopupButton) button : ((SplitPopupButton) button).getPopupButton());
				}
				if(selectionMode != SelectionMode.NONE) { //if applicable, add select listener
					(getButtonClass().isAssignableFrom(SplitPopupButton.class)
							? ((SplitPopupButton) button).getMainButton()
							: (Button) button).addClickListener(e -> {
								if(selection.contains(tag)) {
									deselect(tag);
									return;
								}
								if(selectionMode == SelectionMode.SINGLE)
									setSelection(Collections.emptySet());
								select(tag);
							});
				}
				buttons.put(tag, button); //add to cache
			}
			layout.addComponent(button);
		});
		append.forEach(layout::addComponent);
	}
	
	private Class<? extends Component> getButtonClass() {
		if(actions.isEmpty()) {
			return Button.class;
		} else if(selectionMode == SelectionMode.NONE) {
			return PopupButton.class;
		} else {
			return SplitPopupButton.class;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Set<T>> getType() {
		return (Class<? extends Set<T>>) Set.class;
	}

}
