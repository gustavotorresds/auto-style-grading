/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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

	// Calculates the number of bricks the player has to start 
	public static final int STARTING_BRICKS = NBRICK_ROWS*NBRICK_COLUMNS;

	double BALL_DIAM = BALL_RADIUS*2.0;
	GOval ball = new GOval(BALL_DIAM, BALL_DIAM);

	private int STARTING_HITS = 0;

	// set velocity of x and y coordinates 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;  
	private double vy;
	private GRect paddle;
	private GObject collider;
	private int turnsRemaining;
	private int bricksRemaining;
	private int paddleHits;

	public void run() { 
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		addMouseListeners();
		turnsRemaining = NTURNS;
		play();
	}

	private void setup() { // creates bricks, paddle, ball and sets velocity of the x coordinate of the ball
		makeBricks();
		makePaddle();
		makeBall();
		vx = rgen.nextDouble(1.0, 3.0); // velocity of x coordinate of ball is randomly chosen between 1.0 and 3.0 
		if (rgen.nextBoolean(0.5)) { // makes velocity of x negative half the time, randomly 
			vx = -vx; 
		}
	}
	private GOval makeBall() { // creates filled black ball starting in the center of the screen
		double startingCoordinateX = (getWidth()/ 2.0 - BALL_RADIUS); //sets the starting x coordinate of the ball
		double startingCoordinateY = (getHeight()/2.0 - BALL_RADIUS); //sets the starting y coordinate of the ball
		ball.setFilled(true); 
		add(ball, startingCoordinateX, startingCoordinateY); // creates oval used as ball with defined coordinates and constant height and width
		return(ball);
	}

	private void makePaddle() { // creates and centers filled paddle at the bottom of the screen with y coordinate offset 
		double xcoordinate = ((getWidth()-PADDLE_WIDTH)/2.0); // sets the starting x coordinate of the paddle 
		double ycoordinate = (getHeight()- PADDLE_Y_OFFSET); // sets the starting y coordinate of the paddle
		paddle = new GRect (xcoordinate, ycoordinate, PADDLE_WIDTH, PADDLE_HEIGHT); //creates rectangle used as paddle with defined coordinates and constant height and width
		paddle.setFilled(true);
		add(paddle);
	}

	public void mouseMoved( MouseEvent e) { // Mouse Event tracks mouse and sets x location of the mouse to be the x coordinate of the paddle 
		if (e.getX() + PADDLE_WIDTH <= getWidth() && e.getX() >= 0) { //makes sure the paddle does'nt leave the screen
			paddle.setLocation(e.getX(), getHeight() - PADDLE_Y_OFFSET); 
		}
	}


	private void makeBricks() {
		double a = BRICK_WIDTH*NBRICK_COLUMNS + BRICK_SEP*(NBRICK_COLUMNS-1); // sets the variable part of the x coordinate of the bricks 
		for (int row = 0; row < NBRICK_ROWS; row++) { // sets the number of rows of bricks depending on NBRICK_ROWS
			for (int col = 0; col < NBRICK_COLUMNS; col++) { // sets the number of bricks in each column depending on NBRICK_COLUMNS
				double x = (((getWidth()-a))/2) + ((BRICK_WIDTH+ BRICK_SEP)*col); //specifies and centers the x coordinate for each brick   
				double y = ((BRICK_HEIGHT+ BRICK_SEP)*row + BRICK_Y_OFFSET); // specifies the y coordinate for each brick 
				GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT); // creates rectangle used as a brick with the defined coordinates and constant height and width
				if (row < 2) { //sequential if loops change the color of the bricks according to what row they are in 
					rect.setColor(Color.RED); 
				} else if (row < 4) {
					rect.setColor(Color.ORANGE);
				} else if (row < 6) {
					rect.setColor(Color.YELLOW);
				} else if (row < 8) {
					rect.setColor(Color.GREEN);
				} else if (row < 10) {
					rect.setColor(Color.CYAN);
				}		
				rect.setFilled(true);
				add(rect);
			}
		}
	}

	private void play() { //creates a loop to play the game 
		vy = VELOCITY_Y; //sets starting velocity of the ball to be VELOCITY_Y
		bricksRemaining= STARTING_BRICKS; //keeps track of bricks remaining in order to stop the game when there are none left
		paddleHits = STARTING_HITS; // keeps track of the number of times the ball hits the paddle to speed up the ball after 5 hits
		waitForClick(); 
		while(turnsRemaining > 0 && bricksRemaining > 0) { //creates loop to move ball when there are still turns and bricks left
			if(paddleHits == 5 ) { //condition that speeds up the ball after 5 hits 
				vx = 6;
				vy = 6;
			}
			ball.move(vx, vy); //moves ball according to vx and vy 
			pause(DELAY);
			checkIfCollidingWithWall(); // checks if the ball is colliding with a wall in order to know when to change direction 
			collider = getCollidingObject(); // sets collider to keep track of what the ball is running into 
			if (collider == paddle ) { //bounces ball upwards if the collider is the paddle 
				vy = -1*Math.abs(vy);
				paddleHits++; //increases paddleHits each time the ball hits the paddle  
			} else if(collider != null){ //if the collider is not null and not a paddle, it is a brick
				bricksRemaining --; //subtracts one from bricksRemaining each time the ball hits one 
				remove(collider); //removes the brick
				collider = null; //sets collider back to null afterwards 
				vy = -vy; //reverses y direction of ball after hit 
			}
			if (turnsRemaining == 0) { //removes ball if the player has hit all bricks 
				remove(ball);
			}
			if (bricksRemaining == 0) { // notifies player of their victory and removes ball from screen
				GLabel YouWon = new GLabel("YOU WON!");
				add(YouWon, (getWidth() - YouWon.getWidth())/2, (getHeight() - YouWon.getAscent())/2);
				remove(ball);
			}
		}
	}			


	private GObject getCollidingObject() { //assesses whether any of the four corners of the ball is touching anything each time it moves
		double x = ball.getX(); //locates x coordinate of ball and set it equal to variable x
		double y = ball.getY(); //locates y coordinate of ball and set it equal to variable y
		GObject object = null; 
		if (getElementAt(x, y) != null && collider != ball) { // sequential if else loops check each corner and register an object if there is one
			object = getElementAt(x, y);
		} else if (getElementAt(x + BALL_DIAM, y) != null && collider != ball) {
			object = getElementAt(x + BALL_DIAM, y);	
		} else if (getElementAt(x, y + BALL_DIAM) != null && collider != ball) {
			object = getElementAt(x, y + BALL_DIAM);		
		} else if (getElementAt(x + BALL_DIAM, y + BALL_DIAM) != null && collider != ball) {
			object = getElementAt(x + BALL_DIAM, y + BALL_DIAM);
		}
		return object;
	}

	private void checkIfCollidingWithWall() { //assesses whether any of the four corners of the ball is touching a wall each time it moves 
		checkLeftWall();
		checkRightWall();
		checkTopWall();
		checkBottomWall();
	}
	private void checkLeftWall() { //ball has negative x velocity if it hits left wall 
		if(ball.getX() < 0) { 
			vx = -vx; 
		}
	}	
	private void checkRightWall() { //ball has negative x velocity if it hits right wall 
		if(ball.getX() > getWidth()-BALL_DIAM) { 
			vx = -vx;
		}
	}
	private void checkTopWall() { //ball has negative y velocity if it hits top wall 
		if(ball.getY() < 0) { 
			vy = -vy;
		}
	}

	private void checkBottomWall() { //ball is removed from screen if it "falls" through bottom wall and subtracts a turn from the player
		double startingCoordinateX = (getWidth()/ 2.0 - BALL_RADIUS);
		double startingCoordinateY = (getHeight()/2.0 - BALL_RADIUS);
		if(ball.getY() > getHeight()-BALL_RADIUS) { 
			remove(ball); 
			turnsRemaining --; //subtracts a turn from player
			if (turnsRemaining <= 0) { //ends game if there are no turns remaining 
				showGameOverLabel();
			} else {
				addTurnsRemaining();
			}
			add(ball, startingCoordinateX, startingCoordinateY); //gives a new ball back to player in starting location once old ball is removed 
			waitForClick();
			ball.move(vx, vy);
		}
	}

	private void showGameOverLabel() { //places "Game Over" label in the center of the screen if the player has no turns left
		GLabel gameOver = new GLabel ("Game Over");
		add( gameOver, (getWidth() - gameOver.getWidth())/2, (getHeight() - gameOver.getAscent())/2);
	}

	private void addTurnsRemaining() { //adds a label to tell the player how many turns they have left if ball touches bottom wall
		GLabel turnsRemainingLabel = new GLabel("Turn(s) Remaining: " + (turnsRemaining));
		add(turnsRemainingLabel, (getWidth() - turnsRemainingLabel.getWidth())/2, (getHeight() - turnsRemainingLabel.getAscent())/2);
		waitForClick();
		remove (turnsRemainingLabel);
	}
}	

