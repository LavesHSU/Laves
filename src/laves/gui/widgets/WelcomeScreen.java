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
 * Class:		WelcomeScreen
 * Task:		Show a welcome screen
 * Created:		25.04.14
 * LastChanges:	11.11.15
 * LastAuthor:	jdornseifer
 */

package laves.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import laves.PluginManagerListener;
import laves.configuration.MainConfiguration;
import laves.gui.HowToDialog;
import laves.gui.MainWindow;
import laves.resources.ResourceManager;
import laves.utils.Constants;
import laves.utils.Utils;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.views.View;
import lavesdk.language.LanguageFile;

/**
 * Displays a welcome screen the information about LAVES, the last opened plugins and a quick start panel.
 * <br><br>
 * Use {@link #setWelcomeScreenListener(WelcomeScreenListener)} to add a listener to listen for events of the welcome screen.
 * 
 * @author jdornseifer
 * @version 1.4
 * @since 1.0
 */
public class WelcomeScreen extends View {

	private static final long serialVersionUID = 1L;
	
	/** the main configuration */
	private final MainConfiguration config;
	/** the main window */
	private final MainWindow mainWin;
	/** the listener of events or <code>null</code> */
	private WelcomeScreenListener listener;
	/** the listener for plugin manager events */
	private final PluginManagerListener pluginManListener;
	/** the table model of the open recent list */
	private final LastAlgorithmsTableModel lastAlgosTableModel;
	/** the table model of the quick start list */
	private final AlgorithmTableModel quickStartTableModel;

	/**
	 * Creates a new welcome screen.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the welcome screen is closed manually it is set {@link MainConfiguration#setShowWelcomeScreen(boolean)} to <code>false</code>.
	 * 
	 * @param mw the main window
	 * @param cfg the main configuration
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if mw is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if cfg is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public WelcomeScreen(final MainWindow mw, final MainConfiguration cfg) throws NullPointerException {
		super(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_TITLE", mw.getLanguageID(), "Welcome"), true, mw.getLanguageFile(), mw.getLanguageID());

		if(cfg == null)
			throw new IllegalArgumentException("No valid argument!");
		
		config = cfg;
		mainWin = mw;
		listener = null;
		pluginManListener = new PluginManagerListener() {
			
			@Override
			public void onInstalledPluginsChanged(boolean increase) {
				// update the tables if the installed plugins list changed
				if(!increase)
					lastAlgosTableModel.reload();
				quickStartTableModel.reload();
			}
		};
		
		// install the listener
		mainWin.getLoader().getPluginManager().addListener(pluginManListener);
		
		final Font f = UIManager.getFont("Label.font");
		final Font captionFont = f.deriveFont(Font.BOLD, 12.0f);
		final Font linkFont = f.deriveFont(Font.BOLD, 14.0f);
		final Color captionColor = new Color(70, 120, 200);
		
		final SpringLayout contentLayout = new SpringLayout();
		content.setLayout(contentLayout);
		
		content.setBackground(Color.white);
		content.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		final JLabel lblHeadline = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_MESSAGE", mw.getLanguageID(), "Welcome to LAVES!"));
		lblHeadline.setForeground(Constants.COLOR_LOGO);
		lblHeadline.setFont(f.deriveFont(Font.BOLD, 28.0f));
		contentLayout.putConstraint(SpringLayout.NORTH, lblHeadline, 10, SpringLayout.NORTH, content);
		contentLayout.putConstraint(SpringLayout.WEST, lblHeadline, 10, SpringLayout.WEST, content);
		content.add(lblHeadline);
		
		final JLabel lblLogo = new JLabel(ResourceManager.getInstance().LOGO);
		contentLayout.putConstraint(SpringLayout.EAST, lblHeadline, -20, SpringLayout.WEST, lblLogo);
		contentLayout.putConstraint(SpringLayout.NORTH, lblLogo, 10, SpringLayout.NORTH, content);
		contentLayout.putConstraint(SpringLayout.EAST, lblLogo, -10, SpringLayout.EAST, content);
		content.add(lblLogo);
		
		final JLabel lblSubhead = new JLabel("<html>" + LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_SUBHEAD", mw.getLanguageID(), "a software to visualize, animate and educate the execution of algorithms with the goal to facilitate users the understanding of algorithms") + "</html>");
		lblSubhead.setFont(f.deriveFont(14.0f));
		lblSubhead.setVerticalAlignment(SwingConstants.TOP);
		contentLayout.putConstraint(SpringLayout.NORTH, lblSubhead, 6, SpringLayout.SOUTH, lblHeadline);
		contentLayout.putConstraint(SpringLayout.WEST, lblSubhead, 0, SpringLayout.WEST, lblHeadline);
		contentLayout.putConstraint(SpringLayout.EAST, lblSubhead, 0, SpringLayout.EAST, lblHeadline);
		content.add(lblSubhead);
		
		final JLabel lblHowTo = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_HOWTO", mw.getLanguageID(), "HowTo"));
		lblHowTo.setFont(linkFont);
		Utils.makeLinkLabel(lblHowTo, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final HowToDialog howToDlg = new HowToDialog(mainWin);
				howToDlg.setVisible(true);
			}
		});
		contentLayout.putConstraint(SpringLayout.SOUTH, lblSubhead, -30, SpringLayout.NORTH, lblHowTo);
		contentLayout.putConstraint(SpringLayout.NORTH, lblHowTo, 30, SpringLayout.SOUTH, lblLogo);
		contentLayout.putConstraint(SpringLayout.WEST, lblHowTo, 0, SpringLayout.WEST, lblHeadline);
		content.add(lblHowTo);
		
		final JLabel lblHowToSubtitle = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_HOWTO_SUBTITLE", mw.getLanguageID(), "find out how to work with LAVES"));
		contentLayout.putConstraint(SpringLayout.NORTH, lblHowToSubtitle, 6, SpringLayout.SOUTH, lblHowTo);
		contentLayout.putConstraint(SpringLayout.WEST, lblHowToSubtitle, 0, SpringLayout.WEST, lblHeadline);
		content.add(lblHowToSubtitle);
		
		Dimension oDimTitle = lblHowTo.getPreferredSize();
		Dimension oDimSubtitle = lblHowToSubtitle.getPreferredSize();
		
		final JLabel lblSDK = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_SDK", mw.getLanguageID(), "Software Development Kit"));
		lblSDK.setFont(linkFont);
		Utils.makeLinkLabel(lblSDK, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Utils.openWebsite(Constants.LAVESDK_GETTINGSTARTED_WEBSITE))
					JOptionPane.showMessageDialog(mainWin, LanguageFile.getLabel(langFile, "MSG_ERR_OPENWEBSITE", langID, "The website could not be opened!\nEnsure that you have installed a default browser."), LanguageFile.getLabel(langFile, "MSG_ERR_TITLE_OPENWEBSITE", langID, "Open Website"), JOptionPane.ERROR_MESSAGE);
			}
		});
		contentLayout.putConstraint(SpringLayout.NORTH, lblSDK, 0, SpringLayout.NORTH, lblHowTo);
		contentLayout.putConstraint(SpringLayout.WEST, lblSDK, 30, SpringLayout.EAST, (oDimTitle.width > oDimSubtitle.width) ? lblHowTo : lblHowToSubtitle);
		content.add(lblSDK);
		
		final JLabel lblSDKSubtitle = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_SDK_SUBTITLE", mw.getLanguageID(), "getting started with developing own plugins"));
		contentLayout.putConstraint(SpringLayout.NORTH, lblSDKSubtitle, 6, SpringLayout.SOUTH, lblSDK);
		contentLayout.putConstraint(SpringLayout.WEST, lblSDKSubtitle, 0, SpringLayout.WEST, lblSDK);
		content.add(lblSDKSubtitle);
		
		oDimTitle = lblSDK.getPreferredSize();
		oDimSubtitle = lblSDKSubtitle.getPreferredSize();
		
		final JLabel lblChangelog = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_CHANGELOG", mw.getLanguageID(), "Changelog"));
		lblChangelog.setFont(linkFont);
		Utils.makeLinkLabel(lblChangelog, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Utils.openWebsite(Constants.LAVES_CHANGELOG_WEBSITE))
					JOptionPane.showMessageDialog(mainWin, LanguageFile.getLabel(langFile, "MSG_ERR_OPENWEBSITE", langID, "The website could not be opened!\nEnsure that you have installed a default browser."), LanguageFile.getLabel(langFile, "MSG_ERR_TITLE_OPENWEBSITE", langID, "Open Website"), JOptionPane.ERROR_MESSAGE);
			}
		});
		contentLayout.putConstraint(SpringLayout.NORTH, lblChangelog, 0, SpringLayout.NORTH, lblSDK);
		contentLayout.putConstraint(SpringLayout.WEST, lblChangelog, 30, SpringLayout.EAST, (oDimTitle.width > oDimSubtitle.width) ? lblSDK : lblSDKSubtitle);
		content.add(lblChangelog);
		
		final JLabel lblChangelogSubtitle = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_CHANGELOG_SUBTITLE", mw.getLanguageID(), "what's new in the latest version"));
		contentLayout.putConstraint(SpringLayout.NORTH, lblChangelogSubtitle, 6, SpringLayout.SOUTH, lblChangelog);
		contentLayout.putConstraint(SpringLayout.WEST, lblChangelogSubtitle, 0, SpringLayout.WEST, lblChangelog);
		content.add(lblChangelogSubtitle);
		
		final JPanel lastAlgosPanel = new JPanel(new BorderLayout());
		final TitledBorder tbAlgosPanel = BorderFactory.createTitledBorder(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_LASTALGOS", mw.getLanguageID(), "Recently opened algorithms"));
		tbAlgosPanel.setTitleFont(captionFont);
		tbAlgosPanel.setTitleColor(captionColor);
		lastAlgosPanel.setBorder(tbAlgosPanel);
		lastAlgosPanel.setBackground(content.getBackground());
		contentLayout.putConstraint(SpringLayout.NORTH, lastAlgosPanel, 50, SpringLayout.SOUTH, lblHowToSubtitle);
		contentLayout.putConstraint(SpringLayout.WEST, lastAlgosPanel, 0, SpringLayout.WEST, lblHeadline);
		contentLayout.putConstraint(SpringLayout.SOUTH, lastAlgosPanel, -10, SpringLayout.SOUTH, content);
		contentLayout.putConstraint(SpringLayout.EAST, lastAlgosPanel, 400, SpringLayout.WEST, content);
		content.add(lastAlgosPanel);
		
		final JScrollPane scrollPaneLastAlgos = new JScrollPane();
		scrollPaneLastAlgos.setBackground(content.getBackground());
		scrollPaneLastAlgos.getViewport().setBackground(scrollPaneLastAlgos.getBackground());
		lastAlgosPanel.add(scrollPaneLastAlgos, BorderLayout.CENTER);
		
		lastAlgosTableModel = new LastAlgorithmsTableModel();
		final JTable lastAlgosTable = new JTable(lastAlgosTableModel);
		lastAlgosTable.setAutoCreateRowSorter(false);
		lastAlgosTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lastAlgosTable.setBackground(content.getBackground());
		lastAlgosTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && lastAlgosTable.getSelectedRowCount() > 0 && WelcomeScreen.this.listener != null)
					WelcomeScreen.this.listener.activatePlugin(lastAlgosTableModel.getRow(lastAlgosTable.convertRowIndexToModel(lastAlgosTable.getSelectedRow())));
			}
		});
		scrollPaneLastAlgos.setViewportView(lastAlgosTable);
		
		final JPanel quickStartPanel = new JPanel(new BorderLayout());
		final TitledBorder tbQuickStartPanel = BorderFactory.createTitledBorder(LanguageFile.getLabel(mw.getLanguageFile(), "WELCOME_SCREEN_QUICKSTART", mw.getLanguageID(), "QuickStart"));
		tbQuickStartPanel.setTitleFont(captionFont);
		tbQuickStartPanel.setTitleColor(captionColor);
		quickStartPanel.setBorder(tbQuickStartPanel);
		quickStartPanel.setBackground(content.getBackground());
		contentLayout.putConstraint(SpringLayout.NORTH, quickStartPanel, 0, SpringLayout.NORTH, lastAlgosPanel);
		contentLayout.putConstraint(SpringLayout.WEST, quickStartPanel, 20, SpringLayout.EAST, lastAlgosPanel);
		contentLayout.putConstraint(SpringLayout.SOUTH, quickStartPanel, 0, SpringLayout.SOUTH, lastAlgosPanel);
		contentLayout.putConstraint(SpringLayout.EAST, quickStartPanel, -10, SpringLayout.EAST, content);
		content.add(quickStartPanel);
		
		final JScrollPane scrollPaneQuickStart = new JScrollPane();
		scrollPaneQuickStart.setBackground(content.getBackground());
		scrollPaneQuickStart.getViewport().setBackground(scrollPaneQuickStart.getBackground());
		quickStartPanel.add(scrollPaneQuickStart);
		
		quickStartTableModel = new AlgorithmTableModel(cfg, mainWin.getLoader(), mainWin.getLanguageFile(), mainWin.getLanguageID());
		final JTable quickStartTable = new JTable(quickStartTableModel);
		quickStartTableModel.setTable(quickStartTable);
		quickStartTable.setAutoCreateRowSorter(true);
		quickStartTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		quickStartTable.setBackground(content.getBackground());
		quickStartTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && quickStartTable.getSelectedRowCount() > 0 && WelcomeScreen.this.listener != null)
					WelcomeScreen.this.listener.activatePlugin(quickStartTableModel.getRow(quickStartTable.convertRowIndexToModel(quickStartTable.getSelectedRow())));
			}
		});
		scrollPaneQuickStart.setViewportView(quickStartTable);
	}
	
	/**
	 * Sets the welcome screen listener.
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public void setWelcomeScreenListener(final WelcomeScreenListener listener) {
		this.listener = listener;
	}

	@Override
	public void reset() {
	}
	
	@Override
	protected void close() {
		super.close();
		
		config.setShowWelcomeScreen(false);
	}
	
	@Override
	protected void beforeRemove() {
		mainWin.getLoader().getPluginManager().removeListener(pluginManListener);
	}
	
	/**
	 * Table model of the last algorithms table.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private class LastAlgorithmsTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		/** the rows of the table */
		private List<AlgorithmPlugin> rows;
		
		/**
		 * Creates a new open recent table model.
		 * 
		 * @since 1.0
		 */
		public LastAlgorithmsTableModel() {
			rows = mainWin.getLastOpenedAlgorithms();
		}
		
		/**
		 * Reloads the content of the table.
		 * 
		 * @since 1.0
		 */
		public void reload() {
			rows = mainWin.getLastOpenedAlgorithms();
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}
		
		@Override
		public String getColumnName(int column) {
			return LanguageFile.getLabel(WelcomeScreen.this.mainWin.getLanguageFile(), "COLUMN_ALGONAME", WelcomeScreen.this.mainWin.getLanguageID(), "Name");
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}
		
		/**
		 * Gets the row at the specified index.
		 * 
		 * @param row the row index in the model
		 * @return the row
		 * @since 1.0
		 */
		public AlgorithmPlugin getRow(final int row) {
			return rows.get(row);
		}

		@Override
		public Object getValueAt(int row, int column) {
			return rows.get(row).getName();
		}
		
	}

}
