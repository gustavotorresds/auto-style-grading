/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Cam Burton
 * TA: Akua McLeod
 * This program creates a Label which tells the x and y location of the mouse on the screen
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
	private GLabel label = new GLabel( "" );

	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();


		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	// This method sets the Label and changes the color when the mouse is hovered over it
	public void mouseMoved (MouseEvent e)	{
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x+","+y);
		getElementAt(x,y);
		if (getElementAt(x,y) ==label )	{
			label.setColor(Color.RED);
		}
		else {
			label.setColor(color);
		}

	}



}
