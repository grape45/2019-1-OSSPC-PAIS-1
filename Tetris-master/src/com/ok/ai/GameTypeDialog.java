package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameTypeDialog implements ActionListener
{

	public static int showDialog(JFrame frame, int def)
	{
		return new GameTypeDialog(frame, def).choice;
	}
	
	public int choice;
	
	private static final String okString = "OK";
	private static final String cancelString = "Cancel";
	
	private JDialog dialog;
	
	private ButtonGroup group;
	private JRadioButton marathonButton;
	private JRadioButton sprintButton;
	private JRadioButton matchButton;
	private JRadioButton battleButton;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private GameTypeDialog(JFrame frame, int def)
	{
		dialog = new JDialog(frame, "New Game", true);
		Container pane = dialog.getContentPane();
		pane.setLayout(null);
		
		group = new ButtonGroup();
		
		JRadioButton button;
		
		button = new JRadioButton("Marathon");
		button.setSize(button.getPreferredSize());
		button.setLocation(40, 20);
		button.addActionListener(this);
		button.setVisible(true);
		group.add(button);
		pane.add(button);
		marathonButton = button;
		
		button = new JRadioButton("Sprint");
		button.setSize(button.getPreferredSize());
		button.setLocation(40, 45);
		button.addActionListener(this);
		button.setVisible(true);
		group.add(button);
		pane.add(button);
		sprintButton = button;
		
		button = new JRadioButton("Battle (clear garbage by sending more lines)");
		button.setSize(button.getPreferredSize());
		button.setLocation(40, 70);
		button.addActionListener(this);
		button.setVisible(true);
		group.add(button);
		pane.add(button);
		matchButton = button;
		
		button = new JRadioButton("Battle (clear garbage with tetriminos)");
		button.setSize(button.getPreferredSize());
		button.setLocation(40, 95);
		button.addActionListener(this);
		button.setVisible(true);
		group.add(button);
		pane.add(button);
		battleButton = button;

		if (def == TetrisRenderer.MARATHON)
			marathonButton.setSelected(true);
		if (def == TetrisRenderer.SPRINT)
			sprintButton.setSelected(true);
		if (def == TetrisRenderer.BATTLE)
			matchButton.setSelected(true);
		if (def == TetrisRenderer.BATTLE_GARBAGE)
			battleButton.setSelected(true);
		
		choice = def;
		
		okButton = new JButton(okString);
		okButton.setSize(okButton.getPreferredSize());
		okButton.setLocation(95, 145);
		okButton.addActionListener(this);
		okButton.setVisible(true);
		dialog.getRootPane().setDefaultButton(okButton);
		group.add(okButton);
		pane.add(okButton);
		
		cancelButton = new JButton(cancelString);
		cancelButton.setSize(cancelButton.getPreferredSize());
		cancelButton.setLocation(160, 145);
		cancelButton.addActionListener(this);
		cancelButton.setVisible(true);
		group.add(cancelButton);
		pane.add(cancelButton);

		dialog.setResizable(false);
		dialog.setSize(340, 220);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source == marathonButton)
			choice = TetrisRenderer.MARATHON;
		if (source == sprintButton)
			choice = TetrisRenderer.SPRINT;
		if (source == matchButton)
			choice = TetrisRenderer.BATTLE;
		if (source == battleButton)
			choice = TetrisRenderer.BATTLE_GARBAGE;
		
		if (source == okButton)
			dialog.setVisible(false);
		if (source == cancelButton)
		{
			choice = 0;
			dialog.setVisible(false);
		}
	}

}
