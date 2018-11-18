/*
 * File: Breakout.java
 * -------------------
 * Name: Caue Costa
 * Section Leader: Julia Daniel
 * 
 * This program runs the famous "Breakout" game.
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 700.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//create paddle as an instance variable
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	//create vx and vy as instance variables
	private double vx, vy;

	//create howManyBricksLeft as instance variables
	private double howManyBricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;

	//create RandomGenerator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		gameSetup();

		//loops gameplay for the total amount of turns.
		//prevents game ouver function in case there are 0 bricks
		//left.
		for(int turnCount = 1; turnCount <= NTURNS; turnCount++) {
			gamePlay();
			if(howManyBricksLeft != 0) {
				if(turnCount == NTURNS) {
					gameOver();
				}
			}
		}
	}

	/* Method: gameSetup
	 * Set up the elements (brickWall and Paddle) for the game
	 */
	private void gameSetup() {
		buildBrickWall();
		buildPaddle();
	}

	/* Method:buildPaddle
	 * Build the paddle at a static position,
	 * until the mouse moves
	 */
	private void buildPaddle() {
		paddle.setFilled(true);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET);
	}

	/* Moves the paddle depending on the mouse's x position.
	 * Limits the paddle position to the canvas borders.
	 * Y position is fixed.
	 */
	public void mouseMoved(MouseEvent e) {
		double xPaddle = e.getX() - PADDLE_WIDTH / 2;
		double yPaddle = getHeight() - PADDLE_Y_OFFSET;

		double xPaddleMin = 0;
		double xPaddleMax = getWidth() - PADDLE_WIDTH;

		//limits the paddle position to the canvas borders.
		if(xPaddle < xPaddleMin) {
			paddle.setLocation(xPaddleMin, yPaddle);
		} else if (xPaddle > xPaddleMax) {
			paddle.setLocation(xPaddleMax, yPaddle);
		} else {
			paddle.setLocation(xPaddle, yPaddle);
		}
	}

	/* Method: buildBrickWall
	 * Build the multicolored brick wall
	 */
	private void buildBrickWall() {
		//loop to build multiple rows
		for(int rowCount = 0; rowCount < NBRICK_ROWS; rowCount++) {
			double brickWidthSep = BRICK_WIDTH + BRICK_SEP;
			double brickHeightSep = BRICK_HEIGHT + BRICK_SEP;

			//loop to build a row
			for(int brickCount = 0; brickCount < NBRICK_COLUMNS; brickCount++) {
				double xBrick = (brickCount * brickWidthSep) + (getWidth() - NBRICK_COLUMNS * brickWidthSep) / 2;
				double yBrick = BRICK_Y_OFFSET + rowCount * brickHeightSep;

				//create bricks
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				//set the color of the bricks
				if(rowCount <= 1) {
					brick.setColor(Color.RED);
				} else if(rowCount <= 3) {
					brick.setColor(Color.ORANGE);
				} else if(rowCount <= 5) {
					brick.setColor(Color.YELLOW);
				} else if(rowCount <= 7) {
					brick.setColor(Color.GREEN);
				}else if(rowCount <= 9) {
					brick.setColor(Color.CYAN);
				}

				//add bricks
				add(brick, xBrick, yBrick);
			}
		}
	}

	/* Method: gamePlay
	 * Sets the dynamics of the gameplay, including
	 * the ball animation and the brick removal when
	 * the ball hits it.
	 */
	private void gamePlay() {
		//gamePlay setup
		vx = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		//animation loop
		waitForClick();
		GOval ball = makeBall();
		while(howManyBricksLeft != 0) {
			
			//check for collisions: if paddle, ball bounces
			//and moves 5 times (to avoid "sticking" to paddle);
			//if brick, removes the brick and the ball bounces.
			GObject collider = getCollidingObject(ball);
			if(collider == paddle) {
				vy = -vy;
				for(int n = 0; n < 5; n++) {
					ball.move(vx, vy);
					pause(DELAY);
				}
			} else {
				if(collider != null) {
					remove(collider);
					vy = -vy;
					howManyBricksLeft = howManyBricksLeft - 1;
				}
			}

			//update velocity in case the ball hits walls
			//(except bottom row)
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}

			//in case the ball hits the bottom wall,
			//the ball is removed and the while loop breaks
			if (hitBottomWall(ball)) {
				remove(ball);
				break;
			}

			//player wins
			if(howManyBricksLeft == 0) {
				playerWins();
				break;
			}

			//update visualization
			ball.move(vx, vy);
			pause(DELAY);
		}
	}

	/* Method: getCollidingObject
	 * Tests if there is no colliding objects in each of the
	 * ball's corners. If yes, return the colliding object, if
	 * not, return null
	 */
	private GObject getCollidingObject(GOval ball) {
		double xBall = ball.getX();
		double yBall = ball.getY();
		double ball_diameter = 2 * BALL_RADIUS;

		//checks the 4 "corners" of the ball for objects
		GObject upperLeft = getElementAt(xBall, yBall);
		GObject upperRight = getElementAt(xBall + ball_diameter, yBall);
		GObject bottomLeft = getElementAt(xBall, yBall  + ball_diameter);
		GObject bottomRight = getElementAt(xBall + ball_diameter, yBall  + ball_diameter);

		//return condition if any object is found
		if(upperLeft != null) {
			return upperLeft;
		} else if(upperRight != null) {
			return upperRight;
		} else if(bottomLeft != null) {
			return bottomLeft;
		} else if(bottomRight != null) {
			return bottomRight;
		} else {
			return null;
		}
	}

	/* Method: hitLeftWall
	 * Returns whether or not the ball should bounce
	 * the left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* Method: hitRightWall
	 * Returns whether or not the ball should bounce
	 * the right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/* Method: hitTopWall
	 * Returns whether or not the ball should bounce
	 * the top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/* Method: hitBottomWall
	 * Returns whether or not the ball should bounce
	 * the bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}

	/* Method: makeBall
	 * Insert a black ball at the middle of the canvas
	 */
	private GOval makeBall() {
		//ball's constants
		double ballDiameter = BALL_RADIUS * 2;
		double xBall = getWidth() / 2 - BALL_RADIUS;
		double yBall = getHeight() / 2 - BALL_RADIUS;

		//create ball
		GOval b = new GOval(ballDiameter, ballDiameter);
		b.setFilled(true);
		add(b, xBall, yBall);
		return b;
	}

	/* Method: Game Over!
	 * Display Game Over message if the player looses all NTURNS
	 */
	private void gameOver() {
		GLabel gameOver = new GLabel("Game Over!");
		double gameOverX = getWidth() / 2 - gameOver.getWidth();		
		gameOver.setFont("Courier-26");
		gameOver.setColor(Color.RED);
		add(gameOver, gameOverX, getHeight() / 2);
	}

	/* Method: Player wins!
	 * Display "You Win!" message
	 */
	private void playerWins() {
		GLabel youWin = new GLabel("You Win!");
		double youWinX = getWidth() / 2 - youWin.getWidth();		
		youWin.setFont("Courier-26");
		youWin.setColor(Color.GREEN);
		add(youWin, youWinX, getHeight() / 2);
	}

}