/*
 * File: MouseReporter.java
 * Name: Will Shao
 * Section Leader: Chase Davis
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
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

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		addMouseListeners();
	}

	public void mouseMoved (MouseEvent e) {
		int x = e.getX(); //Gets the coordinates of the mouse
		int y = e.getY();
		label.setLabel(x + "," + y); //inputs the coordinates of the mouse into the GLabel
		GLabel label1 = getElementAt (x, y); 
		if (label1 != null) { //If the mouse is touching the label, it will turn red
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);//The label will be blue if the mouse is not touching the label
		}
	}
}



