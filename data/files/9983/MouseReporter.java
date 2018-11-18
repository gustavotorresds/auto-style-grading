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
	
	/**As the goal of Mouse Report is to show the mouse coordenates, everything will "happen" in the Mouse move method
	 *  
	 */
	public void run() {	
		addMouseListeners();
	} 
	
	public void mouseMoved(MouseEvent e) {
		// The if below will define the color of the Label
		GObject obj = getElementAt(e.getX(), e.getY());
		if (obj == label) {
			label.setColor(Color.RED);
			}
		else {
			label.setColor(Color.BLUE);
			}
		
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
				
		// this setLabel method takes in a "String" 
		// the label will show the coordinates of the mous 
		label.setLabel(e.getX() + "," + e.getY());
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
}
