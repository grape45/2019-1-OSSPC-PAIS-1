package com.ok.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TSetting extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton setok = new JButton("OK");
	private JLabel viewleft = new JLabel("Key left");
	private JLabel viewright = new JLabel("Key right");
	private JLabel viewrotate = new JLabel("Key rotate");
	private JLabel viewdown = new JLabel("Key down");
	private JLabel viewhold = new JLabel("Key hold");
	private JLabel viewdrop = new JLabel("Key drop");
	private JLabel worksai = new JLabel("Only works AI mode");
	private JButton left_button = new JButton();
	private JButton right_button = new JButton();
	private JButton rotate_button = new JButton();
	private JButton down_button = new JButton();
	private JButton hold_button = new JButton();
	private JButton drop_button = new JButton();
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));
	
	public TMain ok;
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);
	public TSetting(TMain main) {
		this.ok = main;
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setUndecorated(true);
		setLayout(null);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setok.setBounds(1000, 300, 100, 30);
		
		menuBar.setBounds(0, 0, 1280, 30);
		exitButton.setBounds(1245, 0, 30, 30);
		exitButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitButtonEnteredImage); // ¸¶¿ì½º°¡ exit ¹öÆ°¿¡ ¿Ã¶ó°¡¸é ÀÌ¹ÌÁö¸¦ ¹Ù²ãÁÜ.
				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ¸¶¿ì½º°¡ ¿Ã¶ó°¡¸é ¼Õ°¡¶ô ¸ð¾çÀ¸·Î¹Ù²Þ
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitButtonBasicImage);
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // ¸¶¿ì½º¸¦ ¶¼¸é ´Ù½Ã µðÆúÆ® ¸ð¾çÀ¸·Î ¹Ù²Þ
			}

			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});
		
		viewleft.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		viewright.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		viewrotate.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		viewdown.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		viewdrop.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		viewhold.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		
		viewleft.setBounds(200, 100, 100, 40);
		viewright.setBounds(400, 100, 100, 40);
		viewrotate.setBounds(600, 100, 100, 40);
		viewdown.setBounds(200, 400, 100, 40);
		viewhold.setBounds(400, 400, 100, 40);
		viewdrop.setBounds(600, 400, 100, 40);
		
		left_button.setText(KeyEvent.getKeyText(main.key_setting[0]));
		right_button.setText(KeyEvent.getKeyText(main.key_setting[1]));
		rotate_button.setText(KeyEvent.getKeyText(main.key_setting[2]));
		down_button.setText(KeyEvent.getKeyText(main.key_setting[3]));
		hold_button.setText(KeyEvent.getKeyText(main.key_setting[4]));
		drop_button.setText(KeyEvent.getKeyText(main.key_setting[5]));
		
		left_button.setBounds(200, 150, 100, 50);
		right_button.setBounds(400, 150, 100, 50);
		rotate_button.setBounds(600, 150, 100, 50);
		down_button.setBounds(200, 450, 100, 50);
		hold_button.setBounds(400, 450, 100, 50);
		drop_button.setBounds(600, 450, 100, 50);
		
		add(viewleft);
		add(viewright);
		add(viewrotate);
		add(viewdown);
		add(viewdrop);
		add(viewhold);
		add(left_button);
		add(right_button);
		add(rotate_button);
		add(down_button);
		add(hold_button);
		add(drop_button);
		add(setok);
		add(menuBar);
		add(exitButton);
	
		setok.addActionListener(new ActionListener() {
		@Override
			public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dispose();
			}
		});
		
		
		left_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(0, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
		
		right_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(1, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
		
		rotate_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(2, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
		
		down_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(3, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
		
		hold_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(4, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
		
		drop_button.addActionListener(new ActionListener() {
			@Override
				public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				KeyPanel temp = new KeyPanel(5, TSetting.this, TSetting.this.ok);
				temp.setFocusable(true);
				}
			});
	}
	
	public void update() {
		left_button.setText(KeyEvent.getKeyText(ok.key_setting[0]));
		right_button.setText(KeyEvent.getKeyText(ok.key_setting[1]));
		rotate_button.setText(KeyEvent.getKeyText(ok.key_setting[2]));
		down_button.setText(KeyEvent.getKeyText(ok.key_setting[3]));
		hold_button.setText(KeyEvent.getKeyText(ok.key_setting[4]));
		drop_button.setText(KeyEvent.getKeyText(ok.key_setting[5]));
		this.repaint();
	}
}

class KeyPanel extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	TSetting wow;
	TMain main;
	int value;
	public KeyPanel(int key, TSetting lol, TMain ok) {
		wow = lol;
		main = ok;
		value = key;
		setTitle("Press");
		setSize(200,100);
		setLayout(null);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setFocusable(true);
		addKeyListener(this);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		main.key_setting[value] = arg0.getKeyCode();
		wow.update();
		dispose();
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
}
