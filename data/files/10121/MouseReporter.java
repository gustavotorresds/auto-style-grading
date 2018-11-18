/*
 * File: MouseReporter.java
 * Name: Ryan Crowley
 * Section Leader: Esteban Rey
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen and changes the color of the label to red when
 * the mouse touches it. This program requires no user input other 
 * than moving the mouse and clicking.  It uses mouseListeners to 
 * track the user.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	
	private GLabel label = new GLabel("");

	/**
	 * method: run
	 * This method adds a label to the screen.  The majority of the 
	 * work actually occurrs outside of the run method when the mouse
	 * is either moved or clicked.
	 */
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}

	/**
	 * method: MouseMoved
	 * When the mouse is moved, this method updates a label on the 
	 * left side of the screen with the current (x, y) location of the
	 * mouse. If the mouse is touching the label, it turns red.
	 */
	public void mouseMoved(MouseEvent e) {
		//updates label with location
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		add(label, INDENT, getHeight()/2);

		//turns red if mouse is on label
		GObject obj = getElementAt(x, y);
		if(obj != null) {
			label.setColor(Color.red);
		}
		else {
			label.setColor(Color.blue);
		}
	}
}



