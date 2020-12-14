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

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

import lavesdk.utils.FileUtils;

/**
 * Contains constants like paths to program data, information, etc.
 * 
 * @author jdornseifer
 * @version 1.4
 * @since 1.0
 */
public class Constants {

	// general information
	/** the name of the application */
	public static final String LAVES = "LAVES";
	/** the full name of the application */
	public static final String LAVES_FULL = "Logistics Algorithms Visualization and Education Software";
	/** the link to the LAVES website */
	public static final String LAVES_WEBSITE = "http://www.wiwi.uni-siegen.de/mis/software/laves.html";
	/** the link to changelog page */
	public static final String LAVES_CHANGELOG_WEBSITE = "http://www.wiwi.uni-siegen.de/mis/software/laves.html#changelog";
	/** the link to getting started page */
	public static final String LAVESDK_GETTINGSTARTED_WEBSITE = "http://www.wiwi.uni-siegen.de/mis/software/lavesdk.html#gettingstarted";
	/** the link to the Plugins Page */
	public static final String PLUGINS_PAGE = "http://www.wiwi.uni-siegen.de/mis/software/laves.html#plugins";
	/** the link to the Department of MIS website */
	public static final String DEPARTEMENT_MIS_WEBSITE = "http://www.wiwi.uni-siegen.de/mis/";
	/**
	 * the version of LAVES
	 * -> the major number is increased when there are significant jumps in functionality, the minor number is incremented when only minor
	 *    features or significant fixes have been added, the major number has to be greater or equal 1 and the minor number has to be greater or equal 0
	 * DO NOT FORGET TO ADJUST THE VERSION_RELEASE (see below)!
	 */
	public static final String VERSION = "1.5";
	/** release date information of the current version: day, month, year */
	public static final int[] VERSION_RELEASE = new int[] { 4, 4, 2016 };
	/** the release date of the current version */
	public static final String RELEASE_DATE;
	/** the contributors of the LAVES project (separated by line breaks "\n") */
	public static final String CONTRIBUTORS = "Jan Dornseifer (jan.dornseifer@student.uni-siegen.de)\nDr. Dominik Kreß (dominik.kress@uni-siegen.de)";
	
	// folders
	/** the path to the application directory (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_APPLICATION = getApplicationDir();
	/** the path to the plugins folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_PLUGINS = PATH_APPLICATION + "plugins" + FileUtils.FILESEPARATOR;
	/** the path to the configuration folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_CONFIG = PATH_APPLICATION + "cfg" + FileUtils.FILESEPARATOR;
	/** the path to the plugins folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_PLUGINS_CONFIG = PATH_CONFIG + "plugins" + FileUtils.FILESEPARATOR;
	/** the path to the language folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_LANGUAGE = PATH_APPLICATION + "lang" + FileUtils.FILESEPARATOR;
	/** the path to the license folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_LICENSE = PATH_APPLICATION + "license" + FileUtils.FILESEPARATOR;
	/** the path to the log file folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_LOG = PATH_APPLICATION + "log" + FileUtils.FILESEPARATOR;
	/** the path to the help file folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_HELP = PATH_LANGUAGE + "help" + FileUtils.FILESEPARATOR;
	/** the path to the temporary directory (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_TEMP = getTmpDirectory();
	/** the path to the how to file folder (ends with a {@link FileUtils#FILESEPARATOR}) */
	public static final String PATH_HOWTO = PATH_LANGUAGE + "howto" + FileUtils.FILESEPARATOR;
	
	// extensions
	/** the extension of a configuration file */
	public static final String EXT_CONFIG = ".cfg";
	
	// files
	/** the main configuration file (including the path) */
	public static final String FILE_MAIN_CONFIG = PATH_CONFIG + "main" + EXT_CONFIG;
	/** the language file (including the path) */
	public static final String FILE_LANGUAGE = PATH_LANGUAGE + "lang.txt";
	/** the license file (including the path) */
	public static final String FILE_LICENSE = PATH_LICENSE + "license.txt";
	/** the log file (including the path) */
	public static final String FILE_LOG = PATH_LOG + "log.txt";
	
	// file names
	/** the how to file name */
	public static final String FILENAME_HOWTO = "howto.html";
	/** the help file name */
	public static final String FILENAME_HELP = "user_guide.pdf";
	
	// colors
	/** the base color of the LAVES logo */
	public static final Color COLOR_LOGO = new Color(90, 133, 212);
	/** the color of a link */
	public static final Color LINK_COLOR = new Color(75, 130, 205);
	/** the hover color of a link */
	public static final Color LINK_HOVER_COLOR = new Color(95, 150, 235);
	
	static {
		final Calendar date = Calendar.getInstance();
		// year, month (zero-based), day
		date.set(VERSION_RELEASE[2], VERSION_RELEASE[1] - 1, VERSION_RELEASE[0]);
		RELEASE_DATE = DateFormat.getDateInstance().format(date.getTime());
	}
	
	private Constants() {
	}
	
	/**
	 * Gets the application directory of LAVES.
	 * 
	 * @return the application directory
	 * @since 1.0
	 */
	private static String getApplicationDir() {
		String dir = new File(".").getAbsolutePath();
		if(!dir.endsWith(FileUtils.FILESEPARATOR))
			dir += FileUtils.FILESEPARATOR;
		
		return dir;
	}
	
	/**
	 * Gets the temporary directory of LAVES.
	 * 
	 * @return the temporary directory
	 * @since 1.0
	 */
	private static String getTmpDirectory() {
		final File f = new File(System.getProperty("java.io.tmpdir"));
		if(f.exists()) {
			String dir = f.getAbsolutePath();
			if(!dir.endsWith(FileUtils.FILESEPARATOR))
				dir += FileUtils.FILESEPARATOR;
			
			return dir;
		}
		else
			return getApplicationDir();
	}

}
