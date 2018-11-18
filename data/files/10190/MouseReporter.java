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

// This program creates a blue label on the left side of the screen that displays the mouse's location and turns red if the mouse touches it:
public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label:
	private static final int INDENT = 20;
	// The label:
	private GLabel label = new GLabel("");
	// The location of the label:
	private int X = 0;
	private int Y = 0;
	
	// This runs the program:
	public void run() {	
		setUpLabel();	
		addMouseListeners();
	}
	
	// This adds a label to the left of the screen:
	private void setUpLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved (MouseEvent e) {
		x = e.getX();
		y = e.getY();
		label.setLabel(" " + X + "," + Y); // This makes the label display the location of the mouse 
		GObject labelPresence = getElementAt(X,Y); // This checks to see whether the mouse is on the label.
		if (labelPresence != null) {
			label.setColor(Color.RED); // This changes the label's color to red if the mouse touches it.
		} else {
			label.setColor(Color.BLUE); // This keeps the label blue if the mouse is not on it. 
		}
	}
}
