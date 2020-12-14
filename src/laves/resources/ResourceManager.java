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

package laves.resources;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * Manages the resources of the application.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class ResourceManager {
	
	/** the logo of LAVES or <code>null</code> if the resource could not be loaded */
	public final ImageIcon LOGO;
	/** the logo of LAVES in small size or <code>null</code> if the resource could not be loaded */
	public final ImageIcon LOGO_SMALL;
	/** the application icon of LAVES or <code>null</code> if the resource could not be loaded */
	public final Image APP_ICON;
	/** the uni siegen logo or <code>null</code> if the resource could not be loaded */
	public final ImageIcon LOGO_UNISIEGEN;
	/** the new icon in big or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_NEW_BIG;
	/** the plugin icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_PLUGIN;
	/** the internet icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_INTERNET;
	/** the help icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_HELP;
	/** the info icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_INFO;
	/** the info icon in large size or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_INFO_LARGE;
	/** the view icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_VIEW;
	/** the plus icon in big or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ICON_PLUGIN_BIG;
	
	/** the resource manager instance */
	private static ResourceManager instance = null;
	
	private ResourceManager() {
		LOGO = getResourceAsIcon("logo.png");
		LOGO_SMALL = getResourceAsIcon("logo_small.png");
		APP_ICON = getResourceAsImage("app_icon.png");
		LOGO_UNISIEGEN = getResourceAsIcon("logo_uni_siegen.jpg");
		ICON_NEW_BIG = getResourceAsIcon("new_big.png");
		ICON_PLUGIN = getResourceAsIcon("plugin.png");
		ICON_INTERNET = getResourceAsIcon("internet.png");
		ICON_HELP = getResourceAsIcon("help.png");
		ICON_INFO = getResourceAsIcon("info.png");
		ICON_INFO_LARGE = getResourceAsIcon("info_large.png");
		ICON_VIEW = getResourceAsIcon("view.png");
		ICON_PLUGIN_BIG = getResourceAsIcon("plugin_big.png");
	}
	
	/**
	 * Gets the instance of the resource manager.
	 * 
	 * @return the resource manager
	 * @since 1.0
	 */
	public static ResourceManager getInstance() {
		if(instance == null)
			instance = new ResourceManager();
		
		return instance;
	}
	
	/**
	 * Gets a resource as an icon.
	 * 
	 * @param name the name of the resource
	 * @return the icon or <code>null</code> if the resource could not be loaded
	 * @since 1.0
	 */
	private ImageIcon getResourceAsIcon(final String name) {
		try {
			return new ImageIcon(ResourceManager.class.getResource(name));
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets a resource as an image.
	 * 
	 * @param name the name of the resource
	 * @return the image or <code>null</code> if the resource could not be loaded
	 * @since 1.0
	 */
	private Image getResourceAsImage(final String name) {
		try {
			return Toolkit.getDefaultToolkit().getImage(ResourceManager.class.getResource(name));
		}
		catch(Exception e) {
			return null;
		}
	}

}
