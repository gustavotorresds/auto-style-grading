/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Antoni Brasó
 * Section Leader: Akua MkLeod
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
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

	//// This variables are visible to the entire program
	// This variables define X and Y mouse coordinates respectively
	int mouseX;
	int mouseY;

	public void run () {
		// adds the label to the screen
		addLabel();
		// adds mouse listeners
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX(); // get the x-coordinate of where the mouse moves to
		mouseY = e.getY(); // get the y-coordinate of where the mouse moves to
		GObject touchedLabel = getElementAt(mouseX, mouseY);  // checks whether there is an object in the mouse position
		boolean touchedLabelbol = false;
		if(touchedLabel != null) {
			if(label == touchedLabel) {
				touchedLabelbol = true;
			}
		}
		updateLabel(touchedLabelbol);
	}

	private void addLabel() {	
		// this code already adds the label to the screen!
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(mouseX + "," + mouseY);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	private void updateLabel(boolean touchedLabelbol) {
		label.setLabel(mouseX + "," + mouseY);

		if(touchedLabelbol) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
