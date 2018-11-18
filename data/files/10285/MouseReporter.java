/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Lizbeth Gomez
 * Section Leader: Garrick Fernandez
 * Date: February 2, 2018
 * 
 * Outputs the location of the mouse to a label on the
 * screen. It also changes the color of the label to red 
 * when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// creates label
	private GLabel label = new GLabel("");

	// initialize my coordinates (x, y) 
	double mouseCoorX; 
	double mouseCoorY; 

	public void run() {	
		// this code adds the label to the screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		while (true) {

			// when mouse touches label, it turns red
			if (getElementAt(mouseCoorX, mouseCoorY) != null) {
				label.setColor(Color.RED);
			} else {
				label.setColor(Color.BLUE);
			}

			// check mouse coordinates
			addMouseListeners();

			// setLabel method takes in a "String" 
			label.setLabel(mouseCoorX + "," + mouseCoorY);

			// add the label to the screen
			add(label, INDENT, getHeight()/2);
		}

	}

	/*
	 * Will take the x and y coordinates and store 
	 * them every time the mouse moves.
	 */
	public void mouseMoved( MouseEvent e) {
		mouseCoorX = e.getX();
		mouseCoorY = e.getY();
	}


}
