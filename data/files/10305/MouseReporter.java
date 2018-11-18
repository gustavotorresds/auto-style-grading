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
	
	// These variables are visible to the entire program
	// They are called "instance" variables
	private GLabel label = new GLabel("");
	private int x = 0;
	private int y = 0;
	
	
	public void run() {	
		//Add Mouse Listeners
	    addMouseListeners();
	    
	    // this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(x + "," + y);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		//Change label to reflect mouse coordinates
		x = e.getX();
		y = e.getY();
		label.setLabel(x + "," + y);
		
		// Change Label Color to Red if Mouse Touches Label, Otherwise Blue
		// may be a GLabel, or null if nothing at (x, y)
		GLabel maybeAnObject = getElementAt(x, y);
		if (maybeAnObject != null) {
			// do something with maybeAnObject
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}

	}


	


}
