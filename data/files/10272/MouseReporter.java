/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Aron Nunez
 * Section Leader: Akua McLeod
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it. This is a starter test.
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
		//label.setFont("Courier-24");
		//label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		// label.setLabel(mouseX + "," + mouseY);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
		/* This method calls the mouseMoved feature and gets the coordinates
		 * returned by moving the mouse. If there is a label at the 
		 * coordinate, then the label is set to be colored Red. Otherwise,
		 * it is blue. 
		 * (non-Javadoc)
		 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved (MouseEvent e) {
			double mouseX = e.getX();
			double mouseY = e.getY();
			label.setLabel(mouseX + "," + mouseY);
			label.setFont("Courier-24");
			label.setColor(Color.BLUE);
			GLabel label = getElementAt(mouseX, mouseY);
			if (label != null) {
				label.setColor (Color.RED);
			}
		}
		
	}
