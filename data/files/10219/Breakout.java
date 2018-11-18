
/*
 * File: Breakout.java
 * -------------------
 * Name: Christina Ding
 * Section Leader: Semir
 * 
 * This file will eventually implement the game of Breakout. The goal of the game is to break all the bricks with the ball, which
 * bounces around the screen hitting the walls, the paddle, and the bricks. The user has three tries.
 * EXTENSION: 1. added scoreboard that increments 5, 10, 15, 20, 25 points for every cyan, green, yellow, orange, red brick destroyed, respectively
 * 			  2. created YOU WON/GAME OVER messages 
 * 			  3. added sound for every time ball hits paddle or brick
 * 			  4. doubles the vx movement speed of ball after 7 hits on paddle
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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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
	public static final double VELOCITY_X_MIN = 4.0;
	public static final double VELOCITY_X_MAX = 6.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;
	
	// Create paddle instance
	private GRect paddle;

	// Horizontal and vertical velocities
	private double vx, vy;

	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Create ball instance
	private GOval ball;

	// Create audio clip using the bounce sound in bounce.au
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	//initialize a score counter
	private int scoreCount = 0;
	
	//create score board label instance
	private GLabel score;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		drawBricks(); //draws all the colored bricks
		drawPaddle(); //draws the paddle
		addScore(); //add scoreboard
		addMouseListeners(); //listens to user's mouse movements
		moveBall(); //moves ball
	}

	private void drawBrick(double startingBrickWidth, double startingBrickHeight, int row) { // draw brick method
		GRect brick = new GRect(startingBrickWidth, startingBrickHeight, BRICK_WIDTH, BRICK_HEIGHT); //create brick object
		brick.setFilled(true); //set brick as filled
		switch (row / 2) { //switch the color of the brick row every two rows
		case 0: //first group of two rows is red
			brick.setColor(Color.RED);
			break;
		case 1: //second group of two rows is orange
			brick.setColor(Color.ORANGE);
			break;
		case 2: //second group of two rows is yellow
			brick.setColor(Color.YELLOW);
			break;
		case 3: //second group of two rows is green
			brick.setColor(Color.GREEN);
			break;
		case 4: //second group of two rows is cyan
			brick.setColor(Color.CYAN);
			break;
		}

		add(brick); //add brick
	}

	private void drawBricks() { //draw the row of bricks
		double startingBrickHeight = BRICK_Y_OFFSET; //set starting brick height to be the offset
		for (int row = 0; row < NBRICK_ROWS; row++) { //loop each row
			double startingBrickWidth = BRICK_SEP; //set starting brick width to the left of the screen (0) plus the brick separation
			for (int col = 0; col < NBRICK_COLUMNS; col++) { //loop each column (each brick in the row)
				drawBrick(startingBrickWidth, startingBrickHeight, row); // draw brick according to starting brick width
				startingBrickWidth += (BRICK_WIDTH + BRICK_SEP); // place the next brick by moving one brick width apart and one BRICK_SEP apart
			}
			startingBrickHeight += (BRICK_HEIGHT + BRICK_SEP); // after the loop ends (all bricks in row placed down), move down a row
		}
	}

	private void drawPaddle() { //draw paddle method
		double paddleStartingWidth = (getWidth() / 2) - (PADDLE_WIDTH / 2); //starting width of paddle, puts it in middle of screen
		double paddleStartingHeight = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET; //starting height of paddle, puts it in middle of screen
		paddle = new GRect(paddleStartingWidth, paddleStartingHeight, PADDLE_WIDTH, PADDLE_HEIGHT); //create new paddle object
		paddle.setFilled(true); //set filled
		paddle.setColor(Color.BLACK); //set black
		add(paddle); //add to canvas
	}

	public void mouseMoved(MouseEvent e) { //mouse tracker method
		double paddleStartingHeight = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		double x = e.getX(); //get x value of mouse
		paddle.setLocation(x, paddleStartingHeight); //move the paddle location to x value of mouse
		if (x >= getWidth() - PADDLE_WIDTH) { //if the mouse goes too far across the screen, make sure paddle does not go off screen
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleStartingHeight);
		}
		pause(DELAY); //delay so you have enough time to see the movement
	}

	private void moveBall() { //move ball method
		int bricks = NBRICK_ROWS * NBRICK_COLUMNS; //set a count for the number of bricks
		int count = 0; //set a count to speed up ball after 7 hits on paddle
		for (int i = NTURNS; i > 0; i--) { //for loop for 3 turns (NTURNS)
			ball = new GOval(BALL_RADIUS, BALL_RADIUS); //create new ball object
			ball.setFilled(true); //set filled
			ball.setColor(Color.BLACK); //make black
			add(ball, getWidth() / 2, getHeight() / 2); //add ball to middle of screen
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); //make vx a random number between x_min and x_max constants
			vy = VELOCITY_Y; //vy is velocity y constant
			if (rgen.nextBoolean(0.5)) { //alternate between vx directions
				vx = -vx;
			}
			while ((ball.getY() + (BALL_RADIUS * 2)) < getHeight()) { //while ball has not hit the bottom of screen yet
				if (ball.getX() <= 0 || (ball.getX() + (BALL_RADIUS * 2)) >= getWidth()) { //if ball hits either left or right wall
					vx = -vx; //change x direction
				}
				if (ball.getY() <= 0) { //if ball hits the top of screen
					vy = -vy; //change y direction
				}
				ball.move(vx, vy); //move the ball vx, vy direction
				pause(DELAY); //delay
				GObject collider = getCollidingObject(); //check collider by calling method
				if (collider == paddle) { //if collider is paddle, play bounce audio, change direction of ball movement, and increment count
					count++;
					bounceClip.play();
					vy = -vy;
					count++;
				} else if (collider != null) { //else if collider is not null (so collider is a brick), play bounce audio, remove brick, and decrement brick counter and change ball's y direction
					if(collider.getColor() == Color.CYAN) { //if brick hit is cyan, add 5 points to score
						scoreCount += 5;
						score.setLabel("SCORE: " + scoreCount);
					} else if(collider.getColor() == Color.GREEN) { //if brick hit is green, add 10 points to score
						scoreCount += 10;
						score.setLabel("SCORE: " + scoreCount);
					} else if(collider.getColor() == Color.YELLOW) { //if brick hit is yellow, add 15 points to score
						scoreCount += 15;
						score.setLabel("SCORE: " + scoreCount);
					} else if(collider.getColor() == Color.ORANGE) { //if brick hit is orange, add 20 points to score
						scoreCount += 20;
						score.setLabel("SCORE: " + scoreCount);
					} else if(collider.getColor() == Color.RED) { //if brick hit is red, add 25 points to score
						scoreCount += 25;
						score.setLabel("SCORE: " + scoreCount);
					}
					bounceClip.play();
					remove(collider);
					bricks--;
					vy = -vy;
				}				
				if (bricks == 0) { //once the number of bricks has reached zero (all bricks destroyed), remove the ball and display congratulations screen and break the loop.
					remove(ball);
					congratsMessage();
					break;
				}
				if(count == 7) { //if count reaches 7 (ball hits paddle 7 times), double the x velocity
					vx *= 2;
				}
			}
			remove(ball); //remove ball after three turns are up
		}
		endMessage(); //add the game over message
	}

	private GObject getCollidingObject() { //check colliding object method
		GObject collider = null; //set collider as null
		collider = getElementAt(ball.getX(), ball.getY()); //check top left corner of ball for collision
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()); //check bottom right corner of ball for collision
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2); //check top right corner of ball for collision
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2); //check bottom left corner of ball for collision
		if (collider != null) {
			return collider;
		}
		return collider;
	}

	private void endMessage() { //if player loses, display game over message in middle of screen
		GLabel label = new GLabel("GAME OVER");
		add(label, (getWidth() / 2) - (label.getWidth() / 2), (getHeight() / 2) + (label.getHeight() / 2));
	}
	
	private void congratsMessage() { //if play wins, display congratulatory message in middle of screen
		GLabel label = new GLabel("YOU WON!");
		add(label, (getWidth() / 2) - (label.getWidth() / 2), (getHeight() / 2) + (label.getHeight() / 2));
	}
	
	private void addScore() { //create new score board label object at middle top of screen
		score = new GLabel("SCORE: ");
		add(score, (getWidth() / 2) - (score.getWidth() / 2), (score.getHeight() * 2));
	}
}
