/*
 * Hannah Rasmussen
 * Section Leader: Andrew Marshall
 * CS106A
 * Assignment 3
 * 2/5/18
 * This program will create a label that tracks the location of the mouse on the screen.
 * If the mouse hovers over the label, the label turns red.
 * Otherwise, the label stays blue.
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		//add mouse listeners
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		//find coordinates of mouse
		int x = e.getX();
		int y = e.getY();
		
		//set the label to print the coordinates of the mouse as it moves
		label.setLabel(x + "," + y);
		
		//this set of code will ask whether there is a label/object present at the mouse location
		GObject maybeLabel = getElementAt(x, y);
		
		//if object present (mouse over label), turn label red, otherwise keep it blue
		if(maybeLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
