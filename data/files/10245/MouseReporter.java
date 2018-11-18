/*
 * File: MouseReporter.java
 * -----------------------------
 * Name:Leonardo Orsini
 * Section Leader:Niki Agrawal
 * 
 * This program outputs the location of the mouse to a label on the
 * screen. It changes the color of the label to red when
 * the mouse touches it.
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
		addMouseListeners();		//in order to respond to mouseEvent
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();		// gets x coordinate of where mouse moves
		double y = e.getY();		// gets y coordinate of where mouse moves
		label.setLabel(x + "," + y);	//updates x and y coordinates
		GLabel mouse = getElementAt(e.getX(), e.getY());
		if(mouse == label) {		// when mouse is over coordinate label, turns red
			label.setColor(Color.RED);
		}
		if(mouse == null) {		// when mouse is anywhere besides coordinate label, turns blue
			label.setColor(Color.BLUE);
		}
	}
	


}
