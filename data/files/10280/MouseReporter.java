/*
 * File: MouseReporter.java 
 * Avery Dekshenieks
 * Thariq Ridha
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
		add(label, INDENT, getHeight()/2); //Adds label to screen
		
		//Allows program to take inputs from the user's mouse
		addMouseListeners();
	}
	
	/* 1) takes in the location of the mouse 
	 * 2) displays location on the label
	 * 3) makes label red while mouse is on it
	 * 4) makes label blue while mouse is not on it
	 */
	public void mouseMoved(MouseEvent e) {
		//Accepts the x and y location of the user's mouse
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY); //Displays location in the label
		
		//Checks to see if mouse is hovering over the label
		if (getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		}
		//Necessary to add or else the label will stay red even after mouse comes off the label
		else {
			label.setColor(Color.BLUE);
		}
	}
}
