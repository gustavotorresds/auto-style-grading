/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Léa Koob
 * Section Leader: Ruiqi Chen
 * 
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
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		// this adds the mouse listeners so that the coordinate changes when 
		// the mouse moves. Essentially implements the whole program. 
		addMouseListeners();

	}


	/*
	 * Method: mouseMoved
	 * 
	 * When the mouse is moved, this gets the x, and y coordinates for the mouseReporter
	 * method, and sets a label that contains those coordinates.
	 */

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		mouseReporter(x, y);
		label.setLabel(x + "," + y);
	}


	/*
	 * Method: mouseReporter
	 * 
	 * When the mouse is moved, method takes in the new coordinates to set for the label
	 * and makes the coordinate show up in blue if the mouse is not touching the coordinate,
	 * and in red if the mouse is touching the coordinate. 
	 */

	public void mouseReporter(int x, int y) {
		if (x >= INDENT && x <= INDENT + label.getWidth() && y >= label.getY() - label.getAscent() && y <= label.getY()) {
			label.setColor(Color.red);
		} else {
			label.setColor(Color.blue);
		}
	}
}
