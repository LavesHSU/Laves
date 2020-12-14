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
 * Class:		PluginManager
 * Task:		Manager of plugin installations or deinstallations
 * Created:		29.04.14
 * LastChanges:	09.10.14
 * LastAuthor:	jdornseifer
 */

package laves;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import laves.utils.Constants;
import lavesdk.LAVESDKV;
import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginBundle;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.ValidationReport;
import lavesdk.algorithm.plugin.Validator;
import lavesdk.configuration.Configuration;
import lavesdk.logging.enums.LogType;

/**
 * Manages the installation or deinstallation of plugins.
 * <br><br>
 * Use {@link #getInstalledPlugins()} to get a (read-only) list of all installed plugins. With {@link #addListener(PluginManagerListener)}
 * you can set a listener to listen for events of the plugin manager.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class PluginManager {
	
	/** the parent loader */
	private final Loader loader;
	/** the host application */
	private final PluginHost host;
	/** the plugin bundles that were loaded */
	private final List<PluginBundle> pluginBundles;
	/** the related configurations of the plugins (key=simple name of bundle, value=configuration data) */
	private final Map<String, Configuration> pluginConfigs;
	/** the unmodifiable list of all installed plugins */
	private List<AlgorithmPlugin> installedPlugins;
	/** the list of all listeners */
	private final List<PluginManagerListener> listeners;
	
	/**
	 * Creates a new plugin manager.
	 * 
	 * @param loader the parent loader
	 * @param host the host application
	 * @param bundles the loaded bundles
	 * @param bundleConfigs the loaded bundle configurations
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if loader is null</li>
	 * 		<li>if host is null</li>
	 * 		<li>if bundles is null</li>
	 * 		<li>if bundleConfigs is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public PluginManager(final Loader loader, final PluginHost host, final List<PluginBundle> bundles, final Map<String, Configuration> bundleConfigs) throws IllegalArgumentException {
		if(loader == null || host == null || bundles == null || bundleConfigs == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.loader = loader;
		this.host = host;
		this.pluginBundles = bundles;
		this.pluginConfigs = bundleConfigs;
		this.installedPlugins = null;
		this.listeners = new ArrayList<PluginManagerListener>(3);
	}
	
	/**
	 * Initializes the manager meaning that all loaded plugins are initialized.
	 * 
	 * @return <code>true</code> if the plugins could be initialized successfully or <code>false</code> if their occurred errors during initialization
	 * @since 1.0
	 */
	public boolean initialize() {
		// if the manager is already initialized then quit
		if(installedPlugins != null)
			return false;
		
		final List<AlgorithmPlugin> plugins = new ArrayList<AlgorithmPlugin>();
		AlgorithmRTE rte;
		boolean result = true;
		
		for(PluginBundle bundle : pluginBundles) {
			// check whether the loaded plugin is compatible with the SDK
			if(!LAVESDKV.checkCompatibility(bundle.getPlugin())) {
				loader.logMessage(bundle.getPlugin(), "plugin is not compatible with the current SDK version!", null, LogType.ERROR);
				continue;
			}
			
			try {
				// initialize the plugin first
				bundle.getPlugin().initialize(host, bundle.getResourceLoader(), pluginConfigs.get(bundle.getSimpleName()));
				// register the host at the runtime environment of the plugin
				rte = bundle.getPlugin().getRuntimeEnvironment();
				if(rte != null)
					rte.registerHost(host);
				// if the plugin could be initialized successfully then add it to the list of installed plugins
				plugins.add(bundle.getPlugin());
			}
			catch(Exception e) {
				// catch any exception so that other plugins can be loaded although one of them is broken
				loader.logMessage(bundle.getPlugin(), "plugin could not be initialized!", e, LogType.ERROR);
				result = false;
			}
		}
		
		installedPlugins = Collections.unmodifiableList(plugins);
		
		return result;
	}
	
	/**
	 * Adds a new listener to the list of listeners.
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public void addListener(final PluginManagerListener listener) {
		if(listener == null || listeners.contains(listener))
			return;
		
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener from the list of listeners.
	 * 
	 * @param listener the listener to be removed
	 * @since 1.0
	 */
	public void removeListener(final PluginManagerListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Gets the list of loaded {@link PluginBundle}s.
	 * 
	 * @return the list of available plugin bundles
	 * @since 1.0
	 */
	public final List<PluginBundle> getPluginBundles() {
		return pluginBundles;
	}
	
	/**
	 * Gets a configuration of a plugin.
	 * 
	 * @param bundle the bundle of the plugin
	 * @return the configuration or <code>null</code> if their is no configuration available
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if bundle is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Configuration getPluginConfiguration(final PluginBundle bundle) throws IllegalArgumentException {
		if(bundle == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return (pluginConfigs != null) ? pluginConfigs.get(bundle.getSimpleName()) : null;
	}
	
	/**
	 * Gets the (read-only) list of all installed plugins that are functioning.
	 * 
	 * @see #initializePlugins(PluginHost)
	 * @return the unmodifiable list of all installed plugins
	 * @since 1.0
	 */
	public final List<AlgorithmPlugin> getInstalledPlugins() {
		return installedPlugins;
	}
	
	/**
	 * Installs a new plugin bundle.
	 * 
	 * @param bundle the bundle
	 * @return the validation report
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if bundle is null</li>
	 * 		<li>if loader is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final ValidationReport install(final PluginBundle bundle) throws IllegalArgumentException {
		if(bundle == null)
			throw new IllegalArgumentException("No valid argument!");
		
		int i = 1;
		int cancelIndex = 1000;
		String pluginFileName = bundle.getName();
		
		if(!LAVESDKV.checkCompatibility(bundle.getPlugin()))
			return new ValidationReport(false, "Plugin is not compatible with the installed SDK version!", 1, 0);
		
		// validate the plugin to check whether it has errors
		final ValidationReport vr = Validator.validate(bundle.getPlugin(), true);
		if(!vr.ok)
			return vr;
		
		// check whether the current file name is free
		while(new File(Constants.PATH_PLUGINS + pluginFileName).exists() && i < cancelIndex)
			pluginFileName = bundle.getSimpleName() + "_" + (i++) + ".jar";
		
		if(i >= cancelIndex)
			return new ValidationReport(false, "Plugin could not be installed in the plugin folder!", 1, 0);
		
		try {
			// install the plugin in the plugin folder
			Files.copy(Paths.get(bundle.getFile().getAbsolutePath()), Paths.get(Constants.PATH_PLUGINS + pluginFileName));
			
			// initialize the plugin
			try {
				bundle.getPlugin().initialize(host, bundle.getResourceLoader(), null);
			}
			catch(Exception e) {
				return new ValidationReport(false, "Plugin could not be initialized! " + e.getMessage(), 1, 0);
			}
			
			// add the bundle to the list (the configuration entry does not need to be added because the plugin
			// has not configuration at the beginning)
			pluginBundles.add(bundle);
			
			// add the plugin to the list of installed plugins
			final List<AlgorithmPlugin> modifiablePluginsList = new ArrayList<AlgorithmPlugin>(installedPlugins);
			modifiablePluginsList.add(bundle.getPlugin());
			installedPlugins = Collections.unmodifiableList(modifiablePluginsList);
			
			fireInstalledPluginsChanged(true);
		} catch (IOException e) {
			return new ValidationReport(false, e.getMessage(), 1, 0);
		}

		return new ValidationReport(true, "", 0, 0);
	}
	
	/**
	 * Deinstalls the specified plugin from the manager.
	 * 
	 * @param plugin the plugin to be deinstalled
	 * @return <code>true</code> if the plugin could be deinstalled otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if plugin is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final boolean deinstall(final AlgorithmPlugin plugin) throws IllegalArgumentException {
		if(plugin == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final PluginBundle bundle = findBundle(plugin);
		if(bundle == null)
			return false;
		
		// remove the plugin from the list of installed plugins
		final List<AlgorithmPlugin> modifiablePluginsList = new ArrayList<AlgorithmPlugin>(installedPlugins);
		if(modifiablePluginsList.remove(bundle.getPlugin())) {
			// remove the plugin file from the plugins folder and the corresponding configuration file
			final File cfgFile = new File(Constants.PATH_PLUGINS_CONFIG + bundle.getSimpleName() + Constants.EXT_CONFIG);
			final File pluginFile = new File(bundle.getPath());
			
			// if plugin jar could not be deleted then add a deinstallation entry
			if(!(cfgFile.delete() && pluginFile.delete()))
				loader.addDeinstallationEntry(bundle);
			
			// remove the corresponding bundle and the configuration of this bundle
			pluginBundles.remove(bundle);
			pluginConfigs.remove(bundle.getSimpleName());
			
			// create a new read-only list
			installedPlugins = Collections.unmodifiableList(modifiablePluginsList);
			
			fireInstalledPluginsChanged(false);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Finds the corresponding {@link PluginBundle} to a specified {@link AlgorithmPlugin}.
	 * 
	 * @param plugin the plugin
	 * @return the bundle or <code>null</code> if their is no plugin bundle available for the given plugin
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if plugin is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final PluginBundle findBundle(final AlgorithmPlugin plugin) throws IllegalArgumentException {
		if(plugin == null)
			throw new IllegalArgumentException("No valid argument!");
		
		for(PluginBundle bundle : pluginBundles)
			if(bundle.getPlugin() == plugin)
				return bundle;
		
		return null;
	}
	
	/**
	 * Indicates whether the plugin of a given bundle is installed in the manager.
	 * 
	 * @param bundle the plugin bundle
	 * @return <code>true</code> if the plugin is installed otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if bundle is null</li>
	 * </ul>
	 * @since 1.1
	 */
	public final boolean isBundleInstalled(final PluginBundle bundle) throws IllegalArgumentException {
		if(bundle == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return installedPlugins.contains(bundle.getPlugin());
	}
	
	/**
	 * Fires {@link PluginManagerListener#onInstalledPluginsChanged()}.
	 * 
	 * @param increase <code>true</code> for new installed plugin or <code>false</code> for deinstallation of a plugin
	 * @since 1.0
	 */
	private void fireInstalledPluginsChanged(final boolean increase) {
		for(PluginManagerListener l : listeners)
			l.onInstalledPluginsChanged(increase);
	}

}
