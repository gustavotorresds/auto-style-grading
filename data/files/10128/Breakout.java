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
	public static final double VELOCITY_Y = 1;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 350.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Total number of bricks
	public static final int TOTAL_BRICKS = NBRICK_COLUMNS*NBRICK_ROWS;
	
	//Lives Label Flash Delay
	public static final int LIVES_FLASH_DELAY = 1500; 
	
	GRect paddle = null; 
	GOval ball = null; 
	private double vx, vy;
	GLabel livesLabel = null; 
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
	
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//builds the bricks and places the ball above the paddle
		buildGame();
		
		//Allows the program to "listen" for mouse movements or mouse clicks
		addMouseListeners();
		int numLives = NTURNS;
		int bricksLeft = TOTAL_BRICKS;
		while(numLives > 0 && bricksLeft > 0) {
			flashLivesLabel (numLives); 
			bricksLeft = playTurn(bricksLeft);
			numLives--;
			addBall();
		}
		if (numLives > 0 && bricksLeft == 0) {
			GLabel congrats = new GLabel ("Congrats you win!"); 
			add (congrats, (getWidth() - congrats.getWidth())/2, (getHeight() - congrats.getAscent())/2);
		}
		else if (numLives == 0 && bricksLeft > 0) {
			GLabel sad = new GLabel ("You lose :("); 
			add (sad, (getWidth() - sad.getWidth())/2, (getHeight() - sad.getAscent())/2);
		}
	}
	
	//Moves the ball with a preset x and y velocity
	//The ball will continue to move as long as bricks are remaining or the ball remains above the bottom of the console
	private int playTurn(int bricksLeft) {
		while ((bricksLeft > 0) && (ball.getY() + BALL_RADIUS*2) < getHeight()) {
			//Sets the number of bricks left to the integer returned by the checkBrick method
			//If this number reaches 0, the loop will not run
			bricksLeft = checkBrick(bricksLeft);
			ball.move(vx, vy);
			pause(DELAY);
			checkWalls();
		}
		remove (ball);
		vx = 0; 
		vy = 0; 
		return bricksLeft; 
	}
	
	//Checks if the collision of the ball is with a brick
	//In the event that the collision is a brick, the brick will be removed,
	//and the total number of bricks will be subtracted by 1
	private int checkBrick(int bricksLeft) {
		//Adds an audio file that produces a bounce noise whenever the ball strikes a surface.
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (getCollidingObject() != null) {
			GObject collider = getCollidingObject();
			if (collider != paddle) {
				remove(collider);
				bricksLeft--;
				vy = -vy; 
				bounceClip.play();
			} 
			else if (getElementAt (ball.getX(), ball.getY() + BALL_RADIUS*2) == paddle) {
				vy = -vy;
				bounceClip.play();
			}
			else if (getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2) == paddle) {
				vy = -vy; 
				bounceClip.play();
			}
			else if ((ball.getX() + BALL_RADIUS*2) > (paddle.getX()) || (ball.getX() + BALL_RADIUS*2 < paddle.getX() + PADDLE_WIDTH)) {
				vx = -vx; 
				bounceClip.play();
			}
		}
		return bricksLeft;
	}

	//Flashes the number of lives left at the beginning of each round
	private void flashLivesLabel(int livesLeft) {
		GLabel livesLabel = new GLabel ("Lives Remaining: " + livesLeft); 
		double labelWidth = livesLabel.getWidth(); 
		double labelHeight = livesLabel.getAscent(); 
		
		double labelX = (getWidth() - labelWidth)/2; 
		double labelY = (getHeight() - labelHeight)/2; 
		
		add (livesLabel, labelX, labelY);
		pause (LIVES_FLASH_DELAY);
		remove (livesLabel);
	}
	
	//Builds the bricks, paddle, and ball onto the screen
	private void buildGame() {
		buildBricks();
		paddle = buildPaddle(); 
		ball = buildBall(); 
		addPaddle();
		addBall(); 
	}

	//Sets the specific dimensions of the ball
	private GOval buildBall() {
		GOval ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2); 
		ball.setFilled(true);
		return ball; 
	}
	
	//Adds the ball to a location above the paddle
	private void addBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight() - PADDLE_Y_OFFSET*5 - BALL_RADIUS*2;
		add (ball, x, y);
	}
	
	//Called when the mouse is pressed; gives the ball an x and y velocity with which to move
	//In the event that the mouse is pressed while the game is running, the velocity of the ball will be changed
	public void mousePressed (MouseEvent e) {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}
	
	//Adds a paddle a certain distance from the bottom of the console
	private void addPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2; 
		double y = getHeight() - PADDLE_Y_OFFSET;
		add (paddle, x, y); 
		
	}

	//Builds a paddle with specific preset dimensions
	private GRect buildPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle; 
	}

	//Called when the mouse is moved
	//Will move the paddle according to the x position of the mouse cursor; 
	//Position does not change with y
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x > getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
		else if (x < PADDLE_WIDTH/2) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		}
		else {
			paddle.setLocation(x - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	//Builds a set of bricks
	//The set of bricks will have a predetermined number of bricks in the row and in the column
	//The bricks will alternate in color every two rows according to the following order:
	//Red, orange, yellow, green, cyan
	private void buildBricks() {
		double x = (getWidth() - (BRICK_WIDTH*NBRICK_COLUMNS) - (BRICK_SEP*NBRICK_COLUMNS))/2;
		double y = BRICK_Y_OFFSET;
		int colorNumber = 10; 
		for (int b = 0; b < NBRICK_ROWS; b++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				Color color = getColor(colorNumber); 
				buildRow (x, y, BRICK_WIDTH, BRICK_HEIGHT, color);
				x = x + BRICK_WIDTH + BRICK_SEP;
			}
			x = (getWidth() - (BRICK_WIDTH*NBRICK_COLUMNS) - (BRICK_SEP*NBRICK_COLUMNS))/2;
			y = y + BRICK_HEIGHT + BRICK_SEP;
			colorNumber --;
			if (colorNumber == 0) {
				colorNumber = 10; 
			}
		}
	}
	
	//Gets the color the row of bricks is supposed to be
	private Color getColor(double brickColor) {
		if (brickColor == 10 || brickColor == 9) {
			return Color.RED; 
		}
		else if (brickColor == 8 || brickColor == 7) {
			return Color.ORANGE; 
		}
		else if (brickColor == 6 || brickColor == 5) {
			return Color.YELLOW; 
		}
		else if (brickColor == 4 || brickColor == 3) {
			return Color.GREEN;
		}
		else {
			return Color.CYAN; 
		}
	}
	
	//Builds a brick of a predetermined color, x, and y coordinate
	private void buildRow(double x, double y, double brickWidth, double brickHeight, Color color) {
		GRect brick = new GRect (x, y, brickWidth, brickHeight);
		brick.setColor(color);
		brick.setFilled(true);
		add(brick); 
	}
	
	//Checks whether there is an object touching a "corner" of the ball
	//If there is an object, the object will be returned
	//If there is no object, null will be returned
	private GObject getCollidingObject () {
		if (getElementAt (ball.getX(), ball.getY()) != null) {
			return (getElementAt (ball.getX(), ball.getY()));
		}
		else if (getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY()) != null) {
			return (getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY())) ; 
		}
		else if (getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2) != null) {
			return ((getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2))); 
		}
		else if (getElementAt (ball.getX(), ball.getY() + BALL_RADIUS*2) != null) {
			return (getElementAt (ball.getX(), ball.getY() + BALL_RADIUS*2)) ; 
		}
		else {
			return null; 
		}
	}
	
	//Checks the walls of the console
	//If the ball is at one of the walls, it will change the velocity of the ball accordingly
	private void checkWalls() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (ball.getX() <= 0) {
			vx = -vx; 
			bounceClip.play();
		}
		else if (ball.getX() >= getWidth() - BALL_RADIUS*2) {
			vx = -vx;
			bounceClip.play();
		}
		else if (ball.getY() <= 0) {
			vy = -vy; 
			bounceClip.play();
		}
	}
}

