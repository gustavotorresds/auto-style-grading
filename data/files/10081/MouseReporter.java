/*
 * File: MouseReporter.java
 * -----------------------------
 * This program provides the location of the mouse to a label on the
 * screen. The colour of the text changes from blue to red when the mouse is
 * hovering over the label
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// "instance" variables
	private GLabel label = new GLabel("");
	
	public void run() {	
		// adds mouse listeners //
		addMouseListeners();
		addLabel();
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		if(getElementAt(x, y) !=  null){ // tests to see if any element below current mouse position //
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
	
	// adds label to the screen with default characteristics //
	private void addLabel() {
		label.setFont("Courier-24");		
		add(label, INDENT, getHeight()/2);
	}

}