#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
#SingleInstance,FORCE

WinGet, id, list,,, Program Manager
xpos := - 10
ypos := 0
Loop, %id%
{
    	this_id := id%A_Index%
    	WinGetClass, this_class, ahk_id %this_id%
    	WinGetTitle, this_title, ahk_id %this_id%
	if InStr(this_title, "Path of Exile")
	{
		WinGetPos, X, Y, Width, Height, ahk_id %this_id%
		FileAppend,%X% %Y%`n,*
		xpos := xpos + 800
		if xpos > 800
		{
			xpos := 0
			yps = ypos + 600
		}
	}
}