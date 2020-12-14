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
 * Class:		StatusBar
 * Task:		A statusbar component
 * Created:		23.04.14
 * LastChanges:	23.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui.widgets;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

/**
 * Represents a statusbar with four fields. Use {@link #setText(int, String)} to set a text of a field.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class StatusBar extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** the label of field 1 */
	private final JLabel lblField1;
	/** the label of field 2 */
	private final JLabel lblField2;
	/** the label of field 3 */
	private final JLabel lblField3;
	/** the label of field 4 */
	private final JLabel lblField4;

	/**
	 * Creates a new statusbar.
	 * 
	 * @since 1.0
	 */
	public StatusBar() {
		final GridBagConstraints gbc = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		lblField1 = new JLabel();
		lblField1.setHorizontalTextPosition(JLabel.LEFT);
		add(lblField1, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(new JSeparator(JSeparator.VERTICAL), gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		lblField2 = new JLabel();
		lblField2.setHorizontalTextPosition(JLabel.LEFT);
		add(lblField2, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(new JSeparator(JSeparator.VERTICAL), gbc);
		
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		lblField3 = new JLabel();
		lblField3.setHorizontalTextPosition(JLabel.LEFT);
		add(lblField3, gbc);
		
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(new JSeparator(JSeparator.VERTICAL), gbc);
		
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		lblField4 = new JLabel();
		lblField4.setHorizontalTextPosition(JLabel.LEFT);
		add(lblField4, gbc);
	}
	
	/**
	 * Gets the text of a statusbar field.
	 * 
	 * @param field the index of the field
	 * @return the text of the field
	 * @since 1.0
	 */
	public String getText(final int field) {
		final JLabel lblField = getField(field);
		
		if(lblField != null) {
			final String s = lblField.getText().replaceAll("<br>", "\n");
			final String s2 = s.substring("<html>".length(), s.length());
			
			return s2.substring(0, s2.length() - "</html>".length());
		}
		else
			return "";
	}
	
	/**
	 * Sets the text of a statusbar field.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The statusbar has for fields with the indices <code>1</code>, <code>2</code>, <code>3</code> and <code>4</code>.
	 * 
	 * @param field the index of the field
	 * @param text the text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setText(final int field, String text) throws IllegalArgumentException {
		if(text == null)
			throw new IllegalArgumentException("No valid argument!");
		
		JLabel lblField = getField(field);
		
		// if the message is to long then add a tooltip and decrease the length
		if(text.length() > 150) {
			lblField.setToolTipText(text);
			text = text.substring(0, 150) + " ...";
		}
		else
			lblField.setToolTipText(null);
		
		if(lblField != null)
			lblField.setText("<html>" + text.replace("\n", "<br>") + "</html>");
	}
	
	/**
	 * Sets the foreground color of a statusbar field.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The statusbar has for fields with the indices <code>1</code>, <code>2</code>, <code>3</code> and <code>4</code>.
	 * 
	 * @param field the index of the field
	 * @param color the foreground color
	 * @since 1.0
	 */
	public void setForeground(final int field, final Color color) {
		final JLabel lblField = getField(field);
		
		if(lblField != null)
			lblField.setForeground(color);
	}
	
	/**
	 * Gets a field of the statusbar.
	 * 
	 * @param index the index of the field
	 * @return the field label or <code>null</code> if the index is invalid
	 * @since 1.0
	 */
	private JLabel getField(final int index) {
		switch(index) {
			case 1:	return lblField1;
			case 2:	return lblField2;
			case 3:	return lblField3;
			case 4:	return lblField4;
		}
		
		return null;
	}
}
