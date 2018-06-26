package com.ok.window;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.ok.classes.Block;

public class EnemyBoard extends JPanel{
	public Block[] map;
	public int test = 0;
	private final int EPANEL_WIDTH = 200;
	private final int EPANEL_HEIGHT = 420;
	public EnemyBoard() {
		enemystart();
	}
	
	public void enemystart() {

	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, EPANEL_WIDTH, EPANEL_HEIGHT);
		g.setColor(Color.darkGray);
		for (int i = 0; i < 10; i++)
		{
			g.drawLine(i*20, 0, i*20, EPANEL_HEIGHT);
		}
		for (int j = 0; j < 21; j++)
		{
			g.drawLine(0, j*20, EPANEL_WIDTH, j*20);
		}
		if (test == 1) {
			Block[][] test = new Block[21][10];
				for (int a = 0; a < 21; a++)
				{
					for(int b = 0; b < 10; b++)
					{
						if (map[a*10 + b] != null)
						{	
							test[a][b] = map[a*10 + b];
							test[a][b].drawEnemyBlock(g, b, a);
						}
					}
				}
		}
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, EPANEL_WIDTH, EPANEL_HEIGHT);
	}

	public void fun() {
		test = 1;
		this.repaint();
	}
}
