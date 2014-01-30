package control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CommandLine extends JTextField implements KeyListener
{
	private ConsolePanel console;
	
	private ArrayList<String> history = new ArrayList<String>();
	private int currentIndex = 0; //within history
	
	CommandLine(ConsolePanel console)
	{
		this.console = console;
		this.addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent key) 
	{
		if(key.getKeyCode() == KeyEvent.VK_ENTER)
		{
			console.sendCommand(getText());
			history.add(getText());
			currentIndex ++ ;
			setText("");
		}
		if(key.getKeyCode() == KeyEvent.VK_UP)
		{
			if(currentIndex >= 0  && currentIndex < history.size())
			{
				setText(history.get(currentIndex));
				currentIndex -- ;
			}
		}
		if(key.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(currentIndex >= 0  && currentIndex < history.size())
			{
				setText(history.get(currentIndex));
				currentIndex ++ ;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }
	
	@Override
	public void keyTyped(KeyEvent key) 
	{

	}

}
