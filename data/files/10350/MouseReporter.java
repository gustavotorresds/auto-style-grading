/*
 * File: MouseReporter.java
 * Name: Kevin Palma
 * Section Leader: Garrick
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
	private GLabel red = new GLabel("");
	public void run() {	
		addMouseListeners();
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel("" + "," + "");
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved (MouseEvent e) {
		double x = e.getX();
		double y= e.getY();
		label.setLabel(x + "," + y);
		if(e.getX() == label.getX() && e.getY() == label.getY()) {
			label.setColor(Color.RED);
			add(label);
		}else {
			add(label);
		}
	}
} 