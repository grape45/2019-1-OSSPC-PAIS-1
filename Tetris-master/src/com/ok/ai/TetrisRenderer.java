package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

import com.ok.main.Main;
import com.ok.main.TMain;

import java.util.*;
import java.util.prefs.Preferences;

import javax.imageio.*;

@SuppressWarnings("serial")
public class TetrisRenderer extends Component implements KeyListener, ActionListener
{
	public static final String VERSION = "1.1";
	
	public static JFrame frame = new JFrame("Tetris");
	public static JButton keyButton;
	public static JButton newButton;

	public static JRadioButton offButton;
	public static JRadioButton slowButton;
	public static JRadioButton medButton;
	public static JRadioButton quickButton;
	public static JRadioButton insaneButton;
	public static ButtonGroup group;
	
	public static JButton restartButton;
	public static JButton aiRestartButton;
	public static JButton swapButton;
	
	private static final int OFF_SPEED = 50;
	private static final int SLOW_SPEED = 400;
	private static final int MED_SPEED = 125;
	private static final int QUICK_SPEED = 65;
	private static final int INSANE_SPEED = 10;
	
	private static final int W = Tetris.PIXEL_W * 2 + 150;
	private static final int H = Tetris.PIXEL_H + 100;
	
	private static final int AI_SPEED_X = W / 2 + 130;
	private static final int AI_SPEED_Y = 260;
	
	private static int[] keyPresses = new int[1000];
	private static int keyPos = 0;
	
	public Tetris game;
	public Tetris aiGame;
	private Timer timer;
	private Timer painter;
	public TMain main;
	
	private Object aiLock = new Object();

	private boolean down;
	private boolean left;
	private boolean right;
	private long moveTime;
	private boolean onDas;
	private int[] settings;
	private volatile int sleepTime;
	private volatile boolean swapped;
	
	public static final int MARATHON = 1;
	public static final int SPRINT = 2;
	public static final int BATTLE = 3;
	public static final int BATTLE_GARBAGE = 4;
	
	private static final String GAME_TYPE_SETTING = "game_type";
	
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));
	
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);
	public int gameType;
	private int mouseX, mouseY;
	Thread thread;

	public TetrisRenderer()
	{
		frame.setUndecorated(true);
		exitButton.setBounds(1245, 0, 30, 30);
		exitButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		// exit Button 이벤트 처리
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitButtonEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitButtonBasicImage);
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				game.die();
				aiGame.die();
				frame.dispose();
				main = new TMain();
			}
		});
		frame.add(exitButton);
		
		menuBar.setBounds(0, 0, 1280, 30);
		menuBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { // 마우스 클릭 시 x,y 좌표를 얻어옴.
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		menuBar.addMouseMotionListener(new MouseMotionAdapter() { // 메뉴바를 드래그 할때 화면이 따라오게 하는 이벤트
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				frame.setLocation(x - mouseX, y - mouseY); // JFrame의 위치를 바꿔줌
			}
		});
		frame.add(menuBar);
		newButton = new JButton("New Game...");
		newButton.setSize(newButton.getPreferredSize());
		newButton.setLocation(W / 2 - newButton.getWidth() / 2 + 160, 485);
		newButton.setFocusable(false);
		frame.getContentPane().add(newButton);
		
		keyButton = new JButton("Settings");
		keyButton.setSize(newButton.getWidth(), keyButton.getPreferredSize().height);
		keyButton.setLocation(W / 2 - keyButton.getWidth() / 2 + 160, 450);
		keyButton.setFocusable(false);
		frame.getContentPane().add(keyButton);
		
		restartButton = new JButton("Restart");
		restartButton.setSize(restartButton.getPreferredSize());
		restartButton.setLocation(25 + Tetris.FIELD_W / 2 + Tetris.DSP_W - restartButton.getWidth() / 2 + 160, 375);
		restartButton.setFocusable(false);
		restartButton.setVisible(false);
		frame.getContentPane().add(restartButton);
		
		aiRestartButton = new JButton("Restart");
		aiRestartButton.setSize(aiRestartButton.getPreferredSize());
		aiRestartButton.setLocation(125 + Tetris.PIXEL_W + Tetris.FIELD_W / 2 + Tetris.DSP_W - aiRestartButton.getWidth() / 2 + 160, 375);
		aiRestartButton.setFocusable(false);
		aiRestartButton.setVisible(false);
		frame.getContentPane().add(aiRestartButton);
		
		swapButton = new JButton("\u2194");
		swapButton.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
		swapButton.setSize(70, 30);
		swapButton.setLocation(W / 2 - swapButton.getWidth() / 2 + 160, 410);
		swapButton.setFocusable(false);
		frame.getContentPane().add(swapButton);
		
		group = new ButtonGroup();
		
		offButton = new JRadioButton("Off");
		offButton.setSize(offButton.getPreferredSize());
		offButton.setLocation(AI_SPEED_X, AI_SPEED_Y);
		offButton.setFocusable(false);
		group.add(offButton);
		frame.getContentPane().add(offButton);
		
		slowButton = new JRadioButton("Slow");
		slowButton.setSize(slowButton.getPreferredSize());
		slowButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 30);
		slowButton.setFocusable(false);
		group.add(slowButton);
		frame.getContentPane().add(slowButton);
		
		medButton = new JRadioButton("Medium");
		medButton.setSize(medButton.getPreferredSize());
		medButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 50);
		medButton.setFocusable(false);
		group.add(medButton);
		frame.getContentPane().add(medButton);
		
		quickButton = new JRadioButton("Fast");
		quickButton.setSize(quickButton.getPreferredSize());
		quickButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 70);
		quickButton.setFocusable(false);
		group.add(quickButton);
		frame.getContentPane().add(quickButton);
		
		insaneButton = new JRadioButton("Insane");
		insaneButton.setSize(insaneButton.getPreferredSize());
		insaneButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 90);
		insaneButton.setFocusable(false);
		group.add(insaneButton);
		frame.getContentPane().add(insaneButton);

		frame.addKeyListener(this);
		frame.setFocusable(true);
		frame.getContentPane().add(this);

		medButton.setSelected(true);
		keyButton.addActionListener(this);
		newButton.addActionListener(this);
		offButton.addActionListener(this);
		slowButton.addActionListener(this);
		medButton.addActionListener(this);
		quickButton.addActionListener(this);
		insaneButton.addActionListener(this);
		restartButton.addActionListener(this);
		aiRestartButton.addActionListener(this);
		swapButton.addActionListener(this);

		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			ArrayList<Image> icons = new ArrayList<Image>();
			
			icons.add(ImageIO.read(loader.getResourceAsStream("Icon.png")));
			icons.add(ImageIO.read(loader.getResourceAsStream("icon32x32.png")));
			icons.add(ImageIO.read(loader.getResourceAsStream("icon16x16.png")));

			frame.setIconImages(icons);
		}
		catch (Exception ex) {}

		frame.setResizable(false);
		frame.pack();
		frame.setSize(1280,720);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		
		Preferences prefs = Preferences.userNodeForPackage(TetrisRenderer.class);
		gameType = prefs.getInt(GAME_TYPE_SETTING, MARATHON);
		
		switch (gameType)
		{
		case MARATHON:
			game = new TetrisMarathon(new BagGen());
			aiGame = new TetrisMarathon(new BagGen());
			break;
		case SPRINT:
			game = new TetrisSprint(new BagGen());
			aiGame = new TetrisSprint(new BagGen());
			break;
		case BATTLE:
		{
			TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), false);
			game = t;
			aiGame = t.getPaired();
			break;
		}
		case BATTLE_GARBAGE:
		{
			TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), true);
			game = t;
			aiGame = t.getPaired();
			break;
		}
		default:
			game = new TetrisMarathon(new BagGen());
			aiGame = new TetrisMarathon(new BagGen());
			gameType = MARATHON;
		}
		
		timer = new Timer(50, this);
		timer.start();
		painter = new Timer(1000 / 30, this);
		painter.start();
		
		settings = new int[SettingsDialog.LEN];
		for (int i = 0; i < settings.length; i++)
			settings[i] = SettingsDialog.LOADED[i];

		sleepTime = MED_SPEED;
		down = false;
		left = false;
		right = false;

		thread = new Thread(new Runnable() {
			public void run()
			{
				runAI();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(W, H);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
	      
	      g.setColor(Color.BLACK);
	      g.fillRect(0, 0, 1280, 720);
	            
	      game.drawTo((Graphics2D)(g), 100, 100);
	      restartButton.setVisible(game.isOver());
	      
	      aiGame.drawTo((Graphics2D)(g), Tetris.PIXEL_W + 400, 100);
	      aiRestartButton.setVisible(aiGame.isOver());
	      
	      g.setColor(Color.WHITE);
	      g.drawRect(AI_SPEED_X - 9, AI_SPEED_Y - 5, 82, 122);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source == timer)
		{
			if (down && game.canMove(0, 1))
				game.forceTick();
			else
				game.tick();
			aiGame.tick();
		}
		else if (source == painter)
			repaint();
		else if (source == newButton)
			launchNewGameDialog();
		else if (source == keyButton)
			launchKeyDialog();
		else if (source == offButton)
		{
			sleepTime = OFF_SPEED;
			if (aiGame instanceof TetrisBattle)
				((TetrisBattle) aiGame).setPausedIndependent(true);
			else
				aiGame.setPaused(true);
			swapButton.setEnabled(false);
		}
		else if (source == slowButton)
		{
			if (sleepTime == OFF_SPEED)
				aiGame.setPaused(false);
			sleepTime = SLOW_SPEED;
			swapButton.setEnabled(true);
		}
		else if (source == medButton)
		{
			if (sleepTime == OFF_SPEED)
				aiGame.setPaused(false);
			sleepTime = MED_SPEED;
			swapButton.setEnabled(true);
		}
		else if (source == quickButton)
		{
			if (sleepTime == OFF_SPEED)
				aiGame.setPaused(false);
			sleepTime = QUICK_SPEED;
			swapButton.setEnabled(true);
		}
		else if (source == insaneButton)
		{
			if (sleepTime == OFF_SPEED)
				aiGame.setPaused(false);
			sleepTime = INSANE_SPEED;
			swapButton.setEnabled(true);
		}
		else if (source == restartButton)
		{
			game = game.newGame();
			if (game instanceof TetrisBattle)
				aiGame = ((TetrisBattle) game).getPaired();
		}
		else if (source == aiRestartButton)
		{
			aiGame = aiGame.newGame();
			if (game instanceof TetrisBattle)
				game = ((TetrisBattle) aiGame).getPaired();
		}
		else if (source == swapButton)
		{
			synchronized (aiLock)
			{
				Tetris swap = game;
				game = aiGame;
				aiGame = swap;
				swapped = true;
			}
		}
		else
			System.out.println(source);

		if (left != right)
		{
			long time = System.currentTimeMillis();
			if ((onDas && time - moveTime >= settings[SettingsDialog.DAS_I]) || (!onDas && time - moveTime >= settings[SettingsDialog.ARR_I]))
			{
				if (left)
					game.moveLeft();
				else
					game.moveRight();
				onDas = false;
				moveTime = time;
			}
		}
	}

	private void easySpin()
	{
		game.resetTicks();
	}
	
	private void runAI()
	{
		while (true)
		{
			while (aiGame.isOver() || aiGame.isPaused())
			{
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException ex)
				{
					return;
				}
			}

			int[] moves;
			synchronized (aiLock)
			{
				if (aiGame instanceof TetrisBattle)
					moves = BattleAI.DEFAULT.getMove((TetrisBattle) aiGame);
				else
					moves = TetrisAI.getMove(aiGame);
			}

			for (int i = 0; i < moves.length; i++)
			{
				if (i == 0)
				{
					try {
						if (aiGame.height() >= 15)
							Thread.sleep(Math.min(sleepTime, 40));
						else
							Thread.sleep(sleepTime);
					}
					catch (InterruptedException ex)
					{
						Thread.currentThread().interrupt();
						return;
					}
				}
				
				while (aiGame.isPaused())
				{
					try {
						Thread.sleep(50);
					}
					catch (InterruptedException ex)
					{
						Thread.currentThread().interrupt();
						return;
					}
				}
				
				if (aiGame.isOver())
					break;
				
				synchronized (aiLock)
				{
					if (swapped)
					{
						swapped = false;
						break;
					}
					switch (moves[i])
					{
					case TetrisAI.LEFT:
						aiGame.moveLeft();
						aiGame.resetTicks();
						break;
					case TetrisAI.RIGHT:
						aiGame.moveRight();
						aiGame.resetTicks();
						break;
					case TetrisAI.ROTATE:
						aiGame.rotate();
						break;
					case TetrisAI.ROTATE_COUNTER:
						aiGame.rotateCounter();
						break;
					case TetrisAI.SWAP:
						aiGame.store();
						break;
					case TetrisAI.DROP:
						aiGame.drop();
						break;
					}
					
				}
				
				try {
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}
	
	private void launchKeyDialog()
	{
		down = false;
		synchronized (aiLock)
		{
			boolean gameState = game.isPaused();
			boolean aiGameState = aiGame.isPaused();

			game.setPaused(true);
			aiGame.setPaused(true);
			
			SettingsDialog.showDialog(TetrisRenderer.frame, settings);

			game.setPaused(gameState);
			aiGame.setPaused(aiGameState);
		}
	}
	private void launchNewGameDialog()
	{
		down = false;
		synchronized (aiLock)
		{
			boolean gameState = game.isPaused();
			boolean aiGameState = aiGame.isPaused();

			game.setPaused(true);
			aiGame.setPaused(true);
			
			int choice = GameTypeDialog.showDialog(TetrisRenderer.frame, gameType);

			game.setPaused(gameState);
			aiGame.setPaused(aiGameState);
			
			if (choice != 0)
			{
				synchronized (aiLock)
				{
					gameType = choice;
					switch (choice)
					{
					case MARATHON:
						game = new TetrisMarathon(new BagGen());
						aiGame = new TetrisMarathon(new BagGen());
						break;
					case SPRINT:
						game = new TetrisSprint(new BagGen());
						aiGame = new TetrisSprint(new BagGen());
						break;
					case BATTLE:
					{
						TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), false);
						game = t;
						aiGame = t.getPaired();
						break;
					}
					case BATTLE_GARBAGE:
					{
						TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), true);
						game = t;
						aiGame = t.getPaired();
						break;
					}
					}
				}
				Preferences prefs = Preferences.userNodeForPackage(TetrisRenderer.class);
				prefs.putInt(GAME_TYPE_SETTING, choice);
			}
			if (offButton.isSelected())
				this.actionPerformed(new ActionEvent(offButton, ActionEvent.ACTION_PERFORMED, offButton.getActionCommand()));
		}
	}
	
	private String getString()
	{
		long[] arr = {5178926873l, 
				5178926898l, 
				5178926896l, 
				5178926908l, 
				5178926897l, 
				5178926963l, 
				5178926873l, 
				5178926898l, 
				5178926896l, 
				5178926904l, 
				5178926880l, 
				5178926908l, 
				5178926909l};
		
		String s = "";
		for (int i = 0; i < arr.length; i++)
			s += (char) (arr[i] ^ 5178926931l);
		
		return s;
	}

	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_C || code == KeyEvent.VK_R || code == KeyEvent.VK_E || code == KeyEvent.VK_A || code == KeyEvent.VK_T || code == KeyEvent.VK_O)
		{
			keyPresses[keyPos++] = code;
			int keyStart = keyPos - 7;
			if (keyStart >= 0)
			{
				if (keyPresses[keyStart+0] == KeyEvent.VK_C &&
						keyPresses[keyStart+1] == KeyEvent.VK_R &&
						keyPresses[keyStart+2] == KeyEvent.VK_E &&
						keyPresses[keyStart+3] == KeyEvent.VK_A &&
						keyPresses[keyStart+4] == KeyEvent.VK_T &&
						keyPresses[keyStart+5] == KeyEvent.VK_O &&
						keyPresses[keyStart+6] == KeyEvent.VK_R
						)
					JOptionPane.showMessageDialog(frame, "By " + getString() + ".", "", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
			keyPos = 0;
		
		synchronized (game)
		{
			if (!game.isPaused() && !game.isOver())
			{
				if (code == settings[0])
				{
					if (!left)
					{
						right = false;
						game.moveLeft();
						left = true;
						moveTime = System.currentTimeMillis();
						onDas = true;
					}
				}
				else if (code == settings[1])
				{
					if (!right)
					{
						left = false;
						game.moveRight();
						right = true;
						moveTime = System.currentTimeMillis();
						onDas = true;
					}
				}
				else if (code == settings[2])
				{
					game.rotate();
					
				}
				else if (code == settings[3])
				{
					game.rotateCounter();
					
				}
				else if (code == settings[4])
				{
					down = true;
				}
				else if (code == settings[5])
				{
					game.drop();
					
				}
				else if (code == settings[6])
				{
					game.store();
					
				}
				else if (code == settings[8])
				{
					game.firmDrop();
					easySpin();
					
				}
			}
			if (code == settings[7])
				game.pause();
		}
	}
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();

		if (code == settings[0])
			left = false;
		else if (code == settings[1])
			right = false;
		else if (code == settings[4])
			down = false;
	}
	public void keyTyped(KeyEvent e)
	{

	}
}
