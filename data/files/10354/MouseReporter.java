/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the colour of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable
	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();
	}

	/* Method: mouseMoved
	 * On mouse moved, we update the coordinates shown on screen
	 */
	public void mouseMoved(MouseEvent e) {
		double mouse_x = e.getX();
		double mouse_y = e.getY();

		label.setFont("Courier-24");
		
		// If the mouse is hovering over the label area, the text is red
		if (getElementAt (mouse_x, mouse_y) == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		
		}

		label.setLabel(mouse_x + "," + mouse_y);
		add(label, INDENT, getHeight()/2);
	}
}
