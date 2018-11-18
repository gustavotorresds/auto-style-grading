/*
 * File: MouseReporter.java
 * -----------------------------
 * Ryan Kang
 * Section Leader: Semir Shafi
 * MouseReporter/Sandcastle: Output the location of the mouse to a label on the
 * screen. Change the color of the label to red when
 * the mouse touches it.
 * Sources: Stanford Java Lib; The Art & Science of Java, CS 106A study group.
 * 
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
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel(0 + "," + 0);
		add(label, INDENT, getHeight()/2);		
		addMouseListeners();
	}
	
	/*
	 * This method sets the current label to display the most recent x and y coordinates
	 * of the mouse. It also changes the color of the label from blue to red when 
	 * the mouse touches the label.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		label.setLabel(mouseX + "," + mouseY);
		//changes the label from red to blue when mouse touches the label.
		if (getElementAt(mouseX,mouseY)!=null){
			label.setColor(Color.RED);
		} else {
			label.setColor(Color.BLUE);
		}
	}
}


