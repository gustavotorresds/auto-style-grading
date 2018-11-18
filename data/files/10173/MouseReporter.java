/*
 * File: MouseReporter.java
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
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
	private GLabel LABEL = new GLabel("");
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		LABEL.setFont("Courier-24");
		LABEL.setColor(Color.BLUE);
				
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		LABEL.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(LABEL, INDENT, getHeight()/2);
		
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		
		double xCoord = e.getX();
		double yCoord = e.getY();
		
		LABEL.setLabel(xCoord + ", " + yCoord);
		
		GObject label = getElementAt(xCoord, yCoord);
		if(label != null) {
			label.setColor(Color.RED);
		} else {
			LABEL.setColor(Color.BLUE);
		}
		
	}


}
