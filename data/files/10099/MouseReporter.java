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

	public void run() {	
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		e.getX();
		e.getY();
		
		// Retrieving the x and y coordinates of the mouse
		double x = e.getX();
		double y = e.getY();
		
		// Label will indicate the x and y coordinates of the mouse 
		GLabel object = getElementAt(x, y);
		
		label.setLabel(x + "," + y);
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		
		if (object != null) {
			label.setColor(Color.RED); // Label turns red if mouse touches object
		} else {
			label.setColor(Color.BLUE); // Otherwise it stays blue
		}
	}
}
