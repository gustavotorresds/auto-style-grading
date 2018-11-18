/*
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen. The color changes from blue (when the mouse is not on the 
 * location coordinates) to red when the mouse touches the coordinates.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	private GLabel label; 
	
	public void run() {
		label = new GLabel("");
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved (MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x >= label.getX() && x <= label.getX() + label.getWidth() && y <= label.getY() && (y >= label.getY() - label.getHeight())) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		label.setLabel(x + ", " + y);
	}
}



























