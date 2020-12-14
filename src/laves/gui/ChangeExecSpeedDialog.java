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
 * Class:		ChangeExecSpeedDialog
 * Task:		Let the user choose the execution speed
 * Created:		26.04.14
 * LastChanges:	26.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.gui.dialogs.OptionDialog;
import lavesdk.language.LanguageFile;

/**
 * A dialog to change the execution speed of an algorithm runtime environment.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ChangeExecSpeedDialog extends OptionDialog {

	private static final long serialVersionUID = 1L;
	
	/** the combobox that displays the factors */
	private final JComboBox<String> cboFactors;
	/** the chosen factor */
	private int chosenFactor;

	/**
	 * Creates a new change execution speed dialog.
	 * 
	 * @param execSpeedFactors the available execution speed factors
	 * @param currentFactor the current factor index (has to be one-based)
	 * @param host the host
	 * @param langFile the language file
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if execSpeedFactors is null</li>
	 * 		<li>if host is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ChangeExecSpeedDialog(final String[] execSpeedFactors, final int currentFactor,final PluginHost host, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(host, LanguageFile.getLabel(langFile, "RTE_EXECSPEED", langID, "Execution Speed"), langFile, langID, true);
		
		chosenFactor = -1;
		
		northPanel.setLayout(new BorderLayout());
		northPanel.add(new JLabel(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_CHANGE", langID, "Change Execution Speed")), BorderLayout.CENTER);
		
		centerPanel.setLayout(new BorderLayout());
		cboFactors = new JComboBox<String>(execSpeedFactors);
		cboFactors.setEditable(false);
		cboFactors.setSelectedIndex(currentFactor - 1);
		centerPanel.add(cboFactors, BorderLayout.CENTER);
		
		pack();
	}
	
	/**
	 * Gets the execution speed factor the user has chosen.
	 * 
	 * @return the factor or <code>-1</code> if the user cancels the dialog
	 * @since 1.0
	 */
	public int getChosenFactor() {
		return chosenFactor;
	}

	@Override
	protected void doOk() {
		chosenFactor = cboFactors.getSelectedIndex() + 1;
	}

}
