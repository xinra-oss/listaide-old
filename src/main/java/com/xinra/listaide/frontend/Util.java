package com.xinra.listaide.frontend;

import java.util.List;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class Util {
	
	/**
	 * Creates an external link (target="_blank").
	 * @param caption The caption (link text)
	 * @param url The target URL
	 */
	public static Label createLink(String caption, String url) {
		String link = String.format("<a href='%s' target='_blank'>%s</a>", url, caption);
		return new Label(link, ContentMode.HTML);
	}
	
	/**
	 * Formats a duration.
	 * @param seconds Duration in milliseconds.
	 */
	public static String formatDuration(long millis) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
//		long hour = (millis / (1000 * 60 * 60)) % 24;
//		return String.format("%02d:%02d:%02d", hour, minute, second);
		return String.format("%02d:%02d", minute, second);
	}
	
	/**
	 * Re-sorts a {@link Grid} like {@link Table#sort()}.
	 */
	public static void sort(Grid grid) {
		List<SortOrder> orders = grid.getSortOrder();
		if(orders.isEmpty()) return;
		Sort sort = Sort.by(orders.get(0).getPropertyId(), orders.get(0).getDirection());
		for(int i = 1; i < orders.size(); i++) {
			sort = sort.then(orders.get(i).getPropertyId(), orders.get(i).getDirection());
		}
		grid.sort(sort);
	}

}
