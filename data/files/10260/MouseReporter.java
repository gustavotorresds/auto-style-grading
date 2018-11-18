import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.GLabel;
import acm.program.*;

/*
 * Method: Mouse Reporter
 * -------------------
 * Outputs the location of the mouse to a label on the
 * screen. Changes the color of the label to red when
 * the mouse touches it.
 */
public class MouseReporter extends GraphicsProgram {

	private static final int INDENT = 20;

	private GLabel label = new GLabel("");

	public void run() {
		label.setFont("Courier-24");
		add(label, INDENT, getHeight() / 2);
		addMouseListeners();
	}

	/*
	 * Method: Mouse Moved ------------------- Updates the location of the mouse to
	 * a label on the screen when the mouse if moved. Changes the color of the label
	 * to red when the mouse touches it.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		GLabel touchedObject = getElementAt(mouseX, mouseY);
		if (touchedObject == null) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED);
		}
	}
}
