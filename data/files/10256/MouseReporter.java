/*
 * File: MouseReporter.java
 * -------------------
 * Name: Natalie Chun
 * Section Leader: Thariq
 * 
 * The location of the mouse is the output on a label on the
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

	int mouseX;
	int mouseY;

	public void run() {	

		addMouseListeners();
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(+mouseX+ ", "+mouseY);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		label.setLabel(+mouseX+ ", "+mouseY);

		GLabel touchedLabel = getElementAt (mouseX, mouseY);

		if(touchedLabel != null) {
			label.setColor(Color.RED);
		}else{
			label.setColor(Color.BLUE);
		}
	}
}









