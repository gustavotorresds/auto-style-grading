/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Longjie Chen
 * Section Leader: Ruiqi Chen
 * 
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
		// Initial Setup like position, color and font for the label.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
		
		// add the mouse behavior.
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		// get mouse's position and know whether GLabel exists by "getElementAt"
		int mouseX = e.getX();
		int mouseY = e.getY();
		GLabel labeldeteminer = getElementAt(mouseX, mouseY);
		
		// label the instant value of mouse, double value is not so pleasure to read
		// so I change it to integer and keep the same style as assignment sample.
		label.setLabel(mouseX + "," + mouseY);
		
		// to determine whether label exists at the mouse and change color
		if(labeldeteminer != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
