/*
 * File: Breakout.java
 * -------------------
 * Name: Ari Brown
 * Section Leader: Niki Agrawal
 * 
 * This file will plays the game of Breakout. The game includes the bouncing of balls off of walls and from the paddle to bricks. The ball
 * bounces off of the paddle. Every time it hits a brick, it removes a brick. Every time it hits the bottom wall, it moves a life. The game ends when either the user
 * wins or loses all of their lives. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram implements MouseListener {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GRect paddle; 
	private GOval ball;
	private static int NUM_LIVES = 3;

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;
	private double vx,vy;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// number of bricks on canvas
	private static int numbrickstotal = NBRICK_COLUMNS *NBRICK_ROWS;

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

	public void run() {
		addMouseListeners();
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); // Set the canvas size. 
		makeGameboard();
		startLive(); //called every time you loose a life and at the beginning. 
	}
	
	private void startLive() {
		if (NUM_LIVES != 0) { // when there is still lives left, play game 
			createBall(); 
			waitForClick(); // waits for user to click in order for ball to start moving
			moveBall();
		} else {
			GLabel gameOver = new GLabel ("Game Over", getWidth()/3, getHeight()/3); // if the user runs out of lives the game is over and prints on the screen
			gameOver.setFont("Times New Roman-16");
			add(gameOver);
			return;
		}
	}
/*Makes the gameboard for the breakout game. Each row is a different color, and is found by using 
 * the remainder function so it can work for more than just 10x10 brick gameboards
 */
	private void makeGameboard() { 
		for (int n = 0; n < NBRICK_ROWS; n++) { 
			if (n%10 ==0 || n%10 ==1) {	// takes the remainder function so every 1st and 2nd row is red 
				makeOneRow(Color.RED, n);
			}
			else if (n%10 == 2 || n%10 == 3) {	
				makeOneRow(Color.ORANGE, n);
			}
			else if  (n%10 == 4 || n%10 == 5) {	
				makeOneRow(Color.YELLOW, n);
			}
			else if (n%10 == 6 || n%10 == 7) {	
				makeOneRow(Color.GREEN, n);
			}
			else if (n%10 == 8 || n%10 == 9) {	
				makeOneRow(Color.CYAN, n);
			}
		}
		addMouseListeners();
		addPaddle(); //adds paddle to board 
		
	}
/* sets the location of each row on a scoreboard by laying out a pre-set
 * number of bricks a brick separation away from each other. 
 */
	private void makeOneRow(Color color, double y) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) { // builds the amount of bricks in each row
			double x = (getWidth()/2 - ((NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP)-BRICK_SEP)/2))+(i*(BRICK_WIDTH+BRICK_SEP));  // assigns a value of x to each brick in the row 
			GRect brick = new GRect (x,BRICK_Y_OFFSET+(y*((BRICK_HEIGHT+BRICK_SEP))), BRICK_WIDTH, BRICK_HEIGHT); // y is multiplied by the row number 
			brick.setColor(color);
			brick.setFilled(true);
			add(brick);
		}
	}

	private void addPaddle() {  // sets the location of the paddle 
		paddle = new GRect (getWidth()/2 - PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}

	/*controls the movement of the paddle  -- as the mouse moves from left to right, the paddle follows it, but 
	 * it doesn't allow for it to go off of the screen
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2; // doesn't let the paddle move off of the right side of the screen
		double y = CANVAS_HEIGHT - PADDLE_Y_OFFSET; // sets the paddle at a specific, stagnant y location
		paddle.setLocation(x,y);
		if (e.getX() <= PADDLE_WIDTH/2) { 
			paddle.setLocation(0, y); // doesn't let the paddle move off of the left side of the screen
		}
		if (e.getX() >= getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y); //doesn't let the paddle move off of the right side of the screen
		}
	}
/*
 * creates ball 
 */
	private void createBall() {
		ball = new GOval (getWidth()/2, getHeight()/2, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFillColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}

	
	private void moveBall() { // makes the ball move/bounce in the game
		vx = rgen.nextDouble(1.0,3.0); // sets the x velocity
		if (rgen.nextBoolean(0.5)) { // x velocity of ball
			vx = -vx;
		}
		vy = 6; // y velocity of ball 
		while (true) {
			if(numbrickstotal == 0) {
				remove(ball);
				GLabel winGame = new GLabel ("congrats", getWidth()/3, getHeight()/3);
				winGame.setFont("Times New Roman-16");
				add(winGame);
				return;
			}
			ball.move(vx, vy);
			pause(DELAY);
			if(!checkForCollisions()) { 
				break;
			}
		}
		startLive();
	}

/* method checks for ball colliding with an object. When it hits a rectangle, it removes the rectangle, and changes direction.
 *  When it hits any wall except for the bottom one, it simply changes direction. When it hits the bottom wall, the user loses a life (if there 
 *  are still lives left) or loses the game if that was their last life. 
 */
	private boolean checkForCollisions() { // only runs if there are lives left 
		GObject collider = getCollidingObject();
		double x = ball.getX();
		double y = ball.getY();
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au"); // initializes sound of ball collisions 
		if (y<=0) { // ball hits top wall
			vy = -vy;
			return true;
		}
		else if (y>=getHeight()-2*BALL_RADIUS) { // ball hits bottom wall
			remove(ball); 
			NUM_LIVES--; // loses life 
			GLabel loseLife = new GLabel (NUM_LIVES + "LIVES LEFT", getWidth()/3, getHeight()/2); // presents a lose life label on the screen 
			loseLife.setFont("Times New Roman-16");
			add(loseLife);
			pause(1000);
			remove(loseLife);
			return false;
		}else if (x<=0 || x>=getWidth()-2*BALL_RADIUS) { // hits a side wall 
			vx = -vx;
			return true;
		}else if (collider == paddle) { // ball hits the paddle
			bounceClip.play(); //plays sounds when hits paddle
			vy = -Math.abs(vy); // prevents ball from "sticking" to paddle
			return true;
		}else if (collider != null) { //ball collides with brick 
			bounceClip.play(); //plays sound when ball collides with brick
			numbrickstotal--; //count down from amount of bricks in scoreboard
			remove(collider);
			vy = -vy;
			return true;
		}
		return true;
	}
	/* This method allows for the ball to hit a colliding object at any point on the ball
	 * and switch direction. As a result, at any coordinate of the ball, the ball can collide with an object and switch direction
	 * 
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return ((getElementAt(ball.getX(), ball.getY())));
		}
		else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != null) {
			return ((getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY())));
		}
		else if (getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS)) != null) {
			return ((getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS))));
		}
		else if (getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS)) != null) {
			return ((getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS))));
		}
		return(null);
		
	}
}




