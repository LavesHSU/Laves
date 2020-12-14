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
 * Class:		HintTextFieldUI
 * Task:		Text field UI to display a hint in a text field
 * Created:		24.04.14
 * LastChanges:	25.04.14
 * LastAuthor:	jdornseifer
 */

package laves.gui.widgets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

/**
 * Text field UI to display a hint in a text field.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class HintTextFieldUI extends BasicTextFieldUI implements FocusListener {

	/** the hint that should be displayed */
	private final String hint;
	/** the color of the hint or <code>null</code> to use a brighter version of the foreground color */
	private final Color color;
	
	/**
	 * Creates a new hint text field UI.
	 * 
	 * @param hint the hint
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if hint is null</li>
	 * 		<li>if hint is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public HintTextFieldUI(final String hint) throws IllegalArgumentException {
		this(hint, Color.gray);
	}

	/**
	 * Creates a new hint text field UI.
	 * 
	 * @param hint the hint
	 * @param color the color of the hint or <code>null</code> to use a brighter version of the foreground color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if hint is null</li>
	 * 		<li>if hint is empty</li>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public HintTextFieldUI(final String hint, final Color color) throws IllegalArgumentException {
		if(hint == null || hint.isEmpty() || color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.hint = hint;
		this.color = color;
	}

	@Override
	protected void paintSafely(Graphics g) {
		super.paintSafely(g);
		
		// get the text field component
		final JTextComponent comp = getComponent();
		
		int offsetX = 2;
		
		// determine the left offset of the hint
		if(comp.getBorder() != null) {
			final Insets insets = comp.getBorder().getBorderInsets(comp);
			if(insets != null)
				offsetX += insets.left;
		}
		
		// only set the hint if the text field does not have text in it and the component is not focused
		if(comp.getText().isEmpty() && !comp.hasFocus()){
			g.setColor(color);
			
			// draw the hint in the background
			g.drawString(hint, offsetX, comp.getHeight() - ((comp.getHeight() - comp.getFont().getSize()) / 2) - 1);          
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// repaint the text field to hide the hint if necessary
		if(getComponent() != null)
			getComponent().repaint();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// repaint the text field to display the hint if necessary
		if(getComponent() != null)
			getComponent().repaint();
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		getComponent().addFocusListener(this);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		getComponent().removeFocusListener(this);
	}
}