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

	private static final int INDENT = 20;

	private GLabel label = new GLabel("");
	double xMouse; 
	double yMouse;
	private GLabel obj = new GLabel("");
	
	public void run() {	
		addMouseListeners();
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
	
	public void mouseMoved(MouseEvent e) {
		xMouse = e.getX();
		yMouse = e.getY();
		obj = getElementAt(e.getX(), e.getY());
		label.setLabel(xMouse + "," + yMouse);
		if (obj == label) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}
