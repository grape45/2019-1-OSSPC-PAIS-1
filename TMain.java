package com.ok.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ok.shape.Line;
import com.ok.window.Tetris;

public class TMain extends JFrame {

	private Tetris uc;
	
	// 더블 버퍼링을 위해 화면에 이미지를 담기 위한 Instance들이다.
	private Image screenImage;
	private Graphics screenGraphic;

	// 백그라운드 이미지 객체 생성
	private Image background = new ImageIcon(Main.class.getResource("../images/IntroBackground.jpg")).getImage();

	// 메뉴바 객체 생성
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));

	// 메뉴 바 위의 exit button 관련 객체 생성
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);

	// SINGLE button 관련 객체 생성
	private Image singleImage = new ImageIcon(Main.class.getResource("../images/SingleBasic.png")).getImage();
	private ImageIcon singleBasicImage = new ImageIcon(Main.class.getResource("../images/SingleBasic.png"));
	private ImageIcon singleEnteredImage = new ImageIcon(Main.class.getResource("../images/SingleEntered.png"));
	private JButton singleBtn = new JButton(singleBasicImage);

	// Normal button 관련 객체 생성
	private Image normalImage = new ImageIcon(Main.class.getResource("../images/NormalBasic.png")).getImage();
	private ImageIcon normalBasicImage = new ImageIcon(Main.class.getResource("../images/NormalBasic.png"));
	private ImageIcon normalEnteredImage = new ImageIcon(Main.class.getResource("../images/NormalEntered.png"));
	private JButton normalBtn = new JButton(normalBasicImage);

	// Hard button 관련 객체 생성
	private Image hardImage = new ImageIcon(Main.class.getResource("../images/HardBasic.png")).getImage();
	private ImageIcon hardBasicImage = new ImageIcon(Main.class.getResource("../images/HardBasic.png"));
	private ImageIcon hardEnteredImage = new ImageIcon(Main.class.getResource("../images/HardEntered.png"));
	private JButton hardBtn = new JButton(hardBasicImage);

	// Back button 관련 객체 생성
	private Image backImage = new ImageIcon(Main.class.getResource("../images/BackBasic.png")).getImage();
	private ImageIcon backBasicImage = new ImageIcon(Main.class.getResource("../images/BackBasic.png"));
	private ImageIcon backEnteredImage = new ImageIcon(Main.class.getResource("../images/BackEntered.png"));
	private JButton backBtn = new JButton(backBasicImage);

	// Multi button 관련 객체 생성
	private Image multiImage = new ImageIcon(Main.class.getResource("../images/MultiBasic.png")).getImage();
	private ImageIcon multiBasicImage = new ImageIcon(Main.class.getResource("../images/MultiBasic.png"));
	private ImageIcon multiEnteredImage = new ImageIcon(Main.class.getResource("../images/MultiEntered.png"));
	private JButton multiBtn = new JButton(multiBasicImage);

	// Setting button 관련 객체 생성
	private Image settingImage = new ImageIcon(Main.class.getResource("../images/SettingBasic.png")).getImage();
	private ImageIcon settingBasicImage = new ImageIcon(Main.class.getResource("../images/SettingBasic.png"));
	private ImageIcon settingEnteredImage = new ImageIcon(Main.class.getResource("../images/SettingEntered.png"));
	private JButton settingBtn = new JButton(settingBasicImage);

	// Exit button 관련 객체 생성
	private ImageIcon exitBasicImage = new ImageIcon(Main.class.getResource("../images/ExitBasic.png"));
	private ImageIcon exitEnteredImage = new ImageIcon(Main.class.getResource("../images/ExitEntered.png"));
	private JButton exitBtn = new JButton(exitBasicImage);

	private boolean isStartScreen = true;
	
	private boolean isSingleModeScreen = false;
	private boolean isNormalModeScrren = false;
	private boolean isHardModeScreen = false;
	private boolean isMultiModeScreen = false;
	private boolean isSettingModeScreen = false;
	
	// 마우스 이벤트에 활용하기 위한 마우스 x, y 좌표
	private int mouseX, mouseY;

	public TMain() {
		
		
		playSound(new File("D:\\bgm.wav"), 1.0f, false); //배경음악 재생
		
		setUndecorated(true); // 기본 메뉴바가 보이지 않음. -> 새로운 menuBar를 넣기 위한 작업
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setResizable(false); // 화면 크기 수정 불가능
		setLocationRelativeTo(null); // 화면 정중앙에 뜨게 함.
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setBackground(new Color(0, 0, 0, 0));
		setLayout(null); // 화면에 배치되는 버튼이나 label을 그 자리 그대로 들어가게 함.

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

		// Single 버튼 관련 처리
		singleBtn.setBounds(130, 200, 400, 100);
		singleBtn.setBorderPainted(false);
		singleBtn.setContentAreaFilled(false);
		singleBtn.setFocusPainted(false);
		// exit Button 이벤트 처리
		singleBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				singleBtn.setIcon(singleEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				singleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				singleBtn.setIcon(singleBasicImage);
				singleBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 싱글 모드 시작 이벤트처리 부분
				normalModeScreen();
			}
		});
		add(singleBtn);

		// Normal 버튼 관련 처리
		normalBtn.setBounds(130, 210, 400, 100);
		normalBtn.setBorderPainted(false);
		normalBtn.setContentAreaFilled(false);
		normalBtn.setFocusPainted(false);
		normalBtn.setVisible(false);
		// exit Button 이벤트 처리
		normalBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				normalBtn.setIcon(normalEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				normalBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				normalBtn.setIcon(normalBasicImage);
				normalBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 노말 모드 시작 이벤트
				normalModeScreen();
			}
		});
		add(normalBtn);

		// Hard 버튼 관련 처리
		hardBtn.setBounds(130, 340, 400, 100);
		hardBtn.setBorderPainted(false);
		hardBtn.setContentAreaFilled(false);
		hardBtn.setFocusPainted(false);
		hardBtn.setVisible(false);
		// hard Button 이벤트 처리
		hardBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hardBtn.setIcon(hardEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				hardBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hardBtn.setIcon(hardBasicImage);
				hardBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 하드 모드 시작 이벤트처리 부분
			}
		});
		add(hardBtn);

		// Back 버튼 관련 처리
		backBtn.setBounds(130, 470, 400, 100);
		backBtn.setBorderPainted(false);
		backBtn.setContentAreaFilled(false);
		backBtn.setFocusPainted(false);
		backBtn.setVisible(false);
		// exit Button 이벤트 처리
		backBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				backBtn.setIcon(backEnteredImage); // 마우스가 exit 버튼에 올라가면 이미지를 바꿔줌.
				backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				backBtn.setIcon(backBasicImage);
				backBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 뒤로 가기 이벤트 처리부분
			}
		});
		add(backBtn);

		// Multi 버튼 관련 처리
		multiBtn.setBounds(130, 320, 400, 100);
		multiBtn.setBorderPainted(false);
		multiBtn.setContentAreaFilled(false);
		multiBtn.setFocusPainted(false);
		// Multi Button 이벤트 처리
		multiBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				multiBtn.setIcon(multiEnteredImage); // 마우스가 Multi 버튼에 올라가면 이미지를 바꿔줌.
				multiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				multiBtn.setIcon(multiBasicImage);
				multiBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 멀티 모드 시작 이벤트처리 부분
			}
		});
		add(multiBtn);

		// Setting 버튼 관련 처리
		settingBtn.setBounds(130, 440, 400, 100);
		settingBtn.setBorderPainted(false);
		settingBtn.setContentAreaFilled(false);
		settingBtn.setFocusPainted(false);
		// Setting Button 이벤트 처리
		settingBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				settingBtn.setIcon(settingEnteredImage); // 마우스가 Setting 버튼에 올라가면 이미지를 바꿔줌.
				settingBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스가 올라가면 손가락 모양으로바꿈
			}

			@Override
			public void mouseExited(MouseEvent e) {
				settingBtn.setIcon(settingBasicImage);
				settingBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스를 떼면 다시 디폴트 모양으로 바꿈
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Setting창 화면으로 넘어가기
			}
		});
		add(settingBtn);

		// exit 버튼 관련 처리
		exitBtn.setBounds(130, 560, 400, 100);
		exitBtn.setBorderPainted(false);
		exitBtn.setContentAreaFilled(false);
		exitBtn.setFocusPainted(false);
		// exit Button 이벤트 처리
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
				System.exit(0);
			}
		});
		add(exitBtn);
	}

	public void paint(Graphics g) {

		// 1280X720만큼의 이미지를 만든다음에 screenImage에 넣어줌.
		screenImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		screenGraphic = screenImage.getGraphics(); // screenImage를 이용해 그래픽을 얻어옴.
		screenDraw(screenGraphic); // 스크린에 그래픽을 그려줌.
		g.drawImage(screenImage, 0, 0, null);
	}

	// 백그라운드 이미지를 그려준다.
	public void screenDraw(Graphics g) {
		g.drawImage(background, 0, 0, null); // drawImage는 추가된 것이 아니라 단순히 화면에 이미지를 출력할 때 쓰는 함수이다.
		if(isNormalModeScrren == true)
		{
			g.clearRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		}
		paintComponents(g); // JLabel, 버튼 같이 Main Frame에 추가된 것들을 그려 주는 역할을 함.
		this.revalidate();
		this.repaint(); // paint 함수를 다시 불러오는 함수. 즉 매 순간순간마다 새로 그려주는 역할을 함.
	}

	public void singleModeScreen() {
		singleBtn.setVisible(false);
		multiBtn.setVisible(false);
		settingBtn.setVisible(false);
		exitBtn.setVisible(false);
		isStartScreen = false;
		isSingleModeScreen = true;
		if (isSingleModeScreen == true) {
			normalBtn.setVisible(true);
			hardBtn.setVisible(true);
			backBtn.setVisible(true);
		}
	}
	
	public void normalModeScreen() {
		isSingleModeScreen = false;
		isNormalModeScrren = true;
		if(isNormalModeScrren == true) {
			normalBtn.setVisible(false);
			hardBtn.setVisible(false);
			backBtn.setVisible(false);
			dispose();
			uc = new Tetris();
		}
	}
	
    Clip bgmclip;
    public void playBgm(File file, float vol, boolean repeat){
            try{
                    //BGM은 임의의 시점에서 정지시킬 수 있어야 하므로 전역으로 전용 Clip을 사용
                    bgmclip = (Clip)AudioSystem.getLine(new Info(Clip.class));
                    bgmclip.open(AudioSystem.getAudioInputStream(file));
                    bgmclip.addLineListener(new LineListener() {
                            @Override
                            public void update(LineEvent event) {
                                    // TODO Auto-generated method stub
                                    System.out.println("" + event.getType());
                                    if(event.getType()==LineEvent.Type.STOP){
                                            bgmclip.close();
                                    }
                            }
                    });
                    FloatControl volume = (FloatControl)bgmclip.getControl(FloatControl.Type.MASTER_GAIN);
                    volume.setValue(vol);
                    bgmclip.start();
                    if(repeat)
                            bgmclip.loop(bgmclip.LOOP_CONTINUOUSLY);
            }catch(Exception e){
                    e.printStackTrace();
            }
    }
    public void stopBgm(){
           
            if(bgmclip!=null && bgmclip.isRunning()){
                    bgmclip.stop();
                    bgmclip.close();
            }
    }
    public void playSound(File file, float vol, boolean repeat){
            try{
                    final Clip clip = (Clip)AudioSystem.getLine(new Info(Clip.class));
                    clip.open(AudioSystem.getAudioInputStream(file));
                    clip.addLineListener(new LineListener() {
                            @Override
                            public void update(LineEvent event) {
                                    // TODO Auto-generated method stub
                                    if(event.getType()==LineEvent.Type.STOP){
                                            // BGM이 메모리에 쌓여서 crash 되는것을 방지
                                            clip.close();
                                    }
                            }
                    });
                    FloatControl volume = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volume.setValue(vol);
                    clip.start();
                    if(repeat)
                            clip.loop(Clip.LOOP_CONTINUOUSLY);
            }catch(Exception e){
                    e.printStackTrace();
            }
    }
}
