package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Tetris
{
	public static final int W = 10;
	public static final int H = 20;

	protected static final int SQR_W = 20;
	public static final int DSP_W = 70;
	public static final int FIELD_W = W * SQR_W;
	public static final int FIELD_H = H * SQR_W;
	public static final int PIXEL_W = FIELD_W + DSP_W * 2;
	public static final int PIXEL_H = FIELD_H + 60;
	
	protected static final int TSPIN_ANIMATION_TICKS = 3;
	public static final BufferedImage[] tspins = new BufferedImage[4];
	protected static final int TPIECE = 3;
	
	static
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		InputStream in = null;
		try {
			in = loader.getResourceAsStream("tspin.png");
			BufferedImage base = ImageIO.read(in);
			
			tspins[0] = base;
			base = Utility.rotateRight(base);
			
			tspins[1] = base;
			base = Utility.rotateRight(base);
			
			tspins[2] = base;
			base = Utility.rotateRight(base);
			
			tspins[3] = base;
		}
		catch (Exception ex) {}
		finally
		{
			if (in != null)
			{
				try {
					in.close();
				}
				catch (IOException ex) {}
			}
		}
	}

	protected static final byte[][][][] PIECES =
								{null,       { { {0, 1, 0, 0},   // I-Tetrimino
												 {0, 1, 0, 0},
												 {0, 1, 0, 0},
												 {0, 1, 0, 0} },
											   { {0, 0, 0, 0},
												 {0, 0, 0, 0},
												 {1, 1, 1, 1},
												 {0, 0, 0, 0} },
											   { {0, 0, 1, 0},
												 {0, 0, 1, 0},
												 {0, 0, 1, 0},
												 {0, 0, 1, 0} },
											   { {0, 0, 0, 0},
												 {1, 1, 1, 1},
												 {0, 0, 0, 0},
												 {0, 0, 0, 0} } },

											 { { {0, 1, 0, 0},   // S-Tetrimino
												 {0, 1, 1, 0},
												 {0, 0, 1, 0},
												 {0, 0, 0, 0} },
										       { {0, 0, 0, 0},
											     {0, 0, 1, 1},
											     {0, 1, 1, 0},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 0},
											     {0, 0, 1, 1},
											     {0, 0, 0, 1},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 1},
											     {0, 1, 1, 0},
											     {0, 0, 0, 0},
											     {0, 0, 0, 0} } },

											 { { {0, 0, 1, 0},   // T-Tetrimino
												 {0, 1, 1, 0},
												 {0, 0, 1, 0},
												 {0, 0, 0, 0} },
										       { {0, 0, 0, 0},
											     {0, 1, 1, 1},
											     {0, 0, 1, 0},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 0},
											     {0, 0, 1, 1},
											     {0, 0, 1, 0},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 0},
											     {0, 1, 1, 1},
											     {0, 0, 0, 0},
											     {0, 0, 0, 0} } },

											 { { {0, 0, 0, 0},   // O-Tetrimino
												 {0, 1, 1, 0},
												 {0, 1, 1, 0},
												 {0, 0, 0, 0} } },

											 { { {0, 0, 1, 0},   // S-Tetrimino
												 {0, 1, 1, 0},
												 {0, 1, 0, 0},
												 {0, 0, 0, 0} },
										       { {0, 0, 0, 0},
											     {0, 1, 1, 0},
											     {0, 0, 1, 1},
											     {0, 0, 0, 0} },
										       { {0, 0, 0, 1},
											     {0, 0, 1, 1},
											     {0, 0, 1, 0},
											     {0, 0, 0, 0} },
										       { {0, 1, 1, 0},
											     {0, 0, 1, 1},
											     {0, 0, 0, 0},
											     {0, 0, 0, 0} } },

											 { { {0, 0, 1, 0},   // L-Tetrimino
												 {0, 0, 1, 0},
												 {0, 1, 1, 0},
												 {0, 0, 0, 0} },
										       { {0, 0, 0, 0},
											     {0, 1, 1, 1},
											     {0, 0, 0, 1},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 1},
											     {0, 0, 1, 0},
											     {0, 0, 1, 0},
											     {0, 0, 0, 0} },
										       { {0, 1, 0, 0},
											     {0, 1, 1, 1},
											     {0, 0, 0, 0},
											     {0, 0, 0, 0} } },

											 { { {0, 1, 1, 0},   // J-Tetrimino
												 {0, 0, 1, 0},
												 {0, 0, 1, 0},
												 {0, 0, 0, 0} },
										       { {0, 0, 0, 0},
											     {0, 1, 1, 1},
											     {0, 1, 0, 0},
											     {0, 0, 0, 0} },
										       { {0, 0, 1, 0},
											     {0, 0, 1, 0},
											     {0, 0, 1, 1},
											     {0, 0, 0, 0} },
										       { {0, 0, 0, 1},
											     {0, 1, 1, 1},
											     {0, 0, 0, 0},
											     {0, 0, 0, 0} } } };
	
	public static final int PIECE_TYPES = PIECES.length - 1;

	protected static final int LEGAL = 0;
	protected static final int COLLISION = 1;
	protected static final int TOO_LOW = 2;
	protected static final int OUT_OF_BOUNDS = 3;
	
	protected int[][] board;

	public int AHEAD;
	public int tx;
	public int ty;
	public byte[][][] piece;
	public int pieceID;
	public int rotation;
	public int[] fMoves;
	public int stored;
	public boolean hasStored;
	public int linesCleared;
	private boolean dead;
	public long[] flash;
	private boolean paused;
	public int tickCount;
	public int tickInterval;
	public int tickThreshold;
	public int ticksPerSecond;
	public int maxDelays;
	public int delays;
	public int combo;
	public boolean lastMoveRotate;
	public int spinX;
	public int spinY;
	public int spinR;
	public int spinTick;
	public boolean justCleared;

	protected PieceGenerator gen;

	Tetris(PieceGenerator gen, int ahead)
	{
		board = new int[W][H];
		flash = new long[H];

		if (gen == null)
			gen = new PieceGenerator() {
				public int nextPiece()
				{
					return 0;
				}
				public void newGame() {}
			};
		
		this.gen = gen;
		stored = -1;
		hasStored = false;
		linesCleared = 0;
		dead = false;
		paused = false;
		tickCount = 0;
		tickInterval = 10;
		ticksPerSecond = 20;
		tickThreshold = 10;
		maxDelays = 7;
		delays = 0;
		combo = 0;
		AHEAD = ahead;
		lastMoveRotate = false;
		spinTick = TSPIN_ANIMATION_TICKS * -4;
		justCleared = false;

		fMoves = new int[AHEAD];
		for (int i = 0; i < AHEAD; i++)
			fMoves[i] = gen.nextPiece();
		putPiece();
	}
	Tetris(PieceGenerator gen)
	{
		this(gen, 3);
	}
	Tetris()
	{
		this(null);
	}

	protected void putGivenPiece(int pieceID)
	{
		if (pieceID == 0)
			return;
		
		tx = W / 2 - 2;
		ty = -2;
		piece = PIECES[pieceID];
		this.pieceID = pieceID;
		rotation = 0;
		
		while (pieceLegal() == COLLISION)
			ty--;
	}
	protected void putPiece()
	{
		lastMoveRotate = false;
		putGivenPiece(fMoves[0]);
		hasStored = false;

		for (int i = 1; i < AHEAD; i++)
			fMoves[i-1] = fMoves[i];
		fMoves[AHEAD-1] = gen.nextPiece();
	}
	public void moveLeft()
	{
		lastMoveRotate = false;
		tx--;
		if (pieceLegal() != LEGAL)
			tx++;
		else
			resetTicks();
	}
	public void moveRight()
	{
		lastMoveRotate = false;
		tx++;
		if (pieceLegal() != LEGAL)
			tx--;
		else
			resetTicks();
	}
	
	//KICK_X[direction][rotation][test]
	private static final int[][][] KICK_X =   {{{0, -1, -1, 0, -1},
											    {0,  1,  1, 0,  1},
											    {0,  1,  1, 0,  1},
											    {0, -1, -1, 0, -1}},
											   {{0,  1,  1, 0,  1},
											    {0,  1,  1, 0,  1},
											    {0, -1, -1, 0, -1},
											    {0, -1, -1, 0, -1}}};

	private static final int[][][] KICK_Y =   {{{0, 0,  1, -2, -2},
											    {0, 0, -1,  2,  2},
											    {0, 0,  1, -2, -2},
											    {0, 0, -1,  2,  2}},
											   {{0, 0, -1,  2,  2},
											    {0, 0,  1, -2, -2},
											    {0, 0, -1,  2,  2},
											    {0, 0,  1, -2, -2}}};

	private static final int[][][] I_KICK_X =   {{{0, -2,  1, -2,  1},
											      {0, -1,  2, -1,  2},
											      {0,  2, -1,  2, -1},
											      {0,  1, -2,  1, -2}},
											     {{0, -1,  2, -1,  2},
											      {0,  2, -1,  2, -1},
											      {0,  1, -2,  1, -2},
											      {0, -2,  1, -2,  1}}};
	
	private static final int[][][] I_KICK_Y =   {{{0, 0, 0, -1,  2},
											      {0, 0, 0,  2, -1},
											      {0, 0, 0,  1, -2},
											      {0, 0, 0, -2,  1}},
											     {{0, 0, 0,  2, -1},
											      {0, 0, 0,  1, -2},
											      {0, 0, 0, -2,  1},
											      {0, 0, 0, -1,  2}}};
	
	public void rotate()
	{
		lastMoveRotate = true;
		int oldrot = rotation;
		int oldx = tx;
		int oldy = ty;

		int[] kicks_x = KICK_X[0][rotation];
		int[] kicks_y = KICK_Y[0][rotation];
		
		if (pieceID == 1)
		{
			kicks_x = I_KICK_X[0][rotation];
			kicks_y = I_KICK_Y[0][rotation];
		}
		
		rotation++;
		rotation %= piece.length;

		for (int i = 0; i < kicks_x.length && pieceLegal() != LEGAL; i++)
		{
			tx = oldx;
			ty = oldy;
			tx += kicks_x[i];
			ty -= kicks_y[i];
		}
		if (pieceLegal() != LEGAL)
		{
			rotation = oldrot;
			tx = oldx;
			ty = oldy;
		}
	}
	public void rotateCounter()
	{
		lastMoveRotate = true;
		int oldrot = rotation;
		int oldx = tx;
		int oldy = ty;

		int[] kicks_x = KICK_X[1][rotation];
		int[] kicks_y = KICK_Y[1][rotation];
		
		if (pieceID == 1)
		{
			kicks_x = I_KICK_X[1][rotation];
			kicks_y = I_KICK_Y[1][rotation];
		}
		
		rotation += piece.length - 1;
		rotation %= piece.length;

		for (int i = 0; i < kicks_x.length && pieceLegal() != LEGAL; i++)
		{
			tx = oldx;
			ty = oldy;
			tx += kicks_x[i];
			ty -= kicks_y[i];
		}
		if (pieceLegal() != LEGAL)
		{
			rotation = oldrot;
			tx = oldx;
			ty = oldy;
		}
	}
	public void forceTick()
	{
		delays = 0;
		
		ty++;
		if (pieceLegal() != LEGAL)
		{
			ty--;
			placePiece();
		}
		else
			lastMoveRotate = false;
		tickThreshold = tickCount + tickInterval;
	}
	public boolean canMove(int x, int y)
	{
		tx += x;
		ty += y;
		
		boolean ans = (pieceLegal() == LEGAL);
		
		tx -= x;
		ty -= y;
		
		return ans;
	}
	public void tick()
	{
		if (isPaused() || isOver())
			return;
		
		tickCount++;
		if (tickCount >= tickThreshold)
			forceTick();
	}
	public void resetTicks()
	{
		if (++delays > maxDelays)
			return;
		
		tickThreshold = tickCount + tickInterval;
	}
	public void firmDrop()
	{
		int oldy = ty;
		while (pieceLegal() == LEGAL)
			ty++;
		ty--;
		if (oldy != ty)
		{
			lastMoveRotate = false;
			delays = 0;
		}
	}
	public void drop()
	{
		firmDrop();
		placePiece();
	}
	public boolean store()
	{
		if (hasStored)
			return false;

		if (stored == -1)
		{
			stored = pieceID;
			putPiece();
		}
		else
		{
			int holder = pieceID;
			putGivenPiece(stored);
			stored = holder;
		}
		hasStored = true;
		resetTicks();
		return true;
	}
	public boolean isOver()
	{
		return dead;
	}
	public void pause()
	{
		paused = !paused;
	}
	public boolean isPaused()
	{
		return paused;
	}
	public void setPaused(boolean val)
	{
		paused = val;
	}
	protected void die()
	{
		dead = true;
		paused = false;
	}

	protected void onLinesCleared(int cleared) {}
	protected void onTSpin(int cleared, int x, int y, int rotation)
	{
		tSpinEffect(x, y, rotation);
		onLinesCleared(cleared);
	}
	public void tSpinEffect(int x, int y, int rotation)
	{
		spinX = x;
		spinY = y;
		spinR = (rotation + 2) % 4;
		spinTick = tickCount;
	}
	protected boolean checkClear(boolean tspin, int x, int y, int rotation)
	{
		boolean ans = false;
		int lines = 0;
		
		for (int row = 0; row < H; row++)
		{
			boolean containsColored = false;
			boolean foundEmpty = false;
			for (int i = 0; i < W; i++)
			{
				if (board[i][row] == 0)
				{
					foundEmpty = true;
					break;
				}
				else if (board[i][row] != 8)
					containsColored = true;
			}
			if (foundEmpty || !containsColored)
				continue;

			for (int i = 0; i < W; i++)
			{
				for (int j = row; j >= 1; j--)
					board[i][j] = board[i][j-1];
				board[i][0] = 0;
			}
			linesCleared++;
			lines++;
			flash[row] = System.currentTimeMillis();
			ans = true;
		}
		if (tspin)
			onTSpin(lines, x, y, rotation);
		else
			onLinesCleared(lines);
		
		justCleared = ans;
		return ans;
	}
	protected void placePiece()
	{
		if (dead) return;

		byte[][] arr = piece[rotation];

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (arr[i][j] == 0)
					continue;

				if (ty + j < -1)
					die();
				if (tx + i < 0 || ty + j < 0)
					continue;
				if (tx + i >= W || ty + j >= H)
					continue;
				board[tx+i][ty+j] = pieceID;
			}
		}
		boolean tspin = false;
		int x = tx + 1;
		int y = ty + 2;
		if (pieceID == TPIECE && lastMoveRotate)
		{
			int corners = 0;
			
			if (x <= 0 || y <= 0 || board[x-1][y-1] != 0)
				corners++;
			
			if (x <= 0 || y >= H-1 || board[x-1][y+1] != 0)
				corners++;
			
			if (x >= W-1 || y <= 0 || board[x+1][y-1] != 0)
				corners++;
			
			if (x >= W-1 || y >= H-1 || board[x+1][y+1] != 0)
				corners++;
			
			if (corners >= 3)
				tspin = true;
		}
		if (checkClear(tspin, x, y, rotation))
			combo++;
		else
			combo = 0;
		putPiece();
	}
	protected int pieceLegal()
	{
		byte[][] arr = piece[rotation];
		int err = LEGAL;

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (arr[i][j] == 0)
					continue;

				if (tx + i < 0 || tx + i >= W)
					return OUT_OF_BOUNDS;
				else if (ty + j >= H)
					err = Math.max(err, TOO_LOW);
				else if (ty + j < 0)
					continue;
				else if (board[tx+i][ty+j] != 0)
					err = Math.max(err, COLLISION);
			}
		}
		return err;
	}

	public Tetris newGame()
	{
		gen.newGame();
		Tetris t = new Tetris(gen);
		t.tickInterval = tickInterval;
		t.ticksPerSecond = ticksPerSecond;
		gen = null;
		return t;
	}
	
	protected static void copy(int[][] dest, int[][] source)
	{
		for (int i = 0; i < dest.length && i < source.length; i++)
			for (int j = 0; j < dest[i].length && j < source[i].length; j++)
				dest[i][j] = source[i][j];
	}
	protected static void copy(int[] dest, int[] source)
	{
		for (int i = 0; i < dest.length && i < source.length; i++)
			dest[i] = source[i];
	}

	public Tetris[] children()
	{
		int len = (W+3) * piece.length + 1;
		Tetris[] ans = new Tetris[len];
		int pos = 0;

		for (int i = -3; i < W; i++)
		{
			for (int r = 0; r < piece.length; r++)
			{
				Tetris t = new Tetris();
				t.piece = piece;
				t.ty = -4;
				t.tx = i;
				t.rotation = r;
				t.pieceID = pieceID;
				t.combo = combo;

				if (t.pieceLegal() != LEGAL)
				{
					pos++;
					continue;
				}

				copy(t.board, board);
				copy(t.fMoves, fMoves);
				t.drop();
				ans[pos++] = t;
			}
		}
		if (!(hasStored))
		{
			Tetris t = new Tetris();
			t.piece = piece;
			t.pieceID = pieceID;
			t.combo = combo;
			copy(t.board, board);
			copy(t.fMoves, fMoves);
			t.store();
			ans[pos++] = t;
		}
		
		return ans;
	}
	public int[] xpos()
	{
		int len = (W+3) * piece.length;
		int[] ans = new int[len];
		int pos = 0;

		for (int i = -3; i < W; i++)
		{
			for (int r = 0; r < piece.length; r++)
				ans[pos++] = i;
		}
		return ans;
	}
	public int[] rotations()
	{
		int len = (W+3) * piece.length;
		int[] ans = new int[len];
		int pos = 0;

		for (int i = -3; i < W; i++)
		{
			for (int r = 0; r < piece.length; r++)
				ans[pos++] = r;
		}
		return ans;
	}
	public int evaluate()
	{
		if (dead)
			return Integer.MIN_VALUE;
		
		int ans = 0;

		int totalfree = 2;
		for (int i = 0; i < W; i++)
		{
			int columnfree = 2;
			
			for (int j = 0; j < H; j++)
			{
				if (board[i][j] != 0)
				{
					ans -= 11;
					
					for (int k = j+1; k < H && board[i][k] == 0; k++)
						ans -= 130;
				}
				else
				{
					for (int k = j-1; k >= 0 && board[i][k] != 0; k++)
						ans -= 120;
					
					if ((i <= 0 || board[i-1][j] != 0) && (i >= W-1 || board[i+1][j] != 0))
					{
						if (columnfree > 0)
							columnfree--;
						else if (totalfree > 0)
							totalfree--;
						else
							ans -= 55 + j * 2;
					}
				}
			}
		}

		int h = height();
		ans -= h * 10;
		
		if (h >= 15)
			ans -= 1000000 * h;
		
		if (stored == 1)
			ans++;

		return ans;
	}
	public int height()
	{
		for (int j = 0; j < H; j++)
		{
			for (int i = 0; i < W; i++)
			{
				if (board[i][j] != 0)
					return H - j;
			}
		}
		return 0;
	}
	public int fallDistance()
	{
		byte[][] arr = piece[rotation];
		
		int max = 0;
		mainloop:
		for (int j = 3; j >= 0; j--)
		{
			for (int i = 0; i < 4; i++)
			{
				if (arr[i][j] != 0)
				{
					max = j;
					break mainloop;
				}
			}
		}
		
		return H - ty - max - height() - 1;
	}

	protected static final Color C_BACKGROUND = Color.BLACK;
	protected static final Color C_BORDER = new Color(63, 63, 63);
	protected static final Color C_SHADOW = new Color(0, 0, 0, 63);
	protected static final Color C_GHOST = new Color(180, 180, 180);
	protected static final Color C_GHOST_FILL = new Color(0, 0, 0, 63);
	protected static final Color C_PIECE_HIGHLIGHT = new Color(0, 0, 0, 50);
	protected static final Color C_NOTICE = new Color(255, 255, 255, 225);
												// I, S, T, O, Z, L, J
	protected static final Color[] COLORS = {null, Color.CYAN, Color.RED, Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.ORANGE, new Color(100, 150, 255), new Color(190, 190, 190)};
	protected static final Font F_LINES = new Font(Font.MONOSPACED, Font.BOLD, 24);
	protected static final Font F_TIME = new Font(Font.MONOSPACED, Font.BOLD, 14);
	protected static final Font F_UI = new Font(Font.DIALOG, Font.BOLD, 14);
	protected static final Font F_PAUSE = new Font(Font.SANS_SERIF, Font.BOLD, 36);
	protected static final Font F_GAMEOVER = new Font(Font.SANS_SERIF, Font.BOLD, 48);
	public void drawTo(Graphics2D g, int x, int y)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		y += 60;

		g.setColor(C_BACKGROUND);
		g.fillRect(x, y, FIELD_W, FIELD_H);


		if (!dead)
		{
			Tetris ghost = new Tetris();
			copy(ghost.board, board);
			ghost.tx = tx;
			ghost.ty = ty;
			ghost.rotation = rotation;
			ghost.piece = piece;
			
			while (ghost.pieceLegal() == LEGAL)
				ghost.ty++;
			ghost.ty--;
			
			byte[][] arr = ghost.piece[ghost.rotation];
			for (int i = 0; i < 4; i++)
			{
				for (int j = 0; j < 4; j++)
				{
					if (arr[i][j] == 0)
						continue;
	
					int xpos = i + ghost.tx;
					int ypos = j + ghost.ty;
					
					if (xpos < 0 || ypos < 0)
						continue;
	
					g.setColor(C_GHOST_FILL);
					g.fillRect(x + xpos * SQR_W + 1, y + ypos * SQR_W + 1, SQR_W - 2, SQR_W - 2);
					
					g.setColor(C_GHOST);
					g.drawRect(x + xpos * SQR_W + 1, y + ypos * SQR_W + 1, SQR_W - 2, SQR_W - 2);
					g.drawRect(x + xpos * SQR_W + 2, y + ypos * SQR_W + 2, SQR_W - 4, SQR_W - 4);
				}
			}
		}
		
		for (int i = 0; i < W; i++)
		{
			for (int j = 0; j < H; j++)
			{
				if (board[i][j] == 0)
					continue;

				g.setColor(COLORS[board[i][j]]);
				g.fillRect(x + i * SQR_W, y + j * SQR_W, SQR_W, SQR_W);
				
				g.setColor(C_PIECE_HIGHLIGHT);
				g.fillRect(x + i * SQR_W, y + j * SQR_W, SQR_W, SQR_W);

				g.setColor(COLORS[board[i][j]]);
				g.fillRect(x + i * SQR_W + 6, y + j * SQR_W + 6, SQR_W - 11, SQR_W - 11);

				g.setColor(C_SHADOW);
				g.drawRect(x + i * SQR_W + 1, y + j * SQR_W + 1, SQR_W - 2, SQR_W - 2);
			}
		}

		if (!dead)
		{
			g.setColor(COLORS[pieceID]);
			for (int i = 0; i < 4; i++)
			{
				for (int j = 0; j < 4; j++)
				{
					if (piece[rotation][i][j] == 0)
						continue;
					if (tx + i < 0 || tx + i >= W)
						continue;
					if (ty + j < 0 || ty + j >= H)
						continue;

					g.setColor(COLORS[pieceID]);
					g.fillRect(x + (tx + i) * SQR_W, y + (ty + j) * SQR_W, SQR_W, SQR_W);
					
					g.setColor(C_PIECE_HIGHLIGHT);
					g.fillRect(x + (tx + i) * SQR_W, y + (ty + j) * SQR_W, SQR_W, SQR_W);

					g.setColor(COLORS[pieceID]);
					g.fillRect(x + (tx + i) * SQR_W + 6, y + (ty + j) * SQR_W + 6, SQR_W - 11, SQR_W - 11);

					g.setColor(C_SHADOW);
					g.drawRect(x + (tx + i) * SQR_W + 1, y + (ty + j) * SQR_W + 1, SQR_W - 2, SQR_W - 2);
				}
			}
		}

		g.setColor(C_BORDER);
		for (int i = 0; i < W; i++)
		{
			for (int j = 0; j < H; j++)
				g.drawRect(x + i * SQR_W, y + j * SQR_W, SQR_W, SQR_W);
		}
		
		long time = System.currentTimeMillis();
		for (int i = 0; i < H; i++)
		{
			long diff = time - flash[i];
			if (diff < 0 || diff >= 500)
				continue;
			
			float alpha = (float) ((500 - diff) / 500.0);
			alpha *= alpha * alpha;
			g.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
			g.fillRect(x + 1, y + i * SQR_W + 1, FIELD_W - 1, SQR_W - 1);
		}

		{
			int diff = tickCount - spinTick;
			if (diff >= 0 && diff < TSPIN_ANIMATION_TICKS * 4 && !dead)
			{
				int rotation = (spinR + diff / TSPIN_ANIMATION_TICKS < 2 ? (diff / TSPIN_ANIMATION_TICKS) : 2) % 4;
				BufferedImage img = tspins[rotation];
				if (img != null)
				{
					Composite comp = g.getComposite();
					if (diff >= TSPIN_ANIMATION_TICKS * 2)
					{
						float alpha = 1.0f - (float)(diff - TSPIN_ANIMATION_TICKS * 2) / (TSPIN_ANIMATION_TICKS * 2);
						g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
						g.setColor(new Color(255, 255, 255, 200));
						g.fillRect(x + spinX * SQR_W, y + spinY * SQR_W, SQR_W, SQR_W);
						
						if (rotation != 0 && spinY + 1 < H)
							g.fillRect(x + spinX * SQR_W, y + (spinY + 1) * SQR_W, SQR_W, SQR_W);
						
						if (rotation != 1 && spinX > 0)
							g.fillRect(x + (spinX - 1) * SQR_W, y + spinY * SQR_W, SQR_W, SQR_W);
						
						if (rotation != 2 && spinY > 0)
							g.fillRect(x + spinX * SQR_W, y + (spinY - 1) * SQR_W, SQR_W, SQR_W);
						
						if (rotation != 3 && spinX + 1 < W)
							g.fillRect(x + (spinX + 1) * SQR_W, y + spinY * SQR_W, SQR_W, SQR_W);
					}
					g.drawImage(img, x + spinX * SQR_W - img.getWidth() / 2 + SQR_W / 2, y + spinY * SQR_W - img.getHeight() / 2 + SQR_W / 2, null);
					g.setComposite(comp);
				}
			}
		}

		g.setColor(Color.WHITE);
		g.setFont(F_UI);
		drawCentered(g, "Hold", x - DSP_W / 2, y + 10);
		g.drawRect(x - DSP_W + 10, y + 20, 50, 50);

		if (stored != -1)
			drawTetrimino(g, stored, x - DSP_W / 2, y + 45, 10);

		g.setColor(Color.WHITE);
		drawCentered(g, "Next", x + FIELD_W + DSP_W / 2, y + 10);

		for (int i = 0; i < AHEAD; i++)
		{
			g.setColor(Color.WHITE);
			int yoffset = i * 50 + (i > 0 ? 8 : 0);
			
			g.drawRect(x + FIELD_W + 10, y + 20 + yoffset, 50, 50);
			drawTetrimino(g, fMoves[i], x + FIELD_W + DSP_W / 2, y + 45 + yoffset, 10);
		}
		g.setColor(Color.WHITE);
		g.drawRect(x + FIELD_W + 10 + 1, y + 20 + 1, 50 - 2, 50 - 2);

		if (dead)
		{
			g.setColor(new Color(0, 0, 0, 80));
			g.fillRect(x, y, FIELD_W, FIELD_H);
			
			g.setColor(new Color(0, 0, 0, 150));
			RoundRectangle2D rect = new RoundRectangle2D.Float(x + 15, y - 80 + FIELD_H / 2, FIELD_W - 30, 130, 15, 15);
			g.fill(rect);
			g.setColor(Color.WHITE);
			g.draw(rect);
			
			g.setColor(C_NOTICE);
			g.setFont(F_GAMEOVER);
			drawCentered(g, "GAME", x + FIELD_W / 2, y - 35 + FIELD_H / 2);
			drawCentered(g, "OVER", x + FIELD_W / 2, y + 35 + FIELD_H / 2);
		}
		else if (paused && !isOver())
		{
			g.setColor(new Color(0, 0, 0, 80));
			g.fillRect(x, y, FIELD_W, FIELD_H);

			g.setFont(F_PAUSE);
			FontMetrics m = g.getFontMetrics();
			int wid = m.stringWidth("PAUSED");
			
			g.setColor(new Color(0, 0, 0, 120));
			RoundRectangle2D rect = new RoundRectangle2D.Float(x + FIELD_W / 2 - wid / 2 - 15, y - 5 - 28 + FIELD_H / 2, wid + 30, 50, 10, 5);
			g.fill(rect);
			g.setColor(Color.WHITE);
			g.draw(rect);
			
			g.setColor(C_NOTICE);
			drawCentered(g, "PAUSED", x + FIELD_W / 2, y + 5 + FIELD_H / 2);
		}
		drawAfter(g, x, y);
	}
	protected void drawAfter(Graphics2D g, int x, int y)
	{
		
	}
	protected static void drawTetrimino(Graphics2D g, int id, int x, int y, int sqrw)
	{
		byte[][] arr = PIECES[id][0];

		int frow = Integer.MAX_VALUE;
		int fcol = Integer.MAX_VALUE;
		int lrow = Integer.MIN_VALUE;
		int lcol = Integer.MIN_VALUE;

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (arr[i][j] == 0)
					continue;

				if (i < frow)
					frow = i;
				if (j < fcol)
					fcol = j;

				if (i > lrow)
					lrow = i;
				if (j > lcol)
					lcol = j;
			}
		}

		int xlen = lrow - frow + 1;
		int ylen = lcol - fcol + 1;
		x -= xlen * sqrw / 2;
		y -= ylen * sqrw / 2;

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (arr[i][j] == 0)
					continue;

				g.setColor(COLORS[id]);
				g.fillRect(x + (i - frow) * sqrw, y + (j - fcol) * sqrw, sqrw, sqrw);

				g.setColor(Color.BLACK);
				g.drawRect(x + (i - frow) * sqrw, y + (j - fcol) * sqrw, sqrw, sqrw);
			}
		}
	}
	protected static void drawCentered(Graphics2D g, String s, int x, int y)
	{
		FontMetrics m = g.getFontMetrics();
		g.drawString(s, x - m.stringWidth(s) / 2, y);
	}
	protected String getTimeString()
	{
		int hours = tickCount / ticksPerSecond / 60 / 60;
		int minutes = (tickCount / ticksPerSecond / 60) % 60;
		double seconds = (double) Math.round((double) tickCount / ticksPerSecond % 60 * 10) / 10;
		
		return "" + hours + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (seconds < 10 ? ("0" + seconds) : seconds);
	}
}
