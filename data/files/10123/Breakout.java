/*
 * File: Breakout.java
 * -------------------
 * Name: Christian Nunez	
 * Section Leader: Robbie Jones
 * 
 * This is the vanilla version of Breakout. BreakoutExtension.java includes various
 * extra features.
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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 4.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// Instance variables
	GRect paddle;
	GOval ball;
	GObject collidingObject;
	GLabel gameEndLabel;
	private boolean lastColliderWasPaddle = false; // A solution for the sticky paddle problem
	private double vx;
	private double vy = VELOCITY_Y;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int counter = 0; //Counts amount of bricks deleted

	public void run() {
		// Set-up phase
		setTitle("Christian Nunez's CS106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		addMouseListeners();
		createAndCenterPaddle();
		createBall();
		int turnsLeft = NTURNS;
		while(turnsLeft > 0 && counter != NBRICK_ROWS * NBRICK_COLUMNS) {
			// Play phase
			centerBall();
			vx = getRandomVx(); // Meanwhile, the ball's vy is set to VELOCITY_Y upon instantiation
			lastColliderWasPaddle = false;
			// Animation loop
			while(bottomWallHit(ball) == false && counter != NBRICK_ROWS * NBRICK_COLUMNS) {
				// update canvas
				ball.move(vx, vy);
				// update velocity components
				if(leftWallHit(ball) || rightWallHit(ball)) {
					vx = -vx;
					lastColliderWasPaddle = false;
				} else if(topWallHit(ball)) {
					vy = -vy;
					lastColliderWasPaddle = false;
				}
				// check for collisions with bricks or paddle
				checkForCollision(getCollidingObject(ball));

				// delay period
				pause(DELAY);
			}
			turnsLeft--; 
		}
		displayGameResults();	
	}	
		


	// Below, in order: (1) Play phase methods, (2) Mouse event methods, (3) Set up phase methods
	
	// -------------- PLAY PHASE METHODS --------------
	
	/*
	 * Method: displayGameResults
	 * -----
	 * This method checks which game-ending condition occurred ((1) the bottom wall was hit after all
	 * lives had been lost, or (2) all of the bricks were deleted. Recognizing these two possible outcomes,
	 * the game displays a suitable label for the game just played. It also removes the ball and the paddle
	 * from the canvas for a more aesthetically pleasing end game screen.
	 */
	private void displayGameResults() {
		if(bottomWallHit(ball) == true) {
			gameEndLabel = new GLabel("You lose! You only deleted " + counter + ((counter != 1) ? " bricks!": " brick!"));			
		} else if (counter == NBRICK_ROWS * NBRICK_COLUMNS) {
			gameEndLabel = new GLabel("You win!");		
		}
		gameEndLabel.setFont(SCREEN_FONT);
		add(gameEndLabel, (getWidth() - gameEndLabel.getWidth())/2, getHeight()/2 + gameEndLabel.getAscent());
		// To clean up the canvas:
		remove(ball);
		remove(paddle);
	}
	
	/*
	 * Method: getCollidingObject
	 * -----
	 * The getCollidingObject method checks all four "corners" of the ball object to see if an object
	 * has been contacted. It returns that object if there was contact and returns null if there was no
	 * colliding object present.
	 */
	private GObject getCollidingObject(GOval ball) {
		// check (x,y) position
		double x = ball.getX();
		double y = ball.getY();
		double diameter = 2 * BALL_RADIUS;
		if(getElementAt(x,y) != null) {
			collidingObject = getElementAt(x,y);
		} else if (getElementAt(x + diameter, y) != null) {
			collidingObject = getElementAt(x + diameter, y);
		} else if (getElementAt(x, y + diameter) != null) {
			collidingObject = getElementAt(x, y + diameter);
		} else if (getElementAt(x + diameter, y + diameter) != null) {
			collidingObject = getElementAt(x + diameter, y + diameter);
		} else {
			collidingObject = null;
		}
		return collidingObject;
	}
	
	/*
	 * Method: checkForCollision
	 * -----
	 * Using getCollidingObject, this method determines what action is taken after a collision occurs.
	 * If the paddle is the collidingObject, the y-velocity of the ball is negated, and if the collider
	 * is not the paddle and not null, then it must be a brick and thus the brick is removed.
	 */
	private void checkForCollision(GObject collidingObject) {
		GObject collider = collidingObject;
		if(collider != null) {
			if(collider == paddle) {
				if(!lastColliderWasPaddle) vy = -vy; // This fixes the sticky paddle problem.
				lastColliderWasPaddle = true;
			} else { // If the collider isn't null and it is not the paddle, it MUST be a brick.
				vy = -vy;
				remove(collider);
				counter++;
				lastColliderWasPaddle = false;
			}
		}
	
	}
	/*
	 * Methods: rightWallHit, leftWallHit, topWallHit, bottomWallHit
	 * -----
	 * The following four methods are boolean-returning methods that indicate whether any of the
	 * four walls of the canvas have been contacted by the ball.
	 */
	private boolean rightWallHit(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	private boolean leftWallHit(GOval ball) {
		return ball.getX() <= 0;
	}
	
	private boolean topWallHit(GOval ball) {
		return ball.getY() <= 0;
	}
	
	private boolean bottomWallHit(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	
	/*
	 * Method: getRandomVx
	 * -----
	 * This method generates a random magnitude and a random sign for vx.
	 */
	private double getRandomVx() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean()) vx = -vx;
		return vx;
	}

	
	// -------------- MOUSE EVENT METHODS --------------
	 
	
	/*
	 * Method: mouseMoved
	 * -----
	 * This method gets the x-coordinate of the mouse and passes it to safeRelocatePaddle(), which, in
	 * addition to moving the paddle along with the x-coordinate of the mouse,
	 * constrains the movement of the paddle to the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		safeRelocatePaddle(mouseX);
	}
	
	/*
	 * Method: safeRelocatePaddle
	 * -----
	 * This method assures that no part of the paddle can be moved outside of the view of the screen.
	 * It sets the location of the paddle's x-coordinate to that of the mouse.
	 */
	private void safeRelocatePaddle(double mouseX) {
		if(mouseX > 0 && mouseX < (getWidth()-paddle.getWidth())) {
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	 // -------------- SET-UP PHASE METHODS --------------
	 
	
	/*
	 * Method: createAndCenterPaddle
	 * -----
	 * The createAndCenterPaddle method creates the paddle object and passes the address
	 * to the instance variable "paddle". The paddle is centered with respect to x and translated from
	 * the bottom of the screen by PADDLE_Y_OFFSET.
	 */
	private void createAndCenterPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double paddleXInitial = (getWidth() - PADDLE_WIDTH)/2;
		double paddleYInitial = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleXInitial, paddleYInitial);
	}
	
	/*
	 * Method: createBall
	 * -----
	 * The createBall method creates the ball object with the specified dimensions.
	 */
	private void createBall() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	/*
	 * Method: centerBall
	 * -----
	 * The centerBall method recenters the ball object in the center with respect to x 
	 * and one brick-separation away from the bottom row of bricks
	 */
	private void centerBall() {
		double xBall = (getWidth() - 2*BALL_RADIUS)/2;
		double yBall = BRICK_Y_OFFSET + NBRICK_ROWS * (BRICK_HEIGHT + BRICK_SEP);
		ball.setLocation(xBall, yBall);
	}
	
	/*
	 * Method: setUpBricks
	 * -----
	 * The setUpBricks method employs nested for-loops to build the grid of bricks. This is
	 * a set-up phase method.
	 */
	private void setUpBricks() {
		// The coordinates of the top left brick.
		double xInitial = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP)))/2;
		double yInitial = BRICK_Y_OFFSET;
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int col = 0; col < NBRICK_COLUMNS; col++) {
				double xBrick = xInitial + (col * (BRICK_WIDTH + BRICK_SEP));
				double yBrick = yInitial + (row * (BRICK_HEIGHT + BRICK_SEP));
				Color brickColor = calculateColor(row);
				makeBrick(xBrick, yBrick, brickColor);
			}
		}
		
	}
	
	/*
	 * Method: calculateColor
	 * -----
	 * The calculateColor method determines what color a row should be colored by taking the current
	 * row index from the outer for-loop of setUpBricks() as a parameter, reducing it to a number between 0 and 9,
	 * and then determining the correct color based on that number.
	 */
	private Color calculateColor(int row) {
		while(row > 9) {
			row -= 10;
		}
		if(row == 0 || row == 1) {
			return Color.RED;
		} else if(row == 2 || row == 3) {
			return Color.ORANGE;
		} else if(row == 4 || row == 5) {
			return Color.YELLOW;
		} else if(row == 6 || row == 7) {
			return Color.GREEN;
		} else if(row == 8 || row == 9) {
			return Color.CYAN;
		} else {
			return null;
		}
	}
	
	/*
	 * Method: makeBrick
	 * -----
	 * The makeBrick method creates a new brick object, positions it at the point specified by
	 * the passed-in parameters, and colors it based on the passed-in color parameter.
	 */
	private void makeBrick(double xBrick, double yBrick, Color color) {
		GRect brick = new GRect(xBrick, yBrick, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}

}
