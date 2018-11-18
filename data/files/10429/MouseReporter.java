/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * 
 * I looked at the sample code in FIGURE 10-4 from 
 * The Art and Science of Java (page 359).
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
		//Set Label Data and formatting
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		label.setLabel(x + ", " + y);
		
		if (label.contains(x,y)) {	
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
