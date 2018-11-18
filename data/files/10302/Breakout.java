/*
 * File: Breakout.java
 * -------------------
 * Name: Ricky Young
 * Section Leader: Ben Barnett
 * This file will implement the game breakout. 
 * The current one is the one with all the extensions in it.
 */



import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	//
	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	//The ball's actual velocity.
	private double Vx, Vy;

	//This is the random generator for the X position velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//This is an instance variable for the paddle.
	private GRect paddle = new GRect(0,0, PADDLE_WIDTH, PADDLE_HEIGHT);

	//This is an instance variable for the balls.
	private GOval balls = new GOval(0,0,BALL_RADIUS,BALL_RADIUS);

	//The following plays the audioclip for the ball bouncing off the bricks.
	AudioClip bounceClip = MediaTools.loadAudioClip("ray_gun.au"); 

	//You're gonna hate me Ben, but I'm gonna make a global instance variable for the rectangles.
	private int n = NTURNS;

	public void run() {		
		GLabel welcome =  new GLabel("Welcome to brick breaker");
		welcome.setColor(Color.black);
		add(welcome, 0,10);
		setBackground(Color.white);
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* The following methods will set up the brick breaker game. */
		setUpBricks();
		//The following line will create the paddle.
		paddle.setColor(Color.BLACK);
		add(paddle);

		//From here we will be making the "playing" portion of Breakout.
		createBalls();
		ballMotion();
		remainingRectangle();
	}

	//The following method will create the pyramid by creating rows of bricks separately 
	//by whether there are even or odd amounts of bricks in that row.
	private void setUpBricks()	{ 
		//THe following code from lines 86 -90 sets up the rows and columns of block with a specified amount of space BRICK_SEP between them.
		for( int r =0; r < NBRICK_ROWS; r++)	{
			for (int i = 0; i < NBRICK_COLUMNS  ; i++)	{
				double x = ((CANVAS_WIDTH - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)))/2 + BRICK_WIDTH*i + BRICK_SEP * i);
				double y = BRICK_HEIGHT*r + BRICK_SEP*r + BRICK_Y_OFFSET;
				GRect rect = new GRect(x, y , BRICK_WIDTH, BRICK_HEIGHT);
				//The following code from lines 91 - 106  will designate the color of the rectangle and should work for any number of rows.
				if (r % 10 == 0 || r % 10 == 1)	{
					rect.setColor(Color.RED);
				}
				else if (r % 10 == 2 || r % 10 == 3)	{
					rect.setColor(Color.ORANGE);
				}
				else if (r % 10 == 4 || r % 10 == 5)	{
					rect.setColor(Color.YELLOW);
				}
				else if (r % 10 == 6 || r % 10 == 7)	{
					rect.setColor(Color.GREEN);
				}
				else if (r % 10 == 8 || r % 10 == 9)	{
					rect.setColor(Color.CYAN);
				}
				rect.setFilled(true);
				add(rect);
			}
		}
	}

	//The following method will make the paddle move within the boundary of the width of the canvas so it can deflect the ball.
	public void mouseMoved(MouseEvent e)	{
		double mouseX = e.getX();
		double rightBound = CANVAS_WIDTH-PADDLE_WIDTH;
		double paddleYHeight = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		paddle.setLocation(mouseX, paddleYHeight);
		paddle.setColor(Color.black);
		paddle.setFilled(true);
		if (mouseX >= rightBound )	{
			paddle.setLocation(rightBound,paddleYHeight);
		} 
	}


	//The following method will create the balls that are intended to break all of the bricks.
	private void createBalls()	{
		double xBallPos = CANVAS_WIDTH/2 - BALL_RADIUS;
		double yBallPos = CANVAS_HEIGHT/2 - BALL_RADIUS;
		balls.setLocation(xBallPos, yBallPos);
		balls.setColor(Color.black);
		balls.setFilled(true);
		add(balls);
	}

	//The following method will determine the initial velocity of the ball
	private void ballMotion()	{
		Vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) Vx = -Vx;
		Vy = 08.0;
		waitForClick();
		while(true) {	
			balls.move(Vx, Vy);
			ballHittingWalls();
			getCollidingObject();
			brickOrPaddle();
			pause(DELAY);
		}
	}

	//The following method will make sure that the ball bounces off the walls.
	private void ballHittingWalls()	{
		//This is an instance variable for the qualities of the ball. I just really didn't like balls.get stylistically.
		double ballXParam = balls.getX();
		double ballYParam = balls.getY();
		double ballBottom = balls.getBottomY();	
		if (ballXParam <= 0 || (ballXParam + 2*BALL_RADIUS) >= CANVAS_WIDTH )	{
			Vx = -1 * Vx;
		}
		if (ballYParam <= 0)	{
			Vy= -1 * Vy;
		}
		if( ballBottom >= CANVAS_HEIGHT) {
			remove(balls);
			GLabel lose = new GLabel("You Lose!");
			add(lose,0,50);
		}
	}



	/* The following method will look for the object that the ball crashes into.
	 * If the object is a paddle it will only change the ball's directions.
	 * If it is a brick it will remove it then change the ball's direction.
	 */ 
	private GObject getCollidingObject()	{
		GObject topLeft = getElementAt(balls.getX(),balls.getY());
		GObject topRight = getElementAt(balls.getX(),balls.getY()+2*BALL_RADIUS);
		GObject bottomLeft = getElementAt(balls.getX() + 2*BALL_RADIUS, balls.getY());
		GObject bottomRight = getElementAt(balls.getX() + 2*BALL_RADIUS, balls.getY() + 2*BALL_RADIUS);
		//The following will check the four corners of the ball to see if a brick or paddle is hitting the ball. 
		if(topLeft != null)	{
			return topLeft;
		}
		if(topRight != null) {
			return topRight;
		}
		if(bottomLeft != null)	{
			return bottomLeft;
		}
		if(bottomRight != null)	{
			return bottomRight;
		}
		return null;
	}

	//The following method checks if the collided object is a paddle or a brick and correspondingly 
	//performs what is needed for the separate situations.
	private void brickOrPaddle()	{
		GObject collider = getCollidingObject();
		if(collider != null)	{
			if(collider == paddle)	{
				Vy = -1* Math.abs(Vy);
			}
			else	{
				bounceClip.play();
				remove(collider);
				remainingRectangle();
				Vx = -Vx;
				Vy = -Vy;
			}
		}
	}

	//This is a for loop for the situation in which the bricks are all cleared.
	private void remainingRectangle()	{
		n = n +1;
		if(n == (NBRICK_COLUMNS * NBRICK_ROWS)-NTURNS)	{
			remove(balls);
			GLabel winning =  new GLabel("CONGRATS YOU WON!!!!");
			winning.setColor(Color.black);
			add(winning, 0,50);
		}
	}
}



