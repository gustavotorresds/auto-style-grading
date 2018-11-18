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
	
	public void run() {	
		// create the default string settings. 
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// create the default string "0,0".
		label.setLabel(0 + "," + 0);
		
		// add the label to the left side of the screen.
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		// show the mouse location in blue when mouse is not pressed.
		label.setLabel (e.getX()+" , "+ e.getY());
		label.setColor(Color.BLUE);
		add(label, e.getX(), e.getY());
	}
	
	public void mousePressed (MouseEvent e) {
		// show the mouse location in red when mouse is pressed.
		label.setLabel(e.getX()+ ", "+ e.getY());
		label.setColor(Color.RED);
		add(label, e.getX(), e.getY());
	}
}
