/*
 * File: MouseReporter.java
 * Student: Aaron Wingad
 * Section Leader: Julia Daniels
 * -----------------------------
 * This program outputs a label on the left middle of the screen.
 * Once the mouse is on the canvas, the label updates with the mouse's current x, y coordinates.
 * The label turns from blue to red when the mouse hovers on top of the label.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// instance variable "label" is used so that all methods can access the object
	private GLabel label = new GLabel("");

	public void run() {	
		// the following section adds the initial label with value "(0,0)" to the screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);

		// this activates the mouse event listening
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		// this section updates the label to the current location of the mouse
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);

		// this section changes the color of the label from blue to red
		// when the mouse touches the label
		GObject obj = getElementAt(e.getX(), e.getY());
		if(obj == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}	
}
