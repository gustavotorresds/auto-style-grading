/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("null");
	private double x, y;

	public void run() {	

		//add mouse listeners
		addMouseListeners();

		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	//Asks the label to read the x and y location of the mouse
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		label.setLabel(x + "," + y);

		/*
		 * Checks if the mouse is over the label
		 * If it is, the label is red
		 * If not, the label is blue
		 */
		GLabel checkForLabel = getElementAt(x, y);
		if(checkForLabel != null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	}
}
