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

	/* CS 106A Winter 2018
	 * Author: Paul Handal
	 */
	
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
		
		// 1. add listeners
		addMouseListeners();

	}
	// 2. Define method
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		if (checkLabelPosition(mouseX,mouseY)) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}
	private boolean checkLabelPosition(double X, double Y) {
		// Check whether mouse is on the label
		GLabel isLabel = getElementAt(X,Y);
		// If null, make blue, else, make red
		return (isLabel == null); 
	}


}