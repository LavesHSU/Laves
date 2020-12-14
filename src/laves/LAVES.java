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
 * Class:		Main
 * Task:		The entry point
 * Created:		22.04.14
 * LastChanges:	30.04.14
 * LastAuthor:	jdornseifer
 */

package laves;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import laves.gui.LoadingScreen;
import laves.gui.MainWindow;
import laves.resources.ResourceManager;

/**
 * The entry point of LAVES.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LAVES {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// set up the ui manager first
				setUpUIManager();
				
				final Loader l = new Loader();
				
				// load the program data and the main GUI
				final MainWindow mw = new LoadingScreen(l).doLoad();
				
				// show the main window
				if(mw != null && l.isDataLoaded())
					mw.setVisible(true);
			}
		});
	}
	
	/**
	 * Updates the {@link UIManager} if necessary.
	 * 
	 * @since 1.0
	 */
	public static void setUpUIManager() {
		final String os = System.getProperty("os.name").toLowerCase();
		
		// firstly set up the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		if(os.contains("mac")) {
			// mac os x uses the application icon in JOptionPane (meaning the java icon) so change the
			// icons to the LAVES logo (small because it is required a 64x64 icon)
			UIManager.put("OptionPane.informationIcon", ResourceManager.getInstance().LOGO_SMALL);
			UIManager.put("OptionPane.errorIcon", ResourceManager.getInstance().LOGO_SMALL);
			UIManager.put("OptionPane.warningIcon", ResourceManager.getInstance().LOGO_SMALL);
			UIManager.put("OptionPane.questionIcon", ResourceManager.getInstance().LOGO_SMALL);
		}
		
	}

}
