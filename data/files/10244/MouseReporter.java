/*
 * File: MouseReporter.java
 * Name: Jennalei Louie
 * Section Leader: Chase Davis
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * 
 * Overview: 
 * A label was created to show the x, y coordinates of the mouse 
 * where if the value of the label was set to not null,
 * the label would be red (if the mouse was touching it).
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
		addMouseListeners();

		label.setFont("Courier-24");

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	//establishes a mouse event to display current x, y coordinates of mouse
	public void mouseMoved(MouseEvent e) {
		//creates coordinates of mouse as it moves
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		//sets the string of the command to be the coordinates of mouse
		label.setLabel(mouseX + "," + mouseY);
		//sets label to be blue as the mouse is moving if not on label
		label.setColor(Color.BLUE);
		
		//returns the value null if mouse is not on label
		GLabel label = getElementAt(mouseX, mouseY);
		
		//if mouse is on label (i.e. label is not null), 
		//then the color of the label is set to red
		if(label != null) {
			label.setColor(Color.RED);
		}
	}
	
}
