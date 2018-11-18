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

	// Constant for the x-value of the label
	private static final int INDENT = 20;
	
	// Private instance variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		addMouseListeners();
		
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		label.setLabel("Welcome to Mouse Reporter");	// If mouse isn't in from at start, sets label to default
		add(label, INDENT, getHeight()/2);
	}
	
	// When mouse is moved, sets label to coordinates of where mouse is.
	// If mouse is on label, label is set to red; otherwise, label is blue.
	public void mouseMoved(MouseEvent e) {
		label.setLabel(e.getX() + ", " + e.getY());
		if (getElementAt(e.getX(), e.getY()) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
	


}
