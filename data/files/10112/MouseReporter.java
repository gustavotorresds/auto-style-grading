/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Amelia O'Donohue
 * Section Leader: Marilyn Zhang
 * 
 * This program shows the location of the mouse on the screen in a set label.
 * The label is blue, but when the mouse touches the label, the mouse turns
 * red.
 * 
 * Sources: Lecture examples, Java library
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
	private GLabel label = null;
	
	public void run() {	
		label = makeLabel();
		addMouseListeners();
		}	

	/*
	 * This method makes the label.
	 */
	private GLabel makeLabel() {
		GLabel label = new GLabel ("X + Y", INDENT, getHeight()/2);
		return label;
	}

	/*
	 * This method finds the mouse X and Y values and then check if there is a label
	 * at the mouse location. If there is a label there, the label is red. Otherwise, the
	 * label is blue. The label is then added to the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		// finds mouse X and Y
		int mouseX = e.getX();
		int mouseY = e.getY();
		// sees if there is a label at mouse location. 
		GLabel maybeLabel = getElementAt(mouseX, mouseY);
		// if there is a label at mouse location, label is red
		if (maybeLabel!= null) {
			label.setColor(Color.RED);
		}
		label.setLabel(mouseX + "," + mouseY);
		add (label, INDENT, getHeight()/2);	
	}
}
