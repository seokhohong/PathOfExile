package map;

import java.awt.Point;
import java.util.*;

public class TileMatrix 
{
	private Tile[][] matrix;						public Tile[][] getMatrix() { return matrix; }
	private HashMap<Point, Point> bridgePortals;	public HashMap<Point, Point> getBridgePortals() { return bridgePortals; }
	
	public int getWidth() { return matrix.length; }
	public int getHeight() { return matrix[0].length; }
	
	public TileMatrix(Tile[][] matrix, HashMap<Point, Point> bridgePortals)
	{
		this.matrix = matrix;
		this.bridgePortals = bridgePortals;
	}
}
