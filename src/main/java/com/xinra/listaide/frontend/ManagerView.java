package com.xinra.listaide.frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.service.PlaylistDTO;
import com.xinra.listaide.service.SpotifyService;

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
		splitLayout.setSplitPosition(20.0f);
		splitLayout.setHeight("100%");
		
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
		Tags<PlaylistDTO> parents = new Tags.Builder<PlaylistDTO>()
				.captionResolver(p -> p.getName())
				.action("Open", this::open, FontAwesome.ARROW_RIGHT)
				.action("Remove", this::removeParent, FontAwesome.REMOVE)
				.append(btnAddParent)
				.build();
		playlistBinding.bind(parents, PlaylistDTO.Parents);
		playlistLayout.addComponent(parents);
		
		//children
		playlistLayout.addComponent(new Label("Children"));
		Button btnAddChild = new Button("Add", FontAwesome.PLUS);
		btnAddChild.addClickListener(e -> addChild());
		btnAddChild.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		Tags<PlaylistDTO> children = new Tags.Builder<PlaylistDTO>()
				.captionResolver(p -> p.getName())
				.action("Open", this::open, FontAwesome.ARROW_RIGHT)
				.action("Remove", this::removeChild, FontAwesome.REMOVE)
				.append(btnAddChild)
				.build();
		playlistBinding.bind(children, PlaylistDTO.Children);
		playlistLayout.addComponent(children);
		
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
			rightSideLayout.addComponent(playlistLayout);
			BeanItem<PlaylistDTO> playlistItem = playlistItems.get(playlistId);
			if(playlistItem.getBean().getTracks() == null) { //load tracks lazily
				playlistItem.getBean().setTracks(ui.getService(SpotifyService.class).getTracks(playlistId));
			}
			playlistBinding.setItemDataSource(playlistItem);
			currentPlaylist = playlistItem.getBean();
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

}
