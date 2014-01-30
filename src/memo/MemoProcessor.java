package memo;

import java.util.ArrayList;

import macro.Macro;
import party.SocialMacro;
import window.PWindow;
import arena.Arena;
import control.Network;
import data.Profile;

public class MemoProcessor 
{
	//Maybe this should be an interface?
	/**
	 * 
	 * Call process right after character selection
	 * 
	 * @param memo
	 */
	public static void process(Network network, PWindow window, Profile profile, InternalMemo memo)
	{
		System.out.println("PROCESSING MEMOS");
		ArrayList<String> params = memo.getParams();
		switch(memo.getInstruction())
		{
		case INVITE : setFriend(network, profile, params); break;
		}
	}
	public static void setFriend(Network network, Profile profile, ArrayList<String> params)
	{
		Profile friend = network.getProfile(params.get(0));
		profile.setFriend(friend);
	}
}
