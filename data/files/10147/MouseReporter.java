/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Cat Davis
 * SUnet ID: catdavis
 * Section Leader: Ben Barnett
 * -----------------------------
 * The MouseReporter class outputs the location of 
 * the mouse to a blue label on the screen. When the
 * mouse touches the label, the label turns red.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;
import acm.graphics.GObject;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// Creates an instance variable of the label
	private GLabel label = new GLabel("");

	/* 
	 * Creates a label of the mouse coordinates
	 * and adds it to the screen.
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();
		double mouseX = getX();
		double mouseY = getY();
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved (MouseEvent event) {
		double mouseX = event.getX();
		double mouseY = event.getY();
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
		GObject possibleObject = getElementAt(mouseX, mouseY); // changes label color when mouse touches it
		if (possibleObject != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}	
}