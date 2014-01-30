package main;

import window.PWindow;
import arena.Arena;
import arena.TheForest;

import img.BinaryImage;
import img.ImageToolkit;
import img.IntBitmap;

public class TestMinimapProcess 
{
	public static void main(String[] args)
	{
		new TestMinimapProcess().go();
	}
	private void go()
	{
		IntBitmap image = IntBitmap.getInstance(ImageToolkit.loadImage("img/Forest.bmp"));
		PWindow window = PWindow.getWindows(image).get(0);
		IntBitmap minimap = image.subimage(window.getMAP_RECT());
		Arena arena = Arena.fromString("The Forest1");
		//minimap.export("map/"+a+"Raw.bmp");
		BinaryImage processed = arena.processMap(minimap);
		System.out.println(((TheForest) arena).bestMovementAngle(processed));
		processed.toGreyscale().display(true);
	}
}
