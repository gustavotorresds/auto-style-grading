/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * 
 * Terence Theisen
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
	
	//Colors for when the mouse is on or off of the label
	private static Color COLOR_ON_LABEL = Color.RED;
	private static Color COLOR_OFF_LABEL = Color.BLUE;
			
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(COLOR_OFF_LABEL);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		//1 add MouseListeners
		addMouseListeners();
	}
	
		//this method displays the x and y coordinators of a mouse when it is moved
	
	
	//2 do things
	//when the mouse is moved the label is made to show the x and y values of the mouse
	//the checkToChangeColor method is used to add function of changing label color
	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		label.setLabel(mx + "," + my);
		checkToChangeColor(mx, my);
	}

	/*
	 * this method takes in any input of the mouse coordinates
	 * and will check to see if an object is at the mouse coordinates
	 * if it is then the object with change colors while the mouse
	 * is on it
	 */
	private void checkToChangeColor(int mx, int my) {
		GObject obj = getElementAt(mx, my);
		if (obj != null ) {
			label.setColor(COLOR_ON_LABEL);
		} else {
			label.setColor(COLOR_OFF_LABEL);
		}
	}
}