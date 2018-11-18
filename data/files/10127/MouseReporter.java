/*
 * File: MouseReporter.java
 * Name: Jasmine Sun
 * Section Leader: Semir Shafi
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
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
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
		
		GObject mouseOver = getElementAt(mouseX, mouseY);
		if(mouseOver != null) {
			label.setColor(Color.RED);
		}
	}
}
