/*
 * File: Breakout.java
 * -------------------
 * Name: Léa Koob
 * Section Leader: Ruiqi Chen
 * 
 * This file will eventually implement the game of Breakout, with the extension
 * so that it gives the player instructions to click the screen to start playing
 * the game so that instead of just shooting the ball out, it waits for the player 
 * to indicate that they are ready by waiting for the screen to be clicked in order 
 * to put the ball in motion. Another extension is and with the extension the 
 * attached audio clip that makes a bounce noise when	 the ball hits either the 
 * paddle or one of the bricks.
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

	public static final double BALL_DIAMETER = 2.0 * BALL_RADIUS;

	// The ball's vertical velocity that vy gets set to
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Sets the y coordinate of the paddle
	public static final double PADDLE_Y = (CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);

	// Paddle and ball for game
	private GRect paddle;
	private GOval ball;

	// Ball vertical and horizontal velocity
	private double vx, vy;

	// Random generator to randomize the velocity of the ball
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Keeps track of how many bricks remain after a brick gets hit and 
	// eliminated
	private int bricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;


	/*
	 * Method: run
	 * 
	 * In this method, we set the window's title bar text, sets the canvas
	 * size to CANVAS_WIDTH and CANVAS_HEIGHT constants (Note: in later code, 
	 * it was important to remember to ALWAYS use getWidth() and getHeight() 
	 * to get the screen dimensions, not these constants! The audio bounce clip
	 * is also activated here (an extension for the ball to make a bounce noise
	 * whenever it collides with a brick or the paddle. Mouse listeners are 
	 * added here and allow the mouse strokes to move the paddle and for the 
	 * ball to start moving once the mouse is clear. And finally, the game graphics
	 * are set up in this method, and the game play method is also called. 
	 */

	public void run() {
		setTitle("CS 106A Breakout"); 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		addMouseListeners();
		setUpGraphics();
		playGame(bounceClip);
	}


	/*
	 * Method: mouseMoved
	 * 
	 * This implements the standard mouse listener method that responds to 
	 * when the mouse has been moved. This method allows the paddle to move
	 * horizontally following the mouse strokes, and makes sure that the
	 * entire paddle always remains on the screen. 
	 */

	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - (PADDLE_WIDTH) / 2.0;
		if (e.getX() > getWidth() - PADDLE_WIDTH / 2.0) {
			paddle.setLocation (getWidth() - PADDLE_WIDTH, PADDLE_Y);
		} else if (e.getX() < PADDLE_WIDTH / 2.0) {
			paddle.setLocation (0, PADDLE_Y);
		} else {
			paddle.setLocation (x, PADDLE_Y);
		}
	}


	/*
	 * Method: setUpGraphics
	 * 
	 * This method sets up the graphics of the game by adding the colored 
	 * bricks to the canvas and adding the paddle in the center of the
	 * screen.  
	 */

	private void setUpGraphics() {
		setBricks();
		addPaddle();
	}


	/*
	 * Method: setBricks
	 * 
	 * This method iterates thought a nested for loop to set each of the 
	 * bricks from the makeBricks method in their proper location. 
	 */

	private void setBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double xStart = (getWidth() / 2.0 - NBRICK_COLUMNS * BRICK_WIDTH / 2.0 - (NBRICK_COLUMNS - 1.0) * BRICK_SEP / 2.0 + j * BRICK_WIDTH + j * BRICK_SEP);
				double yStart = (BRICK_Y_OFFSET + i * BRICK_HEIGHT + i * BRICK_SEP);
				makeBricks(xStart, yStart, i, j);
			}
		}
	}


	/*
	 * Method: makeBricks
	 * 
	 * This method helps to create the initial set up of the bricks by creating
	 * the bricks for the setBricks method and making it so that the top two
	 * rows are red, the next two rows are orange, then yellow, then green, and
	 * then cyan. This also makes sure that the bricks are all of the same height
	 * and width (constants the are set as private instance variables). 
	 */

	private void makeBricks(double xStart, double yStart, int i, int j) {
		GRect brick = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
		add (brick);
		brick.setFilled(true);
		if (i == 0 || i == 1) {
			brick.setColor(Color.RED);
		} else if (i == 2 || i == 3) {
			brick.setColor(Color.ORANGE);
		} else if (i == 4 || i == 5) {
			brick.setColor(Color.YELLOW);
		} else if (i == 6 || i == 7) {
			brick.setColor(Color.GREEN);
		} else if (i == 8 || i == 9) {
			brick.setColor(Color.CYAN);	
		}
	}


	/*
	 * Method: addPaddle
	 * 
	 * This method creates the paddle to on board and sets it's height and width, 
	 * and x and y position on the bottom center of the board. It also fills the
	 * paddle in black. 
	 */

	private void addPaddle() {
		double xStart = ((getWidth() - PADDLE_WIDTH) / 2.0);
		paddle = new GRect(xStart,PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}


	/*
	 * Method: addBall
	 * 
	 * This method adds the ball to the board and sets it's height and 
	 * width, and initial x and y position at the center of the board. 
	 * It then also fills the ball in black and adds the ball to the 
	 * screen. Additionally, this method initializes the ball velocity. It is
	 * randomized by using the random generator instance variable and sets
	 * the initial ball to a velocity between the max and min velocity instance
	 * variables and makes it negative half of the time. And finally sets vy
	 * to whatever double gets placed in the VELOCITY_Y instance variable, in
	 * this case, 3.0.
	 */

	private void addBall() {
		double x = getWidth() / 2.0 - BALL_RADIUS;
		double y = getHeight() / 2.0 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;	
	}


	/*
	 * Method: moveBall
	 * 
	 * This method puts the ball in motion based on the vertical and horizontal
	 * velocities that get assigned in the vx and vy private instance 
	 * variables.
	 */

	private void moveBall() {
		ball.move(vx, vy);
	}


	/*
	 * Method: animateBall
	 * 
	 * This method adds the ball and the instruction label to the board while 
	 * bricks are present and when the mouse is clicked by the player. It sets 
	 * the ball in motion, periodically pauses and checks for objects that may 
	 * be colliding with the ball, and removes the ball from the screen once 
	 * all the bricks have been it, and if the ball gets lost by the player. 
	 */

	private void animateBall(AudioClip bounceClip, int i) {
		if (bricksPresent()) {
			addBall();
			if(i == 0) {
				instructionLabel();
			} else {
				waitForClick(); 
			}
			while (bricksPresent() && ballPresent()) {
				moveBall();	
				getCollidingObj(); 
				checkCollisions(bounceClip);
				pause(DELAY);
			}
			remove(ball);
		}
	}


	/*
	 * Method: bricksPresent
	 * 
	 * This method checks and returns whether there are still any bricks left on
	 * the screen so that the animateBall method can use this check. 
	 */

	private boolean bricksPresent() {
		return (bricksLeft > 0);
	}


	/*
	 * Method: ballPresent
	 * 
	 * This method checks and returns whether the ball is still present on
	 * the screen so that the animateBall method can use this check and we can
	 * know whether or not the game is still in play. 
	 */

	private boolean ballPresent() {
		return (ball.getY() < getHeight());
	}


	/*
	 *  Method: getCollidingObj
	 *  
	 * This method checks the four corners of the imaginary box that surrounds the ball
	 * to see if the ball is colliding into anything. It checks all of the corners one at
	 * a time starting with the top corners. First it checks the top left corner, then the 
	 * top right corner, then the bottom left corner, and finally the bottom right corner. 
	 * It will move on to check the next corner if the current corner that it is checking 
	 * is not colliding with anything. Finally, it returns what the colliding object is.
	 */

	private GObject getCollidingObj() {
		GObject collidingObj = getElementAt(ball.getX(), ball.getY());
		if (collidingObj == null) {
			collidingObj = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
			if (collidingObj == null) {
				collidingObj = getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER); 
				if (collidingObj == null) {
					collidingObj = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
				}
			}
		}
		return (collidingObj);
	}


	/*
	 * Method: checkCollisions
	 * 
	 * This method goes through and explains what the ball should do depending on which 
	 * of the various objects the ball collides with. If the ball collides with the paddle
	 * its vertical velocity is flipped so that the ball appears to be bouncing off the paddle. 
	 * If the ball collides with a brick, the brick that it just hit disappears from the screen. 
	 * And if the ball collides with one of the walls, depending on if it is a side wall, 
	 * or a top wall, the ball will flip either its x or y velocity in order to bounce off
	 * the wall. Finally, this method implements my sound extension where there is a bounce noise
	 * played after each of the collisions either with the bricks or with the paddle. This also 
	 * checks the velocities so that there are no 'sticky' walls or paddles.
	 */

	private void checkCollisions(AudioClip bounceClip) {
		GObject collidingObj = getCollidingObj();
		if (collidingObj == paddle && vy > 0) {
			vy = -vy; 
			bounceClip.play();
		} else if (ball.getY() <= 0) {
			vy = -vy;
		} else if (ball.getX() >= getWidth() - BALL_DIAMETER && vx > 0 || (ball.getX() <= 0 && vx < 0)) { 
			vx = -vx; 
		} else if (collidingObj != paddle && collidingObj != null) {
			vy = -vy; 
			bounceClip.play();
			remove(collidingObj);
			bricksLeft --;
			checkDeletedBricks();
		}
	}


	/*
	 * Method: checkDeletedBricks
	 * 
	 * This method checks for when there are no bricks left so that it can remove the ball
	 * from the board and let the player know that they have won the game by adding a label
	 * that says "You Win!" to the screen. 
	 */

	private void checkDeletedBricks() {
		if (bricksLeft == 0) {
			remove(ball); 
			GLabel label = new GLabel("YOU WIN!");
			add(label, (getWidth() - label.getWidth())  /2.0, (getHeight() - label.getHeight()) / 2.0);
		}
	}


	/*
	 * Method: playGame
	 * 
	 * This method actually plays the game for the given number of turns, animates the ball, 
	 * and prints the "Game Over" label if the ball gets lost before the alloted number of
	 * terms. 
	 */

	private void playGame(AudioClip bounceClip) {
		for (int i = 0; i < NTURNS; i++) {
			animateBall(bounceClip, i);
		}
		if (bricksPresent()) {
			GLabel label = new GLabel("GAME OVER");
			add(label, (getWidth() - label.getWidth()) / 2.0, (getHeight() - label.getHeight()) / 2.0);
		}
	}

	/*
	 * Method: instructionLabel
	 * 
	 * This method prints the instruction label so that the player knows to click the screen to
	 * get started. Once the screen is clicked, the label will go away.  
	 */

	private void instructionLabel() {
		GLabel instructions = new GLabel("WELCOME TO BREAKOUT, CLICK THE SCREEN TO GET STARTED!");
		add(instructions, (getWidth() - instructions.getWidth()) / 2.0, getHeight() / 2.0 - 2 * instructions.getHeight());
		waitForClick(); 
		remove(instructions);
	}
}
