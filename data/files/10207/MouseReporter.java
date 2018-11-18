/*
 * File: MouseReporter.java
 * -----------------------------
 *  * Name: Coco Ramgopal
 * Section Leader: Niki Agrawal 
 * 
 * This program outputs the location of the mouse to the label
 * that is located on the screen. The color of the label is 
 * changed to red when the mouse is touching it. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {
	
	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	//Instance variables
	private GLabel label = new GLabel("");
	private double mouseX, mouseY; 
	
	/** 
	 * Places the x and y values of the mouse coordinates into the label 
	 * that is palced at a fixed position on the screen. 
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/**
	 * Determines the coordinates of the mouse and outputs them to the  
	 * label. It also changes the color of the label's text to red if the mouse
	 * is touching the label.
	 */
	public void mouseMoved (MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		label.setLabel(mouseX + ", " + mouseY);
		if (getElementAt (mouseX, mouseY) == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}