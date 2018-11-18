/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import acm.graphics.*;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
		
	public void run() {	
		
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);		
		colorLabel(x, y);
	}
	// label turns red on mouseover
	private void colorLabel(int x, int y) {
		GObject obj = getElementAt(x, y);
		if(obj != null) {
			obj.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
