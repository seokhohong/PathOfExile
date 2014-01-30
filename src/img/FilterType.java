package img;

/**
 * 
 * Defines a certain color by ratio of components. Extremely effective at identifying objects or obstacles on the minimap
 * 
 * @author Seokho
 *
 */
public enum FilterType 
{
	EQUAL_10(0.9d, 1.1d, 0.9d, 1.1d, 10),
	EQUAL_20(0.8d, 1.2d, 0.8d, 1.2d, 10),
	CAVE_STONE(0.7d, 1.25d, 0.25d, 0.95d, 0),
	CAVE_WATER(0.70d, 0.78d, 1.0d, 1.1d, 0),
	CAVE_JUNK(0.8d, 1.1d, 1.1d, 1.5d, 0),
	CAVE_BRIDGE(1.0d, 10.0d, 3.0d, Double.POSITIVE_INFINITY, 0),
	TWILIGHT_GARBAGE(1.1d, 1.3d, 1.1d, 1.5d, 10),
	ACTUAL_CAVE_WATER1(0.8d, 0.9d, 0.8d, 0.9d, 0),
	ACTUAL_CAVE_WATER2(1.3d, 1.5d, 0.6d, 0.8d, 15),
	BROWN_WALL(1.35d, 1.6d, 1.15d, 1.4d, 0),
	STONE(0.9d, 1.1d, 1.1d, 1.2d, 0),
	LIGHT_STONE(0.95, 1.05d, 1.15d, 1.25d, 0),
	BROWN_ROCK(0.9d, 1.3d, 1.8d, 2.6d, 0),
	WAYPOINT(0.0d, 0.03d, 0.68d, 0.71d, 0),
	WATER(0.7d, 0.8d, 0.95d, 1.05d, 50),
	NEXT_REGION(2.5d, 3.1d, 80d, 100d, 0),
	SHORELINE(0.9d, 0.97d, 1.3d, 1.4d, 0),
	DARK_FOREST(0.8d, 0.9d, 1.3d, 1.50d, 0),
	WESTERN_FOREST1(0.8d, 0.9d, 1.3d, 1.50d, 0),
	GREEN_ROCK(0.7d, 0.95d, 1.5d, 2.2d, 0),
	SARN_LIGHTSTONE(1.00d, 1.09d, 1.01d, 1.05d, 0),
	SARN_DARKSTONE(0.85d, 1.00d, 1.1d, 1.2d, 0),
	SARN_STONE(1.00d, 1.09d, 1.1d, 1.2d, 0),
	SARN_LIGHTROOF(1.35d, 1.5d, 1.45d, 1.6d, 0),
	SARN_ROOFS(1.27d, 1.35d, 1.25d, 1.3d, 0),
	FOREST_SHORELINE(0.85d, 0.98d, 1.1d, 1.18d, 50),
	MANA(.25d, .4d, 0.4d, 0.7d, 0),
	LIFE(2d, 100d, 0.5d, 1.5d, 0),
	SELF(0.8d, 2.6d, 50d, 255d, 0),
	HEALTH_BAR(4.7d, 6.7d, 1.1d, 1.3d, 0),
	INVENT_BROWN_WALL(1.10d, 1.55d, 1.30d, 1.65d, 0),
	INVENT_BLUE_WALL(1.0d, 1.50d, 0.5d, 0.89d, 0),
	TRIBAL_CHEST(1.0d, 1.37d, 1.30d, 1.7, 0),
	ORANGE_TEXT(1.80d, 1.86d, 2.4d, 2.6d, 0),
	STASH(1.10d, 1.2d, 1.43d, 1.62d, 0),
	STASH_LABEL(0.95d, 1.04d, 0.83d, 0.97d, 0),
	CURRENCY_TEXT(1.06d, 1.11d, 1.20d, 1.31d, 0),
	GARBAGE_TEXT(0.95d, 1.05d, 0.95d, 1.05d, 0),
	GREUST_DIALOGUE_TEXT(1.04d, 1.08d, 1.22d, 1.32d, 0); 
	

	double rgMin;
	double rgMax;
	double gbMin;
	double gbMax;
	double minValue;
	FilterType(double rgMin, double rgMax, double gbMin, double gbMax, int minValue)
	{
		this.rgMin = rgMin;
		this.rgMax = rgMax;
		this.gbMin = gbMin;
		this.gbMax = gbMax;
		this.minValue = minValue;
	}
}
