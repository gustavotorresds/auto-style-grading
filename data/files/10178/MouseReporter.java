/*
 * File: MouseReporter.java
 * Name: Julianne Crawford
 * Section Leader: Peter Maldonado
 * -----------------------------
 * Purpose: The MouseReporter Program outputs the location of
 * the mouse to a label on the left-hand side of the screen. 
 * If the mouse moves over the label, the label turns red; 
 * otherwise, the label remains blue. 
 */

import java.awt.Color;
import java.awt.event.*;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

/* A constant for the x value of the label */
	private static final int INDENT = 20;

	public void run() {	
		addLabel();
		addMouseListeners();
	}

	/* Method: addLabel
	 * -----------------
	 * The addLabel method adds a label on the left side of the
	 * window to track the mouse's coordinates.
	 */
	private void addLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);	
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}

	/* Method: mouseMoved
	 * -------------------
	 * The mouseMoved method is called any time the mouse moves in
	 * the program screen. In this instance specifically, this method 
	 * tracks the coordinates of the mouse and displays them in the 
	 * label. If the mouse moves over the label, the label turns red; 
	 * otherwise, it remains blue. 
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		println(mouseX);
		println(mouseY);
		label.setLabel(mouseX + "," + mouseY);
		GObject maybeAnObject = getElementAt(mouseX,mouseY);
		if(maybeAnObject != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

/* Instance variable for the label */
	private GLabel label = new GLabel("");

}
