/*
 * File: Breakout.java
 * -------------------
 * Name: Nicholas Bernhardt-Lanier
 * Section Leader: Kaitlyn
 * 
 * This file plays the game of Breakout 
 * by breaking all 100 bricks without dying 
 * more than two times.
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
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle; 
	private GOval ball; 
	private int turnsUsed = 0; 
	private int NBRICKS = 100; 
	private double vx;
	private double vy = VELOCITY_Y;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collidedBlock;

	public void run() {
		setUpGame(); 
		playBreakout();
	}

	/*
	 * This method initializes the game by setting
	 * up the game. The bricks and paddle are 
	 * all placed correctly. 
	 */

	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);		
		setBricks(); 
		addMouseListeners(); 
		setMovingPaddle(); 
	}

	/*
	 * This method commences the game by first making
	 * the ball move once the mouse is clicked 
	 * and following the normal parameters of the game 
	 * by bouncing, colliding with walls and removing 
	 * bricks. 
	 */

	private void playBreakout() {
		while(NTURNS > turnsUsed) {
			makeBall();
			waitForClick();
			findBallVelocity();
			while (!gameOver()) {
				moveBall();
				checkCollisions(); 
				pause(DELAY);
			}
		}
	}

	/*
	 * This method takes into account the number of bricks
	 * to decide if the game should cease. Once the user
	 * has lost 3 times, the program displays the losing
	 * message.
	 */

	private boolean gameOver() {
		if(NBRICKS == 0) {
			displayWinningMessage();
			turnsUsed = NTURNS;
			return true;
		} else if( ball.getY() >= getHeight() - 2* BALL_RADIUS) {
			remove(ball);
			turnsUsed++;
			if (turnsUsed == NTURNS) {
				displayLosingMessage();
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * This method simply displays a GLabel that informs
	 * the user the he or she has lost the game.
	 */

	private void displayLosingMessage() {
		GLabel label = new GLabel("HAHAHA you lose! See ya!");
		label.setFont("DialogInput");
		label.setLocation(getWidth() / 2 - label.getWidth() / 2, getHeight() / 2 + label.getAscent() / 2);
		add(label);
	}

	/*
	 * This method simply displays a GLabel that informs
	 * the user the he or she has won the game.
	 */

	private void displayWinningMessage() {
		GLabel label = new GLabel ("Um... It appears you won! You lucky duck!"); 
		label.setLocation(getWidth() / 2 - label.getWidth() / 2, getHeight() / 2 + label.getAscent() / 2);
		label.setFont("DialogInput");
		add(label); 
	}

	/*
	 * This method checks if the ball has touched and
	 * collided with any of its four corners. 
	 */

	private GObject collidingObject() {
		collidedBlock = getElementAt(ball.getX(),ball.getY());
		if (collidedBlock !=null) return (collidedBlock);
		collidedBlock = getElementAt(ball.getX(), ball.getY() + 2* BALL_RADIUS);
		if (collidedBlock !=null) return (collidedBlock);
		collidedBlock = getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY());
		if (collidedBlock !=null) return (collidedBlock);
		collidedBlock = getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY()+ 2* BALL_RADIUS);
		if (collidedBlock !=null) return (collidedBlock);
		return null;
	}

	/*
	 * This method checks if the GOval collides with any 
	 * objects. If the ball does, the method makes the ball
	 * bounce off accordingly.
	 */

	private void checkCollisions() {
		AudioClip bounceSound = MediaTools.loadAudioClip("bounce.au"); 
		GObject collidedBlock = collidingObject(); 
		if (collidedBlock == paddle) { 
			vy = -Math.abs(vy);
			bounceSound.play(); 
		} else if (collidedBlock != null) { 
			vy = -vy;
			bounceSound.play(); 
			remove(collidedBlock);
			NBRICKS--;
		}
	}

	/*
	 * This method waits for the user to click the mouse
	 * then enables the ball to move.  
	 */

	private void findBallVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/*
	 * This method moves the ball while the game is not over,
	 * ensuring that the ball moves in the correct direction. 
	 */

	private void moveBall() {
		ball.move (vx, vy);
		if ((ball.getX()<= 0) || ball.getX() > getWidth() - BALL_RADIUS*2) { 
			vx = -vx;
		}  
		if (ball.getY() <0) {
			vy = -vy;
		} 
	}

	/*
	 * This method simply creates the ball for the game.
	 */

	private void makeBall() {
		double size = BALL_RADIUS * 2;
		ball = new GOval((getWidth()/2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS, size, size );
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);	
	}

	/* 
	 * The mouse controlled by the user is tracked
	 * so that the paddle's location is directly correlated 
	 * to the mouse location.
	 */

	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH /2;
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		if (e.getX() >= getWidth() - PADDLE_WIDTH / 2) {
			paddle.setX(getWidth() - PADDLE_WIDTH);
		}
		if (e.getX() <= PADDLE_WIDTH / 2 ) {
			paddle.setX(0);
		}
	}

	/* 
	 * This method simply creates the paddle at the start of 
	 * the game.
	 */

	private void setMovingPaddle() { 	
		paddle =  new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		add(paddle, x, getHeight() - PADDLE_Y_OFFSET);		
	}

	/* 
	 * This method creates the bricks in the orderly and
	 * assigned fashion.
	 */ 

	private void setBricks() {
		Color color = null; 
		for (int row = 0; row <NBRICK_ROWS ; row++) {
			for (int col = 0; col <NBRICK_COLUMNS ; col++) {
				if (row % 10 < 2 ) {
					color = Color.RED;
				} else 	if (row % 10 < 4 ) {
					color = Color.ORANGE;
				} else 	if (row % 10 < 6 ) {
					color = Color.YELLOW;
				} else 	if (row % 10 < 8 ) {
					color = Color.GREEN;
				} else 	if (row % 10 < 10 ) {
					color = Color.CYAN;
				}
				addSingleBrick (row,col,color);
			}
		}		
	}

	/*
	 * This method assigns each brick a specific row, column and color. 
	 */

	private void addSingleBrick ( int row, int col, Color color) {
		GRect brick = new GRect ((BRICK_SEP ) + ((BRICK_WIDTH + (BRICK_SEP ))*col),((BRICK_SEP) + ((BRICK_HEIGHT + (BRICK_SEP ))*row + BRICK_Y_OFFSET)),BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(color);
		brick.setFilled(true);
		add(brick); 
	}



} 

