/*
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen, and changes the color of the label to red when
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
		// this code adds the label to the screen!
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		addMouseListeners();
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);

		GObject checkForLabel = getElementAt (mouseX, mouseY);
		if (checkForLabel != null) { //checks if mouse is over the label
			label.setColor(Color.RED);//If it is, the label turns red
		} else {
			label.setColor(Color.BLUE);
		}
	}
}

