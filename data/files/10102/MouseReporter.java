/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for label
	private GLabel label = new GLabel("");


	/**
	 * Create the label with default coordinates (0,0) and color blue,
	 * adds mouseListeners. 
	 * PRECONDITIONS: none
	 * POSTCONDITIONS: Blue label displaying coordinates (0,0), program
	 * is keeping track of mouse location coordinates.
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	/**
	 * Sets label to display the coordinates of the mouse. If the mouse
	 * touches the label, it will turn red and then back to blue after the
	 * mouse moves off of it.
	 * PRECONDITIONS: Label on canvas, mouseListeners added.
	 * POSTCONDITIONS: Label will display coordinates of the mouse and will
	 * be blue unless the mouse is currently touching it (at which point it 
	 * will turn red).
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject touchedLabel =  getElementAt(mouseX, mouseY);
		if(touchedLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
