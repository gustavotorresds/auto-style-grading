
/*
 * File: MouseReporter.java
 * Name: Helen Lin
 * Section Leader: Semir Shafi
 * ----------------------
 * This program tracks the mouse's location
 * The location of the mouse is displayed as a label on the left hand side of the screen as x,y coordinates
 * The label is default red and should turn blue if the mouse is touching it.
 * 
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

	// Create and instantiate mouse locations x and y
	private int x = 0;
	private int y = 0;

	private double labelWidthX = 0;
	private double labelHeightY = 0;

	public void run() {
		addMouseListeners();

		// this code already adds the label to the screen!
		// default color to RED
		label.setFont("Courier-24");
		label.setColor(Color.RED);

		// add the label to the screen!
		add(label, INDENT, getHeight() / 2);
		
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		// this setLabel method takes in a "String"
		// you can concatenate integers and commas as such:
		label.setLabel(x + "," + y);
		labelWidthX = INDENT + label.getWidth();
		labelHeightY = getHeight() / 2 - label.getHeight();
		
		if (x < labelWidthX && x > 20 && y > labelHeightY && y < getHeight() / 2) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
		
	}

}
