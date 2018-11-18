/*
 * File: MouseReporter.java
 * -----------------------------
 * 
 * This program will display the location of the mouse to a label on the screen.
 * When the mouse is touching the label, the label will turn from blue to red.
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
		addMouseListeners(); // allows the mouse to move.
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
	    GLabel mouseOnLabel = getElementAt(mouseX, mouseY); // This asks, "What element is at the mouse location?" The answer will be stored in the label mouseOnLabel.
	    label.setLabel(mouseX + "," + mouseY); // This line sets the values of the label "label" to display the x and y positions of the mouse.
	    if (mouseOnLabel != null) { 
	    	label.setColor(Color.RED);
	    } else {
	    	label.setColor(Color.BLUE);
	    }
	}
	
	
}
