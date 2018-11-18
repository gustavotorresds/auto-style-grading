/*
 * File: MouseReporter.java
 * Name: Lauren Rood
 * Section: Rachel Gardner
 * -----------------------------
 * This program changes the color of the label that 
 * already exists on the screen from red to blue when
 * you move your mouse over the blue label.
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
		// This code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// This setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// Add the label to the screen!
		add(label, INDENT, getHeight()/2);

		// Add mouse listeners
		addMouseListeners();
	}
	public void mouseMoved(MouseEvent e) {
		// Find the mouse location
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);

		// Determines if object is there
		if (getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}			
	}
}


