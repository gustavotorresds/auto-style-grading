/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Eddie Mattout 
 * Section Leader: Chase Davis
 * -----------------------------
 * MouseReporter displays the location of the mouse on a label in 
 * the middle of the screen. It changes the color of the label when
 * the mouse touches it. When the mouse is not touching the label, the label is blue. 
 * When the mouse touches the label, it turns red. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/*
	 * When the mouse moves on the canvas it displays the location of the mouse on
	 * the label on the middle of the screen. Once the mouse touches the label, it changes
	 * the label to the color red. 
	 */
	public void mouseMoved (MouseEvent e) {
		// Displays the location of the mouse on the label. 
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		// Changes the color of the label to red if the mouse is touching the label. 
		GLabel touchedLabel = getElementAt (mouseX, mouseY);
		if (label == touchedLabel) {
			label.setColor(Color.RED);
		} else {
			label.setColor((Color.BLUE));
		}
	}
	
	// Instance variable that is used by the entire program. 
	private GLabel label = new GLabel("");
}
