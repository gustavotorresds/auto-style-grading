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
	}
	
	public void init() {
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		//Retrieves if mouse touches an object.
		GLabel object = getElementAt(e.getX(), e.getY());
		//sets label to display the x and y coordinates of the mouse
		label.setLabel(e.getX() + "," + e.getY());
		//if the mouse touches the label, it will turn red.
		if (object != null) {
			label.setColor(Color.RED);
		}
		//if the mouse is anywhere else on the canvas, it will be blue.
		else {
			label.setColor(Color.BLUE);
		}
		
		
	}


}
