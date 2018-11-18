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
		
		// Adds mouse listeners :)
		addMouseListeners();
	}
	
	// Updates display of mouse location every time it is moved.
	// Changes color of mouse location when it is touched.
	public void mouseMoved(MouseEvent e) {
		
		// Get the mouse location.
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		// Updates label to reflect current mouse location.
		label.setLabel(mouseX + ", " + mouseY);
		
		// Tests if mouse is touching a label every time it is moved.
		GObject touchedLabel = getElementAt(mouseX, mouseY);
		
		// Changes colors of label according to mouse touching it (or not).
		if(touchedLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
