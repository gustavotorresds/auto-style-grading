/*
 * File: MouseReporter.java  
 * Name: Elina Thadhani 
 * Section Leader: Vineet Kosaraju 
 * -----------------------------
 * This file outputs the location of the mouse to a label on the
 * screen. The color of the label changes to red when
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
		createLabel();
		addMouseListeners(); 



	}
	/* 
	 * This method creates the label on the left hand side of the screen. 
	 * The label is intially blue and of set font. 
	 * Precondition: None 
	 * Postcondition: the label is displayed halfway down the left hand 
	 * side of the canvas (adjacent to the left wall). The label is blue 
	 * and reads (0,0). 
	 */
	private void createLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel( 0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
	/* 
	 * This method tracks the movement of the mouse, such that when the mouse 
	 * is moved, the coordinates of the label update so that the label 
	 * displays the x and y coordinates of the mouse. If the mouse touches the 
	 * label, then the label turns red, but turns back blue if the mouse moves away. 
	 * Precondition: The label is already displayed on the canvas, and starts out blue. 
	 * Postcondition: the mouse movement is tracked and the label coordinates update 
	 * as the mouse moves around the canvas. If the mouse touches the label, it turns red.
	 */
	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX(); 
		int mouseY = e.getY(); 
		label.setLabel ( mouseX + "," + mouseY);
		if (getElementAt( mouseX, mouseY)!= null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE); 
		}
	}






}
