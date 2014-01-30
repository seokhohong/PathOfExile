package main;

import img.BleedResult;
import map.GlobalMap;
import window.PWindow;
import window.WindowManager;
import data.Config;

public class TestLabel 
{
	public static void main(String[] args)
	{
		new TestLabel().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		for(BleedResult result : GlobalMap.getLabels(window))
		{
			System.out.println(result.toRectangle());
		}
	}
}
