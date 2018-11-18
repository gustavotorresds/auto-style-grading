/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Yahui Zhu
 * Section Leader: Ben Allen
 * 
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
	
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		// add mouthListeners
		addMouseListeners();
	}
	
	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject object = getElementAt (mouseX, mouseY);
		if (object == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}
}
		
