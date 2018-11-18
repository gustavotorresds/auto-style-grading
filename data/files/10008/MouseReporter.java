/*
 * File: MouseReporter.java
 * -----------------------------
 * This program is designed to report the location of the mouse to a label on 
 * the center-left of the screen. The color of the label changes to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label.
	private static final int INDENT = 20;
	
	// The label is an instance variable, visible to all the program.
	private GLabel label = new GLabel("");
	
	public void run() {
		label.setLabel(0 + "," + 0);
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}	
	
	/*
	 * Method: Connect the mouse movements to the label. The label will report the location of the mouse, and turn red if the mouse is on top of the label.
	 * Precondition: There is no coordination between the mouse and the label to the left side of the screen.
	 * Postcondition: The label tracks the mouse movements and turns red under certain conditions, as described in the method.
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (getElementAt (x, y) != null) {
			label.setColor(Color.RED);
		}
		label.setLabel(x + "," + y);
	}
	


}
