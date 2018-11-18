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
	
	private GLabel label = new GLabel("");
	
	public void run() {	
		// adds label to screen
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// set label string:
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
		
		
	}
	
	/*
	 * Updates label on the location of the mouse if the mouse enters the window.
	 * If the mouse's location is on the label, the label turns red, if not it remains blue.
	 */
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		label.setLabel(x + "," + y);
		if (getElementAt(x,y) != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
	}


}
