/*
 * File: Breakout.java
 * -------------------
 * Name: Nicole Orsak
 * Section Leader: Luciano Gonzalez
 * 
 * This file writes the arcade game Breakout. Rows of bricks appear at the top of the screen, 
 * and the user uses the mouse to control the horizontal direction of a paddle at the bottom 
 * of the screen. The ball begins in the center of the window, and upon the user clicking the 
 * screen, the ball drops. The goal of the game is to use the paddle to make the ball bounce
 * off, change directions, and eliminate bricks. The player wins when all bricks have been 
 * removed. The player has three turns to remove all of the bricks; if the player failures to
 * do so, he/she loses the game. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	// Define instance variables:
	
	// Instance variable: paddle
	private GRect paddle; 
	
	// Instance variable: ball
	private GOval ball; 
	
	// Initializes x-coordinate of velocity to 0
	private double vx = 0; 
	
	// Sets y-coordinate of velocity to given value
	private double vy = VELOCITY_Y; 
	
	// Creates a random generator to later determine x-value of velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Total number of bricks on screen
	private int numBricks = NBRICK_COLUMNS * NBRICK_ROWS; 
	
	// Dimensions of the canvas, in pixels
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
	public static final double BALL_RADIUS = 2*10;

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
	private int lives = 3;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Add bricks to screen at set number of lives to 3
		setup(); 
		addMouseListeners();
		addPaddle();
		addBall();
		
		// Determine a random x-value of velocity between the min and max
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		// Play the game for as many turns as given
		for (int i = 0; i < NTURNS; i++) {
			playGame();
		}
		
		// If bricks remain on screen with no lives remain, player loses
		if (numBricks != 0) {
			gameLost();
			
		// If no bricks remain on screen, player wins
		} else {
			gameWon();
		}
	}
	
	// When the player loses, clear screen and display "You lost!" message
	private void gameLost() {
		remove(ball);
		remove(paddle);
		lives = 0;
		GLabel label = new GLabel("You lost!");
		label.setFont("SansSerif-20");
		label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2);
		add(label);
	}
	
	// When the player wins, clear screen and display "You won!" message
	private void gameWon() {
		remove(ball);
		remove(paddle);
		GLabel label = new GLabel("You won!");
		label.setFont("SansSerif-20");
		label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2);
		add(label);
	}
	
	private void playGame() {
		waitForClick(); // Game begins when player clicks the screen
		while (true) {
			ball.move(vx, vy); // Update the ball's position
		
		// If the ball hits a side wall, change the x-direction
		if(hitRightWall(ball) || hitLeftWall(ball)) {
			vx = -vx;
		}
		
		// If the ball hits the top wall, change the y-direction
		if(hitTopWall(ball)) { 
			vy = -vy;
		}
		
		// Checks for bricks or the paddle
		GObject collider = checkForCollisions();
		
		// If ball hits an object, change y-direction of velocity
		if (collider != null) {
			vy = -vy;
			
			// Sticky paddle case: If ball hits paddle, change y-direction of velocity
			if (collider == paddle && vy >= 0) {
				vy = -vy;
			}
			
			//If ball hits a brick, remove the brick
			if (collider != paddle) {
				remove(collider);
				numBricks--;
			}
		}
		
		// pause
		pause(DELAY);	
		
		// If ball hits the bottom wall, player loses a life and restarts the game
		if(hitBottomWall(ball)) {
			lives--;
			restartGame();
			break;
		}
		
		// If no bricks remain, player wins
		if (numBricks == 0) {
			gameWon();
			lives = 0;
			break;
		}
	}
	}
	
	// After player loses a life, restart the game (keeping bricks in same position)
	private void restartGame() {
		remove(ball);
		remove(paddle);
		addMouseListeners();
		addPaddle();
		addBall();
	}
	
	// Place all bricks on screen
	private void setup() {
		lives = 3;
		for (int r=0; r < NBRICK_ROWS; r++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (getWidth() / (NBRICK_COLUMNS * BRICK_WIDTH * BRICK_SEP)) + (c * BRICK_WIDTH + BRICK_SEP) + (c * BRICK_SEP);
				double y = BRICK_Y_OFFSET + (r * BRICK_HEIGHT) + (r * BRICK_SEP); 
				drawBrick(x, y, getBrickColor(r));
			}
		}
	}
	
	// Create a color pattern for the bricks by row
	private Color getBrickColor(int r) {
		if (r%10 == 0 || r%10 == 1) 
			return Color.RED;
		else if (r%10 == 2 || r%10 == 3) 
			return Color.ORANGE;
		else if (r%10 == 4 || r%10 == 5) 
			return Color.YELLOW;
		else if (r%10 == 6 || r%10 == 7) 
			return Color.GREEN;
		else
			return Color.CYAN;
	}
	
	// Draw brick
	private void drawBrick(double startX, double startY, Color c) {
		GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(c);
		add(rect, startX, startY);
	}
	
	// Draw Paddle
	private void addPaddle() {
		GRect rect = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		add(rect, getWidth()/2 - (PADDLE_WIDTH/2), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		rect.setFilled(true);
		paddle = rect;
	}

	// Draw Ball
	private void addBall() {
		GOval oval = new GOval(BALL_RADIUS, BALL_RADIUS);
		add(oval, getWidth()/2 - 0.5*BALL_RADIUS, getHeight()/2);
		oval.setFilled(true);
		ball = oval;
	}
	
	// Moves paddle with mouse
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= 0 && e.getX() <= getWidth() - PADDLE_WIDTH && paddle != null)
		paddle.setLocation(e.getX(), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	
	// Hit Bottom Wall: Returns if the given ball should bounce off the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// Hit Bottom Wall: Returns if the given ball should bounce off the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	// Hit Bottom Wall: Returns if the given ball should bounce off the right wall
	private boolean hitRightWall(GOval ball) {
		return (ball.getX() >= getWidth() - ball.getWidth());
	}

	// Hit Bottom Wall: Returns if the given ball should bounce off the left wall
	private boolean hitLeftWall(GOval ball) {
		return (ball.getX() <= 0);
	}
	
	// Checks for collisions
	private GObject checkForCollisions() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS);
		} else if (getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS);
		} else {
			return null;
		}
	}
}

 	
