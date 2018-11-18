/*
 * File: Breakout.java
 * -------------------
 * Name: Thomas Henri
 * SUID: thenri
 * Section Time: Wednesday 4:30
 * Date: 2/7/18
 * Section Leader: Ben Barnett
 * 
 * This file implements the game of Breakout.
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
	public static final double DELAY = 1000.0 / 90.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Game pauses for 1 second when you lose a life for you to reflect on
	//your poor video game skills
	private static final double YOU_SHOULD_FEEL_BAD_DELAY_TIME = 1000;

	//game pauses for .5 second to wait for player to get ready
	private static final double OH_BOY_GET_READY_DELAY_TIME = 500;

	private GRect paddle;
	private GOval ball;
	private double mouseX, vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		addMouseListeners();
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle();
		createBall();
		int turnNumber = 0;
		int bricksRemoved = 0;
		while (turnNumber < NTURNS && bricksRemoved < NBRICK_COLUMNS*NBRICK_ROWS) {
			paddle.setLocation(mouseX, getHeight()-PADDLE_Y_OFFSET);
			ball.move(vx, vy);

			if (hitRightWall() || hitLeftWall()) {
				vx = -vx;
			}
			if (hitTopWall()) {
				vy = -vy;
			}

			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -vy * 1.008;
				if(ball.getX() + BALL_RADIUS < paddle.getX() + 0.5 * PADDLE_WIDTH && vx > 0) {
					vx = -vx;
				}
				if (ball.getX() + BALL_RADIUS > paddle.getX() + 0.5 * PADDLE_WIDTH && vx <0) {
					vx = -vx;
				}
				//the following if statement solves the sticky paddle bug by moving the ball
				//to above the paddle if it hits the paddle below the height of the top of the paddle
				if (ball.getY() > getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT-2 * BALL_RADIUS) {
					ball.setLocation(ball.getX(), getHeight() - PADDLE_Y_OFFSET-PADDLE_HEIGHT - 2 * BALL_RADIUS);	
				}
			}

			if (collider != null && collider != paddle) {
				remove (collider);
				bricksRemoved++;
				vy = -vy * 1.008;
			}

			if (hitBottomWall()) {
				pause(YOU_SHOULD_FEEL_BAD_DELAY_TIME);
				turnNumber++;
				if (turnNumber<NTURNS) {
					reset();
				} else {
					remove(ball);
				}
			}

			pause(DELAY); 
		}

		if (turnNumber == NTURNS) {
			displaySadEndgameMessage();
		} else {
			displayVictoriousEndgameMessage();
		}
	}

	/*
	 * Method: Display Victorious Endgame Message
	 * ___________________________________________
	 * This method displays a victory message if the players wins the game
	 */
	private void displayVictoriousEndgameMessage() {
		GLabel victory = new GLabel ("YOU FREAKING");
		victory.setFont("Courier-50");
		victory.setColor(Color.BLUE);
		GLabel victory2 = new GLabel ("WINNNN!!!!!!!!");
		victory2.setFont("Courier-50");
		victory2.setColor(Color.BLUE);
		add(victory, 0.5 * (getWidth() - victory.getWidth()), getHeight() / 2);		
		add(victory2, 0.5 * (getWidth() - victory2.getWidth()), getHeight() / 2 + victory.getAscent());
	}

	/*
	 * Method: Display Sad Endgame Message
	 * ____________________________________
	 * This method displays a sad message if the player loses the game
	 */
	private void displaySadEndgameMessage() {
		GLabel defeat = new GLabel ("You lost. LOL.");
		defeat.setFont("Courier-50");
		defeat.setColor(Color.RED);
		add(defeat, 0.5 * (getWidth() - defeat.getWidth()), getHeight() / 2);
	}

	/*
	 * Method: Reset
	 * __________________
	 * This method removes the ball and then resets it in the 
	 * center of the screen with a new, randomized x-velocity
	 * and the same y-velocity
	 */
	private void reset() {
		remove (ball);
		createBall();
	}

	/*
	 * Method: Get Colliding Object
	 * ---------------------------
	 * This method finds whether the square that the circle is
	 * inscribed in is superimposed over any other graphical object.
	 * If yes, the method stores the GObject in the variable
	 * "collider" and returns it to the run method. 
	 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		} else {
			return null;
		}
	}

	/*
	 * Method: Mouse Moved
	 * ____________________
	 * This method stores the x location of the mouse into a variable
	 * mouseX if it moves. If the x location is large enough that the 
	 * paddle wouldn't fit in the screen, it sets the value of mouseX
	 * to a value that will fit the paddle in the screen.
	 */
	public void mouseMoved (MouseEvent e) {
		mouseX = e.getX();
		if (e.getX() > getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
	}

	/*
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall() {
		return ball.getY() >= getHeight() - 2 * BALL_RADIUS;
	}

	/*
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/*
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/*
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - 2 * BALL_RADIUS;
	}

	/*
	 * Method: Create Ball
	 * _______________________
	 * This method creates a ball and adds it to the center of the
	 * screen. It starts the ball with y-velocity VELOCITY_Y and creates a
	 * random x-velocity for the ball between 1 and 3. It also pauses for 
	 * an amount of time equal to OH_BOY_GET_READY_DELAY_TIME to leave time for  
	 * the player to get ready.
	 */
	private void createBall() {
		ball = new GOval(2.0 * BALL_RADIUS, 2.0 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball, 0.5 * getWidth() - BALL_RADIUS, 0.5 * getHeight() - BALL_RADIUS);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		pause(OH_BOY_GET_READY_DELAY_TIME);
	}

	/*
	 * Method: Create Paddle
	 * _______________________
	 * This method creates a paddle and adds it to the center bottom of the screen
	 */
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2.0, getHeight() - PADDLE_Y_OFFSET);
	}

	/*
	 * Method: Create Bricks
	 * _______________________
	 * This method creates bricks equal to NBRICK_ROWS*NBRICK_COLUMNS
	 * and puts them in a number of centered rows equal to NBRICK_ROWS at the
	 * top of the screen in a number of columns equal to NBRICK_COLUMNS.
	 * The method also colors them in five equal groups,
	 * consecutively being red, orange, yellow, green, and cyan.
	 */
	private void createBricks() {
		for (int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			for (int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (rowNumber < NBRICK_ROWS/5) {
					brick.setColor(Color.RED);
				} else if (rowNumber < 2 * NBRICK_ROWS / 5) {
					brick.setColor(Color.ORANGE);
				} else if (rowNumber < 3 * NBRICK_ROWS / 5) {
					brick.setColor(Color.YELLOW);
				} else if (rowNumber < 4 * NBRICK_ROWS / 5) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
				add(brick, getWidth() / 2 - (NBRICK_COLUMNS) * (BRICK_WIDTH+BRICK_SEP) / 2 + (BRICK_WIDTH + BRICK_SEP) * columnNumber,
						BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * rowNumber);
			}
		}
	}
}