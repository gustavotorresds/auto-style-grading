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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {
		
		addMouseListeners();
		
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		label.setLabel(mouseX + "," + mouseY);
		colorLabel(mouseX, mouseY);
	}

	private void colorLabel(int mouseX, int mouseY) {
		GObject labelCheck = getElementAt(mouseX, mouseY);
		if(labelCheck == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
		
	}

}
