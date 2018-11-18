/*
 * File: MouseReporter.java
 * Walter "Teke" Dado
 * Ruiqi Chen
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		//add mouseListener
		addMouseListeners();
	}
	/* This method constantly tracks x and y location, returning
	 * them to setLabel. If there is an 'element' at the location of the
	 * mouse (i.e. the label), then the label turns red. otherwise it goes
	 * back (needed a if/else statement because while loop would freeze it
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		boolean onLabel = getElementAt(mouseX, mouseY) != null;
		if(onLabel == true) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}