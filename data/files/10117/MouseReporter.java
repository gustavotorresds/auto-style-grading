/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Britney Armstrong
 * Section Leader: Maggie Davis 
 * 
 * Displays the location of the mouse through a label. 
 * The display label changes its color to red when touched 
 * by the mouse. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// Label is defined as instance variable 
	private GLabel label = new GLabel("");
	
	public void run() {	
		developLabel();
		add(label, INDENT, getHeight()/2.0);
		addMouseListeners();
	}
	
	/* Method: developLabel
	 * --------------------
	 * Develops the necessary characteristics of the instance 
	 * variable, label. 
	 */
	private void developLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
	}
	
	/* Method: Mouse Moved
	 * ------------------
	 * Is called whenever the mouse moves on the program screen.
	 * Gets the coordinates of the mouse and displays their
	 * value on the screen. 
	 */

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		changeColor(x,y);
	}

	/* Method: changeColor
	 * -------------------
	 * Changes the color of the label if the mouse is touching 
	 * it.
	 */
	private void changeColor(int x, int y) {
		GObject possibleLabel = getElementAt(x,y);
		if (possibleLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
