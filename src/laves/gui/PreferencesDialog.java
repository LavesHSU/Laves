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
 * Class:		PreferencesDialog
 * Task:		The preferences dialog
 * Created:		28.04.14
 * LastChanges:	08.07.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.UIManager;

import laves.configuration.MainConfiguration;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.gui.widgets.PropertiesList;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.language.LabelEntry;
import lavesdk.language.LanguageFile;

/**
 * The preferences dialog of LAVES.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the main window */
	private final MainWindow mainWin;
	/** the configuration */
	private final MainConfiguration config;
	/** the content panel */
	private final JPanel contentPanel = new JPanel();
	/** the combobox with the available languages */
	private final JComboBox<String> cboLanguages;
	/** the checkbox to set whether the welcome screen should be shown */
	private final JCheckBox cbShowWelcomeScreen;
	/** the checkbox to set whether the exercise mode info dialog should be shown */
	private final JCheckBox cbShowExerciseModeInfoDlg;
	/** the checkbox to set whether the info bar should be disabled permanently */
	private final JCheckBox cbDisableInfoBarPermanently;
	/** the checkbox to set whether the algorithm problem affiliation column should be displayed */
	private final JCheckBox cbAlgoProblemAffiliation;
	/** the checkbox to set whether the algorithm subject column should be displayed */
	private final JCheckBox cbAlgoSubject;
	/** the checkbox to set whether the algorithm type column should be displayed */
	private final JCheckBox cbAlgoType;
	/** the checkbox to set whether the algorithm author column should be displayed */
	private final JCheckBox cbAlgoAuthor;
	/** the checkbox to set whether the algorithm author contact column should be displayed */
	private final JCheckBox cbAlgoAuthorContact;
	/** the checkbox to set whether the algorithm version column should be displayed */
	private final JCheckBox cbAlgoVersion;
	/** the checkbox to set whether the algorithm used sdk version column should be displayed */
	private final JCheckBox cbUsedSDKVersion;
	/** the slider to configure the open recent count */
	private final JSlider openRecentCountSlider;
	/** the combobox that contains the plugins that can be customized */
	private final JComboBox<String> cboPlugins;
	/** list of the original indices of the customizable plugins */
	private final List<Integer> cboPluginsIndices;
	/** the properties list of the customizable plugins */
	private final PropertiesList propsList;
	/** the model of the {@link #propsList} */
	private final CustomizationListModel propsListModel;

	/**
	 * Creates a new preferences dialog.
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
	public PreferencesDialog(final MainWindow mw, final MainConfiguration cfg) throws IllegalArgumentException {
		if(mw == null || cfg == null)
			throw new IllegalArgumentException("No valid argument!");
		
		mainWin = mw;
		config = cfg;
		
		mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		final Font f = UIManager.getFont("Label.font");
		final Font infoFont = f.deriveFont(Font.ITALIC);
		
		setTitle(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_TITLE", mainWin.getLanguageID(), "Preferences"));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(config.getPreferencesDialogWidth(), config.getPreferencesDialogHeight());
		mainWin.adaptDialog(this);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, tabbedPane, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, tabbedPane, 5, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, tabbedPane, -5, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, tabbedPane, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(tabbedPane);
		
		JPanel generalPanel = new JPanel();
		tabbedPane.addTab(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN", mainWin.getLanguageID(), "Main"), null, generalPanel, null);
		SpringLayout sl_generalPanel = new SpringLayout();
		generalPanel.setLayout(sl_generalPanel);
		
		JLabel lblLanguage = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_LANGUAGE", mainWin.getLanguageID(), "Language:"));
		sl_generalPanel.putConstraint(SpringLayout.WEST, lblLanguage, 10, SpringLayout.WEST, generalPanel);
		generalPanel.add(lblLanguage);
		
		cboLanguages = new JComboBox<String>();
		loadLanguages();
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cboLanguages, 5, SpringLayout.NORTH, generalPanel);
		sl_generalPanel.putConstraint(SpringLayout.NORTH, lblLanguage, 3, SpringLayout.NORTH, cboLanguages);
		sl_generalPanel.putConstraint(SpringLayout.EAST, cboLanguages, -10, SpringLayout.EAST, generalPanel);
		generalPanel.add(cboLanguages);

		JLabel lblLanguageInfo = new JLabel("<html>" + LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_LANGUAGE_INFO", mainWin.getLanguageID(), "The selected language refers to the user interface of the software and does not have to be supported by each installed plugin!") + "</html>");
		lblLanguageInfo.setFont(infoFont);
		sl_generalPanel.putConstraint(SpringLayout.NORTH, lblLanguageInfo, 6, SpringLayout.SOUTH, cboLanguages);
		sl_generalPanel.putConstraint(SpringLayout.WEST, lblLanguageInfo, 0, SpringLayout.WEST, cboLanguages);
		sl_generalPanel.putConstraint(SpringLayout.EAST, lblLanguageInfo, -10, SpringLayout.EAST, generalPanel);
		generalPanel.add(lblLanguageInfo);
		
		JSeparator sep1 = new JSeparator();
		sl_generalPanel.putConstraint(SpringLayout.NORTH, sep1, 5, SpringLayout.SOUTH, lblLanguageInfo);
		sl_generalPanel.putConstraint(SpringLayout.WEST, sep1, 10, SpringLayout.WEST, generalPanel);
		sl_generalPanel.putConstraint(SpringLayout.EAST, sep1, -10, SpringLayout.EAST, generalPanel);
		generalPanel.add(sep1);
		
		cbShowWelcomeScreen = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_SHOWWELCOMESCREEN", mainWin.getLanguageID(), "Show the Welcome Screen?"));
		cbShowWelcomeScreen.setSelected(config.getShowWelcomeScreen());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbShowWelcomeScreen, 6, SpringLayout.SOUTH, sep1);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbShowWelcomeScreen, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbShowWelcomeScreen);
		
		cbShowExerciseModeInfoDlg = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_SHOWEXERCISEMODEINFODLG", mainWin.getLanguageID(), "Show the Exercise Mode Information Dialog?"));
		cbShowExerciseModeInfoDlg.setSelected(config.getShowExerciseModeInfoDialog());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbShowExerciseModeInfoDlg, 6, SpringLayout.SOUTH, cbShowWelcomeScreen);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbShowExerciseModeInfoDlg, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbShowExerciseModeInfoDlg);
		
		cbDisableInfoBarPermanently = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_DISABLEINFOBAR", mainWin.getLanguageID(), "Disable the information bar permanently?"));
		cbDisableInfoBarPermanently.setSelected(config.getDisableInformationBarPermanently());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbDisableInfoBarPermanently, 6, SpringLayout.SOUTH, cbShowExerciseModeInfoDlg);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbDisableInfoBarPermanently, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbDisableInfoBarPermanently);
		
		JSeparator sep2 = new JSeparator();
		sl_generalPanel.putConstraint(SpringLayout.NORTH, sep2, 6, SpringLayout.SOUTH, cbDisableInfoBarPermanently);
		sl_generalPanel.putConstraint(SpringLayout.WEST, sep2, 0, SpringLayout.WEST, sep1);
		sl_generalPanel.putConstraint(SpringLayout.EAST, sep2, 0, SpringLayout.EAST, sep1);
		generalPanel.add(sep2);
		
		JLabel lblColumns = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_COLUMNS", mainWin.getLanguageID(), "Which columns should be displayed in the algorithms table?"));
		sl_generalPanel.putConstraint(SpringLayout.NORTH, lblColumns, 6, SpringLayout.SOUTH, sep2);
		sl_generalPanel.putConstraint(SpringLayout.WEST, lblColumns, 0, SpringLayout.WEST, lblLanguage);
		generalPanel.add(lblColumns);
		
		cbAlgoProblemAffiliation = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOPROBLEMAFFILIATION", mainWin.getLanguageID(), "Problem Affiliation"));
		cbAlgoProblemAffiliation.setSelected(config.getColumnAlgoProblemAffiliationVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoProblemAffiliation, 6, SpringLayout.SOUTH, lblColumns);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoProblemAffiliation, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbAlgoProblemAffiliation);
		
		cbAlgoSubject = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOSUBJECT", mainWin.getLanguageID(), "Subject"));
		cbAlgoSubject.setSelected(config.getColumnAlgoSubjectVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoSubject, 6, SpringLayout.SOUTH, cbAlgoProblemAffiliation);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoSubject, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbAlgoSubject);
		
		cbAlgoType = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOTYPE", mainWin.getLanguageID(), "Type"));
		cbAlgoType.setSelected(config.getColumnAlgoTypeVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoType, 6, SpringLayout.SOUTH, cbAlgoSubject);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoType, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbAlgoType);
		
		cbAlgoAuthor = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOAUTHOR", mainWin.getLanguageID(), "Author"));
		cbAlgoAuthor.setSelected(config.getColumnAlgoAuthorVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoAuthor, 6, SpringLayout.SOUTH, cbAlgoType);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoAuthor, 0, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbAlgoAuthor);
		
		cbAlgoAuthorContact = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOAUTHORCONTACT", mainWin.getLanguageID(), "Author Contact Details"));
		cbAlgoAuthorContact.setSelected(config.getColumnAlgoAuthorContactVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoAuthorContact, 0, SpringLayout.NORTH, cbAlgoProblemAffiliation);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoAuthorContact, 250, SpringLayout.WEST, cboLanguages);
		generalPanel.add(cbAlgoAuthorContact);
		
		cbAlgoVersion = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOVERSION", mainWin.getLanguageID(), "Version"));
		cbAlgoVersion.setSelected(config.getColumnAlgoVersionVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbAlgoVersion, 0, SpringLayout.NORTH, cbAlgoSubject);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbAlgoVersion, 0, SpringLayout.WEST, cbAlgoAuthorContact);
		generalPanel.add(cbAlgoVersion);
		
		cbUsedSDKVersion = new JCheckBox(LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOSDKVERSION", mainWin.getLanguageID(), "Used SDK Version"));
		cbUsedSDKVersion.setSelected(config.getColumnAlgoSDKVersionVisible());
		sl_generalPanel.putConstraint(SpringLayout.NORTH, cbUsedSDKVersion, 0, SpringLayout.NORTH, cbAlgoType);
		sl_generalPanel.putConstraint(SpringLayout.WEST, cbUsedSDKVersion, 0, SpringLayout.WEST, cbAlgoAuthorContact);
		generalPanel.add(cbUsedSDKVersion);
		
		JSeparator sep3 = new JSeparator();
		sl_generalPanel.putConstraint(SpringLayout.NORTH, sep3, 6, SpringLayout.SOUTH, cbAlgoAuthor);
		sl_generalPanel.putConstraint(SpringLayout.WEST, sep3, 0, SpringLayout.WEST, sep1);
		sl_generalPanel.putConstraint(SpringLayout.EAST, sep3, 0, SpringLayout.EAST, sep1);
		generalPanel.add(sep3);
		
		JLabel lblOpenRecentCount = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_OPENRECENTCOUNT", mainWin.getLanguageID(), "Open Recent Count:"));
		sl_generalPanel.putConstraint(SpringLayout.WEST, cboLanguages, 30, SpringLayout.EAST, lblOpenRecentCount);
		sl_generalPanel.putConstraint(SpringLayout.WEST, lblOpenRecentCount, 0, SpringLayout.WEST, lblLanguage);
		generalPanel.add(lblOpenRecentCount);
		
		openRecentCountSlider = new JSlider();
		openRecentCountSlider.setMajorTickSpacing(2);
		openRecentCountSlider.setMaximum(10);
		openRecentCountSlider.setPaintLabels(true);
		openRecentCountSlider.setPaintTicks(true);
		openRecentCountSlider.setValue(config.getLastOpendAlgorithmsCount());
		sl_generalPanel.putConstraint(SpringLayout.WEST, openRecentCountSlider, 0, SpringLayout.WEST, cboLanguages);
		sl_generalPanel.putConstraint(SpringLayout.NORTH, lblOpenRecentCount, 5, SpringLayout.NORTH, openRecentCountSlider);
		sl_generalPanel.putConstraint(SpringLayout.NORTH, openRecentCountSlider, 6, SpringLayout.SOUTH, sep3);
		generalPanel.add(openRecentCountSlider);
		
		JLabel lblOpenRecentCountInfo = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_MAIN_OPENRECENTCOUNT_INFO", mainWin.getLanguageID(), "Specify how many algorithms should be listed in the history."));
		lblOpenRecentCountInfo.setFont(infoFont);
		sl_generalPanel.putConstraint(SpringLayout.NORTH, lblOpenRecentCountInfo, 6, SpringLayout.SOUTH, openRecentCountSlider);
		sl_generalPanel.putConstraint(SpringLayout.WEST, lblOpenRecentCountInfo, 0, SpringLayout.WEST, openRecentCountSlider);
		sl_generalPanel.putConstraint(SpringLayout.EAST, lblOpenRecentCountInfo, -10, SpringLayout.EAST, generalPanel);
		generalPanel.add(lblOpenRecentCountInfo);
		
		JPanel customizePanel = new JPanel();
		tabbedPane.addTab(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_CUSTOMIZE", mainWin.getLanguageID(), "Customize"), null, customizePanel, null);
		SpringLayout sl_customizePanel = new SpringLayout();
		customizePanel.setLayout(sl_customizePanel);
		
		JLabel lblSelectPlugin = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_CUSTOMIZE_SELECTPLUGIN", mainWin.getLanguageID(), "Select a plugin:"));
		sl_customizePanel.putConstraint(SpringLayout.WEST, lblSelectPlugin, 10, SpringLayout.WEST, customizePanel);
		customizePanel.add(lblSelectPlugin);
		
		cboPlugins = new JComboBox<String>();
		cboPluginsIndices = loadCustomizablePlugins();
		cboPlugins.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					loadPluginCustomization();
			}
		});
		sl_customizePanel.putConstraint(SpringLayout.WEST, cboPlugins, 20, SpringLayout.EAST, lblSelectPlugin);
		sl_customizePanel.putConstraint(SpringLayout.EAST, cboPlugins, -10, SpringLayout.EAST, customizePanel);
		sl_customizePanel.putConstraint(SpringLayout.NORTH, lblSelectPlugin, 3, SpringLayout.NORTH, cboPlugins);
		sl_customizePanel.putConstraint(SpringLayout.NORTH, cboPlugins, 10, SpringLayout.NORTH, customizePanel);
		customizePanel.add(cboPlugins);
		
		JSeparator separator = new JSeparator();
		sl_customizePanel.putConstraint(SpringLayout.NORTH, separator, 6, SpringLayout.SOUTH, cboPlugins);
		sl_customizePanel.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.WEST, customizePanel);
		sl_customizePanel.putConstraint(SpringLayout.EAST, separator, -10, SpringLayout.EAST, customizePanel);
		customizePanel.add(separator);
		
		propsListModel = new CustomizationListModel();
		propsList = new PropertiesList(propsListModel);
		sl_customizePanel.putConstraint(SpringLayout.NORTH, propsList, 6, SpringLayout.SOUTH, separator);
		sl_customizePanel.putConstraint(SpringLayout.WEST, propsList, 0, SpringLayout.WEST, lblSelectPlugin);
		sl_customizePanel.putConstraint(SpringLayout.EAST, propsList, 0, SpringLayout.EAST, cboPlugins);
		customizePanel.add(propsList);
		
		JButton applyBtn = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_PREFERENCES_CUSTOMIZE_APPLY", mainWin.getLanguageID(), "Apply"));
		applyBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.doApplyCustomization();
			}
		});
		sl_customizePanel.putConstraint(SpringLayout.SOUTH, propsList, -10, SpringLayout.NORTH, applyBtn);
		sl_customizePanel.putConstraint(SpringLayout.SOUTH, applyBtn, -10, SpringLayout.SOUTH, customizePanel);
		sl_customizePanel.putConstraint(SpringLayout.EAST, applyBtn, -10, SpringLayout.EAST, customizePanel);
		customizePanel.add(applyBtn);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_BTN_OK", mainWin.getLanguageID(), "Ok"));
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						PreferencesDialog.this.doOk();
						PreferencesDialog.this.dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_BTN_CANCEL", mainWin.getLanguageID(), "Cancel"));
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						PreferencesDialog.this.dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		
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
				
				// save the size of the dialog
				cfg.setPreferencesDialogWidth(PreferencesDialog.this.getWidth());
				cfg.setPreferencesDialogHeight(PreferencesDialog.this.getHeight());
				
				closed = true;
			}
		});
		
		mainWin.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Loads the available languages an activates the current language id.
	 * 
	 * @since 1.0
	 */
	private void loadLanguages() {
		final List<LabelEntry> langs = mainWin.getLanguageFile().getAvailableLanguages();
		int currLangIndex = -1;
		
		for(int i = 0; i < langs.size(); i++) {
			if(langs.get(i).langID.equals(mainWin.getLanguageID()))
				currLangIndex = i;
			
			cboLanguages.addItem(langs.get(i).description + " [" + langs.get(i).langID + "]");
		}
		
		cboLanguages.setSelectedIndex(currLangIndex);
	}
	
	/**
	 * Loads the customizable plugins.
	 * 
	 * @return a list of the original indices of the customizable plugins in the list of installed plugins
	 * @since 1.0
	 */
	private List<Integer> loadCustomizablePlugins() {
		final List<Integer> indices = new ArrayList<Integer>();
		final List<AlgorithmPlugin> plugins = mainWin.getLoader().getPluginManager().getInstalledPlugins();
		
		for(int i = 0; i < plugins.size(); i++) {
			if(plugins.get(i).hasCustomization()) {
				cboPlugins.addItem(plugins.get(i).getName());
				indices.add(i);
			}
		}
		
		cboPlugins.setSelectedIndex(-1);
		
		return indices;
	}
	
	/**
	 * Loads the customization properties of the current selected plugin.
	 * 
	 * @since 1.0
	 */
	private void loadPluginCustomization() {
		final int selIndex = cboPlugins.getSelectedIndex();
		
		if(selIndex < 0)
			return;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		propsListModel.removeAll();
		mainWin.getLoader().getPluginManager().getInstalledPlugins().get(cboPluginsIndices.get(selIndex)).loadCustomization(propsListModel);
		propsList.repaint();
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Saves the configuration.
	 * 
	 * @since 1.0
	 */
	private void doOk() {
		final List<LabelEntry> langs = mainWin.getLanguageFile().getAvailableLanguages();
		final String newLangID = langs.get(cboLanguages.getSelectedIndex()).langID;
		final boolean langChanged = !config.getLanguageID().equals(newLangID);
		
		// if their is a plugin selected that should be customized then apply the customization so that the user
		// can quit the dialog and applying the customization using ok
		doApplyCustomization();
		
		// store the preferences
		config.setLanguageID(newLangID);
		config.setShowWelcomeScreen(cbShowWelcomeScreen.isSelected());
		config.setShowExerciseModeInfoDialog(cbShowExerciseModeInfoDlg.isSelected());
		config.setDisableInformationBarPermanently(cbDisableInfoBarPermanently.isSelected());
		config.setColumnAlgoProblemAffiliationVisible(cbAlgoProblemAffiliation.isSelected());
		config.setColumnAlgoSubjectVisible(cbAlgoSubject.isSelected());
		config.setColumnAlgoTypeVisible(cbAlgoType.isSelected());
		config.setColumnAlgoAuthorVisible(cbAlgoAuthor.isSelected());
		config.setColumnAlgoVersionVisible(cbAlgoVersion.isSelected());
		config.setColumnAlgoSDKVersionVisible(cbUsedSDKVersion.isSelected());
		config.setLastOpenedAlgorithmsCount(openRecentCountSlider.getValue());
		
		// if the language has changed then show a message so that the user knows that he has to restart LAVES to apply the langugae
		if(langChanged)
			JOptionPane.showMessageDialog(this, LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_INFO_LANGUAGECHANGED", newLangID, "Please restart LAVES to apply the changed language settings."), LanguageFile.getLabel(mainWin.getLanguageFile(), "MSG_INFO_TITLE_LANGUAGECHANGED", newLangID, "Language"), JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Applies the customization to the current selected plugin.
	 * 
	 * @since 1.0
	 */
	private void doApplyCustomization() {
		final int selIndex = cboPlugins.getSelectedIndex();
		
		if(selIndex < 0)
			return;
		
		mainWin.getLoader().getPluginManager().getInstalledPlugins().get(cboPluginsIndices.get(selIndex)).applyCustomization(propsListModel);
	}
	
	/**
	 * The properties list model for the customization of a plugin.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private class CustomizationListModel extends PropertiesListModel {
		
		/**
		 * Creates a new model.
		 * 
		 * @since 1.0
		 */
		public CustomizationListModel() {
			super(PreferencesDialog.this.mainWin.getLanguageFile(), PreferencesDialog.this.mainWin.getLanguageID());
		}
		
		@Override
		public boolean isHeaderVisible() {
			return true;
		}
		
		@Override
		public boolean hasAutoRowSorter() {
			return true;
		}
		
	}
}
