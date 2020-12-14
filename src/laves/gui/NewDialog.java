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
 * Class:		NewDialog
 * Task:		The new dialog
 * Created:		23.04.14
 * LastChanges:	05.05.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.SpringLayout;

import laves.configuration.MainConfiguration;
import laves.gui.widgets.AlgorithmTableModel;
import laves.gui.widgets.HintTextFieldUI;
import laves.resources.ResourceManager;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.views.ViewGroup;
import lavesdk.gui.widgets.PropertiesList;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.language.LanguageFile;

/**
 * The new dialog to create a new algorithm instance.
 * <br><br>
 * Use {@link #getSelectedPlugin()} to get the plugin that should be created and {@link #getCreatorPreferences()} to get its
 * creator preferences.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class NewDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the main window */
	private final MainWindow mainWin;
	/** the result of the dialog */
	private AlgorithmPlugin selectedPlugin;
	/** the selected plugin or <code>null</code> */
	private AlgorithmPlugin currSelectedPlugin;
	/** the result of the dialog */
	private PropertiesListModel creatorPreferences;
	/** the associated creator preferences or <code>null</code>*/
	private PropertiesListModel currCreatorPreferences;
	/** the content panel */
	private final JPanel contentPanel = new JPanel();
	/** the properties list that displays the creator preferences */
	private final PropertiesList prefList;
	/** the model of the algorithm table */
	private final AlgorithmTableModel algoTableModel;
	/** the table that shows the installed algorithm plugins */
	private final JTable algoTable;
	/** the sorter of the algorithm table */
	private final TableRowSorter<AlgorithmTableModel> algoTableSorter;
	/** the info panel that shows information about the selected plugin */
	private final JPanel infoPanel;
	/** the preferences group */
	private final JPanel prefsGroup;
	/** the label of the algorithm name in the information panel */
	private final JLabel lblAlgoName;
	/** the label of the algorithm description in the information panel */
	private final JLabel lblAlgoDesc;
	/** the label of the algorithm problem affiliation in the information panel */
	private final JLabel lblAlgoProbAffil;
	/** the label of the algorithm subject in the information panel */
	private final JLabel lblAlgoSubject;
	/** the label of the algorithm type in the information panel */
	private final JLabel lblAlgoType;
	/** the label of the algorithm assumptions in the information panel */
	private final JLabel lblAlgoAssumptions;
	/** the label of the algorithm name */
	private final String algoNameLabel;
	/** the label of the algorithm description */
	private final String algoDescLabel;
	/** the label of the algorithm problem affiliation */
	private final String algoProbAffilLabel;
	/** the label of the algorithm subject */
	private final String algoSubjectLabel;
	/** the label of the algorithm type */
	private final String algoTypeLabel;
	/** the label of the algorithm assumptions */
	private final String algoAssumptionsLabel;

	/**
	 * Create the dialog.
	 * 
	 * @param mw the main window
	 * @param cfg the configuration of the dialog
	 * @throws IllegalArgumentException
	 * <ul>
	 *		<li>if mw is null</li>
	 *		<li>if cfg is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public NewDialog(final MainWindow mw, final MainConfiguration cfg) throws IllegalArgumentException {
		if(mw == null || cfg == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Font f = UIManager.getFont("Label.font");
		final Font boldFont = f.deriveFont(Font.BOLD);
		
		this.mainWin = mw;
		this.selectedPlugin = null;
		this.currSelectedPlugin = null;
		this.creatorPreferences = null;
		this.currCreatorPreferences = null;
		
		// load labels of the info panel and algorithm table
		algoNameLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGONAME", mainWin.getLanguageID(), "Name");
		algoDescLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGODESC", mainWin.getLanguageID(), "Description");
		algoProbAffilLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOPROBLEMAFFILIATION", mainWin.getLanguageID(), "Problem Affiliation");
		algoSubjectLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOSUBJECT", mainWin.getLanguageID(), "Subject");
		algoTypeLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "COLUMN_ALGOTYPE", mainWin.getLanguageID(), "Type");
		algoAssumptionsLabel = LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_ALGOASSUMPTIONS", mainWin.getLanguageID(), "Assumption(s)");
		
		// initialize the dialog
		setTitle(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_TITLE", mainWin.getLanguageID(), "New algorithm"));
		setSize(cfg.getNewDialogWidth(), cfg.getNewDialogHeight());
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// let the main window (host) adapt the dialog
		mainWin.adaptDialog(this);
		
		getContentPane().setLayout(new BorderLayout());
		
		// create a description panel at the top
		final JPanel descPanel = new JPanel(new BorderLayout(10, 10));
		descPanel.setBackground(Color.white);
		descPanel.add(new JLabel(ResourceManager.getInstance().ICON_NEW_BIG), BorderLayout.WEST);
		descPanel.add(new JLabel("<html>" + LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_DESCRIPTION", mainWin.getLanguageID(), "<b>Select an algorithm from the list below.</b><br>Use the preferences to individualize the selected algorithm.") + "</html>"), BorderLayout.CENTER);
		descPanel.add(new JSeparator(), BorderLayout.SOUTH);
		descPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Color.white));
		getContentPane().add(descPanel, BorderLayout.NORTH);
		
		// initialize the content panel
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		// create main split pane (VERTICAL)
		final ViewGroup gblSplitPane = new ViewGroup(ViewGroup.HORIZONTAL);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, gblSplitPane, 2, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, gblSplitPane, 2, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, gblSplitPane, -2, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, gblSplitPane, -2, SpringLayout.EAST, contentPanel);
		contentPanel.add(gblSplitPane);
		
		// create the algorithm table area
		final JPanel algoTablePanel = new JPanel(new BorderLayout(2, 2));
		gblSplitPane.add(algoTablePanel);
		
		// create the search text field in the algorithm table area
		final JTextField searchField = new JTextField();
		searchField.setUI(new HintTextFieldUI(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_SEARCH", mainWin.getLanguageID(), "Search")));
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				filterAlgoTable(searchField);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterAlgoTable(searchField);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				filterAlgoTable(searchField);
			}
		});
		algoTablePanel.add(searchField, BorderLayout.NORTH);
		
		// create the algorithm table
		algoTableModel = new AlgorithmTableModel(cfg, mainWin.getLoader(), mainWin.getLanguageFile(), mainWin.getLanguageID());
		algoTableSorter = new TableRowSorter<AlgorithmTableModel>(algoTableModel);
		algoTable = new JTable(algoTableModel);
		algoTableModel.setTable(algoTable);
		algoTable.setRowSorter(algoTableSorter);
		algoTable.setRowHeight(20);
		algoTable.getTableHeader().setReorderingAllowed(false);
		algoTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		algoTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(NewDialog.this.algoTable.getSelectedRowCount() < 1)
					return;
				
				NewDialog.this.selectionChanged(NewDialog.this.algoTableModel.getRow(NewDialog.this.algoTable.convertRowIndexToModel(NewDialog.this.algoTable.getSelectedRow())));
			}
		});
		algoTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && NewDialog.this.algoTable.getSelectedRowCount() > 0) {
					NewDialog.this.selectionChanged(NewDialog.this.algoTableModel.getRow(NewDialog.this.algoTable.convertRowIndexToModel(NewDialog.this.algoTable.getSelectedRow())));
					doOk();
				}
			}
		});
		algoTablePanel.add(new JScrollPane(algoTable), BorderLayout.CENTER);
		
		// create the number of installed algorithms label in the algorithm table area
		final JLabel lblNOIA = new JLabel(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_NUMBEROFINSTALLEDALGOS", mainWin.getLanguageID(), "Number of installed algorithms:") + " " + algoTableModel.getRowCount());
		algoTablePanel.add(lblNOIA, BorderLayout.SOUTH);
		
		// create top split pane (HORIZONTAL) with the table and the info area
		final ViewGroup rightSplitPane = new ViewGroup(ViewGroup.VERTICAL);
		gblSplitPane.add(rightSplitPane);
		
		// create the information panel
		final JPanel infoGroup = new JPanel(new BorderLayout());
		infoGroup.setBorder(BorderFactory.createTitledBorder(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_INFORMATIONAREA_TITLE", mainWin.getLanguageID(), "Information")));
		infoPanel = new JPanel(new GridBagLayout());
		infoGroup.add(infoPanel, BorderLayout.CENTER);
		rightSplitPane.add(infoGroup);
		{
			// create labels of information panel
			final GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.6;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			JLabel lbl = new JLabel(algoNameLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.4;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			lbl = new JLabel(algoTypeLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.6;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoName = new JLabel();
			infoPanel.add(lblAlgoName, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.4;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoType = new JLabel();
			infoPanel.add(lblAlgoType, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			lbl = new JLabel(algoDescLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			gbc.gridheight = 2;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoDesc = new JLabel();
			infoPanel.add(lblAlgoDesc, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 5;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.6;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			lbl = new JLabel(algoProbAffilLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 5;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.4;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			lbl = new JLabel(algoSubjectLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 6;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.6;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoProbAffil = new JLabel();
			infoPanel.add(lblAlgoProbAffil, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 6;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.4;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoSubject = new JLabel();
			infoPanel.add(lblAlgoSubject, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 7;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 0, 2);
			lbl = new JLabel(algoAssumptionsLabel);
			lbl.setFont(boldFont);
			infoPanel.add(lbl, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.insets = new Insets(2, 2, 2, 2);
			lblAlgoAssumptions = new JLabel();
			infoPanel.add(lblAlgoAssumptions, gbc);
			
			// add a dummy label that fills out the bottom space
			gbc.gridx = 0;
			gbc.gridy = 9;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(0, 0, 0, 0);
			infoPanel.add(new JLabel(), gbc);
		}
		
		// create the creator properties group
		prefsGroup = new JPanel(new BorderLayout());
		prefsGroup.setBorder(BorderFactory.createTitledBorder(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_NEW_CREATORPREFSAREA_TITLE", mainWin.getLanguageID(), "Preferences")));
		prefList = new PropertiesList();
		prefsGroup.add(prefList, BorderLayout.CENTER);
		prefsGroup.setVisible(false);
		rightSplitPane.add(prefsGroup);
		
		// create button pane
		final JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		final JButton okButton = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_BTN_OK", mainWin.getLanguageID(), "Ok"));
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewDialog.this.doOk();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		final JButton cancelButton = new JButton(LanguageFile.getLabel(mainWin.getLanguageFile(), "DLG_BTN_CANCEL", mainWin.getLanguageID(), "Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewDialog.this.doCancel();
			}
		});
		buttonPane.add(cancelButton);
		
		addWindowListener(new WindowAdapter() {
			
			private boolean closed = false;
			private boolean opened = false;
			
			@Override
			public void windowClosed(WindowEvent e) {
				windowClosing(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(closed)
					return;
				
				// save the size of the dialog
				cfg.setNewDialogWidth(NewDialog.this.getWidth());
				cfg.setNewDialogHeight(NewDialog.this.getHeight());
				NewDialog.this.algoTableModel.storeColumnWidths();
				
				closed = true;
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
				if(opened)
					return;
				
				// the window should get the focus so that the search field focus is deactivated
				NewDialog.this.requestFocusInWindow();
				
				opened = true;
			}
		});
		
		// finally set the weights of the split panes (has to be done at the end when all components are added to the split panes)
		gblSplitPane.setWeights(new float[] { 0.6f, 0.4f });
		rightSplitPane.setWeights(new float[] { 0.65f, 0.35f });
	}
	
	/**
	 * Gets the selected plugin.
	 * 
	 * @return the selected plugin or <code>null</code> if the dialog was canceled
	 * @since 1.0
	 */
	public AlgorithmPlugin getSelectedPlugin() {
		return selectedPlugin;
	}
	
	/**
	 * Gets the creator preferences of the selected plugin.
	 * 
	 * @return the creator preferences or <code>null</code> if the plugin does not have creator preferences
	 * @since 1.0
	 */
	public PropertiesListModel getCreatorPreferences() {
		return creatorPreferences;
	}
	
	/**
	 * Performs the ok of the dialog.
	 * 
	 * @since 1.0
	 */
	private void doOk() {
		// store the result data
		selectedPlugin = currSelectedPlugin;
		creatorPreferences = currCreatorPreferences;
		// and quit the dialog
		dispose();
	}
	
	/**
	 * Cancels the dialog.
	 * 
	 * @since 1.0
	 */
	private void doCancel() {
		// clear the result data because the dialog is canceled
		selectedPlugin = null;
		creatorPreferences = null;
		dispose();
	}
	
	/**
	 * Updates the information panel and the creator preferences list with the current selected plugin.
	 * 
	 * @param plugin the selected plugin
	 * @since 1.0
	 */
	private void selectionChanged(final AlgorithmPlugin plugin) {
		if(plugin == null) {
			// clear the info panel labels
			lblAlgoName.setText("");
			lblAlgoType.setText("");
			lblAlgoDesc.setText("");
			lblAlgoProbAffil.setText("");
			lblAlgoSubject.setText("");
			lblAlgoAssumptions.setText("");
			// disable the preferences
			prefsGroup.setVisible(false);
			return;
		}
		
		if(plugin.hasCreatorPreferences()) {
			// create and load the creator preferences
			currCreatorPreferences = new PropertiesListModel(mainWin.getLanguageFile(), mainWin.getLanguageID());
			plugin.loadCreatorPreferences(currCreatorPreferences);
			
			// display the preferences
			prefList.setModel(currCreatorPreferences);
			prefsGroup.setVisible(true);
		}
		else {
			// clear the creator preferences because the current selection does not have some
			currCreatorPreferences = null;
			prefsGroup.setVisible(false);
		}
		
		// load the information panel
		lblAlgoName.setText(plugin.getName());
		lblAlgoType.setText(plugin.getType());
		lblAlgoDesc.setText("<html>" + plugin.getDescription() + "</html>");
		lblAlgoProbAffil.setText(plugin.getProblemAffiliation());
		lblAlgoSubject.setText(plugin.getSubject());
		lblAlgoAssumptions.setText("<html>" + plugin.getAssumptions() + "</html>");
		
		// set the current selected plugin
		currSelectedPlugin = plugin;
	}
	
	/**
	 * Filters the algorithm table using the search string entered in the search field.
	 * 
	 * @param searchField the search field
	 * @since 1.0
	 */
	private void filterAlgoTable(final JTextField searchField) {
		final int[] columns = new int[algoTableModel.getColumnCount()];
		RowFilter<AlgorithmTableModel, Object> filter = null;
		
		for(int i = 0; i < columns.length; i++)
			columns[i] = i;
		
		try {
			// use the embedded flag (?i) for case insensitive
			filter = RowFilter.regexFilter("(?i)" + searchField.getText(), columns);
		}
		catch(PatternSyntaxException e) {
			filter = null;
		}
		
		if(filter != null)
			algoTableSorter.setRowFilter(filter);
	}
}
