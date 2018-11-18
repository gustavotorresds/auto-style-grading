/*
 *File: MouseReporter.java
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

	// A constant for the x value of the label
	private static final int INDENT = 20;

	// label
	private GLabel label = new GLabel("");
	//coordinates of the mouse
	public double mouseX;
	public double mouseY;

	public void run() {	

		addMouseListeners();

		//sets up the label.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);	
		label.setLabel(mouseX + "," + mouseY);


		// adds the label to the screen.
		add(label, INDENT, getHeight()/2.0);

	}

	public void mouseMoved (MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		GObject location = getElementAt(mouseX, mouseY);
		label.setLabel(mouseX + "," + mouseY);

		//change the color of the label to red when the mouse touches it
		if (location != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);	
		}
		
	}
}
