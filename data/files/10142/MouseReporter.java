/*
 * File: MouseReporter.
 * Name: Sabrina Halper
 * Section Leader: Ella Tessier-Lavigne
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");

	//makes the x and y location of the mouse print on the label and changes the color to red if the mouse scrolls over the label
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		if (getElementAt(mouseX, mouseY) == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
	//creates font, color, format, and location for the label
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		label.setColor(Color.BLUE);
	}
}
