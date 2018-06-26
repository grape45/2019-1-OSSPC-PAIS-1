package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.util.Arrays;


public class BattleAI
{
	public static final BattleAI DEFAULT = new BattleAI(new int[]
														/*{0, (10 * 4) + 20, (15 * 4) + 20, (25 * 4) + 20, (50 * 4) + 20, (5 * 4) + 20,  (5 * 4) + 20,  (-5 * 4) + 20, (-10 * 4) + 20, (-10 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20, (-5 * 4) + 20,
														 0, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20,  (10 * 4) + 20,  (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20, (10 * 4) + 20,
														 0, (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  ( 6 * 4) + 20,  ( 6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,  (6 * 4) + 20,
														 15 * 5, 13 * 5, 3 * 5,
														 50, 100, 28, 0, 10, 5, 2000 / 100});
														 */
												/*{-9, 9, 88, 95, 88, 36, 54, 56, 65, 25, -16, 101, 45, 28, 75, 45, 38, 13, 50, -17, 
												0, 65, 77, 49, 5, 33, 63, 14, -4, -5, 49, 28, 63, 48, 81, -27, 48, 65, 82, 85, 
												10, 4, 55, 40, -15, 21, 26, 31, 54, -14, 42, -17, 39, 43, 46, 71, -16, 13, 1, 75, 
												74, 31, 19, -1, -40, 35, -6, 67, 3, 36});*/
												{61, 22, 53, 69, 76, 7, 67, 39, -38, -4, 16, -4, 22, 10, -10, -5, -12, 25, 85, 21, 
												-13, 55, 96, 39, 50, -16, 27, -9, -10, 25, 28, 38, 5, 73, -3, 14, 11, 4, 18, 2, 
												50, 53, 10, 5, -16, 70, 89, -4, 0, 29, -34, 42, -22, 71, 56, 28, -35, -43, -5, 12, 
												-11, 36, 16, 42, 30, 33, 8, 48, -7, 46});										

	public static final int YIELD = -1;
	
	private TetrisBattle t;
	private boolean usingCombo;

	public final int[] VAL1;
	public final int[] VAL2;
	public final int[] VAL3;
	public final int panicThreshold; // invokes TetrisAI
	public final int comboThreshold; // invokes ComboAI
	public final int stackThreshold; // invokes StackingAI when BELOW this threshold
	
	public final int wHeight; // sub 75
	public final int wCovered; // neg
	public final int wBlock; // sub 20
	public final int wCoveredBlock; // difference from wBlock (sub 50)
	public final int wCoveredSpace; // neg
	public final int wCoveringBlock; // neg
	public final int wLinesPriority; // multiply 100
	
	public final int[] weights;
	
	public static final int LEN = 70;
	
	private static final int W = Tetris.W;
	private static final int H = Tetris.H;
	
	BattleAI(int[] weights)
	{
		VAL1 = new int[20];
		VAL2 = new int[20];
		VAL3 = new int[20];

		VAL1[0] = 0;
		VAL2[0] = 0;
		VAL3[0] = 0;
		
		int pos = 1;

		for (int i = 1; i < 20; i++)
			VAL1[i] = VAL1[i-1] + (weights[pos++] - 20);
		pos++;
		
		for (int i = 1; i < 20; i++)
			VAL2[i] = VAL2[i-1] + (weights[pos++] - 20);
		pos++;
		
		for (int i = 1; i < 20; i++)
			VAL3[i] = VAL3[i-1] + (weights[pos++] - 20);
		
		panicThreshold  = weights[pos++] / 20 + 15;
		comboThreshold  = weights[pos++] / 7 + 7;
		stackThreshold  = Math.min(comboThreshold - 5, weights[pos++] / 5);
		wHeight			= weights[pos++] - 75;
		wCovered		= weights[pos++] * -1;
		wBlock			= weights[pos++] - 20;
		wCoveredBlock	= weights[pos++] / 10 + wBlock - 5;
		wCoveredSpace	= weights[pos++] * -1;
		wCoveringBlock	= weights[pos++] * -1;
		wLinesPriority	= weights[pos++] * 100;
		
		this.weights = weights;
	}
	
	private void bind(TetrisBattle battle)
	{
		t = battle;
		usingCombo = false;
	}
	
	public int[] getMove(TetrisBattle tetris)
	{
		if (tetris.isOver())
			return new int[]{};
		
		if (tetris != t)
			bind(tetris);
		
		int[] moves = null;
		
		if (usingCombo || t.height() >= comboThreshold)
		{
			usingCombo = true;
			moves = c_getMove(t);
			
			if (moves[moves.length - 1] == YIELD && t.height() < 15)
			{
				usingCombo = false;
				moves = null;
			}
		}
		if (moves == null)
			moves = s_getMove(t);
		
		return moves;
	}
	
	private int[] s_getMove(Tetris tetris)
	{
		if (!(tetris instanceof TetrisBattle))
			return TetrisAI.getMove(tetris);
		
		TetrisBattle t = (TetrisBattle) tetris;
		
		TetrisBattle[] children = t.children();
		int[] xpos = t.xpos();
		int[] rotat = t.rotations();

		int bestEval = Integer.MIN_VALUE;
		int bestIndex = 0;
		for (int i = 0; i < children.length; i++)
		{
			if (children[i] == null)
				continue;

			int val;
			
			if (i == children.length - 1)
				val = s_dfs(children[i], 2);
			else
				val = s_dfs(children[i], 1);
			
			if (val > bestEval)
			{
				bestEval = val;
				bestIndex = i;
			}
		}

		if (bestIndex == children.length - 1)
			return new int[]{TetrisAI.SWAP};
		
		int dx = xpos[bestIndex];
		int dr = rotat[bestIndex];

		while (dr < t.rotation)
			dr += t.piece.length;

		int xneeded = Math.abs(dx - t.tx);
		int rneeded = dr - t.rotation;
		
		boolean counter = rneeded == 3;
		if (counter)
			rneeded = 1;

		int len = xneeded + rneeded + 1;
		int[] ans = new int[len];
		int pos = 0;

		if (counter)
			ans[pos++] = TetrisAI.ROTATE_COUNTER;
		else
			for (int i = t.rotation; i < dr; i++)
				ans[pos++] = TetrisAI.ROTATE;

		if (dx < t.tx)
			for (int i = dx; i < t.tx; i++)
				ans[pos++] = TetrisAI.LEFT;

		if (dx > t.tx)
			for (int i = t.tx; i < dx; i++)
				ans[pos++] = TetrisAI.RIGHT;

		ans[pos] = TetrisAI.DROP;
		
		return ans;
	}

	private int s_dfs(TetrisBattle t, int depth)
	{
		if (depth <= 0)
			return s_evaluate(t);

		TetrisBattle[] children = t.children();

		int best = Integer.MIN_VALUE;
		for (int i = 0; i < children.length; i++)
			if (children[i] != null)
				best = Math.max(best, s_dfs(children[i], children[i].hasStored ? depth : (depth - 1)));

		return best;
	}
	
	private int s_evaluate(TetrisBattle t)
	{
		if (t.isOver())
			return Integer.MIN_VALUE;
		
		int ans = 0;
		
		int h = t.height();
		ans += h * wHeight;
		
		boolean[] covered = new boolean[H];
		int n1 = 0,
			n2 = 0,
			n3 = 0;
		for (int j = 0; j < H; j++)
		{
			if (covered[j])
				ans += wCovered;
			
			boolean adj = true;
			boolean foundEmpty = false;
			int count = 0;
			for (int i = 0; i < W; i++)
			{
				if (t.board[i][j] != 0)
				{
					count++;
					if (covered[j])
						ans += wCoveringBlock;
					else
						ans += wBlock;
					
					for (int k = j+1; k < H && t.board[i][k] == 0; k++)
					{
						covered[k] = true;
						ans += wCoveredSpace;
					}
				}
				else
				{
					if (foundEmpty)
					{
						if (t.board[i-1][j] != 0)
							adj = false;
					}
					else
						foundEmpty = true;
					
					for (int k = j-1; k >= 0 && t.board[i][k] != 0; k--)
						ans += wCoveringBlock;
				}
				if (i > 0 && i < W-1 && t.board[i-1][j] != 0 && t.board[i][j] == 0 && t.board[i+1][j] != 0)
					ans -= 40;
			}
			if (adj && (t.board[0][j] == 0 || t.board[W-1][j] == 0))
			{
				if (count == W-1)
					n1++;
				if (count == W-2)
					n2++;
				if (count == W-3)
					n3++;
			}
			if (!adj && count == W-2)
				ans -= 40;
		}
		ans += VAL1[n1];
		ans += VAL2[n2];
		ans += VAL3[n3];
		
		if (t.stored == 1)
			ans += 10 * h;
		
		return ans;
	}
	
	private int[] c_getMove(Tetris tetris)
	{
		if (!(tetris instanceof TetrisBattle))
			return TetrisAI.getMove(tetris);
		
		TetrisBattle t = (TetrisBattle) tetris;
		
		TetrisBattle[] children = t.children();
		int[] xpos = t.xpos();
		int[] rotat = t.rotations();

		int bestEval = Integer.MIN_VALUE;
		int bestIndex = 0;
		for (int i = 0; i < children.length; i++)
		{
			if (children[i] == null)
				continue;

			int val = c_dfs(children[i], 1);
			
			if (val > bestEval)
			{
				bestEval = val;
				bestIndex = i;
			}
		}

		if (bestIndex == children.length - 1)
			return new int[]{TetrisAI.SWAP};
		
		boolean yield = (bestEval / wLinesPriority <= t.linesSent + 1);
		
		int dx = xpos[bestIndex];
		int dr = rotat[bestIndex];

		while (dr < t.rotation)
			dr += t.piece.length;

		int xneeded = Math.abs(dx - t.tx);
		int rneeded = dr - t.rotation;
		
		boolean counter = rneeded == 3;
		if (counter)
			rneeded = 1;

		int len = xneeded + rneeded + 1 + (yield ? 1:0);
		int[] ans = new int[len];
		int pos = 0;

		if (counter)
			ans[pos++] = TetrisAI.ROTATE_COUNTER;
		else
			for (int i = t.rotation; i < dr; i++)
				ans[pos++] = TetrisAI.ROTATE;

		if (dx < t.tx)
			for (int i = dx; i < t.tx; i++)
				ans[pos++] = TetrisAI.LEFT;

		if (dx > t.tx)
			for (int i = t.tx; i < dx; i++)
				ans[pos++] = TetrisAI.RIGHT;

		ans[pos++] = TetrisAI.DROP;
		if (yield)
			ans[pos++] = YIELD;
		
		return ans;
	}
	
	private int c_dfs(TetrisBattle t, int depth)
	{
		if (t.pieceID == 0 || (depth <= 0 && !t.justCleared) || depth <= -2 || this.t.fallDistance() <= 0)
			return t.linesSent * wLinesPriority + t.evaluate();

		TetrisBattle[] children = t.children();

		int best = Integer.MIN_VALUE;
		for (int i = 0; i < children.length; i++)
			if (children[i] != null)
				best = Math.max(best, c_dfs(children[i], children[i].hasStored ? depth : (depth - 1)));

		return best;
	}

	@Override
	public String toString() {
		return "BattleAI [VAL1=" + Arrays.toString(VAL1) + ",\n\t\tVAL2="
				+ Arrays.toString(VAL2) + ",\n\t\tVAL3=" + Arrays.toString(VAL3)
				+ ",\n\t\tpanicThreshold=" + panicThreshold + ", comboThreshold="
				+ comboThreshold + ", stackThreshold=" + stackThreshold
				+ ", wHeight=" + wHeight + ", wCovered=" + wCovered
				+ ", wBlock=" + wBlock + ", wCoveredBlock=" + wCoveredBlock
				+ ", wCoveredSpace=" + wCoveredSpace + ", wCoveringBlock="
				+ wCoveringBlock + ", wLinesPriority=" + wLinesPriority + "]";
	}

}
