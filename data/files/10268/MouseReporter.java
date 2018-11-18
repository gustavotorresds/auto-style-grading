/*
 * File: MouseReporter.java

 * -----------------------------
 * This program finds the location of the mouse and prints it onto the label on the
 * screen. It changes the color of the label from blue to red if the mouse is
 * touching the label.  
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLocation(label.getX(), label.getY());
		label.setLabel(x + "," + y);
		GObject obj = label;
		// if the mouse is not touching the label it is blue, if so it is red
		if(getElementAt(x,y) == null) {
			label.setColor(Color.BLUE);
		}
		else if(getElementAt(x,y) != null) {
			label.setColor(Color.RED);
		}
	}
}




