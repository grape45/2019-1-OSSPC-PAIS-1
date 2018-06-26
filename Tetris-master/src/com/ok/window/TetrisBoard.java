package com.ok.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ok.classes.Block;
import com.ok.classes.TetrisBlock;
import com.ok.controller.TetrisController;
import com.ok.main.Main;
import com.ok.main.TMain;
import com.ok.network.GameClient;
import com.ok.shape.CenterUp;
import com.ok.shape.LeftTwoUp;
import com.ok.shape.LeftUp;
import com.ok.shape.Line;
import com.ok.shape.Nemo;
import com.ok.shape.RightTwoUp;
import com.ok.shape.RightUp;

public class TetrisBoard extends JPanel implements Runnable, KeyListener, MouseListener, ActionListener {

	// Start button 관련 객체 생성
	private Image startImage = new ImageIcon(Main.class.getResource("../images/StartBasic.png")).getImage();
	private ImageIcon startBasicImage = new ImageIcon(Main.class.getResource("../images/StartBasic.png"));
	private ImageIcon startEnteredImage = new ImageIcon(Main.class.getResource("../images/StartEntered.png"));
	private JButton startBtn = new JButton(startBasicImage);

	// Exit button 관련 객체 생성
	private Image exitImage = new ImageIcon(Main.class.getResource("../images/SmallExitBasic.png")).getImage();
	private ImageIcon exitBasicImage = new ImageIcon(Main.class.getResource("../images/SmallExitBasic.png"));
	private ImageIcon exitEnteredImage = new ImageIcon(Main.class.getResource("../images/SmallExitEntered.png"));
	private JButton exitBtn = new JButton(exitBasicImage);

	private static final long serialVersionUID = 1L;

	private Tetris tetris;
	private GameClient client;
	public EnemyBoard enemy;

	public static final int BLOCK_SIZE = 20;
	public static final int BOARD_X = 120;
	public static final int BOARD_Y = 150;
	private int minX = 1, minY = 0, maxX = 10, maxY = 21, down = 50, up = 0;
	private final int MESSAGE_X = 2;
	private final int MESSAGE_WIDTH = BLOCK_SIZE * (7 + minX);
	private final int MESSAGE_HEIGHT = BLOCK_SIZE * (6 + minY);
	private final int PANEL_WIDTH = maxX * BLOCK_SIZE + MESSAGE_WIDTH + BOARD_X;
	private final int PANEL_HEIGHT = maxY * BLOCK_SIZE + MESSAGE_HEIGHT + BOARD_Y;

	private SystemMessageArea systemMsg;
	private MessageArea messageArea;
	private JButton btnStart = new JButton("게임 시작");
	private JButton btnExit = new JButton("게임 종료");
	private JCheckBox checkGhost = new JCheckBox("고스트", true);
	private JCheckBox checkGrid = new JCheckBox("그리드", true);
	private Integer[] lv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	private JComboBox<Integer> comboSpeed = new JComboBox<Integer>(lv);

	private String ip;
	private int port;
	private String nickName;
	private Thread th;
	private ArrayList<Block> blockList;
	private ArrayList<TetrisBlock> nextBlocks;
	private TetrisBlock shap;
	private TetrisBlock ghost;
	private TetrisBlock hold;
	private Block[][] map;
	private TetrisController controller;
	private TetrisController controllerGhost;

	public boolean isSingle = false;
	private boolean isPlay = false;
	private boolean isHold = false;
	private boolean usingGhost = true;
	private boolean usingGrid = true;
	private boolean usingBlind = false;
	private int removeLineCount = 0;
	private int removeLineCombo = 0;
	private long start_blind_time = 0;
	private long end_blind_time = 0;

	public int key_set[] = new int[11];

	public TetrisBoard(Tetris tetris, GameClient _client, boolean isSingle, int[] key_setting) {
		this.tetris = tetris;
		this.client = _client;
		this.isSingle = isSingle;
		if (isSingle == false) // 클라이언트 모드
		{
			systemMsg = new SystemMessageArea(21, 330, 100, 240);
			messageArea = new MessageArea(this, 500, 460, 360, 130);
			this.add(messageArea);
			this.add(systemMsg);

			messageArea.setVisible(true);
		} else {
			systemMsg = null;
			messageArea = null;
			this.add(comboSpeed);
		}
		this.enemy = new EnemyBoard();
		this.setBounds(0, 30, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		this.setBackground(Color.BLACK);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setLayout(null);
		this.setFocusable(true);
		this.key_set = key_setting;
		

		// Start 버튼 관련 처리
		startBtn.setBounds(475, 600, 220, 100);
		startBtn.setBorderPainted(false);
		startBtn.setContentAreaFilled(false);
		startBtn.setFocusPainted(false);
		startBtn.setFocusable(false);
		// Start Button 이벤트 처리
		startBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				startBtn.setIcon(startEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				startBtn.setIcon(startBasicImage);
				startBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 게임 시작 이벤트처리 부분
				if (client != null) {
					client.gameStart((int) comboSpeed.getSelectedItem());
				} else if (isSingle == true) {
					gameStart((int) comboSpeed.getSelectedItem());
				}
			}
		});
		add(startBtn);

		// Start 버튼 관련 처리
		exitBtn.setBounds(700, 600, 220, 100);
		exitBtn.setBorderPainted(false);
		exitBtn.setContentAreaFilled(false);
		exitBtn.setFocusPainted(false);
		exitBtn.setFocusable(false);
		// Start Button 이벤트 처리
		exitBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitBtn.setIcon(exitEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitBtn.setIcon(exitBasicImage);
				exitBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 게임 종료 이벤트
				if (client != null) {
					if (tetris.isNetwork()) {
						client.closeNetwork(tetris.isServer());
					}
				} else {
					new TMain(key_set);
					tetris.dispose();
				}
			}
		});
		add(exitBtn);

		checkGhost.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 70, 85, 20);
		checkGhost.setBackground(Color.BLACK);
		checkGhost.setForeground(Color.WHITE);
		checkGhost.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		checkGhost.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGhost = checkGhost.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		checkGrid.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 50, 85, 20);
		checkGrid.setBackground(Color.BLACK);
		checkGrid.setForeground(Color.WHITE);
		checkGrid.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		checkGrid.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGrid = checkGrid.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 8, 50, 45, 20);
		enemy.setBounds(1000, 150, 202, 422);
		enemy.setVisible(true);

		this.add(btnStart);
		this.add(btnExit);
		this.add(checkGhost);
		this.add(checkGrid);
		this.add(enemy);
	}

	public void startNetworking(String ip, int port, String nickName) {
		this.ip = ip;
		this.port = port;
		this.nickName = nickName;
		this.repaint();
		enemy.repaint();
	}

	/**
	 * TODO : 寃뚯엫�떆�옉 寃뚯엫�쓣 �떆�옉�븳�떎.
	 */

	public void gameStart(int speed) {
		comboSpeed.setSelectedItem(new Integer(speed));
		if (th != null) {
			try {
				isPlay = false;
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		map = new Block[maxY][maxX];
		blockList = new ArrayList<Block>();
		nextBlocks = new ArrayList<TetrisBlock>();

		shap = getRandomTetrisBlock();
		ghost = getBlockClone(shap, true);
		hold = null;
		isHold = false;
		controller = new TetrisController(shap, maxX - 1, maxY - 1, map);
		controllerGhost = new TetrisController(ghost, maxX - 1, maxY - 1, map);
		this.showGhost();
		for (int i = 0; i < 5; i++) {
			nextBlocks.add(getRandomTetrisBlock());
		}

		isPlay = true;
		th = new Thread(this);
		th.start();
	}

	// TODO : paint
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight() + 1);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1280, 720);

		// g.fillRect(0, BOARD_Y, (maxX + minX + 13) * BLOCK_SIZE + 1 + 300, maxY *
		// BLOCK_SIZE + 1);

		Font font = g.getFont();
		g.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		g.setFont(font);
		if (isSingle == false) {
			g.setColor(Color.WHITE);
			g.drawString("ip : " + ip + "     port : " + port, 20, 65);

			g.drawString("닉네임 : " + nickName, 20, 85);
		} else {
			g.setFont(new Font("맑은 고딕", Font.BOLD, 13));
			g.setColor(Color.WHITE);
			g.drawString("속도", PANEL_WIDTH - BLOCK_SIZE * 10, 65);
		}

		g.setColor(Color.BLACK);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX, BOARD_Y, maxX * BLOCK_SIZE + 1, maxY * BLOCK_SIZE + 1);
		g.fillRect(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5, BLOCK_SIZE * 5);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5,
				BLOCK_SIZE * 5);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE * 7,
				BLOCK_SIZE * 5, BLOCK_SIZE * 12);

		g.setColor(Color.WHITE);
		g.setFont(new Font(font.getFontName(), font.getStyle(), 20));
		g.drawString("HOLD", BLOCK_SIZE + 20, BOARD_Y + 7);
		g.drawString("NEXT", BOARD_X + BLOCK_SIZE + (maxX + 1) * BLOCK_SIZE + 1 + 25, BOARD_Y + 7);
		g.drawString("ENEMY", 1070, BOARD_Y - 10);
		g.drawString("PLAYER", 200, BOARD_Y - 10);
		g.setFont(font);

		if (usingGrid) {
			g.setColor(Color.WHITE);
			// 게임 보드판 테두리
			g.drawRect(BOARD_X + BLOCK_SIZE * minX, BOARD_Y, BLOCK_SIZE * 10 + 3, BLOCK_SIZE * 21 + 3);
			// 홀드 테두리
			g.drawRect(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5, BLOCK_SIZE * 5);
			// 넥스트 테두리
			g.drawRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5,
					BLOCK_SIZE * 5);
			// 대기 테두리
			g.drawRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE * 8 - 10,
					BLOCK_SIZE * 5, BLOCK_SIZE * 14);
			// 고스트, 테두리 표시의 테두리
			g.drawRect(370, 45, 92, 50);
			g.setColor(Color.darkGray);
			for (int i = 1; i < maxY; i++) // 게임 보드판 가로 그리기
				g.drawLine(BOARD_X + BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE * (i + minY),
						BOARD_X + (maxX + minX) * BLOCK_SIZE, BOARD_Y + BLOCK_SIZE * (i + minY));
			for (int i = 1; i < maxX; i++) // 게임 보드판 세로 그리기
				g.drawLine(BOARD_X + BLOCK_SIZE * (i + minX), BOARD_Y + BLOCK_SIZE * minY,
						BOARD_X + BLOCK_SIZE * (i + minX), BOARD_Y + BLOCK_SIZE * (minY + maxY));
			for (int i = 1; i < 5; i++) // HOLD 가로 그리기
				g.drawLine(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE * (i + 1), BLOCK_SIZE * (minX + 5) - 1,
						BOARD_Y + BLOCK_SIZE * (i + 1));
			for (int i = 1; i < 5; i++) // HOLD 세로 그리기
				g.drawLine(BLOCK_SIZE * (minY + i + 1), BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * (minY + i + 1),
						BOARD_Y + BLOCK_SIZE * (minY + 6) - 1);

			for (int i = 1; i < 5; i++) // NEXT 가로 그리기
				g.drawLine(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE * (i + 1),
						BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + BLOCK_SIZE * 5,
						BOARD_Y + BLOCK_SIZE * (i + 1));
			for (int i = 1; i < 5; i++) // NEXT 세로 그리기
				g.drawLine(BOARD_X + BLOCK_SIZE * minX + (maxX + 1 + i) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE,
						BOARD_X + BLOCK_SIZE * minX + BLOCK_SIZE + BLOCK_SIZE * (10 + i) + 1,
						BOARD_Y + BLOCK_SIZE * 6 - 1);
		}

		int x = 0, y = 0, newY = 0;
		if (hold != null) {
			x = 0;
			y = 0;
			newY = 3;
			x = hold.getPosX();
			y = hold.getPosY();
			hold.setPosX(-4 + minX);
			hold.setPosY(newY + minY);
			hold.drawBlock(g);
			hold.setPosX(x);
			hold.setPosY(y);
		}

		if (nextBlocks != null) {
			x = 0;
			y = 0;
			newY = 3;
			for (int i = 0; i < nextBlocks.size(); i++) {
				TetrisBlock block = nextBlocks.get(i);
				x = block.getPosX();
				y = block.getPosY();
				block.setPosX(13 + minX);
				block.setPosY(newY + minY);
				if (newY == 3)
					newY = 6;
				block.drawBlock(g);
				block.setPosX(x);
				block.setPosY(y);
				newY += 3;
			}
		}

		if (blockList != null) {
			x = 0;
			y = 0;
			for (int i = 0; i < blockList.size(); i++) {
				Block block = blockList.get(i);
				x = block.getPosGridX();
				y = block.getPosGridY();
				block.setPosGridX(x + minX);
				block.setPosGridY(y + minY);
				block.drawColorBlock(g);
				block.setPosGridX(x);
				block.setPosGridY(y);
			}
		}

		if (ghost != null) {

			if (usingGhost) {
				x = 0;
				y = 0;
				x = ghost.getPosX();
				y = ghost.getPosY();
				ghost.setPosX(x + minX);
				ghost.setPosY(y + minY);
				ghost.drawBlock(g);
				ghost.setPosX(x);
				ghost.setPosY(y);
			}
		}

		if (shap != null) {
			x = 0;
			y = 0;
			x = shap.getPosX();
			y = shap.getPosY();
			shap.setPosX(x + minX);
			shap.setPosY(y + minY);
			shap.drawBlock(g);
			shap.setPosX(x);
			shap.setPosY(y);
		}

		if (usingBlind) {
			end_blind_time = System.currentTimeMillis();
			g.setColor(new Color(0, 0, 0));
			g.fillRect(minX * BLOCK_SIZE * 7, minY * BLOCK_SIZE + 50, maxX * BLOCK_SIZE + 1, maxY * BLOCK_SIZE + 1);
			if ((end_blind_time - start_blind_time) / 1000 > 2)
				usingBlind = false;
			System.out.println("Time Check" + (end_blind_time - start_blind_time) / 1000);
		}

		enemy.repaint();
	}

	@Override
	public void run() {
		int countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
		int countDown = 0;
		int countUp = up;

		while (isPlay) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (countDown != 0) {
				countDown--;
				if (countDown == 0) {

					if (controller != null && !controller.moveDown())
						this.fixingTetrisBlock();
				}
				this.repaint();
				enemy.repaint();
				continue;
			}

			countMove--;
			if (countMove == 0) {
				countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
				if (controller != null && !controller.moveDown())
					countDown = down;
				else
					this.showGhost();
			}

			if (countUp != 0) {
				countUp--;
				if (countUp == 0) {
					countUp = up;
					addBlockLine(1);
				}
			}

			this.repaint();
			enemy.repaint();
		} // while()
	}// run()

	/**
	 * 留�(蹂댁씠湲�, �끉由�)�쓣 �긽�븯濡� �씠�룞�븳�떎.
	 * 
	 * @param lineNumber
	 * @param num
	 *            -1 or 1
	 */
	public void dropBoard(int lineNumber, int num) {

		// 留듭쓣 �뼥�뼱�듃由곕떎.
		this.dropMap(lineNumber, num);

		// 醫뚰몴諛붽퓭二쇨린(1留뚰겮利앷�)
		this.changeTetrisBlockLine(lineNumber, num);

		// �떎�떆 泥댄겕�븯湲�
		this.checkMap();
		if (isSingle == false && isPlay)
		{
			testfun();
			GoBlind();
		}
		// 怨좎뒪�듃 �떎�떆 肉뚮━湲�
		this.showGhost();
	}

	/**
	 * lineNumber�쓽 �쐞履� �씪�씤�뱾�쓣 紐⑤몢 num移몄뵫 �궡由곕떎.
	 * 
	 * @param lineNumber
	 * @param num
	 *            移몄닔 -1,1
	 */
	private void dropMap(int lineNumber, int num) {
		if (num == 1) {
			// �븳以꾩뵫 �궡由ш린
			for (int i = lineNumber; i > 0; i--) {
				for (int j = 0; j < map[i].length; j++) {
					map[i][j] = map[i - 1][j];
				}
			}

			// 留� �쐵以꾩� null濡� 留뚮뱾湲�
			for (int j = 0; j < map[0].length; j++) {
				map[0][j] = null;
			}
		} else if (num == -1) {
			// �븳以꾩뵫 �삱由ш린
			for (int i = 1; i <= lineNumber; i++) {
				for (int j = 0; j < map[i].length; j++) {
					map[i - 1][j] = map[i][j];
				}
			}

			// removeLine�� null濡� 留뚮뱾湲�
			for (int j = 0; j < map[0].length; j++) {
				map[lineNumber][j] = null;
			}
		}
	}

	private void changeTetrisBlockLine(int lineNumber, int num) {
		int y = 0, posY = 0;
		for (int i = 0; i < blockList.size(); i++) {
			y = blockList.get(i).getY();
			posY = blockList.get(i).getPosGridY();
			if (y <= lineNumber)
				blockList.get(i).setPosGridY(posY + num);
		}
	}

	private void fixingTetrisBlock() {
		synchronized (this) {
			if (stop) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		boolean isCombo = false;
		removeLineCount = 0;

		for (Block block : shap.getBlock()) {
			blockList.add(block);
		}

		isCombo = checkMap();

		if (isCombo)
			removeLineCombo++;
		else
			removeLineCombo = 0;

		if (isSingle == false)
			this.getFixBlockCallBack(blockList, removeLineCombo, removeLineCount);

		this.nextTetrisBlock();

		isHold = false;
		if (isSingle == false && isPlay) {
			testfun();
		}
	}// fixingTetrisBlock()

	private boolean checkMap() {
		boolean isCombo = false;
		int count = 0;
		Block mainBlock;

		for (int i = 0; i < blockList.size(); i++) {
			mainBlock = blockList.get(i);

			if (mainBlock.getY() < 0 || mainBlock.getY() >= maxY)
				continue;

			if (mainBlock.getY() < maxY && mainBlock.getX() < maxX)
				map[mainBlock.getY()][mainBlock.getX()] = mainBlock;

			if (mainBlock.getY() == 1 && mainBlock.getX() > 2 && mainBlock.getX() < 7) {
				this.gameEndCallBack();
				break;
			}

			count = 0;
			for (int j = 0; j < maxX; j++) {
				if (map[mainBlock.getY()][j] != null)
					count++;

			}

			if (count == maxX) {
				removeLineCount++;
				this.removeBlockLine(mainBlock.getY());
				isCombo = true;
			}
		}
		return isCombo;
	}

	public void nextTetrisBlock() {
		shap = nextBlocks.get(0);
		this.initController();
		nextBlocks.remove(0);
		nextBlocks.add(getRandomTetrisBlock());
	}

	private void initController() {
		controller.setBlock(shap);
		ghost = getBlockClone(shap, true);
		controllerGhost.setBlock(ghost);
	}

	private void removeBlockLine(int lineNumber) {
		for (int j = 0; j < maxX; j++) {
			for (int s = 0; s < blockList.size(); s++) {
				Block b = blockList.get(s);
				if (b == map[lineNumber][j])
					blockList.remove(s);
			}
			map[lineNumber][j] = null;
		} // for(j)

		this.dropBoard(lineNumber, 1);
	}

	public void gameEndCallBack() {
		if (isSingle == false)
			client.gameover();
		else {
			// systemMsg.printMessage("Game Over!");
		}
		this.isPlay = false;
	}

	public void testfun() {
		// System.out.println("맵 보내기 실험중");
		Block[] temp = new Block[maxX * maxY];
		for (int a = 0; a < 21; a++) {
			for (int b = 0; b < 10; b++) {
				if (map[a][b] != null) {
					temp[a * 10 + b] = map[a][b];
				}
			}
		}

		client.SendMap(temp, tetris.isserver);
	}

	public void GoBlind() {
		Random gen = new Random();
		if (gen.nextInt(10) < 1)
			client.Blind();
	}

	public void BlindAction() {
		start_blind_time = System.currentTimeMillis();
		usingBlind = true;
	}

	private void showGhost() {
		ghost = getBlockClone(shap, true);
		controllerGhost.setBlock(ghost);
		controllerGhost.moveQuickDown(shap.getPosY(), true);
	}

	public TetrisBlock getRandomTetrisBlock() {
		switch ((int) (Math.random() * 7)) {
		case TetrisBlock.TYPE_CENTERUP:
			return new CenterUp(4, 1);
		case TetrisBlock.TYPE_LEFTTWOUP:
			return new LeftTwoUp(4, 1);
		case TetrisBlock.TYPE_LEFTUP:
			return new LeftUp(4, 1);
		case TetrisBlock.TYPE_RIGHTTWOUP:
			return new RightTwoUp(4, 1);
		case TetrisBlock.TYPE_RIGHTUP:
			return new RightUp(4, 1);
		case TetrisBlock.TYPE_LINE:
			return new Line(4, 1);
		case TetrisBlock.TYPE_NEMO:
			return new Nemo(4, 1);
		}
		return null;
	}

	public TetrisBlock getBlockClone(TetrisBlock tetrisBlock, boolean isGhost) {
		TetrisBlock blocks = null;
		switch (tetrisBlock.getType()) {
		case TetrisBlock.TYPE_CENTERUP:
			blocks = new CenterUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTTWOUP:
			blocks = new LeftTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTUP:
			blocks = new LeftUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTTWOUP:
			blocks = new RightTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTUP:
			blocks = new RightUp(4, 1);
			break;
		case TetrisBlock.TYPE_LINE:
			blocks = new Line(4, 1);
			break;
		case TetrisBlock.TYPE_NEMO:
			blocks = new Nemo(4, 1);
			break;
		}
		if (blocks != null && isGhost) {
			blocks.setGhostView(isGhost);
			blocks.setPosX(tetrisBlock.getPosX());
			blocks.setPosY(tetrisBlock.getPosY());
			blocks.rotation(tetrisBlock.getRotationIndex());
		}
		return blocks;
	}

	public void getFixBlockCallBack(ArrayList<Block> blockList, int removeCombo, int removeMaxLine) {
		if (removeCombo < 3) {
			if (removeMaxLine == 3)
				client.addBlock(1);
			else if (removeMaxLine == 4)
				client.addBlock(3);
		} else if (removeCombo < 10) {
			if (removeMaxLine == 3)
				client.addBlock(2);
			else if (removeMaxLine == 4)
				client.addBlock(4);
			else
				client.addBlock(1);
		} else {
			if (removeMaxLine == 3)
				client.addBlock(3);
			else if (removeMaxLine == 4)
				client.addBlock(5);
			else
				client.addBlock(2);
		}
	}

	public void playBlockHold() {
		if (isHold)
			return;

		if (hold == null) {
			hold = getBlockClone(shap, false);
			this.nextTetrisBlock();
		} else {
			TetrisBlock tmp = getBlockClone(shap, false);
			shap = getBlockClone(hold, false);
			hold = getBlockClone(tmp, false);
			this.initController();
		}

		isHold = true;
	}

	boolean stop = false;

	public void addBlockLine(int numOfLine) {
		stop = true;
		Block block;
		int rand = (int) (Math.random() * maxX);
		for (int i = 0; i < numOfLine; i++) {
			this.dropBoard(maxY - 1, -1);
			for (int col = 0; col < maxX; col++) {
				if (col != rand) {
					block = new Block(0, 0, Color.GRAY, Color.GRAY);
					block.setPosGridXY(col, maxY - 1);
					blockList.add(block);
					map[maxY - 1][col] = block;

					if (isSingle == false && isPlay)
						testfun();
				}
			}
			boolean up = false;
			for (int j = 0; j < shap.getBlock().length; j++) {
				Block sBlock = shap.getBlock(j);
				if (map[sBlock.getY()][sBlock.getX()] != null) {
					up = true;
					break;
				}
			}
			if (up) {
				controller.moveDown(-1);
			}
		}

		this.showGhost();
		this.repaint();
		enemy.repaint();
		synchronized (this) {
			stop = false;
			this.notify();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			messageArea.requestFocus();
		}
		if (!isPlay)
			return;
		if (e.getKeyCode() == key_set[0]) {
			controller.moveLeft();
			controllerGhost.moveLeft();
		} else if (e.getKeyCode() == key_set[1]) {
			controller.moveRight();
			controllerGhost.moveRight();
		} else if (e.getKeyCode() == key_set[4]) {
			controller.moveDown();
		} else if (e.getKeyCode() == key_set[2]) {
			controller.nextRotationLeft();
			controllerGhost.nextRotationLeft();
		} else if (e.getKeyCode() == key_set[3]) {
			controller.nextRotationRight();
			controllerGhost.nextRotationRight();
		} else if (e.getKeyCode() == key_set[5]) {
			controller.moveQuickDown(shap.getPosY(), true);
			this.fixingTetrisBlock();
		} else if (e.getKeyCode() == key_set[6]) {
			playBlockHold();
		}
		this.showGhost();
		this.repaint();
		enemy.repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		this.requestFocus();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			if (client != null) {
				client.gameStart((int) comboSpeed.getSelectedItem());
				setFocusable(true);
			} else {
				this.gameStart((int) comboSpeed.getSelectedItem());
				setFocusable(true);
			}
		} else if (e.getSource() == btnExit) {
			if (client != null) {
				if (tetris.isNetwork()) {
					client.closeNetwork(tetris.isServer());
				}
			} else {
				new TMain(key_set);
				tetris.dispose();
			}
		}
	}

	public boolean isPlay() {
		return isPlay;
	}

	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}

	public JButton getBtnStart() {
		return btnStart;
	}

	public JButton getBtnExit() {
		return btnExit;
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public void printSystemMessage(String msg) {
		systemMsg.printMessage(msg);
	}

	public void printMessage(String msg) {
		messageArea.printMessage(msg);
	}

	public GameClient getClient() {
		return client;
	}

	public ArrayList<Block> getMap() {
		return blockList;
	}

	public void changeSpeed(Integer speed) {
		comboSpeed.setSelectedItem(speed);
	}

	public void clearMessage() {
		if (isSingle == false) {
			messageArea.clearMessage();
			systemMsg.clearMessage();
		}
	}

}
