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

package laves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laves.utils.Constants;
import laves.utils.ProgressListener;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginBundle;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.PluginLoader;
import lavesdk.configuration.Configuration;
import lavesdk.language.LanguageFile;
import lavesdk.logging.LogFile;
import lavesdk.logging.enums.LogType;
import lavesdk.utils.FileUtils;

/**
 * Loader that loads the program data of a LAVES instance. The program data is the installed plugins, the main configuration
 * {@link LoaderData#getMainConfiguration()} or the configurations of all loaded {@link PluginBundle}s.
 * <br><br>
 * {@link #isDataLoaded()} indicates whether the program data could be loaded successfully. Access the data by using {@link #getData()}
 * or get a list of all installed plugins by using {@link #getInstalledPlugins()}.
 * <br><br>
 * <b>Log file</b>:<br>
 * The loader creates a log file using {@link Constants#FILE_LOG}. With {@link #logMessage(String, LogType)} or {@link #logMessage(AlgorithmPlugin, String, Exception, LogType)}
 * you can write a message to the log file.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Loader {

	/** flag that indicates whether the loader has loaded the data */
	private boolean dataLoaded;
	/** the main configuration of LAVES */
	private Configuration mainConfig;
	/** the plugin bundles that were loaded */
	private List<PluginBundle> pluginBundles;
	/** the related configurations of the plugins (key=simple name of bundle, value=configuration data) */
	private Map<String, Configuration> pluginConfigs;
	/** the language file of LAVES */
	private LanguageFile langFile;
	/** the manager of the installed plugins */
	private PluginManager pluginManager;
	/** the log file of the loader (can be <code>null</code>) */
	private final LogFile logFile;
	/** flag that indicates whether the data was loaded with errors */
	private boolean hasErrors;
	
	/** the file name of the deletion file of the plugins */
	private static final String DEINSTALL_PLUGINS_FILENAME = "deinstall_plugins.txt";
	
	/**
	 * Creates a new loader.
	 * 
	 * @since 1.0
	 */
	public Loader() {
		dataLoaded = false;
		mainConfig = null;
		pluginBundles = null;
		pluginConfigs = null;
		langFile = null;
		pluginManager = null;
		hasErrors = false;
		
		// create a log file
		LogFile lf;
		try {
			lf = new LogFile(Constants.FILE_LOG);
		} catch (IOException e) {
			lf = null;
		}
		this.logFile = lf;
	}
	
	/**
	 * Loads the data.
	 * 
	 * @param listener a progress listener
	 * @return the loader data
	 * @since 1.0
	 */
	public void loadData(final ProgressListener listener) {
		// first delete the plugin files that are deinstalled
		deinstallPluginFiles();
		
		// load all available plugins
		pluginBundles = PluginLoader.getInstance().loadPlugins(Constants.PATH_PLUGINS, new lavesdk.utils.ProgressListener() {
			
			private int totalPlugins = 0;
			
			@Override
			public void totalProgress(int total) {
				totalPlugins = total;
				listener.totalProgress(totalPlugins + 3);
			}
			
			@Override
			public void currentProgress(int current) {
				listener.currentProgress(current, "Loading Plugin " + current + "/" + totalPlugins + " ...");
			}
			
		});
		
		int current = pluginBundles.size() + 1;
		
		// load configuration
		listener.currentProgress(current++, "Loading main configuration ...");
		mainConfig = Configuration.load(Constants.FILE_MAIN_CONFIG);
		
		// load plugin configurations
		listener.currentProgress(current++, "Loading plugin configurations ...");
		pluginConfigs = new HashMap<String, Configuration>();
		for(PluginBundle bundle : pluginBundles)
			pluginConfigs.put(bundle.getSimpleName(), Configuration.load(Constants.PATH_PLUGINS_CONFIG + bundle.getSimpleName() + Constants.EXT_CONFIG));
		
		// load language file
		listener.currentProgress(current++, "Loading languages ...");
		try {
			langFile = new LanguageFile(Constants.FILE_LANGUAGE);
		} catch (IOException e) {
			langFile = null;
			logMessage("Loader: Loading languages failed!", LogType.ERROR);
			hasErrors = true;
		}
		
		dataLoaded = true;
	}
	
	/**
	 * Indicates whether the program data is loaded.
	 * 
	 * @return <code>true</code> if the data is loaded otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isDataLoaded() {
		return dataLoaded;
	}
	
	/**
	 * Initializes and validates the plugins so that faulty plugins are removed and only functioning ones can be used.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method can only be called once. To access the installed plugins that were initializable use {@link #getInstalledPlugins()}.
	 *  
	 * @param host the host application
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if host is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void initializePlugins(final PluginHost host) throws IllegalArgumentException {
		// the installed plugins are already initialized?
		if(!dataLoaded || pluginManager != null)
			return;
		
		// create the manager
		pluginManager = new PluginManager(this, host, pluginBundles, pluginConfigs);
		// initialize the plugins
		if(!pluginManager.initialize())
			hasErrors = true;
	}
	
	/**
	 * Gets the plugin manager of the installed plugins.
	 * 
	 * @return the manager or <code>null</code> if the plugins were not initialized yet
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Gets the main configuration of LAVES.
	 * 
	 * @return the main configuration or an empty configuration if their is no main configuration available
	 * @since 1.0
	 */
	public final Configuration getMainConfiguration() {
		return mainConfig;
	}
	
	/**
	 * Gets the language file of LAVES.
	 * 
	 * @return the language file or <code>null</code> if their is no language file available
	 * @since 1.0
	 */
	public final LanguageFile getLanguageFile() {
		return langFile;
	}
	
	/**
	 * Indicates whether it occurred errors during the loading progress.
	 * 
	 * @return <code>true</code> if their are errors (logged in the log file) otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasErrors() {
		return hasErrors;
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param msg the message
	 * @param type the message type
	 * @since 1.0
	 */
	public void logMessage(final String msg, final LogType type) {
		logMessage(null, msg, null, type);
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param plugin the plugin that causes the message or <code>null</code>
	 * @param msg the message
	 * @param e the exception that occurred or <code>null</code>
	 * @param type the message type
	 * @since 1.0
	 */
	public void logMessage(final AlgorithmPlugin plugin, final String msg, final Exception e, final LogType type) {
		if(logFile != null)
			logFile.writeToLog(plugin, msg, e, type);
	}
	
	/**
	 * Adds an entry to the plugin delete file of LAVES.
	 * <br><br>
	 * This has to be done when a plugin jar cannot be removed because the JVM holds a lock on it.
	 * 
	 * @param bundle the bundle of the plugin that should be deleted
	 * @since 1.0
	 */
	void addDeinstallationEntry(final PluginBundle bundle) {
		FileWriter fw = null;
		
		try {
			final File f = FileUtils.createFilePath(Constants.PATH_TEMP + DEINSTALL_PLUGINS_FILENAME);
			fw = new FileWriter(f, true);
			
			// write the file path to the plugin that should be deleted at the end of the file
			fw.write(bundle.getFile().getAbsolutePath() + "\n");
			fw.flush();
		} catch (IOException e) {
		}
		finally {
			if(fw != null) try { fw.close(); } catch(IOException e) { fw = null; }
		}
	}
	
	/**
	 * Deinstalls all plugin files that are listed in the {@link #DEINSTALL_PLUGINS_FILENAME} file.
	 * 
	 * @since 1.0
	 */
	private void deinstallPluginFiles() {
		final File f = new File(Constants.PATH_TEMP + DEINSTALL_PLUGINS_FILENAME);
		BufferedReader br = null;
		String entry;
		
		// no deinstall file? then break up because their is nothing to do
		if(!f.exists())
			return;
		
		// read the deinstallation entries
		try {
			br = new BufferedReader(new FileReader(f));
			
			while((entry = br.readLine()) != null) {
				if(!entry.isEmpty()) {
					final File delFile = new File(entry);
					if(delFile.exists())
						delFile.delete();
				}
			}
		} catch (IOException e) {
		}
		finally {
			if(br != null) try { br.close(); } catch(IOException e) { br = null; }
		}
		
		// delete the deinstallation file
		f.delete();
	}

}
