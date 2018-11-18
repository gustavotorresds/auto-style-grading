/*
 * File: Breakout.java
 * -------------------
 * Name: Katherine Silk
 * Section Leader: Niki Agrawal
 * 
 * This program will let the user play Breakout, a game in which the ball bounces around 
 * the screen while knocking out bricks it touches. 
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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// X and Y positions of labels
	public static final int LABEL_X = 20;
	public static final int LABEL_Y = 10;

	// This will count the number of bricks in the brick block.
	private int numBricks = NBRICK_COLUMNS * NBRICK_ROWS;

	// loads audio
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// This creates an instance variable for the number of times the ball bounces.
	// The rest of the instance variables are fairly self-explanatory.
	private int numTurns = NTURNS;
	private GRect paddle = null;
	private GOval ball = null;
	private GObject collided = null;
	private double vy = 3.0;
	private RandomGenerator rgen = RandomGenerator.getInstance(); // This creates a random variable.

	private double vx = rgen.nextDouble(1.0, 3.0);
	{
		if (rgen.nextBoolean(.05))
			vx = -vx; // This calls the random generator to make the x value negative half the time.
	}

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners(); // Allows the program to detect mouse movement.
		createBricksAtTop();
		while (numTurns > 0) {
			paddle = makePaddle(); // instance variables take the value of the method (rather than null).
			ball = createBall();
			createIntroLabel();
			bounceBall();
			checkTurnsRemaining();

		}

	}

	public void mouseMoved(MouseEvent e) {// This allows the entire program to track where the mouse is.
		double mouseX = e.getX(); // defines x coordinate of mouse location
		if ((mouseX + PADDLE_WIDTH) < CANVAS_WIDTH) { // This if statement ensures that the paddle's location is only
			// set for bounds within the screen.
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
			// Because the "run" method stops running temporarily and gives its
			// functionality to the mouseMoved method, the paddle must be created inside the
			// mouse movement method in order for it to track the mouse's movement.

		}
	}

	// this allows the mouse to move faster after the user double-clicks.
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			vx = 1.5 * vx;
			vy = 1.5 * vy;
		}

	}

	// This method defines paddle as a GRect.
	private GRect makePaddle() {
		GRect paddle = new GRect(getWidth() / 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		return (paddle);
	}

	// This method creates a block of bricks at the top of the screen.
	private void createBricksAtTop() {
		for (int a = 0; a < NBRICK_COLUMNS; a++) {
			double initialX = ((CANVAS_WIDTH / 2) / -(BRICK_WIDTH * NBRICK_ROWS) - (BRICK_SEP - 1 * NBRICK_ROWS)
					- BRICK_SEP / 2); // The initialX variable allows the program to find the first value of x that
			// will cause the brick block to be centered.
			for (int i = 0; i < NBRICK_ROWS; i++) {
				double x1 = (initialX + BRICK_SEP * (i) + i * (BRICK_WIDTH));
				double y1 = (BRICK_Y_OFFSET + (a * BRICK_HEIGHT) + (a * BRICK_SEP));
				GRect brick = new GRect(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				if (a < 2) { // the if statements allow the rows to take on different colors without having
					// to rewrite a for loop for every separate color.
					brick.setColor(Color.RED);
				}
				if (a >= 2 && a <= 4) {
					brick.setColor(Color.ORANGE);
				}
				if (a >= 4 && a <= 6) {
					brick.setColor(Color.YELLOW);
				}
				if (a >= 6 && a <= 8) {
					brick.setColor(Color.GREEN);
				}
				if (a >= 8 && a <= 10) {
					brick.setColor(Color.CYAN);
				}

			}
		}
	}

	private GOval createBall() {
		ball = new GOval((getWidth() / 2 - BALL_RADIUS), (getHeight() / 2 - BALL_RADIUS), BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		add(ball);
		return (ball);
	}

	private void createIntroLabel() {
		GLabel label = new GLabel("Click to play the game.", BRICK_WIDTH / 2, getHeight() - 60);
		label.setFont("Courier-24");
		add(label);
		GLabel label2 = new GLabel("For an extra challenge, double-click to make the ball go faster. Good luck!",
				BRICK_WIDTH / 2, getHeight() - 40);
		add(label2);
		waitForClick();
		remove(label); // removes labels when the user begins playing the game
		remove(label2);

	}

	/*
	 * This method is responsible for bouncing the ball. It creates an animation
	 * loop, and checks to see whether the ball has collided with anything. It also
	 * checks to see whether the ball has hit one of the side walls. If so, it
	 * reverses the x direction. If the ball has hit a top or bottom wall, the ball
	 * reverses y direction.
	 */
	private void bounceBall() {
		while (!hitBottomWall()) {
			ball.move(vx, vy);
			pause(DELAY);
			collided = getCollidingObject();
			while (hitSideWall()) {
				vx = -vx;
				ball.move(vx, vy);
				pause(DELAY);
				collided = getCollidingObject();
			}
			while (hitTopWall()) {
				vy = -vy;
				ball.move(vx, vy);
				pause(DELAY);
				collided = getCollidingObject();
			}

			if (collided != null) { // checks that a collision has occurred.
				if (collided == paddle) { // if the ball collides with the paddle, reverse y direction (travel up).
					bounceClip.play();
					vy = -Math.abs(vy);
				} else { // if the ball collides with something (but doesn't collide with the paddle)
					// remove the object.
					remove(collided);
					vy = -vy;
					numBricks = numBricks - 1; // every time a brick is removed, the numBricks variable reduces by one.

				}
				if (numBricks == 0) { // prints a special message if the user won.
					numTurns = 3;
					GLabel label = new GLabel("Congratulations, you won!", LABEL_X, getHeight() / 2);
					label.setFont("Courier-20");
					label.setLocation((getWidth() - label.getWidth()) / 2, (getHeight() - label.getAscent()) / 2);
					add(label);
					break;


				}
			}
		}
	}

	private void checkTurnsRemaining() {
		if (hitBottomWall()) {
			numTurns = numTurns - 1;
			remove(ball);
			remove(paddle);
		}
		if (numTurns == 0) {
			remove(ball);
			remove(paddle);
			GLabel label = new GLabel("Sorry, you did not win. Play again!", LABEL_X, getHeight() / 2);
			label.setFont("Courier-20");
			add(label);
		}
	}
	
	/*
	 * This method tests whether the ends of the ball's x axis have bumped against
	 * the screen.
	 */
	private boolean hitSideWall() {
		if (ball.getX() <= 0 || ball.getX() + 2 * BALL_RADIUS > getWidth()) {
			return (true);
		} else {
			return (false);
		}
	}

	private boolean hitTopWall() {
		if (ball.getY() <= 0) {
			return (true);
		} else {
			return (false);
		}
	}

	// This method creates a boolean. If the ball touches the bottom wall, this will
	// return true.
	private boolean hitBottomWall() {
		if (ball.getY() > getHeight() - 2 * BALL_RADIUS) {
			return (true);
		} else {
			return (false);
		}
	}

	/*
	 * If this method doesn't return null at one of the parameters, it will return
	 * the value of "collided", telling the bounceBall() method which corner of the
	 * ball collided with an object.
	 */
	public GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			GObject collided = getElementAt(ball.getX(), ball.getY());
			return (collided);
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			GObject collided = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
			return (collided);
		}
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			GObject collided = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
			return (collided);
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			GObject collided = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
			return (collided);

		} else {
			return (null);
		}
	}

}
