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

	// A constant for the x value of the label
	private static final int INDENT = 20;
	
	public void run() {	
		addFirstLabel();
		addMouseListeners();
	}
	
	//adds first label with 0,0 values for x and y coordinates
	private void addFirstLabel() {
		add(makeLabel(Color.BLUE, 0, 0), INDENT, getHeight()/2);
	}
	
	//makes a label, takes as input color, and values to display for x coordinate and y coordinate
	private GLabel makeLabel(Color  color, double xlabel, double ylabel) {
		GLabel label = new GLabel("");
		label.setFont("Courier-24");
		label.setColor(color);
		label.setLabel(xlabel + "," + ylabel);
		return label;
	}
	
	//gets position of mouse every time it is moved and updates label
	public void mouseMoved(MouseEvent a) {
		//get mouse location
		double mouseX = a.getX();
		double mouseY = a.getY();
		add(updateLabel(mouseX, mouseY), INDENT, getHeight()/2 );	
	}

	public GLabel updateLabel(double x, double y){
		//gets label and removes it
		GLabel b = getElementAt(INDENT, getHeight()/2);
		remove(b);
		//gets label dimensions
		double labelXi = INDENT;
		double labelXf = INDENT+b.getWidth();
		double labelYi = getHeight()/2 - b.getHeight();
		double labelYf = getHeight()/2;
		
		//checks if mouse is in label position. If mouse is in position it 
		//updates the label in color red. If the mouse is anywhere else it
		//updates the lable in color blue,
		if (x >= labelXi && x <= labelXf && y >= labelYi && y <= labelYf) {
			return makeLabel(Color.RED,x,y);
		} else {
			return makeLabel(Color.BLUE,x,y);
		}	
	}
}
