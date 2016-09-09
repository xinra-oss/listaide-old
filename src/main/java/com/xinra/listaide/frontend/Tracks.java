package com.xinra.listaide.frontend;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.DefaultItemSorter.DefaultPropertyValueComparator;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.GeneratedPropertyContainer.GeneratedPropertyItem;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.xinra.listaide.service.AlbumDTO;
import com.xinra.listaide.service.ArtistDTO;
import com.xinra.listaide.service.DurationConverter;
import com.xinra.listaide.service.PlaylistDTO;
import com.xinra.listaide.service.TrackDTO;
import com.xinra.listaide.service.UserDTO;

import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;

/**
 * Displays a list of tracks in a grid. Is capable of highlighting tracks in different levels.
 */
public class Tracks extends CustomField<List<TrackDTO>> {

	private static final long serialVersionUID = 1L;
	
	public static enum HighlightLevel {
		NONE("la-highlight-none-symbol", null),
		SINGLE("la-highlight-single-symbol", "la-highlight-single"),
		MULTI("la-highlight-multi-symbol", "la-highlight-multi");
		
		private final String symbolStyle;
		private final String rowStyle;
		
		private HighlightLevel(String symbolStyle, String rowStyle) {
			this.symbolStyle = symbolStyle;
			this.rowStyle = rowStyle;
		}
		
		public String getSymbolStyle() {
			return symbolStyle;
		}
	}
	
	private static class TrackPropertyComparator extends DefaultPropertyValueComparator {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Object o1, Object o2) {
			if(o1 instanceof UserDTO) {
				o1 = ((UserDTO) o1).getId();
				o2 = ((UserDTO) o2).getId();
			} else if(o1 instanceof List) { //NOTE: needs to be edited if there are more lists!
				o1 = ((List<ArtistDTO>) o1).get(0).getName();
				o2 = ((List<ArtistDTO>) o2).get(0).getName();
			} else if(o1 instanceof AlbumDTO) {
				o1 = ((AlbumDTO) o1).getName();
				o2 = ((AlbumDTO) o2).getName();
			} else if(o1 instanceof Map) { //NOTE: needs to be edited if there are more maps!
				o1 = ((Map<?, ?>) o1).size();
				o2 = ((Map<?, ?>) o2).size();
			}
			return super.compare(o1, o2);
		}
	}
	
	private class InheritanceConverter implements Converter<MenuBar, Map<PlaylistDTO, Set<TrackDTO>>> {

		private static final long serialVersionUID = 1L;

		@Override
		public Map<PlaylistDTO, Set<TrackDTO>> convertToModel(MenuBar value,
				Class<? extends Map<PlaylistDTO, Set<TrackDTO>>> targetType, Locale locale)
						throws com.vaadin.data.util.converter.Converter.ConversionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public MenuBar convertToPresentation(Map<PlaylistDTO, Set<TrackDTO>> value, Class<? extends MenuBar> targetType,
				Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
			
			MenuBar menu = new MenuBar();
			menu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
			MenuBar.MenuItem item = menu.addItem(""+value.size(), null);
			value.forEach((p, t) -> item.addItem(p.getName() + " (" + t.size() + ")",
					inhertitanceClickHandler == null ? null
					: i -> inhertitanceClickHandler.accept(p)));
			//Set fixed with for client-side performance as per https://github.com/datenhahn/componentrenderer/#limitations
			//This is sufficient for a two-digit amount of relations
			menu.setWidth("66px");
			return menu;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<Map<PlaylistDTO, Set<TrackDTO>>> getModelType() {
			return (Class<Map<PlaylistDTO, Set<TrackDTO>>>) (Class<?>) Map.class;
		}

		@Override
		public Class<MenuBar> getPresentationType() {
			return MenuBar.class;
		}
		
	}
	
	private Grid grid;
	private Consumer<PlaylistDTO> inhertitanceClickHandler;
	private BeanItemContainer<TrackDTO> container;
	
	/**
	 * Creates a grid without highlighting
	 */
	public Tracks() {
		this(t -> HighlightLevel.NONE);
	}
	
	public Tracks(Function<TrackDTO, HighlightLevel> highlightLevelResolver) {
		grid = new Grid();
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		
		container = new BeanItemContainer<TrackDTO>(TrackDTO.class) {
			private static final long serialVersionUID = 1L;
			
			//make all properties sortable although they do not implement comparable
			//comparison is handled in TrackPropertyComparator
			@Override
			public Collection<?> getSortablePropertyIds() {
				return getContainerPropertyIds();
			}
		};
		container.setItemSorter(new DefaultItemSorter(new TrackPropertyComparator()));
		//wrap into GeneratedPropertyContainer so that the track name can be a link
		//and to be able to remove properties
		GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(container);
		gpc.addGeneratedProperty(TrackDTO.Name, new PropertyValueGenerator<TrackDTO>() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public TrackDTO getValue(Item item, Object itemId, Object propertyId) {
				return ((BeanItem<TrackDTO>) item).getBean();
			}

			@Override
			public Class<TrackDTO> getType() {
				return TrackDTO.class;
			}
			
			@Override
			public SortOrder[] getSortProperties(SortOrder order) {
				return new SortOrder[] {order};
			}
			
		});
		gpc.removeContainerProperty(TrackDTO.Url);
		gpc.removeContainerProperty(TrackDTO.TrackId);
		gpc.removeContainerProperty(TrackDTO.Id);
		grid.setContainerDataSource(gpc);
		
		grid.getColumn(TrackDTO.Name).setRenderer(new HtmlRenderer(), new LinkConverter<>(TrackDTO.class, TrackDTO::getName, TrackDTO::getUrl));
		grid.getColumn(TrackDTO.Artists).setRenderer(new HtmlRenderer(), new MultiLinkConverter<>(ArtistDTO.class, ArtistDTO::getName, ArtistDTO::getUrl));
		grid.getColumn(TrackDTO.Album).setRenderer(new HtmlRenderer(), new LinkConverter<>(AlbumDTO.class, AlbumDTO::getName, AlbumDTO::getUrl));
		grid.getColumn(TrackDTO.AddedBy).setRenderer(new HtmlRenderer(), new LinkConverter<>(UserDTO.class, UserDTO::getId, UserDTO::getUrl));
		grid.getColumn(TrackDTO.AddedAt).setRenderer(new DateRenderer("%tF"));
		grid.getColumn(TrackDTO.Duration).setConverter(new DurationConverter());
		grid.getColumn(TrackDTO.InheritedFrom).setRenderer(new ComponentRenderer(), new InheritanceConverter());
		grid.getColumn(TrackDTO.BequeathedTo).setRenderer(new ComponentRenderer(), new InheritanceConverter());
		
		grid.setColumnOrder(
			TrackDTO.Number,
			TrackDTO.Name,
			TrackDTO.Artists,
			TrackDTO.Album,
			TrackDTO.AddedBy,
			TrackDTO.AddedAt,
			TrackDTO.Duration,
			TrackDTO.InheritedFrom,
			TrackDTO.BequeathedTo
		);
		
		grid.getDefaultHeaderRow().getCell(TrackDTO.Number).setHtml(FontAwesome.HASHTAG.getHtml());
		grid.getDefaultHeaderRow().getCell(TrackDTO.Duration).setHtml(FontAwesome.CLOCK_O.getHtml());
		grid.getDefaultHeaderRow().getCell(TrackDTO.InheritedFrom).setHtml(FontAwesome.ARROW_UP.getHtml());
		grid.getDefaultHeaderRow().getCell(TrackDTO.BequeathedTo).setHtml(FontAwesome.ARROW_DOWN.getHtml());
		
		grid.setRowStyleGenerator(row -> highlightLevelResolver.apply(getBean(row.getItem())).rowStyle);
	}
	
	/**
	 * Call this when you changed the highlightLevelResolver to update rows that are already visible.
	 */
	public void refreshHighlighting() {
		grid.setRowStyleGenerator(grid.getRowStyleGenerator());
	}
	
	@SuppressWarnings("unchecked")
	private TrackDTO getBean(Item item) {
		return ((BeanItem<TrackDTO>) ((GeneratedPropertyItem) item).getWrappedItem()).getBean();
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	@Override
	protected void setInternalValue(List<TrackDTO> newValue) {
		super.setInternalValue(newValue);
		container.removeAllItems();
		container.addAll(newValue);
		Util.sort(grid);
	}

	@Override
	protected Component initContent() {
		return grid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<TrackDTO>> getType() {
		return (Class<? extends List<TrackDTO>>) (Class<?>) List.class;
	}

}
