/*
 * File: MouseReporter.java

 * Name: Madeleine Chang
 * Section Leader: Maggie Davis
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
	//step 1: add mouse listeners 
		addMouseListeners();
	}
	//step 2: create a mouse event that listens for movement so we can track the mouse!
	public void mouseMoved(MouseEvent e) {
		//step 3: we can to look out for the coordinates of the mouse, which we can extract using the variable "e" 
		int x = e.getX();
		int y = e.getY();
		
		//step 4: do something with those coordinates we collected
		addLabel(x,y);
	}				
	
/*
 * Method: addLabel
 * ---------
 * Adds the coordinates to the screen using a GLabel that takes the parameters int x and int y, which were defined in the mouse event.
 * Additionally, checks to see if the mouse has moved onto the label itself. If so, it makes the label red. 
 */
	private void addLabel(int x, int y) {
		// this code already adds the label to the screen!
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(x + "," + y);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		//check if mouse is on label, and if so, make the label red
		GLabel moved = getElementAt(x,y);
			if(moved !=null) {
				label.setColor(Color.RED);
			}
	}	
}
