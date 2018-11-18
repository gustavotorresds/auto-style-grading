/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	private int MouseX;
	private int MouseY;
	
	public void run() {	
		addMouseListeners();
		
		//This sets up the label for the first time.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
	}
	
	//This is the Mouse Moved method. It tracks the movement of the mouse and changes the label accordingly.
	public void mouseMoved (MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();
		label.setLabel(MouseX + "," + MouseY);
		add(label, INDENT, getHeight()/2);
		
		//This changes the color of the label depending on the location of the mouse. If the mouse is on the
		//label, it will be red, otherwise it will be blue.
		if(getElementAt(MouseX,MouseY = e.getY())==label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}