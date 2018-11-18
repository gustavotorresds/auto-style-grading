/*
 * File: MouseReporter.java
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label from blue to red when
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

	// Creates an instance variable visible through the program
	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners(); 					// add mouse listener to identify location of mouse
		createBlueLabel(); 						// creates a Blue label with string 0,0 on screen
	}

	private void createBlueLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}

	//Identifies the location of mouse and changes label accordingly
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();			 			//Find mouse x-coordinate			
		int y = e.getY();			 			//Find mouse y-coordinate
		label.setLabel(x + "," + y); 			//Modify label value to reflect mouse position

		GObject obj = getElementAt(x, y);
		if(obj == label) {
			label.setColor(Color.RED);			//If mouse points at the label, turn text color to Red
		} else {
			label.setColor(Color.BLUE);			//Else, turn text color to Blue
		}
	}

}
