/*
 * File: MouseReporter.java
 * Name: Johannes Hui
 * Section Leader: Thariq Ridha
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
	
	//Label variable to display coordinates of mouse. Label set under mouseMoved method
	private GLabel label = new GLabel("");
	
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		//Adds label at half the height of the screen
		add(label, INDENT, getHeight()/2);
	}
	
	//Sets label location and color based on mouse location
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		//Displays mouse location on label
		label.setLabel(mouseX + "," + mouseY);
		
		//If mouse is over the label, the label color will be red (otherwise it will remain blue)
		if (getElementAt(mouseX, mouseY) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}


}
