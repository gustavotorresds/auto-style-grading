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

	/** A constant for the x value of the label */
	private static final int INDENT = 20;
	
	/** Instance variables, visible to all the program */
	private GLabel label = new GLabel("");
	
	
	/**
	 * Here I strat by setting the label and then calling mouse listeners for when the mouse moves.
	 * 
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		addMouseListeners ();
	}
	
	
	/**
	 * Here I define the mouse listeners, specifically for when the mouse moves. If the mouse is moving,
	 * the locations is recorded and updated in the instance variable label. The location is also used to set color.
	 */
	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		makeBlueOrRed(mouseX , mouseY);
		add(label, INDENT, getHeight()/2);
	}

	
	/**
	 * This method decides whether to make the label red of blue depending on the passed location of the mouse"
	 */
	private void makeBlueOrRed(double mouseX, double mouseY) {
		GObject clicked = getElementAt (mouseX , mouseY);
		if (clicked == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}