/*
 * File: MouseReporter.java
 * -----------------------------
 * The MouseReporter subclass outputs the location of the mouse 
 * to a label on the screen and changes the color of the label from 
 * blue to red when the mouse touches it.
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

	/*
	 * Method: Run
	 * -----------
	 * This program draws a blue label in the left side of the screen. 
	 * The label outputs the location of the mouse and turns red when the 
	 * mouse touches it.
	 */
	public void run() {	
		addMouseListeners();
		// this adds the label to the screen.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// this setLabel method takes in a "String".
		// this concatenates integers and commas:
		label.setLabel(0 + "," + 0);
		// this adds the label to the screen.
		add(label, INDENT, getHeight()/2);
	}

	/*
	 * Method: Mouse Moved
	 * -------------------
	 * This method responds to mouse motion. It gets the current x,y mouse
	 * location and displays it in the label. When the mouse is touching the 
	 * label it turns red, otherwise it is blue. 
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject obj = getElementAt (mouseX, mouseY);
		if (obj != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}