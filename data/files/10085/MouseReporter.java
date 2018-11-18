/* Michelle Ly
 * TA: Drew Bassilakis 
 * File: MouseReporter.java
 * -----------------------------
 * Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	//Instance variable for the label for the mouse coordinate
	private GLabel label = new GLabel("");
	
	//adds label that plots the coordinates where the mouse is
	//and turns the label from blue to red if the mouse is touching it.
	public void run() {	
		makeCoordLabel();
		addMouseListeners();
	}
	
	private void makeCoordLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
	
	//Tracks where the mouse is and turns the coordinate label
	//from blue to red if the mouse is on the label
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + " , " + y);
		GObject lab = getElementAt( x, y);
		if (lab != null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	}
}