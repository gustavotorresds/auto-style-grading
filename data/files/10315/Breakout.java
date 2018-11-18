/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	private static final long serialVersionUID = 1L;
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
	public static final double VELOCITY_Y = 2.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	//Indent to use for labels
	public static final int INDENT = 20;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		initialConfig();
		
		//initialize random velocity variables
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		addTitleLabel(titleLabel);
	
		
		
		while(turnsLeft >= 0) {
			//add mouse listeners
			addMouseListeners();
			
			//add ball movements
			ball.move(vx, -vy);
			checkBallBoundaries(ball);
			
			//evaluate objects ball encounters. act accordingly.
			GObject collider = getCollidingObject();
			evaluateCollidingObject(collider);
			
			//add dynamic labels
			if (turnsLeft != -1) {
				addTurnsLeftLabel(turnsLeftLabel);
			}
			addScoreLabel(scoreLabel);
			
			//delay to create animation
			pause(DELAY);
		}
			addGameOverLabel(gameOverLabel);		
	}
	
	/*
	 * This method describes the initial configuration of the game.
	 * It produces a wall of bricks, ball and paddle.
	 * Helper Methods: makeBall, makePaddle, makeBrick, addRow.
	 */
	private void initialConfig() {
		
		//creates initial configuration of bricks
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double dy = 70 + (row * BRICK_HEIGHT);
			addRow(dy, row);
		}
		
		//adds paddle and ball objects
		add(makePaddle());
		add(makeBall());	
	}
	
	//makes a paddle
	private GObject makePaddle() {
		paddle = new GRect( (CANVAS_WIDTH / 2) - PADDLE_HEIGHT, (CANVAS_HEIGHT - PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}
	
	//makes a ball
	private GOval makeBall() {
		ball = new GOval( (CANVAS_WIDTH / 2) - BALL_RADIUS, (CANVAS_HEIGHT / 2) - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.GRAY);
		return ball;
	}
	
	//adds a row of bricks
		private void addRow(double dy, int row) {
			for (int brick = 0; brick < NBRICK_COLUMNS; brick++) {
				double dx = (brick * BRICK_WIDTH) + BRICK_SEP;
				makeBrick(dx, dy, row);	
			}	
		}
	
	//makes a brick with the appropriate colors
		private void makeBrick(double dx, double dy, int row) {
			
			//create brick 
			GRect brick = new GRect(dx, dy, BRICK_WIDTH, BRICK_HEIGHT);
			
			//activate and determine color for brick
			brick.setFilled(true);
			switch (row) {
			case 0: brick.setFillColor(Color.RED); break;
			case 1: brick.setFillColor(Color.RED); break;
			
			case 2: brick.setFillColor(Color.ORANGE); break;
			case 3: brick.setFillColor(Color.ORANGE); break;
			
			case 4: brick.setFillColor(Color.YELLOW); break;
			case 5: brick.setFillColor(Color.YELLOW); break;
			
			case 6: brick.setFillColor(Color.GREEN); break;
			case 7: brick.setFillColor(Color.GREEN); break;
			
			case 8: brick.setFillColor(Color.CYAN); break;
			case 9: brick.setFillColor(Color.CYAN); break;
			}
			//add brick to canvas
			add(brick);
		}
		
		
		//gets coordinates of mouse when clicked and gets the gobj at those coordinates
		public void mousePressed(MouseEvent e) {
			lastX = e.getX();
		}
		
		//if an object is present when dragged, it drags the object while keeping it 
		//within specified boundary
		public void mouseDragged(MouseEvent e) {
			if (isWithinBoundary()) {
				paddle.move(e.getX()-lastX, 0);
				lastX = e.getX();
			} else {
				bumpPaddle();
			}
		}
		
		//moves paddle back into game region if outside
		private void bumpPaddle() {
			if (paddle.getX() < 0) {
				paddle.move(3, 0);
			} else {
				paddle.move(-3,0);
			} 
		}

		//checks if paddle is within boundary
		private boolean isWithinBoundary() {
			if (paddle.getX() < 0 || paddle.getX() >= (CANVAS_WIDTH - PADDLE_WIDTH)) {
				return false;
			}
			else {
				return true;
			}
		}
		
		/*
		 * checks to see if the ball is at a boundary and acts accordingly
		 * helper methods: booleans to return T/F if at a given boundary;
		 * if at bottom, carry out play loses method.
		 */
		private void checkBallBoundaries(GOval ball) {

			//check if ball is at boundary
			//adjust velocity accordingly
			if (isLeftWall(ball)) {
				vx = -vx;
			}
			if (isRightWall(ball)) {
				vx = -vx;
			}
			if (isTopWall(ball)) {
				vy = -vy;
			} 
			if (isBottomWall(ball)) {
				playerLost();
			}
		}
		
		private void playerLost() {
			
			//inform player
			addYouLoseLabel(youLoseLabel);
			
			//reset ball
			remove(ball);
			pause(500);
			add(makeBall());
			
			//update number of turns left
			turnsLeft--;
		}
		
		//helper methods to determine if ball is at a boundary
		private boolean isBottomWall(GOval ball) {
			return ball.getY() > CANVAS_HEIGHT - ball.getHeight();
		}
		
		private boolean isTopWall(GOval ball) {
			return ball.getY() < 0;
		}
		
		private boolean isLeftWall(GOval ball) {
			return ball.getX() > CANVAS_WIDTH - ball.getHeight();
		}
		
		private boolean isRightWall(GOval ball) {
			return ball.getX() < 0;
		}
		
		
		/*
		 * This method determines if one of the corners of the ball
		 * collides with an object and returns that object.
		 * helper methods: getCornerX, getCornerY
		 */
		private GObject getCollidingObject() {
			GObject isObject = null;
			for (int i = 0; i < 4; i++) {
				isObject = getElementAt(getCornerX(i),getCornerY(i));
				if (isObject != null) return isObject;
			}
			return isObject;
		}
		
		//gets x-coordinates for each corner of ball
		private double getCornerX (int corner) {
			double cornerX = 0.0;
			switch (corner) {
			case 1: 
				cornerX = ball.getX(); 
				break;
			case 2:
				cornerX = ball.getX() + 2 * BALL_RADIUS; 
				break;
			case 3:
				cornerX = ball.getX() + 2 * BALL_RADIUS; 
				break;
			case 4: 
				cornerX = ball.getX(); 
				break;
			}
			return cornerX;
		}
		
		//gets y-coordinates for each corner of ball
		private double getCornerY (int corner) {
			double cornerY = 0.0;
			switch (corner) {
			case 1: 
				cornerY = ball.getY(); 
				break;
			case 2:
				cornerY = ball.getY(); 
				break;
			case 3:
				cornerY = ball.getY() + 2 * BALL_RADIUS; 
				break;
			case 4: 
				cornerY = ball.getY()+ 2 * BALL_RADIUS;
				break;
			}
			return cornerY;
		}
		
		/*
		 * this method determines if an object is a paddle or a brick and acts
		 * accordingly.
		 * helper methods: accelerate paddle.
		 */
		private void evaluateCollidingObject(GObject collider) {
			if (collider == paddle) {
				vy = -vy;
				accelerator++;
				if ( (accelerator % 7) == 0 && accelerator != 0) {
					vx = KICKER + vx;
				}
			} else  if (collider != null) {
				remove(collider);
				vy = -vy;
				bricksRemaining--;
				if (bricksRemaining == 0) {
					addYouWinLabel(youWinLabel);
					vx = 0;
					vy = 0;
					pause(10000);
				}
			} 
		}
		
		//add title label
		private void addTitleLabel(GLabel label) {
			label.setFont("Courier-24");
			label.setColor(Color.BLUE);
			label.setLabel("Ball Breaker");
			add(titleLabel, INDENT, CANVAS_HEIGHT + INDENT);
		}
		
		//add label to track score
		private void addScoreLabel(GLabel label) {
			label.setFont("Courier-18");
			label.setColor(Color.BLACK);
			label.setLabel("score: " + bricksRemaining);
			add(label, INDENT, CANVAS_HEIGHT + (turnsLeftLabel.getHeight()) + (2 *titleLabel.getHeight() ));
		}
		
		//add label to track score
		private void addTurnsLeftLabel(GLabel label) {
			label.setFont("Courier-18");
			label.setColor(Color.BLACK);
			label.setLabel("Attempts: " + turnsLeft);
			add(label, INDENT, CANVAS_HEIGHT + (2 *titleLabel.getHeight() ));
		}
		
		private void addGameOverLabel(GLabel label) {
			label.setFont("Courier-24");
			label.setColor(Color.BLACK);
			label.setLabel("GAME OVER");
			add(label, (CANVAS_WIDTH / 2) - (label.getHeight() / 2) , (CANVAS_HEIGHT / 2) - label.getHeight());
		}
		
		private void addYouWinLabel(GLabel label) {
			label.setFont("Courier-24");
			label.setColor(Color.BLACK);
			label.setLabel("Congratulations! You Win");
			add(label, (CANVAS_WIDTH / 2) - (label.getHeight() / 2) , (CANVAS_HEIGHT / 2) - label.getHeight());
		}
		
		private void addYouLoseLabel (GLabel label) {
			label.setFont("Courier-18");
			label.setColor(Color.RED);
			label.setLabel("Whoops! Try again.");
			add(label, (CANVAS_WIDTH / 2) - (label.getHeight() / 2) , (CANVAS_HEIGHT / 2) - label.getHeight());
			
			pause(3000);
			remove(label);
		}
		
		/*private instance variables*/	
		private GRect paddle = null;
		private GOval ball = null;
		
		private RandomGenerator rgen = RandomGenerator.getInstance();
		
		private double lastX;
		private double vx;
		private double vy = VELOCITY_Y;
		private int accelerator = 0;
		
		private static final int KICKER = 3;
		
		private GLabel titleLabel = new GLabel("");
		private GLabel scoreLabel = new GLabel("");
		private GLabel turnsLeftLabel = new GLabel("");
		private GLabel youLoseLabel = new GLabel("");
		private GLabel gameOverLabel = new GLabel("");
		private GLabel youWinLabel = new GLabel("");
		
		private int turnsLeft = NTURNS;
		private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;

}