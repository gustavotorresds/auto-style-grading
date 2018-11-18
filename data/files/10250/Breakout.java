/*
 * File: Breakout.java
 * -------------------
 * Name: Emma Glickman
 * Section Leader: Drew Bassilakis
 * 
 * This file creates and implements the game Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	//Diameter of the ball in pixels
	public static final double BALL_DIAM = 2 * BALL_RADIUS;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 6;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for paddle 
	GRect paddle = null;

	//Instance variable for ball
	GOval ball = null;

	//Instance variable for ball velocity
	private double vx, vy;

	//Instance variable for random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variable for number of bricks remaining
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;

	//Instance variable for number of lives remaining
	private int livesLeft = NTURNS;

	/** Creates 10 rows of bricks, with colors red, orange, yellow, green, and cyan each occupying
	 * two rows of bricks. Sets paddle at the bottom of the screen tracked by mouse movement and 
	 * ball that bounces off paddle towards bricks at proper angles of inflection. User attempts to
	 * steer paddle towards the ball, which bounces off the paddle and removes bricks in its way, until
	 * all bricks have been removed. Neither the ball nor the paddle ever leave the sides of the screen, 
	 * but if the ball falls below the bottom limit of the screen, the user loses that round. If the user
	 * does not win on any of the three lives, the game continues for three rounds until the user 
	 * loses for good, meaning they have no lives left.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setAllBricks();
		createPaddle();

		while(livesLeft > 0 && !checkIfWin()) {
			createBall();
			startTurn();
		}
	}

	/** Initializes ball movement and direction using random generator, then checks for collision with paddle,
	 * bricks, and wall sides & top 
	 */
	private void startTurn() {
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(true) {
			ball.move(vx, vy);
			pause(DELAY);
			if(ball.getY() > getHeight()) {
				livesLeft--;
				checkIfLost();
				break;
			}
			checkForCollisions();
			sideWallBounce();
			if (checkIfWin()) {
				remove(ball);
				showMessage("We have ourselves a winner!");
				break;
			}
		}
	}


	/** If player removes all bricks, display the "winning" message */
	private boolean checkIfWin() {
		if (brickCounter == 0) {
			return true;
		}
		return false;
	}

	/** If ball's y-coordinate goes below the height of the screen, display the "losing" message */
	private boolean checkIfLost() {
		if(livesLeft != 0) {
			showMessage("Oops! Try again!");
			return false;
		} else {
			showMessage("Tough luck! Game over.");
			return true;
		}
	}


	/** Creates centered winning or losing label in Serif size 18 font */
	private void showMessage(String msg) {
		GLabel endMsg = new GLabel (msg); 
		endMsg.setFont("Serif-18");
		double labelX = getWidth()/2 - endMsg.getWidth()/2;
		double labelY = getHeight()/2;
		add (endMsg, labelX, labelY); 
		waitForClick();
		remove(endMsg);
	}

	/** Ball bounces off side walls and ceiling, changing vx direction at each side wall collision and
	 * vy direction at each ceiling collision */
	private void sideWallBounce() {
		if (ball.getX() > (getWidth()-BALL_DIAM)) {
			vx = Math.abs(vx)*-1;
		}
		if (ball.getX() < 0) {
			vx = Math.abs(vx);
		}
		if (ball.getY() < 0) {
			vy = Math.abs(vy);
		}
	}

	/** Ball checks for collision with paddle or with bricks. If it collides with paddle, changes 
	 * direction from positive vy to negative vy. If it collides with a brick, it removes the brick
	 * and changes direction from negative y to positive y.
	 */
	private void checkForCollisions() {
		//collidedElementNW refers to the northwest corner of the ball hitting a brick
		GObject collidedElementNW = getElementAt(ball.getX(), ball.getY());
		//collidedElementNW refers to the northeast corner of the ball hitting a brick
		GObject collidedElementNE = getElementAt(ball.getX()+BALL_DIAM, ball.getY());
		//collidedElementSW refers to the southwest corner of the ball hitting an object
		GObject collidedElementSW = getElementAt(ball.getX(), ball.getY()+BALL_DIAM);	
		//collidedElementSW refers to the southwest corner of the ball hitting an object
		GObject collidedElementSE = getElementAt(ball.getX()+BALL_DIAM, ball.getY()+BALL_DIAM);

		//Ensures that the ball does not remove more than one brick at once
		if(collidedElementNW != null) {
			bounceOffObject(collidedElementNW);
		} else if (collidedElementNE != null) {
			bounceOffObject(collidedElementNE);
		} else if (collidedElementSW != null) {
			bounceOffObject(collidedElementSW);
		} else if (collidedElementSE != null) {
			bounceOffObject(collidedElementSE);
		}
	}

	/** Ball bounces off whichever object it encounters by changing y direction, and removing the brick
	 * if the collided element is not a paddle
	 * @param collidedElement
	 */
	private void bounceOffObject(GObject collidedElement) {
		if (collidedElement == paddle) {
			vy = Math.abs(vy)*-1;
		} else if (collidedElement != null) {
			vy = -vy;
			remove (collidedElement);
			brickCounter--;

		}
	}

	/** Creates a filled GOval called 'ball' in the center of the screen */
	private void createBall() {
		ball = new GOval (BALL_DIAM, BALL_DIAM);
		ball.setFilled(true);
		double ballX = (getWidth() - BALL_DIAM) / 2;
		double ballY = (getHeight() - BALL_DIAM) / 2;
		add (ball, ballX, ballY);

	}
	/** Creates paddle at the bottom center of the screen */ 
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
		addMouseListeners();
	}

	/** Enables paddle to move within screen confines according to mouse movement */ 
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX() - PADDLE_WIDTH / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(paddleX, paddleY);
		if (paddle.getX() > getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleY);
		}
		if (paddle.getX() < 0) {
			paddle.setLocation(0, paddleY);
		}
	}

	/** Lays 10 rows of bricks, each 10 bricks long, of which 2 are red, 2 are orange, 2 are 
	 * yellow, 2 are green, and 2 are blue. The stack of bricks is 70 pixels away from the top.
	 */
	private void setAllBricks() {
		for (int i = 0; i < NBRICK_ROWS ; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double startX = (getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH) - (NBRICK_COLUMNS-1)*BRICK_SEP ) / 2.0;
				double x = startX + (j*BRICK_WIDTH) + (BRICK_SEP*(j));
				double y = BRICK_Y_OFFSET + i*BRICK_HEIGHT + (BRICK_SEP*(i)); 
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				int remainder = i % 10;
				if (remainder == 0 | remainder == 1) {
					brick.setColor(Color.RED);
				} else if (remainder == 2 | remainder == 3) {
					brick.setColor(Color.ORANGE);
				} else if (remainder == 4 | remainder == 5) {
					brick.setColor(Color.YELLOW);
				} else if (remainder == 6 | remainder == 7) {
					brick.setColor(Color.GREEN);
				} else if (remainder == 8 | remainder == 9) {
					brick.setColor(Color.CYAN);
				} add(brick);
			}
		}
	}
}