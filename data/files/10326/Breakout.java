/*
 * File: Breakout.java
 * -------------------
 * Name:Jeremiah Coleman
 * Section Leader: Jordan Rosen-Kaplan
 * 
 * This file plays the game Breakout. In this game a ball bounces off the side walls, ceiling and a paddle towards the bottom floor.The
 * objective is to have the ball hit each brick until all the bricks are eliminated. However, if the ball goes past the paddle, then you
 * lose a life. Destroy the bricks before you lose all your lives.
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
	public static final double CANVAS_WIDTH = 1000;
	public static final double CANVAS_HEIGHT = 800;

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
	public static final double PADDLE_WIDTH = 120;
	public static final double PADDLE_HEIGHT = 20;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = PADDLE_HEIGHT + 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	private double vx, vy= VELOCITY_Y;
	private GOval ball;
	private GRect paddle;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;

	int ready = 0;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		addMouseListeners();
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		displayIntroScene();


		setupBreakout();
		playBreakout();

	}

	private void setupBreakout() {
		setupBricks();
		setupPaddle();
	}

	private void displayIntroScene() {
		GLabel label = new GLabel("ARE YOU READY? ... Click Anywhere", getWidth()/3, getHeight()/2);
		label.setFont("Courier-24");
		label.setColor(Color.BLACK);
		add(label);

		while (ready == 0) {
			pause(1000);
		}
		
		remove(label);
	}
	

	public void mouseClicked(MouseEvent e) {
		ready=1;
	}

	/*
	 * Setups up the bricks to be centered and equally spaced using the constants given in the beginning of the program
	 */
	private void setupBricks() {
		double totalBrickSpace = (NBRICK_COLUMNS * BRICK_WIDTH) + (NBRICK_COLUMNS  * BRICK_SEP);
		//Initial for loop is for the amount of rows of bricks
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double anchor_X_Coordinate = (getWidth() - totalBrickSpace)/2;
			//Second for loop for the amount of bricks in each row
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				double anchor_Y_Coordinate = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;

				//Designates the color for every 2 rows.
				Color color = null;
				if (row % 10 < 2) {
					color = Color.RED;
				} else if (row % 10 < 4) {
					color = Color.ORANGE;
				} else if (row % 10 < 6) {
					color =Color.YELLOW;
				} else if (row % 10 < 8) {
					color = Color.GREEN;
				} else if (row % 10  < 10) {
					color = Color.CYAN;
				}

				createBrick(anchor_X_Coordinate, anchor_Y_Coordinate, color);
				anchor_X_Coordinate = anchor_X_Coordinate + (BRICK_WIDTH + BRICK_SEP);
			}
		}
	}

	/*
	 *Creates one brick at a give position and color given by parameters
	 */
	private void createBrick(double x, double y, Color color) {
		GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(color);
		add(rect);
	}

	/*
	 * Places the paddle in the correct starting position in the center of the screen
	 */
	private void setupPaddle() {
		paddle = new GRect(getWidth()/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}


	/*
	 * This code makes the center of the paddle move with the mouse movement until the paddle hits one of the edges
	 */
	public void mouseMoved(MouseEvent e) {
		if (ready == 1) {
			double mouse_x = e.getX();
			if (mouse_x < PADDLE_WIDTH/2){
				paddle.setX(0);
			} else if (mouse_x > getWidth() - PADDLE_WIDTH/2) {
				paddle.setRightX(getWidth());
			} else {
				paddle.setCenterX(mouse_x);

			}
		}
	}

	/*
	 * This code adds the ball and starts the ball moving (essentially starting the dynamics of the game) and displays the winning or losing screen
	 * depending on the outcome of the player.
	 */
	private void playBreakout() {
		for (int lives = 0; lives < NTURNS; lives++) {
			if (numberOfBricks != 0) {	
				initiateBall();
				activateBall();
			} else {
				winnerScreen();
				return;
			}
		}
		looserScreen();
	}

	/*
	 * This creates the ball and adds it to the center of the screen below the bricks
	 */
	private void initiateBall() {
		double stackedBrickHeight = NBRICK_ROWS * (BRICK_HEIGHT +BRICK_SEP) + BRICK_Y_OFFSET; 
		ball = new GOval(getWidth()/2 - BALL_RADIUS, stackedBrickHeight, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/* 
	 * This method controls how the ball interacts with the world. The ball starts off with a random velocity towards the bottom of the
	 * screen. Within the animation loop, the ball bounces off the side walls, top walls. bricks and the paddle. When the ball hits
	 * the top of the paddle, it will bounce up but if it hits the side of the paddle, the ball will bounce downward as expected.
	 */
	private void activateBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; 
		}
		
		while(numberOfBricks != 0) {
			if (ball.getRightX() >= getWidth() || ball.getX() <= 0) {
				vx=-vx;
			} else if (ball.getY() <= 0) {
				vy=-vy;
			} else if (ball.getBottomY() >= getHeight()) {
				remove(ball);
				return;
			}

			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy= -vy;
				if (ball.getBottomY() > paddle.getY() && (ball.getRightX() > paddle.getRightX() || ball.getX() < paddle.getX())) {
					vx=-vx;
					vy= -vy;
				}
				bounceClip.play();
			} else if (collider !=null) {
				remove(collider);
				vy=-vy;	
				numberOfBricks--;
				bounceClip.play();
			}

			ball.move(vx, vy);
			pause(DELAY);
		}
		winnerScreen();
	}

	/*
	 * This method returns the object that is being collided with by checking what object is at given x positions.
	 */
	private GObject getCollidingObject() {		
		GObject object = checkCorner(ball.getX(), ball.getY());
		if (object == null) {
			object = checkCorner(ball.getRightX(), ball.getY());
			if (object == null) {
				object = checkCorner(ball.getRightX(), ball.getBottomY());
				if (object == null) {
					object = checkCorner(ball.getX(), ball.getBottomY());
				}

			}
		}
		return object;
	}


	/*
	 * This method checks whether an object is present at a given coordinate. It returns the object or null otherwise
	 */
	private GObject checkCorner(double x,double y) {
		GObject potentialObject = getElementAt(x,y);
		if (potentialObject != null) {
			return potentialObject;
		}
		return null;
	}

	/*
	 * This method displays the winner screen when all the bricks are gone.
	 */
	private void winnerScreen() {
		GRect rect = new GRect(0, 0, getWidth(), getHeight());
		rect.setFilled(true);
		rect.setColor(Color.BLACK);
		add(rect);

		GLabel label = new GLabel("CONGRATULATIONS!", getWidth()/3, getHeight()/2);
		label.setFont("Courier-24");
		label.setColor(Color.WHITE);
		add(label);
	}

	/*
	 * This method displays the loser screen when all the lives are used up.
	 */
	private void looserScreen() {
		GRect rect = new GRect(0, 0, getWidth(), getHeight());
		rect.setFilled(true);
		rect.setColor(Color.BLACK);
		add(rect);

		GLabel label = new GLabel("GAME OVER", getWidth()/3, getHeight()/2);
		label.setFont("Courier-24");
		label.setColor(Color.WHITE);
		add(label);
	}
}


