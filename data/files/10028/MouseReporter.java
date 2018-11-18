/*
 * File: MouseReporter.java
 * ------------------------
 * Name: Eric Brandon Kam
 * Section Leader: Avery Wang
 * 
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;
	
	// visible to the run and mouse moved methods
	private GLabel label = new GLabel("");
	
	// adds label with coordinates of (0, 0) to the screen
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + ", " + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	// the coordinates on the label change according to the location of the mouse
	// the label turns red when the mouse touches it
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + ", " + mouseY);
		GLabel obj = getElementAt(mouseX, mouseY);
		if (obj == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
