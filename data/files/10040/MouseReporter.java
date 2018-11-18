/*
 * File: MouseReporter.java
  * -------------------
 * Name: Larsen Jones
 * Section Leader: Julia Daniel
 * 
 * This is a prep exercise for Breakout. It will display a label 
 * on the left side of the screen with mouse coordinates and when the 
 * mouse is touching the label, it will turn from red to blue.
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
	
	public void run() {	
		addLabel();
		addMouseListeners();
	}
	
	// sets the label to display the coordinates of the mouse location. Changes label color from Blue (default)
	// to red when mouse is touching label
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject obj = getElementAt(mouseX, mouseY);
		if(obj != null) {
			label.setColor(Color.BLUE);
			}
		else label.setColor(Color.RED);
	}

	// adds label to the screen. 
	private void addLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
}
