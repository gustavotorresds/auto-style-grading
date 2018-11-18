/*
 * File: MouseReporter.java
 * 
 * Name: Jack Nichols
 * 
 * Section Leader: Marilyn Zhang
 * 
 * Sources: CS106A Style Guide (Assignment 2); The Art & Science of Java by Eric S. Roberts
 * 
 * Description: This program records the position of the mouse and puts it in red print
 * on the left side of the screen (centered in the y - direction). When the label is
 * touched, it turns from red to blue. When the mouse moves away again, it turns back 
 * to red.
 * 
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		// add MouseListeners such that movements of the mouse can be recorded
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		// once the mouse enters the screen, the label is immediately set to its location!
		label.setLabel (mouseX + "," + mouseY);
		// The mouse now searches for an object at the location of the mouse.
		GObject obj = getElementAt (mouseX, mouseY);
		// If an object is present (i.e. not null), the label's color is set to red.
		if (obj != null) {
			label.setColor(Color.RED);
		// If an object is not present (i.e. null)< the label's color is set to blue.
		} else if (obj == null) {
			label.setColor(Color.BLUE); 
		}	
	}	
}
