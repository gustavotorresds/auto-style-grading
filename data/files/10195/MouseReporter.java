/*
 * File: MouseReporter.java
 * -----------------------------
 * Leigh Warner
 * Esteban
 * 
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
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();

		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}

	/*This method detects mouse movement, updating the label to the current coordinates
	 * while also turning the label red if the mouse is over the label. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + ", " + mouseY);				//label shows coordinates of the mouse
		GLabel detector = getElementAt(mouseX, mouseY);		//detector looks at whether mouse is over label, and if so prints in red. 
		if (detector !=null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}


	}


}
