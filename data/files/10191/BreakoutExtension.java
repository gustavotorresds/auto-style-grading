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

public class BreakoutExtension extends GraphicsProgram {
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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Velocity in the x direction
	private double vx;

	// Velocity in the y direction
	private double vy = VELOCITY_Y;

	// This creates a random variable that assist in creating vx
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//This is the number of bricks left in the game
	private static int totalNumberofBricks = NBRICK_ROWS *NBRICK_COLUMNS;

	// These set up the paddle and ball as global variables
	GRect paddle;
	GOval ball;

	//This starts the audio for when the ball his a collider
	AudioClip bounceClip = MediaTools.loadAudioClip ("bounce.au");

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpBreakout();
		playBreakout();
	}

	/*
	 * playBreakout sets up a for loop that runs makeBall for the number of turns the game allows
	 * or until the player eliminates all of the bricks. If all bricks are gone, the player wins, otherwise
	 * after three lives are lost, the player loses
	 */
	private void playBreakout () {
		for (int i=0; i < NTURNS; i++) {
			if (makeBall() == true) {
				GLabel winner = new GLabel ("WINNER",getWidth()/2, getHeight()/2);
				winner.setColor(Color.YELLOW);
				add(winner);
				remove (ball);
				return;
			}
		}
		GLabel gameover = new GLabel ("GAME OVER", getWidth()/2 - BRICK_WIDTH/2, getHeight()/2 );
		gameover.setColor(Color.RED);
		add(gameover);
		return;
	}

	/*
	 * makeBall is creates the ball in the middle of the screen and then determines how it moves
	 * in reaction to different events. The vx is determined randomly within a set of values. This
	 * method also sets the limits on where the ball is allowed to travel, making it bounce off the top
	 * and side walls, and getting rid of the ball and starting the method over if the ball exits the bottom
	 * of the screen. This section of the code also determines what occurs when  the ball runs into an object,
	 * called the collider. If it is the paddle, the ball reverses. If it is a brick, the brick is eliminated
	 * and a sound is played. 
	 */
	private boolean makeBall () {
		double x = (getWidth()/ 2)- BALL_RADIUS;
		double y = (getHeight() / 2) -BALL_RADIUS;
		ball = new GOval (x, y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
		vx = rgen.nextDouble(3.0,5.0);
		if (rgen.nextBoolean (0.5)) vx = -vx;
		while (true) {
			if (ball.getX() < 0 || ball.getX() > getWidth() - (2 * BALL_RADIUS)) {
				vx = -vx;
			}
			if (ball.getY() < 0 ) {
				vy = -vy;
			}
			if (ball.getY() > getHeight() - (2 * BALL_RADIUS)) {
				remove (ball);
				return (false);
			}
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy= -vy;
				bounceClip.play();
			} else  if(collider != null){
				remove(collider);
				totalNumberofBricks--;
				if (totalNumberofBricks == 0) {
					return (true);
				}
				vy=-vy;
				bounceClip.play();
			}
			ball.move(vx, vy);
			pause (DELAY);
		}
	}

	/*
	 * getCollidingObject checks all of the "corners" of the ball to determine if there is an object
	 * the ball is coming into contact with. If nothing is there, the ball continues to move, otherwise
	 * the ball alerts getCollidingObject
	 */
	private GObject getCollidingObject () {
		GObject maybeAnObject = getElementAt (ball.getX(), ball.getY());
		if (maybeAnObject != null) {
			return (maybeAnObject);
		}
		maybeAnObject = getElementAt (ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (maybeAnObject != null) {
			return (maybeAnObject);
		}
		maybeAnObject = getElementAt (ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (maybeAnObject != null) {
			return (maybeAnObject);
		}
		maybeAnObject = getElementAt (ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (maybeAnObject != null) {
			return (maybeAnObject);
		}
		return (null);
	}

	/*
	 * setUpBreakout contains the two parts of the setup required for play
	 */
	private void setUpBreakout () {
		makeBricks();
		makePaddle();
	}

	/*
	 * makeBricks starts with the first x coordinate of the first column. It also sets up a starting color, red.
	 * it then sets up a for loop that continues for the number of rows and columns in the program, setting a color value 
	 * for each row so that makeRect can use this value to make the rectangles.
	 */
	private void makeBricks () {
		double starterx = ((CANVAS_WIDTH/2) - NBRICK_COLUMNS*(BRICK_WIDTH + BRICK_SEP)/2) ;
		Color color = Color .RED;
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				if (row == 2 || row == 3) {
					color = Color.ORANGE;
				} 
				if (row == 4 || row == 5) {
					color = Color.YELLOW;
				}
				if (row == 6 || row == 7 ) {
					color = Color.GREEN;
				}
				if (row == 8 || row == 9) {
					color = Color.CYAN;
				} 
				makeRect (starterx + ((BRICK_WIDTH + BRICK_SEP) * col),row * (BRICK_HEIGHT+BRICK_SEP) + BRICK_Y_OFFSET, color);
			}
		}

	}

	/*
	 * makeRect uses parameters for x and y location and color. All of these are supplied
	 * by values from the makeBricks method. With this info, makeRect creates a rectangle, which is then
	 * repeated due to the for loop in makeBricks
	 */
	public void makeRect (double x, double y, Color color) {
		GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setColor(color);
		rect.setFilled(true);
		add(rect);
	}

	/*
	 * makePaddle creates the paddle in the game as a rectangle
	 */
	private void makePaddle () {
		double x = 0;
		double y = getHeight()- PADDLE_Y_OFFSET;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);	
	}

	/*
	 * mouseMoved is what controls the paddle in the game. As the player moves his mouse around
	 * the screen, the mouse is put on the new x coordinate of the mouse. The Y coordinate does
	 * not change since the y value for the paddle is fixed.
	 */
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		if (x < getWidth() - PADDLE_WIDTH && x > 0) {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		}
	}
}






