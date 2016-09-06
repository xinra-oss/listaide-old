package com.xinra.listaide.frontend;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;

public class Util {
	
	/**
	 * Creates an external link (target="_blank").
	 * @param caption The caption (link text)
	 * @param url The target URL
	 */
	public static Link createLink(String caption, String url) {
		Link link = new Link(caption, new ExternalResource(url));
		link.setTargetName("_blank");
		return link;
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
