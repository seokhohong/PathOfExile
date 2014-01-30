package img;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import macro.Macro;

public class Display
{
	public static void show(IntBitmap img)
	{
		display(img, false);
	}
	public static void showHang(IntBitmap img)
	{
		display(img, true);
	}
	public static void display(IntBitmap image, boolean hang)
	{
		int[][][] data = image.getData();
		BufferedImage img = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		drawIntBitmap(g, data);
		showFrame(img);
		hang(hang);
	}
	public static void show(GreyscaleImage img)
	{
		display(img, false);
	}
	public static void showHang(GreyscaleImage img)
	{
		display(img, true);
	}
	public static void display(GreyscaleImage image, boolean hang)
	{
		int[][] data = image.getData();
		BufferedImage img = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		drawGreyscaleImage(g, data);
		showFrame(img);
		hang(hang);
	}
	public static void show(BinaryImage img)
	{
		display(img, false);
	}
	public static void showHang(BinaryImage img)
	{
		display(img, true);
	}
	public static void display(BinaryImage image, boolean hang)
	{
		boolean[][] data = image.getData();
		BufferedImage img = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		drawGreyscaleImage(g, data);
		showFrame(img);
		hang(hang);
	}
	private static void hang(boolean hang)
	{
		if(hang) Macro.sleep(999999999);
	}
	private static void showFrame(BufferedImage img)
	{
		JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private static void drawGreyscaleImage(Graphics g, int[][] data)
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = Math.max(Math.min(data[a][b], 255), 0);
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
	}
	private static void drawGreyscaleImage(Graphics g, boolean[][] data)
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = data[a][b] ? 255 : 0;
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
	}
	private static void drawIntBitmap(Graphics g, int[][][] data)
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int[] color = new int[3];
				for(int c = 0 ; c < IntBitmap.RGB; c++)
				{
					color[c] = Math.max(Math.min(data[a][b][c], 255), 0);
				}
				g.setColor(new Color(color[0], color[1], color[2]));
				g.drawLine(a, b, a, b);
			}
		}
	}
}
