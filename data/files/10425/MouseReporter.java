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
		addMouseListeners();
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
	
	}
	
	public void mouseMoved(MouseEvent e) {
		//gets x and y location of mouse to then be used for label values
		double mouseX = e.getX();
		double mouseY = e.getY();
	
		//creates label using mouse locations
		label.setLabel(mouseX + "," + mouseY);
		//places label in desired location on left of screen
		add(label,  INDENT, getHeight()/2);
		//establishes new "label" that exists at 
		GLabel text = getElementAt(e.getX(), e.getY());
		if(text != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		}


}
