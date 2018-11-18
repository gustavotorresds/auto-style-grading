/*
 * File: MouseReporter.java
 * 
 * Name: Alisha Birk
 * Section Leader: Marilyn Zhang
 * Sources: The Art & Science of Java by Eric Roberts 
 * -----------------------------
 * Records the location of the mouse on the screen in
 * a blue label, which turns red when the mouse scrolls
 * over the label. 
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
		
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GLabel labelPosition = getElementAt(mouseX, mouseY);
		if (labelPosition != null) {
			labelPosition.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
