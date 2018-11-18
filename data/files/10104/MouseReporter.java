/*
 * File: MouseReporter.java
 * 
 * Name: Katina Mattingly
 * Section Leader: Thariq Ridha
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
	
	// Since using mouseListeners, we must add another 
	// instance variable to communicate between methods
	private double mouseX = 0;
	private double mouseY = 0;
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		
		while (true) {
			label.setColor(Color.BLUE);
			
			//Add mouse tracking capabilities 
			addMouseListeners();
			
			// Tests if mouse is over the label and changes to red
			GObject labelPresent = getElementAt(mouseX, mouseY);
			if (labelPresent != null) {
				label.setColor(Color.RED);
			}
			
			// this setLabel method takes in a "String" 
			// you can concatenate integers and commas as such:
			label.setLabel(mouseX + "," + mouseY);
			
			// add the label to the screen!
			add(label, INDENT, getHeight()/2);	
		}
	}
	
	// sets instance variables to the x and y value of the mouse
	public void mouseMoved(MouseEvent e) { //must be public!
		mouseX = e.getX();
		mouseY = e.getY();
	}


}
