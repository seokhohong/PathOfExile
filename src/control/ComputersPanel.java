package control;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Config;

/**
 * 
 * Should use JTable, except it is hard to use
 * 
 * @author Seokho
 *
 */
@SuppressWarnings("serial")
public class ComputersPanel extends JPanel
{
	private JScrollPane scrollPane;
	private JTextArea textArea = new JTextArea();
	
	private Config config;
	
	ComputersPanel(Config config)
	{
		this.config = config;
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVisible(true);
		add(scrollPane, BorderLayout.CENTER);
		textArea.setEditable(false);
		displayText();
	}
	private void displayText()
	{
		textArea.setText("");
		for(Computer c : config.getNetwork().getComputers())
		{
			textArea.append(buildComputerInfo(c).toString());
			textArea.append("\n");
		}
	}
	private static final int PADDING = 50;
	private StringBuilder buildComputerInfo(Computer c)
	{
		return new StringBuilder(padRight(c.getName(), PADDING - c.getName().length()) + c.getStatus() + c.getDescription());
	}
	public static String padRight(String s, int n) 
	{
	     return String.format("%1$-" + n + "s", s);  
	}
	void update()
	{
		displayText();
	}
}
