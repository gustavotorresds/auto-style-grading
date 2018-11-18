/*
 * File: MouseReporter.java
 * -----------------------------
 * This program displays the location of the mouse to a label on the
 * screen. It also changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	//define instance variable 
	private GLabel label = new GLabel("");
	
	
	public void run() {	
		//add mouse listeners
		addMouseListeners();
	}
	
	
		//add mouse event
		public void mouseMoved(MouseEvent e) {
			//get mouse location
			double mouseX = e.getX();
			double mouseY = e.getY();
			
			//format label
			label.setFont("Courier-24");
			label.setColor(Color.BLUE);
			label.setLabel(mouseX+ "," + mouseY);
			
			//print label to screen
			add(label, INDENT, getHeight()/2);
			
			//make label red when touched 
			GLabel move= getElementAt(mouseX,mouseY);
			move.setColor(Color.RED);
		}
	
	}
	



