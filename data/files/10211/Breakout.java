/*
 * File: Breakout.java
 * -------------------
 * Name: Jennifer Xilo
 * Section Leader: Esteban Rey
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	
	// If accompanied by an asterisk, these values were provided in starter code
	// *Dimensions of the canvas, in pixels
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// *Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// *Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// *Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// *Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// *Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// *Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// *Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// *Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// *Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// *The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// *The ball's minimum and maximum horizontal velocity; the bounds of the
	// *initial random velocity that you should choose (randomly +/-)
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// *Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// *Number of turns 
	public static final int NTURNS = 3;

	// The rest of the code before the run method represents necessary instance variables for the program
	private int countTurns = NTURNS;
	
	private int countBricks;
	
	private GRect paddle;
	
	private GOval useBall;
	
	private double diameter = BALL_RADIUS * 2;
	
	private double vx, vy;

	private GObject collidedWith;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// The setup section of the game
		gameSetUp();
		addMouseListeners();
		countBricks = NBRICK_COLUMNS * NBRICK_ROWS;

		// The play section of the game (includes setup section for ball to account for each turn and conditions
		// for moving on between turns or winning/losing at any time)
		while (countTurns > 0 && countBricks > 0) {
			ballSetUp();
			waitForClick();
			initializeVelocities();
			while (countBricks > 0) {
				moveUseBall();
				useBall.move (vx,vy);
				pause(DELAY);
				if (hitBottomWall(useBall)) {
					loseATurn();
					if (countTurns == 0) {
						loserLabel();
					}
					break;
				}
				collidedWith = getCollidingObject();
				if (collidedWith!= null) {
					paddleOrBrick();
				}
			}
		}
	}

	private void gameSetUp() {
		brickSetUp();
		paddleSetUp();
	}

	private void brickSetUp() {
		for (int col = NBRICK_COLUMNS; col > 0; col = col-1) {
			for (int row = NBRICK_ROWS; row > 0; row = row-1) {
				double nextxpos = col * BRICK_WIDTH - BRICK_WIDTH;
				double nextypos = row * BRICK_HEIGHT;

				double xpos = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS + 1) * BRICK_SEP))/2 + (col * BRICK_SEP) + (nextxpos);
				double ypos = (0 - BRICK_HEIGHT - BRICK_SEP + BRICK_Y_OFFSET  + (row * BRICK_SEP) + nextypos);

				GRect brick = new GRect (xpos, ypos, BRICK_WIDTH, BRICK_HEIGHT);

				switch(row) {
				case 1: 
					brick.setFilled(true);
					brick.setColor(Color.RED);
					add (brick);
					break;
				case 2:
					brick.setFilled(true);
					brick.setColor(Color.RED);
					add(brick);
					break;
				case 3:
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
					add(brick);
					break;
				case 4:
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
					add(brick);
					break;
				case 5:
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
					add(brick);
					break;
				case 6:
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
					add(brick);
					break;
				case 7:
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
					add(brick);
					break;
				case 8:
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
					add(brick);
					break;
				case 9:
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
					add(brick);
					break;
				case 10:
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
					add(brick);
					break;
				default:
					brick.setFilled(true);
					brick.setColor(Color.MAGENTA);
					add(brick);
					break;
				}
			}
		}
	}

	private void paddleSetUp() {
		double xPaddle = (getWidth() - PADDLE_WIDTH)/2;
		paddle = new GRect (xPaddle, (getHeight() - PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	// This is the MouseEvent that helps the paddle track the mouse
	public void mouseMoved (MouseEvent e) {
		double movingX = e.getX();
		double maxX = getWidth() - PADDLE_WIDTH;
		if (movingX < maxX) {
			paddle.setLocation (movingX, (getHeight() - PADDLE_Y_OFFSET));
		}
	}

	private void ballSetUp () {
		useBall = makeBall();
	}

	private GOval makeBall() {
		double ballX = (getWidth() - diameter)/2;
		double ballY = (getHeight() - diameter)/2;
		GOval ball = new GOval (diameter, diameter);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, ballX, ballY);
		return ball;
	}

	private void initializeVelocities() {
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;

	}
	
	// This method makes the ball bounce and stay within the bounds when it hits the top, left, or right wall.
	private void moveUseBall() {
		if (hitTopWall(useBall)) {
			vy = -vy;
		}
		if (hitLeftWall(useBall) || hitRightWall(useBall)) {
			vx = -vx;
		}
	}

	private boolean hitBottomWall (GOval useBall) {
		return useBall.getY() >= getHeight() - useBall.getHeight();
	}

	private boolean hitTopWall (GOval useBall) {
		return useBall.getY() <= 0;
	}

	private boolean hitLeftWall (GOval useBall) {
		return useBall.getX() <= 0;
	}

	private boolean hitRightWall (GOval useBall) {
		return useBall.getX() >= getWidth() - useBall.getWidth();
	}

	// This method comes into effect whenever the ball comes in contact with the bottom wall,
	// taking away a turn and removing that old turn's ball as the program makes another
	// ball for the next turn
	private void loseATurn() {
		countTurns--;
		remove(useBall);
	}

	private void loserLabel() {
		GLabel loser = new GLabel ("You lose. Why am I still seeing you?");
		loser.setLocation((getWidth() - loser.getWidth())/2, (getHeight() - loser.getHeight())/2);
		add(loser);
	}

	// This method checks the ball's four important points and, when called, returns the object that it has 
	// collided with or null if there is no collision
	private GObject getCollidingObject () {
		GObject thing;
		thing = getElementAt (useBall.getX(), useBall.getY());
		if (thing!= null) {
			return thing;
		}
		thing = getElementAt (useBall.getX() + diameter, useBall.getY ());
		if (thing!= null) {
			return thing;
		}
		thing = getElementAt (useBall.getX(), useBall.getY() + diameter);
		if (thing!= null) {
			return thing;
		}
		thing = getElementAt (useBall.getX() + diameter, useBall.getY() + diameter);
		if (thing!= null) {
			return thing;
		}
		else {return null;}
	}

	// This method comes under effect when the ball does collide with an object, determining if that object
	// is the paddle or (a) brick(s) and providing a course of action that depends on which it actually is
	private void paddleOrBrick() {
		if (collidedWith == paddle){
			vy = -vy;
		}

		else {
			remove(collidedWith);
			countBricks--;
			vy = -vy;
			if (countBricks == 0) {
				remove(useBall);
				GLabel winner = new GLabel ("Winner, winner, chicken dinner!!!");
				winner.setLocation((getWidth() - winner.getWidth())/2, (getHeight() - winner.getHeight())/2);
				add(winner);
			}
		}
	}
}
