/*
 * File: MouseReporter.java
 * -----------------------------
 * This file outputs the location of the mouse to a label on the screen in 
 * blue and changes the label color to red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;

	// Instance variable
	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24");
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		double MouseX = e.getX();
		double  MouseY = e.getY();
		// getElementAt stores the coordinates the mouse is at
		GObject onLabel = getElementAt(MouseX, MouseY);
		// Print label in red when mouse location is NOT null, therefore it IS on the label
		if (onLabel != null) {
			label.setLabel(MouseX+ "," + MouseY);
			label.setColor(Color.RED);
		// Print label in blue when mouse location is null
		} else {
			label.setLabel(MouseX+ "," + MouseY);
			label.setColor(Color.BLUE);
		}
	}
}

