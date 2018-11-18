/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Ju Yeon (Julie) Lee
 * Section Leader: Niki Agrawal
 * 
 * This program will display x,y-coordinates of the mouse
 * in blue and they will turn red when the mouse is on 
 * the coordinates.
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
	 * The label for x,y coordinates is determined
	 * and displayed, changing color from blue to red
	 * when mouse is on the label.
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();
		add(label, INDENT, getHeight()/2);	
	}
	
	/*
	 * The coordinates of mouse is determined
	 * and label is created based on them. 
	 * Changes color to red when mouse is on the label. 
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x + "," + y);
		GObject obj = getElementAt(x,y);
		if(obj!=null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	}
}
