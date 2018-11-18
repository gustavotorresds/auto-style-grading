/*
 * File: Breakout.java
 * -------------------
 * Name: Chelsey Pan
 * Section Leader: Julia Daniel
 * 
 * This file implements the game Breakout. Requires the user to first do a mouse click to start,
 * after which they can play the game until either the user misses the ball with the paddle and 
 * the ball heads to the bottom wall of the screen (lose condition), or no more bricks remain
 * (win condition).
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Dimensions of the canvas, in pixels */
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	/** Number of bricks in each row */
	public static final int NBRICK_COLUMNS = 10;

	/** Number of rows of bricks */
	public static final int NBRICK_ROWS = 10;

	/** Separation between neighboring bricks, in pixels */
	public static final double BRICK_SEP = 4;

	/** Width of each brick, in pixels */
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	/** Height of each brick, in pixels */
	public static final double BRICK_HEIGHT = 8;

	/** Offset of the top brick row from the top, in pixels */
	public static final double BRICK_Y_OFFSET = 70;

	/** Dimensions of the paddle */
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */ 
	public static final double PADDLE_Y_OFFSET = 30;

	/** Radius of the ball in pixels */
	public static final double BALL_RADIUS = 10;

	/** The ball's vertical velocity. */
	public static final double VELOCITY_Y = 3.0;

	/** The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that is chosen randomly */
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	/** Animation delay or pause time between ball moves (ms) **/
	public static final double DELAY = 1000.0 / 60.0;

	/** Number of turns */
	public static final int NTURNS = 3;

	/** Sets the color for each pair of rows */
	public static final int CYAN_BRICKS_ROWS = 8;

	public static final int GREEN_BRICKS_ROWS = 6;

	public static final int YELLOW_BRICKS_ROWS = 4;

	public static final int ORANGE_BRICKS_ROWS = 2;

	/**Creates instance variable for paddle */
	private GRect paddle = null;

	/** Creates instance variable for instructions label */
	private GLabel startInstructions = new GLabel("");

	/** Creates the variables for the ball's x and y velocity */
	private double vx, vy;

	/** Sets up random number generator used to determine the value of the x velocity */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		playGame();

	}

	/* Moves the paddle based on the mouse's location.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseXLocation = e.getX();
		int mouseYLocation = e.getY();

		if (mouseXLocation <= getWidth() - PADDLE_WIDTH)
			paddle.setLocation(mouseXLocation, getHeight() - PADDLE_Y_OFFSET);
	}

	/* Removes the instructions label upon mouse click, which also starts the game.
	 */
	public void mouseClicked(MouseEvent e) {
		remove(startInstructions);
	}

	/* Sets up the layout of the game. Creates the bricks and the paddle in preparation
	 * for playing.
	 */
	private void setUpGame() {
		makeBricks();
		makePaddle();
		addMouseListeners();

	}

	/* Implements game play. Creates the ball, and the sets all the conditions for bouncing. 
	 * This part of the code also removes a brick each time the ball makes contact with it,
	 * and sets up the terms for losing the game, as well as winning.
	 * 
	 */
	private void playGame() {
		//Loads audio for ball bouncing.
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		//Finds the center values of the screen.
		double screenXCenter = getWidth() / 2;
		double screenYCenter = getHeight() / 2;

		int brickCounter = 100; //Sets the count for the initial number of bricks
		boolean gameNotOver = true; //Continues the game as long as none of the terminating conditions have been met.

		//Makes the ball for game play
		GOval ball = makeBall();

		//Sets the x and y velocities of the ball. The x velocity is randomized
		// in terms of speed and direction for each game.
		vx = rgen.nextDouble(1.0, 3.0);
		vy = 3.0;
		if (rgen.nextBoolean(0.5)) vx = -vx;

		//Creates the label asking the user to click the mouse in order to start the game.
		displayStartInstructions();

		//Waits for a mouse click before starting the game.
		waitForClick();

		//Runs the game.
		while(gameNotOver) {
			//Checks to see whether the ball has collided with any object.
			GObject collider = getCollidingObject(ball);

			//If the ball collided with the paddle, it bounces back upwards.
			//If it hits a brick, it removes the brick, moves back downward,
			//and also decreases the brick counter by 1.
			if (collider == paddle) {
				vy = -vy;
				bounceClip.play(); //Plays the bounce audio whenever it hits the paddle.
			} else if (collider != null) {
				remove(collider);
				vy = -vy;
				brickCounter = brickCounter - 1;
				bounceClip.play(); //Plays the bounce audio whenever it hits a brick.
			}

			//Gets the ball to bounce off walls based on whether the ball hit
			//the left, right, or top wall.
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}

			if (hitTopWall(ball)) {
				vy = -vy;
			}

			//Sets the lose condition. If the ball moves past the paddle and "hits"
			//the bottom wall, the game informs player that they lost and terminates the game.
			if (hitBottomWall(ball)) {
				GLabel loseMessage = new GLabel("You lose, sucker.");
				loseMessage.setFont("Courier-24");
				loseMessage.setColor(Color.RED);
				add(loseMessage, screenXCenter, screenYCenter);
				gameNotOver = false;

			}

			//Sets the win condition. If there are no remaining bricks left, as kept
			//track of by the brickCounter, the game informs the player that they won,
			//and terminates the game.
			if (brickCounter == 0) {
				GLabel winMessage = new GLabel("Nailed it, winner!");
				winMessage.setFont("Courier-24");
				winMessage.setColor(Color.GREEN);
				add(winMessage, screenXCenter, screenYCenter);
				gameNotOver = false;
			}

			//Moves the ball according to the set x and y velocities.
			ball.move(vx, vy);
			pause(DELAY);
		}

	}

	/* Makes each row of bricks, centers them and fills 
	 * them with a certain color based on row number.
	 */
	private void makeBricks() {
		for (int brickRowNumber = 0; brickRowNumber < NBRICK_ROWS; brickRowNumber++) {
			for (int brickInRow = 0; brickInRow < NBRICK_COLUMNS; brickInRow++) {
				double brickYLocation = BRICK_Y_OFFSET + (BRICK_HEIGHT * brickRowNumber) + (BRICK_SEP * brickRowNumber);
				double brickXLocation = BRICK_WIDTH * brickInRow + (BRICK_SEP * brickInRow) + BRICK_WIDTH/6;

				GRect brick = new GRect(brickXLocation, brickYLocation, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				//Sets color of bricks based on row number.
				if (brickRowNumber >= CYAN_BRICKS_ROWS) {
					brick.setColor(Color.CYAN);
				} else if (brickRowNumber >= GREEN_BRICKS_ROWS) {
					brick.setColor(Color.GREEN);
				} else if (brickRowNumber >= YELLOW_BRICKS_ROWS) {
					brick.setColor(Color.YELLOW);
				} else if (brickRowNumber >= ORANGE_BRICKS_ROWS) {
					brick.setColor(Color.ORANGE);
				} else {
					brick.setColor(Color.RED);
				}
				add(brick);

			}
		}
	}

	/* Makes the paddle that will be used in the game.
	 */
	private void makePaddle() {
		double paddleXLocation = (getWidth() - PADDLE_WIDTH)/2;
		double paddleYLocation = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(paddleXLocation, paddleYLocation, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/* Makes the ball that will be used in the game.
	 */
	public GOval makeBall() {
		double ballXLocation = (getWidth() / 2) - BALL_RADIUS;
		double ballYLocation = (getHeight() / 2) - BALL_RADIUS;
		double ballSize = BALL_RADIUS * 2;
		GOval ball = new GOval(ballXLocation, ballYLocation, ballSize, ballSize);
		ball.setFilled(true);
		add(ball);
		return(ball);
	}

	/* Creates the label that writes the instructions on how to start the game.
	 */
	private void displayStartInstructions() {
		double startInstructionsYValue = getHeight() / 3;
		startInstructions.setLabel("Please click your mouse in order to start the game.");
		startInstructions.setColor(Color.DARK_GRAY);
		startInstructions.setFont("Courier-18");
		add(startInstructions, 0, startInstructionsYValue);
	}

	/* Checks to see if the ball has hit the left wall
	 * and returns true if so.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	/* Checks to see if the ball has hit the right wall and returns
	 * true if so.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*Checks to see if the ball has hit the top wall
	 * and returns true if so.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;

	}

	/*Checks to see if the ball has hit the bottom wall
	 * and returns true if so.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}

	/* Checks to see if the ball has collided with an object
	 * at any of the corners of the ball's bounding box. If so,
	 * returns the object that the ball collided with.
	 */
	private GObject getCollidingObject(GOval ball) {
		double ballXValue = ball.getX();
		double ballYValue = ball.getY();
		GObject objectAtTopLeftCorner = getElementAt(ballXValue, ballYValue);
		if (objectAtTopLeftCorner != null) {
			return objectAtTopLeftCorner;
		} else {
			GObject objectAtTopRightCorner = getElementAt((ballXValue + 2 * BALL_RADIUS), ballYValue);
			if (objectAtTopRightCorner != null) {
				return objectAtTopRightCorner;
			}
			else {
				GObject objectAtBottomLeftCorner = getElementAt(ballXValue, (ballYValue + 2 * BALL_RADIUS));
				if (objectAtBottomLeftCorner != null) {
					return objectAtBottomLeftCorner;
				} else {
					GObject objectAtBottomRightCorner = getElementAt((ballXValue + 2 * BALL_RADIUS), (ballYValue + 2 * BALL_RADIUS));
					if (objectAtBottomRightCorner != null) {
						return objectAtBottomRightCorner;
					} else {
						return null;
					}
				}
			}
		}
	}

}
