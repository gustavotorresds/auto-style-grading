/*
 * File: MouseReporter.java
 * Student: Kailash Raman
 * Section Leader: Chase Davis
 * -----------------------------
 * Displays the current location of the mouse
 * once the mouse is moved.  Changes the color 
 * of label from blue to red only when mouse is
 * over the label.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// Instance variable for location label
	private GLabel label = new GLabel("");
	
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		// adds label to screen
		add(label, INDENT, getHeight()/2);
	}
	
	/* Mouse event that sets label to display current 
	 * location of mouse whenever mouse is moved.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		changeColor(mouseX,mouseY);
	}
	
	/* Changes color of label from blue to red whenever
	 * mouse is over the label (getElementAt(x,y) != null)
	 */
	private void changeColor(double x,double y) {
		if(getElementAt(x,y)!=null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
			
	}
	


}
