package control;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Config;
import data.Profile;

@SuppressWarnings("serial")
public class ProfilesPanel extends JPanel
{
	private JScrollPane scrollPane;
	private JTextArea textArea = new JTextArea();
	
	private Config config;
	
	ProfilesPanel(Config config)
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
		for(Profile p : config.getNetwork().getProfiles())
		{
			textArea.append(buildProfileInfo(p).toString());
			textArea.append("\n");
		}
	}
	private static final int PADDING = 50;
	private StringBuilder buildProfileInfo(Profile c)
	{
		return new StringBuilder(padRight(c.getName(), PADDING - c.getName().length()) + c.getStatus());
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
