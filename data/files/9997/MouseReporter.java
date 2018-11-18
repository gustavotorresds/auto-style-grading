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
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	/*
	 *Pre: Blank canvas
	 *Post: A label on the left middle of the screen that says (0,0)
	 */
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
	 * This method runs when a mouse event happens.
	 * It changes the label to display the x and y coordinate of the mouse
	 * and changes the label's color to red when the mouse goes over the label.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		
		// sets color back to blue when mouse is no longer on the label
		if(getElementAt(mouseX, mouseY) != (label))
		{
			label.setColor(Color.BLUE);
		}
		
		// if the mouse hovers over the label it will turn red
		else if(getElementAt(mouseX, mouseY).equals(label))
		{
			label.setColor(Color.RED);
		}
	}
}
