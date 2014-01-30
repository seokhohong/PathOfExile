package control;

import javax.swing.JTextArea;

public class FallingTextArea extends JTextArea
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8300005793297956759L;
	public FallingTextArea()
	{
		super();
	}
	@Override
	public void append(String text)
	{
		super.append(text);
		selectAll();
	}
}
