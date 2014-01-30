package main;

import img.ImageToolkit;

public class BenchmarkScreenshot 
{
	private static final int NUM_ITERS = 10;
	public static void main(String[] args)
	{
		new BenchmarkScreenshot().go();
	}
	public void go()
	{
		double time = System.currentTimeMillis();
		for(int a = 0; a < NUM_ITERS; a++)
		{
			double singleTime = System.currentTimeMillis();
			ImageToolkit.takeScreenshot();
			System.out.println(System.currentTimeMillis() - singleTime);
		}
		System.out.println("Robot time "+((System.currentTimeMillis() - time) / 10));
	}
}
