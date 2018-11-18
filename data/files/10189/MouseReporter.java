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

		// add mouse listeners to track the movement of the mouse
		addMouseListeners();	
	}

	public void mouseDragged(MouseEvent e) {
		// define x and y coordinates of mouseEvent e
		double mouseX = e.getX();
		double mouseY = e.getY();

		// format label
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// make label reflect the position of the mouse
		label.setLabel(mouseX + ", " + mouseY);
	
		// add the label to the screen
		add(label, INDENT, getHeight()/2);

		// get the coordinates of the label 
		GLabel lab = getElementAt(mouseX,mouseY);
		// make the label red if the mouse is on it
		// make the label blue if the mouse in not on it
		if (lab == null) {
			label.setColor(Color.BLUE);
		} else { 
			label.setColor(Color.RED);
		}

	}

}
