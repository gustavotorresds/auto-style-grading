/*
 * File: Breakout.java
 * -------------------
 * Name: Eli Feierabend-Peters
 * Section Leader: Meng Zhang
 * 
 * This file implements the game Breakout. I added labels to instruct the user what to do at any point during the game.
 * But basically, they have 3 turns to try to destroy all the bricks by hitting the ball into them.
 * They lose turns/lives if the ball falls down to the bottom of the screen without the user hitting the ball back upwards with the paddle
 * I added a few extensions in terms of labels and such, but basically the program runs normally
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;

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
	public static final double DELAY = 5; //1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// this is the amount that I shift things by for ideal visual placement
	private static final int SHIFT = 20;
	
	// this sets up my audio clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	// this is my instance variable for my paddle so I can interact with the paddle throughout without the mess of too many parameters
	private GRect paddle;
	// this is my instance variable that represents the horizontal and vertical velocities of the ball
	private double vx, vy;
	// this is my instance variable for the ball, this object is used throughout the program
	private GOval ball;
	// this is my instance variable that handles how many lives are left (this is used for ending conditions)
	private int lives = NTURNS;
	// this is my instance variable that handles how many bricks are left (this is used for ending conditions)
	private int bricksLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	
	// this is my run method that sets up the world, builds the paddle, and then runs the game
	public void run() {
		setUp();
		addMouseListeners();
		paddle();
		brickBreaker();
	}
	// this method handles the setup of the program including the building of the bricks
	private void setUp() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// this double for loop builds 10 rows of 10 bricks centered in the window based on our constants found above
		for (int col = 0; col < NBRICK_ROWS; col++) {
			for (int row = 0; row < NBRICK_COLUMNS; row++) {
				GRect rect = new GRect(getWidth()/2-NBRICK_COLUMNS/2*(BRICK_SEP + BRICK_WIDTH)+ BRICK_SEP/2, BRICK_Y_OFFSET+BRICK_HEIGHT*NBRICK_ROWS+BRICK_SEP*(NBRICK_ROWS-1), BRICK_WIDTH, BRICK_HEIGHT);
				rect.move((BRICK_SEP+BRICK_WIDTH)*row, -(BRICK_SEP+BRICK_HEIGHT)*col);
				rect.setFilled(true); // this fills in our bricks
				Color setColor = pickColor(col); // this creates a local variable that is equal to the return of the method below "pickColor(int col)"
				rect.setColor(setColor); // this sets the color of the brick to be the color of the above local variable
				add(rect); // add the brick
			}
		}
	}
	// this methods takes in "int col" which represents how many bricks into the column the for loop is at and modifies the color for each
	private Color pickColor(int col) {
		Color color = Color.CYAN; // the first two rows are cyan
		if (col == 2 || col == 3) { // the second two rows are green
			color = Color.GREEN;
		} else if (col == 4 || col == 5) { // the 5th and 6th rows are yellow
			color = Color.YELLOW;
		} else if (col == 6 || col == 7) { // the 7th and 7th rows are orange
			color = Color.ORANGE;
		} else if (col == 8 || col == 9) { // the 9th and 10th rows are red
			color = Color.RED;
		}
		return color; // the method returns "color" which is the appropriate color for a given row
	}
	// this method initializes and builds the paddle
	private void paddle() {
		paddle = new GRect(getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT); // initialize the paddle
		paddle.setFilled(true);
		add(paddle); // add the paddle
	}
	/* 
	 * This method handles mouse events
	 * the paddle moves to the x location of the mouse while the y location stays constant
	 * this allows the user to move the paddle back and forth across the screen with conditions that keep the paddle from leaving the window
	 */ 
	public void mouseMoved(MouseEvent e) {
		int x = e.getX(); // this local variable is equal to the x location of the mouse
		GPoint point = new GPoint(x-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); // this point is the x location of the mouse shifts so that the mouse corresponds with the center of the paddle as well as maintaining its fixed y location at the bottom 
		if (x > PADDLE_WIDTH/2 && x < getWidth()-PADDLE_WIDTH/2) { // checks that the location of the mouse will keep the paddle in the window 
			paddle.setLocation(point); // the mouse will move to x location of the mouse
		} 
	} 
	// this method initializes and builds the ball
	private void ball() {
		ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2); // initialize the ball
		ball.setFilled(true);
		add(ball); // add the ball
	}
	// this method initializes and chooses the velocity of the ball in the game 
	private void velocity() {
		RandomGenerator rgen = RandomGenerator.getInstance();
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(.5)) {
			vx = -vx;
		}
	}
	//this method handles the logistics of the game
	private void brickBreaker() {
			for (int i = 0; i < NTURNS; i++) { // this for loop runs as many times as the user gets turns/lives			
					ball();
					ballMove();
			}
	}
	// this method returns the label that introduces the user to the game
	private GLabel starterLabel() {
		GLabel startScreen = new GLabel("Welcome to Breakout! Click to start the game", getWidth()/2, getHeight()/2); // initialize the label
		startScreen.move(-startScreen.getWidth()/2, -startScreen.getAscent()/2-SHIFT);
		return startScreen; // return the label to be called elsewhere
	}
	// This method handles the movement of the ball along with a few supplementary details to help game flow
	private void ballMove() {
		GLabel loseLife = new GLabel("You lost a life!", getWidth()/2, getHeight()/2); // this initializes a label that tells the user they lost a life
		loseLife.move(-loseLife.getWidth()/2, -loseLife.getAscent()/2-SHIFT);
		if (lives < NTURNS) {
			add(loseLife); // that label is only displayed after the first round, because we only can get back here after the first time in the event the user lost a life
		}
		GLabel startingDisplay = starterLabel(); // initializes a label to the return of our method that returns a starting label to introduce the user to the game
		if (lives == NTURNS && bricksLeft > 0) {		
			add(startingDisplay); // the label only is added on the first time this method is called
		}
		velocity(); // this picks our velocities for our ball
		waitForClick(); // the user must click to start the game
		remove(loseLife); // upon clicking the labels go away
		remove(startingDisplay);
		ballInteractors(); // run the method that handles the actual movement and interactions of the ball
		end(); // run the method that handles the ending conditions
	}
	// This method handles what happens when the ball interacts with the edges of the world aka the 3 walls and the bottom
	private void ballInteractors() {
		while (lives > 0 && bricksLeft > 0) { //the ball will move around unless the ending conditions break the loop (or very specific things trigger breaks)
			pause(DELAY); // this delay makes the game run more smoothly
			ball.move(vx, vy); // using "move" in a while loop with the chosen velocities means that the ball will keep moving
			collisionControl(); // this determines what collision is happening
			if (ball.getX()<=0 || ball.getX()+BALL_RADIUS*2>=getWidth()) {
	            vx = -vx; //if the ball hits side walls its horizontal velocity switches signs
	        }
	        if (ball.getY()<=0) {
	            vy = -vy; //if ball hits top, its vertical velocity swaps signs
	        } if (ball.getY()>= getHeight()) { 
	        		lives--; // if the ball gets past the paddle, the user loses and life and we break out of the loop
	        		remove(ball);
	        		break;
	        }    
		}
	}
	/*
	 * This method checks to see if the ball is colliding with anything,
	 * by checking to see if any of the 4 corners of the bounding box of the ball come into contact with other objects
	 */
	private GObject getCollidingObject() {
		// checks the upper left corner of the bounding box of the ball for collisions
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		// checks the upper right corner of the bounding box of the ball for collisions
		else if (getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2) !=null) {
			return getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2);
		}
		// checks the bottom left corner of the bounding box of the ball for collisions
		else if (getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()) !=null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());
		}
		// checks the bottom right corner of the bounding box of the ball for collisions
		else if (getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2) !=null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2);
		} else {
			return null;
		} 
	}
	/* This method handles what to do in case of a collision between the ball and another object
	 * in the case of the collision the program changes the velocity and depending on if it is a collision with the paddle or a brick it acts differently
	 */
	private void collisionControl() {
		if (getCollidingObject() != null) { // this checks to see that a collision occurred
			GObject collider = getCollidingObject(); // local variable that is the object that the ball is colliding with
			if (collider == paddle) { // if the colliding object is the paddle then...
				vy = -Math.abs(vy); // we want to send the ball upwards (by using the absolute value we resolve the sticky paddle issue which occurs when the program thinks the ball hit the ball more than once.  
				bounceClip.play(); // play sound upon collision
			} else { // if the colliding object is anything but the paddle we can assume it is a brick (we can't use "collider == brick" because each brick is its own object
				remove(collider); // remove the brick
				bounceClip.play(); // play the colliding sound
				bricksLeft--; //subtract 1 from the count of total bricks on each collision, so we know when all the bricks get destroyed
				velocity(); // this is used to select new random horizontal velocity
				if (ball.getY() >= collider.getY()) { // if the ball is coming from below the bricks it should bounce back downwards even if it hits more than one brick
					vy = Math.abs(vy); 
				} else if (ball.getY() <= collider.getY()) { // if the ball is coming from above the bricks (aka from a deflection off the top) it should bounce in the opposite direction
					vy = -vy;
				}
			}
		}
	}
	/* this method handles our ending conditions
	 *  if the user ran out of lives then the program runs the losing conditions
	 *  if there are no more bricks left then the program runs the winning conditions
	 */
	private void end() {
		if (lives == 0) { // this runs if the user runs out of lives
			removeAll(); // clear the screen
			GLabel youLose = new GLabel("You Lose!", getWidth()/2, getHeight()/2); // display losing message
			youLose.move(-youLose.getWidth()/2, youLose.getAscent()/2);
			youLose.setColor(Color.RED);
			add(youLose);
		} else if (bricksLeft == 0) { // this runs if the user destroys all the bricks
			removeAll(); // clear the screen
			GLabel youWin = new GLabel("Congratulations! You Win.", getWidth()/2, getHeight()/2-50); // display winning message
			youWin.move(-youWin.getWidth()/2, youWin.getAscent()/2);
			youWin.setColor(Color.GREEN);
			add(youWin);
		}
	}
}
