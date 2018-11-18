/*Name: Jeffrey Propp
 * Section Leader: Peter Maldonado
 * File: MouseReporter adds a label to the screen that lists the x and y location
 * of the mouse. When the mouse is over the label, the label turns from blue to red.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
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
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
		
	}
	/*Reads in the x and y location of the mouse. If there is an element (label)
	 * at that location, it sets the color to red. Otherwise, it's blue.
	 * It also prints the current location of the mouse.
	 */
public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		GObject obj = getElementAt(x, y);
		if(obj!=null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		label.setLabel(x + "," + y);
	}
}
