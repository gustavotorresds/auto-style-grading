/*
 * File: Breakout.java
 * -------------------
 * Name: Tyler J. Layden
 * Section Leader: Justin Xu
 * 
 * This file allows you to play the pinnacle of trendy
 * 1990's cell phone games... BREAKOUT!!! Move the paddle
 * with your mouse and try to destroy all the bricks. But 
 * don't let the ball go below the paddle!
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import javafx.scene.input.MouseDragEvent;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 500;			//original value was 420
	public static final double CANVAS_HEIGHT = 800;	//original value was 600

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

	// Diameter of the ball in pixels
	public static final double BALL_DIAMETER = 10;

	// The ball's vertical velocity. Keep it below the height of the blocks
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	// Velocity variables for the ball
	private double vx = 0, vy = 0;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Number of Bricks left in the world
	private double brickNum = NBRICK_ROWS*NBRICK_COLUMNS;

	// Coordinates of the ball
	double ballX = CANVAS_WIDTH/2-BALL_DIAMETER;
	double ballY = CANVAS_HEIGHT/2-BALL_DIAMETER;

	// Instance shapes to be used in the game
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);
	private GRect block = null;

	// Controls the amount of lives remaining. 
	//There is a three second timer between lives.
	private int lives = NTURNS;
	private GLabel life = new GLabel("Lives: " +lives);

	//Values used for point counting.
	private double points = 0;
	GLabel pointCount = new GLabel("Points: " + points);

	//Audio clip for the ball bouncing
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addBlocks();
		addBall();
		addlePaddle();
		addLifeCounter();
		add(pointCount, getWidth() - pointCount.getWidth() - 30, BRICK_Y_OFFSET/2);

		addMouseListeners();

		while(brickNum > 0 && lives > 0) {
			ball.move(vx,vy);
			pause(DELAY);

			GObject collider = getCollidingObject();
			if (collider != paddle && collider != null && collider != life && collider != pointCount) {
				remove(collider);
				brickNum--;
				vy = -vy;
				getPoints(collider.getY());
				bounceClip.play();
			}
			else if (collider != null && collider != life && collider != pointCount) {
				vy = -vy;
				checkForSkill();
				bounceClip.play();
			}
			bounce();
		}
		if (lives == 0) {																			//Condition for loss
			GLabel gO = new GLabel("GAME OVER");
			add(gO, (getWidth() - gO.getWidth()) / 2, getHeight() / 2 - gO.getAscent() - BALL_DIAMETER);
		}
		else {																						//Condition for victory
			GLabel win = new GLabel("Congratulations! You Win!");
			add(win, (getWidth() - win.getWidth()) / 2, (getHeight() - win.getAscent()) / 2);
		}
	}

	//Starts the game when the mouse is pressed.
	public void mouseClicked (MouseEvent c) {
		if (vx == 0) {
			startGame();
		}
	}

	//Tells the paddle to track the mouse's x coordinate
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		paddle.setLocation(mouseX, getHeight()-PADDLE_Y_OFFSET);
	}


	/**
	 * Method: Get Points
	 * ------------------
	 * Main method for adding points to your total. Blocks are worth
	 * progressively more points the further up they are, and
	 * hitting the farthest rows early on is sure to nab some
	 * extra points.
	 */
	private void getPoints(double y) {
		if (y <= 2*NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP)/10) {
			points = points + 100 + 1000*(brickNum / (NBRICK_ROWS*NBRICK_COLUMNS));
		}
		else if (y <= 4 * NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP) / 10 && y >= 2 * NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP) / 10) {
			points = points + 100;
		}
		else if (y <= 6 * NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP) / 10 && y >= 4 * NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP) / 10) {
			points = points + 50;
		}
		pointCount.setLabel("Points: " + points);
	}

	/**
	 * Method: Add Life Counter
	 * ------------------------
	 * Pretty intuitive. It adds a life
	 * counter to the top left of the screen.
	 */
	private void addLifeCounter() {
		add(life, 0, BRICK_Y_OFFSET / 2);
	}

	/** 
	 * Method: Check for Skill
	 * -----------------------
	 * Allows the user to reflect the ball's x velocity as well
	 * as y by using the outer thirds of the paddle.
	 */
	private void checkForSkill() {
		if (vx > 0 && ball.getX() + 2 * BALL_DIAMETER <= paddle.getX() + PADDLE_WIDTH / 3) {
			vx = -vx;
		}
		else if (vx < 0 && ball.getX() >= paddle.getX() +2 * PADDLE_WIDTH/3) {
			vx = -vx;
		}
	}

	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Checks the four corners of the ball for any obstacles.
	 * Mainly used to change the ball's y velocity, but also
	 * clears bricks.
	 */
	private GObject getCollidingObject() {
		GObject blocker = null;
		double cornerX = ball.getX();
		double cornerY = ball.getY();

		for(int i=0; i<4; i++) {
			if (i == 2 || i == 3) {
				cornerX = ball.getX() + 2*BALL_DIAMETER;
			}
			if (i == 1 || i == 2) {
				cornerY = ball.getY() + 2*BALL_DIAMETER;
			}
			if (i == 3) {
				cornerY = ball.getY();
			}
			GObject contact = getElementAt(cornerX, cornerY);
			if (contact != null) {
				blocker = contact;
			}
		}
		return blocker;
	}

	/**
	 * Method: Bounce
	 * --------------
	 * Causes the ball to bounce off of the walls. If the ball hits
	 * the lower boundary, the ball resets, lowers the life counter,
	 * and starts the game anew after a 3 second break.
	 */
	private void bounce() {
		if(ball.getX() <= 0 || ball.getX() + BALL_DIAMETER >= getWidth()) {
			vx = -vx;
		}
		if(ball.getY() <= 0) {
			vy = -vy;
		}
		if (ball.getY() >= getHeight() - BALL_DIAMETER) {
			remove(ball);
			AudioClip marioDies = MediaTools.loadAudioClip("smb_mariodie.wav");										//adds a nice death noise to soften the blow
			marioDies.play();
			lives = lives - 1;
			life.setLabel("Lives: " + lives);
			add(ball, ballX, ballY);
			if (lives != 0) {
				for (int i = 3; i > 0; i--) {

					GLabel countdown = new GLabel("" + i);															//Countdown timer to let you know when the game is going to restart
					add(countdown, (getWidth() - countdown.getWidth()-BALL_DIAMETER)/2, getHeight()/2 - 3*BALL_DIAMETER);
					countdown.setLabel("" + i);
					pause(1000);
					remove(countdown);
				}
			}
			startGame();
		}
	}

	/**
	 * Method: Start Game
	 * ------------------
	 * Starts the ball's motion and initiates the game.
	 */
	private void startGame() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/**
	 * Method: Addle Paddle
	 * --------------------
	 * Addles (adds) the paddle to the screen, so you can
	 * block the ball.
	 */
	private void addlePaddle() {
		double paddleX = (getWidth()-PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, paddleX, paddleY);
	}

	/**
	 * Method: Add Ball
	 * ----------------
	 * Adds the ball to the game screen. Gotta have
	 * something to hit, right?
	 */
	private void addBall() {
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/**
	 * Method: Add Blocks
	 * ------------------
	 * Adds the blocks to the screen in the correct color order.
	 */
	private void addBlocks() {
		double x = BRICK_SEP;
		double y = BRICK_Y_OFFSET;
		for(int i=0; i<NBRICK_ROWS; i++) {
			y = BRICK_Y_OFFSET + i * (BRICK_HEIGHT+BRICK_SEP);							//Switches row in which bricks are added
			for(int j=0; j<NBRICK_COLUMNS; j++) {
				block = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				block.setFilled(true);
				block.setColor(changeColor(i));
				add(block, x + j * (BRICK_WIDTH + BRICK_SEP), y);
			}
		}
	}

	/**
	 * Method: Change Color
	 * --------------------
	 * Selects the color output to make the bricks.
	 * Decides the output based on the integer put into 
	 * the method.
	 */
	private Color changeColor(int i) {
		Color fill = null;
		if(i < NBRICK_ROWS / 5) {
			fill = Color.RED;
		}
		else if(NBRICK_ROWS / 5 <= i && i < 2 * NBRICK_ROWS / 5) {
			fill = Color.ORANGE;
		}
		else if(2 * NBRICK_ROWS / 5 <= i && i < 3 * NBRICK_ROWS / 5) {
			fill = Color.YELLOW;
		}
		else if(3 * NBRICK_ROWS / 5 <= i && i < 4 * NBRICK_ROWS / 5) {
			fill = Color.GREEN;
		}
		else if(4 * NBRICK_ROWS / 5 <= i && i <= NBRICK_ROWS) {
			fill = Color.CYAN;
		}
		return fill;
	}
}
