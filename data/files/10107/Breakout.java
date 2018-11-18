/*
 * File: Breakout.java
 * -------------------
 * Name: Sela 
 * Section Leader: Luciano
 * 
 * This file consists of 4 methods: setUpRows(), setUpPaddle(), createBall(), and playGame(). The 
 * first three methods set up the game with the necessary objects: the bricks, the paddle, and the
 * ball. The fourth method playGame implements two loops in order to enable the user to play the game. The first loop
 * controls how many turns the player gets. The second while loop is related to the animation that
 * enables the ball to move, bounce, and remove bricks. Within playGame, further methods are defined
 * in order to handle the outcomes when objects collide. This is described in further detail where
 * the methods are defined later in the code. The ball begins moving in a random direction using a random
 * generator for the direction and speed. I added waitForClick so that the user can choose when to 
 * start the game (when to get the ball moving and begin playing). Finally, if statements within playGame 
 * check whether the player has won, lost, or has enough turns left to continue playing. Many instance variables are 
 * defined so that variables can be accessed in different blocks of code.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

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

	// Colors for bricks.
	public static final Color[] BRICK_COLORS = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};

	// use instance variables for brick, ball, paddle, random number generator 
	private GRect brick;
	private GOval ball;
	private GRect paddle;
	private double vx;
	private double vy;
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;
	// Add random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// allows program to respond to mouse
		addMouseListeners();
		setUpRows();
		setUpPaddle();
		createBall();
		playGame();
	}
	/* setUpRows sets up the bricks using a for loop within a for loop based on columns then on rows. The bricks are 
	 * centered and spaced evenly in the x and y directions using a mathematical function that can be applied to any
	 * number of bricks. Every two rows is filled with the same color in the rainbow pattern.
	 */
	private void setUpRows() {
		// loop build columns of bricks
		for (int r = 0; r < NBRICK_ROWS; r++) {
			// loop to build rows of bricks
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double connectedBrickWidth = NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP;
				double x = (getWidth() - connectedBrickWidth)/2 + c * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + r * (BRICK_HEIGHT + BRICK_SEP);
				// color gets defined as an integer
				int color = r / 2 - 5 * (r / 10);
				brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				// grab color from an array
				brick.setColor(BRICK_COLORS[color]);
				add(brick, x, y);
			}
		}
	}

	private void setUpPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2,
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	// Make paddle respond to mouse moving. Paddle only responds to mouse movements in the X direction.
	public void mouseMoved(MouseEvent e) {
		if (paddle != null) {
			double x = e.getX();
			double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
			double rightBound = getWidth() - PADDLE_WIDTH;
			double leftBound = 0;
			paddle.setLocation(x, y);
			// must restrict direction of the paddle.
			if (paddle.getX() >= getWidth() - PADDLE_WIDTH) {
				paddle.setLocation(rightBound, y);
			} 
			if (paddle.getX() <= 0) {
				paddle.setLocation(leftBound, y);
			} 
		}
	}
	// Create ball for game and set it in the middle of the screen.
	private void createBall() {
		ball = new GOval (BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 + BALL_RADIUS;
		ball.setLocation(x, y);
		add(ball);
	}

	// Hit Bottom Wall returns whether or not the given ball should bounce off the bottom wall of the window.
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	// Hit Top Wall method returns whether or not the ball should bounce off the top wall.
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	// Right Wall method
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	// Left Wall method
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* Define method playGame
	 * is a method that enables both an animation for the ball to move and a loop to dictate the 
	 * number of turns a player gets to play the game.
	 */
	private void playGame() {
		int count = 0;
		/* The user only enters this loop is there are bricks left on the screen
		 * and the user has a turn(s) left to continue playing.
		 */
		while(bricksLeft != 0 && count < NTURNS) {
			ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 + BALL_RADIUS);
			/* waitForClick() enables the use to choose when to start the game.
			 * Once the user clicks the mouse, the ball will begin moving in a random
			 * direction due to the use of the random number generator.
			 * After that, the ball follows the outcomes related to the if 
			 * statements in the animation loop.
			 */
			waitForClick();
			vx = rgen.nextDouble(1.0, 3.0);
			vy = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			while(true) {
				// update visualization
				ball.move(vx, vy);
				GObject collider = getCollidingObject();
				/* collider is returned as null or as an object that must be a paddle
				 * or a brick. The following if statement checks if collider is null
				 * and if not, it must be a paddle or a brick, so the a method is called
				 * to check if the GObject is a paddle or a brick.
				 */
				if (collider != null) {
					checkIfBrickOrPaddle(collider);
				}
				// update velocity direction based on wall
				if (hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if (hitTopWall(ball)) {
					vy = -vy;
				}
				if (hitBottomWall(ball)) {
					/* count allows the program to keep track of how many turns the user
					 * has used and how many are left.
					 */
					count ++;
					break;
				}
				/* If there are no bricks left and you have 3 or fewer turns, you won!
				 * A label will appear on the screen to congratulate you!
				 */
				if (bricksLeft == 0 && count < NTURNS) {
					GLabel label = new GLabel("Congrats, you won!");
					label.setLocation(getWidth()/2, getHeight()/2);
					add(label);
				}
				pause(DELAY);
			}
		}
		
		/* If there are bricks left and you have used all your turns, you will lose
		 * A label will appear on the screen to let you know you lost.
		 */
		if (bricksLeft != 0 && count == NTURNS) {
			GLabel label = new GLabel("Sorry, you lost!");
			label.setLocation(getWidth()/2, getHeight()/2);
			add(label);
		}
	}
	/* Define method check for collisions to see if any corner of the ball collides with an object that is a brick or a paddle.
	 * If it is a brick or paddle, it bounces off, and if it is a brick, it also removes the brick.
	 */

	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		return null;
	}
	/* Define method checkIfBrickOrPaddle:
	 * This body of code is entered if collider is not null. Thus the object must
	 * be a paddle or a brick. 
	 */
	private void checkIfBrickOrPaddle(GObject collider) {
		if (collider == paddle) {
			/* Fix sticky paddle by making sure that the ball only bounces
			 * back up in the negative y direction (towards the bricks) after
			 * hitting the paddle.
			 */
			vy = -Math.abs(vy);
			/* This else statement is for the case in which the collided object is
			 * a brick. Thus, a brick must be removed from the tally count in order
			 * to determine how close the player is to winning the game.
			 */
		} else {
			vy = -vy;
			remove(collider);
			collider = null;
			bricksLeft = bricksLeft - 1;
		}
	}
}
