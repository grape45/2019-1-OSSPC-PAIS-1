package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class TetrisMarathon extends Tetris
{
	public int points = 0;
	
	public static final int[] VALUES = {0, 100, 175, 350, 700, 1000};
	
	TetrisMarathon()
	{}
	TetrisMarathon(PieceGenerator gen)
	{
		super(gen);
	}
	
	public void onLinesCleared(int cleared)
	{
		points += VALUES[cleared] * (combo + 1);
		if (cleared >= 3)
			combo++;
	}
	public void onTSpin(int cleared, int x, int y, int rotation)
	{
		onLinesCleared(cleared + 2);
		tSpinEffect(x, y, rotation);
	}
	
	public void drawTo(Graphics2D g, int x, int y)
	{
		x += DSP_W;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.BLACK);
		g.setFont(F_LINES);
		g.drawString("" + points, x + 30, y + 10);
		
		g.setFont(F_TIME);
		
		g.drawString("" + linesCleared + " lines", x + 140, y + 32);
		g.drawString(getTimeString(), x + 30, y + 32);
		if (combo > 0)
			g.drawString("x " + (combo + 1), x + 140, y + 7);
		
		super.drawTo(g, x, y);
	}
	
	public int evaluate()
	{
		return super.evaluate() + points / 100;
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
				TetrisMarathon t = new TetrisMarathon();
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
			TetrisMarathon t = new TetrisMarathon();
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
	
	public TetrisMarathon newGame()
	{
		gen.newGame();
		TetrisMarathon t = new TetrisMarathon(gen);
		t.tickInterval = tickInterval;
		t.ticksPerSecond = ticksPerSecond;
		gen = null;
		return t;
	}
}
