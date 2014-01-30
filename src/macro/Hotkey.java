package macro;

import java.awt.event.KeyEvent;

import window.ScreenRegion;

public enum Hotkey 
{
	Q(ScreenRegion.HOTKEY_Q),
	W(ScreenRegion.HOTKEY_W),
	E(ScreenRegion.HOTKEY_E),
	R(ScreenRegion.HOTKEY_R),
	T(ScreenRegion.HOTKEY_T);
	
	private ScreenRegion region;		public ScreenRegion getRegion() { return region; }
	
	public static Hotkey fromKeyEvent(int keyEvent)
	{
		switch(keyEvent)
		{
		case KeyEvent.VK_Q : return Q;
		case KeyEvent.VK_W : return W;
		case KeyEvent.VK_E : return E;
		case KeyEvent.VK_R : return R;
		case KeyEvent.VK_T : return T;
		}
		return null;
	}
	
	private Hotkey(ScreenRegion region)
	{
		this.region = region;
	}
}
