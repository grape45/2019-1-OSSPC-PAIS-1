package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.prefs.*;
import javax.imageio.*;

public class SettingsDialog implements KeyListener, ActionListener, DocumentListener
{
	
	
	public static final String[] NAMES = {"Move ",
										  "Move ",
										  "Rotate ",
										  "Rotate ",
										  "Soft Drop ",
										  "Hard Drop ",
										  "Hold Piece",
										  "Pause",
										  "Firm Drop ",
										  null,
										  null};
	
	public static final String[] PREF_NAMES = {"move_left",
											   "move_right",
											   "rotate_clockwise",
											   "rotate_counter_clockwise",
											   "soft_drop",
											   "hard_drop",
											   "hold_piece",
											   "pause",
											   "firm_drop",
											   "das_value",
											   "arr_value"};
	
	public static final int[] DEFAULTS = {KeyEvent.VK_LEFT,
										  KeyEvent.VK_RIGHT,
										  KeyEvent.VK_UP,
										  KeyEvent.VK_Z,
										  KeyEvent.VK_DOWN,
										  KeyEvent.VK_SPACE,
										  KeyEvent.VK_SHIFT,
										  KeyEvent.VK_P,
										  KeyEvent.VK_F,
										  350,
										  100
														};
	
	private static final int[] POSITIONS = {0, 1, 2, 3, 4, 5, 7, 8, 6, -1, -1};
	
	public static final int DAS_I = 9;
	public static final int ARR_I = 10;
	
	public static final int LEN = NAMES.length;
	public static final int[] LOADED = new int[LEN];
	private static final Icon[] ICONS = new Icon[LEN];
	
	private static final String okText = "OK";
	private static final String cancelText = "Cancel";
	private static final String defaultText = "Reset to Defaults";
	
	private static final int SPACING = 40;
	private static final int W = 700;
	private static final int H = 550;
	private static final int NAMES_X = 40;
	private static final int CODES_X = 265;
	private static final int TOP_MARGIN = 20;
	private static final int LABEL_OFFSET = 17;
	private static final int BOTTOM_BUTTON_OFFSET = 17;
	private static final int MAX_BUTTON_WIDTH = 100;
	private static final int TEXT_BOX_Y = 390;
	
	public static final int MIN_DAS = 50;
	public static final int MAX_DAS = 1000;
	public static final int MIN_ARR = 0;
	public static final int MAX_ARR = 1000;
	
	public static final Font F_DIALOG = new Font(Font.DIALOG, Font.BOLD, 16);
	public static final Font F_HIGHLIGHT = new Font(Font.DIALOG, Font.BOLD, 20);
	
	private static final ImageIcon xIcon = new ImageIcon(Utility.iconToImage(UIManager.getIcon("OptionPane.errorIcon")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	
	private static final String PREF_STRING = "Tetris";
	
	private static Thread imageLoader;
	
	static
	{
		Preferences prefs = Preferences.userRoot().node(PREF_STRING);
		for (int i = 0; i < LEN; i++)
			LOADED[i] = prefs.getInt(PREF_NAMES[i], DEFAULTS[i]);
		
		imageLoader = new Thread(new Runnable() {
			public void run() {
				loadImages();
			}
		});
		imageLoader.start();
	}
	
	private static void loadImages()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			ICONS[0] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("left.png")));
		}
		catch (Exception ex)
		{
			NAMES[0] = "Move Left";
		}
		
		try {
			ICONS[1] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("right.png")));
		}
		catch (Exception ex)
		{
			NAMES[1] = "Move Right";
		}
		
		try {
			ICONS[2] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("clockwise.png")));
		}
		catch (Exception ex)
		{
			NAMES[2] = "Rotate Clockwise";
		}
		
		try {
			ICONS[3] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("counter_clockwise.png")));
		}
		catch (Exception ex)
		{
			NAMES[3] = "Rotate Counterclockwise";
		}
		
		try {
			ICONS[4] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("softdrop.png")));
		}
		catch (Exception ex)
		{}
		
		try {
			ICONS[5] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("harddrop.png")));
		}
		catch (Exception ex)
		{}
		
		try {
			ICONS[8] = new ImageIcon(ImageIO.read(loader.getResourceAsStream("firmdrop.png")));
		}
		catch (Exception ex)
		{}
	}
	
	private JDialog dialog;
	private Container pane;
	private JLabel[] labels;
	private JButton[] buttons;
	private int[] settings;
	private int[] old;
	
	private int modifying;
	
	private int BUTTON_HEIGHT;

	private JButton okButton;
	private JButton cancelButton;
	private JButton defaultButton;
	private JLabel highlight;
	private JTextField dasField;
	private JTextField arrField;
	private JLabel xDasLabel;
	private JLabel xArrLabel;
	
	public static void showDialog(JFrame frame, int[] settings)
	{
		try {
			imageLoader.join();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
			return;
		}
		new SettingsDialog(frame, settings);
	}
	
	private SettingsDialog(JFrame frame, int[] settings)
	{
		dialog = new JDialog(frame, "Settings", true);
		pane = dialog.getContentPane();
		labels = new JLabel[LEN];
		buttons = new JButton[LEN];
		modifying = -1;
		this.settings = settings;
		
		old = new int[LEN];
		for (int i = 0; i < LEN; i++)
			old[i] = settings[i];
		
		pane.setLayout(null);
		
		okButton = new JButton(okText);
		okButton.setSize(okButton.getPreferredSize());
		okButton.setLocation(W / 2 - okButton.getWidth() + 25 + BOTTOM_BUTTON_OFFSET, H - 80);
		okButton.setFocusable(false);
		okButton.addActionListener(this);
		dialog.getRootPane().setDefaultButton(okButton);
		BUTTON_HEIGHT = okButton.getHeight();
		pane.add(okButton);
		
		cancelButton = new JButton(cancelText);
		cancelButton.setSize(cancelButton.getPreferredSize());
		cancelButton.setLocation(W / 2 + 50 + BOTTOM_BUTTON_OFFSET, H - 80);
		cancelButton.setFocusable(false);
		cancelButton.addActionListener(this);
		pane.add(cancelButton);
		
		defaultButton = new JButton(defaultText);
		defaultButton.setSize(defaultButton.getPreferredSize());
		defaultButton.setLocation(W / 2 - okButton.getWidth() - defaultButton.getWidth() + BOTTOM_BUTTON_OFFSET, H - 80);
		defaultButton.setFocusable(false);
		defaultButton.addActionListener(this);
		pane.add(defaultButton);
		
		highlight = new JLabel("\u2192");
		highlight.setFont(F_HIGHLIGHT);
		highlight.setSize(highlight.getPreferredSize());
		updateHighlight();
		pane.add(highlight);
		
		dasField = new JTextField(String.valueOf(settings[DAS_I]));
		dasField.setSize(100, dasField.getPreferredSize().height);
		dasField.setLocation(W / 2 + 20, TEXT_BOX_Y);
		dasField.getDocument().addDocumentListener(this);
		pane.add(dasField);
		
		arrField = new JTextField(String.valueOf(settings[ARR_I]));
		arrField.setSize(100, arrField.getPreferredSize().height);
		arrField.setLocation(W / 2 + 20, TEXT_BOX_Y + dasField.getHeight() + 10);
		arrField.getDocument().addDocumentListener(this);
		pane.add(arrField);
		
		JLabel label;
		
		label = new JLabel("Autoshift delay (ms):");
		label.setSize(label.getPreferredSize());
		label.setLocation(W / 2 - 130, TEXT_BOX_Y + 2);
		pane.add(label);
		
		label = new JLabel("Auto repeat rate (ms):");
		label.setSize(label.getPreferredSize());
		label.setLocation(W / 2 - 130, TEXT_BOX_Y + dasField.getHeight() + 12);
		pane.add(label);
		
		xDasLabel = new JLabel(xIcon);
		xDasLabel.setSize(xDasLabel.getPreferredSize());
		xDasLabel.setLocation(W / 2 + 20 + dasField.getWidth() + 5, TEXT_BOX_Y + 2);
		pane.add(xDasLabel);
		
		xArrLabel = new JLabel(xIcon);
		xArrLabel.setSize(xArrLabel.getPreferredSize());
		xArrLabel.setLocation(W / 2 + 20 + arrField.getWidth() + 5, TEXT_BOX_Y + 32);
		pane.add(xArrLabel);
		
		for (int i = 0; i < buttons.length; i++)
		{
			if (i == DAS_I || i == ARR_I)
				continue;
			
			labels[i] = new JLabel(NAMES[i], ICONS[i], SwingConstants.CENTER);
			labels[i].setFont(F_DIALOG);
			labels[i].setSize(labels[i].getPreferredSize());
			labels[i].setLocation(NAMES_X, TOP_MARGIN + POSITIONS[i] * SPACING - labels[i].getHeight() / 2 + LABEL_OFFSET);
			labels[i].setHorizontalTextPosition(SwingConstants.LEFT);
			pane.add(labels[i]);
			
			buttons[i] = new JButton();
			buttons[i].setLocation(CODES_X, TOP_MARGIN + POSITIONS[i] * SPACING);
			buttons[i].setFocusable(false);
			buttons[i].addActionListener(this);
			buttons[i].addKeyListener(this);
			
			if (settings[i] == 0)
				buttons[i].setSize(60, BUTTON_HEIGHT);
			
			pane.add(buttons[i]);
		}
		setButtonText();
		updateFields();
		
		dialog.requestFocus();
		dialog.setResizable(false);
		dialog.addKeyListener(this);
		dialog.setSize(W, H);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	private void setButtonText()
	{
		for (int i = 0; i < LEN; i++)
		{
			if (buttons[i] == null)
				continue;
			
			if (settings[i] != 0)
			{
				buttons[i].setText(KeyEvent.getKeyText(settings[i]));
				buttons[i].setSize(buttons[i].getPreferredSize());
				if (buttons[i].getWidth() > MAX_BUTTON_WIDTH)
					buttons[i].setSize(MAX_BUTTON_WIDTH, buttons[i].getHeight());
			}
			else
			{
				buttons[i].setText("");
			}
		}
	}
	
	private void savePreferences()
	{
		Preferences prefs = Preferences.userRoot().node(PREF_STRING);
		for (int i = 0; i < LEN; i++)
		{
			prefs.putInt(PREF_NAMES[i], settings[i]);
			LOADED[i] = settings[i];
		}
	}
	
	private void updateHighlight()
	{
		if (modifying == -1)
		{
			highlight.setVisible(false);
		}
		else
		{
			highlight.setLocation(CODES_X - 21, TOP_MARGIN + SPACING * POSITIONS[modifying] - 1);
			highlight.setVisible(true);
			dialog.getRootPane().setDefaultButton(null);
		}
	}
	
	private void updateFields()
	{
		if (modifying == -1)
		{
			dasField.setEnabled(true);
			arrField.setEnabled(true);
		}
		else
		{
			dasField.setEnabled(false);
			arrField.setEnabled(false);
			dialog.requestFocus();
		}
		
		try {
			int val = Integer.parseInt(dasField.getText());
			
			if (val >= MIN_DAS && val <= MAX_DAS)
			{
				xDasLabel.setVisible(false);
				settings[DAS_I] = val;
			}
			else
			{
				xDasLabel.setVisible(true);
				if (val < MIN_DAS)
					settings[DAS_I] = MIN_DAS;
				else if (val > MAX_DAS)
					settings[DAS_I] = MAX_DAS;
				else
					settings[DAS_I] = LOADED[ARR_I];
			}
		}
		catch (Exception ex)
		{
			xDasLabel.setVisible(true);
			settings[DAS_I] = LOADED[ARR_I];
		}
		
		try {
			int val = Integer.parseInt(arrField.getText());
			
			if (val >= MIN_ARR && val <= MAX_ARR)
			{
				xArrLabel.setVisible(false);
				settings[ARR_I] = val;
			}
			else
			{
				xArrLabel.setVisible(true);
				if (val < MIN_ARR)
					settings[ARR_I] = MIN_ARR;
				else if (val > MAX_ARR)
					settings[ARR_I] = MAX_ARR;
				else
					settings[ARR_I] = LOADED[ARR_I];
			}
		}
		catch (Exception ex)
		{
			xArrLabel.setVisible(true);
			settings[ARR_I] = LOADED[ARR_I];
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		JButton b = (JButton) e.getSource();
		
		if (b == okButton)
		{
			savePreferences();
			dialog.setVisible(false);
		}
		else if (b == cancelButton)
		{
			for (int i = 0; i < LEN; i++)
				settings[i] = old[i];
			dialog.setVisible(false);
		}
		else if (b == defaultButton)
		{
			for (int i = 0; i < LEN; i++)
				settings[i] = DEFAULTS[i];
			dasField.setText(String.valueOf(DEFAULTS[DAS_I]));
			arrField.setText(String.valueOf(DEFAULTS[ARR_I]));

			modifying = -1;
			setButtonText();
			updateHighlight();
			updateFields();
		}
		
		for (int i = 0; i < LEN; i++)
		{
			if (buttons[i] == b)
			{
				if (modifying == i)
					modifying = -1;
				else
					modifying = i;
				
				updateFields();
				break;
			}
		}
		
		updateHighlight();
	}

	public void keyPressed(KeyEvent e)
	{
		if (modifying == -1)
			return;
		
		int code = e.getKeyCode();
		
		for (int i = 0; i < settings.length; i++)
		{
			if (settings[i] == code)
				settings[i] = 0;
		}
		
		settings[modifying] = code;
		modifying = -1;
		
		setButtonText();
		updateHighlight();
		updateFields();
	}
	public void keyReleased(KeyEvent e)
	{
		if (modifying == -1)
			dialog.getRootPane().setDefaultButton(okButton);
	}
	public void keyTyped(KeyEvent e) {}

	public void changedUpdate(DocumentEvent e)
	{
		updateFields();
	}
	public void insertUpdate(DocumentEvent e)
	{
		updateFields();
	}
	public void removeUpdate(DocumentEvent e)
	{
		updateFields();
	}
}
