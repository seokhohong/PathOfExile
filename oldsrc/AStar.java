package map;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 
 * Provides the functionality of the AStar algorithm
 * 
 * @author Seokho
 *
 */
public class AStar extends PathAlgorithm
{	
	private enum Direction { 
		EAST(1, 0), 
		WEST(-1, 0), 
		NORTH(0, -1), 
		SOUTH(0, 1);
		private int dx;
		private int dy;
		private Direction(int dx, int dy)
		{
			this.dx = dx;
			this.dy = dy;
		}
	}
	
	/**
	 * AStar algorithm
	 * Takes two maps to save the computation of having to combine a static and a volatile map that is often needed by the client
	 * (Maybe its slower this way)
	 * The path includes the start and end points unless they are equal in which it contains only one point
	 * If computations take too long (As defined by this class's G_LIMIT constant) or it exhausts the search, an empty list is returned.
	 * 
	 * @param staticMap		: map of blocked coordinates
	 * @param volatileMap	: map of blocked coordinates (for a map that changes on the fly)
	 * 							(can be null)
	 * @param s				: the starting Point
	 * @param f				: the finishing Point
	 * @return List of Points with the path from start to finish
	 */

	public static Path getPath(PathFrame pathFrame, ArrayList<ArrayList<State>> states, Rectangle boundaries)
	{
		Set<Node> closedSet = new HashSet<Node>();
		PriorityQueue<Node> openSet = new PriorityQueue<Node>();
		
		Path path = new Path();
		
		Node start = new Node(pathFrame.getDestination()); //This AStar works backwards
		Node finish = new Node(pathFrame.getOrigin());
		
		path.addPoint(finish);
		
		//Check if the finishing point is valid
		if(isInvalid(start, states, boundaries)) //can't go here
		{
			return path;
		}
		
		start.f = 0;
		start.g = manhattanDist(start, finish)*H_MULT;
		openSet.add(start);
		
		Node currNode = start;
		
		int triedNodes = 0;
		
		while(manhattanDist(currNode, finish)>1) //distance of 1 is enough
		{
			triedNodes++;
			if(triedNodes>start.g*G_LIMIT || openSet.isEmpty()) //searched too long or empty
			{
				return path;
			}
			currNode = openSet.poll();
			openSet.remove(currNode);
			closedSet.add(currNode);
			for(Direction dir : Direction.values()) //check all neighbors
			{
				Node neighbor = createNeighbor(currNode, dir);
				if(isInBounds(neighbor, boundaries))
				{
					if(isInvalid(neighbor, states, boundaries)) //coan't go here
					{
						openSet.remove(neighbor);
						closedSet.add(neighbor);
						continue;
					}
					useValidNode(currNode, neighbor, finish, openSet, closedSet);
				}
			}
		}
		path.reverseTrace(currNode);
		if(path.getLength()>1)
		{
			path.addPoint(start);
		}
		return path;
	}
	private static Node createNeighbor(Node currNode, Direction dir)
	{
		Node neighbor = new Node(currNode.x + dir.dx, currNode.y + dir.dy);
		neighbor.parent = currNode;
		return neighbor;
	}
	private static boolean isInBounds(Node node, Rectangle bounds)
	{
		return node.x > bounds.x && node.y > bounds.y && node.x < bounds.width && node.y < bounds.height;
	}
	private static boolean isInvalid(Node node, ArrayList<ArrayList<State>> states, Rectangle bounds)
	{
		return (!states.get(node.x + bounds.x).get(node.y + bounds.y).isPathable()); //can't go here
	}
	private static void useValidNode(Node currNode, Node neighbor, Point finish, PriorityQueue<Node> openSet, Set<Node> closedSet)
	{
		if(closedSet.contains(neighbor))
		{
			if(currNode.g<neighbor.g)
			{
				neighbor.g = currNode.g;
			}
		}
		else if(openSet.contains(neighbor))
		{
			if(currNode.g<neighbor.g)
			{
				neighbor.g = currNode.g;
			}
		}
		else
		{
			//g = f + h
			neighbor.f = currNode.f + 1;
			neighbor.g = neighbor.f + manhattanDist(neighbor, finish)*H_MULT;
			openSet.add(neighbor);
		}
	}
}

