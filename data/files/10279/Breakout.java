/*
 * File: Breakout.java
 * -------------------
 * Name: Alexander Bhatt
 * Section Leader: THE Chase Davis
 * 
 * This file will eventually implement the game of Breakout. First, a set of rows (10 x 10) will be created of differing colors. There
 * will be a paddle which hits a ball up towards the bricks until one rather runs out of lives or wins the game. Enjoy!
 * 
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

	public void run() 
	{
		setTitle("CS 106A Breakout");
		
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		turns = 3; //This instance variable serves as a "life count."
		
		initializeBricks(); 

		paddle = new GRect((getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		
		waitForClick(); //This method allows the player to start when he or she chooses with a click.
		addMouseListeners();
		runGame();
	}
	
	/*
	 * This is where all of the action occurs. At the beginning of the method, a ball is created with which to play. The while loop is created 
	 * so that the loop can animate and so that the ball can respond to the collisions between the ball and rather the paddle and/or wall. This 
	 * method counts the bricks remaining as well as the lives remaining. 
	 */
	private void runGame() 
	{
			ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
			ball.setFilled(true);
			add(ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
			
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			
			vy = VELOCITY_Y;
			
			while (true)
			{
				ball.move(vx, vy);
				
				if (ball.getX()<=0)
				{
					vx = -vx;
				}
				
				if (ball.getY()<=0)
				{
					vy = -vy;
				}
				
				if (ball.getX()>getWidth()- 2*BALL_RADIUS)
				{
					vx = -vx;
				}
				
				GObject collider = getCollidingObject();
				
				if (ball.getY() >= getHeight())
				{
					remove(ball); //This makes sure that once the ball leaves the screen, it is removed.
					turns--;
					if (turns > 0)
						runGame();
					if (turns == 0)
						break;
				}
				
				if (bricksRemaining == 0)
				{
					break;
				}
				
				if (collider != null)
				{
					if (collider == paddle)
					{
						if (vy > 0) //This check counters the issue of having a "sticky paddle."
							vy = -vy; 
					}
					
					else 
					{
						vy = -vy;
						remove(collider); //This is important in that it removes the bricks after each hit.
						bricksRemaining--; //COun
					}
				}
				pause (DELAY);
				
			}
		}
	
	/*
	 * This method is essential in detecting contact between the the ball and brick. Since the ball can hit at any corner, the ball must be 
	 * accounted for at each corner and thus four objects were made. If none of the locations of those objects were satisfied, this method 
	 * would not return a GObject; rather it would return null.
	 */
	private GObject getCollidingObject()
	{
		GObject topLeft = getElementAt (ball.getX(), ball.getY());
		GObject bottomLeft = getElementAt (ball.getX(), ball.getY() + 2*BALL_RADIUS);
		GObject bottomRight = getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		GObject topRight = getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY());
		
		if (topLeft != null)
			return topLeft;
		if (bottomLeft != null)
			return bottomLeft;
		if (bottomRight != null)
			return bottomRight;
		if (topRight != null)
			return topRight;
		else 
			return null;
	}
	
	/*
	 * This method makes a 10 x 10 row of bricks. It calls upon another method to set the colors of the bricks.
	 */
	private void initializeBricks() 
	{
		for (int i = 0; i < NBRICK_ROWS; i++)
			for (int j = 0; j < NBRICK_COLUMNS; j++)
				setBrickRows(i,j);		
	}
	
	/*
	 * This method helps maneuver the paddle from left to right based on the location of the mouse. The method is designed so that the paddle 
	 * will never exit the screen's canvas.
	 */
	public void mouseMoved(MouseEvent e)
	{
		double mouseLocation = e.getX();
		if (mouseLocation < getWidth()-PADDLE_WIDTH)
			paddle.setLocation(mouseLocation, paddle.getY());
	}
	
	/*
	 * This method creates the colors of the rows of bricks in the game. Based on the y-value of the row, a certain color is assigned to the 
	 * bricks.
	 */
	private void setBrickRows(double x, double y) 
	{	
		double xOffset = (getWidth() - (BRICK_WIDTH + BRICK_SEP-1)*NBRICK_COLUMNS)/2;
		GRect brick = new GRect (xOffset +x*(BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT-1)*y, BRICK_WIDTH,BRICK_HEIGHT);
		
		if (y <2) 
			brick.setColor(Color.RED);
		else if (y <4) 
			brick.setColor(Color.ORANGE);
		else if (y <6) 
			brick.setColor(Color.YELLOW);
		else if (y <8) 
			brick.setColor(Color.GREEN);
		else
			brick.setColor(Color.CYAN);
	
		brick.setFilled(true);
		add(brick);
	}
	
	private int bricksRemaining = NBRICK_ROWS*NBRICK_COLUMNS;
	private int turns;
	private GRect paddle; 
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
}


