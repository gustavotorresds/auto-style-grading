
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
import acm.graphics.GObject;
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
		// this setLabel method takes in a "String"
		// you can concatenate integers and commas as such:
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// add the label to the screen!
		label.setLabel(x + "," + y);

		GObject obj = getElementAt(x, y);
		if (obj != null) {
			label.setColor(Color.RED);
			add(label, INDENT, getHeight() / 2);
		} else {
			label.setColor(Color.BLUE);
			add(label, INDENT, getHeight() / 2);
		}

	}
}