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
import jdk.nashorn.internal.ir.BreakableNode;
import sun.security.jgss.TokenTracker;
import sun.tools.jar.resources.jar;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;

import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 4.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//the paddle
	private GRect paddle;
	
	//instance variables for the velocity in both directions of the ball
	private double vx, vy;
	
	//creates a random number generator to be used for the initial x-velocity of the ball
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//creates an instance variable for any object the ball collides with, either a brick or the paddle
	private GObject getCollidingObject;
	
	//to be used as a counter to determine if/when all bricks are eliminated
	private int numberBricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//amount of time the program waits between lives before launching next ball
	public static final double BETWEEN_TURNS_DELAY = 2500;
	
	//calls the audio clip for sound effects
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		addMouseListeners();
		
		//this forloop endows the user with three lives
		for(int k = 0; k < 3; k++) {
			playGame();
			//if the user eliminates all bricks before using all three lives, this if statement ends the game at that point
			if(numberBricksRemaining == 0) {
				break;
			}
			pause(BETWEEN_TURNS_DELAY);
		}
		//if the user runs out of lives but there are still bricks remaining, this losing message will display.
		if(numberBricksRemaining > 0) {
			losingMessage();
		}
	}
//this method creates the losing message GLabel.
	private void losingMessage() {
		GLabel losingMessage = new GLabel("You lose.", getWidth() / 2, getHeight() / 2);
		losingMessage.setFont("Courier-24");
		add(losingMessage);
		
	}

	private void setUpGame() {
		setUpBricks();
		makePaddle();
	}
	
	private void setUpBricks() {
		/* Same essential structure as pyramid problem from Asgn 2. 
		 * The inner forloop regulates the x-coordinates of each brick (aka each column).
		 * The outer forloop regulates the y-coordinate of each row.
		 * Additionally, because the color of a brick is determined by the row 
		 * which it is in, the value of the integer assigned in the outer for loop
		 * is used in method setBrickColors to determine the color of a row of bricks.
		 */
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for(int z = 0; z < NBRICK_COLUMNS; z++) {
				double brickOneX = (getWidth()-(NBRICK_COLUMNS*BRICK_WIDTH) - (BRICK_SEP * (NBRICK_COLUMNS + 1)) + z * (BRICK_WIDTH + BRICK_SEP));
				double brickOneY = BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * i;
				GRect oneRow = new GRect(brickOneX, brickOneY, BRICK_WIDTH, BRICK_HEIGHT);
				oneRow.setFilled(true);
				//determines color of row of bricks based on value of i, which signifies which row is being colored.
				setBrickColors(i, oneRow);
				add(oneRow);
			}
		}
	}
	//this method determines the color of the row of bricks by checking the value of i, which indicates which row is currently being rendered.
	private void setBrickColors(int i, GRect oneRow) {
		if(i < 2) {
			oneRow.setColor(Color.RED);
			oneRow.setFillColor(Color.RED);
		} else if (i < 4) {
			oneRow.setColor(Color.ORANGE);
			oneRow.setFillColor(Color.ORANGE);
		} else if (i < 6 ) {
			oneRow.setColor(Color.YELLOW);
			oneRow.setFillColor(Color.YELLOW);
		} else if (i < 8) {
			oneRow.setColor(Color.GREEN);
			oneRow.setFillColor(Color.GREEN);
		} else  {
			oneRow.setColor(Color.CYAN);
			oneRow.setFillColor(Color.CYAN);
		}
	}
	//this method creates the paddle.
	private void makePaddle() {
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	/* this method is in charge of directing the movement of the paddle. Provided that
	 * the x coordinate of the mouse + the length of the paddle is less than the width 
	 * of the screen and the x coordinate of the mouse is greater than 0, this method 
	 * sets the x coordinate of the paddle to match the x coordinate of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() + PADDLE_WIDTH < getWidth() && e.getX() > 0) {
			paddle.setX(e.getX());
		}
	}
	// this method executes the game.
	private void playGame() {
		// this creates the GOval ball by calling the ball method (at bottom of code).
		GOval ball = ball();
		/* these set the variables for the velocity of the ball using provided constant for velocity y
		 * and a random value between the x max and x min velocities. It also determines a random beginning
		 * velocity at which to launch the ball to begin the game.
		 */
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		// this while loop is in charge of executing the main functions of the game.
		while(true) {
			ball.move(vx, vy);
			/* these if statements make the ball bounce off the right, left, and top walls,
			 * playing a bounce noise each time.
			 * it also ends the game if the ball hits the bottom wall.
			 */
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
				bounceClip.play();
			}
			if (hitTopWall(ball)) {
				vy = -vy;
				bounceClip.play();
			}
			if (hitBottomWall(ball)) {
				remove(ball);
				break;
			}
			/*  these GPoints will be used in getCollidingObject to determine if the ball is hitting anything 
			 *  at any of its four corners.
			 */ 
			GPoint ballTopLeft = new GPoint(ball.getX(), ball.getY());
			GPoint ballTopRight = new GPoint(ball.getRightX(), ball.getY());
			GPoint ballBottomLeft = new GPoint(ball.getX(), ball.getBottomY());
			GPoint ballBottomRight = new GPoint(ball.getRightX(), ball.getBottomY());
			//this method determines if the ball is hitting anything. See the method for more detailed comments.
			getCollidingObject(ballTopLeft, ballTopRight, ballBottomLeft, ballBottomRight); 
			//if the method getCollidingObject determines that the ball is colliding with something, this if statement is entered.
			if (getCollidingObject != null) {
				//takes the resulting element found in getCollidingObject and turns it into a GObject.
				GObject collider = getCollidingObject;
				// this if statement will run if the colliding object is the paddle.
				if (collider == paddle) {
					/* This solves the sticky paddle problem. When the ball was sticking, vy would constantly alternate between
					 * being negative (the ball was heading back up) and positive (ball heading down) as vy was set to -vy. This inner 
					 * if statement ensures that if vy is negative (i.e. < 0), vy remains negative and the ball simply continues to head 
					 * up, rather than continually being reset to +vy and back to -vy and so on. 
					 */
					if (vy > 0) {
						// when the ball hits the paddle, this reverses its y velocity.
						vy = -vy;	
					}
					bounceClip.play();
				// if the object the ball collides with is not the paddle, this else statement is entered.
				} else {
					// this subtracts from the counter keeping track of how many bricks remain to eventually determine if the user has won
					numberBricksRemaining--;
					vy = -vy;
					// since the object the ball is colliding with, if not the paddle, is a brick, this removes that brick.
					remove(collider);
					bounceClip.play();
				} 
				// this if statement executes if the user has deleted all of the bricks.
				if (numberBricksRemaining == 0) {
					//since the game is over, removes the ball.
					remove(ball);
					// displays a message letting the user know she won.
					GLabel winningMessage = winningMessage();
					add(winningMessage);
					break;	
				}	
			}
			// this pause makes it so the user can see the graphics being rendered.
			pause(DELAY);
		}
	}
	// this method is responsible for creating and setting the text and coordinates of the winning message.
	public GLabel winningMessage() {
		GLabel winningMessage = new GLabel("You Win!", getWidth() / 2, getHeight() / 2);
		winningMessage.setFont("Courier-24");
		return winningMessage;
	}
	/* this method determines if the ball is hitting anything by checking at each of the four
	 * corners of the square enclosing the ball to see if there is an object at that point. The
	 * method checks each of the four points in succession. If there is no object at the point it
	 * is checking, getElementAt will return "null" and the method will move on the check the next
	 * corner until it has checked all four corners. 
	 */
	private void getCollidingObject(GPoint ballTopLeft, GPoint ballTopRight, GPoint ballBottomLeft,
			GPoint ballBottomRight) {
		getCollidingObject = getElementAt(ballBottomLeft);
		if (getCollidingObject == null) {
			getCollidingObject = getElementAt(ballBottomRight);
		} if (getCollidingObject == null) {
			getCollidingObject = getElementAt(ballTopLeft);
		} if (getCollidingObject == null) {
			getCollidingObject = getElementAt(ballTopRight);
		}
	}
	// these booleans determine if the ball is hitting any of the walls.
	private boolean hitBottomWall(GOval ball) {
		// returns if the ball's y coordinate is greater than the height of the screen minus the ball's height.
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		// returns if the ball's y coordinate is less than the height of the screen.
		return ball.getY() <= 0;
	}
	
	private boolean hitRightWall(GOval ball) {
		// returns if the ball's x coordinate is greater than the width of the screen minus the ball's height.
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		// returns if the ball's x coordinate is less than the leftmost side of the screen.
		return ball.getX() <= 0;
	}
	//this GOval creates the ball and initially places it in the center of the screen.
	private GOval ball() {
		double ballStartX = getWidth() / 2 - BALL_RADIUS;
		double ballStartY = getHeight() / 2 - BALL_RADIUS;
		GOval ball = new GOval(ballStartX, ballStartY, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
