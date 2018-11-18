/*
 * File: MouseReporter.java
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

	private GLabel label;

	public void run() {	
		addMouseListeners();	
		addLabel();
	}

	/* This method creates label and adds it to the screen*/
	private void addLabel() {
		label= new GLabel("");
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}

	/* This tracks mouse locations and prints it in the GLabel
	 * If mouse touches label, label turns red
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY(); 			
		label.setLabel(x + "," + y);
		if(getElementAt(x, y) != null) {
			label.setColor(Color.red);
		}else {
			label.setColor(Color.BLUE);
		}
	}
}
