/*
 * File: MouseReporter.java
 * 
 * Name: Jessie Dalman
 * Section Leader: Esteban Rey
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + ", " + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	/**
	 * Method: Mouse Moved
	 * Tracks the mouse on the screen. 
	 * When the mouse touches the label, the label turns red.
	 * When the mouse is not touching the label, the label is blue.
	 */
	
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GObject tappedLabel = getElementAt(mouseX, mouseY);
		if(tappedLabel != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}


