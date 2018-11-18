/* Name: Angie Lee
 * Section Leader: Julia Daniel
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen and changes the color of the label to red when
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


	private GLabel label = new GLabel("");
	
	public void run() {	
		
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		
		add(label, INDENT, getHeight()/2);
		
		addMouseListeners(); 
	}
	
	/* This method is called when the mouse is moved,
	 * gets the location of the cursor at all times, and sets
	 * the label to show that location. If the mouse is hovering
	 * over the label, the font should be red, but if not, the font
	 * is blue.
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		GObject maybeAnObject = getElementAt(x, y);
		if(maybeAnObject != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

}
