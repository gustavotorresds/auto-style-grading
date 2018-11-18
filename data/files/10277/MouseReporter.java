/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Kendra Dunsmoor
 * Section Leader: Garrick Fernandez
 * Date: 2/7/18
 * -----------------------------
 * This file outputs the location of the mouse to a label on the
 * screen. It changes the color of the label to red when
 * the mouse touches it and back to blue when it is not.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// creates label as a private instance variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y); // changes label to show location of mouse
		GObject maybeLabel = getElementAt (x,y); //looks for object at mouse location
		if (maybeLabel != null) {
			label.setColor(Color.red); //if on label makes label red
		}
		else {
			label.setColor(Color.blue); // if not on label makes label blue
		}
	}
	


}