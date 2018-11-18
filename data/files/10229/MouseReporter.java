/*
 * File: MouseReporter.java
 * -------------------
 * Name: Joel Ramirez
 * Section Leader: Esteban Rey
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
		addMouseListeners();
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	// allows for the GLabel to change colors if the mouse is on it
	public void  mouseMoved(MouseEvent e) { 
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x+ "," + y);
		GLabel obj = getElementAt(e.getX(), e.getY());
				if (obj != null) {
					label.setColor(Color.RED);
				} else {
					label.setColor(Color.BLUE);
				}
	}
	 
	
	}



