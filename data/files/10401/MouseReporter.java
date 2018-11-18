/*
 * File: MouseReporter.java
 * Student: Victoria Yang
 * Section Leader: Cat Xu
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

	//A GLabel instance variable 
	private GLabel label = new GLabel("");

	public void run() {	
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	//This defines the mouseEvent when mouse is moved. The event will modify the number displayed 
	//in label. If the mouse if on the label, the mouseEvent will change the color of label to red as well
	public void mouseMoved (MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y);
		GObject maybeAnObject = getElementAt (x,y);
		if (maybeAnObject != null) {
			label.setLabel(x + "," + y);
			label.setColor(Color.RED);
		} 
	}
}
