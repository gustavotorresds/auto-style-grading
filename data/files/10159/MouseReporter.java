/*
 * File: MouseReporter.java
 * -----------------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red from blue when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;
	
	/* Instance variable creating a label
	 */
	private GLabel label = new GLabel(" ");
	
	/* The program creates the label and font and listens for instructions from
	 * the mouse listeners.
	 */
	public void run() {	
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}
	
	/* The program adds a label to the screen that displays the x and y
	 * coordinates of wherever the mouse is. If the mouse is touching the
	 * label, it is blue. Otherwise, it is red.
	 */
	public void mouseMoved (MouseEvent e) {
		GObject obj = getElementAt (e.getX(), e.getY());
		label.setLabel(e.getX() + "," + e.getY());
		if (obj == null) {
			label.setColor(Color.BLUE);
		}
		if (obj != null) {
			label.setColor(Color.RED);
		}
	}
	


}
