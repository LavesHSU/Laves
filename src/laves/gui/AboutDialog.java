/**
 * This is part of LAVES - Logistics Algorithms Visualization and Education Software.
 * 
 * Copyright (C) 2020 Jan Dornseifer & Department of Management Information Science, University of Siegen &
 *                    Department for Management Science and Operations Research, Helmut Schmidt University
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
 */

package laves.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JTabbedPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JLabel;

import laves.resources.ResourceManager;
import laves.utils.Constants;
import laves.utils.Utils;
import lavesdk.LAVESDKV;
import lavesdk.language.LanguageFile;

/**
 * The about dialog that displays information about the application.
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/** the content panel */
	private final JPanel contentPanel = new JPanel();

	/**
	 * Creates a new about dialog.
	 * 
	 * @param mw the main window
	 * @since 1.0
	 */
	public AboutDialog(final MainWindow mw) throws IllegalArgumentException {
		if(mw == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Font f = UIManager.getFont("Label.font");
		final Font boldFont = f.deriveFont(Font.BOLD);
		
		setTitle(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_TITLE", mw.getLanguageID(), "About"));
		setModal(true);
		setSize(520, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mw.adaptDialog(this);
		
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
		
		final JPanel aboutPanel = new JPanel();
		tabbedPane.addTab(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO", mw.getLanguageID(), "Information"), null, aboutPanel, null);
		SpringLayout sl_aboutPanel = new SpringLayout();
		aboutPanel.setLayout(sl_aboutPanel);
		
		final JLabel lblLogo = new JLabel(ResourceManager.getInstance().LOGO_SMALL);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblLogo, 10, SpringLayout.NORTH, aboutPanel);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, lblLogo, -10, SpringLayout.EAST, aboutPanel);
		aboutPanel.add(lblLogo);
		
		final JLabel lblUniLogo = new JLabel(ResourceManager.getInstance().LOGO_UNISIEGEN);
		lblUniLogo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black), BorderFactory.createMatteBorder(4, 4, 4, 4, Color.white)));
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblUniLogo, 6, SpringLayout.SOUTH, lblLogo);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, lblUniLogo, 0, SpringLayout.EAST, lblLogo);
		aboutPanel.add(lblUniLogo);
		
		final JLabel lblHeadline = new JLabel(Constants.LAVES);
		lblHeadline.setForeground(Constants.COLOR_LOGO);
		lblHeadline.setFont(f.deriveFont(Font.BOLD, 16.0f));
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblHeadline, 10, SpringLayout.NORTH, aboutPanel);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblHeadline, 10, SpringLayout.WEST, aboutPanel);
		aboutPanel.add(lblHeadline);
		
		final JLabel lblSubhead = new JLabel(Constants.LAVES_FULL);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblSubhead, 6, SpringLayout.SOUTH, lblHeadline);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblSubhead, 0, SpringLayout.WEST, lblHeadline);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, lblSubhead, -6, SpringLayout.WEST, lblLogo);
		aboutPanel.add(lblSubhead);
			
		final JLabel lblVersionTitle = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO_VERSION", mw.getLanguageID(), "Version:"));
		lblVersionTitle.setFont(boldFont);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblVersionTitle, 30, SpringLayout.SOUTH, lblSubhead);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblVersionTitle, 0, SpringLayout.WEST, lblHeadline);
		aboutPanel.add(lblVersionTitle);
		
		final JLabel lblLAVESDKVersionTitle = new JLabel(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO_SDKVERSION", mw.getLanguageID(), "SDK Version:"));
		lblLAVESDKVersionTitle.setFont(boldFont);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblLAVESDKVersionTitle, 6, SpringLayout.SOUTH, lblVersionTitle);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblLAVESDKVersionTitle, 0, SpringLayout.WEST, lblHeadline);
		aboutPanel.add(lblLAVESDKVersionTitle);
		
		final JLabel lblLAVESDKVersion = new JLabel(LAVESDKV.CURRENT.toString() + " (" + LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO_SDKVERSION_MIN", mw.getLanguageID(), "min.") + " " + LAVESDKV.MINIMUM.toString() + ")");
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblLAVESDKVersion, 6, SpringLayout.EAST, lblLAVESDKVersionTitle);
		sl_aboutPanel.putConstraint(SpringLayout.SOUTH, lblLAVESDKVersion, 0, SpringLayout.SOUTH, lblLAVESDKVersionTitle);
		aboutPanel.add(lblLAVESDKVersion);
		
		final JLabel lblVersion = new JLabel(Constants.VERSION + " (" + Constants.RELEASE_DATE + ")");
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblVersion, 0, SpringLayout.NORTH, lblVersionTitle);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblVersion, 0, SpringLayout.WEST, lblLAVESDKVersion);
		aboutPanel.add(lblVersion);
		
		final JLabel lblProductInfo = new JLabel("<html>" + LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO_PRODUCTINFO", mw.getLanguageID(), "This product includes the JLaTeXMath library and uses the Fugue Icons Set by Yusuke Kamiyamane.") + "</html>");
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblProductInfo, 30, SpringLayout.SOUTH, lblLAVESDKVersionTitle);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblProductInfo, 0, SpringLayout.WEST, lblHeadline);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, lblProductInfo, -6, SpringLayout.WEST, lblLogo);
		aboutPanel.add(lblProductInfo);
		
		final JLabel lblDevInfo = new JLabel("<html>" + LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_INFO_DEVINFO", mw.getLanguageID(), "This software is developed at the Departement of Management Information Science, University of Siegen.") + "</html>");
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblDevInfo, 6, SpringLayout.SOUTH, lblUniLogo);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblDevInfo, 0, SpringLayout.WEST, lblHeadline);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, lblDevInfo, 0, SpringLayout.EAST, lblUniLogo);
		aboutPanel.add(lblDevInfo);
		final JLabel lblDepartmentWebsite = new JLabel(Constants.DEPARTEMENT_MIS_WEBSITE);
		Utils.makeLinkLabel(lblDepartmentWebsite, Constants.DEPARTEMENT_MIS_WEBSITE);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, lblDepartmentWebsite, 3, SpringLayout.SOUTH, lblDevInfo);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, lblDepartmentWebsite, 0, SpringLayout.WEST, lblHeadline);
		aboutPanel.add(lblDepartmentWebsite);
		
		final JPanel contributorsPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_CONTRIBUTORS", mw.getLanguageID(), "Contributors"), null, contributorsPanel, null);
		contributorsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		final JTextArea contributors = new JTextArea(Constants.CONTRIBUTORS);
		contributors.setFont(f);
		contributors.setEditable(false);
		contributorsPanel.add(new JScrollPane(contributors), BorderLayout.CENTER);
		
		final JPanel licensePanel = new JPanel(new BorderLayout());
		tabbedPane.addTab(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_ABOUT_LICENSE", mw.getLanguageID(), "License"), null, licensePanel, null);
		licensePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		final JTextArea license = new JTextArea(getLicenseText());
		license.setFont(f);
		license.setEditable(false);
		licensePanel.add(new JScrollPane(license), BorderLayout.CENTER);
		
		final JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		final JButton quitButton = new JButton(LanguageFile.getLabel(mw.getLanguageFile(), "DLG_BTN_QUIT", mw.getLanguageID(), "Quit"));
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.dispose();
			}
		});
		buttonPane.add(quitButton);
		getRootPane().setDefaultButton(quitButton);
		
		setMinimumSize(new Dimension(400, 350));
	}
	
	/**
	 * Gets the license text of LAVES.
	 * 
	 * @return the license
	 * @since 1.0
	 */
	private String getLicenseText() {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(Constants.FILE_LICENSE));
			final StringBuilder s = new StringBuilder();
			String line;
			
			while((line = br.readLine()) != null) {
				s.append(line);
				s.append('\n');
			}
			
			return s.toString();
		} catch (FileNotFoundException e) {
			return "License not found!";
		} catch (IOException e) {
			return "License file could not be read!";
		}
		finally {
			if(br != null) try { br.close(); } catch(IOException e) { br = null; }
		}
	}

}
