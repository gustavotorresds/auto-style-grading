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
import acm.graphics.GRect;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	private static final Color BLUE = null;
	
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		placeLabel(INDENT, getHeight()/2);
		// 1. add mouse listeners 
		addMouseListeners();
	}
		
		public void mouseMoved(MouseEvent event) { 
			// Java runs this when mouse is moved
			int x = event.getX();
			int y = event.getY();
			placeLabel(x, y);
				if (x == INDENT || y == getHeight()/2) {
					Color label = Color.RED;
				}
		}
	
		private void placeLabel(double x, double y) {
			label.setFont("Courier-24");
			label.setColor(Color.BLUE);
			label.setLocation(x, y);
			// this setLabel method takes in a "String" 
			// you can concatenate integers and commas as such:
			label.setLabel(x + "," + y);
			// add the label to the screen!
			add(label, INDENT, getHeight()/2);
		}
	}

	
	


