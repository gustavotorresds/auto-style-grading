/*Sofia Carrillo
 * TA: Julia Daniel 
 * File: MouseReporter.java
 * -----------------------------
 * This prints a label on the screen and when the mouse 
 * touches it, it turns red and displays the location
 * of the mouse.
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
		addMouseListeners(); 
	}
	/*
	 * This detects where the mouse is displays its x/y location
	 * and changes the color of the label when the mouse
	 * touches it. 
	 */
	public void mouseMoved(MouseEvent e) {
		GObject obj = getElementAt(e.getX(), e.getY());
		if (obj == label) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x+ "," + y);
	}

}
