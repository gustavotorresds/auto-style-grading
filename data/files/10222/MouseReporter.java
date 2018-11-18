/*
 * File: MouseReporter.java
 * Name: Danielle Tang
 * Section Leader: Semir Shafi
 * -----------------------------
 * This program follows the location of the mouse
 * and writes its coordinates on the left side of
 * the screen as it moves around. If the mouse
 * touches the label, the label should turn red.
 * Otherwise, it remains blue.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// This is the x value of the label.
	private static final int INDENT = 20;
	
	private GLabel label = new GLabel("");
	
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		// Checks if mouse is touching the label
		GObject onLabel = getElementAt(x, y);
		if (onLabel != null) {
		label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
