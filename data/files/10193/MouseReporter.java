/*
 * File: MouseReporter.java
 * Name: Sophie Maguy
 * Section Leader: Esteban Rey
 * -----------------------------
 * This program will change a label on the screen to reflect the mouse's location on the screen.
 * If the mouse is touching the label, it will change color from blue to red.
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
	//these are set as instance variables so they can be accessed in the mouseMoved method
	int x= 0;
	int y= 0;
	
	public void run() {	
		// this code adds the label to the screen.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		//adding mouse listeners so that the computer can react to changes in the mouse
		addMouseListeners();
		// this setLabel method takes in a "String" 
		label.setLabel(x + "," + y);
		//this adds the label to the screen
		add(label, INDENT, getHeight()/2);
	}
	
	//this is a mouse listener method that will run whenever the mouse is moved
	public void mouseMoved(MouseEvent event) { 
		//sets the new coordinates of the mouse to x and y
		x= event.getX();
		y= event.getY();
		//resets the label's coordinates
		label.setLabel(x + "," + y);
		//changes the color of the label if the mouse is touching it by using the getElementAt() method
		if (getElementAt(x, y)!=null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}
}
