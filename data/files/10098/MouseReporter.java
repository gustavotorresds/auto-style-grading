/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Ri
 * Section Leader: Rachel
 * 
 * This file outputs the location of the mouse to a label on the
 * screen. It changes the color of the label to red when
 * the mouse touches it. The color of the label is initially blue.
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
		// this code creates the label
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// this setLabel method takes in a "String" 
		label.setLabel(0 + "," + 0);
		// add the label to the screen
		add(label, INDENT, getHeight()/2);
		//add mouse listeners
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		//continuously sets the x and y coordinates of the mouse
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		//this finds what's underneath the mouse at a given point, and if it's the label,
		//then it turns the label blue
		GLabel obj = getElementAt(e.getX(), e.getY());
		if(obj != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
