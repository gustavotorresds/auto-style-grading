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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// this code adds a MouseListener 
		addMouseListeners();

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);


	}
	/* This code listens for the mouse and adds the coordinates of the mouse to the label. 
	 * If the coordinates of the mouse overlap with those of the label, the label will turn red. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		//sets the content of the label equal to mouse coordinates.
		label.setLabel(mouseX + "," + mouseY);
		// if there is an element at mouse coordinates, it will turn it red. 
		if (getElementAt(mouseX, mouseY) !=null) {
			label.setColor(Color.RED);
		}
		// otherwise 
		else {
			label.setColor(Color.BLUE);
		}

	}

}


