/*
 * File: MouseReporter.java
 * ------------------------
 * Name: Thea
 * Section Leader: Rhea

 * MouseReporter is a GraphicsProgram that creates a label on the
 * left side of the screen. When the mouse is moved, the label is
 * updated to display the current x, y location of the mouse.
 * If the mouse is touching the label it turns red, otherwise it
 * is blue.
 * 
 * Sources:
 * Lecture slides and the Java textbook were used to understand and 
 * implement variables, methods, parameters, statements, events, and 
 * control flow, as well as correct syntax.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// an empty label whose scope is the entire program
	private GLabel label = new GLabel("");
	// a constant for the x value of the label
	private static final int INDENT = 20;

	public void run() {	
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	/*
	 * Method: Mouse Moved
	 * ------------------------
	 * The mouseMoved method is called any time the mouse moves in the
	 * program screen. The x, y location of the mouse is passed into
	 * the setLabel method as parameters as represented by mouseX and
	 * mouseY. The label then displays the mouse coordinates on the 
	 * screen. getElementAt receives mouseX and mouseY to check whether 
	 * or not the mouse is on the label. If the mouse is not on the
	 * label, it returns null and the label is blue. If the mouse is
	 * on the label, the label turns red. 
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		// displays the x and y coordinates of the mouse on the screen
		label.setLabel(mouseX + "," + mouseY);
		// sets color blue or red depending on whether the mouse is on the label
		if(getElementAt(mouseX, mouseY) == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}
}