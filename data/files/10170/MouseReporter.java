/*
 * Jonathan Khalfayan
 * Ruiqi Chen
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

		// Adds mouse listeners
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {

		// Creates mouse x and y coordinate variables
		int mouseX = e.getX();
		int mouseY = e.getY();

		// Sets label font
		label.setFont("Courier-24");

		// Gets and stores label's x dimensions
		double labelX = label.getX();
		double labelY = label.getY();

		// Gets and stores label's Y dimensions
		double labelWidth = label.getWidth();
		double labelHeight = label.getAscent();

		// Conditionally sets font color if mouse in on the label
		if (inXLabel(mouseX, labelX, labelWidth) && inYLabel(mouseY, labelY, labelHeight)) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}

		// Sets and adds the label
		label.setLabel(mouseX + "," + mouseY);
		add(label, INDENT, getHeight()/2);
	}

	// Boolean to determine if mouse is in Y dimensions of label
	private boolean inYLabel(int mouseY, double labelY, double labelHeight) {
		return mouseY <= labelY && mouseY >= labelY - labelHeight;
	}

	// Boolean to determine if mouse is in X dimensions of label
	private boolean inXLabel(int mouseX, double labelX, double labelWidth) {
		return mouseX >= labelX && mouseX <= labelX + labelWidth;
	}
}
