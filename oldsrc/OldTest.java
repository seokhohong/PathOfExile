package main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import window.PWindow;

import arena.Arena;

import combat.*;

import macro.Macro;
import map.LevelMap;

import img.*;
import items.Letters;

public class OldTest
{
	static final Rectangle itemRegion = new Rectangle(10, 80, 630, 540); 
	public static void main(String[] args)
	{
		//Config.init();
		new OldTest().testHealth();
		System.exit(0);
	}
	private void testHealth()
	{
		//IntBitmap one = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_436.bmp"));
		//System.out.println(HealthBar.shouldHeal(one.subimage(PWindow.getWindows(one).get(0).getHEALTH_RECT())));
	}
	private void testItemRecognition()
	{
		IntBitmap word = IntBitmap.getInstance(ImageToolkit.loadImage("char/ToCut/word1.bmp"));
		ArrayList<IntBitmap> letters = Letters.cutLetters(word);
		for(int i = 0; i < letters.size(); i ++) 
		{
			letters.get(i).export("export/Letters/" + i + ".bmp");
		}
	}
	private void testGrid()
	{
		IntBitmap one = IntBitmap.getInstance(ImageToolkit.loadImage("img/429.bmp"));
		IntBitmap two = IntBitmap.getInstance(ImageToolkit.loadImage("img/430.bmp"));
		LevelMap levelMap = new LevelMap(one.toGreyscale().doubleCutoff(ImageToolkit.MAX_VAL / 2));
		levelMap.addImage(two.toGreyscale().doubleCutoff(ImageToolkit.MAX_VAL / 2));
		levelMap.export("export/map.txt");
	}
	private void testDelta()
	{
		IntBitmap one = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_774.bmp"));
		IntBitmap two = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_775.bmp"));
		//Combat test = new Combat(one, two);
		//grey.display();
		/*IntBitmap one1 = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_750.bmp"));
		IntBitmap two1 = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_751.bmp"));
		one1.subtract(two1); 
		one1.blur(1);
		one1.highPass(10);
		one1.export("export/delta2.bmp");
		IntBitmap one2 = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_749.bmp"));
		IntBitmap two2 = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_751.bmp"));
		one2.subtract(two2); 
		one2.blur(1);
		one2.highPass(10);
		one2.export("export/delta3.bmp");*/
		//one.toGreyscale().display();
		/*
		ArrayList[] ops = new ArrayList[1];
		ops[0] = new ArrayList();
		ops[0].add("bandPassFilter");       // fn name
		ops[0].add(200.0);                   // lowCutoff: raise to blur
		ops[0].add(300.0);                  // highCutoff: lower to sharpen
		GreyscaleImage grey = one.toGreyscale();
		FourierTransform.operations(grey, ops);    // Fourier transform
		grey.multiply(1.5f);
		grey.highPass(50);
		grey.display();
		*/
		/*
		int[][][] oneData = one.getData();
		int[][][] twoData = two.getData();
		int[][][] newData = new int[one.getWidth()][one.getHeight()][];
		for(int a = 0; a < one.getWidth(); a++)
		{
			for(int b = 0; b < one.getHeight(); b++)
			{
				newData[a][b] = new int[IntBitmap.RGB];
				for(int c = 0; c < IntBitmap.RGB; c++)
				{
					newData[a][b][c] = Math.max(twoData[a][b][c] - oneData[a][b][c], 0);
				}
			}
		}
		IntBitmap subtracted = IntBitmap.getInstance(newData);
		subtracted.export("img/subtracted.bmp");
		*/
		
		//one.export("export/subtracted.bmp");
	}
	private BinaryImage processMap(String filename, Arena arena)
	{
		BufferedImage img = ImageToolkit.loadImage("img/"+filename+".png");
		PWindow window = PWindow.getWindows(IntBitmap.getInstance(img)).get(0);
		BufferedImage minimap = window.getMinimap(img);
		BinaryImage preprocessed = arena.processMap(IntBitmap.getInstance(minimap));
		//BinaryImage processed = LevelMap.preprocessMap(preprocessed);
		return preprocessed;
	}
	private void testMap()
	{
		Arena currentArena = Arena.fromString("Lion's Eye Watch1");
		BinaryImage map430 = processMap("LEW", currentArena);
		LevelMap levelMap = new LevelMap(map430);
		
		//BinaryImage map429 = processMap("ScreenHunter_429");
		//double time = System.currentTimeMillis();
		//levelMap.addImage(map429);

		//System.out.println("Splicing time "+ (System.currentTimeMillis() - time));
		
		//time = System.currentTimeMillis();
		//levelMap.addImage(map430);
		//levelMap.addImage(map429);
		levelMap.export("export/map.txt");
		//System.out.println("Splicing time "+ (System.currentTimeMillis() - time));
		//levelMap.move();

		/*
		int a = 1;
		ArrayList<BinaryImage> imgs = new ArrayList<BinaryImage>();
		for(File img : new File("img").listFiles())
		{
			ArrayList<PWindow> windows = PWindow.getWindows(ImageToolkit.loadImage("img/"+img.getName()), Library.getWindowIcon());
			//windows.get(0).processMap(IntBitmap.getInstance(ImageToolkit.getSubimage(ImageToolkit.loadImage("img/"+img.getName()), windows.get(0).getMAP_RECT()))).export("export/"+a+".bmp");
			a++;
		}
		int[] translation = FourierTransform.on(IntBitmap.getInstance(ImageToolkit.loadImage("export/1.bmp")).toGreyscale(),
				IntBitmap.getInstance(ImageToolkit.loadImage("export/2.bmp")).toGreyscale(), "export/spliced.bmp");
		System.out.println(translation[0] + " " + translation[1]);
		*/
		//GreyscaleImage orComb = MapOps.orCombine(imgs.get(0), imgs.get(1));
		//orComb = MapOps.expandBlack(orComb);
		//orComb.export("export/combine.bmp");
		//System.exit(0); //because of escape key
	}
	private void testItem()
	{
		/*
		BufferedImage image430 = ImageToolkit.loadImage("img/ScreenHunter_751.bmp");
		IntBitmap int430 = IntBitmap.getInstance(image430);
		ArrayList<PWindow> windows = PWindow.getWindows(image430);
		ArrayList<Item> items = windows.get(0).findItems(int430);
		new Letters(int430, items).findWords(FilterType.EQUAL_10);
		System.exit(0);
		*/
		//summed = original.add(summed, 1);
        //summed.display();
        //System.out.println();
		/*
		for(File img : new File("img").listFiles())
		{
			ArrayList<PWindow> windows = PWindow.getWindows(ImageToolkit.loadImage("img/"+img.getName()), Library.getWindowIcon());
			System.out.println("For Image "+a);
			for(Point p : windows.get(0).findItem(IntBitmap.getInstance(ImageToolkit.getSubimage(ImageToolkit.loadImage("img/"+img.getName()), windows.get(0).getITEM_SCAN_RECT())), true))
			{
				System.out.println(p);
			}
			a++;
		}
		*/
		//System.exit(0); //because of escape key
	}
	private void go()
	{
		/*
		openPoE(); //later have it minimize eclipse
		ArrayList<PWindow> windows = PWindow.getWindows();
		long time = System.currentTimeMillis();
		while(true)
		{
			for(PWindow window : windows)
			{
				//window.execute();
			}
			System.out.println("Time Elapsed "+(time-System.currentTimeMillis()));
		}*/
	}
	private void openPoE()
	{
		Macro.macro.click(100, 1150);
		Macro.macro.sleep(1000);
	}
}
