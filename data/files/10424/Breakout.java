/*
 * File: Breakout.java
 * -------------------
 * Name: Christiana Lee // clee719@stanford.edu 
 * Section Leader: Ruiqi Chen 
 * 
 * This program creates a game called Breakout, which is a game that uses a paddle and ball to try to break all the "bricks".
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels

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
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	//makes the paddle an instance variable since it needs to be accessed throughout the entire program 
	private GRect paddle = null; 
	//Makes ball an instance variable since it needs to be accessed throughout the entire program 
	private GOval ball = null; 

	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Getting all the bricks on the screen, setting up 
		setUpGame();
		paddle = makePaddle(); 
		addMouseListeners();


		//  Play the Game 3 times
		for(int i = 0; i<3; i++) {
			//So that the user knows to click to get the new ball if there are any balls remaining 
			//55 is a random number I chose so I could place the label on the screen where I wanted. 
			GLabel label = new GLabel ("Click to get new Ball", getWidth()/2.0-55.0, getHeight()/2.0); 
			add(label); 
			//Actually waits for the user to click
			waitForClick(); 
			//Removes the label asking the user to click so that it doesn't get in the way of the game 
			remove(label); 
			ball = makeBall(); 
			//playGame returns a boolean to hasMoreTries that indicates whether the user hit the bottom wall and has more tries (true)
			//or hit all of the bricks, and won the game (false) 
			boolean hasMoreTries = playGame();
			remove(ball);
			//If the user has no more tries left 
			if(hasMoreTries == false) {
				GLabel gameOver = new GLabel ("Game Over", getWidth()/2.0-55.0, getHeight()/2.0); 
				add(gameOver); 
				break;
			}
		}
	}	


	private boolean playGame() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y; 
		//The starting number for all the bricks 
		int numberBricks = NBRICK_ROWS*NBRICK_COLUMNS; 

		//ball bouncing off all the walls, except the bottom wall. If it hits the bottom wall, then the game should end or give more tries. 
		while(true) {
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}

			if (hitBottomWall(ball)) {
				println("Game Over"); 
				// You get more tries if there are tries remaining 
				return true;
			}

			ball.move(vx, vy);
			pause(DELAY);
			//the checkForCollisions method informs the program if there were any objects hit, every time an object is hit bricks remaining
			//goes down by 
			boolean bricksRemaining=checkForCollisions();
			if (bricksRemaining == true) {
				numberBricks = numberBricks-1; 
				//when the user hits all of the bricks 
				if (numberBricks == 0) {
					//they have won the game 
					GLabel youWon = new GLabel ("You won!", getWidth()/2.0-55.0, getHeight()/2.0); 
					add(youWon); 
					//and the game ends! 
					return false; 
				}
			}
		}
	}

	//this method helps the program to identify if the ball hit an object. It checks all four corners of the ball to see if it 
	//hits (getElementAt) an object, if it doesn't hit an object then it returns null. 
	private GObject getCollidingObject() {
		//checking the left upper corner
		GObject obj = getElementAt(ball.getX(), ball.getY());
		if(obj == null) {
			//checking the right upper corner
			obj = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		} 
		if (obj == null) {
			//checking the bottom left corner
			obj = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS); 
		} 
		if (obj == null) {
			//checking the bottom right corner 
			obj = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}
		return obj; 
	}

	private boolean checkForCollisions() {
		// Collider is the object we hit
		GObject collider = getCollidingObject();

		// If its the paddle, change the ball's velocity
		if(collider == paddle) {
			vy = -Math.abs(vy);
		} 

		// If we hit something (and its not the paddle), remove it
		if (collider != null) {
			if (collider != paddle) {
				remove(collider); // remove the object
				vy = -vy;
				//True, means that we hit a brick
				return true; 
			}
		}
		//False means nothing happened
		return false;
	}

	//the following booleans inform the program whether or not it hit each type of wall 
	//did it hit the bottom wall? 
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() -BALL_RADIUS*2;
	}
	//did it hit the top wall? 
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <=0; 
	}
	//did it hit the right wall? 
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-BALL_RADIUS*2; 
	}
	//did it hit the left wall? 
	private boolean hitLeftWall(GOval oval) {
		return oval.getX() <= 0; 
	}


	private void setUpGame() {
		//the starting location for the first brick, found by finding the distance of a value x, that is equal from both sides of the screen
		//hence the dividing by 2 
		double startX = (getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH) - (BRICK_SEP*(NBRICK_COLUMNS-1)) ) /2.0;
		//building up the rows 
		for (int i = 0; i<NBRICK_ROWS; i++) {
			//building each row brick by brick 
			for (int j = 0; j<NBRICK_COLUMNS; j++) {
				double x = startX+ j*BRICK_WIDTH +(j)*BRICK_SEP; 
				double y = BRICK_Y_OFFSET + (i+1)*BRICK_HEIGHT + i*BRICK_SEP;

				//creates the different colors of the bricks row by row (changes every 2) 
				GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(rect);
				
				if (i <= 1) {
					rect.setFilled(true);
					rect.setColor(Color.RED);

				} else if (i>1 && i<=3) {
					rect.setFilled(true);
					rect.setColor(Color.ORANGE);

				} else if (i>3 && i<=5) {
					rect.setFilled(true);
					rect.setColor(Color.YELLOW);

				} else if (i>5 && i<=7) {
					rect.setFilled(true);
					rect.setColor(Color.GREEN);

				} else {
					rect.setFilled(true);
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	//makes the paddle given set constants 
	private GRect makePaddle() {
		paddle = new GRect (getWidth()/2.0, getHeight()- PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}

	//helps the mouse move and the paddle move with the mouse 
	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX(); 
		if(mouseX <= 0 ) {
			paddle.setLocation(0,getHeight()- PADDLE_Y_OFFSET);
		} else if(mouseX >= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight()- PADDLE_Y_OFFSET);
		}else { 
			paddle.setLocation(mouseX, getHeight()- PADDLE_Y_OFFSET);
		}
	}

	//makes the ball given the set constants 
	private GOval makeBall() {
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS; 
		ball = new GOval(x, y, BALL_RADIUS*2.0, BALL_RADIUS*2.0);
		ball.setFilled(true);
		add(ball);
		return ball; 
	}
}