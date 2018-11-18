/*
 * File: MouseReporter.java
 * Name: Ramona Greene
 * Section Leader: Thariq Ridha
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen. It then changes the color of the label to red when
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
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		addMouseListeners(); 
	}
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		
		String newCoordinate = "(" + Double.toString(mouseX) + "," + Double.toString(mouseY) + ")";
		label.setLabel(newCoordinate);
		
		if(label.contains(mouseX, mouseY)) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	} 
}
