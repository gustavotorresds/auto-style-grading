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
import acm.graphics.*;

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

	public void mouseMoved(MouseEvent e) {
		
		//Update the coordinates displayed on the screen as the mouse moves
		double mouse_x = e.getX();
		double mouse_y= e.getY();
		label.setLabel(mouse_x + "," + mouse_y);
		add (label, INDENT, getHeight()/2);
		
		//Adjusting for the Colors depending on the Location of the Mouse
		// Check if an object exists (mouse touches the label) at the given location
		GObject isObject = getElementAt(mouse_x,mouse_y);
		if (isObject != null) { // if the mouse touches the label - fill it with Red
			label.setColor(Color.RED);
		} else { // if the mouse is not on the label- fill it with Blue
			label.setColor(Color.BLUE);
		}	
		
	}
	
}
