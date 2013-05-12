package img;

//Not sure if it belongs here
public class Neighbors 
{
	static int numNeighbors(boolean[][] data, int x, int y)
	{
		int numNeighbors = 0;
		if(x > 0 && y > 0 && x < data.length - 1 && y < data[0].length - 1)
		{
			if(data[x][y] == data[x-1][y-1]) numNeighbors ++ ;
			if(data[x][y] == data[x][y-1]) numNeighbors ++ ;
			if(data[x][y] == data[x+1][y-1]) numNeighbors ++ ;		
			if(data[x][y] == data[x-1][y]) numNeighbors ++ ;
			if(data[x][y] == data[x+1][y]) numNeighbors ++ ;
			if(data[x][y] == data[x-1][y+1]) numNeighbors ++ ;
			if(data[x][y] == data[x][y+1]) numNeighbors ++ ;
			if(data[x][y] == data[x+1][y+1]) numNeighbors ++ ;
		}
		return numNeighbors;
	}
}
