/* Name: Kara Eng
 * TA: Ben Allen
 * File: MouseReporter.java
 * -----------------------------
 * displays in blue the location of the mouse wherever it is on the console. displays in red when the mouse is over the label
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		//adds the mouse listener
		addMouseListeners();
		
		
	}
	
	public void mouseMoved(MouseEvent e) {
		//changes the x and y according to where the mouse is 
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		
		GObject mayhapsLabel = getElementAt(x,y);
		
		//checks to see if you're over the label
		if (mayhapsLabel !=null) {
			label.setColor(Color.RED);
		}
		//changes the color back to blue so that once you leave the label you're back to normal 
		else {
			label.setColor(Color.BLUE);
		}
		
	}
	


}
