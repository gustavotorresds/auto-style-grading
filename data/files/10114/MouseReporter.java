/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.GLabel;
import acm.program.*;

@SuppressWarnings("serial")
public class MouseReporter extends GraphicsProgram {

	int mouseX = 0;
	int mouseY = 0;

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variablem
	private GLabel label = new GLabel(mouseX + "," + mouseY);

	public void run() {	
		label.setFont("Courier-24");
		add(label, INDENT, (getHeight() - label.getAscent())/2);
		addMouseListeners();
		
		while(true) {
		if(mouseY > getHeight() / 2) {
			label.setColor(Color.RED);
			
		} else {
			label.setColor(Color.BLUE);
			
		}

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(mouseX + "," + mouseY);

		// add the label to the screen!
		
		}
	}
	
	public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
	}

}
