package window;

import java.awt.Point;
import java.awt.Rectangle;

public enum ScreenRegion
{
	MAP_RECT(new Rectangle(645, 4, 150, 150)),
	ITEM_SCAN_RECT(new Rectangle(0, 0, 650, 500)),
	NARROW_ITEM_RECT(new Rectangle(150, 150, 350, 200)),
	LIFE_RECT(new Rectangle(30, 480, 90, 120)),
	MANA_RECT(new Rectangle(670, 480, 150, 150)),
	LOGOUT_RECT(new Rectangle(364, 230, 65, 18)),
	LOGIN_RECT(new Rectangle(538, 388, 46, 12)),
	WORLD_RECT(new Rectangle(159, 32, 52, 12)),
	NAV_RECT(new Rectangle(70, 30, 300, 100)),
	CHAR_SELECT_RECT(new Rectangle(695, 33, 68, 9)),
	FIND_HEALTH_RECT(new Rectangle(14, 581, 12, 12)),
	INVENTORY_ICON(new Rectangle(434, 320, 6, 6)),
	INVENTORY_SCAN_RECT(new Rectangle(400, 200, 400, 280)),
	INVENTORY_OPEN_RECT(new Rectangle(480, 5, 250, 50)),
	INVENTORY_ITEM_TYPE_ID_RECT(new Rectangle(437, 70, 355, 404)),
	GREUST_DIALOGUE_BOX_RECT(new Rectangle(363, 7, 15, 10)),
	DESTROY_ITEM_RECT(new Rectangle(449, 303, 47, 10)),
	STASH_OPEN_RECT(new Rectangle(140, 30, 60, 20)),
	STORE_OPEN_RECT(new Rectangle(360, 0, 100, 20)),
	STORE_DIALOG_RECT(new Rectangle(360, 0, 100, 250)),
	SOCIAL_PANEL_RECT(new Rectangle(0, 0, 450, 460)),
	FIRST_FRIEND_RECT(new Rectangle(0, 100, 400, 400)),
	PARTY_INVITE_RECT(new Rectangle(580, 365, 220, 90)),
	HOTKEY_Q(new Rectangle(517, 566, 29, 29)),
	HOTKEY_W(new Rectangle(548, 566, 29, 29)),
	HOTKEY_E(new Rectangle(579, 566, 29, 29)),
	HOTKEY_R(new Rectangle(610, 566, 29, 29)),
	HOTKEY_T(new Rectangle(640, 566, 29, 29));
	
	private Rectangle rect;
	
	public Rectangle getRectangle() { return new Rectangle(rect.x, rect.y, rect.width, rect.height); }
	
	public int getAbsoluteX() { return rect.x; }
	public int getAbsoluteY() { return rect.y; }
	public int getWidth() { return rect.width; }
	public int getHeight() { return rect.height; }
	public Point getTopLeft() { return new Point(rect.x, rect.y); }
	public Point getCenter() { return new Point(rect.x + rect.width / 2, rect.y + rect.height / 2); }
	
	private ScreenRegion(Rectangle rect)
	{
		this.rect = rect;
	}
	
	/**
	 * Changes relative coordinates to absolute screen coordinates based on the PWindow given
	 * @param window	:		Coordinates are with respect to this window
	 * @return			:		A Rectangle corresponding to the correct ScreenRegion
	 */
	public Rectangle get(PWindow window)
	{
		return new Rectangle(rect.x + window.getX(), rect.y + window.getY(), rect.width, rect.height);
	}
}
