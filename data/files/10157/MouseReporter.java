/*
 * File: MouseReporter.java
 * -----------------------------
 * MouseReporter creates a GLabel on the left side of the screen.
 * When the mouse is moved the GLabel displays the current x, y
 * location of the mouse.  The GLabel is normally blue, but
 * turns red when the mouse touches it.
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
		
		// now add the mouse whisperer//
		addMouseListeners();
	}
	// now add what moving the mouse does //
	public void mouseMoved(MouseEvent event) {
		
		// define the mouse position and display it in label //
		int x = event.getX();
		int y = event.getY();
		label.setLabel(x + ", " + y);
		
		/* if mouse is over label, turn label red.  if mouse is
		   not over label, turn label blue again */
		if (getElementAt(x, y) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
