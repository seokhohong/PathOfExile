package main;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener
{
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) 
	{
        if (arg0.getKeyCode() == NativeKeyEvent.VK_F12) {
        	System.out.println("Terminated by Escape Key");
            System.exit(0);
        }
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
