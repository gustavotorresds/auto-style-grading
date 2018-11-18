/*
 * File: MouseReporter.java
 * -----------------------------
 * This program displays the location of the mouse on a label on the
 * screen. It changers the color of the label to red from blue when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for GLabel called "label."
	private GLabel label = new GLabel("");

	public void run() {	
		// This code adds the label to the screen and adds Mouse Listeners.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	//This method tracks the mouse and displays its x-y coordinates on the screen on a colored label
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GLabel possibleLabelLocation = getElementAt (mouseX, mouseY);
		//Changes the color of the label if the mouse is hovering over it//
		if (possibleLabelLocation != null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	}

}
