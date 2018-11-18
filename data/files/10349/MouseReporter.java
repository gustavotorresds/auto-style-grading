/*
 * File: MouseReporter.java
 * Name: Chinedum Egbosimba
 * Section Leader: Marilyn Zhang
 * Date: Last Modified Feb 3 2018
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
	private GLabel label = new GLabel("");
	
	public void run() {	
		//add mouse listeners.
		addMouseListeners();
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	public void mouseMoved (MouseEvent e) {
		//find x and y location of mouse
		int x = e.getX();
		int y = e.getY();
		
		//pop values for location into label
		label.setLabel(x + "," + y);
		
		GLabel maybeOurLabel = getElementAt(x,y);
		
		//check if we our on the label and change to red if we are
		if (maybeOurLabel != null) {
			label.setColor(Color.RED);
		}
		//change back to blue if we aren't
		else {
			label.setColor(Color.BLUE);
		}
		
	}


}
