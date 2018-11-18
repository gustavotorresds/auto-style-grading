/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import acm.graphics.*;

import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	/*This progam    creates a GLabel on the left side of the screen, on which the coordinates of the mouse's position is displayed. When the mouse is moved the label is updated to display the current x, y location of the mouse. When the mouse is touching the label it turns red, otherwise it remains blue.*/
	public void run() {
		
		createLabel();
		addMouseListeners();

	}
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		GObject obj = getElementAt(mouseX,mouseY);
		label.setLabel(mouseX + "," + mouseY);
		if (obj== label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		
	}
	/*This method creates the label, according to a specified font size and initial color and display it at a specific point to the left of the screen*/
	private void createLabel() {
	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-40");
		label.setColor(Color.BLUE);
					
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		//label.setLabel(0 + "," + 0);
					
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
}