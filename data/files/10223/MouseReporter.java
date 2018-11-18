/*
 * File: MouseReporter.java
 *  * Name: Carson Conley
 * Section Leader: Vineet Kosaraju
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label.
	private static final int INDENT = 20;

	// An instance variable for the label. 
	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}
	/*
	 * This method changes the label to display the mouse coordinates
	 * and changes the label to red if the mouse is over it. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject object = getElementAt(mouseX, mouseY);
		if (object == label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
