/*
 * Name: Kaila Kim
 * Section leader: Chase Davis
 * File: MouseReporter.java
 * -----------------------------
 * The program outputs the location of the mouse to a label on the
 * screen, and it changes the color of the label to red when
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
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/*
	 * When the mouse moves, the program will update the numbers shown
	 * by the label (and the color if the mouse is on top of the label).
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX(); //gives the x-coordinate of the mouse's position
		int y = e.getY(); //gives the y-coordinate of the mouse's position
		if (getElementAt(x, y) == label) { //if mouse is over the label, the label will turn red
			label.setColor(Color.RED);
		}
		label.setLabel(x + "," + y); 
	}
	


}
