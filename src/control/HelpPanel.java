package control;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class HelpPanel extends JPanel
{
	private JScrollPane scrollPane;
	private JTextArea consoleText = new JTextArea();
	
	HelpPanel()
	{
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(consoleText);
		scrollPane.setVisible(true);
		add(scrollPane, BorderLayout.CENTER);
		consoleText.setEditable(false);
		consoleText.setText(helpText);
	}
	
	private String helpText = 
			"Mustapha Mond v0.1\n\n\n"
			+ "Commands:\n"
			+ "Commands are issued in the Console, with components separated by spaces.\n"
			+ "Example: halt Seokho-HP\n\n"
			+ "halt (computer name)\n"
			+ "run (computer name) (account name)\n";
}
