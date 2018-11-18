/*
 * File: MouseReporter.java 
 * -----------------------------
 * Name: Ildemaro Gonzalez
 * Section Leader: Rachel Gardner
 * 
 * This file outputs the location of the mouse to a label on the
 * screen. It also changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Instance variable for label 
	private GLabel label = new GLabel("");
	
	// Instance variable for x and y coordinates of mouse
	int mouseX = 0;
	int mouseY = 0;


	public void run() {	
		
		// Enables the tracking of mouse events
		addMouseListeners();
	}
	/**
	 * Creates label that reports mouse location onto screen.
	 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(mouseX + "," + mouseY);

		// Places label at appropriate location
		add(label, INDENT, getHeight()/2);

		// If mouse touches label, label turns red
		GLabel underMouse = getElementAt(mouseX, mouseY);
		if(underMouse != null) {
			label.setColor(Color.RED);
		}
	}
}
