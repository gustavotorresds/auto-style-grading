/*
 * File: MouseReporter.java
 * Name: Alex Nam
 * Section Leader: Ella Tessier-Lavigne
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
	private GLabel label = new GLabel(" ");

	public void run() {	
		// before mouse starts moving, "0, 0" is displayed on the center of the screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		label.setLabel(0 + "," + 0);

		add(label, INDENT, getHeight() / 2);

		// 1. add mouse listeners; once mouse is moved, method setLabel stops and method mouseMoved is activated
		addMouseListeners();
	}

	// 2. define the mouse type method	
	public void mouseMoved(MouseEvent e) {
		// 3. get the mouse location
		double x = e.getX();
		double y = e.getY();

		// 4. do something
		label.setLabel(x + "," + y);

		// if the mouse touches the label, the color changes to red
		GObject mouseTouches = getElementAt(x, y);
		if(mouseTouches != null) {
			// if mouse touches the label, the color of the label changes to red
			label.setColor(Color.RED);
		} else { 
			label.setColor(Color.BLUE); 
		}
	}
}
