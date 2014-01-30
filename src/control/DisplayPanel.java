package control;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class DisplayPanel extends JTabbedPane 
{
	
	private ConsolePanel consolePanel;
	private ComputersPanel byComputerPanel;
	private ProfilesPanel byProfilePanel;
	private HelpPanel helpPanel;
	private PriorityPanel priorityPanel;
	
	DisplayPanel(MustaphaMond mm)
	{
		super();
		consolePanel = new ConsolePanel(mm);
		byComputerPanel = new ComputersPanel(mm.getConfig());
		byProfilePanel = new ProfilesPanel(mm.getConfig());
		helpPanel = new HelpPanel();
		priorityPanel = new PriorityPanel(mm.getConfig());
		buildTabs();
	}
	private void buildTabs()
	{
		addTab("Console", consolePanel);
		addTab("Computers", byComputerPanel);
		addTab("Accounts", byProfilePanel);
		addTab("Help", helpPanel);
		addTab("Priorities", priorityPanel);
	}
	void updateConsole(String s)
	{
		consolePanel.updateConsole(s);
	}
	void update()
	{
		byComputerPanel.update();
		byProfilePanel.update();
	}
}
