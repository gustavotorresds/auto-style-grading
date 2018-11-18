/*
 * File: Breakout.java
 * -------------------
 * Name:Carson Conley
 * Section Leader:Vineet Kosaraju
 * 
 * This file will make the game Breakout. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {

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

	// Keeps track of which row of bricks it is.
	private int row = 0;

	// Creates an instance variable for the GRect of the paddle.
	private GRect paddle = null;

	// Instance variable for the x-velocity of the ball.
	private double vx = 0;

	// Instance variable for the y-velocity of the ball.
	private double vy = VELOCITY_Y;

	// Creates the random generator to vary the x-direction of the ball.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Creates the ball as an instance variable.
	private GOval ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);

	// Creates an instance variable that keeps track of the bricks removed by the ball.
	private int bricksLost = 0;

	public void run() {
		addMouseListeners();
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createStartScreen();
		createBricks();
		createPaddle();
		playGame();

	}

	/*
	 * Creates a start screen for the game.
	 * Precondition: Canvas is blank.
	 * Postcondition: Canvas is blank.
	 */
	private void createStartScreen() {
		GLabel startScreen = new GLabel("CLICK TO OPEN GAME");
		startScreen.setFont("Courier-24");
		startScreen.setColor(Color.RED);
		add(startScreen, getWidth()/2 - startScreen.getWidth()/2, getHeight()/2);
		createBricks();
		GLabel breakout = new GLabel("BREAKOUT");
		breakout.setFont("Courier-24");
		add(breakout, getWidth()/2 - breakout.getWidth()/2, BRICK_Y_OFFSET + (BRICK_HEIGHT+ BRICK_SEP)*(NBRICK_ROWS/2));
		waitForClick();
		removeAll();

	}

	/* 
	 * This method creates the ball and controls its movements. 
	 * It also controls all elements of gameplay including winning
	 * and losing conditions.
	 * Precondition: The canvas contains only the paddle and bricks.
	 * Postcondition: The game is beaten or three losses have occurred.
	 */
	private void playGame() {
		for (int i = 0; i<3; i++) {
			if (bricksLost == NBRICK_ROWS* NBRICK_COLUMNS) {
				break;
			}
			ball.setFilled(true);
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			GLabel start = new GLabel("CLICK TO START");
			start.setFont("Courier-24");
			start.setColor(Color.BLUE);
			add(start, getWidth()/2 - start.getWidth()/2, getHeight()/2);
			waitForClick();
			remove(start);
			add(ball, getWidth()/2, getHeight()/2);
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			while (true) {
				if (bricksLost == NBRICK_ROWS* NBRICK_COLUMNS) {
					removeAll();
					GLabel winner = new GLabel("WINNER!");
					winner.setFont("Courier-48");
					winner.setColor(Color.RED);
					add(winner, (getWidth()/2 - winner.getWidth()/2), getHeight()/2);
					waitForClick();
					vx = 0;
					break;
				}	
				ball.move(vx,vy);
				pause(DELAY);
				if (ball.getY() <= 0) {
					vy = -vy;
					bounceClip.play();
				}
				if (ball.getX() > getWidth() - 2*BALL_RADIUS || ball.getX() <= 0) {
					vx = -vx;
					bounceClip.play();
				}
				if (ball.getY() >= getHeight() - 2*BALL_RADIUS) {
					remove(ball);
					break;
				}
				GObject collider = getCollidingObject();
				if (collider != null) {
					vy = -vy;
					bounceClip.play();
				} 
				if (collider != null && collider != paddle) {
					remove(collider);
					bricksLost ++;
				}

				/* The next two if statements correct for the common bug "sticky paddle."
				 * These check if the top of the ball is hitting the paddle and, if so, force the ball to continue
				 * moving up instead of getting "stuck" to the paddle.
				 */
				if (getElementAt(ball.getX(), ball.getY()) == paddle || getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) == paddle) {
					vy = -1*Math.abs(vy);
				}
				if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) == paddle || getElementAt(ball.getX()+ 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) == paddle) {
					vy = -1*Math.abs(vy);
				}
			}
		}	
		if (vx != 0) {
			remove(paddle);
			GLabel loser = new GLabel("LOSER!");
			loser.setFont("Courier-48");
			add(loser, getWidth()/2 -loser.getWidth()/2, getHeight()/2);
			waitForClick();
		}
	}
	/*
	 * Checks to see what the ball is hitting, if it even is hitting anything.
	 * The method then returns what the object was or that there was no object hit. 
	 */
	private GObject getCollidingObject() {
		GObject leftTopCorner = getElementAt(ball.getX(), ball.getY());
		if (leftTopCorner != null) {
			return leftTopCorner;
		}	
		GObject rightTopCorner = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if (rightTopCorner != null) {
			return rightTopCorner;
		}
		GObject leftBottomCorner = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if (leftBottomCorner != null) {
			return leftBottomCorner;
		}
		GObject rightBottomCorner = getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (rightBottomCorner != null) {
			return rightBottomCorner;
		}	
		return null;
	}
	/*
	 * Creates the paddle.
	 * Precondition: The canvas contains the bricks.
	 * Postcondition: The canvas contains the bricks and a paddle.
	 */
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle,getWidth()/2, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}
	/*
	 * Controls the paddle and causes it to follow the x-value of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x >= getWidth()-PADDLE_WIDTH/2) {
			x = getWidth()-PADDLE_WIDTH/2;
		}
		if (x <= PADDLE_WIDTH/2) {
			x = PADDLE_WIDTH/2;
		}
		paddle.setLocation(x-(PADDLE_WIDTH/2),getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}

	/*
	 * Creates the colored rows of bricks for the game.
	 * Precondition: Canvas is blank.
	 * Postcondition: The canvas contains ten rows of colored bricks.
	 */
	private void createBricks() {
		for (row = 0; row<NBRICK_ROWS; row++) {
			for (int j = 0; j<NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				double brickLength = ((NBRICK_COLUMNS*BRICK_WIDTH) + (NBRICK_COLUMNS - 1)*BRICK_SEP);
				add(brick, getWidth()/2 - (brickLength/2) +j*(BRICK_WIDTH+BRICK_SEP), BRICK_Y_OFFSET+row*(BRICK_HEIGHT+BRICK_SEP));
				brick.setFilled(true);
				Color brickColor = getColor();
				brick.setColor(brickColor);
			}
		}
	}
	/*
	 *Returns the color that the specific row should be.
	 */
	private Color getColor() {
		if (row % 10  == 0 || row % 10 == 1) {
			return Color.RED;
		}
		if (row % 10 == 2 || row % 10 == 3) {
			return Color.ORANGE;
		}
		if (row % 10 == 4 || row % 10 == 5) {
			return Color.YELLOW;
		}
		if (row % 10 == 6 || row % 10 == 7) {
			return Color.GREEN;
		}
		if (row % 10 == 8 || row % 10 == 9) {
			return Color.CYAN;
		}
		return null;
	}
}