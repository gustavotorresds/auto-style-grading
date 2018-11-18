/*
 * File: MouseReporter.java
 * -----------------------------
 * MouseReporter outputs the location of the mouse to a label on the
 * screen. The text indicating the location of the mouse changes to red when
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
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		
		// Label displays the coordinates of the mouse's location
		label.setLabel(x + "," + y);
		GObject mouseOver = getElementAt(x,y);
		
		// If the mouse is touching the label, the color of the font displaying the coordinates is red
		if(mouseOver != null) {
			label.setColor(Color.RED);
			
		// If the mouse is not touching the label, the color of the font displaying the coordinates is blue	
		} else {
			label.setColor(Color.BLUE);
		}

	}


}
