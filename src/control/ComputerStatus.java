package control;

public enum ComputerStatus
{
	OFF,		//Computer is off or program is not running
	IDLE,		//Computer is waiting for a bot
	BUSY,		//Program is running
	ORDERED,	//Temporary state, the computer has just been sent an order
	HALTED,		//Halted, wait to move to idle
	UNKNOWN;	//Possibly an error
}
