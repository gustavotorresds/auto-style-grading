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
		//make the program respond to the movement of the mouse
		addMouseListeners();

		//create label 
		int x = INDENT;
		int y = getHeight()/2;

		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(x + "," + y);
		add(label, x, y);
	}

	// the mouse moved method - get the mouse location
	public void mouseMoved (MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);

		// change the color of the coordinates when mouse hovers over the label
		if (x >= INDENT && x <= (INDENT + label.getWidth()) && y >= label.getY() && y <= (label.getY() + label.getAscent())) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}