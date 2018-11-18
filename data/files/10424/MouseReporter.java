/*
 * File: MouseReporter.java
 * -----------------------------
 * Christiana Lee // clee719@stanford.edu 
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

	//Instance variable because it is the main thing of this program and needs to be constantly updating. 
	private GLabel label = new GLabel("");

	public void run() {	

		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		//The start value on the label before the mouse starts moving. 
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);

		addMouseListeners(); 
	}

	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX(); 
		int mouseY = e.getY();
		//In order for the label to update the location of the mouse at all times. 
		label.setLabel(mouseX + "," + mouseY);
		//The following code is so that the label turns red when the mouse touches it. 
		GObject obj = getElementAt(mouseX, mouseY);
		if (obj != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
