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
 * Class:		ExerciseModeInfoDialog
 * Task:		A dialog to show information about the exercise mode with an "do not show again"-option 
 * Created:		08.07.14
 * LastChanges:	08.07.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import laves.configuration.MainConfiguration;
import laves.resources.ResourceManager;
import laves.utils.Utils;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.language.LanguageFile;

import javax.swing.JLabel;
import javax.swing.JCheckBox;

/**
 * Represents an information dialog for the exercise mode.
 * <br><br>
 * Use {@link #getDoNotShowAgain()} to get notified about whether the user disables the dialog for the next time.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.1
 */
public class ExerciseModeInfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the content panel */
	private final JPanel contentPanel = new JPanel();
	/** the checkbox of the "do not show again"-option */
	private final JCheckBox cbDoNotShowAgain;

	/**
	 * Creates a new exercise mode information dialog.
	 * 
	 * @param mw the main window
	 * @param cfg the main configuration
	 * @param activePlugin the active plugin
	 * @param modal <code>true</code> if the dialog should be modal otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if mw is null</li>
	 * 		<li>if cfg is null</li>
	 * 		<li>if activePlugin is null</li>
	 * </ul> 
	 * @since 1.0
	 */
	public ExerciseModeInfoDialog(final MainWindow mw, final MainConfiguration cfg, final AlgorithmPlugin activePlugin, final boolean modal) throws IllegalArgumentException {
		if(mw == null || cfg == null || activePlugin == null)
			throw new IllegalArgumentException("No valid argument!");
		
		setTitle(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_EXERCISEMODEINFO_TITLE", mw.getLanguageID(), "Information about the Exercise Mode"));
		setType(Type.UTILITY);
		setModal(modal);
		setResizable(false);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 20));
		
		// create the text panel containing the information text and the see also labels
		final JPanel textPanel = new JPanel(new BorderLayout(10, 5));
		
		final JLabel lblInfoText = new JLabel("<html>" + LanguageFile.getLabel(mw.getLanguageFile(), "DLG_EXERCISEMODEINFO_TEXT", mw.getLanguageID(), "The exercise mode let you practice the algorithm in an interactive way.<br>Start the algorithm and answer the exercises, you will be asked during the execution. You have an arbitrary<br>number of attempts to solve an exercise. If you are not able to process an exercise, you can cancel the task<br>and view the sample solution in the visualization.<br><br>If an exercise can be solved directly in a view of the algorithm the corresponding view is highlighted with a border.<br>Enter your solution and afterwards press the \"solve\"-button of the exercise.") + "</html>");
		textPanel.add(lblInfoText, BorderLayout.CENTER);
		final JLabel lblInfoIcon = new JLabel(ResourceManager.getInstance().ICON_INFO_LARGE);
		lblInfoIcon.setVerticalAlignment(JLabel.TOP);
		textPanel.add(lblInfoIcon, BorderLayout.WEST);
		
		JPanel seeAlsoPanel = null;
		
		// only add the see also labels if their is are instructions
		if(activePlugin.getInstructions() != null && !activePlugin.getInstructions().isEmpty()) {
			seeAlsoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			seeAlsoPanel.add(new JSeparator(), BorderLayout.NORTH);
			
			JLabel lblSeeAlso = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_EXERCISEMODEINFO_SEEALSO", mw.getLanguageID(), "See also here:"));
			seeAlsoPanel.add(lblSeeAlso, BorderLayout.WEST);
			
			JLabel lblInstructionsLink = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_EXERCISEMODEINFO_INSTRUCTIONSLINK", mw.getLanguageID(), "Instructions of the algorithm"));
			seeAlsoPanel.add(lblInstructionsLink, BorderLayout.CENTER);
			Utils.makeLinkLabel(lblInstructionsLink, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(mw, new JLabel("<html>" + activePlugin.getInstructions() + "</html>"), LanguageFile.getLabel(mw.getLanguageFile(), "MENU_HELP_INSTRUCTIONS", mw.getLanguageID(), "Instructions"), JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
		
		if(seeAlsoPanel != null)
			textPanel.add(seeAlsoPanel, BorderLayout.SOUTH);
		contentPanel.add(textPanel, BorderLayout.CENTER);
		
		// add the "do not show again"-option
		cbDoNotShowAgain = new JCheckBox(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_CB_DONOTSHOWAGAIN", mw.getLanguageID(), "Do not show this dialog again"));
		contentPanel.add(cbDoNotShowAgain, BorderLayout.SOUTH);
		
		// add the quit button
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnQuit = new JButton(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_BTN_QUIT", mw.getLanguageID(), "Quit"));
				btnQuit.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						ExerciseModeInfoDialog.this.dispose();
					}
				});
				buttonPane.add(btnQuit);
				getRootPane().setDefaultButton(btnQuit);
			}
		}
		
		pack();
		mw.adaptDialog(this);
		
		addWindowListener(new WindowAdapter() {
			
			boolean closed = false;
			
			@Override
			public void windowClosing(WindowEvent e) {
				windowClosed(e);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				if(closed)
					return;
				
				cfg.setShowExerciseModeInfoDialog(!cbDoNotShowAgain.isSelected());
				closed = true;
			}
			
		});
	}
}
