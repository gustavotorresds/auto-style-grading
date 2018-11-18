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
	
	public void run() {	
		//Sets fonts and color for the label. 
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		
		// This setLabel method takes in a "String"  
		label.setLabel(0 + "," + 0);
		
		// This adds the label to the screen.
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners();
		}
	
	// This method is called whenever the mouse is moved anywhere within the screen. It finds the location of the mouse in the context of the screen's parameters 
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();  
		GLabel potentialLabel = getElementAt(x, y);
			if (potentialLabel == label) {
				label.setColor(Color.RED);
				}	else if (potentialLabel != label) {
						label.setColor(Color.BLUE);
						} 
							label.setLabel(x + "," + y);
							} 
}
