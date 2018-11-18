/* File: MouseReporter.java
 * Name: Drew Young
 * SL: Julia Daniel
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

	private static final int INDENT = 20;

	/*Label for the mouse coordinates*/
	private GLabel label = new GLabel("");

	/*Makes the label change according to the coordinates
	 * of the mouse*/
	public void mouseMoved(MouseEvent e) {
		label.setLabel(e.getX() + "," + e.getY());
		if(getElementAt(e.getX(), e.getY()) != null) {
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}

	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
	}
}
