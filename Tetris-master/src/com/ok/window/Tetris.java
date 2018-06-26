package com.ok.window;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.ok.classes.Block;
import com.ok.main.Main;
import com.ok.main.TMain;
import com.ok.network.GameClient;
import com.ok.network.GameServer;

public class Tetris extends JFrame implements ActionListener {

	// 메뉴바 객체 생성
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));

	// 메뉴 바 위의 exit button 관련 객체 생성
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);

	// 마우스 이벤트에 활용하기 위한 마우스 x, y 좌표
	private int mouseX, mouseY;

	private static final long serialVersionUID = 1L;
	private GameServer server;
	private GameClient client;
	private TetrisBoard board;
	private JMenuItem itemServerStart = new JMenuItem("서버 만들기");
	private JMenuItem itemClientStart = new JMenuItem("클라이언트 접속하기");

	private boolean isNetwork;
	private boolean isServer;

	public int isserver = 0;
	public int mode = 0;

	public Tetris(int mode, int[] key_setting) {
		setUndecorated(true); // 기본 메뉴바가 보이지 않음. -> 새로운 menuBar를 넣기 위한 작업
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT + 30);
		setResizable(false); // 화면 크기 수정 불가능
		setLocationRelativeTo(null); // 화면 정중앙에 뜨게 함.
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setBackground(Color.BLACK);
		// setBackground(new Color(0, 0, 0, 0));
		setLayout(null); // 화면에 배치되는 버튼이나 label을 그 자리 그대로 들어가게 함.
		JMenuBar mnBar = new JMenuBar();
		JMenu mnGame = new JMenu("게임하기");
		this.mode = mode;
		if (mode == 1)
			board = new TetrisBoard(this, client, true, key_setting);
		else
			board = new TetrisBoard(this, client, false, key_setting);

		// Menu bar exit 버튼 관련 처리
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
				new TMain(key_setting);
				dispose();
			}
		});
		add(exitButton);

		// 메뉴바 이벤트
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
				setLocation(x - mouseX, y - mouseY); // JFrame의 위치를 바꿔줌
			}
		});
		add(menuBar);

		// mnGame.add(SingleStart);
		if (this.mode != 1) {
			mnGame.add(itemServerStart);
			mnGame.add(itemClientStart);
			mnBar.add(mnGame);

			this.setJMenuBar(mnBar);
		}
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().add(board);

		if (this.mode == 1) {
			board.setClient(client);
			board.getBtnStart().setEnabled(true);
			board.clearMessage();
			board.requestFocus();
		}

		// SingleStart.addActionListener(this);
		itemServerStart.addActionListener(this);
		itemClientStart.addActionListener(this);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (client != null) {
					System.out.println("123");
					if (isNetwork()) {
						client.closeNetwork(isServer);
					}
				} else {
					new TMain(key_setting);
					dispose();
				}

			}

		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String ip = null;
		int port = 0;
		String nickName = null;
		if (e.getSource() == itemServerStart) {

			String sp = JOptionPane.showInputDialog("포트 번호를 입력하세요", "9500");
			if (sp != null && !sp.equals(""))
				port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("닉네임을 입력하세요", "default");

			if (port != 0) {
				if (server == null)
					server = new GameServer(port);
				server.startServer();
				isserver = 1;
				try {
					ip = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				if (ip != null) {
					client = new GameClient(this, ip, port, nickName);
					if (client.start()) {
						itemServerStart.setEnabled(false);
						itemClientStart.setEnabled(false);
						board.setClient(client);
						board.getBtnStart().setEnabled(true);
						board.startNetworking(ip, port, nickName);
						isNetwork = true;
						isServer = true;
					}
				}
			}
		} else if (e.getSource() == itemClientStart) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}

			ip = JOptionPane.showInputDialog("IP 주소를 입력하세요.", ip);
			String sp = JOptionPane.showInputDialog("port 번호를 입력하세요", "9500");
			if (sp != null && !sp.equals(""))
				port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("닉네임을 입력하세요", "default");

			if (ip != null) {
				client = new GameClient(this, ip, port, nickName);
				isserver = 0;
				if (client.start()) {
					itemServerStart.setEnabled(false);
					itemClientStart.setEnabled(false);
					board.setClient(client);
					board.startNetworking(ip, port, nickName);
					isNetwork = true;
				}
			}
		}

	}

	public void closeNetwork() {
		isNetwork = false;
		client = null;
		itemServerStart.setEnabled(true);
		itemClientStart.setEnabled(true);
		board.setPlay(false);
		board.setClient(null);
	}

	public JMenuItem getItemServerStart() {
		return itemServerStart;
	}

	public JMenuItem getItemClientStart() {
		return itemClientStart;
	}

	public TetrisBoard getBoard() {
		return board;
	}

	public void gameStart(int speed) {
		board.gameStart(speed);
	}

	public boolean isNetwork() {
		return isNetwork;
	}

	public void setNetwork(boolean isNetwork) {
		this.isNetwork = isNetwork;
	}

	public void printSystemMessage(String msg) {
		board.printSystemMessage(msg);
	}

	public void printMessage(String msg) {
		board.printMessage(msg);
	}

	public void setmap(Block[] map) {
		board.enemy.map = map;
		board.enemy.fun();
	}

	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public void changeSpeed(Integer speed) {
		board.changeSpeed(speed);
	}
}
