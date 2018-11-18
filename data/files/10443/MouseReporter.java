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

	// This variable is an "instance variable" and is visible to the entire program
	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();
		add(label, INDENT, getHeight()/2);
		label.setFont("Courier-24");
	}

	//This method gets the x and y location of where the mouse moves to
	//It changes the color of the label if the mouse is on it
	public void mouseMoved(MouseEvent e) {
		label.setLabel(e.getX() + "," + e.getY());
		
		//if the mouse is not on the label, the label should remain blue
		//else the mouse is on the label, the label should be set to red
		if (getElementAt (e.getX(), e.getY()) == null) {
			label.setColor(Color.BLUE);
		}
		
		else {
			label.setColor(Color.RED);
		}
	}
}
