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

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// instance variable for the label of the coordinates
	private GLabel label = new GLabel("");
	
	public void run() {	
		// this adds the label to the screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		//the label displays x and y coordinates
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
	}
	// this method is called each time the mouse is moved 
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		// the label changes to reflect the coordinates of the mouse
		label.setLabel(mouseX + "," + mouseY);
		GLabel maybeLabel = getElementAt(mouseX, mouseY);
		// if the mouse touches the label, the label turns red
		if(maybeLabel != null) {
			label.setColor(Color.RED);
		}
		// if the mouse is not touching the label, the label stays blue
		if(maybeLabel == null) {
			label.setColor(Color.BLUE);
		}
	}
}
