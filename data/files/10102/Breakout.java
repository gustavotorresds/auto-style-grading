/*
 * File: Breakout.java
 * -------------------
 * Name: Cara Turnbull
 * Section Leader: James Mayclin
 * 
 * This implements the game of Breakout. At the beginning of each turn, a 
 * ball is launched from the center of the screen and the player must use 
 * the paddle to bounce the ball off the walls and hit bricks. If a brick 
 * is hit, it is destroyed and the ball will continue to bounce off the
 * walls, bricks, and/or paddle until either A) the turn is over, or B) all
 * of the bricks are destroyed. A turn is over when the player misses the 
 * ball with the paddle and the ball hits the lower wall. The player has 
 * three turns in which to attempt to win the game by destroying all of
 * the bricks.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Instance variable for paddle
	private GRect paddle;
	//Instance variable for ball
	private GOval ball;
	//Instance variables for velocity
	private double vx, vy;
	//Instance variable for random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//Instance variable for brick counter
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;

	/**
	 * RUN METHOD: Sets up game (assigns title and canvas dimensions,
	 * calls methods to create the game and enable the user to play.
	 * PRECONDITIONS: None.
	 * POSTCONDITIONS: User can play Breakout.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
	}

	/**
	 * Creates bricks and paddle, and allows user to control paddle.
	 * PRECONDITIONS: Canvas size has been defined.
	 * POSTCONDITIONS: Canvas with NBRICK_ROWS and NBRICK_COLUMNS of
	 * colored bricks, one paddle with a fixed vertical placement and
	 * horizontal movement controlled by the users mouse movements.
	 */
	private void setUpGame() {
		makeBricks();
		paddle = makePaddle();
		addMouseListeners();	
	}

	/**
	 * Creates bricks by calling makeRow method and specifying which row
	 * (from 1 up to NBRICK_ROWS) is being created.
	 * PRECONDITIONS: Canvas dimensions defined.
	 * POSTCONDITIONS: NBRICK_ROWS and NBRICK_COLUMNs of bricks colored
	 * in approximately a rainbow.
	 */
	private void makeBricks() {
		for(int rowNumber = 1; rowNumber <= NBRICK_ROWS; rowNumber ++) {
			makeRow(rowNumber);
		}	
	}

	/**
	 * Creates a single row of bricks according to the parameter rowNumber.
	 * The row is NBRICK_COLUMNS long and all of the bricks in the row are the
	 * same color as determined by getColor. The bricks are centered on the
	 * canvas and equally spaced horizontally (from each other) and vertically 
	 * (from other pre-existing and future rows).
	 * PRECONDITIONS: Value "rowNumber" from 1 to NBRICK_ROWS
	 * POSTCONDITIONS: One row of NBRICK_COLUMNS bricks in a particular color.
	 * @param rowNumber
	 */
	private void makeRow(int rowNumber) {
		double xLoc = getWidth() / 2 - (BRICK_WIDTH + BRICK_SEP) * (NBRICK_COLUMNS) / 2 + BRICK_SEP / 2;
		double yLoc = BRICK_Y_OFFSET + (rowNumber - 1) * (BRICK_HEIGHT + BRICK_SEP);
		for(int brickNumber = 1; brickNumber <= NBRICK_COLUMNS; brickNumber ++) {
			GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			add(brick, xLoc, yLoc);
			brick.setFilled(true);
			Color color = getColor(rowNumber);
			brick.setColor(color);
			xLoc = xLoc + BRICK_WIDTH + BRICK_SEP;
		}	
	}

	/**
	 * Returns the correct color for each row based on the parameter rowNumber.
	 * PRECONDITIONS: Int rowNumber from 1 to NBRICK_ROWS
	 * POSTCONDITIONS: Returns the color to be assigned to each brick in a particular row.
	 * The default value of NBRICK_ROWS (=10) returns two rows each of red, orange,
	 * yellow, green, and cyan (in that order). To complete the rainbow, it can 
	 * accommodate values up to NBRICK_ROWS = 14 (resulting in two rows of blue and
	 * two of magenta) and any additional rows will be colored black.
	 * @param rowNumber
	 * @return
	 */
	private Color getColor(int rowNumber) {
		if(rowNumber == 1 || rowNumber == 2) {
			return Color.RED;
		}
		if(rowNumber == 3 || rowNumber == 4) {
			return Color.ORANGE;
		}
		if(rowNumber == 5 || rowNumber == 6) {
			return Color.YELLOW;
		}
		if(rowNumber ==7 || rowNumber == 8) {
			return Color.GREEN;
		}
		if(rowNumber == 9 || rowNumber == 10) {
			return Color.CYAN;
		}
		if(rowNumber == 11 || rowNumber == 12) {
			return Color.BLUE;
		}
		if(rowNumber == 13 || rowNumber == 14) {
			return Color.MAGENTA;
		}
		return null;
	}

	/**
	 * Assigns instance variable 'paddle' to a filled GRect with the proper 
	 * paddle dimensions.
	 * PRECONDITIONS: none
	 * POSTCONDITIONS: returns GRect paddle with proper dimensions
	 * @return
	 */
	private GRect makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	/**
	 * Called when mouse is moved. Assigns x coordinate of mouse to the upper
	 * left corner of the paddle (but does not allow paddle to go off-screen).
	 * PRECONDITIONS: Existing GRect paddle with dimensions PADDLE_WIDTH and 
	 * PADDLE_HEIGHT (not yet added to canvas)
	 * POSTCONDITIONS: Paddle on canvas with fixed y coordinate that follows
	 * the x coordinates of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX >= getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		add(paddle, mouseX, getHeight() - PADDLE_Y_OFFSET);
	}

	/**
	 * Establishes which turn the user is on. For each turn, a ball is
	 * created and launched from the center of the screen. After the
	 * playTurn() protocol is complete (by either eliminating all of the
	 * bricks or allowing the ball to hit the bottom wall), playGame()
	 * checks to see whether to proceed to the following term or declare
	 * the game complete. If the game is not won after 3 turns, it is
	 * declared "game over".
	 * PRECONDITIONS: Correctly set canvas with bricks and user-
	 * controllable paddle.
	 * POSTCONDITIONS: Game is over; if all bricks are eliminated in three
	 * turns or less the game is won, otherwise the game has been lost.
	 */
	private void playGame() {
		for(int turnNumber = 1; turnNumber <= NTURNS; turnNumber ++) {
			makeBall();
			playTurn();
			GObject winGame = getElementAt(getWidth() / 2, getHeight() / 2); //checks i
			if(winGame != null) {
				break;
			}
		}
		GLabel gameOver = new GLabel("Game Over!");
		gameOver.setFont("Courier-70");
		gameOver.setColor(Color.PINK);
		add(gameOver, getWidth() / 2 - gameOver.getWidth() / 2, getHeight() / 2 + 100);			
	}

	/**
	 * Creates a ball and places it in the center of the screen.
	 * PRECONDITIONS: Canvas with bricks and paddle.
	 * POSTCONDITIONS: Same canvas, with filled GOval ball in the center of
	 * the canvas.
	 */
	private void makeBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);	
	}

	/**
	 * Generates gameplay for each "turn." The ball is launched and bounces off the
	 * paddle, bricks, and top/left/right walls until the game is won or the turn is over.
	 * PRECONDITIONS: Canvas with paddle and bricks, ball in the center of the canvas.
	 * POSTCONDITIONS: Determines quasi-random launch angle for ball. Once ball is set 
	 * in motion, it will "bounce" off the bricks, walls (top, right, and left), and 
	 * paddle. If it collides with a brick, in addition to bouncing the brick will be
	 * removed and the brickCounter will be changed to reflect the new number of
	 * bricks on the screen. The ball will continue to collide with the bricks,
	 * paddle, and 3 walls until either A) the bricksInPlay counter reflects
	 * that there are 0 bricks remaining (in which case the game is won), or
	 * B) the user fails to bounce the ball off of the paddle and it collides
	 * with the lower boundary on the screen (in which case the turn is over and
	 * the ball disappears).
	 */

	private void playTurn() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		while(ball.getY() + BALL_RADIUS * 2 < getHeight()) {
			ball.move(vx,vy);
			if(ball.getY() + BALL_RADIUS * 2 >= getHeight()) {
				remove(ball);
				break;
			}
			if(ball.getY() < 1) {
				vy = -vy;
			} else if(ball.getX() < 1 || ball.getX() + BALL_RADIUS * 2 >= getWidth()) {
				vx = -vx;
			}
			checkForCollisions();
			if(brickCounter == 0) {
				remove(ball);
				GLabel winner = new GLabel("You've won!");
				winner.setFont("Courier-50");
				winner.setColor(Color.PINK);
				add(winner, getWidth() / 2 - winner.getWidth() / 2, getHeight() / 2);
				break;
			}
			pause(DELAY);
		} remove(ball);
	}

	/**
	 * Determines that the ball has collided with an object and checks whether it is
	 * the paddle, another object (a brick), or a "dummy" object set to null by the
	 * getCollidingObject() method. It then performs the necessary actions (a bounce 
	 * off of the paddle, a bounce off a brick followed by the removal of said brick, 
	 * or nothing at all (if the collider is set to null).
	 * PRECONDITIONS: Canvas is set with bricks and paddle, ball is in motion.
	 * POSTCONDITIONS: Ball has changed direction based on its collision and, if
	 * it has collided with a brick, the brick is removed and the brickCounter is
	 * updated.
	 * @return
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject(ball.getX(), ball.getY());
		if(collider == paddle) {
			if(ball.getY() + BALL_RADIUS * 2 >= paddle.getY() && ball.getY() + BALL_RADIUS * 2 <= paddle.getY() + PADDLE_HEIGHT) {
				vy = -Math.abs(vy); //keeps ball from getting stuck "inside" the paddle and performing multiple collision-based course corrections
			}
		} else if(collider != paddle && collider != null) {
			vy=-vy;
			remove(collider);
			brickCounter = brickCounter - 1;
		}
	}

	/**
	 * Checks the perimeter of the ball at four points to determine if
	 * it has come into contact with an object.
	 * PRECONDITIONS: getCollidingObject is fed two values (the
	 * x and y coordinates of the ball's current location.
	 * POSTCONDITIONS: returns the object (if any) that coincides with one of
	 * the four points around the ball. If no object is found at any of the four
	 * points, the object in the return is set to null.
	 */
	private GObject getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x, y);
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(x + BALL_RADIUS * 2, y);
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(x, y + BALL_RADIUS * 2);
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
		if(collider != null) {
			return collider;
		}else {
			collider = null;
			return collider;
		}
	}
}
