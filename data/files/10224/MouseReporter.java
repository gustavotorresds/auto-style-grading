/*
 * Name: Michael Oduoza
 * Section Leader: Andrew Marshall
 * 
 * This program will output the location of the mouse to a label on the
 * screen. The color of the label will change to red when
 * the mouse touches it.
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
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setFont("Courier-24");
		if (getElementAt(mouseX, mouseY) != null){
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
	}
}
