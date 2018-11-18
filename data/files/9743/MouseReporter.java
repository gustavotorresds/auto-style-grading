/*
 * File: MouseReporter.java
 * Author: Olivia Higa
 * Section Leader: Garrick Fernandez
 * Date: January 29, 2018
 * -----------------------------
 * Outputs the coordinates of the cursor whenever it is moved.
 * If the cursor is touching the label, it will turn red.
 * Otherwise the label will show up as blue
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	//instance variable for access throughout program
	private GLabel label = new GLabel(""); 
	
	public void run() {	
		addInitialCoordinates();
		addMouseListeners();
	}

	/**
	 * Method: addInitialCoordinates
	 * ------------------------------
	 * Postcondition: creates the starter label in blue on the left side of the screen
	 */
	private void addInitialCoordinates() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2); 
	}
	
	/**
	 * Method: mouseMoved 
	 * ----------------------------------
	 * Postcondition: updates the labels
	 * 		If the cursor is touching the label position, change the label color to red
	 * 		Otherwise keep the label color at blue
	 */
	public void mouseMoved(MouseEvent e) {
		//gets coordinates of the cursor
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		label.setLabel (mouseX + "," + mouseY); //update the label with the cursor's coordinates
		GObject touchedLabel = getElementAt (mouseX, mouseY); //gives back the object cursor is touching
		if (touchedLabel != null) { 
			label.setColor(Color.RED); //if the cursor is touching the label
		}
		else {
			label.setColor(Color.BLUE);
		}
		
	}
	

}
