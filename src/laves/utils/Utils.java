/**
 * This is part of LAVES - Logistics Algorithms Visualization and Education Software.
 * 
 * Copyright (C) 2020 Jan Dornseifer & Department of Management Information Science, University of Siegen &
 *                    Department for Management Science and Operations Research, Helmut Schmidt University
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * See license/LICENSE.txt for further information.
 */

package laves.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JLabel;

import lavesdk.utils.FileUtils;

/**
 * Contains general utility function as static methods.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Utils {
	
	private Utils() {
	}
	
	/**
	 * Gets the url of the how to file for the specified language id.
	 * 
	 * @param langID the language id
	 * @return the url or <code> null</code> if their is no such how to file available
	 * @since 1.0
	 */
	public static URL getHowToURL(final String langID) {
		try {
			return new File(Constants.PATH_HOWTO + langID + FileUtils.FILESEPARATOR + Constants.FILENAME_HOWTO).toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	/**
	 * Makes a label appear as a link using {@link Constants#LINK_COLOR} and {@link Constants#LINK_HOVER_COLOR}.
	 * 
	 * @param label the label
	 * @param url the url to open or <code>null</code> if the action is set manually
	 * @since 1.0
	 */
	public static void makeLinkLabel(final JLabel label, final String url) {
		ActionListener l = null;
		
		if(url != null && !url.isEmpty()) {
			l = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					openWebsite(url);
				}
			};
		}
		
		makeLinkLabel(label, l);
	}
	
	/**
	 * Makes a label appear as a link using {@link Constants#LINK_COLOR} and {@link Constants#LINK_HOVER_COLOR}.
	 * 
	 * @param label the label
	 * @param listener the listener that is informed when the link is clicked or <code>null</code> if the link does not have an action
	 * @since 1.0
	 */
	public static void makeLinkLabel(final JLabel label, final ActionListener listener) {
		label.setForeground(Constants.LINK_COLOR);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				label.setForeground(Constants.LINK_HOVER_COLOR);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				label.setForeground(Constants.LINK_COLOR);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listener != null)
					listener.actionPerformed(new ActionEvent(label, 1, "link"));
			}
		});
	}
	
	/**
	 * Opens the specified website if possible using the default browser.
	 * 
	 * @param url the url
	 * @return <code>true</code> if the website could be opened otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if url is null</li>
	 * 		<li>if url is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean openWebsite(final String url) throws IllegalArgumentException {
		if(url == null || url.isEmpty())
			throw new IllegalArgumentException("No valid argument!");
		
		if(Desktop.isDesktopSupported()) {
			final Desktop d = Desktop.getDesktop();
			if(d.isSupported(Desktop.Action.BROWSE)) {
				try {
					d.browse(new URI(url));
					return true;
				} catch (IOException | URISyntaxException e) {
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Opens the specified file if possible using the default application of the file extension.
	 * 
	 * @param file the file
	 * @return <code>0</code> if the file does not exist, <code>-1</code> if their is no default application that can open the file or <code>1</code> if the file could be opened
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * 		<li>if file is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public static int openFile(final String file) throws IllegalArgumentException {
		if(file == null || file.isEmpty())
			throw new IllegalArgumentException("No valid argument!");
		
		final File f = new File(file);
		if(!f.exists())
			return 0;
		
		if(Desktop.isDesktopSupported()) {
			final Desktop d = Desktop.getDesktop();
			try {
				d.open(f);
				return 1;
			} catch (IOException e) {
				return -1;
			}
		}
		
		return -1;
	}

}
