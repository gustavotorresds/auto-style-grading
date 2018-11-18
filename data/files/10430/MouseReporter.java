/*
 * File: MouseReporter.java
 * Name: Timothy Sah
 * Section Leader: Jordan Rosen-Kaplan
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
		label = makeLabel();
		addMouseListeners();		
	}

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();

		// Changes the label color to red if the mouse hovers over it
		if (getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		} 
		// Changes the label color back to blue if the mouse is not hovering over it
		else {
			label.setColor(Color.BLUE);
		}
		// Adds the label to the screen to display the x and y position of the mouse
		label.setLabel(mouseX + "," + mouseY);
	}

	// Creates the label
	private GLabel makeLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		return label;
	}

}
