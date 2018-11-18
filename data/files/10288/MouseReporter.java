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

	/* Constant for the x value of the label */
	private static final int INDENT = 20;

	/*Private instance variable (visible to entire program)*/
	private GLabel label = new GLabel("");

	public void run() {	

		// Adds the label to the screen
		addInitialLabel (); 	
		addMouseListeners();
	}

	/*
	 * Displays a generic coordinate (x, y) when the mouse has not yet moved 
	 */
	private void addInitialLabel() {
		add(label, INDENT, getHeight()/2);
		label.setFont("Courier-24"); 
		label.setColor(Color.BLUE);  
		label.setLabel("x, y"); 
	}

	/*
	 * Gets location of mouse and inputs those coordinates into the label
	 * If mouse touches label, the label turns from blue to red
	 */
	public void mouseMoved (MouseEvent e) {
		int movex = e.getX();
		int movey = e.getY();
		label.setLabel(movex + "," + movey); 
		GLabel touchingLabel = getElementAt(movex, movey); 	
		if (touchingLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}