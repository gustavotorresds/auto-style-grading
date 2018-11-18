/*
 * File: MouseReporter.java
 * -----------------------------
 * 
 * Name: Avery McCall
 * Section Leader: Cat Xu
 * 
 * This program prints a label on the screen that shows the value of the location of the mouse as the user moves it around the screen. 
 * When the user places the mouse on top of the label it turns red, otherwise it is blue.  
 *
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);

		//adds the label to the screen and adds mouse listeners to track where the mouse is 
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		//Creates constants that track the x and y values of the mouse as it moves around the screen 
		int mouseX=e.getX();
		int mouseY=e.getY();

		//Sets the label's text as the constantly updating location of the mouse 
		label.setLabel(mouseX + "," + mouseY);

		//The following portion checks if the label is underneath the mouse everywhere that the mouse moves. 
		//If the labelPresentTest is not null, the label is there and the label turns red. If it is null, 
		// it ensures that the label remains set to blue. 
		GObject labelPresentTest= getElementAt(mouseX, mouseY);
		if(labelPresentTest!=null) {
			label.setColor(Color.RED); 
		}else {
			label.setColor(Color.BLUE);
		}
	}
}
