/*
 * File: MouseReporter.java
 * -----------------------------
 * S106ATiles.java
 * Name: Seth Liyanage
 * Date: 02/06/18
 * Section Leader: Marilyn Zhang 
 * Sources: Style Guide, Assignment #3 Hand out, Stanford java lib
 * -------------------------------------------------
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
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 * Allows the mouse to effect the value and color of the label
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x + "," + y);
		if(getElementAt(x,y) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		
	}
	


}
