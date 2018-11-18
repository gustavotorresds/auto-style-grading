
/*
 * File: Breakout.java
 * -------------------
 * Name: Grace Connor
 * Section Leader: Andrew Davis 
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import stanford.cs106.util.RandomGenerator;

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

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Instance variables for objects in the program (specifically the ball, paddle, and bricks)
	private GOval ball;
	private GRect paddle;
	private GRect brick;
	
	// X & y velocities of the ball 
	private double vx;
	private double vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Keeps track of things including how many bricks are left, how many turns were used,
	//the starting x coordinate of the bricks, and what the object being hit by the ball is
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;
	private int turnsUsed;
	private double firstBrickXCoord;
	GObject objectBeingHit;
	
	/*This is the code for the computer game brick breaker. In this game, the user moves a paddle at 
	 * the bottom of the screen around by moving their mouse. They are trying to keep a bouncing ball 
	 * from falling to the ground and have the ball hit (and thus remove) a chunk of bricks from the 
	 * top of the screen. Every time the ball falls to the ground the user looses a life; in this version
	 * of the game the user gets 3 lives. The user wins by removing all the bricks from the screen and looses
	 * if the ball falls to the ground 3 times before this task is completed. 
	 */

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp(); //adds the bricks and the paddle 
		addMouseListeners();
		turnsUsed = 0; //when the game starts, the user has used 0 lives 
		while (turnsUsed < NTURNS) { //they play the game until they've used all their turns
			addBall();
			waitForClick();
			animateBall(); //moves ball and checks for collisions with paddle, bricks, and walls
		}
		addGameOverLabel();
	}

	//puts a label on the screen telling the user when they've lost the game
	private void addGameOverLabel() { 
		GLabel gameover = new GLabel("Game Over!");
		double x = getWidth() / 2 - gameover.getWidth() / 2;
		double y = getHeight() / 2 - gameover.getAscent() / 2;
		add(gameover, x, y);
	}

	//All the possible collision checks for the ball
	private void checkForCollisions() {
		checkForCollisionsWithBrick();
		checkForCollisionsWithWall();
		checkForCollisionsWithPaddle();
	}

	//Initial setup of the bricks and the paddle on the screen 
	private void setUp() {
		buildBricks();
		buildPaddle();
	}

	//Moves the ball and calls checkForCollisions above 
	private void animateBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); //Generates a random starting x velocity between the min and the max desired velocities
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) { //This makes the x velocity negative half the time so when the
			vx = -vx; //ball is initially fired it doesn't always go to the right 
		}
		int currentTurns = turnsUsed; 
		while (bricksLeft > 0 && currentTurns == turnsUsed) { //The ball should move when the bricks aren't cleared and the max number of lives hasn't been reached
			ball.move(vx, vy);
			pause(DELAY);
			checkForCollisions();
		}
	}

	//Checks for collisions between the ball and bricks. It checks all 4 corners, one at a time, only 
	//moving on to check the next corner if the first one didn't hit a brick. When a brick is hit
	//it is removed from the screen. 
	private void checkForCollisionsWithBrick() {
		double x1 = ball.getX();
		double x2 = ball.getX() + BALL_RADIUS * 2;
		double y1 = ball.getY();
		double y2 = ball.getY() + BALL_RADIUS * 2;
		if (vy != -vy) { //
			brickCollisionCheckMethod(x1, y1);
		} else if (vy != -vy) {
			brickCollisionCheckMethod(x1, y2);
		} else if (vy != -vy) {
			brickCollisionCheckMethod(x2, y1);
		} else if (vy != -vy) {
			brickCollisionCheckMethod(x2, y2);
		}
	}

	//General method for checking for brick collisions. If one corner of the ball hits a brick and
	//this method is executed, the brick is removed, the Y velocity of the ball switches directions,
	//and one is subtracted from our count of bricks left. 
	private void brickCollisionCheckMethod(double xCoord, double yCoord) {
		objectBeingHit = getElementAt(xCoord, yCoord);
		if (objectBeingHit != paddle && objectBeingHit != null) {
			remove(objectBeingHit);
			bricksLeft -= 1;
			vy = -vy;
		}
	}

	//Checks if the ball has hit the paddle. If it does hit the paddle, the y velocity
	//switches directions.
	private void checkForCollisionsWithPaddle() {
		if (ball != null) {
			GObject objectBeingHit = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
			if (objectBeingHit == paddle) {
				vy = -vy;
			}
		}
	}

	//Checks if the ball hits one of the four walls. Something different happens to the ball 
	//depending on which wall it hits. 
	private void checkForCollisionsWithWall() {
		double diameter = BALL_RADIUS * 2;
		if (ball.getX() + diameter >= getWidth()) { //If the ball hits the wall 
			vx = -vx; //The X velocity switches 
		}
		if (ball.getX() - BALL_RADIUS * 2 + diameter <= 0) { //If the ball hits the left wall 
			vx = -vx; //The X velocity switches 
		}
		if (ball.getY() - BALL_RADIUS * 2 <= 0) { //If the ball hits the top wall 
			vy = -vy; //The Y velocity switches 
		}
		if (ball.getY() >= getHeight() - BALL_RADIUS * 2) { //If the ball hits the bottom wall 
			turnsUsed++; //One is added to the players "turns used" count 
			remove(ball); //The ball is removed from the screen 
		}
	}

	//Keeps track of the mouse being moved and makes the paddle follow it 
	public void mouseMoved(MouseEvent e) {
		double newXCoord = e.getX(); //Gets the X coordinate of the mouse
		if (newXCoord <= getWidth() - PADDLE_WIDTH) { //Keeps the paddle within the bounds of the canvas
			paddle.setLocation(newXCoord, getHeight() - PADDLE_Y_OFFSET); //Resets paddle's location to the mouse's location
		}
	}

	//Builds the ball and adds it to the middle of the screen for the start of the game
	private void addBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		double xCoord = getWidth() / 2 - BALL_RADIUS;
		double yCoord = getHeight() / 2 - BALL_RADIUS;
		add(ball, xCoord, yCoord);
	}

	// Builds the paddle, adding it to the starting position
	// at the bottom middle of the screen
	private void buildPaddle() {
		double xCoord = getWidth() / 2.0 - PADDLE_WIDTH / 2.0;
		double yCoord = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(xCoord, yCoord, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	//Builds the rainbow of bricks 
	private void buildBricks() {
		firstBrickXCoord = getWidth() / 2 - NBRICK_COLUMNS * (BRICK_WIDTH+BRICK_SEP) / 2; //The x coordinate of the first brick
		for (int row = 0; row < NBRICK_ROWS; row++) { //This loop is for the row number 
			for (int brick = 0; brick < NBRICK_COLUMNS; brick++) { //This loop is for the brick number within a row 
				double x = firstBrickXCoord + brick * (BRICK_WIDTH+BRICK_SEP); //X coordinate of every brick in the rainbow
				double y = row * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET; //Y coordinate of every brick in the rainbow 
				if (row < 2 && row >= 0) { //Builds the first two rows red 
					buildOneBrick(Color.RED, x, y);
				}
				if (row < 4 && row >= 2) { //Builds the next two rows orange
					buildOneBrick(Color.ORANGE, x, y);
				}

				if (row < 6 && row >= 4) { //Builds the next two rows yellow
					buildOneBrick(Color.YELLOW, x, y);
				}
				if (row < 8 && row >= 6) { //Builds the next two rows green
					buildOneBrick(Color.GREEN, x, y);
				}
				if (row < 10 && row >= 8) { //Builds the final two rows blue 
					buildOneBrick(Color.CYAN, x, y);
				}
			}
		}
	}

	//Basic method for building one brick. The rectangles made from this method will all be the
	//same size but can be different colors and have different x and y coordinates 
	private void buildOneBrick(Color brickColor, double xCoordinate, double yCoordinate) {
		brick = new GRect(xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(Color.WHITE);
		brick.setFilled(true);
		brick.setFillColor(brickColor);
		add(brick);
	}

}
