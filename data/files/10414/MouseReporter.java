
/*
 * File: MouseReporter.java
 * -----------------------------
 * A program that outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
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

	public void run() {
		
		label.setFont("Courier-24");

		addMouseListeners();

		// adds the label to the screen!
		add(label, INDENT, getHeight() / 2);

	}

	public void mouseMoved(MouseEvent e) {

		double mouseX = e.getX();
		double mouseY = e.getY();

		label.setLabel(mouseX + "," + mouseY);
		GObject obj = getElementAt(mouseX, mouseY);
		if (obj == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}

	}

}
