/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Hikaru Hotta
 * Section Leader: Meng Zhnag
 * File purpose: Implements Sand Castle
 * Sources: N/A
 * 
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
	
	/*
	 * Outputs the location of the mouse to a label on the screen and changes the 
	 * color of the label to red from blue when the mouse touches it
	 * 
	 * Pre: Empty window
	 * Post: Implements Sand Castle
	 */
	public void run() {	
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/*
	 * Sets label as coordinate of mouse on window and changes the color of the 
	 * label to red from blue when the mouse touches it
	 * 
	 * Pre: Empty Window
	 * Post: Sets label as mouse coordinate and changes color of label to 
	 * red when the mouse touches it
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		// this setLabel method takes in a "String" 
		label.setLabel(mouseX + "," + mouseY);
		// determines object at mouse
		GObject obj = getElementAt(mouseX, mouseY);
		// turns label red when mouse is on labels
		if (obj == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
