/*
 * File: MouseReporter.java
 * Name: Liz Wallace
 * Section Leader: Cat Xu
 * --------------------
 * This warmup problem adds a label to the screen that prints the x and y location of
 *  the mouse. If the mouse touches the label it will turn red. Otherwise, it will turn 
 *  blue. Pre-condition: blank white screen. Post-condition: label that changes color 
 *  and value based on where the mouse lies on the screen. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {	
		addLabel();
		addMouseListeners();
	}
	// This method initially sets conditions for the label. 
	private void addLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);

		// this adds the label to the screen
		add(label, INDENT, getHeight()/2);
	}

	// This method allows for the computer to track mouse movement and record them. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);

		// This determines whether the mouse is on the label or not.
		GLabel maybeLabel = getElementAt(mouseX, mouseY);

		// If the mouse is on the label, this will make the location value appear red.
		if (maybeLabel != null) {
			label.setColor(Color.RED);
		}

		// If the mouse is anywhere else on the screen, the value will appear blue.
		else {
			label.setColor(Color.BLUE);
		}
		println(label);
		
	}
	
}

