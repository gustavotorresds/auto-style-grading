/*
 * name: David Guo, Section Leader: Akua
 * This program allows you to see the x and y coordinates of your mouse in a space. If you hover over a created
 * label, then you the label itself will highlight red, and if you don't, it will still give you the coordinates,
 * but the label will show blue. 
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

/**
 * this program is supposed to 1. create a Glabel 2. allow the user to hover over the screen and if they touch
 * a Glabel, then it makes the Glabel red and gives you the mouse's coordinates 3. if they don't touch a 
 * Glable then it makes the Glabel blue and gives the mouse's coordinates. 
 *
 */
public class MouseReporter extends GraphicsProgram {

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	private GLabel label = new GLabel("");  
	
	public void run() {	
		makeLabel();
		addMouseListeners();
		
	}
	
	/**
	 * make label actually creates the label that will be hovered over and manipulated with the mouse events 
	 */
	private void makeLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE); // initially set the label color to blue 
		
		// this setLabel method takes in a "String" 
		// you can concatenate integers and commas as such:
		label.setLabel(0 + "," + 0);
		
		add(label, INDENT, getHeight()/2);
	}
	
	/**
	 * this method, when your mouse has been moved, should return whether or not that touched Object 
	 * is a label or not. if it's not/null then it should set the label color to be blue and give you the 
	 * mouse's coordinates. if it is then the label should be red and set the label to the mouse's coordinates
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX(); //record the x-coordinates of your mouse
		double mouseY = e.getY(); //record the y-coordinates of your mouse
		
		GLabel touchedObject = getElementAt(mouseX, mouseY); //see if you touched an object at the coordinates of your mouse
		
		if (touchedObject != null) { 
			label.setColor(Color.RED);
			label.setLabel(mouseX + "," + mouseY); //record the mouse's coordinates and set it to the label
			
		} else { // if there is no object you've touched with your mouse
			label.setColor(Color.BLUE);
			label.setLabel(mouseX + "," + mouseY);
		}
	}

	
	
	


}
