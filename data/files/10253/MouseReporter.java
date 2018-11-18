/*
 * Name: Thomas Henri
 * Section Leader: Ben Barnett
 * Section Time: Wednesday 4:30
 * Date: 2/7/18
 * SUID: thenri
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
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

	public void run() {	
		addMouseListeners();
		addlabel();	
	}

	/*
	 * Method: Add Label:
	 * __________________
	 * This method adds a label to the screen
	 */
	private void addlabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);		
	}
	
	/*
	 * Method: Mouse Moved
	 * ____________________
	 * This method sets the label to say the mouse location
	 * and also makes the label colored red if the mouse is 
	 * hovering over the label
	 */
	public void mouseMoved(MouseEvent e) {
		label.setLabel(e.getX() + "," + e.getY());
		GObject object = getElementAt(e.getX(), e.getY());
		if (object==label) {
			label.setColor(Color.RED);		
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
