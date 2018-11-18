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

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	//Making instance variables for X and Y location 
	private static int mouseX = 0;
	private static int mouseY = 0;

	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();


		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel( 0 + "," + 0);
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
//This method moves the mouse over the label and the print the coordinates red when the mouse is over the label.  When the mouse is not it prints the coordinates of the mouse blue. 
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GLabel label1 = getElementAt(mouseX, mouseY);
		//making labels equal to each other and printing different colors when the mouse is at the original location or someplace else. 
		if(label1 == label) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}

	}

}






