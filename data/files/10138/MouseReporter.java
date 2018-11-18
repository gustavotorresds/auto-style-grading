/*
 * File: MouseReporter.java
 * -----------------------------
 * Name: Jung-Won Ha	
 * Section Leader: Shanon Reckinger
 * 
 * Output the location of the mouse to a label on the screen. 
 * Change the color of the label to red when the mouse touches it.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.program.*;

public class MouseReporter extends GraphicsProgram {

	/**A constant for the x value of the label**/
	private static final int INDENT = 20;

	private GLabel label=new GLabel(null);

	public void run() {	
		createLabel();
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) { 
		double mouseX=e.getX();
		double mouseY=e.getY();
		
		label.setLabel(mouseX+","+mouseY);
		
		GLabel obj=getElementAt(mouseX, mouseY);
		
		if(obj!=null) {
			obj.setColor(Color.RED);
		}else{
			label.setColor(Color.BLUE);
		}
	}
	private void createLabel() {
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		add(label,INDENT,getHeight()/2-label.getAscent()/2);
	}
}


