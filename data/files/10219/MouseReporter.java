/*
 * File: MouseReporter.java
 * Name: Christina Ding
 * Section Leader: Semir

 * -----------------------------
 * Output the location of the mouse to a blue label on the
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
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
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
		int x = e.getX(); //get x location of mouse
		int y = e.getY(); //get y location of mouse
		if(getElementAt(x, y) != null) { //if element at mouse is not null (if mouse is on top of label)
			label.setColor(Color.RED); //set the label to red
		} else {
			label.setColor(Color.BLUE); //else set color to blue
		}
		label.setLabel(x + "," + y); //update cooardinates
	}

}
