package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.util.*;
import java.io.*;

public class Genetic
{
	private static final Random rng = new Random();
	
	private static final int N = 10;
	private static final int LEN = BattleAI.LEN;
	
	private Genetic() {}
	
	public static void main(String[] args) throws InterruptedException
	{
		System.out.print("Enter starting generation: ");
		
		Scanner sc = new Scanner(System.in);
		int generation = sc.nextInt();
		sc.close();
		
		while (true)
		{
			System.out.println();
			System.out.println();
			System.out.println("Generation #" + generation);
			System.out.println("---------------");
			System.out.println();
			
			BattleAI[] ai = read("ai.txt");
			Pair[] pairs = new Pair[N];
			Worker[] workers = new Worker[N];
			
			for (int i = 0; i < pairs.length; i++)
			{
				System.out.println("Starting test #" + (i + 1));
				pairs[i] = new Pair(ai[i], 0);
				workers[i] = new Worker(pairs[i], i + 1);
				workers[i].setDaemon(true);
				workers[i].start();
			}
			
			for (int i = 0; i < pairs.length; i++)
				workers[i].join();
			
			Arrays.sort(pairs, pairs[0]);
			
			for (int i = pairs.length - 1; i >= 0; i--)
			{
				System.out.println(pairs[i].ai);
				System.out.println("\t" + pairs[i].value);
				System.out.println();
			}
			
			BattleAI[] newai = new BattleAI[N];
			newai[0] = pairs[0].ai;
			newai[1] = pairs[1].ai;
			newai[2] = pairs[2].ai;
			newai[3] = mutate(pairs[0].ai);
			newai[4] = mutate(pairs[1].ai);
			newai[5] = mutate(pairs[2].ai);
			newai[6] = pairs[3].ai;
			newai[7] = pairs[4].ai;
			newai[8] = randomAI();
			newai[9] = randomAI();
			
			write(newai, "ai.txt");
			generation++;
		}
	}
	
	@SuppressWarnings("resource")
	private static BattleAI[] read(String filename)
	{
		Scanner sc = null;
		try {
			System.out.println("Reading data from " + filename);
			
			sc = new Scanner(new FileReader(filename));

			int len = sc.nextInt();
			if (len != LEN)
			{
				System.out.println("\tFile's LEN of " + len + " does not match required LEN of " + LEN);
				throw new Exception();
			}
			
			BattleAI[] arr = new BattleAI[N];
			for (int i = 0; i < arr.length; i++)
			{
				int[] w = new int[LEN];
				for (int j = 0; j < w.length; j++)
					w[j] = sc.nextInt();
				
				arr[i] = new BattleAI(w);
			}
			sc.close();
			sc = null;
			
			System.out.println("\tSuccessfully read AI");
			
			return arr;
		}
		catch (Exception ex)
		{
			try {
				if (sc != null)
					sc.close();
			} catch (Exception ex2) {}
			
			System.out.println("\tAn exception occurred: " + ex);
			
			BattleAI[] arr = new BattleAI[N];
			for (int i = 0; i < N; i++)
				arr[i] = randomAI();
			
			System.out.println("\tCreated random AI");
			
			return arr;
		}
	}
	
	private static void write(BattleAI[] arr, String filename)
	{
		System.out.println("Writing data to " + filename);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filename));
			
			out.write(String.valueOf(LEN));
			out.newLine();
			out.newLine();
			
			for (int i = 0; i < arr.length; i++)
			{
				int[] w = arr[i].weights;

				for (int j = 0; j < 20; j++)
					out.write(String.valueOf(w[j]) + " ");
				out.newLine();
				for (int j = 20; j < 40; j++)
					out.write(String.valueOf(w[j]) + " ");
				out.newLine();
				for (int j = 40; j < 60; j++)
					out.write(String.valueOf(w[j]) + " ");
				out.newLine();
				for (int j = 60; j < LEN; j++)
					out.write(String.valueOf(w[j]) + " ");
				out.newLine();
				out.newLine();
				out.newLine();
				out.newLine();
			}
			out.close();
			out = null;
			
			System.out.println("\tSuccessfully wrote AI");
		}
		catch (Exception ex)
		{
			try {
				if (out != null)
					out.close();
			}
			catch (Exception ex2) {}
			
			System.out.println("\tCould not write AI: " + ex);
			System.out.print("\tEnter a new filename to retry: ");
			
			try {
				String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
				if (s.length() != 0)
					write(arr, s);
			}
			catch (Exception ex2)
			{
				System.out.println("error: can't receive screen input");
			}
		}
	}
	
	public static int value(BattleAI ai)
	{
		TetrisBattle t = new TetrisBattle(new BagGen());
		for (int i = 0; i < 1600;)
		{
			int[] moves = ai.getMove(t);
			
			if (moves.length == 0)
				break;
			
			for (int j = 0; j < moves.length; j++)
			{
				switch (moves[j])
				{
				case TetrisAI.LEFT:
					t.moveLeft();
					break;
				case TetrisAI.RIGHT:
					t.moveRight();
					break;
				case TetrisAI.ROTATE:
					t.rotate();
					break;
				case TetrisAI.ROTATE_COUNTER:
					t.rotateCounter();
					break;
				case TetrisAI.SWAP:
					t.store();
					break;
				case TetrisAI.DROP:
					t.drop();
					i++;
					break;
				}
			}
		}
		return t.linesSent / 4;
	}
	
	private static BattleAI randomAI()
	{
		int[] arr = new int[LEN];
		
		for (int i = 0; i < arr.length; i++)
			arr[i] = rng.nextInt(100);
		
		return new BattleAI(arr);
	}
	
	private static BattleAI mutate(BattleAI parent)
	{
		int[] arr = new int[LEN];
		
		for (int i = 0; i < arr.length; i++)
			arr[i] = parent.weights[i] + rng.nextInt(10) - 5;
		
		return new BattleAI(arr);
	}
	
	private static class Pair implements Comparator<Pair>
	{
		public BattleAI ai;
		public int value;
		
		Pair(BattleAI ai, int value)
		{
			this.ai = ai;
			this.value = value;
		}
		
		public int compare(Pair p1, Pair p2)
		{
			if (p1.value < p2.value)
				return 1;
			if (p2.value < p1.value)
				return -1;
			return 0;
		}
	}
	
	private static class Worker extends Thread
	{
		private Pair pair;
		public int id;
		
		Worker(Pair pair, int id)
		{
			this.pair = pair;
			this.id = id;
		}
		
		public void run()
		{
			pair.value = Genetic.value(pair.ai);
			System.out.println("Completed test #" + id);
		}
	}
}
