/*
 * File: MouseReporter.java
 * -----------------------------
 * This is a program that outputs the location of the mouse to a label on the
 * screen. It will change the color of the label to red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program; instance variable
	private GLabel label = new GLabel("");

	public void run() {	
		setUpLabel(); // Sets up the basic conditions of the label
		addMouseListeners(); // Tracks the mouse movement
		}
	
	/**
	 * Method: Track movement and display necessary changes
	 * -----------------------
	 * Tracks mouse movements and displays the x- and y-coordinates of the mouse on the screen.
	 * If the mouse ever touches the label, it will recognize the object and then turn red.
	 * If the mouse stops touching the label (or just isn't touching the label), it will be blue.
	 */
	public void mouseMoved(MouseEvent e) {
		int  x = e.getX();
		int y = e.getY();
		label.setLabel( ""+ x + "," + y +"");
		GObject labelDetector = getElementAt(x, y);
		if ( labelDetector == label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		} 
	
	/**
	 * Method: Add label
	 * -----------------------
	 * This method creates a blue label that is present in the left side of the screen.
	 */
	private void setUpLabel() {
		label.setFont("Courier-24"); // this code already adds the label to the screen!
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2); // add the label to the screen!
	}
}
