/*
 * File: MouseReporter.java
 * Name: Daniel Turley
 * Section Leader: Kathryn Rydberg
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;
import acm.graphics.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// Instance variable for the label to track coordinates
	private GLabel label = new GLabel("");
	
	public void run() {	
		// Adds the label to the screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
		// Add mouse listeners to track and update coordinates and label color. 
		addMouseListeners();			
	}
	
	/**
	 * Method: Track Mouse
	 * -------------------
	 * This method is called anytime the mouse moves in the
	 * program screen, and sets the label to show its x,y coordinates.
	 * If the mouse hovers over the label, the label turns red. 
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		GObject obj = getElementAt(e.getX(), e.getY());
		if(obj == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}

}
