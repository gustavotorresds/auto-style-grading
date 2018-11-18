/*
 * File: Breakout.java
 * -------------------
 * Name: Jayla Kilson
 * Section Leader: Peter Maldonado
 * 
 * This file implements the game of Breakout, which is a game
 * that makes colored rectangles disappear upon collision with
 * a round ball.
 
 * NOTE!!!!
 * I began the this project very late and therefore
 * had this misfortune of a plethora of bugs that 
 * with more time, I'm sure I could have figured out.
 * Throughout this process, I scrapped lines of code here 
 * and there. Towards the end, I reorganized my code 
 * because it was becoming very convoluted and was 
 * confusing me-- that's the source of all the copying
 * and pasting. Thanks! 
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
	public static int NTURNS = 3;

	//Brick Counter
	public static int NUMBER_OF_BRICKS = NBRICK_ROWS * NBRICK_COLUMNS;

	//The dimensions of the paddle 
	private GRect paddleTime = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	
	//Dimension of the ball
	private GOval ballBreaksWalls = new GOval(BALL_RADIUS, BALL_RADIUS);
	
	//Randomizes direction that ball can come from
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//velocity of the ball in the x and y direction
	private double vx, vy;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		createGame();
		playGame();

	}

	//This method combines the graphics that are responsible for how the game looks
	private void createGame() {
		allBricks();
		paddle();
		ball();
	}

	//Allows user to play Breakout upon clicking mouse
	private void playGame() {
		waitForClick();
		ballsSpeed();
		moveBall();	
	}

	//Designes the array of bricks that happen to vary in color depending on row
	private void allBricks() {
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect colorBricks = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				colorBricks.setLocation((BRICK_WIDTH + BRICK_SEP) * j + (BRICK_SEP/2), (BRICK_HEIGHT + BRICK_SEP) * i + BRICK_Y_OFFSET + (BRICK_SEP/2));
				add(colorBricks); 
				colorBricks.setFilled(true);

				if(i == 0 || i == 1) {
					colorBricks.setColor(Color.RED);
				} else if(i == 2 || i == 3 ) {
					colorBricks.setColor(Color.ORANGE);
				} else if(i == 4 || i == 5) {
					colorBricks.setColor(Color.YELLOW);
				} else if(i == 6 || i == 7) {
					colorBricks.setColor(Color.GREEN);
				} else {
					colorBricks.setColor(Color.CYAN);
				}

			}
		}
	}

	//Creates a paddle that will follow the position of the mouse and hit the ball
	private void paddle() {
		paddleTime.setFilled(true);
		paddleTime.setLocation((getWidth() - PADDLE_WIDTH)/2, (getHeight() - PADDLE_HEIGHT)/2 );
		add(paddleTime);
	}

	//Creates the ball that will be responsible for destroying bricks
	private void ball() {
		ballBreaksWalls.setFilled(true);
		add(ballBreaksWalls);
	}

	//Synchronizes the moving of the paddle to that of the mouse
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		
		// returns paddle to a distances that is not outside of the perimeter
		double xreturn = (getWidth() - PADDLE_WIDTH) - 10; 
		
		//prohibits paddle from leaving screen interior
		double xmax = getWidth() - PADDLE_WIDTH; 
		paddleTime.setLocation(x, y);
		if(x > xmax) {
			paddleTime.setLocation(xreturn,y);
		}
	}
	
//This method gives the ability of motion to the ball
	private void moveBall() {
		ballsSpeed();
		while(true) {
			byeBricks();
			ballBreaksWalls.move(vx,vy);

			//if the ball goes into the perimeter of the screen, it will go in the opposite direction
			if(ballBreaksWalls.getX() < 0 || ballBreaksWalls.getX() > getWidth() - 2 * BALL_RADIUS) {
				vx = -vx;
			}
			
			if(ballBreaksWalls.getY() < 0) {
				vy = -vy;
			}

			//When the ball goes to the bottom of the screen, the player loses a turn and the game restarts
			//After using each of the three turns, message of "You Lose" appears
			if(ballBreaksWalls.getY() > getHeight() - 2 * BALL_RADIUS) {				
				NTURNS--;
				if(NTURNS != 0) {
					createGame();
					playGame();
				} else {
					println("You Lose");
				}

			}
			
			//random variable that gives random direction to the ball's vx variable
			double vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;

			pause(DELAY);
		}
	}

	//This method controls that speed that the ball travels across the screen and when colliding 
	//with abjoects
	private void ballsSpeed() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vy;
		}
	}

	//When the ball collides with the paddle, it bounces back in the opposite direction.
	//When it collides with a break, the brick is removed
	private void byeBricks() {
		GObject collider = getCollidingObjects();
		if(collider == paddleTime) {
			vy = -vy;
		} else if(collider != null) {
			remove(collider);
			NUMBER_OF_BRICKS--; //lowers number of bricks
			vy = -vy;
		}
	}
	
	//This method allows the ball to be able to detect when it is touching another object. 
	//Depending on what the object is, the ball responds accordinly.
	private GObject getCollidingObjects() {
		GObject collider = null;

		double colliderx = ballBreaksWalls.getX();
		double collidery = ballBreaksWalls.getY();

		if(getElementAt(colliderx, collidery) != null) {
			collider = getElementAt(colliderx, collidery);

		}else if(getElementAt(colliderx + 2 * BALL_RADIUS, collidery) != null) {
			collider = getElementAt(colliderx + 2 * BALL_RADIUS, collidery);

		}else if(getElementAt(colliderx, collidery + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(colliderx, collidery + 2 * BALL_RADIUS);

		}else if(getElementAt(colliderx + 2 * BALL_RADIUS, collidery + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(colliderx + 2 * BALL_RADIUS, collidery + 2 * BALL_RADIUS);
		}
		return collider; 
	}
}

