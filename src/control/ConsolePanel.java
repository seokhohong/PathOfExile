package control;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import message.Instruction;
import message.Message;
import data.Profile;

@SuppressWarnings("serial")
public class ConsolePanel extends JPanel 
{	
	private JScrollPane scrollPane;
	private FallingTextArea consoleText = new FallingTextArea();
	private CommandLine cmdLine = new CommandLine(this);
	
	private static final String CMD_CARET = " > ";
	private static final int HALT_PARAMS = 1;
	private static final int RUN_PARAMS = 2;
	private static final int RESUME_PARAMS = 1;
	private static final int WAITER_PARAMS = 2 ;
	
	private MustaphaMond mm;
	
	ConsolePanel(MustaphaMond mm)
	{
		this.mm = mm;
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(consoleText);
		scrollPane.setVisible(true);
		add(scrollPane, BorderLayout.CENTER);
		consoleText.setEditable(false);
		add(cmdLine, BorderLayout.SOUTH);
		println("Welcome!");
	}
	void sendCommand(String cmd)
	{
		println(CMD_CARET+cmd);
		ArrayList<String> params = getParameters(cmd);
		Instruction instr;
		if(Instruction.hasValidInstruction(cmd))
		{
			instr = Instruction.parseInstruction(cmd);
		}
		else
		{
			if(!cmd.isEmpty())
			{
				println("Invalid Command");
			}
			return;
		}
		try
		{
			switch(instr)
			{
			case HALT : sendHalt(params); break;
			case HALT_ALL : sendHaltAll(params); break;
			case RUN : sendRun(params); break;
			case RESUME : sendResume(params); break;
			case WAITER : sendWaiter(params); break;
			//Trade not built yet
			case TRADE : sendTrade(params); break;
			default : break;
			}
		}
		catch(CmdException e)
		{
			println(e.toString());
		}
	}
	private void verifyParameters(ArrayList<String> params, int numParams) throws CmdException
	{
		if(params.size() != numParams)
		{
			throw new CmdException("Incorrect Number of Parameters");
		}
	}
	private void verifyComputer(Computer comp) throws CmdException
	{
		if(comp == null) throw new CmdException("Not a valid Computer");
	}
	private void verifyProfile(Profile comp) throws CmdException
	{
		if(comp == null) throw new CmdException("Not a valid Profile");
	}
	
	private void sendTrade(ArrayList<String> params) throws CmdException
	{
		verifyParameters(params, WAITER_PARAMS);
		Profile source = mm.getNetwork().getProfile(params.get(0));
		Profile destination = mm.getNetwork().getProfile(params.get(1));
		if(source != null && destination != null)
		{
			if(source.getComputer() != null)
			{
				println(destination+" is receiving items from "+source);
				ArrayList<String> sourceParams = new ArrayList<String>();
				sourceParams.add(destination.getName());
				sourceParams.add(destination.getComputer().getName());
				sourceParams.add("source");
				ArrayList<String> destinationParams = new ArrayList<String>();
				destinationParams.add(source.getName());
				destinationParams.add(source.getComputer().getName());
				destinationParams.add("destination");
				mm.sendMessage(new Message(mm.getConfig().getComputer(), source.getComputer(), Instruction.TRADE, sourceParams));
				mm.sendMessage(new Message(mm.getConfig().getComputer(), destination.getComputer(), Instruction.TRADE, destinationParams));
			}
			else
			{
				throw new CmdException(source+" is not running currently");
			}
		}
		else
		{
			throw new CmdException("Invalid Parameters");
		}
	}
	
	private void sendWaiter(ArrayList<String> params) throws CmdException
	{
		verifyParameters(params, WAITER_PARAMS);
		Profile inviter = mm.getNetwork().getProfile(params.get(0));
		Profile waiter = mm.getNetwork().getProfile(params.get(1));
		if(inviter != null && waiter != null)
		{
			if(inviter.getComputer() != null)
			{
				println(waiter+" Is Waiting on "+inviter);
				ArrayList<String> inviterParams = new ArrayList<String>();
				inviterParams.add(waiter.getName());
				inviterParams.add(waiter.getComputer().getName());
				inviterParams.add("INVITER");
				ArrayList<String> waiterParams = new ArrayList<String>();
				waiterParams.add(inviter.getName());
				waiterParams.add(inviter.getComputer().getName());
				waiterParams.add("WAITER");
				mm.sendMessage(new Message(mm.getConfig().getComputer(), inviter.getComputer(), Instruction.WAITER, inviterParams));
				mm.sendMessage(new Message(mm.getConfig().getComputer(), waiter.getComputer(), Instruction.WAITER, waiterParams));
			}
			else
			{
				throw new CmdException(inviter+" is not running currently");
			}
		}
		else
		{
			throw new CmdException("Invalid Parameters");
		}
	}
	
	private void sendResume(ArrayList<String> params) throws CmdException
	{
		verifyParameters(params, RESUME_PARAMS);
		Computer comp = mm.getNetwork().getComputer(params.get(0));
		Profile prof = mm.getNetwork().getProfile(params.get(0));
		if(comp != null)
		{
			println("Resuming "+comp);
			comp.setStatus(ComputerStatus.IDLE);
		}
		else if(prof != null)
		{
			println("Resuming "+prof);
			prof.setStatus(ProfileStatus.IDLE);
		}
		else
		{
			throw new CmdException("Invalid Parameters");
		}
	}
	
	private void sendHaltAll(ArrayList<String> params) throws CmdException
	{
		for(Computer comp : mm.getNetwork().getComputers())
		{
			ArrayList<String> compParam = new ArrayList<String>();
			compParam.add(comp.toString());
			sendHalt(compParam);
		}
	}
	
	private void sendHalt(ArrayList<String> params) throws CmdException
	{
		verifyParameters(params, HALT_PARAMS);
		Computer comp = mm.getNetwork().getComputer(params.get(0));
		Profile prof = mm.getNetwork().getProfile(params.get(0));
		if(comp != null)
		{
			println("Halting "+comp);
			mm.sendMessage(new Message(mm.getConfig().getComputer(), comp, Instruction.HALT, params));
			//mm.sendMessage(new MessagePacket(comp, Instruction.HALT.createMessage(params)));
		}
		else if(prof != null && prof.getComputer() != null)
		{
			println("Halting "+prof);
			mm.sendMessage(new Message(mm.getConfig().getComputer(), prof.getComputer(), Instruction.HALT, params));
		}
		else
		{
			throw new CmdException("Invalid Parameters");
		}
	}
	
	private void sendRun(ArrayList<String> params) throws CmdException
	{
		verifyParameters(params, RUN_PARAMS);
		Computer comp = mm.getNetwork().getComputer(params.get(0));
		verifyComputer(comp);
		Profile prof = mm.getNetwork().getProfile(params.get(1));
		verifyProfile(prof);
		params.remove(0); //remove computer
		mm.sendMessage(new Message(mm.getConfig().getComputer(), comp, Instruction.RUN, params));
	}
	
	void updateConsole(String s)
	{
		println(" + "+s);
	}
	
	void updateComputer(Computer c)
	{
		println(" + "+c.getName()+" \tis "+c.getStatus());
	}
	
	//Returns all the parameters in the command
	private ArrayList<String> getParameters(String cmd)
	{
		String[] split = cmd.split(Message.delim());
		ArrayList<String> params = new ArrayList<String>();
		for(int a = 1 ; a < split.length ; a++)
		{
			params.add(split[a]);
		}
		return params;
	}
	void print(String s)
	{
		consoleText.append(s);
		consoleText.selectAll();
		int last = consoleText.getSelectionEnd();
		consoleText.select(last, last);
	}
	void println(String s)
	{
		print(s+"\n");
	}
}
