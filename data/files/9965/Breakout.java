/*
 * File: Breakout.java
 * -------------------
 * Name: Kailash Raman
 * Section Leader: Chase Davis
 * 
 * This file implements the game of Breakout. The game sets up the stack of bricks
 * and paddle.  It then asks user to click, and starts a bouncing ball animation.
 * The player moves the paddle by moving the mouse. The player must bounce the ball
 * off the paddle to break bricks and score points.  If player breaks all the bricks
 * on the canvas, player wins.  If the ball falls off the bottom of the canvas, the 
 * player loses (3 attempts are given).
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
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Audio Clips
	public AudioClip darude = MediaTools.loadAudioClip("darude.mp3");

	public void run() {
		darude.play(); // play background music
		setTitle("CS 106A Breakout"); //set title
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBricks();
		setPaddle();
		setLabels();
		addMouseListeners();
		turnsRemaining = NTURNS; // Initializes number of attempts remaining
		waitForClick(); // Waits for user to click before starting game
		remove(startMessage);
		ballAnimation(); // Starts ball animation loop
	}
	
	/* Adds a grid of bricks to the canvas of dimensions NBRICK_ROWS x NBRICK_COLUMNS). Ensures separation
	 * of bricks by BRICK_SEP.  Sets color of each two rows to a different color.
	 */
	private void setBricks() {
		double brickXOffset = (getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS-1)*BRICK_SEP))/2;
		for(int i=0;i<NBRICK_ROWS;i++) {
			for(int j=0;j<NBRICK_COLUMNS;j++) {
				double xBrick = brickXOffset + (BRICK_WIDTH + BRICK_SEP)*j;
				double yBrick = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP)*i;
				GRect brick = new GRect(xBrick,yBrick,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				if(i<2) { // Sets color of each two rows to a new color
					brick.setColor(Color.RED);
				} else if(i<4) {
					brick.setColor(Color.ORANGE);
				} else if(i<6) {
					brick.setColor(Color.YELLOW);
				} else if(i<8) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
				add(brick);					
			}
		}
	
	}
	
	/* Sets up the paddle, with a specified y offset from bottom of canvas,
	 * and centered in the x direction
	 */
	private void setPaddle() {
		double paddleX = (getWidth() - PADDLE_WIDTH)/2;
		double paddleY = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		paddle = new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// Sets up score board and start message
	private void setLabels() {
		score = 0;
		scoreBoard = new GLabel("YOUR SCORE: " + score);
		add(scoreBoard,10,getHeight() - 10);
		startMessage = new GLabel("CLICK TO START");
		add(startMessage,(getWidth()-startMessage.getWidth())/2,(getHeight()-startMessage.getHeight())/2);
	}
	
	/* Mouse event that updates to x location of paddle to the x location
	 * of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double maxX = getWidth()-PADDLE_WIDTH;
		// if statement ensures that paddle stays within the bounds of canvas
		if(mouseX >= 0 && mouseX <= maxX) {
			paddle.setLocation(mouseX,paddle.getY());
		}
	}
	
	//Creates an animation loop which has the ball bounce off the three walls, as well as the brick and paddle
	private void ballAnimation() {
		double diameter = 2*BALL_RADIUS;
		ball = new GOval(getWidth()/2-BALL_RADIUS,getHeight()/2-BALL_RADIUS,diameter,diameter);
		ball.setFilled(true);
		add(ball); //Add ball to canvas
		bricksRemaining = NBRICK_ROWS*NBRICK_COLUMNS;
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx; //generates random x velocity
		vy = VELOCITY_Y;
		while(true) {
			ball.move(vx, vy);
			// Reverse sign of vx if ball hits one of the side walls
			if(ball.getX()<=0) vx = Math.abs(vx); // Ball bounces to right off the left wall. Abs ensure no "sticky wall"
			if(ball.getX()+diameter>= getWidth()) vx = -Math.abs(vx); // Ball bounces to left off the right wall
			if(ball.getY()<=0) vy = -vy; //Ball bounces off top wall
			GObject collidingObject = getCollidingObject();
			if(collidingObject != null) {
				// Change ball direction when ball hits paddle
				if(collidingObject == paddle) {
					vy = -Math.abs(vy); // Ball moves up after hitting paddle.  - Absolute value prevents "stick paddle"
					// If ball hits left side of paddle, it bounces to the left
					if(ball.getX() < paddle.getX()+PADDLE_WIDTH/2) vx = -Math.abs(vx);
					// If ball hits right side of paddle, it bounces to the right
					else if(ball.getX() > paddle.getX()+PADDLE_WIDTH/2) vx = Math.abs(vx);
				}
				// Switch vy and remove brick if ball hits brick
				else if(collidingObject != scoreBoard) {
					remove(collidingObject); 
					bricksRemaining--;
					vy = -vy;
					score += getScore(ball.getY()); //update score
					scoreBoard.setLabel("YOUR SCORE: " + score);
				}
			}
			// If ball goes off bottom of canvas, remove ball and decrease number of turns
			if(ball.getY()>getHeight()) {
				remove(ball);
				turnsRemaining--;
				// If there are turns left, restart animation loop
				if(turnsRemaining>0) { 
					GLabel turns = new GLabel("WATCH OUT, YOU HAVE " + turnsRemaining + " TURNS LEFT!");
					add(turns,(getWidth()-turns.getWidth())/2,(getHeight()-turns.getHeight())/2);
					pause(2000);
					remove(turns);
					ballAnimation();
				// If there are no turns left, player loses and game ends
				} else {
					GLabel lose = new GLabel("You lose! Better luck next time...");
					add(lose,(getWidth()-lose.getWidth())/2,(getHeight()-lose.getHeight())/2);
					break;
				}
			}
			// If no bricks remaining, player wins and game ends
			if(bricksRemaining == 0) {
				GLabel win = new GLabel("YOU WIN!");
				add(win,(getWidth()-win.getWidth())/2,(getHeight()-win.getHeight())/2);
				break;
			}
			pause(DELAY);
		}
	}
	
	/* Checks for elements at the four corners surrounding the ball, and returns
	 * the element if there is one present at either of the four corners.
	 */
	private GObject getCollidingObject() {
		GObject corner1 = getElementAt(ball.getX(),ball.getY());
		GObject corner2 = getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY());
		GObject corner3 = getElementAt(ball.getX(),ball.getY() + 2*BALL_RADIUS);
		GObject corner4 = getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY() + 2*BALL_RADIUS);
		if(corner1 != null) return corner1;
		else if(corner2 != null) return corner2;
		else if(corner3 != null) return corner3;
		else if(corner4 != null) return corner4;
		else return null;
	}
	
	/* Calculates and returns the number of points earned for breaking a brick 
	 * of a certain color (Red = 5, orange = 4, yellow = 3, green = 2, cyan = 1).
	 * Takes in the double y, which represents y position of ball at time of
	 * collision with brick.
	 */
	private int getScore(double y) {
		if(y < BRICK_Y_OFFSET + 2*(BRICK_HEIGHT + BRICK_SEP)) return 5;
		else if(y < BRICK_Y_OFFSET + 4*(BRICK_HEIGHT + BRICK_SEP)) return 4;
		else if(y < BRICK_Y_OFFSET + 6*(BRICK_HEIGHT + BRICK_SEP)) return 3;
		else if(y < BRICK_Y_OFFSET + 8*(BRICK_HEIGHT + BRICK_SEP)) return 2;
		else return 1;
	}
	
	
	private GRect paddle; //Creates paddle object as an instance variable
	private GOval ball; //Creates ball as an instance variable
	private double vx , vy; //Creates x and y velocity as instance variables
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int bricksRemaining; //Instance variable to track number of unbroken bricks
	private int turnsRemaining; //Instance variable to track number of attempts remaining
	private int score; //Instance variable to track player's score
	private GLabel scoreBoard; //Creates a GLabel for the score board
	private GLabel startMessage; //Creates a GLabel for the starting message

	
	
}
