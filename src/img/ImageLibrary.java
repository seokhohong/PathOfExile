package img;

/**
 * 
 * Static library that loads IntBitmap's generally sought on the screen at some point in the program
 * 
 * Not the cleanest design pattern: perhaps static instances within the IntBitmap class? Code is more verbose that way, with repetitive elements
 * 
 * @author Seokho
 *
 */
public enum ImageLibrary
{
	//screen play and login stuff up here
	ACT1_HIGHLIGHT("imglib/Act1Highlighted.bmp"),
	ACT2_HIGHLIGHT("imglib/Act2Highlighted.bmp"),
	ACT3_HIGHLIGHT("imglib/Act3Highlighted.bmp"),
	ACT1_UNHIGHLIGHT("imglib/Act1Unhighlighted.bmp"),
	ACT2_UNHIGHLIGHT("imglib/Act2Unhighlighted.bmp"),
	ACT3_UNHIGHLIGHT("imglib/Act3Unhighlighted.bmp"),
	NORMAL_HIGHLIGHT("imglib/NormalHighlighted.bmp"),
	CRUEL_HIGHLIGHT("imglib/CruelHighlighted.bmp"),
	MERCILESS_HIGHLIGHT("imglib/MercilessHighlighted.bmp"),
	NORMAL_UNHIGHLIGHT("imglib/NormalUnhighlighted.bmp"),
	CRUEL_UNHIGHLIGHT("imglib/CruelUnhighlighted.bmp"),
	MERCILESS_UNHIGHLIGHT("imglib/MercilessUnhighlighted.bmp"),
	CHARACTER_SELECT("imglib/CharSelect.bmp"),
	LOGIN_BUTTON("imglib/Login.bmp"),
	LOGOUT_BUTTON("imglib/Logout.bmp"),
	WORLD_LABEL("imglib/World.bmp"),
	NEW_INSTANCE("imglib/newInstance.bmp"),
	LOGIN_OK("imglib/LoginOk.bmp"),
	LOGIN_ERROR("imglib/LoginError.bmp"),
	RESURRECT("imglib/Resurrect.bmp"),
	LEVEL_UP_GEM("imglib/LevelUpGem.bmp"),
	INVENTORY("imglib/InventoryCorner.bmp"),
	STASH_CORNER("imglib/StashCorner.bmp"),
	STASH_1_H("imglib/Stash1Highlighted.bmp"),
	STASH_1_UH("imglib/Stash1Unhighlighted.bmp"),
	STASH_2_H("imglib/Stash2Highlighted.bmp"),
	STASH_2_UH("imglib/Stash2Unhighlighted.bmp"),
	STASH_3_H("imglib/Stash3Highlighted.bmp"),
	STASH_3_UH("imglib/Stash3Unhighlighted.bmp"),
	STASH_4_H("imglib/Stash4Highlighted.bmp"),
	STASH_4_UH("imglib/Stash4Unhighlighted.bmp"),
	GREUST_DIALOGUE_BOX("imglib/GreustStoreOpen.bmp"),
	DESTROY_ITEM("imglib/DestroyItem.bmp"),
	FRIEND_OPTION_BUTTON("imglib/FriendOptionButton.bmp"),
	UNFRIEND_OFFLINE("imglib/UnfriendOffline.bmp"),
	UNFRIEND_ONLINE("imglib/UnfriendOnline.bmp"),
	REMOVE_FRIEND_OK("imglib/RemoveFriendOk.bmp"),
	INVITE_TO_PARTY("imglib/InviteToParty.bmp"),
	ACCEPT_PARTY("imglib/AcceptParty.bmp"),
	STORE_OPEN("imglib/StoreOpen.bmp"),
	STASH_OPEN("imglib/StashOpen.bmp");
	
	private IntBitmap image;
	private ImageLibrary(String filename)
	{
		image = IntBitmap.getInstance(ImageToolkit.loadImage(filename));
	}
	public IntBitmap get()
	{
		return image;
	}
	
	public static IntBitmap levelHighlight(int level)
	{
		switch(level)
		{
		case 1: return NORMAL_HIGHLIGHT.get();
		case 2: return CRUEL_HIGHLIGHT.get();
		case 3: return MERCILESS_HIGHLIGHT.get();
		}
		return null;
	}
	public static IntBitmap levelUnhighlight(int level)
	{
		switch(level)
		{
		case 1: return NORMAL_UNHIGHLIGHT.get();
		case 2: return CRUEL_UNHIGHLIGHT.get();
		case 3: return MERCILESS_UNHIGHLIGHT.get();
		}
		return null;
	}
	public static IntBitmap actHighlight(int act)
	{
		switch(act)
		{
		case 1: return ACT1_HIGHLIGHT.get();
		case 2: return ACT2_HIGHLIGHT.get();
		case 3: return ACT3_HIGHLIGHT.get();
		}
		return null;
	}
	public static IntBitmap actUnhighlight(int act)
	{
		switch(act)
		{
		case 1: return ACT1_UNHIGHLIGHT.get();
		case 2: return ACT2_UNHIGHLIGHT.get();
		case 3: return ACT3_UNHIGHLIGHT.get();
		}
		return null;
	}
	public static IntBitmap stashHighlight(int tab)
	{
		switch(tab)
		{
		case 1: return STASH_1_H.get();
		case 2: return STASH_2_H.get();
		case 3: return STASH_3_H.get();
		case 4: return STASH_4_H.get();
		}
		System.out.println("Null because input int didn't match a tab number. You should probably check your code.");
		return null;
	}
	public static IntBitmap stashUnhighlight(int tab)
	{
		switch(tab)
		{
		case 1: return STASH_1_UH.get();
		case 2: return STASH_2_UH.get();
		case 3: return STASH_3_UH.get();
		case 4: return STASH_4_UH.get();
		}
		System.out.println("Null because input int didn't match a tab number. You should probably check your code.");
		return null;
	}
}
