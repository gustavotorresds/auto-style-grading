/*
 * File: MouseReporter.java
 * -----------------------------
 * Reports the location of the mouse to a label on the
 * screen, and changes the color of the label to red when
 * the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	// This variable is visible to the entire program
	// It is called an "instance" variable
	private GLabel label = new GLabel("");
	
	
	public void run() {	
	// This adds the label to the screen.
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
	    
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	
	     		}
		 
	public void mouseMoved(MouseEvent e) { 
	//Creates variable's for the mouse;s location.
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		//This allows the label to report the mouse's location.
		label.setLabel (mouseX +","+ mouseY);
	
		//Allows the mouse to detect the label and change its color.
		GLabel label = getElementAt(e.getX(),e.getY());
			if (getElementAt(e.getX(),e.getY()) == null) { 
				label.setColor(Color.BLUE);
		}  else {
				label.setColor(Color.RED);
		}
	}
}
	     
		

	



