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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	private GLabel label = new GLabel("");
	
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.RED);
		label.setLabel("0 , 0" );
		add(label, INDENT, getHeight()/2);
	}
	
	//when mouse is moved the numbers in the label is updated with the cursor 
	// coordinates in the window
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x + "," + y);
		GObject obj = getElementAt(e.getX(), e.getY());
		if (obj == label) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}
}
