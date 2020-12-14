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
 * Interface:	WelcomeScreenListener
 * Task:		Listener of a welcome screen
 * Created:		25.04.14
 * LastChanges:	25.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui.widgets;

import lavesdk.algorithm.plugin.AlgorithmPlugin;

/**
 * Listener to listen for events of a {@link WelcomeScreen}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface WelcomeScreenListener {
	
	/**
	 * Indicates that the user double clicks in the algorithm table of the welcome screen to
	 * open the selected algorithm.
	 * 
	 * @param plugin the plugin to activate
	 * @since 1.0
	 */
	public void activatePlugin(final AlgorithmPlugin plugin);

}
