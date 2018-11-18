/*
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;
	
	private GLabel label = new GLabel("");
	
	public void run() {	
		
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		//initial has the label display "0,0."
		label.setLabel(0 + "," + 0);
		addMouseListeners();
		//places the label at the correct location on the screen.
		add(label, INDENT, getHeight()/2);
	}

	public void mouseMoved(MouseEvent e) {
		// these doubles will track the x and y coordinates of the mouse.
		double mouseX = e.getX();
		double mouseY = e.getY();
		// has the label display the x and y coordinates of the mouse
		label.setLabel(mouseX + "," + mouseY);
		// this finds the element at the mouse's location
		GObject object = getElementAt(mouseX, mouseY);	
		// if there is an object (which can only be the label), then it changes the label's color to Red.
		if (object != null) {
			label.setColor(Color.RED);
		// if there is no object, or as soon as the mouse moves off the label, the label's color is switched back to Blue.
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
