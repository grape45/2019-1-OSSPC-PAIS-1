package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class TetrisBattle extends Tetris
{
	TetrisBattle other;
	TetrisBattle newGame;
	
	private boolean linked;
	public int linesSent;
	private boolean victory;
	private int balance;
	private int crntLines;
	private boolean garbage;
	private boolean sending;
	private ArrayList<Integer> deficit = new ArrayList<Integer>();
	
	TetrisBattle(PieceGenerator gen1, PieceGenerator gen2, boolean garbage)
	{
		super(gen1, 5);
		other = new TetrisBattle(gen2, this, garbage);
		linesSent = 0;
		victory = false;
		balance = 0;
		crntLines = 0;
		sending = false;
		linked = true;
		this.garbage = garbage;
	}
	TetrisBattle(PieceGenerator gen1)
	{
		super(gen1, 5);
		other = null;
		linesSent = 0;
		victory = false;
		balance = 0;
		crntLines = 0;
		sending = false;
		linked = true;
	}
	private TetrisBattle(PieceGenerator gen, TetrisBattle other, boolean garbage)
	{
		super(gen, 5);
		this.other = other;
		linesSent = 0;
		victory = false;
		balance = 0;
		crntLines = 0;
		sending = false;
		linked = true;
		this.garbage = garbage;
	}
	
	public TetrisBattle getPaired()
	{
		return other;
	}
	
	public void tick()
	{
		super.tick();
		
		if (sending && garbage)
		{
			for (int i = 0; i * 10 < deficit.size(); i++)
				addLine(deficit.remove(deficit.size() - 1));
		}
		
		if (deficit.isEmpty() && garbage)
			sending = false;
		
		if (crntLines > balance && sending)
		{
			addLine();
			crntLines--;
		}
		if (crntLines < balance && crntLines < 0)
		{
			clearLine();
			crntLines++;
		}
		if (crntLines == balance && !garbage)
			sending = false;
	}
	
	public boolean isOver()
	{
		return super.isOver() || victory;
	}
	
	public void pause()
	{
		linked = true;
		
		super.pause();
		if (other.isPaused() != isPaused() && linked && other.linked)
			other.pause();
	}
	
	public void setPaused(boolean val)
	{
		linked = true;
		
		super.setPaused(val);
		if (other.isPaused() != val && linked && other.linked)
			other.setPaused(val);
	}
	
	public void setPausedIndependent(boolean val)
	{
		linked = false;
		super.setPaused(val);
	}
	
	public void die()
	{
		boolean wasLinked = linked;
		super.die();
		sending = false;
		linked = wasLinked;
		
		if (other != null)
		{
			other.victory = true;
		
			int count = Math.min(tickCount, other.tickCount);
			tickCount = other.tickCount = count;
		}
	}
	
	private void clearLine()
	{
		for (int i = 0; i < W; i++)
		{
			for (int j = H - 1; j > 0; j--)
				board[i][j] = board[i][j-1];
			board[i][0] = 0;
		}
	}
	
	private void addLine()
	{
		if (height() == H)
			die();
		
		for (int i = 0; i < W; i++)
		{
			for (int j = 0; j < H - 1; j++)
				board[i][j] = board[i][j+1];
			board[i][H-1] = 8;
		}
		while (pieceLegal() != LEGAL)
			ty--;
	}
	private void addLine(int hole)
	{
		if (height() == H)
			die();
		
		for (int i = 0; i < W; i++)
		{
			for (int j = 0; j < H - 1; j++)
				board[i][j] = board[i][j+1];
			if (i == hole)
				board[i][H-1] = 0;
			else
				board[i][H-1] = 8;
		}
		while (pieceLegal() != LEGAL)
			ty--;
	}
	
	private void updateBalance()
	{
		if (other == null)
			return;
		
		int b = linesSent - other.linesSent;
		
		balance = b;
		
		if (other.balance != -b)
			other.updateBalance();
	}
	private void sendGarbage(int lines)
	{
		if (other == null)
			return;
		
		int index = (int) (Math.random() * 10);
		
		for (int i = 0; i < lines; i++)
			other.deficit.add(index);
	}
	
	private static final int[] VALUES = {0, 0, 1, 2, 4};
	private static final int[] COMBOS = {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6};
	public void onLinesCleared(int cleared)
	{
		int initial = linesSent;
		if (cleared < VALUES.length)
			linesSent += VALUES[cleared];
		else
			linesSent += VALUES[VALUES.length - 1];
		
		if (cleared > 0)
		{
			if (combo >= COMBOS.length)
				linesSent += COMBOS[COMBOS.length - 1];
			else
				linesSent += COMBOS[combo];
			
			if (cleared > 2)
				combo++;
		}
		
		int change = linesSent - initial;
		if (garbage)
		{
			if (change > 0)
			{
				if (deficit.isEmpty())
					sendGarbage(change);
				else
				{
					if (change >= deficit.size())
					{
						sendGarbage(change - deficit.size());
						deficit.clear();
					}
					else if (deficit.size() > 0)
					{
						for (int i = 0; i < change; i++)
							deficit.remove(deficit.size() - 1);
					}
					else
						sendGarbage(change);
				}
			}
		}
		else
			updateBalance();
		
		if (cleared <= 0)
			sending = true;
	}
	public void onTSpin(int cleared, int x, int y, int rotation)
	{
		tSpinEffect(x, y, rotation);
		onLinesCleared(cleared + 2);
	}
	
	public TetrisBattle newGame()
	{
		if (newGame != null)
			return newGame;
		if (other == null)
			return null;
		
		gen.newGame();
		other.gen.newGame();
		TetrisBattle t = new TetrisBattle(gen, other.gen, garbage);
		t.tickInterval = tickInterval;
		t.ticksPerSecond = ticksPerSecond;
		t.getPaired().tickInterval = other.tickInterval;
		t.getPaired().ticksPerSecond = other.ticksPerSecond;
		if (!linked)
		{
			t.linked = false;
			t.setPausedIndependent(true);
		}
		if (!other.linked)
		{
			t.getPaired().linked = false;
			t.getPaired().setPausedIndependent(true);
		}
		newGame = t;
		other.newGame = t.getPaired();
		gen = null;
		return t;
	}
	
	public TetrisBattle[] children()
	{
		int len = (W+3) * piece.length + 1;
		TetrisBattle[] ans = new TetrisBattle[len];
		int pos = 0;

		for (int i = -3; i < W; i++)
		{
			for (int r = 0; r < piece.length; r++)
			{
				TetrisBattle t = new TetrisBattle(null);
				t.piece = piece;
				t.ty = -4;
				t.tx = i;
				t.rotation = r;
				t.pieceID = pieceID;
				t.combo = combo;
				t.linesSent = linesSent;
				t.hasStored = hasStored;
				t.stored = stored;

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
			TetrisBattle t = new TetrisBattle(null);
			t.piece = piece;
			t.pieceID = pieceID;
			t.combo = combo;
			t.linesSent = linesSent;
			t.hasStored = hasStored;
			t.stored = stored;
			copy(t.board, board);
			copy(t.fMoves, fMoves);
			t.store();
			ans[pos++] = t;
		}
		
		return ans;
	}
	
	public void drawTo(Graphics2D g, int x, int y)
	{
		x += DSP_W;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.BLACK);
		g.setFont(F_LINES);
		g.drawString("" + linesSent + " lines sent", x + 20, y + 10);
		
		g.setFont(F_TIME);
		
		g.drawString(getTimeString(), x + 20, y + 40);
		if (combo > 0)
			g.drawString("Combo " + combo + " (+" + COMBOS[combo >= COMBOS.length ? (COMBOS.length - 1) : combo]
					+ ")", x + 120, y + 40);
		
		g.setColor(Color.RED);
		if (garbage)
			g.fillRect(x , y + 52, Math.min(deficit.size(), H) * FIELD_W / H, 3);
		else
		{
			int queued = crntLines - balance;
			if (queued > 0)
			{
				g.fillRect(x , y + 52, Math.min(queued, H) * FIELD_W / H, 3);
			}
		}
		
		super.drawTo(g, x, y);
	}
	protected void drawAfter(Graphics2D g, int x, int y)
	{
		if (victory)
		{
			g.setColor(new Color(0, 0, 0, 80));
			g.fillRect(x, y, FIELD_W, FIELD_H);

			g.setFont(F_PAUSE);
			FontMetrics m = g.getFontMetrics();
			int wid = m.stringWidth("VICTORY") - 10;
			
			g.setColor(new Color(0, 0, 0, 120));
			RoundRectangle2D rect = new RoundRectangle2D.Float(x + FIELD_W / 2 - wid / 2 - 15, y - 5 - 28 + FIELD_H / 2, wid + 30, 50, 10, 5);
			g.fill(rect);
			g.setColor(Color.WHITE);
			g.draw(rect);
			
			g.setColor(C_NOTICE);
			drawCentered(g, "VICTORY", x + FIELD_W / 2, y + 5 + FIELD_H / 2);
		}
	}
}
