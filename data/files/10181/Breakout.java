/*
 * File: Breakout.java
 * -------------------
 * Name: Dante Gaudet
 * Section Leader: Avery Wanh
 * 
 * This file runs the game "breakout." The user has 3 attempts at destroying all the bricks
 * by bouncing a ball of a paddle (controlled by the mouse) which destroys a brick when 
 * colliding with it. An attempt ends if the paddle misses the ball. The user wins if all 
 * the bricks are destroyed and loses if they use all 3 attempts.
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
	
	// Diameter of the ball in pixels
	public static final double BALL_DIAMETER = BALL_RADIUS*2;

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

	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//set up phase
		setUpBricks();
		paddle = makePaddle();
		addMouseListeners();
		
		//play phase
		int turn = 1;
		int brickCounter = 100;
		while (turn <= 3 && brickCounter != 0)  {
			ball = makeBall();
			setXVelocity();
			//Y velocity magnitude is held constant
			vy = 3;
			boolean condition = true;
			//the animation loop
			while (condition) {
				ball.move(vx, vy);
				//ball bounces off side walls
				if (ball.getX() <= 0 || ball.getX() + BALL_DIAMETER >= getWidth()) {
					vx = -vx;
				} 
				//ball bounces off top wall
				if (ball.getY() <= 0) {
					vy = -vy;
				}
				GObject collider = getCollidingObject();
				if (collider == paddle) {
					vy = -vy;
				} else if (collider != null && collider != paddle) {
					vy = -vy;
					remove (collider);
					brickCounter = brickCounter - 1;
				}
				//when ball goes through bottom wall, user loses a turn
				//and the ball resets
				if (ball.getY() >= getHeight() + BALL_DIAMETER) {
					turn = turn + 1;
					ball = null;
					condition =  false;
				}
				//Winning Scenario (no bricks left). Game ends
				if (brickCounter == 0) {
					ball = null;
					condition = false;
				}
			
				pause(DELAY);
			}
		}
		GLabel endLabel = new GLabel("GAME OVER");
		double yCenterLabel = (getHeight() + endLabel.getAscent())/2;
		add(endLabel, (getWidth() - endLabel.getWidth())/2, yCenterLabel);
		if (brickCounter == 0) {
			GLabel winLabel = new GLabel("YOU WIN!");
			add(winLabel, (getWidth() - winLabel.getWidth())/2, yCenterLabel + 20);
		}
		if (turn > 3) {
			GLabel looseLabel = new GLabel("YOU LOOSE!");
			add(looseLabel,  (getWidth() - looseLabel.getWidth())/2, yCenterLabel + 20);
		}
	}
	
	private GObject getCollidingObject() {
		//These four "if" statements check all four corners of the square encapsulating
		//the ball for contact with another object. If contact, it returns the object.
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
		} else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER) != null) {
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
		} else {
			return null;
		}
		
	}

	//this event gets the paddle to move with the mouse
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX();
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		//this is so the paddle doesn't go off screen
		if (e.getX() >= getWidth() - PADDLE_WIDTH) {
			paddleX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(paddleX, paddleY);
	}
		

	private void setUpBricks() {
		//x coordinate for all first bricks in each row
		double xCoord = (getWidth() - 10*(BRICK_WIDTH) - 9*(BRICK_SEP))/2;
		double firstYCoord = BRICK_Y_OFFSET;
		//adds rows
		for (int i = 0; i < 10; i++) {
			double yCoord = firstYCoord + i*(BRICK_HEIGHT + BRICK_SEP);
			//adds columns
			for (int i2 = 0; i2 < 10; i2++) {
				GRect rect = new GRect(xCoord + i2*(BRICK_WIDTH + BRICK_SEP), yCoord, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				//These color code the row pairs by the given specifications
				if (i == 0 || i == 1) {
					rect.setColor(Color.RED);
				} else if (i == 2 || i == 3) {
					rect.setColor(Color.ORANGE);
				} else if (i == 4 || i == 5) {
					rect.setColor(Color.YELLOW);
				} else if (i == 6 || i == 7) {
					rect.setColor(Color.GREEN);
				} else {
					rect.setColor(Color.CYAN);
				}
				add(rect);
			}
		}
	}
	
	private GRect makePaddle() {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		double xCentered = (getWidth() - PADDLE_WIDTH)/2;
		GRect paddle = new GRect(xCentered, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}
	
	private GOval makeBall() {
		double xCentered = getWidth()/2 - BALL_RADIUS;
		double yCentered = getHeight()/2 - BALL_RADIUS;
		GOval ball = new GOval(BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		add(ball, xCentered, yCentered);
		return ball;
	}

	//this method randomizes the x velocity
	private double setXVelocity() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		return vx;
	}
	
}


