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
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	/*
	 * Connects the run method with mouse events.
	 */
	public void run() {	
		addMouseListeners();
	}

	/*
	 * Adds a label that prints whatever the mouse's x and y coordinates are. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mousex=e.getX();
		double mousey=e.getY();
		label.setLabel(mousex + "," + mousey);
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		//Makes the label BLUE by default but turn RED if
		//mouse hovers over it. 
		if(getElementAt(mousex,mousey)==label){
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}

