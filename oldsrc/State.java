package map;

public enum State 
{ 
	//Priorities are somewhat arbitrary
	//Second parameter indicates whether the location is pathable
	HIDDEN(' ', false, 0), 
	BLOCKED('X', false, 4),
	WAYPOINT('W', false, 4), 		//unpathable for now, just so we don't click on it
	VISITED_PATH(',', true, 3),	 	//no need to differentiate between visited and unvisited for exporting
	UNVISITED_PATH('.', true, 1);
	
	private char symbol; 		//for exporting
	private boolean pathable;
	private int priority;		//States can be written over on the map by states with higher priorities
	State(char symbol, boolean pathable, int priority)
	{
		this.symbol = symbol;
		this.pathable = pathable;
		this.priority = priority;
	}
	char getSymbol() { return symbol; }
	boolean isPathable() { return pathable; }
	static State fromChar(char c) 
	{
		for(State s : values())
		{
			if(s.symbol == c)
			{
				return s;
			}
		}
		System.err.println("Could not cast "+c+" to a State");
		System.exit(1);
		return null;
	}
	boolean hasPriority(State otherState)
	{
		return priority > otherState.priority;
	}
}
