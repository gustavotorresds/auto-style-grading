/*
 * File: MouseReporter.java
 * The goal of this program is to output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it. The label is controlled by the movement of the mouse.
 * When the mouse is touching the label, the label changes color to red, else the label
 * stays blue.
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
	GLabel label = null;
	public void run() {	
		label = new GLabel("");
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		// allows program to respond to mouse
		addMouseListeners();

	}
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		if (getElementAt(x,y) == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}


