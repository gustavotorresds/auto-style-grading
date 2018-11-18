/*
 * File: MouseReporter.java
 * -----------------------------
 * A label will appear onscreen and display the location of the mouse, updating
 * the label every time it moves. The default color of the label text will be blue,
 * but it will change to red when the mouse moves over the text.
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
	
	
	/*
	 * Creates the setup for the program and then adds mouseListeners
	 */
	public void run() {			
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);		
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + " , " + mouseY);
		//check if element underneath and change color of label if present
		getElementAt(mouseX, mouseY);
		GLabel obj = getElementAt(mouseX, mouseY);
		if (obj != null) {
			label.setColor(Color.RED);
		} else { //change back if not present
			label.setColor(Color.BLUE);
		}
		
	}
	

}
