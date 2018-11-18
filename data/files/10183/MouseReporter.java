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

	// The instance variable label allows the entire program
	//to acess it.
	private GLabel label = new GLabel("");





	public void run() {	
		//This code sets size and color of the label
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);

		// This setLabel command creates the label
		label.setLabel(0 + "," + 0);

		// This adds the label to the screen!
		add(label, INDENT, getHeight()/2);
		//This mouse listener activates the (MouseEvent e) method any time the mouse is moved.
		addMouseListeners();
	}

	/*
	 * This mouseEvent stores coordinates of the mouse and updates the label as they change.  
	 * It also checks the if the mouse is over the label and updates it color according to that.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		label.setLabel(x + "," + y);
		if (mouseIsOnLabel(x,y)) {
			label.setColor(Color.RED);
		}
		if (mouseIsNotOnLabel(x,y)) {
			label.setColor(Color.BLUE);
		}
	}
	
	/*
	 * mouseIsOnLabel is a boolean that creates a condition for the mouse listener to check before it updates 
	 * label color.  It uses the range of the x and y dimensions of the label to determine an area where the 
	 * condition is true.
	 */
	private boolean mouseIsOnLabel(double x, double y) {
		if (x > INDENT && x < (label.getWidth()+INDENT) && y<getHeight()/2 && y>((getHeight()/2)-label.getAscent())) {
			return true;
		}else return false;
	}
	
	/*
	 * mouseIsNotOnLabel is a boolean that creates the opposing condition to mouseIsOnLabel by returning true
	 * when mouseIsOnLabel is false.  This allows the mouseEvent to change the color of the label back to blue
	 * in the event that the first if statement passed.
	 */
	private boolean mouseIsNotOnLabel (double x, double y) {
		if (mouseIsOnLabel(x, y) == false ) {
			return true;
		} else return false;
	}
}
