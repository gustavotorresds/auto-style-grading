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

	private static final int INDENT = 20;                   /*constant value for X coordinate of the label*/
	private GLabel label = new GLabel("");						   /*label and maybelabel are two instance variables*/
	private GLabel maybelabel = null;                               /*that can be seen throughout the program*/
	
	public void run() {	
		label.setFont("Courier-24");                        /*creates a label*/
		add(label, INDENT, getHeight()/2);                  
		addMouseListeners();                                /*add MouseListeners*/
	}
	
	public void mouseMoved(MouseEvent e ) {
		label.setLabel(e.getX() + "," + e.getY());         /*set the content of label to be mouse coordinates*/
		maybelabel = getElementAt(e.getX(),e.getY());      /*detect of the mouse is at the same location of label*/
		if (maybelabel != null) {                          /*if it is, label turns RED*/
			label.setColor(Color.RED);
		}
		else {
			label.setColor(Color.BLUE);                    /*if mouse is not at label, label turns BLUE*/
		}
	}
}