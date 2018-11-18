/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */
/*
 * Disney Vorng
 * CS106A
 * SL: Andrew Marshall
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
	
	/*
	 * A label that displays the coordinates of the mouse changes colors from 
	 * blue to red when the mouse scrolls over it. 
	 */
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		createLabel();
		addMouseListeners();
	}
	
	/*
	 * Creates the basics of the label. 
	 */
	public void createLabel() {
		label.setFont("Courier-24");
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);	
	}
	
	/*
	 * Tracks the coordinates of the mouse to display in the label and changes the 
	 * color of the label depending on the conditions of the mouse. 
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(mouseX + "," + mouseY);
		GLabel here = getElementAt (mouseX, mouseY);
		if (here != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}