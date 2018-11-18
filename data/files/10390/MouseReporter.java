/* Name: Chelsey Pan
 * Section Leader: Julia Daniel
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

	/** Constant for the X value of the label */
	private static final int INDENT = 20;
	
	/** Creates an instance variable of the label. */
	private GLabel label = new GLabel("");
	
	public void run() {	
		
		//Adds label to the screen prior to the start of the mouse moving
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
		
		//Adds a mouse listener
		addMouseListeners();
	}
	
	/*Finds the (x,y) location of the mouse and sets the color of the label
	 *as blue generally, and then red whenever the mouse is hovering over 
	 *the label.
	 */
	public void mouseMoved(MouseEvent e) {
		//Finds the X and Y location of the mouse
		int mouseXLocation = e.getX();
		int mouseYLocation = e.getY();
		
		//Sets the font and color of the label, as well as writes the (x,y)
		//location of the mouse on the screen.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(mouseXLocation + "," + mouseYLocation);
		
		//Checks to see if there is an element at the mouse's location. If there is,
		//since the only object on the screen is the label, it turns the label red
		//when it is hovered over.
		GObject object = getElementAt(mouseXLocation, mouseYLocation);
		if (object != null) {
			label.setColor(Color.RED);
		}
		
	}

}
