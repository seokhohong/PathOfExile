#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
#SingleInstance,FORCE

WinGet, id, list,,, Program Manager
firstX := -100
foundSecond := 0
firstId := none

; The worst program I have coded. Ever.

Loop, %id%
{
    	this_id := id%A_Index%
    	WinGetClass, this_class, ahk_id %this_id%
    	WinGetTitle, this_title, ahk_id %this_id%
	if InStr(this_title, "Path of Exile")
	{
		WinGetPos, x, y, width, height, ahk_id %this_id%,
		if firstX = -100
		{
			firstX := x
			firstId = %this_id%
		}
		else
		{

			foundSecond := 1
			WinActivate, ahk_id %firstId%
			WinActivate, ahk_id %this_id%
			sleep, 500
			if(firstX < x)
			{
				WinMove, ahk_id %firstId%,, -10, 0
				WinMove, ahk_id %this_id%,, 800, 0	
			}
			else
			{
				WinMove, ahk_id %firstId%,, 800, 0
				WinMove, ahk_id %this_id%,, -10, 0
			}
		}
	}
}
if(foundSecond = 0)
{
	if(firstX < 400)
	{
		WinMove, ahk_id %firstId%,, -10, 0
	}
	else
	{
		WinMove, ahk_id %firstId%,, 800, 10
	}
	WinActivate, ahk_id %firstId%
}