/*
 * File: MouseReporter.java
 * -----------------------------
 * This program displays the x and y location of the mouse with
 * a label that is blue unless the mouse is touching the label.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This label is visible to the entire program
	// It is called an instance variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		// Allow the program to notice mouse movements.
		addMouseListeners();
		// Set font of label 
		label.setFont("Courier-24");
		// Add label to the screen
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e){
		// Record the x and y positions of the mouse
		int x = e.getX();
		int y = e.getY();
		// Set the label coordinates to match coordinates of mouse
		label.setLabel(x + "," + y);
		GLabel maybeALabel = getElementAt(x,y);
		// If mouse touches the label, it is red. Otherwise it is blue.
		if (maybeALabel != null){
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
