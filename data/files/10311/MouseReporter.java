/*
 * File: MouseReporter.java
 * -------------------
 * Name: A.J. "CS is cool" Aldana
 * Section Leader: Kaitlyn "CS is /incredible/" Lagattuta
 * 
 * This file reads the mouse location on the program screen, prints it to the screen,
 * and turns red if touched.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;
import stanford.spl.GObject_remove;

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
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
	}


	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();

		label.setLabel(mouseX + ", " + mouseY);
		
		GLabel touchedObject = getElementAt(mouseX, mouseY);
		
		checkIfLabel(touchedObject);
	}


	private void checkIfLabel(GLabel touchedObject) {
		if (touchedObject != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
