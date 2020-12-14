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
 * Class:		AlgorithmTableModel
 * Task:		The model of the algorithm table
 * Created:		25.04.14
 * LastChanges:	09.10.14
 * LastAuthor:	jdornseifer
 */

package laves.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import laves.Loader;
import laves.configuration.MainConfiguration;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.language.LanguageFile;

/**
 * The table model of the algorithm table.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class AlgorithmTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	
	/** the configuration */
	private final MainConfiguration cfg;
	/** the loader */
	private final Loader loader;
	/** the table of the model */
	private JTable table;
	/** the columns of the table */
	private final List<String> columns;
	/** the real indices of the columns */
	private final List<Integer> colIndices;
	/** the rows of the table */
	private List<AlgorithmPlugin> rows;
	
	/** the index of the name column */
	private static final int COLUMN_NAME_INDEX = 0;
	/** the index of the problem affiliation column */
	private static final int COLUMN_PROBLEMAFFILIATION_INDEX = 1;
	/** the index of the subject column */
	private static final int COLUMN_SUBJECT_INDEX = 2;
	/** the index of the type column */
	private static final int COLUMN_TYPE_INDEX = 3;
	/** the index of the author column */
	private static final int COLUMN_AUTHOR_INDEX = 4;
	/** the index of the author contact column */
	private static final int COLUMN_AUTHORCONTACT_INDEX = 5;
	/** the index of the version column */
	private static final int COLUMN_VERSION_INDEX = 6;
	/** the index of the sdk version column */
	private static final int COLUMN_SDKVERSION_INDEX = 7;
	
	/**
	 * Creates a new table model.
	 * 
	 * @param cfg the main configuration
	 * @param loader the loader of the application
	 * @param langFile the language file
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if cfg is null</li>
	 * 		<li>if loader is null</li>
	 * 		<li>if the installed plugins are not available yet</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmTableModel(final MainConfiguration cfg, final Loader loader, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(cfg, loader, langFile, langID, false);
	}
	
	/**
	 * Creates a new table model.
	 * 
	 * @param cfg the main configuration
	 * @param loader the loader of the application
	 * @param langFile the language file
	 * @param langID the language id
	 * @param showAllColumns <code>true</code> if all columns should be displayed except what is configured otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if cfg is null</li>
	 * 		<li>if loader is null</li>
	 * 		<li>if the plugin manager is not available yet</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmTableModel(final MainConfiguration cfg, final Loader loader, final LanguageFile langFile, final String langID, final boolean showAllColumns) throws IllegalArgumentException {
		if(cfg == null || loader == null || loader.getPluginManager() == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.cfg = cfg;
		this.loader = loader;
		this.table = null;
		this.columns = new ArrayList<String>();
		this.colIndices = new ArrayList<Integer>();
		this.rows = loader.getPluginManager().getInstalledPlugins();
		
		// create the visible columns
		columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGONAME", langID, "Name"));
		colIndices.add(COLUMN_NAME_INDEX);
		if(showAllColumns || cfg.getColumnAlgoProblemAffiliationVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOPROBLEMAFFILIATION", langID, "Problem Affiliation"));
			colIndices.add(COLUMN_PROBLEMAFFILIATION_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoSubjectVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOSUBJECT", langID, "Subject"));
			colIndices.add(COLUMN_SUBJECT_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoTypeVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOTYPE", langID, "Type"));
			colIndices.add(COLUMN_TYPE_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoAuthorVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOAUTHOR", langID, "Author"));
			colIndices.add(COLUMN_AUTHOR_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoAuthorContactVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOAUTHORCONTACT", langID, "Author Contact Details"));
			colIndices.add(COLUMN_AUTHORCONTACT_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoVersionVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOVERSION", langID, "Version"));
			colIndices.add(COLUMN_VERSION_INDEX);
		}
		if(showAllColumns || cfg.getColumnAlgoSDKVersionVisible()) {
			columns.add(LanguageFile.getLabel(langFile, "COLUMN_ALGOSDKVERSION", langID, "Used SDK Version"));
			colIndices.add(COLUMN_SDKVERSION_INDEX);
		}
	}
	
	/**
	 * Sets the table if the model and restores the column widths of the configuration.
	 * 
	 * @param table the table
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if table is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setTable(final JTable table) throws IllegalArgumentException {
		if(table == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.table = table;
		
		// update the column model
		fireTableStructureChanged();
		
		// afterwards the columns are available in the column model so set the preferred widths of the columns
		for(int i = 0; i < colIndices.size(); i++) {
			final TableColumn column = table.getColumnModel().getColumn(i);
			
			if(column == null)
				continue;
			
			switch(colIndices.get(i)) {
				case COLUMN_NAME_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoNameWidth());
					break;
				case COLUMN_PROBLEMAFFILIATION_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoProblemAffiliationWidth());
					break;
				case COLUMN_SUBJECT_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoSubjectWidth());
					break;
				case COLUMN_TYPE_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoTypeWidth());
					break;
				case COLUMN_AUTHOR_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoAuthorWidth());
					break;
				case COLUMN_AUTHORCONTACT_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoAuthorContactWidth());
					break;
				case COLUMN_VERSION_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoVersionWidth());
					break;
				case COLUMN_SDKVERSION_INDEX:
					column.setPreferredWidth(cfg.getColumnAlgoSDKVersionWidth());
					break;
			}
		}
	}
	
	/**
	 * Reloads the data in the algorithm table.
	 * 
	 * @since 1.0 
	 */
	public void reload() {
		rows = loader.getPluginManager().getInstalledPlugins();
		fireTableDataChanged();
	}
	
	/**
	 * Applies the column widths to the configuration (only possible if a table was previously set using {@link #setTable(JTable)}).
	 * 
	 * @since 1.0
	 */
	public void storeColumnWidths() {
		if(table == null)
			return;
		
		for(int i = 0; i < colIndices.size(); i++) {
			final TableColumn column = table.getColumnModel().getColumn(i);
			
			if(column == null)
				continue;
			
			switch(colIndices.get(i)) {
				case COLUMN_NAME_INDEX:
					cfg.setColumnAlgoNameWidth(column.getPreferredWidth());
					break;
				case COLUMN_PROBLEMAFFILIATION_INDEX:
					cfg.setColumnAlgoProblemAffiliationWidth(column.getPreferredWidth());
					break;
				case COLUMN_SUBJECT_INDEX:
					cfg.setColumnAlgoSubjectWidth(column.getPreferredWidth());
					break;
				case COLUMN_TYPE_INDEX:
					cfg.setColumnAlgoTypeWidth(column.getPreferredWidth());
					break;
				case COLUMN_AUTHOR_INDEX:
					cfg.setColumnAlgoAuthorWidth(column.getPreferredWidth());
					break;
				case COLUMN_AUTHORCONTACT_INDEX:
					cfg.setColumnAlgoAuthorContactWidth(column.getPreferredWidth());
					break;
				case COLUMN_VERSION_INDEX:
					cfg.setColumnAlgoVersionWidth(column.getPreferredWidth());
					break;
				case COLUMN_SDKVERSION_INDEX:
					cfg.setColumnAlgoSDKVersionWidth(column.getPreferredWidth());
					break;
			}
		}
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}
	
	@Override
	public String getColumnName(int column) {
		return columns.get(column);
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
		final AlgorithmPlugin p = rows.get(row);
		
		// convert the column index to its real index and get the value
		switch(colIndices.get(column)) {
			case COLUMN_NAME_INDEX:					return p.getName();
			case COLUMN_PROBLEMAFFILIATION_INDEX:	return p.getProblemAffiliation();
			case COLUMN_SUBJECT_INDEX:				return p.getSubject();
			case COLUMN_TYPE_INDEX:					return p.getType();
			case COLUMN_AUTHOR_INDEX:				return p.getAuthor();
			case COLUMN_AUTHORCONTACT_INDEX:		return p.getAuthorContact();
			case COLUMN_VERSION_INDEX:				return p.getVersion();
			case COLUMN_SDKVERSION_INDEX:			return p.getUsedSDKVersion().toString();
		}
		
		return null;
	}
	
}
