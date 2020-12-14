/**
 * This is part of LAVES - Logistics Algorithms Visualization and Education Software.
 * 
 * Copyright (C) 2014 Jan Dornseifer & Department of Management Information Science, University of Siegen
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
 * 
 *=========================================================================================================
 * 
 * Interface:	PluginManagerListener
 * Task:		The listener to listen for events of a plugin manager 
 * Created:		25.04.14
 * LastChanges:	29.04.14
 * LastAuthor:	jdornseifer
 */

package laves;

/**
 * Listener to listen for events of a {@link PluginManager}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface PluginManagerListener {
	
	/**
	 * Indicates that the list of installed plugins has changed.
	 * 
	 * @param increase <code>true</code> if a new plugin was installed or <code>false</code> if an existing plugin was deinstalled
	 * @since 1.0
	 */
	public void onInstalledPluginsChanged(final boolean increase);

}
