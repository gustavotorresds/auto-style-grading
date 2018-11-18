
/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * 
 * Cody Evans
 * TA: Jonathan Kula
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRectangle;
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
		add(label, INDENT, getHeight() / 2);

		// Add mouse listeners to check for mouse location
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent event) {
		/*
		 * finds coordinates of the mouse
		 */
		int mouseX = event.getX();
		int mouseY = event.getY();
		label.setLabel(mouseX + "," + mouseY);

		/*
		 * Test if object (i.e. label) present at mouse location
		 */
		GObject testForObject = getElementAt(event.getX(), event.getY());

		/*
		 * If object is present, set color to red. Set color to blue if object is not
		 * present.
		 */
		if (testForObject != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
