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
 * Class:		InstallNewPluginsDialog
 * Task:		The dialog to install or deinstall plugins
 * Created:		28.04.14
 * LastChanges:	28.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;

import laves.PluginManagerListener;
import laves.configuration.MainConfiguration;
import laves.gui.widgets.AlgorithmTableModel;
import laves.resources.ResourceManager;
import lavesdk.algorithm.plugin.PluginBundle;
import lavesdk.algorithm.plugin.PluginLoader;
import lavesdk.algorithm.plugin.ValidationReport;
import lavesdk.algorithm.plugin.exceptions.InvalidPluginException;
import lavesdk.language.LanguageFile;

/**
 * The dialog to install or deinstall plugins.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class PluginManagerDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the main window */
	private final MainWindow mainWin;
	/** the content panel */
	private final JPanel contentPanel = new JPanel();
	/** the table showing the installed plugins */
	private final JTable table;
	/** the table model */
	private final AlgorithmTableModel tableModel;
	/** the deinstall button */
	private final JButton deinstallBtn;
	/** the listener of the plugin manager */
	private final PluginManagerListener pluginManListener;

	/**
	 * Creates a new plugin manager dialog.
	 * 
	 * @param mw the main window
	 * @param cfg the main configuration
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if mw is null</li>
	 * 		<li>if cfg is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public PluginManagerDialog(final MainWindow mw, final MainConfiguration cfg) throws IllegalArgumentException {
		if(mw == null || cfg == null)
			throw new IllegalArgumentException("No valid argument!");
		
		mainWin = mw;
		pluginManListener = new PluginManagerListener() {
			
			@Override
			public void onInstalledPluginsChanged(boolean increase) {
				tableModel.reload();
				deinstallBtn.setEnabled(false);
			}
		};
		
		// install the listener
		mw.getLoader().getPluginManager().addListener(pluginManListener);
		
		setTitle(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PLUGINMANAGER_TITLE", mainWin.getLanguageID(), "Installed Plugins"));
		setModal(true);
		setSize(cfg.getPluginManagerDialogWidth(), cfg.getPluginManagerDialogHeight());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mainWin.adaptDialog(this);
		
		getContentPane().setLayout(new BorderLayout());
		
		// create a description panel at the top
		final JPanel descPanel = new JPanel(new BorderLayout(10, 10));
		descPanel.setBackground(Color.white);
		descPanel.add(new JLabel(ResourceManager.getInstance().ICON_PLUGIN_BIG), BorderLayout.WEST);
		descPanel.add(new JLabel("<html>" + LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PLUGINMANAGER_DESCRIPTION", mainWin.getLanguageID(), "<b>Install new plugins or deinstall existing ones.</b>") + "</html>"), BorderLayout.CENTER);
		descPanel.add(new JSeparator(), BorderLayout.SOUTH);
		descPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Color.white));
		getContentPane().add(descPanel, BorderLayout.NORTH);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(5, 5));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		tableModel = new AlgorithmTableModel(cfg, mainWin.getLoader(), mainWin.getLanguageFile(), mainWin.getLanguageID(), true);
		table = new JTable(tableModel);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				PluginManagerDialog.this.deinstallBtn.setEnabled(true);
			}
		});
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.EAST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JButton installBtn = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PLUGINMANAGER_BTN_INSTALL", mainWin.getLanguageID(), "Install..."));
		installBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PluginManagerDialog.this.doInstallNew();
			}
		});
		panel.add(installBtn);
		
		deinstallBtn = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PLUGINMANAGER_BTN_DEINSTALL", mainWin.getLanguageID(), "Deinstall"));
		deinstallBtn.setEnabled(false);
		deinstallBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PluginManagerDialog.this.doDeinstallPlugin();
			}
		});
		panel.add(deinstallBtn);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton quitButton = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_BTN_QUIT", mainWin.getLanguageID(), "Quit"));
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PluginManagerDialog.this.dispose();
			}
		});
		buttonPane.add(quitButton);
		getRootPane().setDefaultButton(quitButton);
		
		addWindowListener(new WindowAdapter() {
			
			private boolean closed = false;
			
			@Override
			public void windowClosed(WindowEvent e) {
				windowClosing(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(closed)
					return;
				
				// deinstall the listener
				PluginManagerDialog.this.mainWin.getLoader().getPluginManager().removeListener(PluginManagerDialog.this.pluginManListener);
				
				// save the size of the dialog
				cfg.setPluginManagerDialogWidth(PluginManagerDialog.this.getWidth());
				cfg.setPluginManagerDialogHeight(PluginManagerDialog.this.getHeight());
				
				closed = true;
			}
		});
	}
	
	/**
	 * Installs a new plugin the user can choose.
	 * 
	 * @since 1.0
	 */
	private void doInstallNew() {
		final JFileChooser fc = new JFileChooser();
		final String errMsg = LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_ERR_INVALIDPLUGIN", mainWin.getLanguageID(), "The selected plugin is not valid, therefore it cannot be installed!") + "\n";
		final String errTitle = LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_ERR_TITLE_INVALIDPLUGIN", mainWin.getLanguageID(), "Invalid plugin");
		
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(false);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Plugin (*.jar)", "jar"));
		
		final int result = fc.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			final File pluginFile = fc.getSelectedFile();
			
			if(!pluginFile.getAbsolutePath().toLowerCase().endsWith(".jar"))
				return;
			
			try {
				final PluginBundle bundle = PluginLoader.getInstance().loadPlugin(pluginFile);

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				final ValidationReport vr = mainWin.getLoader().getPluginManager().install(bundle);
				setCursor(Cursor.getDefaultCursor());
				
				if(!vr.ok)
					JOptionPane.showMessageDialog(this, errMsg + vr.message, errTitle, JOptionPane.ERROR_MESSAGE);
			} catch (InvalidPluginException e) {
				JOptionPane.showMessageDialog(this, errMsg + e.getMessage(), errTitle, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Deinstalls the selected plugin.
	 * 
	 * @since 1.0
	 */
	private void doDeinstallPlugin() {
		if(table.getSelectedRow() < 0)
			return;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		final boolean result = mainWin.getLoader().getPluginManager().deinstall(tableModel.getRow(table.convertRowIndexToModel(table.getSelectedRow())));
		setCursor(Cursor.getDefaultCursor());
		
		if(!result)
			JOptionPane.showMessageDialog(this, LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_ERR_DEINSTALLPLUGIN", mainWin.getLanguageID(), "The plugin could not be deinstalled!"), LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_ERR_TITLE_DEINSTALLPLUGIN", mainWin.getLanguageID(), "Deinstall plugin"), JOptionPane.ERROR_MESSAGE);
	}

}
