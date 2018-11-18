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

	int mouseX = 0;
	int mouseY = 0;


	public void init() {
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		//x coordinate wherever the mouse is
		mouseX = e.getX();
		//y coordinate wherever the mouse is
		mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		//will be red text if the mouse is where the coordinates are displayed
		if (label.contains(mouseX, mouseY)) {
			label.setColor(Color.RED);
		}
		//will be blue text if the mouse is anywhere besides where the coordinates are displayed
		else {
			label.setColor(Color.BLUE);

		}
	}

	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(mouseX + "," + mouseY);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}



}
