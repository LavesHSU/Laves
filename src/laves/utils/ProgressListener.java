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

/**
 * Listener to listen for events of a progress.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface ProgressListener {
	
	/**
	 * The total amount of data to load.
	 * 
	 * @param total amount of data to load
	 * @since 1.0
	 */
	public void totalProgress(int total);
	
	/**
	 * The current progress.
	 * 
	 * @param current the current data that is loaded
	 * @param desc the description
	 * @since 1.0
	 */
	public void currentProgress(int current, final String desc);

}
