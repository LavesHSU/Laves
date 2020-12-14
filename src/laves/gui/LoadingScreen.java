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

package laves.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import laves.Loader;
import laves.resources.ResourceManager;
import laves.utils.ProgressListener;
import lavesdk.logging.enums.LogType;

/**
 * A splash screen to load the program data and the main window.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LoadingScreen extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the loader */
	private final Loader loader;
	/** the loaded main window */
	private MainWindow mainWin;
	/** the information label */
	private final JLabel lblInfo;
	/** the progress bar */
	private final JProgressBar progressBar;
	
	/**
	 * Creates a new splash screen.
	 * 
	 * @param loader the loader to load the program data
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if loader is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LoadingScreen(final Loader loader) throws IllegalArgumentException {
		super();
		
		if(loader == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.loader = loader;
		this.mainWin = null;
		
		// initialize the screen
		setSize(400, 250);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setAlwaysOnTop(true);
		setModal(true);
		
		final Container pane = getContentPane();
		final JPanel content = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		
		pane.setLayout(new BorderLayout());
		content.setBackground(Color.white);
		content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		
		lblInfo = new JLabel("Loading...");
		progressBar = new JProgressBar();
		
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		content.add(new JLabel(ResourceManager.getInstance().LOGO), gbc);
		
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.0f;
		content.add(lblInfo, gbc);
		
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.0f;
		content.add(progressBar, gbc);
		
		pane.add(content, BorderLayout.CENTER);
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				load();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}
	
	/**
	 * Displays the splash screen and loads the program data using the specified {@link Loader}.
	 * 
	 * @return the main window of LAVES
	 * @since 1.0
	 */
	public MainWindow doLoad() {
		// because the splash scrren is model setVisible() waits until the screen is closed
		setVisible(true);
		
		/*
		 * INFO:
		 * load() is invoked when the splash screen becomes visible
		 */
		
		return mainWin;
	}
	
	/**
	 * Loads the program data.
	 * 
	 * @since 1.0
	 */
	private void load() {
		final SwingWorker<MainWindow, String> loadingTask = new SwingWorker<MainWindow, String>() {
			
			private int lastCurrent = 0;
			
			private static final String TOTAL = "TOTAL#";
			private static final String CURRENT = "CURR#";
			
			@Override
			protected MainWindow doInBackground() throws Exception {
				try {
					// let the loader load the program data
					loader.loadData(new ProgressListener() {
						
						@Override
						public void totalProgress(int total) {
							// beside the loader data this thread also loads the GUI and initializes
							// the plugins meaning +2
							publish(TOTAL + (total + 2));
						}
						
						@Override
						public void currentProgress(int current, String desc) {
							lastCurrent = current;
							publish(CURRENT + current, desc);
						}
					});
					
					// load the gui of the main window
					publish("Loading GUI ...");
					mainWin = new MainWindow(loader);
					lastCurrent++;
					publish(CURRENT + lastCurrent);
					
					// initialize the plugins
					publish("Initializing plugins (wait a minute please) ...");
					loader.initializePlugins(mainWin);
					lastCurrent++;
					publish(CURRENT + lastCurrent);
				}
				catch(Exception e) {
					loader.logMessage(null, "Loading the application has failed!", e, LogType.ERROR);
				}
				
				return mainWin;
			}
			
			@Override
			protected void done() {
				LoadingScreen.this.dispose();
			}
			
			@Override
			protected void process(List<String> chunks) {
				for(String chunk : chunks) {
					if(chunk.startsWith(TOTAL))
						progressBar.setMaximum(new Integer(chunk.substring(TOTAL.length(), chunk.length())));
					else if(chunk.startsWith(CURRENT))
						progressBar.setValue(new Integer(chunk.substring(CURRENT.length(), chunk.length())));
					else
						lblInfo.setText(chunk);
				}
			}
			
		};
		loadingTask.execute();
	}

}
