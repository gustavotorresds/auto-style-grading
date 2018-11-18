/*
 * Name: Christopher Bechler
 * Due Date: February 7, 2018
 * Section Leader: Garrick Fernandez
 * Sources Used: Lecture & Handout Materials
 * File: MouseReporter.java
 * -----------------------------
 * This code creates a label on the left side of the screen (vertically centered). This
 * label indicates the location of the mouse (x,y position) when the mouse moves. The label
 * reports a value of "0,0" until the mouse moves. The label changes from blue to red when 
 * the mouse hovers over the label. 
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
		// This code already adds the label to the screen!
		// Run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// This setLabel method takes in a "String" 
		// You can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// Add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		// WHERE MY CODE BEGINS
		addMouseListeners();
	}
	
	/*
	 * Mouse Event Name: Change Label Value and Label Color
	 * --------------------------------
	 * This event changes the label to update the current x,y location of the mouse
	 * and changes the color of the label to red when the mouse is over the label.
	 */		
	
	public void mouseMoved(MouseEvent e) {
		// Change the label to update the current x,y location of the mouse
		// Used int instead of double given the example in the HW!
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		
		// Change the color of the label only if the mouse is over the label		
		GObject maybeLabel = getElementAt(x, y);
		if (maybeLabel != null) {
			label.setColor(Color.RED); 
		} else {
			label.setColor(Color.BLUE);
		}	
	}
}
