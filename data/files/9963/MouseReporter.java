/*
 * File: MouseReporter.java
 * -----------------------------
 * Jassi Pannu
 * Section leader: Luciano Gonzalez 
 * 
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
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
		
		addMouseListeners(); 
		
	}
	



	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY(); 
		
		label.setLabel(x + "," + y);
		
		GObject obj = getElementAt(e.getX(), e.getY());
		
		if(obj != null ) {
			label.setColor(Color.red); 
		}else {
			label.setColor(Color.blue);
		}
		
		
	}
	
	

}