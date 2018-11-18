/*
 * File: MouseReporter.java
 * Name: Simon Qin
 * Section Leader: Julia Daniel
 * -----------------------------
 * This warm-up program outputs the location of the mouse to a label on the
 * screen, and changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;
	private GLabel label = new GLabel("");

	public void run() {	
		addLabel();				//Step 1. Add the label.
		addMouseListeners();		//Step 2. Make the label change copy and color based on mouse location.
	}

	/* This method addLabel sets the font and color of instance variable "label" and adds it.
	 * Pre: No label on screen
	 * Post: Blue label on screen left margin = INDENT, vertically centered.
	 */
	private void addLabel() {
		label.setFont("Courier-24");	
		label.setColor(Color.BLUE);	
		add(label, INDENT, getHeight() / 2);
	}
	
	/* This method mouseMoved initiates a mouse event. 
	 * The method tracks the mouse's location, and if the object at that location
	 * is the label, then it sets the label's color to red, else flips it back to blue.
	 * Additionally, every time the mouse is moved it sets label to the mouse position.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		GObject hover = getElementAt(mouseX, mouseY);
		if(hover == label) {						// This sets hover color behavior.
			label.setColor(Color.RED);
		} else { 
			label.setColor(Color.BLUE);
		}
		label.setLabel(mouseX + "," + mouseY);	// This sets the label copy to the mouse position.

	}

}


