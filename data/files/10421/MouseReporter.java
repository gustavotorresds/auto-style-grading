/*
 * MouseReporter.java
 * ------
 * Name: Allan Zhao
 * Section Leader: Marilyn Zhang
 * ------
 * This program prints the position of the cursor
 * on the screen and changes the color of the label
 * based on whether or not the cursor is over the
 * label.
 * ------
 * Resources Used: lecture videos
 * ------
 * Preconditions: blank screen
 * Postconditions: the label prints the position of
 * 		the cursor, blue while not on the label and
 * 		red when on the label.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	private GLabel label = new GLabel("");
	
	public void run() {	
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
	
		label.setFont
		("Courier-24");
		
		//This sets the color of the label based
		//on whether or not the cursor is on the label.
		if (getElementAt(x,y) == null ) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
		
		//This next line sets the text of the label
		//based on the position of the mouse.
		label.setLabel(x + "," + y);
		
		//Adds the label to the screen.
		add(label, INDENT, getHeight()/2);
	}
	


}
