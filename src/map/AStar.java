package map;

import geom.MathTools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStar
{
	public static ArrayList<Point> getPath(Pathable world, Point origin, Point dest)
	{
		ArrayList<Point> path = new ArrayList<Point>();
		HashSet<Point> openSet = new HashSet<Point>();
		HashSet<Point> closedSet = new HashSet<Point>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		
		Node start = new Node(origin);
		start.setH(MathTools.manhattanDist(start, dest));
		
		pq.add(start);
		openSet.add(start);
		
		Node curr = null;
		while(!pq.isEmpty())
		{
			curr = pq.poll();
			//System.out.println("Visited "+curr);
			if(curr.equals(dest))	//the world declares that curr is a satisfying destination
			{
				break;
			}
			openSet.remove(curr);
			closedSet.add(curr.toPoint());
			for(Node neighbor : world.getNeighbors(curr))
			{
				neighbor.setParent(curr);
				if(closedSet.contains(neighbor.toPoint()))
				{
					if(curr.getG() < neighbor.getG())
					{
						neighbor.setG(curr.getG());
					}
				}
				else if(openSet.contains(neighbor.toPoint()))
				{
					if(curr.getG() < neighbor.getG())
					{
						neighbor.setG(curr.getG());
					}
				}
				else
				{
					//f-score = g + h
					neighbor.setG(curr.getG() + MathTools.manhattanDist(curr, neighbor));
					neighbor.setH(MathTools.manhattanDist(curr, dest));
					openSet.add(neighbor.toPoint());
					pq.add(neighbor);
				}
			}
		}
		if(pq.isEmpty())
		{
			System.out.println("Failed to find path");
			return path;
		}
		//System.out.println("Found Path");
		fillPath(path, curr);
		return path;
	}
	//Works backwards from curr to fill the path with the correct nodes
	private static void fillPath(ArrayList<Point> path, Node curr)
	{
		Stack<Node> inverter = new Stack<Node>();
		while(curr != null)
		{
			inverter.push(curr);
			curr = curr.getParent();
		}
		while(!inverter.isEmpty())
		{
			path.add(inverter.pop());
		}
	}
}
