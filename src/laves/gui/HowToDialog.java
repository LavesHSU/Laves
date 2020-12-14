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
 * Class:		HowToDialog
 * Task:		The dialog that displays the HowTo
 * Created:		28.04.14
 * LastChanges:	28.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JEditorPane;

import laves.utils.Utils;
import lavesdk.language.LanguageFile;

/**
 * Dialog that displays the HowTo of LAVES.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class HowToDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the content panel */
	private final JPanel contentPanel = new JPanel();

	/**
	 * Creates a new HowTo dialog.
	 * 
	 * @param mw the main window
	 * @since 1.0
	 */
	public HowToDialog(final MainWindow mw) throws IllegalArgumentException {
		if(mw == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final URL howToURL = Utils.getHowToURL(mw.getLanguageID());
		
		setTitle(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_HOWTO_TITLE", mw.getLanguageID(), "HowTo"));
		setSize(600, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mw.adaptDialog(this);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JEditorPane editorPane;
		try {
			editorPane = (howToURL != null) ? new JEditorPane(howToURL) : new JEditorPane("No HowTo file available!");
			editorPane.setContentType("text/html");
			editorPane.setEditable(false);
			contentPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
		} catch (IOException e) {
			contentPanel.add(new JLabel("<html>HowTo file could not be loaded!<br>" + e.getMessage() + "</html>"), BorderLayout.CENTER);
		}
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton quitButton = new JButton(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_BTN_QUIT", mw.getLanguageID(), "Quit"));
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				HowToDialog.this.dispose();
			}
		});
		buttonPane.add(quitButton);
		getRootPane().setDefaultButton(quitButton);
	}

}
