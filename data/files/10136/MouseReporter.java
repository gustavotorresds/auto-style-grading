/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Marine Yamada 
 * Section Leader: Rhea Karuturi 
 * This outputs the location of the mouse to a label on the
 * screen and changes the color of the label to red when
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
	
	private GLabel label = new GLabel("");
	private double mouseX; 
	private double mouseY; 

	public void run() {
		//adds mouse functions 
		addMouseListeners();	

		//sets the font and color of the label 
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// adds the label to the screen
		add(label, INDENT, getHeight()/2);
	}

	//sets the label to report the location of the mouse and makes the label red when the mouse hovers over it 
	public void mouseMoved(MouseEvent e) {
		double mouseX= e.getX();
		double mouseY = e.getY(); 
		label.setLabel(mouseX + "," + mouseY);	//label reports location of mouse 

		//checks if the mouse is hovering over the label 
		GObject labelPresent = getElementAt(mouseX, mouseY);		
		if (labelPresent != null) {			//if mouse is over label --> label is red  
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);		//if mouse is not over label --> label remains blue 
		}

	}
}
