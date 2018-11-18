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
import acm.graphics.GRect;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {
	private static final int LABEL_WIDTH = 60;
	private static final int LABEL_HEIGHT = 10;

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");

	public void run()
	{
		// this code already adds the label to the screen!
		// run it to see what it does.
		label.setFont("Courier-24");
		label.setLabel(0 + "," + 0);
		label.setColor(Color.BLUE);
		addMouseListeners();

		add(label, INDENT, getHeight() / 2);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xCoord = e.getX();
		int yCoord = e.getY();

		label.setText(xCoord + " " + yCoord);
		GLabel current = getElementAt(xCoord, yCoord);
		if (current != null)
			current.setColor(Color.RED);
		else
			label.setColor(Color.BLUE);

	}
}
