/* Name: Tess Stewart
 * Section Leader: Garrick Fernandez 
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
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
		
		//add mouse listeners 
		addMouseListeners();
		
	}
	
	/*
	 * Pre: blank window
	 * Post: have the label display the coordinates of the mouse, 
	 * if the mouse is touching the label, have the label turn red.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY(); 
		
		label.setLabel(mouseX + "," + mouseY);
		
		if (getElementAt(mouseX, mouseY)!= null) {
			label.setColor(Color.RED);
		}else {
			label.setColor(Color.BLUE);
		}
	
		
	}
	
	


}
