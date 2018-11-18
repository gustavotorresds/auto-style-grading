/*Name: Chuyi Yang
 * Section Leader: Andrew Davis
 * 
 * File: MouseReporter.java
 * -----------------------------
 * This program shows the coordinates of the mouse on the screen. If the mouse hovers
 * over the label, the label will turn red. If not, the label will be blue.
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
	
	public void run() {	
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		addMouseListeners();
		// add the label to the screen!
		add(label, INDENT, getHeight()/2);
	}
	

	public void mouseMoved(MouseEvent z) {
		double mouseX = z.getX();
		double mouseY = z.getY();
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);	//sets the position for the label and adds it onto the screen
		getElementAt(mouseX, mouseY); 	//tests if the label is present
		if (getElementAt(mouseX, mouseY) != null) { //if statement tests if the mouse is on the label. If it is, the label turns red.
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
		
	}

}
