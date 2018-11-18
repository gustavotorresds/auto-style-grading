/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;


//The following program will report the x and y coordinates of the mouse on the canvas.
//If the mouse hover overs the text that reports the location the text will change from the color blue to red.
public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");


	public void run() {	
		addMouseListeners();

		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel( 0 + "," + 0);

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	/*This element finds out the X and Y position of the mouse.
	 *If the mouse is over the label it will turn red and back to blue 
	 *if it is not over the label.
	 */
	public void mouseMoved(MouseEvent e)	{
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		if (getElementAt(mouseX,mouseY)==label)	{
			label.setColor(Color.RED);
		} 
		else {
			label.setColor(Color.BLUE);
		}	
	}
}
