/*
 * File: MouseReporter.java


 * -----------------------------
 * Name: Carly Malatskey
 * Section Leader: Rachel Gardner
 * 
 * This program outputs the location of the mouse to a label on the
 * screen and changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {
	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for label
	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// This setLabel method takes in a "String" 
		// where you can concatenate integers and commas as such.
		label.setLabel(0 + "," + 0);

		// Adds the label to the screen.
		add(label, INDENT, getHeight()/2);

		addMouseListeners();
	}

	/*
	 * Method: mouseMoved
	 * This method allows mouse to move over the label and change
	 * the color of the label to red if the mouse is over the label. 
	 */

	public void mouseMoved(MouseEvent e) { 
		// These are the X and Y coordinates of the mouse. 
		double mouseX = e.getX();
		double mouseY = e.getY();

		label.setLabel(mouseX + "," + mouseY); 

		// This sets the color of the label depending on 
		// whether or not there is an object, in this case a label, 
		// where the mouse is. 
		if (getElementAt(mouseX, mouseY) != null ) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
