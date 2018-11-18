/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This graphics program will play the game Breakout. With a paddle following the mouse on the bottom of the window,
 * the user bounces a ball off of the paddle and walls to hit and remove rows of bricks at the top of the screen. If the ball 
 * falls out of the screen, the round is lost. If three rounds are lost, the game is lost. The game is won by removing all bricks.
 * */

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

	// The GObject that will serve as the paddle
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// The GObject that will serve as the ball
	private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);

	// Needed to later generate random numbers
	private RandomGenerator random = RandomGenerator.getInstance();

	// Ball velocity in y direction
	double yVelocity;

	// Ball velocity in the x direction
	double xVelocity;

	// y coordinate of top of ball
	double ballUpper;

	// y coordinate of bottom of ball
	double ballLower;

	// x coordinate of left of ball
	double ballLeft;

	// y coordinate of right of ball
	double ballRight;

	// x coordinate of ball's starting location
	double ballX;

	// y coordinate of bricks starting location
	double ballY;
	
	// x coordinate of left side of paddle
	double paddleX;
	
	// y coordinate of top of paddle
	double paddleY;

	// number of bricks remaining that have not been removed by hits
	int bricksRemaining = (NBRICK_COLUMNS * NBRICK_ROWS);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Adds all necessary objects to the screen (i.e. bricks, paddle, ball)
		setUpScreen();

		/*
		 *  Begins game play with user click, each round ends when the ball falls below the graphics 
		 * window and starts again by moving the ball back to its starting position and waiting for the 
		 * user to click to bring the round. Only three rounds can be played, and a round will only begin
		 * if bricks still remain.
		 */
		for (int round = 1; round < 4; round++) {
			playRound();
		}
	}



	private void setUpScreen() {
		/* 
		 * This method adds the required elements to the screen, including the bricks, paddle, and ball. The 
		 * method also adds MouseListeners which allows the paddle to be moved by the user's mouse.
		 */
		addBricks();
		addPaddle();
		addMouseListeners();
		addBall();
	}

	private void addBall() {
		/*
		 * This method adds the ball to the screen as a black, filled GOVal with x and y locations that center
		 * the object in the graphics window.
		 */
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		ballX = (getWidth() / 2) - BALL_RADIUS;
		ballY = (getHeight() / 2) - BALL_RADIUS;
		add(ball);
	}

	private void playRound() {
		/*
		 * This method calls the actual playing of the game. Upon the initial click by the user to begin the game,
		 * the ball begins moving downwards in the graphics window with a random velocity in the x direction within
		 * given bounds, and a given constant y velocity. The ball moves until it hits a side, at which point its x
		 * velocity is made negative to create the illusion of bouncing, or until it hits the top wall, at which point
		 * its y velocity will be made opposite. The same velocity changes happen when the ball hits the brick or paddle,
		 * but these instances are dealt with in a separate called method entitled bounceOffBrickAndPaddle. Finally, if 
		 * the ball falls below the height of the graphics window, the return will exit the while loop and the next 
		 * round will be called by the run method. 
		 */
		if (bricksRemaining != 0) {
			ball.setLocation(ballX, ballY);
			waitForClick();
			xVelocity = random.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			yVelocity = VELOCITY_Y;
			while(true) {
				ball.move(xVelocity, yVelocity);
				pause(DELAY);
				// specifies the events that will happen when the ball hits the paddle or a brick
				bounceOffBrickAndPaddle();
				if (hitsLeftWall(ball) || (hitsRightWall(ball))) {
					xVelocity = - xVelocity;
				}
				if (hitsTopWall(ball)) {
					yVelocity = - yVelocity;
				}
				if (ballUpper >= getHeight()) {
					return;
				}
			}
		}
	}

	private void bounceOffBrickAndPaddle() {
		/*
		 * This method specifies what will happen if the ball hits a brick or the paddle. The tests to check
		 * if the ball has hit use the coordinates of the corners of the square that could be drawn around the ball.
		 * The method specifies that if any of these edges collide with the paddle, the y velocity will be made negative
		 * while if any of these edges collide with a brick (specified as any object that is not the paddle or null), the method 
		 * removeBrick will be called.
		 */
		ballUpper = ball.getY();
		ballLower = ball.getY() + (2 * BALL_RADIUS);
		ballLeft = ball.getX();
		ballRight = ball.getX() + (2 * BALL_RADIUS);

		if (getElementAt(ballLeft, ballLower) == paddle || getElementAt(ballRight, ballLower) == paddle) {
			yVelocity = - yVelocity;
		}
		else if(getElementAt(ballLeft, ballLower) != null) {
			removeBrick(getElementAt(ballLeft, ballLower));
		}
		// specifies that if the top of the ball has hit the paddle that it will not bounce or remove the element as it has already fallen through
		else if(getElementAt(ballLeft, ballUpper) != null && getElementAt(ballLeft, ballUpper) != paddle) {
			removeBrick(getElementAt(ballLeft, ballUpper));
		}
		else if(getElementAt(ballRight, ballLower) != null) {
			removeBrick(getElementAt(ballRight, ballLower));
		}
		// specifies that if the top of the ball has hit the paddle that it will not bounce or remove the element as it has already fallen through
		else if(getElementAt(ballRight, ballUpper) != null && getElementAt(ballRight, ballUpper) != paddle) {
			removeBrick(getElementAt(ballRight, ballUpper));
		}
	}

	private void removeBrick(GObject brick) {
		/*
		 * The removeBrick method specifies what happens when the ball hits a brick. The brick is removed, and the 
		 * bricksRemaining count is decreased by one, and the velocity is made negative to create the bouncing effect.
		 */
		remove(brick);
		bricksRemaining = bricksRemaining - 1;
		yVelocity = - yVelocity;
	}

	private boolean hitsTopWall(GOval ball) {
		// This checks if the ball has hit the top wall by checking if its top y coordinate is less than 0.
		return ball.getY() <= 0;
	}

	private boolean hitsRightWall(GOval ball) {
		/*
		 *  This checks if the ball has hit the right wall by checking if its right x coordinate is greater than
		 *  the width of the graphics window.
		 */
		return ball.getX() >= getWidth() - (2 * BALL_RADIUS);
	}

	private boolean hitsLeftWall(GOval ball) {
		/*
		 *  This checks if the ball has hit the right wall by checking if its left x coordinate is less than
		 *  zero.
		 */
		return ball.getX() <= 0; 
	}

	private void addPaddle() {
		/*
		 * This method creates the paddle needed and adds it to the screen. Setting it as filled and black,
		 * the method centers it in the x-plane of the window and puts it at the y coordinate height specified
		 * by the assignment for its initial location. The paddle is then added to the screen.
		 */
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
	}

	public void mouseMoved(MouseEvent e) {
		/*
		 * This method allows the user's mouse to move the paddle from side to side in the x direction. 
		 * The height of the paddle remains the same as specified with the creation of the paddle.
		 */
		paddleX = (e.getX() - (PADDLE_WIDTH) / 2);
		paddle.setLocation(paddleX, paddleY);
	}

	private void addBricks() {
		/*
		 * This method adds the rainbow colored bricks to the graphics window. The number of rows and columns is 
		 * given as a static int above and used here. The method accounts for the changing of those numbers in the 
		 * pure creation of bricks, but colors the bricks in a rainbow that is created based on the specific number
		 * of rows present. The size of the bricks and the separation between them is also decided by the given constants.
		 */
		for (int row = 0; row < NBRICK_ROWS; row ++) {
			for (int column = NBRICK_COLUMNS; column > 0; column --) {
				double horizontalLocation = ((getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP))) / 2) + ((column - 1) * (BRICK_WIDTH + BRICK_SEP));
				double verticalLocation = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * (row + 1));
				GRect brick = new GRect (horizontalLocation, verticalLocation, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (row == 0 || row == 1) {
					brick.setColor(Color.RED);
				}
				if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
}
