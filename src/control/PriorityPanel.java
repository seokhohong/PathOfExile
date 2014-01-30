package control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.Config;
import data.Profile;

@SuppressWarnings("serial")
public class PriorityPanel extends JPanel
{
	private JScrollPane scrollPane;
	
	private JList<Profile> list;
	private DefaultListModel<Profile> listModel;
	
	private JPanel modifyPanel = new JPanel(); //for the shift up and down buttons
	private JButton upButton = new JButton("+");
	private JButton downButton = new JButton("-");
	
	private Config config;
	
	PriorityPanel(Config config)
	{
		this.config = config;
		defineList(); //defines listModel
		list = new JList<Profile>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.addListSelectionListener(new SelectionListener());
		list.setVisibleRowCount(Integer.MAX_VALUE);
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(list);
		scrollPane.setVisible(true);
		add(scrollPane, BorderLayout.CENTER);
		
		upButton.addActionListener(new UpButtonListener());
		downButton.addActionListener(new DownButtonListener());
		modifyPanel.add(upButton);
		modifyPanel.add(downButton);
		add(modifyPanel, BorderLayout.WEST);
	}
	private void defineList()
	{
		listModel = new DefaultListModel<Profile>();
		for(int a = 0; a < config.getNetwork().getProfiles().size(); a++)
		{
			listModel.add(a, config.getNetwork().getProfiles().get(a));
		}
	}
	void update()
	{
		config.getNetwork().sortProfiles();
		defineList();
		Profile.savePriorities(config.getNetwork());
	}
	class SelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent arg0) 
		{
			if(list.getSelectedIndex() == 0)
			{
				upButton.setEnabled(false);
				downButton.setEnabled(true);
			}
			else if(list.getSelectedIndex() == listModel.size() - 1)
			{
				downButton.setEnabled(false);
				upButton.setEnabled(true);
			}
			else
			{
				upButton.setEnabled(true);
				downButton.setEnabled(true);
			}
		}
	}
	class UpButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(list.getSelectedIndex() != 0) //not the top
			{
				//Switch the two elements
				Profile selected = list.getSelectedValue();
				Profile above = listModel.elementAt(list.getSelectedIndex() - 1);
				selected.setPriority(selected.getPriority() - 1);
				above.setPriority(above.getPriority() + 1);
				listModel.setElementAt(above, list.getSelectedIndex());
				listModel.setElementAt(selected, list.getSelectedIndex() - 1);
				list.setSelectedIndex(list.getSelectedIndex() - 1);
				Profile.savePriorities(config.getNetwork());
			}
		}
	}
	class DownButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(list.getSelectedIndex() != listModel.size() - 1) //not the bottom
			{
				//Switch the two elements
				Profile selected = list.getSelectedValue();
				Profile below = listModel.elementAt(list.getSelectedIndex() + 1);
				selected.setPriority(selected.getPriority() - 1);
				below.setPriority(below.getPriority() + 1);
				listModel.setElementAt(below, list.getSelectedIndex());
				listModel.setElementAt(selected, list.getSelectedIndex() + 1);
				list.setSelectedIndex(list.getSelectedIndex() + 1);
				Profile.savePriorities(config.getNetwork());
			}
		}
	}
}
