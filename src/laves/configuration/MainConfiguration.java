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
 * Class:		MainConfiguration
 * Task:		Wrapper for the main configuration of LAVES
 * Created:		23.04.14
 * LastChanges:	17.12.14
 * LastAuthor:	jdornseifer
 */

package laves.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import laves.PluginManager;
import laves.gui.NewDialog;
import laves.gui.PluginManagerDialog;
import laves.gui.PreferencesDialog;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginBundle;
import lavesdk.configuration.Configuration;
import lavesdk.language.LabelEntry;

/**
 * A wrapper for the main configuration data.
 * 
 * @author jdornseifer
 * @version 1.3
 * @since 1.0
 */
public class MainConfiguration {
	
	/** the configuration */
	private final Configuration config;
	/** the default language id */
	private final String defLangID;

	/**
	 * Creates a new wrapper for the main configuration.
	 * 
	 * @param config the main configuration
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if config is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MainConfiguration(final Configuration config) throws IllegalArgumentException {
		this(config, null);
	}
	
	/**
	 * Creates a new wrapper for the main configuration.
	 * 
	 * @param config the main configuration
	 * @param availableLangs the available languages or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if config is null</li>
	 * </ul>
	 * @since 1.1
	 */
	public MainConfiguration(final Configuration config, final List<LabelEntry> availableLangs) throws IllegalArgumentException {
		if(config == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.config = config;
		
		if(availableLangs != null) {
			// get the default language id from the JVM
			String defLangTmp = Locale.getDefault().getLanguage().toLowerCase();
			boolean exists = false;
			for(LabelEntry e : availableLangs) {
				if(e.langID.equals(defLangTmp)) {
					exists = true;
					break;
				}
			}
			if(!exists)
				defLangTmp = "en";
			
			defLangID = defLangTmp;
		}
		else
			defLangID = "en";
	}
	
	/**
	 * Gets the main configuration data.
	 * 
	 * @return the main configuration
	 * @since 1.0
	 */
	public Configuration getConfiguration() {
		return config;
	}
	
	/**
	 * Gets the window width of the main window.
	 * 
	 * @return the window width
	 * @since 1.0
	 */
	public int getWindowWidth() {
		return config.getInt("windowWidth", 800);
	}
	
	/**
	 * Sets the window width of the main window.
	 * 
	 * @param width the window width
	 * @since 1.0
	 */
	public void setWindowWidth(final int width) {
		config.addInt("windowWidth", width);
	}
	
	/**
	 * Gets the window height of the main window.
	 * 
	 * @return the window height
	 * @since 1.0
	 */
	public int getWindowHeight() {
		return config.getInt("windowHeight", 600);
	}
	
	/**
	 * Sets the window height of the main window.
	 * 
	 * @param height the window height
	 * @since 1.0
	 */
	public void setWindowHeight(final int height) {
		config.addInt("windowHeight", height);
	}
	
	/**
	 * Gets the x position of the main window.
	 * 
	 * @return the window x position
	 * @since 1.0
	 */
	public int getWindowPosX() {
		return config.getInt("windowPosX", 100);
	}
	
	/**
	 * Sets the x position of the main window.
	 * 
	 * @param x the window x position
	 * @since 1.0
	 */
	public void setWindowPosX(final int x) {
		config.addInt("windowPosX", x);
	}
	
	/**
	 * Gets the y position of the main window.
	 * 
	 * @return the window y position
	 * @since 1.0
	 */
	public int getWindowPosY() {
		return config.getInt("windowPosY", 100);
	}
	
	/**
	 * Sets the y position of the main window.
	 * 
	 * @param y the window y position
	 * @since 1.0
	 */
	public void setWindowPosY(final int y) {
		config.addInt("windowPosY", y);
	}
	
	/**
	 * Indicates whether the main window is maximized.
	 * 
	 * @return <code>true</code> if the main window is maximized otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getWindowMaximized() {
		return config.getBoolean("windowMaximized", false);
	}
	
	/**
	 * Sets whether the main window is maximized.
	 * 
	 * @param maximized <code>true</code> if the main window is maximized otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setWindowMaximized(final boolean maximized) {
		config.addBoolean("windowMaximized", maximized);
	}
	
	/**
	 * Gets the language id.
	 * 
	 * @return the language id
	 * @since 1.0
	 */
	public String getLanguageID() {
		return config.getString("langID", defLangID);
	}
	
	/**
	 * Sets the language id.
	 * 
	 * @param langID the language id
	 * @since 1.0
	 */
	public void setLanguageID(final String langID) {
		config.addString("langID", langID);
	}
	
	/**
	 * Gets the width of the {@link NewDialog}.
	 * 
	 * @return the dialog width
	 * @since 1.0
	 */
	public int getNewDialogWidth() {
		return config.getInt("newDlgWidth", 650);
	}
	
	/**
	 * Sets the width of the {@link NewDialog}.
	 * 
	 * @param width the dialog width
	 * @since 1.0
	 */
	public void setNewDialogWidth(final int width) {
		config.addInt("newDlgWidth", width);
	}
	
	/**
	 * Gets the height of the {@link NewDialog}.
	 * 
	 * @return the dialog height
	 * @since 1.0
	 */
	public int getNewDialogHeight() {
		return config.getInt("newDialogHeight", 550);
	}
	
	/**
	 * Sets the height of the {@link NewDialog}.
	 * 
	 * @param height the dialog height
	 * @since 1.0
	 */
	public void setNewDialogHeight(final int height) {
		config.addInt("newDialogHeight", height);
	}
	
	/**
	 * Gets the width of the {@link PluginManagerDialog}.
	 * 
	 * @return the dialog width
	 * @since 1.0
	 */
	public int getPluginManagerDialogWidth() {
		return config.getInt("plugManDlgWidth", 600);
	}
	
	/**
	 * Sets the width of the {@link PluginManagerDialog}.
	 * 
	 * @param width the dialog width
	 * @since 1.0
	 */
	public void setPluginManagerDialogWidth(final int width) {
		config.addInt("plugManDlgWidth", width);
	}
	
	/**
	 * Gets the height of the {@link PluginManagerDialog}.
	 * 
	 * @return the dialog height
	 * @since 1.0
	 */
	public int getPluginManagerDialogHeight() {
		return config.getInt("plugManDlgHeight", 450);
	}
	
	/**
	 * Sets the height of the {@link PluginManagerDialog}.
	 * 
	 * @param height the dialog height
	 * @since 1.0
	 */
	public void setPluginManagerDialogHeight(final int height) {
		config.addInt("plugManDlgHeight", height);
	}
	
	/**
	 * Gets the width of the {@link PreferencesDialog}.
	 * 
	 * @return the dialog width
	 * @since 1.0
	 */
	public int getPreferencesDialogWidth() {
		return config.getInt("prefsDlgWidth", 650);
	}
	
	/**
	 * Sets the width of the {@link PreferencesDialog}.
	 * 
	 * @param width the dialog width
	 * @since 1.0
	 */
	public void setPreferencesDialogWidth(final int width) {
		config.addInt("prefsDlgWidth", width);
	}
	
	/**
	 * Gets the height of the {@link PreferencesDialog}.
	 * 
	 * @return the dialog height
	 * @since 1.0
	 */
	public int getPreferencesDialogHeight() {
		return config.getInt("prefsDlgHeight", 550);
	}
	
	/**
	 * Sets the height of the {@link PreferencesDialog}.
	 * 
	 * @param height the dialog height
	 * @since 1.0
	 */
	public void setPreferencesDialogHeight(final int height) {
		config.addInt("prefsDlgHeight", height);
	}
	
	/**
	 * Gets the preferred width of the algorithm name column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoNameWidth() {
		return config.getInt("columnAlgoNameWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the algorithm name column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoNameWidth(final int width) {
		config.addInt("columnAlgoNameWidth", width);
	}
	
	/**
	 * Indicates whether the problem affiliation column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoProblemAffiliationVisible() {
		return config.getBoolean("columnAlgoProbAffilVisible", true);
	}
	
	/**
	 * Sets whether the problem affiliation column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoProblemAffiliationVisible(final boolean visible) {
		config.addBoolean("columnAlgoProbAffilVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the problem affiliation column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoProblemAffiliationWidth() {
		return config.getInt("columnAlgoProbAffilWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the problem affiliation column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoProblemAffiliationWidth(final int width) {
		config.addInt("columnAlgoProbAffilWidth", width);
	}
	
	/**
	 * Indicates whether the subject column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoSubjectVisible() {
		return config.getBoolean("columnAlgoSubjectVisible", false);
	}
	
	/**
	 * Sets whether the subject column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoSubjectVisible(final boolean visible) {
		config.addBoolean("columnAlgoSubjectVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the subject column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoSubjectWidth() {
		return config.getInt("columnAlgoSubjectWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the subject column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoSubjectWidth(final int width) {
		config.addInt("columnAlgoSubjectWidth", width);
	}
	
	/**
	 * Indicates whether the algorithm type column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoTypeVisible() {
		return config.getBoolean("columnAlgoTypeVisible", false);
	}
	
	/**
	 * Sets whether the algorithm type column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoTypeVisible(final boolean visible) {
		config.addBoolean("columnAlgoTypeVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the algorithm type column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoTypeWidth() {
		return config.getInt("columnAlgoTypeWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the algorithm type column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoTypeWidth(final int width) {
		config.addInt("columnAlgoTypeWidth", width);
	}
	
	/**
	 * Indicates whether the algorithm author column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoAuthorVisible() {
		return config.getBoolean("columnAlgoAuthorVisible", false);
	}
	
	/**
	 * Sets whether the algorithm author column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoAuthorVisible(final boolean visible) {
		config.addBoolean("columnAlgoAuthorVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the algorithm author column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoAuthorWidth() {
		return config.getInt("columnAlgoAuthorWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the algorithm author column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoAuthorWidth(final int width) {
		config.addInt("columnAlgoAuthorWidth", width);
	}
	
	/**
	 * Indicates whether the algorithm author contact column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoAuthorContactVisible() {
		return config.getBoolean("columnAlgoAuthorContactVisible", false);
	}
	
	/**
	 * Sets whether the algorithm author contact column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoAuthorContactVisible(final boolean visible) {
		config.addBoolean("columnAlgoAuthorContactVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the algorithm author contact column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoAuthorContactWidth() {
		return config.getInt("columnAlgoAuthorContactWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the algorithm author contact column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoAuthorContactWidth(final int width) {
		config.addInt("columnAlgoAuthorContactWidth", width);
	}
	
	/**
	 * Indicates whether the algorithm version column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoVersionVisible() {
		return config.getBoolean("columnAlgoVersionVisible", true);
	}
	
	/**
	 * Sets whether the algorithm version column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoVersionVisible(final boolean visible) {
		config.addBoolean("columnAlgoVersionVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the algorithm version column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoVersionWidth() {
		return config.getInt("columnAlgoVersionWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the algorithm version column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoVersionWidth(final int width) {
		config.addInt("columnAlgoVersionWidth", width);
	}
	
	/**
	 * Indicates whether the used sdk version column in the {@link NewDialog} should be visible.
	 * 
	 * @return <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getColumnAlgoSDKVersionVisible() {
		return config.getBoolean("columnAlgoSDKVersionVisible", false);
	}
	
	/**
	 * Sets whether the used sdk version column in the {@link NewDialog} should be visible.
	 * 
	 * @param visible <code>true</code> if the column should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setColumnAlgoSDKVersionVisible(final boolean visible) {
		config.addBoolean("columnAlgoSDKVersionVisible", visible);
	}
	
	/**
	 * Gets the preferred width of the used sdk version column in the {@link NewDialog}.
	 * 
	 * @return the column width
	 * @since 1.0
	 */
	public int getColumnAlgoSDKVersionWidth() {
		return config.getInt("columnAlgoSDKVersionWidth", 0);
	}
	
	/**
	 * Sets the preferred width of the used sdk version column in the {@link NewDialog}.
	 * 
	 * @param width the column width
	 * @since 1.0
	 */
	public void setColumnAlgoSDKVersionWidth(final int width) {
		config.addInt("columnAlgoSDKVersionWidth", width);
	}
	
	/**
	 * Indicates whether the welcome screen should be visible.
	 * 
	 * @return <code>true</code> if the welcome screen should be visible when the application is started otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getShowWelcomeScreen() {
		return config.getBoolean("showWelcomeScreen", true);
	}
	
	/**
	 * Sets whether the welcome screen should be visible.
	 * 
	 * @param show <code>true</code> if the welcome screen should be visible when the application is started otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setShowWelcomeScreen(final boolean show) {
		config.addBoolean("showWelcomeScreen", show);
	}
	
	/**
	 * Indicates whether the information bar of the plugins should be disabled permanently.
	 * 
	 * @return <code>true</code> if the information bar should be disabled permanently otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getDisableInformationBarPermanently() {
		return config.getBoolean("disableInfoBarPermanently", false);
	}
	
	/**
	 * Sets whether the information bar of the plugins should be disabled permanently.
	 * 
	 * @param disable <code>true</code> if the information bar should be disabled permanently otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setDisableInformationBarPermanently(final boolean disable) {
		config.addBoolean("disableInfoBarPermanently", disable);
	}
	
	/**
	 * Gets the list of the last opened algorithms.
	 * 
	 * @param manager the plugin manager of the application
	 * @return the last opened algorithms
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if manager is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public List<AlgorithmPlugin> getLastOpenedAlgorithms(final PluginManager manager) throws IllegalArgumentException {
		if(manager == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<String> list = config.getList("lastOpenedAlgorithms", new ArrayList<String>(0));
		final List<AlgorithmPlugin> res = new ArrayList<AlgorithmPlugin>(list.size());
		
		// add the algorithms of the corresponding bundles
		for(int i = 0; i < list.size(); i++) {
			for(PluginBundle bundle : manager.getPluginBundles()) {
				// if the corresponding bundle is found then check whether this bundle is even installed
				if(bundle.getSimpleName().equals(list.get(i)) && manager.isBundleInstalled(bundle)) {
					res.add(bundle.getPlugin());
					break;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Sets the list of the last opened algorithms.
	 * 
	 * @param algorithms the last opened algorithms
	 * @param manager the plugin manager
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if algorithms is null</li>
	 * 		<li>if manager is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setLastOpenedAlgorithms(final List<AlgorithmPlugin> algorithms, final PluginManager manager) throws IllegalArgumentException {
		if(algorithms == null || manager == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<String> res = new ArrayList<String>(algorithms.size());
		PluginBundle bundle;
		
		for(int i = 0; i < algorithms.size(); i++) {
			bundle = manager.findBundle(algorithms.get(i));
			if(bundle != null)
				res.add(bundle.getSimpleName());
		}
		
		config.addList("lastOpenedAlgorithms", res);
	}
	
	/**
	 * Gets the last opened algorithms count.
	 * 
	 * @return the last opened algorithms count
	 * @since 1.0
	 */
	public int getLastOpendAlgorithmsCount() {
		return config.getInt("lastOpenedAlgorithmsCount", 3);
	}
	
	/**
	 * Sets the last opened algorithms count.
	 * 
	 * @param count the last opened algorithms count
	 * @since 1.0
	 */
	public void setLastOpenedAlgorithmsCount(final int count) {
		config.addInt("lastOpenedAlgorithmsCount", count);
	}
	
	/**
	 * Indicates whether the information dialog of the exercise mode of an algorithm plugin show be shown.
	 * 
	 * @return <code>true</code> if the dialog should be shown otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean getShowExerciseModeInfoDialog() {
		return config.getBoolean("showExerciseModeInfoDialog", true);
	}
	
	/**
	 * Sets whether the information dialog of the exercise mode of an algorithm plugin show be shown.
	 * 
	 * @param show <code>true</code> if the dialog should be shown otherwise <code>false</code>
	 * @since 1.1
	 */
	public void setShowExerciseModeInfoDialog(final boolean show) {
		config.addBoolean("showExerciseModeInfoDialog", show);
	}
	
	/**
	 * Indicates whether the "pause before stop"-option of the toolbar is selected.
	 * 
	 * @return <code>true</code> if the option is selected otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean getToolBarOptPauseBeforeStopSelected() {
		return config.getBoolean("toolBarOptPauseBeforeStopSelected", false);
	}
	
	/**
	 * Sets whether the "pause before stop"-option of the toolbar is selected.
	 * 
	 * @param selected <code>true</code> if the option is selected otherwise <code>false</code>
	 * @since 1.1
	 */
	public void setToolBarOptPauseBeforeStopSelected(final boolean selected) {
		config.addBoolean("toolBarOptPauseBeforeStopSelected", selected);
	}
	
	/**
	 * Indicates whether the "skip breakpoints"-option of the toolbar is selected.
	 * 
	 * @return <code>true</code> if the option is selected otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean getToolBarOptSkipBreakpointsSelected() {
		return config.getBoolean("toolBarOptSkipBreakpointsSelected", false);
	}
	
	/**
	 * Sets whether the "skip breakpoints"-option of the toolbar is selected.
	 * 
	 * @param selected <code>true</code> if the option is selected otherwise <code>false</code>
	 * @since 1.1
	 */
	public void setToolBarOptSkipBreakpointsSelected(final boolean selected) {
		config.addBoolean("toolBarOptSkipBreakpointsSelected", selected);
	}

}
