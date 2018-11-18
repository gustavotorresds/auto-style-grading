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
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	private String labeltext = new String("");
	
	public void run() {	
		addMouseListeners();
		
		// this code adds the label to the screen!
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
	}
	
	public void mouseMoved(MouseEvent e) {
		label.setColor(Color.BLUE);
		int x = e.getX();
		int y = e.getY();
		String labelXAndY = new String(x + "," + y);
		label.setLabel(labelXAndY);
		GLabel maybeExistentLabel = getElementAt(x,y);
		if (maybeExistentLabel != null) {
			label.setColor(Color.RED);
		}
	}

}
