/*
 * * Name: Sun (Woo) Lee
 * Section Leader: Maggie Davis
 * 
 * 
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

	public void run() {	
		addMouseListeners(); 
		addlabel();
	}
	/*this adds the label*/
	private void addlabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		//makes label display x and y coordinates of the mouse
		label.setLabel(x + "," + y);
		//if mouse is on the label, then label will turn red 
		if (label == getElementAt(x, y)) {
			label.setColor(Color.RED);
		}
		//make label blue again when mouse is not on label
		else {
			label.setColor(Color.BLUE);	
		}
	}
}


	
	


