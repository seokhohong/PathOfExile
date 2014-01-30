package map;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import data.Data;

public class PredefMap
{
	MapGrid grid;
	Point waypoint = null;		//each map has one waypoint. This point indicates the top left corner of the waypoint on the minimap
	public PredefMap(File f)
	{
		ArrayList<String> lines = new ArrayList<String>();
		Data.readLines(f, lines);
		parseMap(lines);
	}
	private void parseMap(ArrayList<String> rows)
	{
		for(int a = 0; a < rows.size(); a++)
		{
			for(int b = 0; b < rows.get(a).length(); b++)
			{
				State state = State.fromChar(rows.get(a).charAt(b));
				grid.addData(a, b, state);
				if(state == State.WAYPOINT && waypoint == null)
				{
					waypoint = new Point(a, b);
				}
			}
		}
	}
	public void export(String filename)
	{
		grid.export(filename);
	}
}
