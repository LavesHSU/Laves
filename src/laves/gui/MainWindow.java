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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import laves.Loader;
import laves.PluginManagerListener;
import laves.configuration.MainConfiguration;
import laves.gui.widgets.StatusBar;
import laves.gui.widgets.WelcomeScreen;
import laves.gui.widgets.WelcomeScreenListener;
import laves.resources.ResourceManager;
import laves.utils.Constants;
import laves.utils.Utils;
import lavesdk.algorithm.AlgorithmExerciseProvider;
import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.RTEListener;
import lavesdk.algorithm.RTEvent;
import lavesdk.algorithm.enums.AlgorithmStartOption;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginBundle;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.enums.MessageIcon;
import lavesdk.algorithm.plugin.extensions.ToolBarExtension;
import lavesdk.algorithm.plugin.views.ExercisesListView;
import lavesdk.algorithm.plugin.views.View;
import lavesdk.algorithm.plugin.views.ViewContainer;
import lavesdk.algorithm.plugin.views.ViewGroup;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.configuration.Configuration;
import lavesdk.gui.widgets.InformationBar;
import lavesdk.gui.widgets.Option;
import lavesdk.gui.widgets.OptionComboButton;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.language.LanguageFile;
import lavesdk.logging.enums.LogType;
import lavesdk.resources.Resources;
import lavesdk.utils.FileUtils;
import lavesdk.utils.MathUtils;

/**
 * The main window of LAVES.
 * 
 * @author jdornseifer
 * @version 1.4
 * @since 1.0
 */
public class MainWindow extends JFrame implements PluginHost {

	private static final long serialVersionUID = 1L;
	
	// data
	/** the loader of the program data */
	private final Loader loader;
	/** the main configuration */
	private final MainConfiguration config;
	/** the language file */
	private final LanguageFile langFile;
	/** the language id */
	private final String langID;
	/** a mapping between execution speed factors (values) and integer values (keys from <code>1</code> to <code>execSpeedFactors.size()</code>) */
    private final Map<Integer, Float> execSpeedFactors;
    /** the key of the normal execution speed (<code>1.0f</code>) in the {@link #execSpeedFactors} map */
    private final int normalExecSpeedKey;
    /** flag that indicates whether the main window is initialized */
    private boolean initialized;
    /** the active plugin or <code>null</code> if no plugin is active currently */
    private AlgorithmPlugin activePlugin;
    /** the list of the last opened plugins where the plugin of index <code>0</code> is the last active one and so on */
    private List<AlgorithmPlugin> lastOpenedPlugins;
    /** the count of the last opened plugins history */
    private final int lastOpenedPluginsCount;
	
    // GUI
	/** the event controller */
	private final EventController eventController;
    /** the toolbar of the sandbox */
	private final JToolBar toolBar;
	/** the panel with the content meaning the plugin and the information bar with the assumption and the instructions */
	private final JPanel contentPanel;
	/** the information bar with the assumption and the instructions of the plugin */
	private final InformationBar infoBar;
	/** the exercises list as the default {@link AlgorithmExerciseProvider} */
	private final ExercisesListView exercisesList;
	/** the split pane that splits the exercises list from the view container */
	private final ViewGroup splitPane;
	/** the view container of the plugin area */
	private final ViewContainer viewContainer;
	/** the statusbar of the main window */
	private final StatusBar statusBar;
	/** the new button in the toolbar */
	private final JButton newBtn;
	/** the save as button in the toolbar */
	private final JButton saveAsBtn;
	/** the open button in the toolbar */
	private final JButton openBtn;
	/** the exercise mode button in the toolbar */
	private final JToggleButton exerciseModeBtn;
	/** the start button in the toolbar */
	private final OptionComboButton startBtn;
	/** the pause button in the toolbar */
	private final JButton pauseBtn;
	/** the stop button in the toolbar */
	private final JButton stopBtn;
	/** the previous step button in the toolbar */
	private final JButton prevStepBtn;
	/** the next step button in the toolbar */
	private final JButton nextStepBtn;
	/** the skip breakpoints button in the toolbar */
	private final JToggleButton skipBreakpointsBtn;
	/** the pause before stop button in the toolbar */
	private final JToggleButton pauseBeforeStopBtn;
	/** the execution speed slider in the toolbar */
	private final JSlider execSpeedSlider;
	/** the slower button in the toolbar */
	private final JButton slowerBtn;
	/** the faster button in the toolbar */
	private final JButton fasterBtn;
	/** the reset execution speed button in the toolbar */
	private final JButton resetExecSpeedBtn;
	/** the toolbar label for the current execution speed factor of the runtime environment of the active plugin */
	private final JLabel execSpeedLbl;
	/** the file chooser */
	private final JFileChooser fileChooser;
    /** menu for the active plugin's toolbar extensions */
    private JMenu menuFunctions;
	
	// actions
	/** the action for "new" to create a new algorithm */
	private static final String ACTION_NEW = "onNew";
	/** the action for "save as" to save data of an algorithm */
	private static final String ACTION_SAVEAS = "onSaveAs";
	/** the action for "open" to open data of an algorithm */
	private static final String ACTION_OPEN = "onOpen";
	/** the action for "change rte mode" to change the mode of an algorithm */
	private static final String ACTION_CHANGERTEMODE = "onChangeRTEMode";
	/** the action for "start" to start or resume an algorithm */
	private static final String ACTION_OPT_START = "onStart";
	/** the action for "start to finish" to start an algorithm that runs to its end */
	private static final String ACTION_OPT_STARTTOFINISH = "onStartToFinish";
	/** the action for play and pause" to start an algorithm that pauses after the current step is executed */
	private static final String ACTION_OPT_PLAYANDPAUSE = "onPlayAndPause";
	/** the action for "pause" to pause the execution of an algorithm */
	private static final String ACTION_PAUSE = "onPause";
	/** the action for "stop" to stop the execution of an algorithm */
	private static final String ACTION_STOP = "onStop";
	/** the action for "previous step" to go to the previous step of an algorithm */
	private static final String ACTION_PREVSTEP = "onPrevStep";
	/** the action for "next step" to go to the next step of an algorithm */
	private static final String ACTION_NEXTSTEP = "onNextStep";
	/** the action for "pause before stop" to pause the algorithm before it stops */
	private static final String ACTION_PAUSEBEFORESTOP = "onPauseBeforeStop";
	/** the action for "skip breakpoints" to skip the breakpoints of an algorithm */
	private static final String ACTION_SKIPBREAKPOINTS = "onSkipBreakpoints";
	/** the action for "slower" slow down the execution speed of an algorithm */
	private static final String ACTION_SLOWER = "onSlower";
	/** the action for "faster" to make the execution of an algorithm faster */
	private static final String ACTION_FASTER = "onFaster";
	/** the action for "reset execution speed" to reset the execution speed of the runtime environment of the active plugin */
	private static final String ACTION_RESETEXECSPEED = "onResetExecSpeed";
	/** the action for "exit" to close the application */
	private static final String ACTION_EXIT = "onExit";
	/** the action for "install new plugins" to extend the application */
	private static final String ACTION_INSTALLNEWPLUGINS = "onInstallNewPlugins";
	/** the action for "open plugins" to open the plugins page */
	private static final String ACTION_OPENPLUGINSPAGE = "onOpenPlugins";
	/** the action for "preferences" to open the preferences of the application */
	private static final String ACTION_PREFERENCES = "onPreferences";
	/** the action for "help" to open the help of the application */
	private static final String ACTION_HELP = "onHelp";
	/** the action for "how to" to open the HowTo of the application */
	private static final String ACTION_HOWTO = "onHowTo";
	/** the action for "open website" to open the website of the application */
	private static final String ACTION_OPENWEBSITE = "onOpenWebsite";
	/** the action for "about" to open the about dialog of the application */
	private static final String ACTION_ABOUT = "onAbout";
	
	/** the identifier of a dynamic action */
	private static final String DYNACTION = "dynOn";
	/** the parameter separator of dynamic actions */
	private static final String DYNACTION_PARAMSEP = "#";
	/** a dynamic action for "open last algorithm" containing the index of the algorithm that should be opened after a "#" */
	private static final String DYNACTION_OPENRECENT = DYNACTION + "OpenLastAlgo" + DYNACTION_PARAMSEP;
	/** a dynamic action for functions containing the index of the toolbar extension that should be handled after a "#" */
	private static final String DYNACTION_SHOWFUNC = DYNACTION + "ShowFunc" + DYNACTION_PARAMSEP;
	
	/** the width of the slider in the toolbar */
	private static final int EXECSPEED_SLIDER_WIDTH = 100;
	
	/**
	 * Creates a new main window.
	 * 
	 * @param loader the loader of the program data
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if loader is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MainWindow(final Loader loader) throws IllegalArgumentException {
		super();
		
		if(loader == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.initialized = false;
		this.loader = loader;
		this.config = new MainConfiguration(loader.getMainConfiguration(), loader.getLanguageFile().getAvailableLanguages());
		this.langFile = loader.getLanguageFile();
		this.langID = config.getLanguageID();
		this.execSpeedFactors = new HashMap<Integer, Float>();
		this.normalExecSpeedKey = createExecSpeedFactors();
		this.activePlugin = null;
		this.lastOpenedPluginsCount = config.getLastOpendAlgorithmsCount();
		this.menuFunctions = null;
		
		// initialize the window
		setTitle(Constants.LAVES + " - " + Constants.LAVES_FULL);
		setIconImage(ResourceManager.getInstance().APP_ICON);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(config.getWindowWidth(), config.getWindowHeight());
		if(config.getWindowMaximized())
			setExtendedState(MAXIMIZED_BOTH);
		setLocation(config.getWindowPosX(), config.getWindowPosY());
		setLayout(new BorderLayout());
		
		// create components of the main window
		eventController = new EventController();
		contentPanel = new JPanel(new BorderLayout());
		infoBar = new InformationBar(this, langFile, langID);
		infoBar.setVisible(false);
		exercisesList = new ExercisesListView(langFile, langID);
		exercisesList.setVisible(false);
		viewContainer = new ViewContainer(0);
		splitPane = new ViewGroup(ViewGroup.HORIZONTAL, 4);
		splitPane.add(exercisesList);
		splitPane.add(viewContainer);
		splitPane.setWeights(new float[] { 0.25f, 0.75f });
		splitPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		statusBar = new StatusBar();
		fileChooser = new JFileChooser();
		
		contentPanel.add(infoBar, BorderLayout.NORTH);
		contentPanel.add(splitPane, BorderLayout.CENTER);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		// create an initial status message
		statusBar.setText(1, LanguageFile.getLabel(langFile, "WELCOME_MESSAGE", langID, "Welcome to LAVES!"));
		
		/* *** load the toolbar *** */
		
		// create new button
		newBtn = new JButton(Resources.getInstance().NEW_ICON);
		{
			newBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_NEW", langID, "New..."));
			newBtn.setActionCommand(ACTION_NEW);
			newBtn.addActionListener(eventController);
			toolBar.add(newBtn);
			toolBar.addSeparator();
		}
		// create save button
		saveAsBtn = new JButton(Resources.getInstance().SAVE_ICON);
		{
			saveAsBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_SAVE_AS", langID, "Save as..."));
			saveAsBtn.setActionCommand(ACTION_SAVEAS);
			saveAsBtn.addActionListener(eventController);
			toolBar.add(saveAsBtn);
		}
		// create open button
		openBtn = new JButton(Resources.getInstance().OPEN_ICON);
		{
			openBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_OPEN", langID, "Open..."));
			openBtn.setActionCommand(ACTION_OPEN);
			openBtn.addActionListener(eventController);
			toolBar.add(openBtn);
			toolBar.addSeparator();
		}
		// create exercise mode button
		exerciseModeBtn = new JToggleButton(Resources.getInstance().EXERCISE_MODE_ICON);
		{
			exerciseModeBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXERCISE_MODE", langID, "Exercise Mode"));
			exerciseModeBtn.setActionCommand(ACTION_CHANGERTEMODE);
			exerciseModeBtn.addActionListener(eventController);
			toolBar.add(exerciseModeBtn);
			toolBar.addSeparator();
		}
		// create options for start button
		final Option optStart = new Option(Resources.getInstance().START_ICON, LanguageFile.getLabel(langFile, "RTE_START", langID, "Start/Resume"));
		{
			optStart.setActionCommand(ACTION_OPT_START);
			optStart.addActionListener(eventController);
		}
		final Option optStart2Finish = new Option(Resources.getInstance().START_FINISH_ICON, LanguageFile.getLabel(langFile, "RTE_START_TO_FINISH", langID, "Start/Resume to Finish"));
		{
			optStart2Finish.setActionCommand(ACTION_OPT_STARTTOFINISH);
			optStart2Finish.addActionListener(eventController);
		}
		final Option optPlayAndPause = new Option(Resources.getInstance().PLAY_PAUSE_ICON, LanguageFile.getLabel(langFile, "RTE_PLAY_AND_PAUSE", langID, "Play And Pause"));
		{
			optPlayAndPause.setActionCommand(ACTION_OPT_PLAYANDPAUSE);
			optPlayAndPause.addActionListener(eventController);
		}
		startBtn = new OptionComboButton(new Option[] { optStart, optStart2Finish, optPlayAndPause });
		{
			toolBar.add(startBtn);
		}
		// create pause button
		pauseBtn = new JButton(Resources.getInstance().PAUSE_ICON);
		{
			pauseBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PAUSE", langID, "Pause"));
			pauseBtn.setActionCommand(ACTION_PAUSE);
			pauseBtn.addActionListener(eventController);
			toolBar.add(pauseBtn);
		}
		// create stop button
		stopBtn = new JButton(Resources.getInstance().STOP_ICON);
		{
			stopBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_STOP", langID, "Stop"));
			stopBtn.setActionCommand(ACTION_STOP);
			stopBtn.addActionListener(eventController);
			toolBar.add(stopBtn);
			toolBar.addSeparator();
		}
		// create previous step button
		prevStepBtn = new JButton(Resources.getInstance().PREVSTEP_ICON);
		{
			prevStepBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PREV_STEP", langID, "Previous Step"));
			prevStepBtn.setActionCommand(ACTION_PREVSTEP);
			prevStepBtn.addActionListener(eventController);
			toolBar.add(prevStepBtn);
		}
		// create next step button
		nextStepBtn = new JButton(Resources.getInstance().NEXTSTEP_ICON);
		{
			nextStepBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_NEXT_STEP", langID, "Next Step"));
			nextStepBtn.setActionCommand(ACTION_NEXTSTEP);
			nextStepBtn.addActionListener(eventController);
			toolBar.add(nextStepBtn);
			toolBar.addSeparator();
		}
		// create pause before stop button
		pauseBeforeStopBtn = new JToggleButton(Resources.getInstance().PAUSE_BEFORE_STOP_ICON);
		{
			pauseBeforeStopBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PAUSE_BEFORE_STOP", langID, "Pause Before Stop"));
			pauseBeforeStopBtn.setActionCommand(ACTION_PAUSEBEFORESTOP);
			pauseBeforeStopBtn.addActionListener(eventController);
			pauseBeforeStopBtn.setSelected(config.getToolBarOptPauseBeforeStopSelected());
			toolBar.add(pauseBeforeStopBtn);
		}
		// create skip breakpoints toggle button
		skipBreakpointsBtn = new JToggleButton(Resources.getInstance().SKIP_BREAKPOINTS_ICON);
		{
			skipBreakpointsBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_SKIP_BREAKPOINTS", langID, "Skip All Breakpoints"));
			skipBreakpointsBtn.setActionCommand(ACTION_SKIPBREAKPOINTS);
			skipBreakpointsBtn.addActionListener(eventController);
			skipBreakpointsBtn.setSelected(config.getToolBarOptSkipBreakpointsSelected());
			toolBar.add(skipBreakpointsBtn);
			toolBar.addSeparator();
		}
		// create slower button
		slowerBtn = new JButton(Resources.getInstance().SLOWER_ICON);
		{
			slowerBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_SLOWER", langID, "Slower"));
			slowerBtn.setActionCommand(ACTION_SLOWER);
			slowerBtn.addActionListener(eventController);
			toolBar.add(slowerBtn);
		}
		// create execution speed slider
		execSpeedSlider = new JSlider(1, execSpeedFactors.size(), normalExecSpeedKey);
		{
			execSpeedSlider.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_CHANGE", langID, "Change Execution Speed"));
			// restrict the slider to a specific size otherwise he would take the remaining toolbar space
			final Dimension sliderDim = new Dimension(EXECSPEED_SLIDER_WIDTH, execSpeedSlider.getPreferredSize().height);
			execSpeedSlider.setPreferredSize(sliderDim);
			execSpeedSlider.setMaximumSize(sliderDim);
			execSpeedSlider.setMinimumSize(sliderDim);
			execSpeedSlider.addChangeListener(eventController);
			toolBar.add(execSpeedSlider);
		}
		// create faster button
		fasterBtn = new JButton(Resources.getInstance().FASTER_ICON);
		{
			fasterBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_FASTER", langID, "Faster"));
			fasterBtn.setActionCommand(ACTION_FASTER);
			fasterBtn.addActionListener(eventController);
			toolBar.add(fasterBtn);
		}
		// create execution speed label
		execSpeedLbl = new JLabel();
		{
			updateExecSpeed();
			toolBar.add(execSpeedLbl);
		}
		// create reset execution speed button
		resetExecSpeedBtn = new JButton(Resources.getInstance().RESET_EXECSPEED_ICON);
		{
			resetExecSpeedBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_RESET", langID, "Reset Execution Speed"));
			resetExecSpeedBtn.setActionCommand(ACTION_RESETEXECSPEED);
			resetExecSpeedBtn.addActionListener(eventController);
			toolBar.add(resetExecSpeedBtn);
			toolBar.addSeparator();
		}
		// create a gap AS THE LAST TOOLBAR ELEMENT EXCEPT THE EXTENSIONS that is used to separate the extensions graphically
		toolBar.add(Box.createHorizontalStrut(30));
		
		// create and set the menu bar
		setJMenuBar(createMenuBar());
		
		// update button states
		updateRTECtrlButtonStates();
		updateNonRTECtrlButtonStates();
		
		// add the window listener to get notified if the main window is closed
		addWindowListener(eventController);
		
		initialized = true;
	}

	@Override
	public void adaptDialog(JDialog dlg) {
		dlg.setLocationRelativeTo(this);
		dlg.setIconImage(this.getIconImage());
	}

	@Override
	public boolean checkPermission(PluginHost host) {
		return host == this;
	}

	@Override
	public AlgorithmExerciseProvider getDefaultExerciseProvider() {
		return exercisesList;
	}

	@Override
	public LanguageFile getLanguageFile() {
		return langFile;
	}

	@Override
	public String getLanguageID() {
		return langID;
	}

	@Override
	public boolean isActivePlugin(AlgorithmPlugin plugin) {
		return plugin == activePlugin;
	}

	@Override
	public void rteModeChanged() {
		if(isValidActiveRTE()) {
			exerciseModeBtn.setSelected(activePlugin.getRuntimeEnvironment().isExerciseModeEnabled());
			
			// if the mode has changed to exercise mode then show an information dialog if necessary
			if(exerciseModeBtn.isSelected() && config.getShowExerciseModeInfoDialog())
				new ExerciseModeInfoDialog(this, config, activePlugin, true).setVisible(true);
		}
		else
			exerciseModeBtn.setSelected(false);
		
		updateRTECtrlButtonStates();
	}

	@Override
	public void showMessage(AlgorithmPlugin plugin, String msg, String title, MessageIcon icon) {
		if(!isActivePlugin(plugin))
			return;
		
		JOptionPane.showMessageDialog(this, msg, title, icon.toMessageType());
	}
	
	@Override
	public void writeLogMessage(AlgorithmPlugin plugin, String msg, LogType type) {
		writeLogMessage(plugin, msg, null, type);
	}

	@Override
	public void writeLogMessage(AlgorithmPlugin plugin, String msg, Exception e, LogType type) {
		loader.logMessage(plugin, msg, e, type);
		
		// add the log message to the statusbar
		final String oldText = statusBar.getText(1);
		statusBar.setText(1, msg);
		
		if(type == LogType.ERROR)
			statusBar.setForeground(1, Color.red);
		else if(type == LogType.WARNING)
			statusBar.setForeground(1, Color.yellow);
		
		// and reset the old statusbar text in the first field after 15 sec
		final Timer t = new Timer(15000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.statusBar.setText(1, oldText);
				MainWindow.this.statusBar.setForeground(1, Color.black);
			}
		});
		t.start();
	}

	@Override
	public int getPluginCount() {
		return loader.getPluginManager().getInstalledPlugins().size();
	}

	@Override
	public String getPluginAssumptions(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getAssumptions();
	}

	@Override
	public String getPluginAuthor(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getAuthor();
	}

	@Override
	public String getPluginAuthorContact(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getAuthorContact();
	}

	@Override
	public String getPluginDescription(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getDescription();
	}

	@Override
	public String getPluginInstructions(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getInstructions();
	}

	@Override
	public String getPluginName(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getName();
	}

	@Override
	public String getPluginProblemAffiliation(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getProblemAffiliation();
	}

	@Override
	public String getPluginSubject(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getSubject();
	}

	@Override
	public AlgorithmText getPluginText(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getText();
	}

	@Override
	public String getPluginType(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getType();
	}

	@Override
	public String getPluginVersion(int index) throws IndexOutOfBoundsException {
		return loader.getPluginManager().getInstalledPlugins().get(index).getVersion();
	}
	
	/**
	 * Gets the loader of the program data.
	 * 
	 * @return the loader of the LAVES instance
	 * @since 1.0
	 */
	public Loader getLoader() {
		return loader;
	}
	
	/**
	 * Gets a (read-only) list of the last opened algorithms in LAVES.
	 * 
	 * @return the last opened algorithms
	 * @since 1.0
	 */
	public List<AlgorithmPlugin> getLastOpenedAlgorithms() {
		return Collections.unmodifiableList(lastOpenedPlugins);
	}
	
	@Override
	protected void addImpl(Component component, Object constraints, int index) {
		if(!initialized)
			super.addImpl(component, constraints, index);
	}
	
	/**
	 * Creates the menu bar of the main window.
	 * 
	 * @return the menu bar
	 * @since 1.0
	 */
	private JMenuBar createMenuBar() {
		final JMenuBar bar;
		
		// create the bar
		bar = new JMenuBar();
		
		/*
		 * key strokes:
		 * CTRL+N = new
		 * CTRL+O = open
		 * CTRL+S = save as
		 * ALT+E = exercise mode
		 * ALT+S = start
		 * ALT+P = pause
		 * ALT+T = stop
		 * ALT+ARROWLEFT = previous step
		 * ALT+ARROWRIGHT = next step
		 * ALT+L = slower
		 * ALT+F = faster
		 * CTRL+E = preferences
		 * CTRL+F1 = help
		 */
		
		// create menu: file
		final JMenu menuFile = new JMenu(LanguageFile.getLabel(langFile, "MENU_FILE", langID, "File"));
		bar.add(menuFile);
		
		final JMenuItem itemNew = new JMenuItem(LanguageFile.getLabel(langFile, "FILE_NEW", langID, "New..."));
		itemNew.setActionCommand(ACTION_NEW);
		itemNew.addActionListener(MainWindow.this.eventController);
		itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		itemNew.setIcon(Resources.getInstance().NEW_ICON);
		
		final JMenu subMenuOpenRecent = new JMenu(LanguageFile.getLabel(langFile, "MENU_FILE_OPENRECENT", langID, "Open Recent"));
		subMenuOpenRecent.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				// the sub menu "open recent" is a dynamic menu using the list of last opened plugins
				
				JMenuItem subItem;
				
				for(int i = 0; i < MainWindow.this.lastOpenedPlugins.size(); i++) {
					subItem = new JMenuItem(MainWindow.this.lastOpenedPlugins.get(i).getName());
					subItem.setActionCommand(DYNACTION_OPENRECENT + i);
					subItem.addActionListener(MainWindow.this.eventController);
					subItem.setEnabled(MainWindow.this.newBtn.isEnabled());
					subMenuOpenRecent.add(subItem);
				}
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				subMenuOpenRecent.removeAll();
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				subMenuOpenRecent.removeAll();
			}
		});
		
		final JMenuItem itemOpen = new JMenuItem(LanguageFile.getLabel(langFile, "FILE_OPEN", langID, "Open..."));
		itemOpen.setActionCommand(ACTION_OPEN);
		itemOpen.addActionListener(eventController);
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		itemOpen.setIcon(Resources.getInstance().OPEN_ICON);
		
		final JMenuItem itemSaveAs = new JMenuItem(LanguageFile.getLabel(langFile, "FILE_SAVE_AS", langID, "Save as..."));
		itemSaveAs.setActionCommand(ACTION_SAVEAS);
		itemSaveAs.addActionListener(eventController);
		itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		itemSaveAs.setIcon(Resources.getInstance().SAVE_ICON);
		
		final JMenuItem itemExit = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_FILE_EXIT", langID, "Exit"));
		itemExit.setActionCommand(ACTION_EXIT);
		itemExit.addActionListener(eventController);
		
		menuFile.add(itemNew);
		menuFile.add(subMenuOpenRecent);
		menuFile.addSeparator();
		menuFile.add(itemOpen);
		menuFile.add(itemSaveAs);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		
		menuFile.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				// set the enabled state of the items if the menu is opened based in the states
				// of the corresponding toolbar buttons
				itemNew.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_NEW));
				subMenuOpenRecent.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_NEW));
				itemOpen.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_OPEN));
				itemSaveAs.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_SAVEAS));
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		// create menu: algorithm
		final JMenu menuAlgo = new JMenu(LanguageFile.getLabel(langFile, "MENU_ALGORITHM", langID, "Algorithm"));
		bar.add(menuAlgo);
		
		final JCheckBoxMenuItem cbiExerciseMode = new JCheckBoxMenuItem(LanguageFile.getLabel(langFile, "RTE_EXERCISE_MODE", langID, "Exercise Mode"));
		cbiExerciseMode.setActionCommand(ACTION_CHANGERTEMODE);
		cbiExerciseMode.addActionListener(eventController);
		cbiExerciseMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK));
		cbiExerciseMode.setIcon(Resources.getInstance().EXERCISE_MODE_ICON);
		
		final JMenuItem itemStart = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_START", langID, "Start/Resume"));
		itemStart.setActionCommand(ACTION_OPT_START);
		itemStart.addActionListener(eventController);
		itemStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_MASK));
		itemStart.setIcon(Resources.getInstance().START_ICON);
		
		final JMenuItem itemStart2Finish = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_START_TO_FINISH", langID, "Start/Resume to Finish"));
		itemStart2Finish.setActionCommand(ACTION_OPT_STARTTOFINISH);
		itemStart2Finish.addActionListener(eventController);
		itemStart2Finish.setIcon(Resources.getInstance().START_FINISH_ICON);
		
		final JMenuItem itemPlayAndPause = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_PLAY_AND_PAUSE", langID, "Play And Pause"));
		itemPlayAndPause.setActionCommand(ACTION_OPT_PLAYANDPAUSE);
		itemPlayAndPause.addActionListener(eventController);
		itemPlayAndPause.setIcon(Resources.getInstance().PLAY_PAUSE_ICON);
		
		final JMenuItem itemPause = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_PAUSE", langID, "Pause"));
		itemPause.setActionCommand(ACTION_PAUSE);
		itemPause.addActionListener(eventController);
		itemPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK));
		itemPause.setIcon(Resources.getInstance().PAUSE_ICON);
		
		final JMenuItem itemStop = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_STOP", langID, "Stop"));
		itemStop.setActionCommand(ACTION_STOP);
		itemStop.addActionListener(eventController);
		itemStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_MASK));
		itemStop.setIcon(Resources.getInstance().STOP_ICON);
		
		final JMenuItem itemPrevStep = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_PREV_STEP", langID, "Previous Step"));
		itemPrevStep.setActionCommand(ACTION_PREVSTEP);
		itemPrevStep.addActionListener(eventController);
		itemPrevStep.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK));
		itemPrevStep.setIcon(Resources.getInstance().PREVSTEP_ICON);
		
		final JMenuItem itemNextStep = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_NEXT_STEP", langID, "Next Step"));
		itemNextStep.setActionCommand(ACTION_NEXTSTEP);
		itemNextStep.addActionListener(eventController);
		itemNextStep.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK));
		itemNextStep.setIcon(Resources.getInstance().NEXTSTEP_ICON);
		
		final JCheckBoxMenuItem cbiPauseBeforeStop = new JCheckBoxMenuItem(LanguageFile.getLabel(langFile, "RTE_PAUSE_BEFORE_STOP", langID, "Pause Before Stop"));
		cbiPauseBeforeStop.setActionCommand(ACTION_PAUSEBEFORESTOP);
		cbiPauseBeforeStop.addActionListener(eventController);
		cbiPauseBeforeStop.setIcon(Resources.getInstance().PAUSE_BEFORE_STOP_ICON);
		
		final JCheckBoxMenuItem cbiSkipBreakpoints = new JCheckBoxMenuItem(LanguageFile.getLabel(langFile, "RTE_SKIP_BREAKPOINTS", langID, "Skip All Breakpoints"));
		cbiSkipBreakpoints.setActionCommand(ACTION_SKIPBREAKPOINTS);
		cbiSkipBreakpoints.addActionListener(eventController);
		cbiSkipBreakpoints.setIcon(Resources.getInstance().SKIP_BREAKPOINTS_ICON);
		
		final JMenuItem itemSlower = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_SLOWER", langID, "Slower"));
		itemSlower.setActionCommand(ACTION_SLOWER);
		itemSlower.addActionListener(eventController);
		itemSlower.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK));
		itemSlower.setIcon(Resources.getInstance().SLOWER_ICON);
		
		final JMenuItem itemFaster = new JMenuItem(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_FASTER", langID, "Faster"));
		itemFaster.setActionCommand(ACTION_FASTER);
		itemFaster.addActionListener(eventController);
		itemFaster.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
		itemFaster.setIcon(Resources.getInstance().FASTER_ICON);
		
		String changeExecSpeedLabel = LanguageFile.getLabel(langFile, "RTE_EXECSPEED_CHANGE", langID, "Change Execution Speed");
		if(!changeExecSpeedLabel.endsWith("..."))
			changeExecSpeedLabel += "...";
		final JMenuItem itemChangeExecSpeed = new JMenuItem(changeExecSpeedLabel);
		itemChangeExecSpeed.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final String[] factors = new String[MainWindow.this.execSpeedFactors.size()];
				for(int i = 1; i <= factors.length; i++)
					factors[i - 1] = MainWindow.this.getExecSpeedFactorAsText(MainWindow.this.execSpeedFactors.get(i));
				
				final ChangeExecSpeedDialog dlg = new ChangeExecSpeedDialog(factors, MainWindow.this.execSpeedSlider.getValue(), MainWindow.this, MainWindow.this.langFile, MainWindow.this.langID);
				dlg.setVisible(true);
				
				if(!dlg.isCanceled())
					MainWindow.this.execSpeedSlider.setValue(dlg.getChosenFactor());
			}
		});
		
		menuAlgo.add(cbiExerciseMode);
		menuAlgo.addSeparator();
		menuAlgo.add(itemStart);
		menuAlgo.add(itemStart2Finish);
		menuAlgo.add(itemPlayAndPause);
		menuAlgo.addSeparator();
		menuAlgo.add(itemPause);
		menuAlgo.add(itemStop);
		menuAlgo.addSeparator();
		menuAlgo.add(itemPrevStep);
		menuAlgo.add(itemNextStep);
		menuAlgo.addSeparator();
		menuAlgo.add(cbiPauseBeforeStop);
		menuAlgo.add(cbiSkipBreakpoints);
		menuAlgo.addSeparator();
		menuAlgo.add(itemSlower);
		menuAlgo.add(itemFaster);
		menuAlgo.add(itemChangeExecSpeed);
		
		menuAlgo.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				final AlgorithmRTE rte = isValidActiveRTE() ? MainWindow.this.activePlugin.getRuntimeEnvironment() : null;
				
				// set the checked state of the checkbox items
				cbiExerciseMode.setSelected((rte != null) ? rte.isExerciseModeEnabled() : false);
				cbiPauseBeforeStop.setSelected((rte != null) ? rte.getPauseBeforeStop() : false);
				cbiSkipBreakpoints.setSelected((rte != null) ? rte.getSkipBreakpoints() : false);
				
				// set the enabled state based on the corresponding toolbar buttons
				cbiExerciseMode.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_CHANGERTEMODE));
				itemStart.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_OPT_START));
				itemStart2Finish.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_OPT_STARTTOFINISH));
				itemPlayAndPause.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_OPT_PLAYANDPAUSE));
				itemPause.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_PAUSE));
				itemStop.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_STOP));
				itemPrevStep.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_PREVSTEP));
				itemNextStep.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_NEXTSTEP));
				cbiPauseBeforeStop.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_PAUSEBEFORESTOP));
				cbiSkipBreakpoints.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_SKIPBREAKPOINTS));
				itemSlower.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_SLOWER));
				itemFaster.setEnabled(MainWindow.this.isActionEnabled(MainWindow.ACTION_FASTER));
				itemChangeExecSpeed.setEnabled(MainWindow.this.execSpeedSlider.isEnabled());
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		// create menu: view
		final JMenu menuView = new JMenu(LanguageFile.getLabel(langFile, "MENU_VIEW", langID, "View"));
		bar.add(menuView);
		
		menuView.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				// no plugin enabled? then do not create the view menu
				if(MainWindow.this.activePlugin == null)
					return;
				
				final List<View> views = MainWindow.this.viewContainer.queryAllViews();
				JCheckBoxMenuItem cbiItem;
				
				for(int i = 0; i < views.size(); i++) {
					final View view = views.get(i);
					
					// if a view is not closable then he may not be shown in the menu
					if(!view.isClosable())
						continue;
					
					cbiItem = new JCheckBoxMenuItem(view.getTitle());
					cbiItem.setSelected(view.isVisible());
					cbiItem.setIcon(ResourceManager.getInstance().ICON_VIEW);
					cbiItem.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							view.setVisible(!view.isVisible());
						}
					});
					menuView.add(cbiItem);
				}
				
				if(views.size() > 0)
					menuView.addSeparator();
				
				final JCheckBoxMenuItem itemInfoBar = new JCheckBoxMenuItem(LanguageFile.getLabel(langFile, "MENU_VIEW_INFOBAR", langID, "Information Bar"));
				itemInfoBar.setSelected(MainWindow.this.infoBar.isVisible());
				itemInfoBar.setEnabled(MainWindow.this.infoBar.isActivatable());
				itemInfoBar.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						MainWindow.this.infoBar.setVisible(!MainWindow.this.infoBar.isVisible());
					}
				});
				menuView.add(itemInfoBar);
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				menuView.removeAll();
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				menuView.removeAll();
			}
		});
		
		// create menu: tools
		final JMenu menuTools = new JMenu(LanguageFile.getLabel(langFile, "MENU_TOOLS", langID, "Tools"));
		bar.add(menuTools);
		
		final JMenuItem itemInstallNewPlugins = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_TOOLS_INSTALLNEWPLUGINS", langID, "Install New Plugins..."));
		itemInstallNewPlugins.setActionCommand(ACTION_INSTALLNEWPLUGINS);
		itemInstallNewPlugins.addActionListener(eventController);
		itemInstallNewPlugins.setIcon(ResourceManager.getInstance().ICON_PLUGIN);
		menuTools.add(itemInstallNewPlugins);
		
		final JMenuItem itemPlugins = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_TOOLS_OPENPLUGINS", langID, "Open Plugins Page"));
		itemPlugins.setActionCommand(ACTION_OPENPLUGINSPAGE);
		itemPlugins.addActionListener(eventController);
		menuTools.add(itemPlugins);
		menuTools.addSeparator();
		
		final JMenuItem itemPreferences = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_TOOLS_PREFERENCES", langID, "Preferences"));
		itemPreferences.setActionCommand(ACTION_PREFERENCES);
		itemPreferences.addActionListener(eventController);
		itemPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
		menuTools.add(itemPreferences);
		
		// create menu: help
		final JMenu menuHelp = new JMenu(LanguageFile.getLabel(langFile, "MENU_HELP", langID, "Help"));
		bar.add(menuHelp);

		final JMenuItem itemHelp = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_HELP_HELP", langID, "LAVES Help"));
		itemHelp.setActionCommand(ACTION_HELP);
		itemHelp.addActionListener(eventController);
		itemHelp.setIcon(ResourceManager.getInstance().ICON_HELP);
		itemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
		menuHelp.add(itemHelp);
		
		final JMenuItem itemHowTo = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_HELP_HOWTO", langID, "HowTo"));
		itemHowTo.setActionCommand(ACTION_HOWTO);
		itemHowTo.addActionListener(eventController);
		menuHelp.add(itemHowTo);
		
		final JMenuItem itemInstructions = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_HELP_INSTRUCTIONS", langID, "Instructions"));
		itemInstructions.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainWindow.this, new JLabel("<html>" + MainWindow.this.activePlugin.getInstructions() + "</html>"), LanguageFile.getLabel(langFile, "MENU_HELP_INSTRUCTIONS", langID, "Instructions"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuHelp.add(itemInstructions);
		menuHelp.addSeparator();
		
		final JMenuItem itemWebsite = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_HELP_WEBSITE", langID, "LAVES Website"));
		itemWebsite.setActionCommand(ACTION_OPENWEBSITE);
		itemWebsite.addActionListener(eventController);
		itemWebsite.setIcon(ResourceManager.getInstance().ICON_INTERNET);
		menuHelp.add(itemWebsite);
		menuHelp.addSeparator();
		
		final JMenuItem itemAbout = new JMenuItem(LanguageFile.getLabel(langFile, "MENU_HELP_ABOUT", langID, "About"));
		itemAbout.setActionCommand(ACTION_ABOUT);
		itemAbout.addActionListener(eventController);
		itemAbout.setIcon(ResourceManager.getInstance().ICON_INFO);
		menuHelp.add(itemAbout);
		
		menuHelp.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				itemInstructions.setEnabled(MainWindow.this.activePlugin != null && MainWindow.this.activePlugin.getInstructions() != null && !MainWindow.this.activePlugin.getInstructions().isEmpty());
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		return bar;
	}
	
	/**
	 * Indicates whether a given action is currently enabled.
	 * 
	 * @param action the action like {@link #ACTION_NEW}, etc.
	 * @return <code>true</code> if the action is allowed otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean isActionEnabled(final String action) {
		switch(action) {
			case ACTION_NEW:				return MainWindow.this.newBtn.isEnabled();
			case ACTION_OPEN:				return MainWindow.this.openBtn.isEnabled();
			case ACTION_SAVEAS:				return MainWindow.this.saveAsBtn.isEnabled();
			case ACTION_CHANGERTEMODE:		return MainWindow.this.exerciseModeBtn.isEnabled();
			case ACTION_OPT_START:
			case ACTION_OPT_STARTTOFINISH:
			case ACTION_OPT_PLAYANDPAUSE:	return MainWindow.this.startBtn.isEnabled();
			case ACTION_PAUSE:				return MainWindow.this.pauseBtn.isEnabled();
			case ACTION_STOP:				return MainWindow.this.stopBtn.isEnabled();
			case ACTION_PREVSTEP:			return MainWindow.this.prevStepBtn.isEnabled();
			case ACTION_NEXTSTEP:			return MainWindow.this.nextStepBtn.isEnabled();
			case ACTION_PAUSEBEFORESTOP:	return MainWindow.this.pauseBeforeStopBtn.isEnabled();
			case ACTION_SKIPBREAKPOINTS:	return MainWindow.this.skipBreakpointsBtn.isEnabled();
			case ACTION_SLOWER:				return MainWindow.this.slowerBtn.isEnabled();
			case ACTION_FASTER:				return MainWindow.this.fasterBtn.isEnabled();
			default:						return true;
		}
	}
	
	/**
	 * Saves the configuration data of the host application and the plugins.
	 * 
	 * @since 1.0
	 */
	private void saveConfigurationData() {
		final boolean isMaximized = (getExtendedState() & MAXIMIZED_BOTH) != 0;
		Configuration cfg;
		
		// store the current window data
		if(!isMaximized) {
			// the window size and position may only be stored if the window is not maximized
			// otherwise the desktop size and (0,0) would be stored
			config.setWindowWidth(getWidth());
			config.setWindowHeight(getHeight());
			config.setWindowPosX(getX());
			config.setWindowPosY(getY());
		}
		config.setWindowMaximized(isMaximized);
		
		// store additional data
		config.setLastOpenedAlgorithms(lastOpenedPlugins, loader.getPluginManager());
		config.setToolBarOptPauseBeforeStopSelected(pauseBeforeStopBtn.isSelected());
		config.setToolBarOptSkipBreakpointsSelected(skipBreakpointsBtn.isSelected());
		
		// save the configuration data
		try {
			Configuration.save(Constants.FILE_MAIN_CONFIG, config.getConfiguration());
		} catch (IllegalArgumentException | IOException e) {
			loader.logMessage("Main configuration could not be saved!", LogType.ERROR);
		}
		
		// save the configuration data of each plugin
		for(PluginBundle bundle : loader.getPluginManager().getPluginBundles()) {
			try {
				cfg = bundle.getPlugin().getConfiguration();
				if(cfg != null)
					Configuration.save(Constants.PATH_PLUGINS_CONFIG + bundle.getSimpleName() + Constants.EXT_CONFIG, cfg);
			} catch (IllegalArgumentException | IOException e) {
				loader.logMessage(bundle.getPlugin(), "Configuration of plugin " + bundle.getName() + " could not be saved!", e, LogType.ERROR);
			}
		}
	}
	
	/**
	 * Activates a new algorithm plugin.
	 * 
	 * @see #closeActivePlugin()
	 * @param plugin the plugin
	 * @param creatorProperties the creator properties of the plugin or <code>null</code> if the plugin does not have creator properties
	 * @since 1.0
	 */
	private void activatePlugin(final AlgorithmPlugin plugin, final PropertiesListModel creatorProperties) {
		if(plugin == null)
			return;
		
		// firstly close the currently active plugin and clear the view container (is done in the method)
		closeActivePlugin();
		
		// set the new active plugin
		activePlugin = plugin;
		// request the plugin's runtime environment
		final AlgorithmRTE rte = activePlugin.getRuntimeEnvironment();
		
		if(rte != null) {
			// add the listener of runtime events
			rte.addListener(eventController);
			// activate the "pause before stop"- and "skip breakpoints"-option if necessary meaning if the correponding button
			// is selected in the toolbar
			rte.setPauseBeforeTerminate(pauseBeforeStopBtn.isSelected());
			rte.setSkipBreakpoints(skipBreakpointsBtn.isSelected());
		}
		
		// create an extension menu for toolbar functions
		menuFunctions = new JMenu(LanguageFile.getLabel(langFile, "MENU_FUNCTIONS", langID, "Functions"));
		menuFunctions.setEnabled(false);
		getJMenuBar().add(menuFunctions, 2);
		
		// load the toolbar extensions
		if(activePlugin.getToolBarExtensions() != null) {
			for(int i = 0; i < activePlugin.getToolBarExtensions().length; i++) {
				final ToolBarExtension ext = activePlugin.getToolBarExtensions()[i];
				
				if(ext != null) {
					ext.apply(toolBar);
					if(ext.getShowInMenu()) {
						final JMenuItem itemFunc = new JMenuItem(ext.getMenuOptionText());
						itemFunc.setActionCommand(DYNACTION_SHOWFUNC + i);
						itemFunc.addActionListener(MainWindow.this.eventController);
						itemFunc.setIcon(ext.getMenuOptionIcon());
						menuFunctions.add(itemFunc);
						menuFunctions.setEnabled(true);
					}
				}
			}
			toolBar.repaint();
		}
		
		// update the list of the last opened plugins
		if(lastOpenedPlugins.contains(activePlugin))
			lastOpenedPlugins.remove(activePlugin);
		else if(lastOpenedPlugins.size() == lastOpenedPluginsCount)
			lastOpenedPlugins.remove(lastOpenedPluginsCount - 1);
		lastOpenedPlugins.add(0, activePlugin);
		
		// reset the exercises list
		exercisesList.reset();
		exercisesList.setVisible(rte != null && rte.isExerciseModeEnabled());
		rteModeChanged();
		
		// create the plugin
		activePlugin.onCreate(viewContainer, creatorProperties);
		viewContainer.revalidate();
		
		// update the information bar
		infoBar.update(activePlugin);
		if(config.getDisableInformationBarPermanently())
			infoBar.setVisible(false);
		
		// set the name, description and problem affiliation of the plugin in the statusbar
		statusBar.setText(2, activePlugin.getDescription());
		statusBar.setText(3, activePlugin.getName());
		statusBar.setText(4, activePlugin.getProblemAffiliation());
		
		// update the toolbar buttons
		updateRTECtrlButtonStates();
		updateNonRTECtrlButtonStates();
	}
	
	/**
	 * Closes the active plugin.
	 * 
	 * @since 1.0
	 */
	private void closeActivePlugin() {
		if(activePlugin != null) {
			// close the plugin
			activePlugin.onClose();
			
			if(activePlugin.getRuntimeEnvironment() != null) {
				// stop the runtime environment in case of the algorithm was not stopped yet (although this should not happen)
				activePlugin.getRuntimeEnvironment().stop();
				// remove the listener of runtime events
				activePlugin.getRuntimeEnvironment().removeListener(eventController);
			}
			
			// remove the toolbar extensions of the plugin
			if(activePlugin.getToolBarExtensions() != null) {
				for(ToolBarExtension ext : activePlugin.getToolBarExtensions()) {
					if(ext != null)
						ext.remove(toolBar);
				}
				toolBar.repaint();
			}
		}
		
		// if plugin has added a new menu entry then remove it
		if(menuFunctions != null) {
			getJMenuBar().remove(menuFunctions);
			menuFunctions = null;
		}
		
		activePlugin = null;
		
		// delete the statusbar entires
		statusBar.setText(2, "");
		statusBar.setText(3, "");
		statusBar.setText(4, "");
		
		// clear the view container
		viewContainer.removeAll();
		viewContainer.setLayout(null);
		viewContainer.revalidate();
		viewContainer.repaint();
		
		// update the toolbar buttons
		updateRTECtrlButtonStates();
	}
	
	/**
	 * Loads the welcome screen if necessary.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This has to be done when the application window opens (<b>not in the constructor</b>) because the plugins are initialized
	 * after the main GUI is loaded.
	 * 
	 * @since 1.0
	 */
	private void loadWelcomeScreen() {
		// load the welcome screen but only if necessary
		if(!config.getShowWelcomeScreen())
			return;
		
		final WelcomeScreen ws = new WelcomeScreen(this, config);
		ws.setWelcomeScreenListener(new WelcomeScreenListener() {
			
			@Override
			public void activatePlugin(AlgorithmPlugin plugin) {
				MainWindow.this.activatePlugin(plugin, null);
			}
		});
		
		viewContainer.setLayout(new BorderLayout());
		viewContainer.add(ws, BorderLayout.CENTER);
		viewContainer.revalidate();
	}
	
	/**
	 * Validates the last opened algorithms list and the active plugin.
	 * <br><br>
	 * This is necessary when the user deinstalls plugins.
	 * 
	 * @since 1.0
	 */
	private void validatePlugins() {
		final List<AlgorithmPlugin> installedPlugins = loader.getPluginManager().getInstalledPlugins();
		
		// if the active plugin is not available any more then close it
		if(activePlugin != null && !installedPlugins.contains(activePlugin))
			closeActivePlugin();
		
		// check whether the list of last opened plugins is valid
		for(int i = lastOpenedPlugins.size() - 1; i >= 0; i--)
			if(!installedPlugins.contains(lastOpenedPlugins.get(i)))
				lastOpenedPlugins.remove(i);
	}
	
	/**
	 * The application window is opened.
	 * 
	 * @since 1.0
	 */
	private void onOpenApplication() {
		lastOpenedPlugins = config.getLastOpenedAlgorithms(loader.getPluginManager());
		
		loadWelcomeScreen();
		
		// if a plugin is deinstalled then check whether an open recent has to be removed
		loader.getPluginManager().addListener(new PluginManagerListener() {
			
			@Override
			public void onInstalledPluginsChanged(boolean increase) {
				if(!increase)
					MainWindow.this.validatePlugins();
			}
		});
		
		// log that LAVES was started with errors
		if(loader.hasErrors())
			writeLogMessage(null, LanguageFile.getLabel(langFile, "MSG_OPENEDWITHERRORS", langID, "Opened with errors (see log/log.txt for further information)!"), LogType.ERROR);
	}
	
	/**
	 * The application is terminated meaning that the method closes the active plugin and saves the configuration data.
	 * 
	 * @since 1.0
	 */
	private void onTerminateApplication() {
		// stop the runtime environment of the active algorithm
		onStop();
		
		// firstly close the active plugin so that configuration data can be stored
		closeActivePlugin();
		// afterwards save the entire configuration
		saveConfigurationData();
	}
	
	/**
	 * Does the action "new".
	 * 
	 * @since 1.0
	 */
	private void onNew() {
		final NewDialog dlg = new NewDialog(this, config);
		dlg.setVisible(true);
		
		if(dlg.getSelectedPlugin() != null)
			activatePlugin(dlg.getSelectedPlugin(), dlg.getCreatorPreferences());
	}
	
	/**
	 * Does the save as action of the active plugin.
	 * 
	 * @since 1.0
	 */
	private void onSaveAs() {
		if(activePlugin == null || activePlugin.getSaveFileFilters() == null)
			return;

		resetFileChooser();
		
		// add all file filters of the plugin to the file chooser
		for(FileNameExtensionFilter fnef : activePlugin.getSaveFileFilters())
			if(fnef != null)
				fileChooser.addChoosableFileFilter(fnef);
		
		final int result = fileChooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			// approve the selection
			final FileNameExtensionFilter fnef = (FileNameExtensionFilter)fileChooser.getFileFilter();
			final File file = FileUtils.validateFile(fileChooser.getSelectedFile(), fnef.getExtensions()[0]);
			String msg = LanguageFile.getLabel(langFile, "MSG_WARN_SAVEEXISTINGFILE", langID, "The file \"&name&\" already exists.\nDo you want to overwrite it?");
			msg = msg.replace("&name&", file.getAbsolutePath());
			
			if(file.exists() && JOptionPane.showConfirmDialog(this, msg, LanguageFile.getLabel(langFile, "MSG_WARN_TITLE_SAVEEXISTINGFILE", langID, "Save as"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
				return;
			
			// validate the selected file in causa of a valid extension so that several file types
			// can be handled in the save method of the plugin
			activePlugin.save(FileUtils.validateFile(fileChooser.getSelectedFile(), fnef.getExtensions()[0]));
		}
	}
	
	/**
	 * Does the open action of the active plugin.
	 * 
	 * @since 1.0
	 */
	private void onOpen() {
		if(activePlugin == null || activePlugin.getOpenFileFilters() == null)
			return;
		
		resetFileChooser();
		
		// add all file filters of the plugin to the file chooser
		for(FileNameExtensionFilter fnef : activePlugin.getOpenFileFilters())
			if(fnef != null)
				fileChooser.addChoosableFileFilter(fnef);
		
		final int result = fileChooser.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			final FileNameExtensionFilter fnef = (FileNameExtensionFilter)fileChooser.getFileFilter();
			
			// validate the selected file in causa of a valid extension so that several file types
			// can be handled in the open method of the plugin
			activePlugin.open(FileUtils.validateFile(fileChooser.getSelectedFile(), fnef.getExtensions()[0]));
		}
	}
	
	/**
	 * Does the change rte mode action to change the execution mode of the runtime environment of the active plugin.
	 * 
	 * @since 1.0
	 */
	private void onChangeMode() {
		if(isValidActiveRTE()) {
			if(activePlugin.getRuntimeEnvironment().isStarted()) {
				exerciseModeBtn.setSelected(activePlugin.getRuntimeEnvironment().isExerciseModeEnabled());
				JOptionPane.showMessageDialog(this, LanguageFile.getLabel(langFile, "MSG_INFO_EXERCISEMODE", langID, "The exercise mode can only be activated/deactivated when the algorithm is stopped!"), LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_EXERCISEMODE", langID, "Exercise Mode"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			activePlugin.getRuntimeEnvironment().setExerciseModeEnabled(!activePlugin.getRuntimeEnvironment().isExerciseModeEnabled());
		}
	}
	
	/**
	 * Starts the runtime environment of the active plugin but only if their is a runtime environment.
	 * 
	 * @param option the start option
	 * @since 1.0
	 */
	private void onStart(final AlgorithmStartOption option) {
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().start(option);
	}
	
	/**
	 * Pauses the runtime environment of the active plugin but only if their is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void onPause() {
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().pause();
	}
	
	/**
	 * Stops the runtime environment of the active plugin but only if their is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void onStop() {
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().stop();
	}
	
	/**
	 * Goes to the previous step in the runtime environment of the active plugin but only if their is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void onPreviousStep() {
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().prevStep();
	}
	
	/**
	 * Goes to the next step in the runtime environment of the active plugin but only if their is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void onNextStep() {
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().nextStep();
	}
	
	/**
	 * Changes the pause before stop flag of the runtime environment of the active plugin.
	 * 
	 * @since 1.0
	 */
	private void onPauseBeforeStop() {
		final AlgorithmRTE rte = isValidActiveRTE() ? activePlugin.getRuntimeEnvironment() : null;
		
		if(rte != null) {
			rte.setPauseBeforeTerminate(!rte.getPauseBeforeStop());
			pauseBeforeStopBtn.setSelected(rte.getPauseBeforeStop());
		}
		else
			pauseBeforeStopBtn.setSelected(false);
	}
	
	/**
	 * Changes the skip breakpoints flag of the runtime environment of the active plugin.
	 * 
	 * @since 1.0
	 */
	private void onSkipBreakpoints() {
		final AlgorithmRTE rte = isValidActiveRTE() ? activePlugin.getRuntimeEnvironment() : null;
		
		if(rte != null) {
			rte.setSkipBreakpoints(!rte.getSkipBreakpoints());
			skipBreakpointsBtn.setSelected(rte.getSkipBreakpoints());
		}
		else
			skipBreakpointsBtn.setSelected(false);
	}
	
	/**
	 * Opens the dialog to install new plugins.
	 * 
	 * @since 1.0
	 */
	private void onInstallNewPlugins() {
		final PluginManagerDialog plugManDlg = new PluginManagerDialog(this, config);
		plugManDlg.setVisible(true);
	}
	
	/**
	 * Opens the website (page) of the available plugins.
	 * 
	 * @since 1.0
	 */
	private void onOpenPluginsPage() {
		if(!Utils.openWebsite(Constants.PLUGINS_PAGE))
			JOptionPane.showMessageDialog(this, LanguageFile.getLabel(langFile, "MSG_ERR_OPENWEBSITE", langID, "The website could not be opened!\nEnsure that you have installed a default browser."), LanguageFile.getLabel(langFile, "MSG_ERR_TITLE_OPENWEBSITE", langID, "Open Website"), JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Opens the preferences dialog.
	 * 
	 * @since 1.0
	 */
	private void onPreferences() {
		final PreferencesDialog prefDlg = new PreferencesDialog(this, config);
		prefDlg.setVisible(true);
	}
	
	/**
	 * Opens the help (file) of LAVES.
	 * 
	 * @since 1.0
	 */
	private void onHelp() {
		final String helpFile = Constants.PATH_HELP + langID + FileUtils.FILESEPARATOR + Constants.FILENAME_HELP;
		
		final int res = Utils.openFile(helpFile);
		switch(res) {
			case -1:
				JOptionPane.showMessageDialog(this, LanguageFile.getLabel(langFile, "MSG_INFO_CANNOTOPENHELPFILE", langID, "The user guide cannot be opened!\nEnsure that you have installed a suitable PDF-Reader."), LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_CANNOTOPENHELPFILE", langID, "User guide"), JOptionPane.INFORMATION_MESSAGE);
				break;
			case 0:
				JOptionPane.showMessageDialog(this, LanguageFile.getLabel(langFile, "MSG_INFO_NOHELPFILE", langID, "Their is no user guide available for the selected language!"), LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_NOHELPFILE", langID, "No user guide available"), JOptionPane.INFORMATION_MESSAGE);
				break;
		}
	}
	
	/**
	 * Opens the HowTo of LAVES.
	 * 
	 * @since 1.0
	 */
	private void onHowTo() {
		final HowToDialog howToDlg = new HowToDialog(this);
		howToDlg.setVisible(true);
	}
	
	/**
	 * Opens the website of LAVES.
	 * 
	 * @since 1.0
	 */
	private void onOpenWebsite() {
		if(!Utils.openWebsite(Constants.LAVES_WEBSITE))
			JOptionPane.showMessageDialog(this, LanguageFile.getLabel(langFile, "MSG_ERR_OPENWEBSITE", langID, "The website could not be opened!\nEnsure that you have installed a default browser."), LanguageFile.getLabel(langFile, "MSG_ERR_TITLE_OPENWEBSITE", langID, "Open Website"), JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Opens the about dialog of the application.
	 * 
	 * @since 1.0
	 */
	private void onAbout() {
		final AboutDialog aboutDlg = new AboutDialog(this);
		aboutDlg.setVisible(true);
	}
	
	/**
	 * Resets {@link #fileChooser} meaning that the last file filters are removed and the selection and
	 * accept file options are reset.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is necessary because {@link #fileChooser} is used for each plugin so that the last location can be retained.
	 * 
	 * @since 1.0
	 */
	private void resetFileChooser() {
		final FileFilter[] filters = fileChooser.getChoosableFileFilters();
		for(FileFilter f : filters)
			fileChooser.removeChoosableFileFilter(f);
		
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	
	/**
	 * Indicates whether the {@link #activePlugin} has a valid runtime environment meaning <code>activePlugin != null &&
	 * activePlugin.getRuntimeEnvironment() != null</code>.
	 * 
	 * @return <code>true</code> if their is an active plugin with a valid runtime environment
	 * @since 1.0
	 */
	private boolean isValidActiveRTE() {
		return (activePlugin != null && activePlugin.getRuntimeEnvironment() != null);
	}
	
	/**
	 * Checks and updates the state of each runtime environment control (start, stop, ...) whether it is enabled or not.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is necessary because it might be that a plugin does not have a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void updateRTECtrlButtonStates() {
		final AlgorithmRTE rte = (activePlugin != null) ? activePlugin.getRuntimeEnvironment() : null;
		final boolean rteState = (rte != null);
		
		exerciseModeBtn.setEnabled(rteState && activePlugin.hasExerciseMode());
		startBtn.setEnabled(rteState && !rte.isRunning());
		pauseBtn.setEnabled(rteState && rte.isRunning() && !rte.isExerciseModeEnabled());
		stopBtn.setEnabled(rteState && rte.isStarted());
		nextStepBtn.setEnabled(rteState && rte.isStarted() && !rte.isExerciseModeEnabled());
		prevStepBtn.setEnabled(rteState && rte.isStarted() && !rte.isExerciseModeEnabled());
		pauseBeforeStopBtn.setEnabled(rteState);
		skipBreakpointsBtn.setEnabled(rteState);
		slowerBtn.setEnabled(rteState);
		execSpeedSlider.setEnabled(rteState);
		fasterBtn.setEnabled(rteState);
		resetExecSpeedBtn.setEnabled(rteState);
	}
	
	/**
	 * Checks and updates the state of each toolbar button, that is not an runtime environment control (like New, Save As, ...)
	 * whether it is enabled or not.
	 * <br><br>
	 * The toolbar extension are non runtime environment controls to meaning if the rte of the current plugin is started the extensions
	 * will be disabled.
	 * 
	 * @since 1.0
	 */
	private void updateNonRTECtrlButtonStates() {
		final boolean validActivePlugin = (activePlugin != null);
		final boolean rteIsStarted = (validActivePlugin && activePlugin.getRuntimeEnvironment() != null) ? activePlugin.getRuntimeEnvironment().isStarted() : false;
		
		newBtn.setEnabled(!rteIsStarted);
		saveAsBtn.setEnabled(!rteIsStarted && (validActivePlugin && activePlugin.getSaveFileFilters() != null));
		openBtn.setEnabled(!rteIsStarted && (validActivePlugin && activePlugin.getOpenFileFilters() != null));
		
		if(activePlugin != null && activePlugin.getToolBarExtensions() != null) {
			for(ToolBarExtension tbe : activePlugin.getToolBarExtensions()) {
				if(tbe != null)
					tbe.setEnabled(!rteIsStarted);
			}
		}
	}
	
	/**
	 * Updates the execution speed of the runtime environment with the current value of the {@link #execSpeedSlider}.
	 * 
	 * @since 1.0
	 */
	private void updateExecSpeed() {
		final float execSpeedFactor = execSpeedFactors.get(execSpeedSlider.getValue());
		if(isValidActiveRTE())
			activePlugin.getRuntimeEnvironment().setExecSpeedFactor(execSpeedFactor);
		
		String execSpeedDesc = LanguageFile.getLabel(langFile, "RTE_EXECSPEED_NORMAL", langID, "Normal");
		if(execSpeedFactor < 1.0f)
			execSpeedDesc = "" + MathUtils.formatFloat(1.0f / execSpeedFactor) + "x " + LanguageFile.getLabel(langFile, "RTE_EXECSPEED_SLOWER", langID, "Slower");
		else if(execSpeedFactor > 1.0f)
			execSpeedDesc = "" + MathUtils.formatFloat(execSpeedFactor) + "x " + LanguageFile.getLabel(langFile, "RTE_EXECSPEED_FASTER", langID, "Faster");
		
		execSpeedLbl.setText(getExecSpeedFactorAsText(execSpeedFactor));
		execSpeedLbl.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED", langID, "Execution Speed") + ": " + execSpeedDesc);
		execSpeedLbl.repaint();
	}
	
	/**
	 * Creates the execution speed factors and stores them in {@link #execSpeedFactors}.
	 * 
	 * @return the key of the normal execution speed (<code>1.0f</code>)
	 * @since 1.0
	 */
	private int createExecSpeedFactors() {
		int normalFactorKey;
		
		execSpeedFactors.put(1, 0.1f);
		execSpeedFactors.put(2, 0.125f);
		execSpeedFactors.put(3, 0.25f);
		execSpeedFactors.put(4, 0.5f);
		execSpeedFactors.put(5, 0.75f);
		execSpeedFactors.put((normalFactorKey = 6), 1.0f);
		execSpeedFactors.put(7, 2.0f);
		execSpeedFactors.put(8, 4.0f);
		execSpeedFactors.put(9, 8.0f);
		execSpeedFactors.put(10, 16.0f);
		
		return normalFactorKey;
	}
	
	/**
	 * Gets an execution speed factor as a text representation.
	 * 
	 * @param factor the factor
	 * @return the text representation
	 * @since 1.0
	 */
	private String getExecSpeedFactorAsText(final float factor) {
		final int intFactor = (int)factor;
		final String result = ((float)intFactor == factor) ? "" + intFactor : "" + factor;
		
		return result + "x";
	}
	
	/**
	 * Extracts the parameter of a dynamic action.
	 * 
	 * @param dynAction the dynamic action
	 * @return the parameter value
	 * @since 1.0
	 */
	private String getDynamicActionParameter(final String dynAction) {
		final int paramSep = dynAction.lastIndexOf(DYNACTION_PARAMSEP);
		
		if(paramSep >= 0)
			return dynAction.substring(paramSep + 1, dynAction.length());
		else
			return "";
	}
	
	/**
	 * Handles the events of the main window.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private class EventController implements WindowListener, ActionListener, ChangeListener, RTEListener {
		
		private boolean closed = false;
		private boolean opened = false;

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
			windowClosing(e);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if(closed)
				return;
			
			MainWindow.this.onTerminateApplication();
			
			closed = true;
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
			if(opened)
				return;
			
			MainWindow.this.onOpenApplication();
			
			opened = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final String action = e.getActionCommand();
			
			// first check whether the action event is a dynamic action
			if(action.startsWith(DYNACTION)) {
				final String paramVal = getDynamicActionParameter(action);
				
				if(action.startsWith(DYNACTION_OPENRECENT)) {
					try {
						final Integer index = new Integer(paramVal);
						MainWindow.this.activatePlugin(MainWindow.this.lastOpenedPlugins.get(index), null);
					}
					catch(NumberFormatException ex) {
					}
				}
				else if(action.startsWith(DYNACTION_SHOWFUNC)) {
					try {
						final Integer index = new Integer(paramVal);
						MainWindow.this.activePlugin.getToolBarExtensions()[index.intValue()].showMenuOption();
					}
					catch(NumberFormatException ex) {
					}
				}
				
				// the other actions need not to be checked
				return;
			}
			
			// first check whether the action is allowed to be executed
			if(!isActionEnabled(action))
				return;
			
			switch(action) {
				case ACTION_NEW:
					MainWindow.this.onNew();
					break;
				case ACTION_SAVEAS:
					MainWindow.this.onSaveAs();
					break;
				case ACTION_OPEN:
					MainWindow.this.onOpen();
					break;
				case ACTION_CHANGERTEMODE:
					MainWindow.this.onChangeMode();
					break;
				case ACTION_OPT_START:
					MainWindow.this.onStart(AlgorithmStartOption.NORMAL);
					break;
				case ACTION_OPT_STARTTOFINISH:
					MainWindow.this.onStart(AlgorithmStartOption.START_TO_FINISH);
					break;
				case ACTION_OPT_PLAYANDPAUSE:
					MainWindow.this.onStart(AlgorithmStartOption.PLAY_AND_PAUSE);
					break;
				case ACTION_PAUSE:
					MainWindow.this.onPause();
					break;
				case ACTION_STOP:
					MainWindow.this.onStop();
					break;
				case ACTION_PREVSTEP:
					MainWindow.this.onPreviousStep();
					break;
				case ACTION_NEXTSTEP:
					MainWindow.this.onNextStep();
					break;
				case ACTION_PAUSEBEFORESTOP:
					MainWindow.this.onPauseBeforeStop();
					break;
				case ACTION_SKIPBREAKPOINTS:
					MainWindow.this.onSkipBreakpoints();
					break;
				case ACTION_SLOWER:
					MainWindow.this.execSpeedSlider.setValue(MainWindow.this.execSpeedSlider.getValue() - 1);
					break;
				case ACTION_FASTER:
					MainWindow.this.execSpeedSlider.setValue(MainWindow.this.execSpeedSlider.getValue() + 1);
					break;
				case ACTION_RESETEXECSPEED:
					MainWindow.this.execSpeedSlider.setValue(MainWindow.this.normalExecSpeedKey);
					break;
				case ACTION_EXIT:
					MainWindow.this.dispose();
					break;
				case ACTION_INSTALLNEWPLUGINS:
					MainWindow.this.onInstallNewPlugins();
					break;
				case ACTION_OPENPLUGINSPAGE:
					MainWindow.this.onOpenPluginsPage();
					break;
				case ACTION_PREFERENCES:
					MainWindow.this.onPreferences();
					break;
				case ACTION_HELP:
					MainWindow.this.onHelp();
					break;
				case ACTION_HOWTO:
					MainWindow.this.onHowTo();
					break;
				case ACTION_OPENWEBSITE:
					MainWindow.this.onOpenWebsite();
					break;
				case ACTION_ABOUT:
					MainWindow.this.onAbout();
					break;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			MainWindow.this.updateExecSpeed();
		}

		@Override
		public void beforeStart(RTEvent e) {
		}

		@Override
		public void beforeResume(RTEvent e) {
		}

		@Override
		public void beforePause(RTEvent e) {
		}

		@Override
		public void onStop() {
			MainWindow.this.updateRTECtrlButtonStates();
			MainWindow.this.updateNonRTECtrlButtonStates();
		}

		@Override
		public void onRunning() {
			MainWindow.this.updateRTECtrlButtonStates();
			MainWindow.this.updateNonRTECtrlButtonStates();
		}

		@Override
		public void onPause() {
			MainWindow.this.updateRTECtrlButtonStates();
		}
		
	}

}
