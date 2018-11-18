/*
 * File: MouseReporter.java
 * Name: Aditya Chander
 * Section Leader: James Mayclin
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
		
		// We need to detect the mouse movement for the program to do what we want.
		addMouseListeners();
	}
	
	/*
	 * We must define a public mouse method (in this case, mouseMoved) to track the cursor.
	 * This method must do the following:
	 * 1. Get the position of the cursor and store it in variables
	 * 2. Set the label to read the current coordinates of the cursor
	 * 3. Detect the presence of an object (label) at those coordinates
	 * 4. If there is a label at the cursor, colour it red; if not, colour it blue
	 */
	
	public void mouseMoved (MouseEvent e) {
		int cursorX = e.getX();
		int cursorY = e.getY();
		label.setLabel(cursorX + "," + cursorY);
		GObject obj = getElementAt(cursorX, cursorY);
		if (obj != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}

}
