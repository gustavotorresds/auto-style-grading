/* Isabel Wang, izwang, 006177443
 * Ben Allen
 * 
 * File: MouseReporter.java
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final double INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		// this code adds the label to the screen
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		double x = INDENT;
		double y = getHeight() / 2 - label.getAscent() / 2;
		add(label, x, y);
		
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		// detects mouse movement to update the label
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		
		// changes the color of the label if there is an object
		if (getElementAt(mouseX,mouseY) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
	


}
