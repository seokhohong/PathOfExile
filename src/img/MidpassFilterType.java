package img;

/**
 * 
 * Defines an image filter which keeps or eliminates only pixels which have RBG components within the specified ranges
 * 
 * @author Jamison
 *
 */
public enum MidpassFilterType
{
				  //RED  GREEN  BLUE
	INVENTORY_BLUE(1, 7, 1, 7, 25, 31),  //Owns inventory backgrounds REALLY hard
	INVENTORY_GREEN(1, 7, 27, 31, 1, 7),
	INVENTORY_RED(35, 50, 0, 7, 0, 7),
	INVENTORY_RED_LINE(50, 65, 18, 33, 10, 22),
	MAGIC_BOX(49, 57, 49, 57, 86, 98),
	TEAL_TEXT(23, 30, 80, 200, 80, 200),
	RARE_TEXT(195, 255, 195, 255, 90, 120),
	RARE_BOX(155, 170, 115, 120, 23, 36),
	GARBAGE_BOX(119, 130, 106, 122, 102, 115),
	GARBAGE_TEXT(150, 255, 150, 255, 150, 255),
	CURRENCY_BOX(110, 126, 95, 105, 65, 68),
	CURRENCY_TEXT(140, 170, 130, 160, 105, 130),
	BRIDGES(200, 255, 135, 180, 0, 25),
	CAVE_DARK_WATER1(17, 18, 13, 15, 21, 23),
	CAVE_DARK_WATER2(15, 18, 21, 24, 28, 31),
	CAVE_DARK_WATER3(21, 24, 29, 31, 36, 38),
	GREUST_DIALOGUE_TEXT(190, 250, 170, 220, 130, 170),
	WATER_TEST(50, 100, 90, 150, 90, 250),
	WATER_PINK(140, 255, 140, 190, 100, 170),
	STASH_OPEN(195, 220, 163, 190, 100, 130),
	WAYPOINT(0, 15, 150, 180, 215, 255),
	ON_SCREEN_PORTAL(253, 255, 253, 255, 253, 255),
	SOCIAL_GREEN(30, 180, 110, 255, 20, 70),
	ORANGE_DOOR(200, 255, 80, 100, 0, 0),
	AURA_ON(230, 255, 220, 255, 80, 165),
	CANNOT_BUY(100, 255, 0, 5, 0, 5), 		//red text on purchase menu
	
	//filters for GPS system
	GPS_STASH(0, 0, 200, 255, 0, 0),
	GPS_STORE(200, 255, 0, 0, 0, 0),
	GPS_WAYPOINT(0, 0, 200, 255, 200, 255),
	GPS_PORTALS(0, 0, 0, 0, 200, 255);
	
	int lowerR;
	int upperR;
	int lowerG;
	int upperG;
	int lowerB;
	int upperB;
	
	MidpassFilterType(int lowerR, int upperR, int lowerG, int upperG, int lowerB, int upperB)
	{
		this.lowerR = lowerR;
		this.upperR = upperR;
		this.lowerG = lowerG;
		this.upperG = upperG;
		this.lowerB = lowerB;
		this.upperB = upperB;
	}
	
}
