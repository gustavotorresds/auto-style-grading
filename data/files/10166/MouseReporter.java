/*
 * File: MouseReporter.java
 * Name: Natalie Hojel
 * Section Leader: Julia Daniel
 * -----------------------------
 * This outputs the location of the mouse to a label on the left 
 * of the screen. It changes the color of the label to red when
 * the mouse touches it. 
 * 
 * Precondition: The screen is empty.
 * Postcondition: There is a label that outputs the location of 
 * the mouse and turns red when the most touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		// Creates the label to the screen. 
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
			
		// This setLabel method takes in a "String" 
		// When the program opens, shows coordinates as 0,0 before the mouse moves.
		label.setLabel(0 + "," + 0);
		
		// Adds the label to the screen. 
		add(label, INDENT, getHeight()/2);
		
		// Adds mouse listeners. 
		addMouseListeners(); 
	}
	
	/* This method defines the mouse move method. This allows for the mouse
	 * to take control and then return to the run method. It outputs the 
	 * coordinates of the mouse and prints them in the label. 
	 * 
	 * Precondition: Mouse movement is not tracked.
	 * Postcondition: Mouse movement is now tracked and label changes color
	 * when the mouse moves on top of it. 
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX(); 
		int mouseY = e.getY(); 
		label.setLabel(mouseX + "," + mouseY);
		if(getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED); 
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
