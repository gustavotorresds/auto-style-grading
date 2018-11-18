
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

	/* Instance variables */
	private static final int INDENT = 20;
	private GLabel label = new GLabel("");

	public void run() {
		label.setFont("Courier-24");
		add(label, INDENT, getHeight() / 2);
	}

	/* Get color from position of mouse */
	private Color getColor(int x, int y) {
		if (getElementAt(x, y) != null) {
			return Color.RED;
		} else {
			return Color.BLUE;
		}
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		label.setColor(getColor(x, y));
	}

}
