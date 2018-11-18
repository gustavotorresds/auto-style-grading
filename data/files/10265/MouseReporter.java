/* Name: Dylan Junkin
 * Section Leader: Ben Allen
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it. This program is a sandbox designed
 * to familiarize users with mouse events.
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
		addMouseListeners();
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	/* The Mouse Moved moved event tracks the users mouse and
	 * sets the label to the x, y coordinate of the users mouse.
	 * If the user is mousing over the label, the label changes color
	 * to red, however if the user is not mousing over the label,
	 * the color is blue.
	 */
	public void mouseMoved(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		label.setLabel(x + "," + y);
		if(getElementAt(x,y) != null) {
			label.setColor(Color.RED);
		}	else {
			label.setColor(Color.BLUE);
		}
		
	}


}
