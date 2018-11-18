/*
 * File: MouseReporter.java
 * Name: Kyra Whitelaw
 * Section Leader: Thariq Ridha
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
	
	private GLabel label = new GLabel("");
	private int mousex = 0;
	private int mousey = 0;

	//Creates the label and displays it on the screen.
	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label, INDENT, getHeight()/2);
		addMouseListeners ();
		
		//Changes the color of the label to red when the mouse touches it and blue otherwise.
		while (true) {
			GLabel maybeLabel = getElementAt (mousex, mousey);
			if (maybeLabel != null) {
				label.setColor(Color.RED);
			} else {
				label.setColor(Color.BLUE);
			}
		}
	}
	//Gets the coordinates of the mouse and saves them into the label.
	public void mouseMoved (MouseEvent e) {
		mousex = e.getX ();
		mousey = e.getY ();
		label.setLabel(mousex + "," + mousey);
	}
}
