package map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Path
{
	private List<Point> path = new ArrayList<Point>(); //should include destination
	public Path()
	{
		
	}
	List<Point> getPath()
	{
		return path;
	}
	public int getLength()
	{
		return path.size();
	}
	public Point getFirst()
	{
		return path.get(0);
	}
	public void removeFirst()
	{
		path.remove(0);
	}
	//public Offset getOffset(double progress)
	{
		//return new Offset((path.get(1).x - path.get(0).x) * progress, (path.get(1).y - path.get(0).y) * progress);
	}
	void addPoint(Point p)
	{
		path.add(p);
	}
	/**
	 * Adds the path stored from currNode->parent->parent etc. in reverse order into path
	 * currNode will be the last node in path after this method
	 */
	/*
	void reversePath(Node3D currNode)
	{
		Stack<Node3D> reverseNodes = new Stack<Node3D>();
		while(currNode.parent!=null)
		{
			reverseNodes.push(currNode);
			currNode = currNode.parent;
		}
		while(!reverseNodes.isEmpty())
		{
			path.add(reverseNodes.pop());
		}
	}
	*/
	void reverseTrace(Node currNode)
	{
		while(currNode.parent!=null)
		{
			path.add(currNode);
			currNode = currNode.parent;
		}
	}
	void cutPath(int window)
	{
		if(path.size()>window)
		{
			path.subList(window, path.size()).clear(); //clear out the A* portion
		}
	}
	@Override
	public String toString()
	{
		return path.toString();
	}
}
