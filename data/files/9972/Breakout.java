/*
 * File: Breakout.java
 * -------------------
 * Name: Gracie Zaro
 * Section Leader: Kate Lattatuga
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
	public static final int NBRICK_COLUMNS = 4;

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
	public static final double BALL_DELAY = 1000.0 / 60.0;
		
	// pause time between paddle movement (ms)
	public static final double MOUSE_DELAY = 1000.0 / 120.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// Paddle that will be used to bounce ball 
	private GRect paddle;
	
	// ball that is used to actually play the game
	private GOval ball;
	
	// random number used for determining x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// velocity components
	private double vx;
	private double vy;
		
	// arbitrary object that ball could possibly hit 
	private GObject collidedWith;
	
	// number of lives a player has left at any given time
	private int lives = NTURNS;
	
	 // number of bricks a player has removed (via collisions w/the ball) at any given time
	private int bricksRemoved = 0;
	
	// color of bricks that will be printed in a given row
	private Color brickColor;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// sets up board
		setUp();
		
		//allows for game play
		playGame();
	}
	
	/*
	 * Method: playGame
	 * -------------------
	 * This method is where the actual game play takes place. While a player still has lives
	 * and has not won the game, the ball stays in motion. Each time the player lets the ball
	 * hit the bottom wall, a life is deducted and the ball is reset in center position. If 
	 * the player loses all of his lives, the game will end and a message will be displayed on
	 * the screen. If a player wins, the game will end and a congratulatory message will be
	 * displayed. 
	 */
	private void playGame() {
		while (hasLives() && !hasWon()) {
			ball = createBall();
			addBall();
			setVelocity();
			while(!loseALife(ball) && !hasWon()) {
				moveBall();
				pause(BALL_DELAY);
			}
			if(loseALife(ball)) {
				lives--;
			}
			remove(ball);	
		}
	}
	
	/*
	 * Method: setUp 
	 * --------------------------------
	 * This method sets up the board for play. That is, 
	 * is displays the wall of bricks, as well as the move-able
	 * paddle, on the window. 
	 */
	private void setUp() {
		addRainbowBricks();
		paddle = createPaddle();
		addPaddle();
		addMouseListeners();
	}
	
	/*
	 * Method: addRainbowBricks 
	 * --------------------------------
	 * This method creates the "wall" of rainbow bricks, based on the designated
	 * number of rows and columns. Every two rows the color of the bricks change in the 
	 * following sequence: red, orange, yellow, green, cyan.The wall should be 70 
	 * units down from the top of the window, and centered in terms of its x coordinates. 
	 */
	public void addRainbowBricks(){
		// keeps track of whether two rows of the same color have been printed (in succession), or just one
		int pairCounter = 0;
		for (int currentCow = 0; currentCow < NBRICK_ROWS; currentCow++) {
			if (pairCounter == 2) {
				pairCounter = 0;
			} 
			pickColor(currentCow, pairCounter);
			pairCounter++;			
			GRect brick;
			double rectangleY = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * currentCow);
			for (int singleRow = 0; singleRow < NBRICK_COLUMNS; singleRow++) {
				double firstXValue = (CANVAS_WIDTH - (((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS) - BRICK_SEP)) / 2;
				double rectangleX = firstXValue + (BRICK_WIDTH + BRICK_SEP) * singleRow;
				brick = new GRect(rectangleX, rectangleY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(brickColor);
				add(brick);			
			}
		}
	}
	
	/*
	 * Method: pickColor
	 * -------------------
	 * This method decides which color brick will be printed across the entire row. The method
	 * takes in the current row being printed, and an additional int which deciphers whether
	 * a row of the same color has been printed immediately above the current row. 
	 * 
	 */
	public void pickColor(int currentRow, int pairCounter) {
		if (currentRow == 0 && pairCounter == 0 || brickColor == Color.RED && pairCounter == 1 || brickColor == Color.CYAN && pairCounter == 0){
			brickColor = Color.RED;
		} else if (brickColor == Color.RED && pairCounter == 0 || brickColor == Color.ORANGE && pairCounter == 1) {
			brickColor = Color.ORANGE;
		} else if (brickColor == Color.ORANGE && pairCounter == 0 || brickColor == Color.YELLOW && pairCounter == 1) {
			brickColor = Color.YELLOW;
		} else if (brickColor == Color.YELLOW && pairCounter == 0 || brickColor == Color.GREEN && pairCounter == 1) {
			brickColor = Color.GREEN;
		} else if (brickColor == Color.GREEN && pairCounter == 0 || brickColor == Color.CYAN && pairCounter == 1) {
			brickColor = Color.CYAN;
		} 
	}
	
	/*
	 * Method: mouseMoved
	 * -------------------
	 * This method will move the paddle across the window
	 * any time the mouse moves in the window. 
	 */
	public void mouseMoved (MouseEvent e) {
		// without delay a nullPointerException is sometimes thrown
		pause(MOUSE_DELAY);
		double paddleY = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		double paddleX = e.getX() - PADDLE_WIDTH/2;
		if (paddleX >= CANVAS_WIDTH - PADDLE_WIDTH){
			paddleX = CANVAS_WIDTH - PADDLE_WIDTH;
		} else if (paddleX <= 0) {
			paddleX = 0;
		}
		paddle.setLocation(paddleX, paddleY);
	}
	
	/*
	 * Method: addPaddle
	 * -------------------
	 * This method adds the Paddle to the screen (70 pixels from the bottom
	 * and centered horizontally). 
	 */
	private void addPaddle() {
		double middleX = (CANVAS_WIDTH - PADDLE_WIDTH) / 2;
		double middleY = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		add(paddle, middleX, middleY);
	}

	/*
	 * Method: createPaddle
	 * ----------------
	 * This method creates a black paddle with designated width and height. 
	 */
	private GRect createPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.black);
		paddle.setFilled(true);
		return paddle;
	}  
	
	/*
	 * Method: createBall
	 * ----------------
	 * This method creates a black ball with designated radius. 
	 */
	private GOval createBall() {
		GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setColor(Color.black);
		ball.setFilled(true);
		return ball;
	}
	
	/*
	 * Method: addBall
	 * -------------------
	 * This method adds the ball to the center of the screen 
	 */
	private void addBall() {
		double middleX = CANVAS_WIDTH / 2 - BALL_RADIUS;
		//middle of white space between last row of bricks & top of paddle 
		double middleY = CANVAS_HEIGHT - ((BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * NBRICK_ROWS)) + PADDLE_Y_OFFSET) - BALL_RADIUS;
		add(ball, middleX, middleY);
	}
	
	/*
	 * Method: setVelocity 
	 * --------------------------------
	 * This method assigns values to the x and y movement of 
	 * the ball. The Y velocity is constant, with the x velocity is
	 * assigned to a random value between 1.0 and 3.0. 
	 */
	private void setVelocity() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) { 
			vx = -vx;
		}
	}
	
	/*
	 * Method: moveBall
	 * -------------------
	 * This method checks to see whether the ball has hit a boundary, brick, or paddle,
	 * negates the appropriate velocity component if needed, and moves the ball 
	 * the value of the respective components (in pixels). If the ball hits a brick,
	 * the brick will be removed. If the ball hits nothing, it will continue on its
	 * normally trajectory. If the ball hits a wall (that is not the bottom) it will 
	 * reverse locations. 
	 */
	private void moveBall(){
		setCollidingObject();
		if (hitTop(ball)) {
			vy = -vy;
		} else if (hitLeft(ball) || hitRight(ball)) {
			vx = -vx;
		} else if (collidedWith != null) {
			if (collidedWith != paddle && collidedWith.getWidth() == BRICK_WIDTH){
				remove(collidedWith);
				if (vy < 0) {
					vy = VELOCITY_Y;
				} else {
					vy = -VELOCITY_Y;
				}
				collidedWith = null;
				bricksRemoved++;
			} else if (collidedWith == paddle){
				vy = -VELOCITY_Y;
			}
			// resets collidedWith in order to prepare for next collision & properly measure bricksRemoved
			collidedWith = null;
		}
		ball.move(vx, vy);
	}
	
	/*
	 * Method: hitLeft
	 * -------------------
	 * This method checks to see whether the ball has hit the 
	 * left wall; returns true if yes, false if not. 
	 */
	private boolean hitLeft(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/*
	 * Method: hitRight
	 * -------------------
	 * This method checks to see whether the ball has hit the 
	 * right wall; returns true if yes, false if not. 
	 */
	private boolean hitRight(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}
	
	/*
	 * Method: loseALife
	 * -------------------
	 * This method checks to see whether the ball has hit the bottom wall; 
	 * returns true if yes, false if not. If the ball does it the bottom wall, 
	 * then we use this as the signal for the player using a life. 
	 */
	private boolean loseALife(GOval ball) {
		return ball.getY() >= (getHeight() - BALL_RADIUS * 2);
	}
	
	/*
	 * Method: hitTop
	 * -------------------
	 * This method checks to see whether the ball has hit the 
	 * top wall; returns true if yes, false if not. 
	 */
	private boolean hitTop(GOval ball) {
		return (ball.getY() <= 0);
	}
	
	/*
	 * Method: setCollidingObject
	 * -------------------
	 * This method checks to see whether the ball has collided with an object;
	 * if the ball has collided with an object, the method stores that object 
	 * as "collidedWith." If no object is hit, collidedWith remains null. 
	 */
	private void setCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collidedWith = getElementAt(ball.getX(), ball.getY());
		} if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			collidedWith = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			collidedWith = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);	
		} if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			collidedWith = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		} 
	}
	
	/*
	 * Method: didCollide
	 * -------------------
	 * This method checks to see whether the ball collided with
	 * an object; returns true  if yes, false if not. 
	 */
	public boolean didCollide() {
		return (collidedWith != null);
	}
	
	/*
	 * Method: hasLives
	 * -------------------
	 * This method checks to see whether player has any lives left; returns true 
	 * if yes, false if there are still bricks left on window. 
	 */
	private boolean hasLives() {
		return (lives > 0);
	}
	
	/*
	 * Method: hasWon
	 * -------------------
	 * This methods checks to see whether all the bricks have been hit; returns true 
	 * if yes, false if there are still bricks left on window. 
	 */
	private boolean hasWon() {
		return (bricksRemoved == (NBRICK_COLUMNS * NBRICK_ROWS));
	}
}
