/*
 * File: MouseReporter.java
 * -----------------------------
 * This program puts the output of the location of the mouse 
 * to a label on the screen. The color of the label changes 
 * to red when the mouse touches it.
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// Enables the user to move the mouse around the canvas
		addMouseListeners();
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	// This method allows the user to move the mouse around the
	// the canvas while its coordinate points are shown on the left
	// side of the screen. This method also calls for the color of the label
	// to change if the mouse is placed on it
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		if(getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}	



