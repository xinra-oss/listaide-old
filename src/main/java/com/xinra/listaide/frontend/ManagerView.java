package com.xinra.listaide.frontend;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.frontend.Tracks.HighlightLevel;
import com.xinra.listaide.service.PlaylistDTO;
import com.xinra.listaide.service.SpotifyService;
import com.xinra.listaide.service.TrackDTO;

/**
 * The main view that contains the actual playlist manager
 * 
 * @author erikhofer
 */
public class ManagerView extends ListaideView implements ValueChangeListener {

	private static final long serialVersionUID = 1L;
	
	private List<PlaylistDTO> playlists;
	private Map<String, BeanItem<PlaylistDTO>> playlistItems;
	private PlaylistDTO currentPlaylist;
	private VerticalLayout playlistLayout;
	private VerticalLayout rightSideLayout;
	private BeanFieldGroup<PlaylistDTO> playlistBinding;
	private Button btnSave;
	private Function<TrackDTO, Integer> inheritanceHighlighting;
	private Tracks tracks;
	private Tags<PlaylistDTO> parents;
	private Tags<PlaylistDTO> children;

	public ManagerView(ListaideUI ui) {
		super(ui);
		setMargin(false);
		playlistItems = new HashMap<>();
		playlists = ui.getService(SpotifyService.class).getPlaylists(ui.getUser().getId());
		playlists.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
		playlists.forEach(p -> {
			BeanItem<PlaylistDTO> item = new BeanItem<>(p);
			item.getItemProperty(PlaylistDTO.Followers).setReadOnly(true);
			playlistItems.put(p.getId(), item);
		});
		
		HorizontalSplitPanel splitLayout = new HorizontalSplitPanel();
		splitLayout.addStyleName(ValoTheme.SPLITPANEL_LARGE);
		splitLayout.setSplitPosition(15.0f);
		
		//build sidebar
		VerticalLayout sidebar = new VerticalLayout();
		sidebar.setSpacing(true);
		sidebar.setMargin(true);
		sidebar.addComponent(new Button("sync", e -> {
			try {
				ui.getService(SpotifyService.class).synchronize();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}));
		playlists.forEach(p -> sidebar.addComponent(new Button(p.getName(), 
				e -> ui.getNavigator().navigateTo(ListaideUI.VIEW_MAIN + "/" + p.getId()))));
		splitLayout.addComponent(sidebar);
		
		//build playlist editor
		btnSave = new Button("Save Changes", e -> {
			Notification.show("TODO save");
		});
		btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnSave.setEnabled(false);
		Button btnDiscard = new Button("Discard Changes", e -> {
			Notification.show("TODO discard");
		});
		btnDiscard.setEnabled(false);
		Button btnDelete = new Button("Delete Playlist");
		btnDelete.addStyleName(ValoTheme.BUTTON_QUIET);
		btnDelete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		//properties
		playlistBinding = new BeanFieldGroup<>(PlaylistDTO.class);
		playlistBinding.setFieldFactory(ListaideFieldGroupFieldFactory.get());
		playlistLayout = new VerticalLayout();
		playlistLayout.setMargin(true);
		playlistLayout.setSpacing(true);
		GridLayout propertyLayout = new GridLayout(5, 2);
		propertyLayout.setSpacing(true);
		propertyLayout.addStyleName(ListaideTheme.CHECKBOX_ALIGNMENT);
		propertyLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		propertyLayout.addComponent(playlistBinding.buildAndBind("Name", PlaylistDTO.Name));
		//make description 2 cell wide
		propertyLayout.addComponent(playlistBinding.buildAndBind("Description", PlaylistDTO.Description), 1, 0, 2, 0);
		propertyLayout.addComponents(
				btnSave,
				btnDiscard,
				playlistBinding.buildAndBind("Owner", PlaylistDTO.Owner),
				playlistBinding.buildAndBind("Followers", PlaylistDTO.Followers),
				playlistBinding.buildAndBind("Collaborative", PlaylistDTO.Collaborative),
				playlistBinding.buildAndBind("Public Access", PlaylistDTO.PublicAccess),
				btnDelete
			);
		propertyLayout.setComponentAlignment(playlistBinding.getField(PlaylistDTO.Owner), Alignment.MIDDLE_LEFT);
		playlistBinding.getField(PlaylistDTO.Name).setRequired(true);
		playlistBinding.getField(PlaylistDTO.Description).setWidth("100%");
		playlistBinding.getFields().forEach(f -> f.addValueChangeListener(this));
		playlistLayout.addComponent(propertyLayout);
		
		//parents
		playlistLayout.addComponent(new Label("Parents"));
		Button btnAddParent = new Button("Add", FontAwesome.PLUS);
		btnAddParent.addClickListener(e -> addParent());
		btnAddParent.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		parents = new Tags.Builder<PlaylistDTO>()
				.captionResolver(p -> p.getName())
				.action("Open", this::open, FontAwesome.ARROW_RIGHT)
				.action("Remove", this::removeParent, FontAwesome.REMOVE)
				.selectHandler(this::highlightParent)
				.deselectHandler(this::clearHighlighting)
				.append(btnAddParent)
				.build();
		playlistBinding.bind(parents, PlaylistDTO.Parents);
		playlistLayout.addComponent(parents);
		
		//children
		playlistLayout.addComponent(new Label("Children"));
		Button btnAddChild = new Button("Add", FontAwesome.PLUS);
		btnAddChild.addClickListener(e -> addChild());
		btnAddChild.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		children = new Tags.Builder<PlaylistDTO>()
				.captionResolver(p -> p.getName())
				.action("Open", this::open, FontAwesome.ARROW_RIGHT)
				.action("Remove", this::removeChild, FontAwesome.REMOVE)
				.selectHandler(this::highlightChild)
				.deselectHandler(this::clearHighlighting)
				.append(btnAddChild)
				.build();
		playlistBinding.bind(children, PlaylistDTO.Children);
		playlistLayout.addComponent(children);
		
		//Tracks
		playlistLayout.addComponent(new Label("Tracks"));
		tracks = new Tracks(this::getHighlightLevel);
		tracks.getGrid().setWidth("100%");
		tracks.getGrid().setHeightMode(HeightMode.ROW);
		tracks.getGrid().setHeightByRows(16);
		tracks.getGrid().sort(TrackDTO.Number);
		playlistBinding.bind(tracks, PlaylistDTO.Tracks);
		playlistLayout.addComponent(tracks);
		
		//symbol explanations
		HorizontalLayout symbolLayout = new HorizontalLayout();
		symbolLayout.setSpacing(true);
		symbolLayout.addComponent(new Label(FontAwesome.ARROW_UP.getHtml() + " Inherited From", ContentMode.HTML));
		symbolLayout.addComponent(new Label(FontAwesome.ARROW_DOWN.getHtml() + " Bequeathed To", ContentMode.HTML));
		Label inheritanceSingle = new Label(FontAwesome.CIRCLE.getHtml() + " Inherited/Bequeathed Once", ContentMode.HTML);
		inheritanceSingle.addStyleName(HighlightLevel.SINGLE.getSymbolStyle());
		Label inheritanceMulti = new Label(FontAwesome.CIRCLE.getHtml() + " Inherited/Bequeathed Multiple Times", ContentMode.HTML);
		inheritanceMulti.addStyleName(HighlightLevel.MULTI.getSymbolStyle());
		symbolLayout.addComponents(inheritanceSingle, inheritanceMulti);
		playlistLayout.addComponent(symbolLayout);
		
		rightSideLayout = new VerticalLayout();
		splitLayout.addComponent(rightSideLayout);
		addComponent(splitLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		rightSideLayout.removeAllComponents();
		String playlistId = event.getParameters();
		if(playlistId.isEmpty()) { //If no id is specified, show the first playlist (if there is one)
			if(!playlists.isEmpty()) {
				open(playlists.get(0));
			} else {
				VerticalLayout noPlaylists = new VerticalLayout();
				noPlaylists.setMargin(true);
				noPlaylists.addComponent(new Label("You do not have any playlists yet."));
				rightSideLayout.addComponent(noPlaylists);
				currentPlaylist = null;
			}
		} else { //show playlist with specified id
			BeanItem<PlaylistDTO> playlistItem = playlistItems.get(playlistId);
			if(playlistItem == null) {
				VerticalLayout error = new VerticalLayout();
				error.setMargin(true);
				Label errorLabel = new Label("Invalid Playlist ID!");
				errorLabel.addStyleName(ValoTheme.LABEL_FAILURE);
				error.addComponent(errorLabel);
				rightSideLayout.addComponent(error);
				currentPlaylist = null;
				return;
			}
			rightSideLayout.addComponent(playlistLayout);
			playlistBinding.setItemDataSource(playlistItem);
			currentPlaylist = playlistItem.getBean();
			tracks.getGrid().getColumn(TrackDTO.AddedBy).setHidden(!currentPlaylist.getCollaborative());
		}
	}
	
	private void removeParent(PlaylistDTO parent) {
		
	}
	
	private void addParent() {
		
	}
	
	private void removeChild(PlaylistDTO child) {
		
	}
	
	private void addChild() {
		
	}
	
	private void highlightParent(PlaylistDTO parent) {
		children.setSelection(Collections.emptySet());
		inheritanceHighlighting = track -> track.getInheritedFrom().get(parent) == null ? 0 : track.getInheritedFrom().size();
		tracks.getGrid().getColumn(Tracks.HIGHLIGHT_INDICATOR).setHidden(false);
		tracks.refreshHighlighting();
	}
	
	private void highlightChild(PlaylistDTO child) {
		parents.setSelection(Collections.emptySet());
		inheritanceHighlighting = track -> track.getBequeathedTo().get(child) == null ? 0 : track.getBequeathedTo().size();
		tracks.getGrid().getColumn(Tracks.HIGHLIGHT_INDICATOR).setHidden(false);
		tracks.refreshHighlighting();
	}
	
	private void clearHighlighting(PlaylistDTO playlistDTO) {
		inheritanceHighlighting = null;
		tracks.getGrid().getColumn(Tracks.HIGHLIGHT_INDICATOR).setHidden(true);
		tracks.refreshHighlighting();
	}
	
	/**
	 * Navigate to the specified playlist
	 */
	private void open(PlaylistDTO playlistDTO) {
		ui.getNavigator().navigateTo(ListaideUI.VIEW_MAIN + "/" + playlistDTO.getId());
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Notification.show(""+playlistBinding.isModified());
	}
	
	private HighlightLevel getHighlightLevel(TrackDTO track) {
		if(inheritanceHighlighting == null) return HighlightLevel.NONE;
		switch(inheritanceHighlighting.apply(track)) {
			case 0: return HighlightLevel.NONE;
			case 1: return HighlightLevel.SINGLE;
			default: return HighlightLevel.MULTI;
		}
	}

}
