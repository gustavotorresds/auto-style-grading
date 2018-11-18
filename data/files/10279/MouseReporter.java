/*
 * File: MouseReporter.java

Name: Alexander Bhatt
Section Leader: Chase Davis

This program produces the location of the mouse on the screen (in pixels). If the mouse ever lands 
atop the label, the label changes colors to red. Otherwise, the label is blue.

 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.program.*;

public class MouseReporter extends GraphicsProgram 
{
	private static final int INDENT = 20;

	private GLabel label = new GLabel("");
	
	public void run() 
	{	
		label.setFont("Courier-24");
		add(label, INDENT, getHeight()/2);
		addMouseListeners();
	}

	/*
	 * This method creates variables for the x and y values of the mouse at any given point.
	 * After importing GObject, the check utilizes whether the value is null or not to set the
	 * color of the label.
	 */
	public void mouseMoved (MouseEvent e)
	{
		double xVal = e.getX();
		double yVal = e.getY();
		label.setLabel(xVal + "," + yVal);
		GObject check = getElementAt (xVal, yVal);
		if (check == null)
		{
			label.setColor(Color.BLUE);
		}
		
		else
			label.setColor(Color.RED);
	}
}
