/*
 * File: MouseReporter.java
 * Name: Ryan Wu
 * SL: James Mayclin
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
	
	/*
	 * This method adds mouse listeners and sets the label.
	 * Pre: Empty program
	 * Post: The label is set with a font, color, and location. However,
	 * it still has no functionality.
	 */
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}
	/*
	 * This method gets the mouse's location in the X and Y coordinates
	 * and changes the label's color from blue to red if the mouse touches it.
	 * Pre: The label is set with the font, location, and color, but does
	 * not react to mouse movements.
	 * Post: When the mouse hovers over the label, the label's color changes
	 * from blue to red. It detects this by getting the location of the label's
	 * location constraints and detecting if the mouse's location is equal. 
	 */
	public void mouseMoved(MouseEvent e) {
		int X = e.getX();
		int Y = e.getY();
		label.setLabel(X + "," + Y);
		double height1 = getHeight()/2 - label.getHeight()/2;
		double height2 = getHeight()/2 + label.getHeight()/2;
		if (e.getX() >= INDENT && e.getX() <= label.getWidth() + INDENT) {
			if ((e.getY() >= height1) && (e.getY() <= height2)) {
				label.setColor(Color.RED);
			} else {
				label.setColor(Color.BLUE);
			}
		} else {
			label.setColor(Color.BLUE);
		}
	}
	


}
