/*
 * File: Breakout.java
 * -------------------
 * Name: Grace Cotter
 * Section Leader:Tessera Chin
 * 
 * This file implements the game of breakout. The user bounces a ball off a paddle
 * (which they control with the mouse). Each time the ball collides with a brick (which
 * is displayed on top of the screen in rainbow order), that brick is removed. The game ends
 * when the user either wins by removing all of the bricks or when they lose due to the fact
 * that they let the ball fall below the paddle and hit the bottom wall (NTURNS) times. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.xml.internal.ws.org.objectweb.asm.Label;

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
	
	//defines the paddle so it is visible to the entire class
	GRect paddle = new GRect(0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//sets the velocity of the ball
	//the y velocity is consistent
	//the x velocity is randomly generated so the ball moves in a different direction
	//at the start of each round
	double vy = VELOCITY_Y;
	double vx = rgen.nextDouble(1.0, 3.0);

	//counts the number of turns the player has
	int counter = NTURNS;
	
	//counts number of bricks left in game
	int bricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;

	public void run() {
		addMouseListeners();

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();
		moveBall();
	}


	private void setUp() {
		placeBricks();
		paddle.setFilled(true);
		add(paddle);
	}

	public void mouseMoved (MouseEvent e) {
		int x = e.getX();
		//keeps the paddle from going off the screen to the left
		if(x <= 0) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		}
		//keeps the paddle from going off the screen to the right
		if (x >= (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
		//sets the paddle to where the mouse is
		else {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	private Color getColor(int row) {

		//assigns a number 0-4 for each color 
		//based on the row of bricks, it determines what color the brick is
		int colorIndex = (row % 10)/2;

		if(colorIndex == 0) {
			return Color.RED; 
		}
		if(colorIndex == 1) {
			return Color.ORANGE; 
		}
		if(colorIndex == 2) {
			return Color.YELLOW; 
		}
		if(colorIndex == 3) {
			return Color.GREEN; 
		}
		if(colorIndex == 4) {
			return Color.CYAN; 
		}

		//just in case - this should never happen
		return Color.BLACK;
	}

	private void placeBricks() {

		//loops through for the number of rows to set up each row
		for(int i = 0; i < NBRICK_ROWS;  i++) {

			Color brickColor = getColor(i);

			//goes and sets up each individual row
			//loops through for the number of columns
			for(int g = 0; g < NBRICK_COLUMNS; g++ ) {
				//x in terms of column
				double startX = ((getWidth() - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS))/2);
				double x = startX  + ((BRICK_WIDTH + BRICK_SEP) * g);
				// y in terms of row
				double y = (BRICK_Y_OFFSET) + ((BRICK_SEP + BRICK_HEIGHT) * i);

				//adds the bricks 
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(brickColor);
				add(brick);
			}
		}
	}

	private void moveBall() {	
		//builds the ball and sets it in the middle of the screen 
		double x = (getWidth()/2) - BALL_RADIUS;
		double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * NBRICK_ROWS;
		GOval ball = new GOval (x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);

		//makes the x velocity negative half of the time 
		if(rgen.nextBoolean(.5)) {
			vx = -vx;
		}

		//animation loop 
		while(counter > 0 && bricksLeft > 0) {
			ball.move(vx, vy);
			pause(DELAY);

			//top left corner
			double topLeftX = ball.getX();
			double topLeftY = ball.getY();
			//top right corner
			double topRightX = ball.getX() + (2*BALL_RADIUS);
			double topRightY = ball.getY();
			//bottom left corner
			double bottomLeftX = ball.getX();
			double bottomLeftY = ball.getY() + (2*BALL_RADIUS);
			//bottom right corner
			double bottomRightX = ball.getX() + (2*BALL_RADIUS);
			double bottomRightY = ball.getY() + (2*BALL_RADIUS);


			//checks each corner to see if it hits any objects
			//if else statements so that the ball only takes off one brick at a time
			if(getElementAt(topLeftX, topLeftY) != null) {
				//if for some reason the ball gets below the paddle, it will
				//just turn around and the user will lose a turn
				if(getElementAt(topLeftX, topRightY) == paddle) {
					vy = -vy;
				}
				else{
					getCollidingObject(topLeftX, topLeftY);
					vy = -vy;
				}
			}
			else if(getElementAt(topRightX, topRightY) != null) {
				//if for some reason the ball gets below the paddle, it will
				//just turn around and the user will lose a turn
				if(getElementAt(topRightX, topRightY) == paddle) {
					vy = -vy;
				}
				getCollidingObject(topRightX, topRightY);
				vy = -vy;
			}
			else if(getElementAt(bottomLeftX, bottomLeftY) != null) {
				//checks the bottom corner to bounce the ball off the paddle 
				if(getElementAt(bottomLeftX, bottomLeftY) == paddle) {
					vy = -vy;
				}
				else {
					getCollidingObject(bottomLeftX, bottomLeftY);
					vy = -vy;
				}

			}
			else if(getElementAt(bottomRightX, bottomRightY) != null) {
				//checks bottom corner to bounce the ball off the paddle 
				if(getElementAt(bottomRightX, bottomRightY) == paddle) {
					vy = -vy;
				}
				else {
					getCollidingObject(bottomRightX, bottomRightY);
					vy = -vy;
				}
			}

			//if the ball hits the bottom of the screen 
			if(bottomRightY >= getHeight()  || bottomLeftY >= getHeight()) {
				ball.setLocation(x,y);
				vx = rgen.nextDouble(1.0, 3.0);
				if(rgen.nextBoolean(.5)) {
					vx = -vx;
				}
				counter --;
			}

			//bounces the ball off the walls of the world
			if(ball.getY() >= getHeight() - BALL_RADIUS * 2) {
				vy = -vy;
			}
			if(ball.getX() >= getWidth() - BALL_RADIUS *2) {
				vx = -vx;
			}
			if(ball.getX() <= 0) {
				vx = -vx;
			}
			if(ball.getY() <= 0) {
				vy = -vy;
			}
		}

		//tells the user if they won or lost the game
		winOrLose();
	}

	private void getCollidingObject(double x, double y) {
		GObject collider = (getElementAt(x, y));
		//gets rid of any bricks hit by the ball
		if(collider != paddle) {
			remove(collider);
			bricksLeft --;
		}
	}

	private void winOrLose() {
		//if the program ended because the user hit all of the bricks
		if(bricksLeft == 0) {
			GLabel winner = new GLabel ("YOU WIN!");
			winner.setLocation(getWidth()/2 - winner.getWidth()/2, winner.getHeight());
			winner.setColor(Color.BLUE);
			add(winner);
		}
		//if the program ended because the user ran out of turns
		if(counter == 0) {
			GLabel loser = new GLabel ("YOU LOSE :(");
			loser.setLocation(getWidth()/2 - loser.getWidth()/2, loser.getHeight());
			loser.setColor(Color.RED);
			add(loser);
		}
	}
}











































