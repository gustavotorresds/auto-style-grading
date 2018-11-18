/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Aris Konstantinidis
 * Section Leader: Brahm Capoor
 * 
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
		addMouseListeners(); //adds mouse listeners

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
	public void mouseMoved(MouseEvent e) {

		double mouseX = e.getX(); // get the x-coordinate of where the mouse moves to
		double mouseY = e.getY(); // get the x-coordinate of where the mouse moves to
		label.setLabel(mouseX + "," + mouseY); // adds the mouse coordinates to the label
		changeColor(mouseX, mouseY); // changes the label's color to red if mouse-over
	}
	private void changeColor(double mouseX, double mouseY) {
		if(mouseX < INDENT + label.getWidth() && mouseY < getHeight()/2 
				&& mouseY > getHeight()/2-label.getAscent() ) { 
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
