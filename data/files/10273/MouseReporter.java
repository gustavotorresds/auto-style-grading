/* Name: Gene Tanaka
 * Section Leader: Peter Maldonado
 * File: MouseReporter.java
 * -----------------------------
 * The program outputs the location of the mouse to a label on the
 * screen. The label becomes red when the mouse touches it. This
 * is a very straightforward program.
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
	double mouseX;
	double mouseY;
	
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
	
	/* This method obtains the position of the mouse using mouseMoved, and creates
	 * a label that displays the coordinates of the mouse. When the mouse hovers over
	 * the label, the label changes color from blue to red.
	 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject hover = getElementAt(mouseX,mouseY);
		if (hover != null) {
			label.setColor(Color.RED); 
		} else {
			label.setColor(Color.BLUE);
		}
	}
	


}
