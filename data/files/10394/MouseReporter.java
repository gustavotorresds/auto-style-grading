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

	;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");


	public void run() {	
		//1Have to add mouse listener to run
		addMouseListeners();

		//  adds the label to the right hand of screen

		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		double x = INDENT;
		double y = getHeight()/2;
		add(label, x, y);



	}

	
	// changes label color to Red if mouse is over label
	public void mouseMoved(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		label.setLabel(x + "," + y);
		if (x < redX() && x > label.getX() && y < redY() && y > (label.getY() - label.getHeight())) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}



	}
	// calculates the highest x coordinates that the label takes ups
	private double redX() {
		double redx = label.getX() + label.getWidth();
		return redx;
	}
	
	// calculates the highest y coordinates that the label takes ups
	private double redY() {
		double redY = label.getY() + label.getHeight();
		return redY;
	}








}
