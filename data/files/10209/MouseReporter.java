/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Tanya Watarastaporn
 * Section Leader: Nidhi Manoj
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it. Otherwise the label is blue.
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/**
	 * Method mouse Moved
	 * ---------------------------------
	 * When the mouse is moved, the label found on the left side of
	 * the screen will display the x and y coordinates of the mouse. If
	 * the mouse scrolls over the label, the label will turn red. Otherwise,
	 * the label will default to blue.
	 */
	public void mouseMoved(MouseEvent e)	{
		int mouseX = e.getX();
		int mouseY = e.getY();
		String mouseX1 = Integer.toString(mouseX);
		String mouseY1 = Integer.toString(mouseY);
		label.setLabel(mouseX1 + " , " + mouseY1);
		if(getElementAt(mouseX, mouseY) != null)	{
			label.setColor(Color.RED);
		} else	{
			label.setColor(Color.BLUE);
		}
	}
	
	

}
