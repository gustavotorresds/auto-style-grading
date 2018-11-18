/*
 * File: MouseReporter.java
 * Name: Ryan Tran 
 * Section Leader: Vineet Kosaraju 
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * 
 * There will be a label on the left side of the screen that will display the x and y coordinate of the mouse as it moves across the "canvas". If mouse is touching the label, the label will turn red. Otherwise, the label will be blue.
 * Precondition: Before mouse moves onto the screen, label will read (0,0) and will be blue.
 * Postcondition: As soon as mouse is on the screen, the label will read the mouse's current coordinates. As mouse continues to move, label will update new coordinates of mouse. The label will change color to red if mouse is touching label; otherwise, it will be blue. Program does not terminate.
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
		
		// adds mouse listeners to allow for program to register mouse actions!
		addMouseListeners();
	}
	
	/** mouseMoved - Actions as mouse moves. Makes label display the current coordinates of the mouse, as well as change colors depending on whether or not the mouse is on the label. Label will change to red, if mouse is on label, but will stay blue otherwise.
	 *  Precondition: Mouse listeners are added. (In the other public void function, before mouseMoved is called, label has already been created with set font and colors and added to the screen.)
	 *  Post condition: Label now changes colors based on location of mouse; if mouse is on label, label becomes red, if not, label stays blue. Label also displays coordinates of mouse as mouse moves to different locations.
	 */ 	  
	public void mouseMoved(MouseEvent e) {
	//Finds and tracks x and y location of mouse as mouse moves. Label will then display the coordinates of the mouse. Label will also change color depending on whether mouse is on label or not.
		int xMouse = e.getX();
		int yMouse = e.getY();
		label.setLabel(xMouse + "," + yMouse);
		labelColor(xMouse,yMouse);
	}
	
	/** Checks to see if mouse is on label by checking to see if any objects are on the mouse's current location. If there is, label will turn red. If not, label will be blue.
	 *  Precondition: Label is blue at all times, and never turns red.
	 *  Postcondition: After code is implemented, label now turns red if mouse is on the label; otherwise, label will be blue.
	 */  
	private void labelColor(int xMouse,int yMouse) {
		GObject obj = getElementAt(xMouse,yMouse);
		if (obj != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
	
}
