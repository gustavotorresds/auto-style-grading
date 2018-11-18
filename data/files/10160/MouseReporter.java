/*
 * File: MouseReporter.java

 * Name: Anna-Luisa Brakman
 * Section Leader: Julia Daniel

 * -----------------------------
 * This file runs a program that outputs the location 
 *of the mouse to a label on the screen. The color of
 *the label changes from blue to red when the mouse
 *touches it.
 *
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// This constant defines an x value of the label
	private static final int INDENT = 20;

	//This instance variable initializes the label
	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24"); //font of label
		label.setColor(Color.BLUE); //color of label
		label.setLabel(0 + "," + 0); //initializes label at 0,0
		add(label, INDENT, getHeight()/2);
		addMouseListeners(); //adds mouse listeners, which leads to calling of mouseMoved method
	}

	/* This method updates the x and y coordinate of the label to the 
	 * current mouse location and sets the label as red when the mouse
	 * is over it
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX(); // x value of mouse
		int mouseY = e.getY(); //y value of mouse
		label.setLabel(mouseX + "," + mouseY); //updates label as x and y value of mouse
		GLabel maybeALabel = getElementAt (mouseX, mouseY); //tests if there is an object where the mouse is touching
		if (maybeALabel != null) {
			label.setColor(Color.RED);	
		} 
		else {
			label.setColor(Color.BLUE);
		}
	}
}
