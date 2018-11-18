/* This program adds a label to the center-left side
 * of the screen, and the label reads the x and y coordinates of
 * the mouse, which change as the mouse is moved. If the user mouses
 * over the label itself, then the label (which is otherwise blue)
 * changes its color to red.
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

	/* The run method sets the font, color (blue), and initial 
	 * values of the x and y slots of the label (0, 0).
	 * It then adds the label at the pre-set location of 
	 * at the center left of the screen (but set off by an indent).
	 * The method then calls the addMouseListeners method, which
	 * "listens" for a MouseEvent--in this case, that event is a
	 * movement of the mouse by the user.
	 */
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	/* This method is a MouseEvent that is called
	 * when the mouse moves. The method stores the x location and
	 * y location of the mouse as mouseX and mouseY, then sets the label
	 * to read these coordinates. It then uses the getElementAt function
	 * to check if there is an object--in this case the label--where the
	 * mouse is. If there is, it sets the label color to red. Otherwise, it
	 * keeps (or resets) the color at (or to) blue. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();

		label.setLabel (mouseX + ", " + mouseY);

		GObject maybeTheLabel = getElementAt(mouseX, mouseY);
		if(maybeTheLabel != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}


	}
}





