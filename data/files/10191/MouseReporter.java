/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;

import java.awt.event.MouseEvent;
import java.util.Set;

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
	/*
	 * This mouseMoved method identifies the point on the screen where the cursor
	 * is and then sets the label to that value. It then tests whether that point
	 * has an object there. Since the only object in the screen is the label, if there
	 * is an object it turns it red. Once the cursor is removed, it returns the color to 
	 * blue.
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject objectPresent = getElementAt (mouseX, mouseY);
		if (objectPresent != null) {
			label.setColor(Color.RED);
		}
		if (objectPresent == null) {
			label.setColor(Color.BLUE);
		}
	}
}
