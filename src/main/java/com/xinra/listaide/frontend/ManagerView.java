package com.xinra.listaide.frontend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.service.DurationConverter;
import com.xinra.listaide.service.PlaylistDTO;
import com.xinra.listaide.service.SpotifyService;
import com.xinra.listaide.service.TrackDTO;

/**
 * The main view that contains the actual playlist manager
 * 
 * @author erikhofer
 */
public class ManagerView extends ListaideView implements ValueChangeListener, CellStyleGenerator {

	private static final long serialVersionUID = 1L;
	
	private List<PlaylistDTO> playlists;
	private Map<String, BeanItem<PlaylistDTO>> playlistItems;
	private PlaylistDTO currentPlaylist;
	private VerticalLayout playlistLayout;
	private VerticalLayout rightSideLayout;
	private BeanFieldGroup<PlaylistDTO> playlistBinding;
	private Button btnSave;
	private Table trackTable;
	private Map<TrackDTO, Object[]> trackItemCache;
	private Function<TrackDTO, Integer> visualizedInheritance;
	private Map<Long, TrackDTO> tracks;

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
		Tags<PlaylistDTO> parents = new Tags.Builder<PlaylistDTO>()
				.captionResolver(p -> p.getName())
				.action("Open", this::open, FontAwesome.ARROW_RIGHT)
				.action("Remove", this::removeParent, FontAwesome.REMOVE)
				.selectHandler(this::visualizeParent)
				.deselectHandler(this::clearVisualization)
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
				.selectHandler(this::visualizeChild)
				.deselectHandler(this::clearVisualization)
				.append(btnAddChild)
				.build();
		playlistBinding.bind(children, PlaylistDTO.Children);
		playlistLayout.addComponent(children);
		
		//Tracks
		playlistLayout.addComponent(new Label("Tracks"));
		trackItemCache = new HashMap<>();
		tracks = new HashMap<>();
		trackTable = new Table();
		trackTable.addStyleName(ValoTheme.TABLE_COMPACT);
		trackTable.addStyleName(ValoTheme.TABLE_SMALL);
		trackTable.setCellStyleGenerator(this);
		trackTable.addContainerProperty(TrackDTO.Number, Integer.class, null);
		trackTable.addContainerProperty(TrackDTO.Name, Label.class, null);
		trackTable.addContainerProperty(TrackDTO.Artists, Label.class, null);
		trackTable.addContainerProperty(TrackDTO.Album, Label.class, null);
		trackTable.addContainerProperty(TrackDTO.AddedBy, Label.class, null);
		trackTable.addContainerProperty(TrackDTO.AddedAt, Date.class, null);
		trackTable.addContainerProperty(TrackDTO.Duration, Integer.class, null);
		trackTable.addContainerProperty(TrackDTO.InheritedFrom, MenuBar.class, null);
		trackTable.addContainerProperty(TrackDTO.BequeathedTo, MenuBar.class, null);
		trackTable.setConverter(TrackDTO.AddedAt, new DateConverter("yyyy-MM-dd"));
		trackTable.setConverter(TrackDTO.Duration, new DurationConverter());
		trackTable.setColumnHeaders(
			FontAwesome.HASHTAG.getHtml(),
			"Name",
			"Artists",
			"Album",
			"Added By",
			"Added At",
			FontAwesome.CLOCK_O.getHtml(),
			FontAwesome.ARROW_UP.getHtml(),
			FontAwesome.ARROW_DOWN.getHtml()
		);
		trackTable.setPageLength(16);
		trackTable.setWidth("1100px");
		trackTable.sort(new Object[] {TrackDTO.Number}, new boolean[] {true});
		playlistLayout.addComponent(trackTable);
		
		//symbol explanations
		HorizontalLayout symbolLayout = new HorizontalLayout();
		symbolLayout.setSpacing(true);
		symbolLayout.addComponent(new Label(FontAwesome.ARROW_UP.getHtml() + " Inherited From", ContentMode.HTML));
		symbolLayout.addComponent(new Label(FontAwesome.ARROW_DOWN.getHtml() + " Bequeathed To", ContentMode.HTML));
		Label inheritanceSingle = new Label(FontAwesome.CIRCLE.getHtml() + " Inherited/Bequeathed Once", ContentMode.HTML);
		inheritanceSingle.addStyleName(ListaideTheme.INHERITANCE_SINGLE_SYMBOL);
		Label inheritanceMulti = new Label(FontAwesome.CIRCLE.getHtml() + " Inherited/Bequeathed Multiple Times", ContentMode.HTML);
		inheritanceMulti.addStyleName(ListaideTheme.INHERITANCE_MULTI_SYMBOL);
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
			rightSideLayout.addComponent(playlistLayout);
			BeanItem<PlaylistDTO> playlistItem = playlistItems.get(playlistId);
			playlistBinding.setItemDataSource(playlistItem);
			currentPlaylist = playlistItem.getBean();
			trackTable.removeAllItems();
			currentPlaylist.getTracks().forEach(track -> {
				//Somehow the track object itself can't be used as itemId
				trackTable.addItem(getTrackItem(track), track.getId());
			});
			trackTable.sort();
		}
	}
	
	private Object[] getTrackItem(TrackDTO trackDTO) {
		Object[] item = trackItemCache.get(trackDTO);
		if(item == null) {
			String artists = trackDTO.getArtists().stream()
					.map(a -> String.format("<a href='%s' target='_blank'>%s</a>", a.getUrl(), a.getName()))
					.collect(Collectors.joining(", "));
			
			item = new Object[] {
				trackDTO.getNumber(),
				Util.createLink(trackDTO.getName(), trackDTO.getUrl()),
				new Label(artists, ContentMode.HTML),
				Util.createLink(trackDTO.getAlbum().getName(), trackDTO.getAlbum().getUrl()),
				Util.createLink(trackDTO.getAddedBy().getId(), trackDTO.getAddedBy().getUrl()),
				trackDTO.getAddedAt(),
				trackDTO.getDuration(),
				getInheritanceCounter(trackDTO.getInheritedFrom()),
				getInheritanceCounter(trackDTO.getBequeathedTo())
			};
			trackItemCache.put(trackDTO, item);
			tracks.put(trackDTO.getId(), trackDTO);
		}
		return item;
	}
	
	private MenuBar getInheritanceCounter(Map<PlaylistDTO, Set<TrackDTO>> inheritance) {
		MenuBar menu = new MenuBar();
		menu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		menu.addStyleName(ValoTheme.MENUBAR_SMALL);
		MenuBar.MenuItem item = menu.addItem(""+inheritance.size(), null);
		inheritance.forEach((p, t) -> item.addItem(p.getName() + " (" + t.size() + ")",  i -> open(p)));
		return menu;
	}
	
	private void removeParent(PlaylistDTO parent) {
		
	}
	
	private void addParent() {
		
	}
	
	private void removeChild(PlaylistDTO child) {
		
	}
	
	private void addChild() {
		
	}
	
	private void visualizeParent(PlaylistDTO parent) {
		visualizedInheritance = track -> track.getInheritedFrom().get(parent) == null ? 0 : track.getInheritedFrom().size();
		trackTable.refreshRowCache();
	}
	
	private void visualizeChild(PlaylistDTO child) {
		visualizedInheritance = track -> track.getBequeathedTo().get(child) == null ? 0 : track.getBequeathedTo().size();
		trackTable.refreshRowCache();
	}
	
	private void clearVisualization(PlaylistDTO playlistDTO) {
		visualizedInheritance = null;
		trackTable.refreshRowCache();
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

	@Override
	public String getStyle(Table source, Object itemId, Object propertyId) {
		if(propertyId == null && visualizedInheritance != null) {
			switch(visualizedInheritance.apply(tracks.get(itemId))) {
				case 0: return null;
				case 1: return ListaideTheme.INHERITANCE_SINGLE;
				default: return ListaideTheme.INHERITANCE_MULTI;
			}
		}
		return null;
	}

}
