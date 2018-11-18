/*
 * File: MouseReporter.java
 * Name: Jonathan Jean-Pierre
 * Section Leader: Ella Tessier-Lavigne
 * ----------------------
 * The MouseReporter class extends the GraphicsProgram class to output the location of the mouse to a label
 * on the screen. The label is blue but turns red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable visible to entire program
	private GLabel label = new GLabel("");

	public void run() {
		addMouseListeners();
		label.setFont("Courier-24");
		add(label, INDENT, getHeight() / 2);
	}

	public void mouseMoved(MouseEvent e) {
		// changes label to display the current x,y location of the mouse
		label.setLabel(e.getX() + "," + e.getY());

		// changes color of the label depending on if the mouse is touching it
		if (getElementAt(e.getX(), e.getY()) != null) {
			label.setColor(Color.red);
		} else {
			label.setColor(Color.blue);
		}
	}
}
