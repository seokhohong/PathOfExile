package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import macro.Macro;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import img.*;

public class Main
{
	static final Rectangle itemRegion = new Rectangle(10, 80, 630, 540); 
    private static String itemImage = "img/ScreenHunter_436.bmp";
	public static void main(String[] args)
	{
		//setUpHook(); //JNI... ugh
		new Main().testDelta();
		//IntBitmap one = IntBitmap.getInstance(ImageToolkit.loadImage("img/1.png"));
		//IntBitmap two = IntBitmap.getInstance(ImageToolkit.loadImage("img/2.png"));
		//ImageToolkit.splice(one, two, 10, 10).export("img/spliced.bmp");
		//System.exit(0);
	}
	private static void setUpHook()
	{
        try {
            GlobalScreen.registerNativeHook();
	    }
	    catch (NativeHookException ex) {
	            System.err.println("There was a problem registering the native hook.");
	            System.err.println(ex.getMessage());
	            System.exit(1);
	    }
        GlobalScreen.getInstance().addNativeKeyListener(new GlobalKeyListener());
	}
	private void testDelta()
	{
		IntBitmap one = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_749.bmp"));
		IntBitmap two = IntBitmap.getInstance(ImageToolkit.loadImage("img/ScreenHunter_750.bmp"));
		one.subtract(two); 
		one.blur(1);
		one.highPass(10);
		//one.export("export/delta1.bmp");
		//one.blur(1);
		//one.toGreyscale().display();
		ArrayList[] ops = new ArrayList[1];
		ops[0] = new ArrayList();
		ops[0].add("bandPassFilter");       // fn name
		ops[0].add(490.0);                   // lowCutoff: raise to blur
		ops[0].add(2000.0);                  // highCutoff: lower to sharpen
		GreyscaleImage grey = one.toGreyscale();
		grey = FourierTransform.operations(grey, ops);    // Fourier transform

		grey.horizontalBlur(10);

		for(int i = 0; i < 5; i++)
		{
			grey = grey.add(one.toGreyscale(), 0.4);
			grey.horizontalBlur(6);
			grey.blur(1);
			//grey = FourierTransform.operations(grey, ops);    // Fourier transform
		}
		grey.display();
		BinaryImage bin =  grey.doubleCutoff(30);
		bin.toGreyscale().display();
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
	private void testMap()
	{
		int a = 1;
		ArrayList<BinaryImage> imgs = new ArrayList<BinaryImage>();
		for(File img : new File("img").listFiles())
		{
			//findItem("img/"+img.getName(), "export/"+img.getName(), false);
			ArrayList<PWindow> windows = PWindow.getWindows(ImageToolkit.loadImage("img/"+img.getName()), Library.getWindowIcon());
			//BufferedImage screen = windows.get(0).takeScreenshot();
			//long time = System.nanoTime();
			windows.get(0).processMap(IntBitmap.getInstance(ImageToolkit.getSubimage(ImageToolkit.loadImage("img/"+img.getName()), windows.get(0).getMAP_RECT()))).export("export/"+a+".bmp");
			//System.out.println(windows.get(0).existsEnemyBar(Integer.toString(a)));
			//System.out.println(System.nanoTime() - time);
			a++;
		}
		int[] translation = FourierTransform.on(IntBitmap.getInstance(ImageToolkit.loadImage("export/1.bmp")).toGreyscale(),
				IntBitmap.getInstance(ImageToolkit.loadImage("export/2.bmp")).toGreyscale(), "export/spliced.bmp");
		System.out.println(translation[0] + " " + translation[1]);
		//GreyscaleImage orComb = MapOps.orCombine(imgs.get(0), imgs.get(1));
		//orComb = MapOps.expandBlack(orComb);
		//orComb.export("export/combine.bmp");
		System.exit(0); //because of escape key
	}
	private void testItem()
	{
		int a = 1;
		ArrayList<PWindow> windows = PWindow.getWindows(ImageToolkit.loadImage(itemImage), Library.getWindowIcon());		
		windows.get(0).isolateItems(IntBitmap.getInstance(ImageToolkit.loadImage(itemImage)));
		GreyscaleImage original = IntBitmap.getInstance(ImageToolkit.loadImage(itemImage)).toGreyscale();
		GreyscaleImage summed = IntBitmap.getInstance(ImageToolkit.loadImage("export/summed.bmp")).toGreyscale();
		summed = original.add(summed, 1);
        summed.display();
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
		openPoE(); //later have it minimize eclipse
		ArrayList<PWindow> windows = findWindows();
		long time = System.currentTimeMillis();
		while(true)
		{
			for(PWindow window : windows)
			{
				window.execute();
			}
			System.out.println("Time Elapsed "+(time-System.currentTimeMillis()));
		}
	}
	private void openPoE()
	{
		Macro.click(100, 1150);
		Macro.sleep(1000);
	}
	private ArrayList<PWindow> findWindows() //Don't move windows while program is running!
	{
		 return PWindow.getWindows(ImageToolkit.takeScreenshot(), Library.getWindowIcon());
	}
}
