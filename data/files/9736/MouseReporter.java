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
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {	
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		GLabel maybeALabel = getElementAt(mouseX,mouseY);
		if (maybeALabel != null) {
			makeLabel(Color.RED, mouseX, mouseY);
		} else {
			makeLabel(Color.BLUE, mouseX, mouseY);
		}
	}

	private void makeLabel (Color color, int x, int y) {
		label.setFont("Courier-24");
		label.setColor(color);
		label.setLabel(x + "," + y);
		add(label, INDENT, getHeight()/2);
	}	
}