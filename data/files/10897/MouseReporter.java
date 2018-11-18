/*
 /* File: MouseReporter.java
 * -----------------------------
 * Name: Roopa Som 
 * Section Leader: Meng Zhang 
 * 
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
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
		addMouseListeners();
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
	}
		
	public void mouseMoved(MouseEvent e) { // makes label reflect mouse position 
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		
		// turns label red when mouse hovers over label area 
		if (getElementAt(mouseX, mouseY) != null) { 
			label.setColor(Color.RED);
		} else { // turns label back to blue when mouse leaves 
			label.setColor(Color.BLUE); 
		}
	}

}
