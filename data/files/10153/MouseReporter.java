/* File: MouseReporter.java
 * Name: Cosima Justus
 * Section Leader: Niki
 * -----------------------------
 * The MouseReporter subclass displays the location of the mouse to a label on the
 * screen. The label's color changes to red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// Instance variable label
	private GLabel label = new GLabel("");
	
	
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		//checks whether mouse is hovering over label
		GObject obj = getElementAt(e.getX(), e.getY());
		//if label is there
		if (obj != null) {
			label.setColor(Color.RED);
		}
		//any other place
		else label.setColor(Color.BLUE);
		label.setLabel(e.getX() + "," + e.getY());
	}
}
