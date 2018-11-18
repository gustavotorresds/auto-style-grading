/*
 * File: MouseReporter.java
 * -----------------------------
 * Miso Kim
 * TA: Rachel Gardner
 * The program outputs the location of the mouse to a label on the
 * screen, and changes the color of the label to red when
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

	private GLabel label = new GLabel("");
	int mouseX;
	int mouseY;

	public void run() {	
		addMouseListeners();
		addLabel();
	}

	// Adds a label to the screen at a fixed location.
	private void addLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
	}
	
	// Tracks the mouse and displays its location in the label, and changes
	// color from blue to red if the mouse touches the label.
	public void mouseMoved (MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		if(getElementAt(mouseX, mouseY) == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
