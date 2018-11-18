/*
 /*
 * File: MouseReporter.java
 * Name: Dan Shevchuk
 * Section Leader: Ella Tessier-Lavigne
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
	public static final int INDENT = 20;
	
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
		
		// add the label to the screen.
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
		
	}
	
	//Creates a mouse event. Every time the mouse moves, the text label reflects the coordinates
	//of the mouse. If the mouse is touching the label, the label text turns red. Otherwise,
	//it remains blue.
	public void mouseMoved (MouseEvent e) {
		int cx = e.getX();
		int cy = e.getY();
		label.setLabel(cx + "," + cy); 
		GLabel maybeALabel = getElementAt(cx, cy);
		if (maybeALabel != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}