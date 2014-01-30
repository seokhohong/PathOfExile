package memo;

import java.util.ArrayList;


/**
 * 
 * Passed around within a particular program, not across the network
 * 
 * @author Seokho
 *
 */
public class InternalMemo 
{
	private ArrayList<String> params;		public ArrayList<String> getParams() { return params; }
	private MemoInstruction instruction; 	public MemoInstruction getInstruction() { return instruction; }
	
	public InternalMemo(MemoInstruction mi, ArrayList<String> params)
	{
		this.instruction = mi;
		this.params = params;
	}
}
