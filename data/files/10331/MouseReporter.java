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
import acm.graphics.GPoint;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;



	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	private int lastX;
	private int lastY;
	private GObject gobj;

	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent e) {
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		lastX=e.getX();
		lastY=e.getY();
		gobj=getElementAt(lastX, lastY);
		label.setLabel(lastX + "," + lastY);
		if(gobj==label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}




