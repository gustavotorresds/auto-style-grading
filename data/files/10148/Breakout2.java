/*
 * File: Breakout.java
 * -------------------
 * Name: Jessica Yeung
 * Section Leader: Maggie Davis
 * 
 * This file will eventually implement the game of Breakout and has the code for extensions.
 * Added sounds, click to start label at beginning of game, life counter in the bottom corner,
 * has messages for winning or losing the game, and speeds up the ball slightly with each
 * hit of the paddle.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout2 extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
	private GRect brick = null;
	private GLabel lifeCounter = new GLabel("");
	private int lives = NTURNS;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx,vy;
	private int count = NBRICK_COLUMNS*NBRICK_ROWS;
	private GLabel start = new GLabel("CLICK TO START!");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		setUpGame();
		//Loops game play each time player loses a life.
		while (lives > 0 || count > 0) {		
			countLives();
			waitForClick();
			moveBall();
			//if player removes all bricks, win screen displays.
			if (count == 0) {
				signalWinGame();
				break; 
			}
			remove(ball);
			createBall();
			//if player loses all lives, lose screen displays.
			if (lives == 0) {
				signalLoseGame();
				break;
			}	
		}		
	}
	
	//Adds blocks, paddle, and ball to screen.
	private void setUpGame() {
		createBlocks();
		createPaddle();
		createBall();
		addStartLabel();
	}
	
	//Adds "Click to start" instruction to screen.
	private void addStartLabel() {
		double sx = start.getWidth();
		add(start,(getWidth()-sx)/2.0,getHeight()/2.0 + 30);
	}
	
	/*
	 * Creates rows of bricks starting at a given distance from the top. 
	 * If loops are added to color in the rows in a set pattern for n number of rows.
	 */
	private void createBlocks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = (getWidth() - NBRICK_COLUMNS*(BRICK_WIDTH) - (NBRICK_COLUMNS-1)*BRICK_SEP)/2 + col*(BRICK_WIDTH + BRICK_SEP);
				double y = getHeight() - (getHeight() - BRICK_Y_OFFSET) + row*(BRICK_HEIGHT + BRICK_SEP);
				brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				add(brick);
				if (row % 10 == 1 || row % 10 == 0) {
					brick.setColor(Color.RED);
					brick.setFilled(true);
					brick.setFillColor(Color.RED);
				}
				if (row % 10 == 3 || row % 10 == 2) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
					brick.setFillColor(Color.ORANGE);
				}
				if (row % 10 == 4 || row % 10 == 5) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
					brick.setFillColor(Color.YELLOW);
				}
				if (row % 10 == 6 || row % 10 == 7) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
					brick.setFillColor(Color.GREEN);
				}
				if (row % 10 == 8 || row % 10 == 9) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);
					brick.setFillColor(Color.CYAN);
				}
			}	
		}
	}
	
	//Creates paddle of set width and height, color black.
	private void createPaddle() {
		add(paddle, (getWidth() - PADDLE_WIDTH)/2,getHeight() - PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
	}
	
	//Creates ball placed at the center of the screen at the bottom right above the paddle.
	private void createBall() {
		double xPosition = (getWidth() - 2*BALL_RADIUS)/2;
		double yPosition = (getHeight() - 2*BALL_RADIUS)/2;
		add(ball, xPosition, yPosition);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
	}
	
	/*
	 * Tells ball when it has hit the side of the walls to change direction of velocity.
	 * Also tells game when ball exits the screen at the bottom.
	 */
	private void moveBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		while(count > 0) {
			ball.move(vx, vy);
			pause(DELAY);
			if (ball.getX() > (getWidth()-BALL_RADIUS*2.0)) {
				vx = -vx;
			}
			if (ball.getX() < 0) {
				vx = - vx;
			}
			if (ball.getY() < 0) {
				vy = - vy;
			}
			if (ball.getY() > getHeight()) {
				lives--;
				break;
			}
			checkCollision();
		}
		
	}
	
	//Tells the ball what to do depending on what object it hits.
	private void checkCollision()  {
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				//increases the velocity of the ball slightly each time it hits the paddle to make game play harder.
				vy = -(Math.abs(vy))*1.02;
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
			}	
			//removes a brick and changes vertical direction of ball if ball hits a brick.
			else if (collider != lifeCounter){
				vy = -vy;
				count--;
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
				remove(collider);
			}
		}
	}
	
	//Tells game what object the ball hits by checking each corner coordinate of the ball.
	private GObject getCollidingObject() {
		double x1 = ball.getX();
		double y1 = ball.getY();
		//checks object at upper left corner of ball.
		GObject object = getElementAt(x1,y1);
		double x2 = ball.getX() + 2*BALL_RADIUS;
		//checks object at upper right corner of ball.
		GObject object2 = getElementAt(x2,y1);
		double y2 = ball.getY() + 2*BALL_RADIUS;
		//checks object at lower left corner of ball.
		GObject object3 = getElementAt(x1,y2);
		//checks object at lower right corner of ball.
		GObject object4 = getElementAt(x2,y2);
		if (object != null) {
			return(object);
		}
		if (object2 != null) {
			return(object2);
		}
		if (object3 != null) {
			return(object3);
		}
		if (object4 != null) {
			return(object4);
		}
		else {
			return(null);
		}		
	}
	
	//Adds a label to tell player how many lives they have during game play.
	private void countLives() {
		lifeCounter.setLabel("Lives: " + lives);
		double x = lifeCounter.getWidth();
		add(lifeCounter, getWidth() - 1.5*x, getHeight() - 10);
	}
	
	
	//Displays message on screen to tell player they won the game.
	private void signalWinGame() {
		GRect background = new GRect(0,0,getWidth(),getHeight());
		background.setFilled(true);
		background.setColor(Color.BLACK);
		add(background);
		GLabel winner = new GLabel("YOU WON!");
		double labelWidth = winner.getWidth();
		double labelHeight = winner.getAscent();
		double x = (getWidth() - labelWidth)/2.0;
		double y = (getHeight() - labelHeight)/2.0;
		winner.setLocation(x,y);
		winner.setColor(Color.GREEN);
		add(winner);
	}
	
	//Displays message on screen to tell player they LOST the game.
	private void signalLoseGame() {
		GRect background = new GRect(0,0,getWidth(),getHeight());
		background.setFilled(true);
		background.setColor(Color.BLACK);
		add(background);
		GLabel loser = new GLabel("YOU LOST YA BUM!");
		double labelWidth = loser.getWidth();
		double labelHeight = loser.getAscent();
		double x = (getWidth() - labelWidth)/2.0;
		double y = (getHeight() - labelHeight)/2.0;
		loser.setLocation(x,y);
		loser.setColor(Color.RED);
		add(loser);
	}
	
	//Allows the mouse to move the paddle only on the x coordinate plane.
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		if (x<(getWidth() - PADDLE_WIDTH)) {
			add(paddle,x,y);	
		}
	}
	
	//removes the start message from the screen when player initiates game play.
	public void mouseClicked(MouseEvent e) {
		remove(start);
	}
	
	
		
	
}
