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

	public void run() {	

		addMouseListeners();

	}

	/*
	 * ------------------------------
	 * This program monitors the movement of the mouse, and displays the mouse's current coordinates on a label centered on the left side of the screen.
	 * If the mouse moves over the label, it turns red, otherwise, the label stays blue.
	 */
	
	public void mouseMoved(MouseEvent e) {

		double mouseX = e.getX();
		double mouseY = e.getY();
		
		if(getElementAt(e.getX(), e.getY()) != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		
		label.setFont("Courier-24");
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
	}

}