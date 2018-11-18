/*
 * Name: Ahmad Ibrahim
 * Section Leader: James Mayclin
 * 
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

	// create a new label
	private GLabel label = new GLabel("");

	// This program tracks the mouse coordinates,
	// prints them on the left side of the screen,
	// and changes their color when the mouse 
	// hovers over those coordinates.
	public void run() {	
		// 1. track the mouse's movements
		addMouseListeners();		
	}

	public void mouseMoved (MouseEvent e) { 
		// 2. identify the x and y coordinates of the mouse
		int mouseX = e.getX();
		int mouseY = e.getY();
		// 3. specify what is written in the label (i.e. the co-ordinates of the mouse)
		label.setLabel(mouseX + "," + mouseY);

		// 4. set the font of the label to Courier 24
		label.setFont("Courier-24");		

		GObject labelPresent = getElementAt (mouseX, mouseY);

		// 5. if there is no label in that position, color the coordinates blue
		if (labelPresent == null) {
			label.setColor(Color.BLUE);
		} else {		
			label.setColor(Color.RED);	
		}
		// 6. print out the label to a specified location in the screen 
		add(label, INDENT, getHeight()/2);
	}
}

