
/*
 * File: MouseReporter.java
 * -----------------------------
 * This program displays the x and y coordinates of the mouse, updating it as the
 * user moves the mouse. Furthermore, this program changes the color of the label 
 * from blue to red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;
import acm.program.GraphicsProgram;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	//Instance variables for the location of the mouse
	private double mouseXCoord;
	private double mouseYCoord;
	
	private GLabel label = new GLabel("");

	public void run() {
		label.setFont("Courier-24"); //Sets the font of the label 
		label.setColor(Color.BLUE); //Sets the color of the label
		addMouseListeners();
	}

	//Keeps track of when the mouse is moves, where it's moved to, displays this location
	//as a glabel, changes the color of the label is the mouse is on top of it
	public void mouseMoved(MouseEvent e) {
		mouseXCoord = e.getX(); //Gets the x coordinate of the mouse
		mouseYCoord = e.getY(); //Gets the y coordinate of the mouse
		GObject test = getElementAt(mouseXCoord, mouseYCoord);
		if (test != null) { //Says that if there is an object at the x and y coordinate of the mouse... 
			label.setColor(Color.RED); //change the label color to red 
		} else {
			label.setColor(Color.BLUE); //else the label should be blue 
		}
		label.setLabel(mouseXCoord + "," + mouseYCoord);
		add(label, INDENT, getHeight() / 2); // add the label to the screen!
	}

}
