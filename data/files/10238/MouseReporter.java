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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		// first step of a mouse event.
		addMouseListeners();
	}
	
	public void mouseMoved (MouseEvent e) {
		//finds the x and y location of the mouse
		int x = e.getX();
		int y = e.getY();
		//changes the label to report the accurate location of the mouse
		label.setLabel(x + "," + y);
		//checks to see if the mouse is on the label
		GObject checkForLabel = getElementAt (x, y);
		// if it is is turns the label red, otherwise the label stays blue.
		if (checkForLabel != null) {
			label.setColor(Color.RED );
		} else {
			label.setColor(Color.BLUE);
		}
	}
}


