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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");

	/*
	 * This program first places a label on the screen with coordinates
	 * (0,0). Then it addsMouseListeners so that the mouse event method 
	 * can later take control once the user moves the mouse around the 
	 * canvas. 
	 */
	public void run() {	
		setup();
		addMouseListeners();
	}
	
	/*
	 * This sets up the label for the coordinates on the screen. 
	 */
	private void setup() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
	
	/*
	 * This method is run each time the mouse is moved. It 
	 * gets the coordinates of the mouse and changes the label to display
	 * the coordinates of the mouse. If the mouse coordinates are overlapping
	 * the coordinates of the label, it turns red. Otherwise, it is blue. 
	 */
	public void mouseMoved (MouseEvent e) {
		double xCoordinate = e.getX();
		double yCoordinate = e.getY();
		GObject obj = getElementAt(e.getX() , e.getY());
		label.setLabel(xCoordinate + "," + yCoordinate);
		if (obj == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
