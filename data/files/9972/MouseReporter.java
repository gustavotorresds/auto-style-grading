/*
 * File: MouseReporter.java
 * Name: Gracie Zaro
 * Section Leader: Kait Lagattuta
 * -----------------------------
 * This method outputs the coordinates of the mouse 
 * on to the screen. The label is blue unless the mouse
 * is directly on the label, in which case the label
 * turns to red. 
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
	
	/*
	 * Method: mouseMoved
	 * --------------------------------
	 * This updates the label on the screen based on the location of the mouse.
	 * If the mouse is actually touching the label, the label will turn red, 
	 * otherwise the label will be blue. 
	 */
	public void mouseMoved(MouseEvent e) {
		int xMouse = e.getX();
		int yMouse = e.getY();
		if (getElementAt(xMouse, yMouse) != null){
			label.setColor(Color.red);
		} else {
			label.setColor(Color.blue);
		}
		label.setLabel(xMouse + "," + yMouse);
	}
}
