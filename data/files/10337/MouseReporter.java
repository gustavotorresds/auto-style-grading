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

	public void run() {	
		addMouseListeners();
		add(label, INDENT, getHeight()/2);
		label.setFont("Courier-24");

	}

	public void mouseMoved (MouseEvent e) {
		int xLoc = e.getX();
		int yLoc = e.getY(); 
		label.setLabel(xLoc+ ","+ yLoc);
		GLabel check = getElementAt (xLoc,yLoc);
		if (check==null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}

	}
}


