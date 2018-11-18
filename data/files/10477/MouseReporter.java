/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Jonathan Morales
 * Section Leader: Brahm Capoor
 * 
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
		setupLabel();
		addMouseListeners();
	}

	// Method to clean up the setup of the label
	private void setupLabel() {
		label.setLabel(0 + "," + 0);
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);

	}

	// tracks location of the cursor when moved
	public void mouseMoved(MouseEvent e) {
		// location of cursor
		int x = e.getX();
		int y = e.getY();
		
		// event
		updateLocation(x, y);
		updateColor(x, y);
	}

	private void updateLocation(int x, int y) {
		// sets label on x and y location based 
		// on cursor location
		label.setLabel(x + "," + y);
	}

	private void updateColor(int x, int y) {
		// most current cursor location
		GObject cursorLocation = getElementAt(x, y);
		// sets color to blue or red 
		if(label == cursorLocation) {
			label.setColor(Color.BLUE);
		} else label.setColor(Color.RED);
	}
}