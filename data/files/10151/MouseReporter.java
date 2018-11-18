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


	private static final int INDENT = 20;
	private int x = 0;
	private int y = 0;

	private GLabel label = new GLabel("");
	
	public void run() {	
		addMouseListeners();
		createLabel();
	}
	
	//creates label that displays x and y values
	public void createLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
	
		label.setLabel(x + "," + y);
		
		add(label, INDENT, getHeight()/2);
	}
	//causes label to update when mouse is moved; changes color to red when mouse is on label
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		label.setLabel(x + "," + y);
		if(getElementAt(x,y) != null) {
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);
		}
		}
	}
	
