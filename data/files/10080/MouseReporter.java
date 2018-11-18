/*
 * File: MouseReporter.java
 * -----------------------------
 * This program outputs the location of the mouse to a label on the
 * screen, and changes the color of the label to blue (from red) when
 * the mouse touches it. The colors were chosen based on the hand-out!
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label (given).
	private static final int INDENT = 20;
	
	private GLabel label = new GLabel(""); //Given.
	//Because it just has "", it is adaptable to later uses of the label!
	
	public void run() {	
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2); //This adds actual label to screen!	
		addMouseListeners(); 
	}
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		label.setLabel(x + "," + y); 
		
		//!!!Please note, hw handout instructions said to have it turn blue when mouse is touching.
		//However, the picture shows it as red when the mouse is touching.
		//Just in case, I went with the written instructions.
		if(getElementAt(x,y) == label) {
			label.setColor(Color.BLUE);
		} else {
			label.setColor(Color.RED); //Don't need to set label originally as red in run(); set-up starts null.
			
		}
	}
}

/*Notes to self:
 * public void is constantly running (detects null at beginning for else statement)
 * add(label) creates the heap, and label.setLabel puts the label onto screen.
 * alternate method: GLabel maybeALabel = getElementAt(x,y) before if... is a fancy command; checks if null/label!
 */
