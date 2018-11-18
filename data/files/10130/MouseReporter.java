/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Alex Hurtado
 * Section Leader: Garrick Fernandez
 * Date: 02/07/18
 * 
 * This program outputs the location of the mouse to a label on the canvas. The label then turns red
 * whenever the mouse touches the label.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// Constants
	private static final int INDENT = 20;
	// Instance variables
	private GLabel label = new GLabel("");

	// Run Method
	public void run() {
		addMouseListeners();
		setUpLabel();
	}

	/**
	 * Method: Mouse Moved
	 * -------------------
	 * This method is called whenever the mouse is moved. When the mouse is moved, this method will retrieve and
	 * output the location of the mouse through a blue label on the canvas. The label will also turn red whenever
	 * the mouse's location is on top of the label.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x + ", " + y);
		if (getElementAt(x, y) == label) { 	// Checks if the label is at the mouse's location
			label.setColor(Color.RED); 		// To set the label's color to red
		} else { // Otherwise, sets the label's color to blue
			label.setColor(Color.BLUE);
		}
	}

	/**
	 * Method: Set Up Label
	 * --------------------
	 * This method sets the font, size, color, location, and initial text of the label.
	 * This method also adds the label to the canvas.
	 */
	private void setUpLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}

}
