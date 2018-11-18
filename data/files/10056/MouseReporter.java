
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
		createLabel();
		addMouseListeners();
	}

	private void createLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight() / 2);
	}

	public void mouseMoved(MouseEvent e) {
		int MouseX = e.getX();
		int MouseY = e.getY();
		label.setLabel(MouseX + "," + MouseY);
		updateLabelColor(MouseX, MouseY);
	}

	private void updateLabelColor(int x, int y) {
		GObject labelPresent = getElementAt(x, y);
		if (labelPresent == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}

	}

}
