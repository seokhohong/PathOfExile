package map;

import java.util.List;

/**
 * 
 * Interface allows the use of AStar
 * 
 * @author Seokho
 *
 */

public interface Pathable
{
	/**
	 * 
	 * Returns all pathable neighbors
	 * 
	 * @param p			: Neighbors should be added with respect to this point
	 */
	public List<Node> getNeighbors(Node p);
}
