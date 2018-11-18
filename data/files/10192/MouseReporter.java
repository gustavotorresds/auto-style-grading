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
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");

	public void run() {	
		addLabel();
		addMouseListeners();
	}
 
	private void addLabel() {
		label.setFont("Courier-24");
		add(label, INDENT, getHeight() / 2); // adds the label to the screen
	}
	
	public void mouseMoved(MouseEvent e) { 
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);

		if(getElementAt(x, y) == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}