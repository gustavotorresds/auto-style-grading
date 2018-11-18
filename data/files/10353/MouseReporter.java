/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Andrew Bempah
 * Section Leader: Vineet Kosaraju 
 * The objective this program is to keep track of the coordinates of the mouse and 
 * have the label update to the current position and have the label update its color from blue to red when 
 * the mouse is overing over the label
 * 
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

	
	public void mouseMoved(MouseEvent e) {
		int xCoordinate =e.getX();
		int yCoordinate =e.getY();
		//I put the update label inside the mouse moved event so that whenever the mouse is tracked 
		// the coordinates can be passed to the label
		updateLabel(xCoordinate, yCoordinate);
		
	}

	/*
	 * 	PreCondition: The label has already been added to the canvas but the coordinates of the mouse have
	 * not been added to the label
	 * PostCondition: The coordinates(the text of the label) will be updated and the color will be updated if the mou
	 * mouse is hovering over that label.  
	 */
	private void updateLabel(int xCoordinate, int yCoordinate) {
		label.setLabel(xCoordinate + "," + xCoordinate);
		if(getElementAt(xCoordinate, yCoordinate)!=null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
		
	}


}
