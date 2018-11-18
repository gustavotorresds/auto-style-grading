/*
* File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * BY: Tyler Abramson
 * TA: Jordan
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
	private int XCord;
	private int YCord;
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(XCord + "," + YCord);
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	/*
	 * 
	 * The goal of this code is to take the position of the mouse and display it through the label
	 * I use the e.getX and e.getY to find the X and Y cordinates and than set the label equal to that.\
	 * I use an if statement to see if the mouse is touvhing the label and if it is I set the color to red
	 * if not I set the color to blue
	 */
	public void mouseMoved(MouseEvent e) {
		XCord = e.getX();
		YCord = e.getY();
		if(getElementAt(XCord, YCord) == label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		label.setLabel(XCord + "," + YCord);
	}
}
