/*
 * File: MouseReporter.java
 * -----------------------------
 * 
 * Name:Joshua Chang
 * Section Leader:Esteban
 * 
 * This file outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// Indent for the x position of the label on the screen
	private static final int INDENT = 20;

	//INSTANCE VARIABLES
	private GLabel label = new GLabel("");

	public void run() {	
		//creates a label and sets its font, size, and color
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// adds the label to the screen
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
	}

	/* Sets the label to display the x and y position of the mouse. 
	 * Pre: Label is created
	 * Post: Label displays x and y position of mouse, and turns red if mouse is over the label.
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + " , " + y);			
		makeRed(x,y);
	}

	/* Changes the color of the label to red if the mouse is over the label.
	 * Pre: Mouse is over the label.
	 * Post: Label is red.
	 */
	private void makeRed(double x, double y) {
		if (getElementAt(x,y) == label) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}

	}




}

