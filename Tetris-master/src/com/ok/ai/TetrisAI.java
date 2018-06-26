package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/


public class TetrisAI
{
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int ROTATE = 3;
	public static final int ROTATE_COUNTER = 4;
	public static final int SWAP = 5;
	public static final int DROP = 6;
	
	public static int[] getMove(Tetris t)
	{
		if (t.stored == -1)
			return new int[]{SWAP};
		
		Tetris[] children = t.children();
		int[] xpos = t.xpos();
		int[] rotat = t.rotations();

		int bestEval = Integer.MIN_VALUE;
		int bestIndex = 0;
		for (int i = 0; i < children.length; i++)
		{
			if (children[i] == null)
				continue;

			int val = dfs(children[i], 1);
			
			if (val > bestEval)
			{
				bestEval = val;
				bestIndex = i;
			}
		}

		if (bestIndex == children.length - 1)
			return new int[]{SWAP};
		
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
			ans[pos++] = ROTATE_COUNTER;
		else
			for (int i = t.rotation; i < dr; i++)
				ans[pos++] = ROTATE;

		if (dx < t.tx)
			for (int i = dx; i < t.tx; i++)
				ans[pos++] = LEFT;

		if (dx > t.tx)
			for (int i = t.tx; i < dx; i++)
				ans[pos++] = RIGHT;

		ans[pos] = DROP;
		
		return ans;
	}

	private static int dfs(Tetris t, int depth)
	{
		if (depth <= 0)
			return t.evaluate();

		Tetris[] children = t.children();

		int best = Integer.MIN_VALUE;
		for (int i = 0; i < children.length; i++)
			if (children[i] != null)
				best = Math.max(best, dfs(children[i], children[i].hasStored ? depth : (depth - 1)));

		return best;
	}

	/*
	private static class Comp implements Comparator<Tetris>
	{
		public int compare(Tetris t1, Tetris t2)
		{
			if (t1 == null)
				if (t2 == null)
					return 0;
				else
					return 1;
			if (t2 == null)
				return -1;

			if (t1.tx > t2.tx)
				return 1;
			if (t1.tx < t2.tx)
				return -1;
			return 0;
		}
	}*/
}
