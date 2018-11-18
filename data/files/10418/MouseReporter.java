/* Cami Katz
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
	
	// Adds mouse listeners, sets a label
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
	}
	
	// Checks to see if the mouse moved. Changes label to show coordinates of mouse. If mouse is on label, it turns red.
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject labelPresent = getElementAt(mouseX, mouseY);
		if (labelPresent == null) {
			label.setColor(Color.BLUE);
		}
		else {
			label.setColor(Color.RED);
		}
	}
}