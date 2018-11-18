/*
 * File: MouseReporter.java
 * -----------------------------
 * This class outputs the location of the mouse to a label on the
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
	private GLabel label = new GLabel("");
	
	public void run() {	
		// Ready for mouse input
		addMouseListeners();
		
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);	
	}
	
	// Method called whenever mouse is moved
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();	// Get x coordinate
		double mouseY = e.getY();	// Get Y coordinate
		// Cast type doubles for display
		int mouseDisplayX = (int) mouseX;
		int mouseDisplayY = (int) mouseY;
		label.setLabel(mouseDisplayX + "," + mouseDisplayY); // Update label
		//Check for GLabel
		GObject isLabel = getElementAt(mouseX, mouseY);
		if(isLabel == label) {
			label.setColor(Color.RED);
		} else if(isLabel != label)	{
			label.setColor(Color.BLUE);
		}
	}
}
