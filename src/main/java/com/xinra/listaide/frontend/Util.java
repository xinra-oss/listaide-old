package com.xinra.listaide.frontend;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

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

}
