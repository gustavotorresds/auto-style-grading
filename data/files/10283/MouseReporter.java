/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Jean-Akim Cameus
 * Section Leader: Maggie Davis
 * 
 * This files output the location of the mouse to a label on the
 * screen. It changes the color of the label to red when
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

	//These instance variables keep track of the location of the mouse.
	int mouseX, mouseY;

	/*
	 * This methods adds a label that shows the location of the mouse. 
	 */
	public void run() {	
		label = makeLabel();
		addMouseListeners();
	}

	/*
	 * This method sets the font and color of the label. It also sets 
	 * 'what the label displays, which is the location of the mouse.
	 * It also sets the location of the label and add it to the screen. 
	 */
	private GLabel makeLabel() {
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(mouseX + "," + mouseY);
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);	
		return label;
	}
	/*
	 * This method gets the location of the mouse as it moves 
	 * and sets it as mouseX. It also checks if there is any 
	 * object at the location of the mouse and turns them red 
	 * if there is any there. 
	 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		// This gets the object at the location of the mouse.
		GObject possibleObject = getElementAt (mouseX,mouseY);
		if (possibleObject != null) {
			label.setColor(Color.RED);

		} else {
			label.setColor(Color.BLUE);
		}
	}
}
