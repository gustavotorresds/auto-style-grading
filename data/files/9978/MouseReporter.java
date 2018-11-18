
/*
 * File: MouseReporter.java
 * Name: Kaylie Mings 
 * Section Leader: Niki Agrawal
 * -----------------------------
 * This program extends Graphics Program so that as the user hovers their mouse over the screen
 * the x and y coordinates of where the mouse is are projected onto the screen in a label. If the 
 * user hovers the mouse over the label the coordinates appear in red. However, when the mouse hovers
 * anywhere else on the screen the coordinates appear blue. 
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
		label.setFont("Courier-24");
		// add the label to the screen!
		add(label, INDENT, getHeight() / 2);
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		add(label);
		GLabel object = getElementAt(x, y);
		if (object != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);

		}
	}
}