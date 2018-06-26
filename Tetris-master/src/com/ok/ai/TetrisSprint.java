package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;


public class TetrisSprint extends Tetris
{
	public int lines = 40;
	private String timeString;
	boolean displayClear = false;
	
	TetrisSprint()
	{}
	TetrisSprint(PieceGenerator gen)
	{
		super(gen);
	}
	
	public void drawTo(Graphics2D g, int x, int y)
	{
		x += DSP_W;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.BLACK);
		g.setFont(F_LINES);
		g.drawString("" + (lines > 0 ? lines : 0) + " lines left", x + 30, y + 10);
		
		g.setFont(F_TIME);
		
		g.drawString(getTimeString(), x + 30, y + 32);
		
		super.drawTo(g, x, y);
	}
	public void drawAfter(Graphics2D g, int x, int y)
	{
		if (displayClear)
		{
			g.setFont(F_PAUSE);
			FontMetrics m = g.getFontMetrics();
			int wid = m.stringWidth("CLEAR!");
			
			g.setColor(new Color(0, 0, 0, 120));
			RoundRectangle2D rect = new RoundRectangle2D.Float(x + FIELD_W / 2 - wid / 2 - 15, y - 5 - 28 + FIELD_H / 2, wid + 30, 50, 10, 5);
			g.fill(rect);
			
			g.setColor(C_NOTICE);
			drawCentered(g, "CLEAR!", x + FIELD_W / 2, y + 5 + FIELD_H / 2);
		}
	}
	
	public void onLinesCleared(int cleared)
	{
		boolean notdone = lines > 0;
		lines -= cleared;
		
		if (lines <= 0 && notdone)
			displayClear = true;
		else
			displayClear = false;
		
		if (lines <= 0 && timeString == null)
			timeString = getTimeString() + " total";
	}
	public void onTSpin(int cleared, int nul1, int nul2, int nul3)
	{
		onLinesCleared(cleared);
	}
	
	public int evaluate()
	{
		if (lines <= 0)
			return Integer.MAX_VALUE;
		else
			return super.evaluate();
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
				TetrisSprint t = new TetrisSprint();
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
			TetrisSprint t = new TetrisSprint();
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
	
	public String getTimeString()
	{
		return timeString == null ? super.getTimeString() : timeString;
	}
	
	public TetrisSprint newGame()
	{
		gen.newGame();
		TetrisSprint t = new TetrisSprint(gen);
		t.tickInterval = tickInterval;
		t.ticksPerSecond = ticksPerSecond;
		gen = null;
		return t;
	}
}
