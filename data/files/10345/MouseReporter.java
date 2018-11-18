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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	
	private GLabel label = new GLabel("");
	
	
	public void run() {	
		// 1. add mouse listeners
		addMouseListeners();
	}

	// 2. define the mouse moved method
	// and it has to have exactly this "prototype"
	public void mouseMoved(MouseEvent e) {
		// 3. get the mouse location
		double mouseX = e.getX();
		double mouseY = e.getY();
		
		// 4. do something!
		label.setFont("Courier-24");
		GObject gobj = getElementAt(mouseX, mouseY);
		
		if (gobj != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		
		label.setLabel(mouseX+ "," + mouseY);
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);	
	}
	


}
