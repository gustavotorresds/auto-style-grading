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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = null;
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		// add the label to the screen!
		label = new GLabel("0,0", INDENT, getHeight()/2);
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label);
		
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double x = e.getX(); 
		double y = e.getY();
		
		label.setLabel(x + "," + y);
		
		GObject touched = getElementAt(x,y); 
		if(touched != null) {
			touched.setColor(Color.RED);
		}else {
			touched.setColor(Color.BLUE);
		}
	}
	
	
	
	
	
	


}
