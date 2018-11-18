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
	private boolean isTouching = false;

	public void run() {	

		addMouseListeners();

		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);


		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	//Method that changes label to indicate the X and Y position of the mouse 

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);

		//Checks if mouse is touching label
		if(mouseX > INDENT && mouseX < INDENT + label.getWidth() && mouseY > getHeight()/2 - label.getAscent() && mouseY < getHeight()/2) {
			isTouching = true;
		}
		else {
			isTouching = false;
		}

		//Paints label red when mouse touches it
		if(isTouching) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
