/*
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen and changes the color of the label to red when
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
	// Instance variables 
	private GLabel label = new GLabel("");
	private int x;
	private int y; 
	
	

	/*
	 * Method: Run
	 * ----------------
	 * Program execution starts here.  
	 */
	public void run() {	
		addLabel();
		addMouseListeners(); 
	}
	
	

	/*
	 * Method: Mouse Moved 
	 * ----------------
	 * This method is implemented every time the mouse is moved. It
	 * detects whether the mouse is touching the label. If so, the color of the
	 * label turns red. 
	 */
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY(); 
		label.setLabel(x + "," + y);
		 
		GObject obj = getElementAt(e.getX(), e.getY()); 
		
		
		if(obj == label) {
			label.setColor(Color.RED);
		} else { 
			label.setColor(Color.BLUE);
		}
	}
	

	/*
	 * Method: Add Label 
	 * ----------------
	 * This method adds a blue label to the screen.  
	 */
	private void addLabel() {
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
}
