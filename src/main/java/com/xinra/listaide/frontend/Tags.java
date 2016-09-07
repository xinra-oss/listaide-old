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

import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;

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
		
		public Builder<T> selectionMode(SelectionMode selectionMode) {
			this.selectionMode = selectionMode;
			return this;
		}
		
		public Builder<T> captionResolver(Function<T, String> captionResolver) {
			this.captionResolver = captionResolver;
			return this;
		}
		
		public Tags<T> build() {
			return new Tags<>(actions, append, prepend, selectionMode, captionResolver, selectHandler, deselectHandler);
		}
	}
	
	private HorizontalLayout layout;
	private Map<T, MenuBar> tagMenus;
	private Set<T> selection;
	
	private List<Action<T>> actions;
	private List<Component> append;
	private List<Component> prepend;
	private SelectionMode selectionMode;
	private Function<T, String> captionResolver;
	private Consumer<T> selectHandler;
	private Consumer<T> deselectHandler;

	public Tags(List<Action<T>> actions, List<Component> append, List<Component> prepend, SelectionMode selectionMode,
			Function<T, String> captionResolver, Consumer<T> selectHandler, Consumer<T> deselectHandler) {
		this.actions = actions;
		this.append = append;
		this.prepend = prepend;
		this.selectionMode = selectionMode;
		this.captionResolver = captionResolver;
		this.selectHandler = selectHandler; 
		this.deselectHandler = deselectHandler; 
		
		tagMenus = new HashMap<>();
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
		tagMenus.get(tag).getItems().get(0).setChecked(true);
		if(selectHandler != null) selectHandler.accept(tag);
	}
	
	private void deselect(T tag) {
		selection.remove(tag);
		tagMenus.get(tag).getItems().get(0).setChecked(false);
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
			MenuBar tagMenu = tagMenus.get(tag); //get tagMenu from cache
			if(tagMenu == null) { //if not cached, create a new one
				tagMenu = new MenuBar();
				MenuBar.MenuItem tagItem = tagMenu.addItem(captionResolver.apply(tag), null);
				if(selectionMode != SelectionMode.NONE) {
					tagItem.setCheckable(true);
					tagItem.setCommand(i -> {
						if(selection.contains(tag)) {
							deselect(tag);
							return;
						}
						if(selectionMode == SelectionMode.SINGLE)
							setSelection(Collections.emptySet());
						select(tag);
					});
				}
				if(!actions.isEmpty()) { //if applicable, add actions
					MenuBar.MenuItem actionsItem = selectionMode == SelectionMode.NONE ? tagItem : tagMenu.addItem("", null);
					actions.forEach(a -> actionsItem.addItem(a.caption, a.icon, i -> a.listener.accept(tag)));
				}
				tagMenus.put(tag, tagMenu); //add to cache
			}
			layout.addComponent(tagMenu);
		});
		append.forEach(layout::addComponent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Set<T>> getType() {
		return (Class<? extends Set<T>>) (Class<?>) Set.class;
	}

}
