/*
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen. It changes the color of the label to red when
 * the mouse touches it.
 * 
 * Gary Schwartz garys1
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
		addMouseListeners();
		
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		
		add(label, INDENT, getHeight()/2);
	}
	
	/**
	 * This method updates the label while the mouse moves around the screen and changes the color to red if mouse is on top of it.
	 */
	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		
		label.setLabel(mx + ", " + my + "");
		if(getElementAt(mx, my) == label) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	}


}
