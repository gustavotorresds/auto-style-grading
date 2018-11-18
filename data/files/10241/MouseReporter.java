/*
 * File: MouseReporter.java
 * Gabrielle Candes
 * Rhea Karuturi
 * -----------------------------
 * Outputs the location of the mouse to a label on the screen. 
 * Changes the color of the label to red when the mouse touches it.
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
		
		// sets the initial font and color of the label
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		label.setLabel(0 + "," + 0);
		
		// adds the label to the screen
		add(label, INDENT, getHeight()/2);
		
		// adds mouse listeners so we can create a mouseMoved function
		addMouseListeners();
	}
	
	
	public void mouseMoved(MouseEvent e) {
		
		// gets and stores x and y coordinates of the mouse
		int x = e.getX();
		int y = e.getY();
		
		// changes the text of the label to be the x and y coordinates of the mouse
		label.setLabel(x + ", " + y);
		
		GLabel hover = getElementAt(x,y);
		
		// if the mouse is hovering over the label, the color of the label changes to red
		if(hover != null) {
			label.setColor(Color.RED);
		}
		
		// if the mouse is not above the label, the color of the label reverts back to blue
		if(hover == null) {
			label.setColor(Color.BLUE);
		}
		
	}
	


}
