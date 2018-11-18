/*
 * File: Breakout.java
 * -------------------
 * Name: Roscoe Harris III
 * Section Leader: Luciano
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram 
{

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	
	//Instance variables
	private RandomGenerator rg = RandomGenerator.getInstance();

	private GRect paddle;
	private GOval ball;
	private GObject top;
	private GObject right;
	private GObject bottom;
	private GObject left;
	private double dx = rg.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	private double dy = VELOCITY_Y;
	private GRect rect;
	private int numLives = NTURNS;
	private int counter = 0;
	private int numBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	
	public void run() 
	{
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		/* You fill this in, along with any subsidiary methods */
		if (rg.nextBoolean(0.5)) 
			dx = -dx;
		setup();
		waitForClick();
		addMouseListeners();
		while(numLives > 0 && counter < numBricks)
		{
			ball.move(dx, dy);
			ballInt();
			pause(DELAY);
		}
		if(counter == numBricks)
		{
			print("You Win!!!");
		}
		else 
			print("You Lose!!");
		
	}
	public void mouseMoved(MouseEvent e)
	{

		double mouseX = e.getX();
		//double mouseY = e.getY();
		
		//if(yuh == null)
		paddle.setLocation(mouseX - (0.5 * PADDLE_WIDTH), getHeight() - PADDLE_Y_OFFSET);
		
	}
	private void makeBall()
	{
		ball = new GOval(getCenterX() - BALL_RADIUS, getCenterY() + BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	private void makePaddle()
	{
		paddle = new GRect(getCenterX()- (0.5 * PADDLE_WIDTH), getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	private void makeWall()
	{
		//5 then half of the gap from center
		for(int j = 0; j < NBRICK_ROWS; j++)
		{
			for(int i = 0; i < NBRICK_COLUMNS; i++)
			{
				rect = new GRect(getCenterX() - (5 * BRICK_WIDTH) - (4.5 * BRICK_SEP) + (i * BRICK_WIDTH) + (i * BRICK_SEP), (10 * BRICK_HEIGHT) + (9 * BRICK_SEP) + (j * BRICK_HEIGHT) + (j * BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if(j == 0 || j == 1)
				{
					rect.setFilled(true);
					rect.setColor(Color.RED);
					//rect.setFillColor(Color.RED);
					add(rect);
				}
				else if(j == 2 || j == 3)
				{
					rect.setFilled(true);
					rect.setColor(Color.ORANGE);
					add(rect);
				}
				else if(j == 4 || j == 5)
				{
					rect.setFilled(true);
					rect.setColor(Color.YELLOW);
					add(rect);
				}
				else if(j == 6 || j == 7)
				{
					rect.setFilled(true);
					rect.setColor(Color.GREEN);
					add(rect);
				}
				else if(j == 8 || j == 9)
				{
					rect.setFilled(true);
					rect.setColor(Color.CYAN);
					add(rect);
				}
				
			}
		}
		
	}
	
	private void setup()
	{
		makeBall();
		makePaddle();
		makeWall();
	}
	
	private void ballInt()
	{
		top = getElementAt(ball.getCenterX(), ball.getCenterY() - (0.5 * BALL_RADIUS));
	    right = getElementAt(ball.getCenterX() + (0.5 *BALL_RADIUS), ball.getCenterY());
		bottom = getElementAt(ball.getCenterX(), ball.getCenterY() + (0.5 * BALL_RADIUS));
		left = getElementAt(ball.getX(), ball.getY() + (0.5 * BALL_RADIUS));
		if(isTouchPaddle())
		{
			dy = -dy;
		}
		if(isTouchSideWalls())
		{
			dx = -dx;
		}
		if(isTouchTopWall())
		{
			dy = -dy;
		}
		if(topIsTouchBrick())
		{
			counter++;
			remove(top);
			dy = -dy;
		}
		if(bottomIsTouchBrick())
		{
			counter++;
			remove(bottom);
			dy = -dy;
		}
		if(leftIsTouchBrick())
		{
			counter++;
			remove(left);
			dx = -dx;
		}
		if(rightIsTouchBrick())
		{
			counter++;
			remove(right);
			dx = -dx;
		}
		if(isTouchBottom())
		{
			numLives--;
			counter = 0;
			ball.setLocation(getCenterX() - BALL_RADIUS, getCenterY() + BALL_RADIUS);
			waitForClick();
		}
	}
	private boolean isTouchTopWall() 
	{
		if(ball.getY() < 0)
			return true;
		else
			return false;
	}
	private boolean isTouchBottom()
	{
		if(ball.getCenterY() + (0.5 * BALL_RADIUS) > getHeight())
		{
			return true;
		}
		else 
			return false;
	}
	/*private boolean isTouchBrick()
	{
		if((top != null || right != null || bottom != null || left != null) && (top != paddle || right != paddle || bottom != paddle || left != paddle))
		{
			return true;
		}
		else
			return false;
	}
	*/
	private boolean topIsTouchBrick()
	{
		if(top != null && top != paddle)
		{
			return true;
		}
		else 
			return false;
	}
	private boolean bottomIsTouchBrick()
	{
		if(bottom != null && bottom != paddle)
		{
			return true;
		}
		else 
			return false;
	}
	private boolean leftIsTouchBrick()
	{
		if(left != null && left != paddle)
		{
			return true;
		}
		else 
			return false;
	}
	private boolean rightIsTouchBrick()
	{
		if(right != null && right != paddle)
		{
			return true;
		}
		else 
			return false;
	}
	
	private boolean isTouchPaddle()
	{
		if(bottom == paddle)
		{
			return true;
		}
		else
			return false;
	}
	private boolean isTouchSideWalls()
	{
		if(ball.getX() + BALL_RADIUS > getWidth() || ball.getX() < 0)
		{
			return true;
		}
		else 
			return false;
	}
}
 