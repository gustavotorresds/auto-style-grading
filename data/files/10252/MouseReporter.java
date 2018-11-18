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
import acm.graphics.GObject;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	int mouseX;
	int mouseY;

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
	
		// add mouse
		addMouseListeners();
		
		while(true) {
			updateLabel();
			updateColor();
		}
	}

	public void mouseMoved(MouseEvent e) {
		//get mouse location
		mouseX = e.getX();
		mouseY = e.getY();
	}

	//updates label to current x and y coordinates of current mouse 
	private void updateLabel() {
		label.setLabel(mouseX + "," + mouseY); 
	}

	//when the mouse is on the label, change the color to red
	private void updateColor() {
		if(getElementAt(mouseX,mouseY) != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
