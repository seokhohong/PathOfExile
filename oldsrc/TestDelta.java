package main;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import macro.Macro;

import fourier.FourierTransform;

import img.*;

public class TestDelta {
	private String firstImgFile = "export/WhileMoving1.bmp";
	private String secondImgFile = "export/WhileMoving2.bmp";
	public static void main(String[] args)
	{
		//setUpHook(); //JNI... ugh
		new TestDelta().FourierDelta();
	}
	
	private void FourierDelta() 
	{
		IntBitmap first = IntBitmap.getInstance(ImageToolkit.loadImage(firstImgFile));
		IntBitmap second = IntBitmap.getInstance(ImageToolkit.loadImage(secondImgFile));
		first.subtractRed(255);
		first.subtractBlue(255);
		second.subtractRed(255);
		second.subtractBlue(255);
		first.toGreyscale().display();
		second.toGreyscale().display();
		int[] offset = FourierTransform.on(first.toGreyscale(), second.toGreyscale(), 10000);
		System.out.println("Offset: (" + offset[0] + ", " + offset[1] + ")");
		ImageToolkit.splice(first, second, offset[0], offset[1]).toGreyscale().display();
		
	}
}
