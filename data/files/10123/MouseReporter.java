/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// Instance variable instantiation
	private GLabel label = new GLabel("");
	private GObject gobj;
	
	public void run() {	
		addLabel();
		addMouseListeners();
	}
	
	private void addLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.RED);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}

	/*
	 * Method: mouseMoved
	 * -----
	 * The following method checks the new position of the mouse and sets the label accordingly. It also
	 * checks if an object (the label) is at the mouse pointer's position -- if the label is there, then
	 * the label is set to the color red, and if not, it is set to blue.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		gobj = getElementAt(mouseX, mouseY);
		if(gobj != null) {
			label.setColor(Color.RED);
		} else if (gobj == null) {
			label.setColor(Color.BLUE);
		}
		
	}
	
	
	


}
