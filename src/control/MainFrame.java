package control;

import javax.swing.JFrame;

public class MainFrame 
{
	
	private DisplayPanel displayPanel;
	
	private JFrame frame;
	MainFrame(MustaphaMond mm)
	{
		buildWindow(mm);
	}
	private void buildWindow(MustaphaMond mm)
	{
		frame = new JFrame();
		frame.setTitle("Controller");
		displayPanel = new DisplayPanel(mm);
		frame.getContentPane().add(displayPanel);
		frame.setLocation(200, 200);
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	void updateConsole(String s)
	{
		displayPanel.updateConsole(s);
	}
	void update()
	{
		displayPanel.update();
	}
}
