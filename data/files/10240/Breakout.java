/*
 * File: Breakout.java
 * -------------------
 * Name: cathy yang
 * Section Leader: jordan rosen-kaplan
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
	
	// Width of the entire brick block 
	public static final double BRICK_BLOCK_WIDTH = NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = CANVAS_WIDTH;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 5.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//random number generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//brick number counter
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;
	
	//turn number counter starting from 1
	private int turn = 0;
	
	//score counter and score label
	private int scoreCounter = 0;
	GLabel score = null;
	
	//paddle object and fixed paddle y coordinate
	private GRect paddle = null; 
	private double paddleY;
	
	// the ball and the vertical velocity of the ball
	private GOval ball;
	private double vy;
	
	//sound clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);	
		while(true) {
			setUpGame();
			playGame();
			restart();
		}
	}

	/**
	 * Method: restart
	 * --------------
	 * resets the game and all variables for a fresh new start
	 */
	private void restart() {
		removeAll();
		GLabel newgame = new GLabel ("Click for a new game.");
		centerLabel(newgame);
		turn = 0;
		scoreCounter = 0;
		brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;
	}

	/**
	 * Method: play game
	 * ---------------
	 * this method plays the game and displays a result
	 * at the end
	 */
	private void playGame() {
		while (turn < NTURNS && brickCounter != 0) {
			makeBall();
			GLabel start = new GLabel ("Click to start game.");
			centerLabel(start);
			ballBounce();
			displayResult();
		}
	}
	
	/**
	 * Method: display result
	 * ----------------------
	 * displays result based on number of turns left
	 * and whether user has completed task of eliminating bricks
	 */
	private void displayResult() {
		//lose label
		GLabel loss = new GLabel ("You lost!");
		loss.setColor(Color.red);
		//win label
		GLabel win = new GLabel ("You won!");
		win.setColor(Color.green);
		//another turn label
		GLabel another = new GLabel("Next turn! You have " + (NTURNS - turn) + " turns left.");
		another.setColor(Color.orange);
		if (brickCounter == 0) {
			centerLabel(win);
			remove(ball);
		} else if (turn < NTURNS) {
			centerLabel(another);
		} else {
			centerLabel(loss);
		}
	}
	

	/**
	 * Method: center label
	 * @param label
	 * takes in a label and displays it centered on screen 
	 * above the ball, then removes when user clicks
	 */
	private void centerLabel(GLabel label) {
		double labelX = (getWidth() - label.getWidth())/2;
		double labelY = (getHeight() - label.getAscent())/2 - 2*BALL_RADIUS;
		add(label, labelX, labelY);
		waitForClick();
		remove(label);	
	}

	/**
	 * Method: ball bounce 
	 * -------------------
	 * makes the ball bounce off the walls and the paddle
	 * but eliminate and bounce off bricks 
	 */
	private void ballBounce() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;	
		if(rgen.nextBoolean(.5)) {
			vx = -vx; 
		}
		if(rgen.nextBoolean(.5)) {
			vy = -vy; 
		}
		while(brickCounter != 0) {
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx=-vx;
			}
			if(hitTopWall(ball)) {
				vy=-vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				turn++;
				break;
			}
			ball.move(vx, vy);
			pause(DELAY);
			collision();		
		}
	}
	
	/**
	 * Method: collision 
	 * ----------------
	 * if the ball is colliding with an object, checks
	 * to see if paddle or brick; if colliding with paddle,
	 * bounces ball, if colliding with brick, eliminates 
	 * brick then bounces ball
	 */
	private void collision() {
		GObject collider = getCollidingObject();
		if(collider != null) {
			if(collider == paddle) {
				vy = - Math.abs(vy);
				bounceClip.play();
			} else if (collider != score) {
				bounceClip.play();
				remove(collider);
				scoreCounter(collider);
				vy = -vy;
				brickCounter--;	
			}
		}
	}

	/**
	 * Method: score counter
	 * @param brick
	 * this method keeps track of the score by modifying the variable
	 * scoreCounter every time a brick is eliminated based on brick color
	 * and then updating the score label
	 */
	private void scoreCounter(GObject brick) {
		Color brickColor = brick.getColor(); 
		if(brickColor == Color.cyan) {
			scoreCounter += 5;
		} else if (brickColor == Color.green) {
			scoreCounter += 10;
		} else if (brickColor == Color.yellow) {
			scoreCounter += 15;
		} else if (brickColor == Color.orange) {
			scoreCounter += 20;
		} else if (brickColor == Color.red) {
			scoreCounter +=25;
		}
		score.setLabel("Score: " + scoreCounter);
	}

	/**
	 * Method: get colliding object 
	 * ---------------------
	 * @return object which ball is colliding with
	 * this method checks the four corners of the ball
	 * and returns an object if the ball is touching one
	 * returns null if ball is not touching any object.
	 */	
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		if (getElementAt(x,y) != null) {
			return getElementAt(x,y);
		} else if (getElementAt(x+2*BALL_RADIUS, y) != null) {
			return getElementAt(x+2*BALL_RADIUS, y);
		} else if (getElementAt(x, y+2*BALL_RADIUS) != null) {
			return getElementAt(x, y+2*BALL_RADIUS);
		} else if (getElementAt(x+2*BALL_RADIUS, y+2*BALL_RADIUS) != null) {
			return getElementAt(x+2*BALL_RADIUS, y+2*BALL_RADIUS);
		} else {
			return null;
		}
	}

	/**
	 * Method: hit top wall
	 * @param ball
	 * @return if ball hits top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/**
	 * Method: hit bottom wall
	 * @param ball
	 * @return if ball hits bottom wall
	 * checks if ball has hit the bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > (getHeight() - ball.getHeight());
	}

	/**
	 * Method: hit right wall
	 * @param ball
	 * @return if ball hits right wall
	 * checks if ball has hit the right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= (getWidth() - ball.getWidth());
	}
	
	/**
	 * Method: hit left wall
	 * @param ball
	 * @return if ball hits left wall
	 * checks if ball has hit the left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/**
	 * Method: make ball
	 * ------------------
	 * this method makes a filled ball and adds it to the center
	 */
	private void makeBall() {
		double centerX = (getWidth() - 2*BALL_RADIUS)/2;
		double centerY = (getHeight() - 2*BALL_RADIUS)/2;
		ball = new GOval (2*BALL_RADIUS, 2*BALL_RADIUS);	
		ball.setFilled(true);
		add(ball, centerX, centerY);
	}

	/**
	 * Method: set up 
	 * --------------
	 * sets up bricks and paddle to be ready for gameplay
	 */
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setUpScore();
		setUpBricks();
		setUpPaddle();
	}

	/**
	 * Method: set up score
	 * this method sets up the score display as a label
	 * on the top left corner of the screen
	 */
	private void setUpScore() {
		score = new GLabel ("Score: " + scoreCounter);
		add(score, 30, 30);
	}

	/**
	 * Method: set up paddle
	 * --------------------
	 * this method sets up the paddle to track the mouse's movement
	 */
	private void setUpPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (x, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();		
	}
		
	/**
	 * Method: track paddle
	 * ---------------------------
	 * this method changes the location of the paddle to follow
	 * the x coordinate of the mouse 
	 */
	public void mouseMoved (MouseEvent e) {
		double paddleX = e.getX() - (PADDLE_WIDTH / 2);
		double screenRight = getWidth() - PADDLE_WIDTH/2;
		double screenLeft = PADDLE_WIDTH/2; 
		if (e.getX() > screenLeft && e.getX() < screenRight) {
			paddle.setLocation(paddleX, paddleY);
		}
	}

	/**
	 * Method: set up bricks
	 * ---------------------
	 * This method sets up the bricks in the game by drawing a 
	 * number of brick stripes in different colors
	 */
	private void setUpBricks() {
		drawBrickStripe(Color.RED, 0);
		drawBrickStripe(Color.ORANGE, 1);
		drawBrickStripe(Color.YELLOW, 2);
		drawBrickStripe(Color.GREEN, 3);
		drawBrickStripe(Color.CYAN, 4);
	}

	/**
	 * Method: draw brick stripe 
	 * -------------------------
	 * this method draws two rows of bricks of a certain color
	 * @param stripeColor is the color of the two rows 
	 * @param row is the number of stripe (by color) from the top down
	 * 		  starting from 0
	 */
	private void drawBrickStripe(Color stripeColor, int row) {
		double stripeStart = row * (2*(BRICK_HEIGHT + BRICK_SEP));
		for (int j = 0; j < 2; j ++) {
			for (int i = 0; i < NBRICK_COLUMNS; i ++) {
				drawBrick(stripeColor, stripeStart, i, j);
			}
		}
	}
	
	/**
	 * Method: draw brick
	 * ------------------
	 * this method draws a single brick
	 * @param brickColor is the color of the brick 
	 * @param stripeStart is the starting y coordinate of a certain colored stripe (2 rows)
	 * @param i is the column index of the brick starting from 0
	 * @param j is the row index of the row within the 2 row stripe starting from 0 
	 */
	private void drawBrick(Color brickColor, double stripeStart, int i, int j) {
		double startX = (getWidth() - BRICK_BLOCK_WIDTH)/2;
		double x = startX + i * (BRICK_WIDTH + BRICK_SEP);
		double y = BRICK_Y_OFFSET + stripeStart + j * (BRICK_HEIGHT + BRICK_SEP);
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(brickColor);
		add (brick);
	}
	
}
