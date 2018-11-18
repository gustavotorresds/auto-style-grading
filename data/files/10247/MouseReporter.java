/*
 * File: MouseReporter.java
 * -----------------------------
 * THis File tells you the location of the mouse on the screen
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
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	//This uses the mouse listener to know where the mouse is.
	//IT gets the positions of the x, then it changes the label to say the new coordinates
	//If the mouse is on something then it must be the label and it changes its color.
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY= e.getY();
		label.setLabel(mouseX + ", " + mouseY);
		if ((getElementAt(mouseX, mouseY)) != null) {
			label.setColor(Color.RED);
			}
		else {
		label.setColor(Color.BLUE);
		}
	}
}


