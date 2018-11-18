/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Elyssa Hofgard
 * Section Leader: Shanon Reckinger
 * 
 * Outputs the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for the label.
	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();

		// Sets the font and color of the label.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// Sets the original label to (0,0).
		label.setLabel(0 + "," + 0);

		// Adds the label to the screen.
		add(label, INDENT, getHeight()/2);

	}

	/*
	 * Sets the label to either blue or red depending on 
	 * where the mouse is.
	 */
	public void mouseMoved(MouseEvent e) {
		// Gets the x and y position of the mouse.
		double x = e.getX();
		double y = e.getY();

		// Sets the label display to the position of the mouse.
		label.setLabel(x + "," + y);
		Object obj = getElementAt( x, y);

		// If the mouse is on the label, the label will turn red.
		if (obj != null) {
			label.setColor(Color.RED);

		// Otherwise, the label will remain blue.
		} else {
			label.setColor(Color.BLUE);
		}
	}
}



