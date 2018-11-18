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
		
		// allows the program to listen to what the mouse does
		addMouseListeners();
	}
	
	/*
	 * This method allows the program to read the x and y positions of the mouse. The label then reflects the current position
	 * of the mouse. Lastly, if the mouse is hovering anywhere on the label, the label turns red.
	 * Pre: The label states 0.0, 0.0 and the screen is blank
	 * Post: The label states the current x and y positions of the mouse. The color is blue, the label turns red if the mouse
	 * is hovering on the label.
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX(); //gets X position of mouse
		double mouseY = e.getY(); //gets Y position of mouse
		
		label.setLabel(mouseX + "," + mouseY);
		
		// This statement tests for whether or not there is an object or element at the current position of the mouse.
		// The only object present on the screen is the label, therefore the test will only turn the label red if the result is not Null.
		if (getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
	


}
