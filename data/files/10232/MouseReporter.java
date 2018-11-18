/*
 * File: MouseReporter.java
 * -----------------------------
 * This program makes a label in the specified location on the screen,
 * that changes to print the location of the mouse at all times.
 * It also will turn red when the mouse is touching any part of the label. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for the label 
	private GLabel label = new GLabel("");

	
	public void run() {	

		// makes a label in Courier font in blue
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		//sets location of the label
		label.setLocation(INDENT, getHeight()/2);

		//sets initial coordinates of label
		label.setLabel(INDENT + "," + getHeight()/2);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		//adds Mouse Event
		addMouseListeners();

	}

	public void mouseMoved (MouseEvent e) {
		//makes a mouse event so the label tracks the mouse location
		int x = e.getX();
		int y = e.getY();

		// this label will read the current location of the mouse
		label.setLabel(x+","+y);
		
		//turns color to read if mouse touches label by creating another object
		// that exists where ever the mouse is, and so if that object equals the 
		// initial label then the coordinates turn red 
		GLabel obj = getElementAt(x,y);
		if (obj==label) {
			label.setColor(Color.RED);
		} else 
			label.setColor(Color.BLUE);
	}

}
