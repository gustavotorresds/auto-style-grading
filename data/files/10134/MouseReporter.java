/*
 * Name: Annie Minondo
 * Section Leader: James Zhuang
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
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
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	
	/* This program takes in the mouse's location (x, y) and displays it on the left side of the screen. When the mouse hovers over the label, it turns from blue to red.
	 * PRE: Blue label, at (0 , 0) at the left, center of screen.
	 * POST: Blue label (turns red if hovered over) displays (x, y) of mouse at any location. 
	 */
	
	public void run() {	
		addMouseListeners();
		// this code makes the starter label, setting its baseline value at (0, 0)
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		//Asks for (x , y) coordinates of mouse. 
		double mouseX = e.getX();
		double mouseY = e.getY();
		//Set label at mouse (x, y)
		label.setLabel(mouseX + " , " + mouseY);
		
		//This part of the method checks if there is an object at the mouse's location. 
		// If there is, it turns the label red. Otherwise, it remains or returns to being blue. 
		GObject check = getElementAt(mouseX, mouseY);
		if (check != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
