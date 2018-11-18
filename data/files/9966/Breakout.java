/*
 * File: Breakout.java
 * -------------------
 * Name: Jade Lintott
 * Section Leader: Julia Daniel
 * 
 * This file is the game of Breakout.
 * Extensions
 * score
 * speed up
 * more variation in bouncing
 * sounds
 * 
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
	public static final double VELOCITY_Y = 3.0;

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

	public static final int CYAN_BRICK_SCORE = 10;
	public static final int GREEN_BRICK_SCORE = 20;
	public static final int YELLOW_BRICK_SCORE = 30;
	public static final int ORANGE_BRICK_SCORE = 40;
	public static final int RED_BRICK_SCORE = 50;

	public static final double SPEED_MODIFIER = .25;

	private GRect paddle;
	private GOval ball;
	private double vx;
	private double vy;
	private int score;
	private GLabel scoreDisplay;
	private RandomGenerator rg= RandomGenerator.getInstance();


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		setUpVelocities();
		setUpBricks();
		int brickNumber = NBRICK_COLUMNS * NBRICK_ROWS;
		int paddleHitsCounter=0;
		setUpPaddle();
		setUpScore();
		setUpBall();
		begin();
		while (true) {
			ball.move(vx, vy);
			if(wallCheck()) {
				end(false);
				break;
			}
			GObject collider = getCollidingObject();
			if (collider==paddle) {
				bounceClip.play();
				vy=-vy;
				ball.setLocation(ball.getX(), paddle.getY() - 2 * BALL_RADIUS);
				paddleHitsCounter++;
				if (paddleHitsCounter%7==0) {
					changeSpeed();
				}
			}
			else if(collider!=null && collider!=scoreDisplay) {
				bounceClip.play();
				updateScore(collider.getColor());
				remove(collider);
				brickNumber--;
				if(brickNumber == 0) {
					end(true);
					break;
				}
				vy = -vy;
			}
			pause(DELAY);
		}
	}
	
	/*
	 * Increases speed of ball by a constant when called
	 * 
	 */
	private void changeSpeed() {
		if(vx>0) {
			vx = vx + SPEED_MODIFIER;
		}
		else {
			vx = vx - SPEED_MODIFIER;
		}
		if(vy>0) {
			vy = vy + SPEED_MODIFIER;
		}
		else {
			vy = vy - SPEED_MODIFIER;
		}
	}

	/*
	 * Takes in the color of the brick removed and updates the score accordingly
	 * Uses constants to decide change in score
	 */
	private void updateScore(Color color) {
		if(color==Color.CYAN) {
			score += CYAN_BRICK_SCORE;
		}
		else if(color==Color.GREEN) {
			score += GREEN_BRICK_SCORE;
		}
		else if(color==Color.YELLOW) {
			score += YELLOW_BRICK_SCORE;
		}
		else if(color==Color.ORANGE) {
			score += ORANGE_BRICK_SCORE;
		}
		else {
			score += RED_BRICK_SCORE;
		}
		scoreDisplay.setText("Score: "+score);
		add(scoreDisplay);
	}

	/*
	 * Sets up score display
	 * displays at bottom of the screen
	 */
	private void setUpScore() {
		scoreDisplay= new GLabel("Score: "+ score);
		scoreDisplay.setFont(SCREEN_FONT);
		add(scoreDisplay, getWidth()-scoreDisplay.getWidth()*2, getHeight()-PADDLE_Y_OFFSET/2);
	}

	/*
	 * Displays you win or you lose depending on what boolean is given
	 * to be called after hitting bottom or removing all the bricks
	 */
	private void end(boolean win) {
		GLabel stateStatement;
		if(win) {
			stateStatement= new GLabel("You Win");
		}
		else {
			stateStatement= new GLabel("You Lose");

		}
		stateStatement.setFont(SCREEN_FONT);
		add(stateStatement, (getWidth()-stateStatement.getWidth())/2, getHeight()/2);
	}

	/*
	 * Sets up everything that lets user click to begin
	 */
	private void begin() {
		GLabel instructions= new GLabel("Click to Start");
		instructions.setFont(SCREEN_FONT);
		add(instructions, (getWidth()-instructions.getWidth())/2, getHeight()/4);
		waitForClick();
		remove(instructions);
	}

	/*
	 * returns anything the ball collides with, including the paddle
	 * checks at the four corners
	 * else returns null
	 */
	private GObject getCollidingObject() {
		GObject test=getElementAt(ball.getX(), ball.getY());
		if(test!=null) {
			return test;
		}
		test=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if(test!=null) {
			return test;
		}
		test=getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if(test!=null) {
			return test;
		}
		test=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		if(test!=null) {
			return test;
		}
		return null;
	}

	/*
	 *changes vx or vy if a wall is hit
	 *returns true if the bottom of the screen is hit in order to break the animation loop
	 *else returns false
	 */
	private boolean wallCheck() {
		if(ball.getX() < 0) {
			vx=-vx;
		}
		if(ball.getX() + 2 * BALL_RADIUS > getWidth()) {
			vx=-vx;
		}
		if(ball.getY() < 0) {
			vy=-vy;
		}
		if(ball.getY() + 2 * BALL_RADIUS > getHeight()) {
			vy=-vy;
			return true;
		}
		return false;
	}

	/*
	 * run once in run before the animation loop
	 * sets initial values of vx and vy
	 */
	private void setUpVelocities() {
		vy=-VELOCITY_Y;
		vx=rg.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rg.nextBoolean(0.5)) vx=-vx;
	}

	/*
	 * Draws the ball in the middle of the screen
	 */
	private void setUpBall() {
		ball=new GOval ((getWidth())/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * draws the paddle for the first time
	 */
	private void setUpPaddle() {
		paddle=new GRect(getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Draws all the bricks for the beginning of the game
	 * uses draw rows
	 */
	private void setUpBricks() {
		double brickX= getWidth()/2-(BRICK_WIDTH)*NBRICK_COLUMNS/2-BRICK_SEP*(NBRICK_COLUMNS-1)/2;
		drawRows(Color.RED, brickX, BRICK_Y_OFFSET);
		drawRows(Color.ORANGE, brickX, BRICK_Y_OFFSET+2*(BRICK_HEIGHT+BRICK_SEP));
		drawRows(Color.YELLOW, brickX, BRICK_Y_OFFSET+4*(BRICK_HEIGHT+BRICK_SEP));
		drawRows(Color.GREEN, brickX, BRICK_Y_OFFSET+6*(BRICK_HEIGHT+BRICK_SEP));
		drawRows(Color.CYAN, brickX, BRICK_Y_OFFSET+8*(BRICK_HEIGHT+BRICK_SEP));
	}

	/*
	 * Draws two rows of a given color at a given x and y
	 */
	private void drawRows(Color color, double startX, double startY) {
		for(int i=0; i<NBRICK_COLUMNS; i++) {
			GRect brick= new GRect (startX+i*(BRICK_WIDTH+BRICK_SEP), startY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick); 
		}
		for(int i=0; i<NBRICK_COLUMNS; i++) {
			GRect brick= new GRect (startX+i*(BRICK_WIDTH+BRICK_SEP), startY+BRICK_HEIGHT+BRICK_SEP, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
		}
	}

	/*
	 * lets the paddle follow the mouse
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleX;
		if(e.getX()<PADDLE_WIDTH/2) {
			paddleX=0;
		}
		else if(e.getX()>getWidth()-PADDLE_WIDTH/2) {
			paddleX=getWidth()-PADDLE_WIDTH;
		}
		else {
			paddleX=e.getX()-PADDLE_WIDTH/2;
		}
		paddle.setLocation(paddleX, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}
}
