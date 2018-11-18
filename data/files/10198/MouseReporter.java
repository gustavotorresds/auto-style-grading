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

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");

	public void run() {	
		//changes label font and size
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		//adds the label to the screen
		add(label, INDENT, getHeight()/2);

		//adds mouse listeners
		addMouseListeners();
	}

	//every time the mouse moves, runs this method
	public void mouseMoved(MouseEvent e) {
		//sets int x equal to the x coordinate of the mouse
		int x = e.getX();
		//sets int y equal to the y coordinate of the mouse
		int y = e.getY();
		//makes label reflect location of the mouse
		label.setLabel(x + "," + y);
		//detects if the mouse is touching the label
		GObject detectLabel = getElementAt(x, y);
		//if the mouse is touching, sets label to red
		if(detectLabel != null) {
			label.setColor(Color.RED);
		//if mouse isn't touching label, label turns to blue
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
