#include "NativeMacro.h"
#include <iostream>
#include <windows.h>



#define BACKSPACE 0x08

#define TAB 0x09

#define CLEAR 0x0C

#define ENTER 0x0D

#define SHIFT 0x10

#define CTRL 0x11

#define MENU 0x12

#define PAUSE 0x13

#define CAPS 0x14

#define ESC 0x1B

#define SPACE 0x20

#define ZERO 0x30

#define ONE 0x31

#define TWO 0x32

#define THREE 0x33

#define FOUR 0x34

#define FIVE 0x35

#define SIX 0x36

#define SEVEN 0x37

#define EIGHT 0x38

#define NINE 0x39

#define A 0x41

#define B 0x42

#define C 0x43

#define D 0x44

#define E 0x45

#define F 0x46

#define G 0x47

#define H 0x48

#define I 0x49

#define J 0x4A

#define K 0x4B

#define L 0x4C

#define M 0x4D

#define N 0x4E

#define O 0x4F

#define P 0x50

#define Q 0x51

#define R 0x52

#define S 0x53

#define T 0x54

#define U 0x55

#define V 0x56

#define W 0x57

#define X 0x58

#define Y 0x59

#define Z 0x5A

#define DOT 0xBE

static HWND windowFromPoint(int x, int y)
{
	POINT p;
	p.x = x;
	p.y = y;
	HWND window = WindowFromPoint(p);
	char title[128];
	GetWindowText(window, title, 128);
	std::cout << title << std::endl;
	return window;
}
static void pressKey(unsigned int key)
{
	keybd_event(key, 0, 0, 0);
	keybd_event(key, 0, KEYEVENTF_KEYUP, 0);
}
static void pressShifted(unsigned int key)
{
	keybd_event(SHIFT, 0, 0, 0);
	pressKey(key);
	keybd_event(SHIFT, 0, KEYEVENTF_KEYUP, 0);
}
static void postKeyMessage(int x, int y, unsigned int msg)
{
	SetForegroundWindow(windowFromPoint(x, y));
	switch(msg)
	{
	case 'a' : pressKey(A); break;
	case 'b' : pressKey(B); break;
	case 'A' : pressShifted(A);break;
	case '1' : pressKey(ONE); break;
	case '2' : pressKey(TWO); break;
	case '3' : pressKey(THREE); break;
	case '4' : pressKey(FOUR); break;
	case '5' : pressKey(FIVE); break;
	case '6' : pressKey(SIX); break;
	case '7' : pressKey(SEVEN); break;
	case '8' : pressKey(EIGHT); break;
	case '9' : pressKey(NINE); break;
	case '0' : pressKey(ZERO); break;
	case '\t' : pressKey(TAB); break;
	case '\n' : pressKey(ENTER); break;
	}
	
}
static void postMessage(int x, int y, unsigned int msg)
{
	HWND hwnd = windowFromPoint(x, y);
	if(hwnd!=NULL)
	{
		PostMessage(hwnd, msg, 0, (y << 16 | x));
	}
	else
	{
		std::cout << "No Window Error (In Native Code)" << std::endl;
	}
}

/*
 * Class:     NativeMacro
 * Method:    leftClick
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_NativeMacro_leftClick
  (JNIEnv * env, jobject jobj, jint x, jint y)
{
	postMessage(x, y, WM_LBUTTONDOWN);
	postMessage(x, y, WM_LBUTTONUP);
}

/*
 * Class:     NativeMacro
 * Method:    leftDoubleClick
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_NativeMacro_leftDoubleClick
  (JNIEnv * env, jobject jobj, jint x, jint y)
{
	postMessage(x, y, WM_LBUTTONDBLCLK);
}

/*
 * Class:     NativeMacro
 * Method:    rightClick
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_NativeMacro_rightClick
  (JNIEnv * env, jobject jobj, jint x, jint y)
{
	postMessage(x, y, WM_RBUTTONDOWN);
	postMessage(x, y, WM_RBUTTONUP);
}

/*
 * Class:     NativeMacro
 * Method:    middleClick
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_NativeMacro_middleClick
  (JNIEnv * env, jobject jobj, jint x, jint y)
{
	postMessage(x, y, WM_MBUTTONDOWN);
	postMessage(x, y, WM_MBUTTONUP);
}

/*
 * Class:     NativeMacro
 * Method:    typeChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_NativeMacro_typeChar
  (JNIEnv * env, jobject jobj, jchar c, jint x, jint y)
{
	postKeyMessage(x, y, c);
}

/*
 * Class:     NativeMacro
 * Method:    typeEscape
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_NativeMacro_typeEscape
  (JNIEnv * env, jobject jobj, jint x, jint y)
{
	postKeyMessage(x, y, ESC);
}

/*
 * Class:     NativeMacro
 * Method:    scrollDown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_NativeMacro_scrollDown
  (JNIEnv * env, jobject jobj, jint x, jint y, jint lines)
{
	HWND hwnd = windowFromPoint(x, y);
	if(hwnd!=NULL)
	{
		for(int a = 0; a < lines; a++)
		{
			PostMessage(hwnd, WM_VSCROLL, SB_LINEDOWN, 0);
		}
	}
	else
	{
		std::cout << "No Window Error (In Native Code)" << std::endl;
	}	
}