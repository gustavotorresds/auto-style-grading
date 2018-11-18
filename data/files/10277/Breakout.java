/*
 * File: Breakout.java
 * -------------------
 * Name: Kendra Dunsmoor
 * Section Leader: Garrick Fernandez
 * Date: 2/7/18
 * --------------
 * This file plays the game of Breakout
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	//Constants:
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
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 14;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//private instance variables
	private GRect paddle = null;
	private GOval ball = null;
	private double vx; //velocity of ball
	private double vy;
	private int ballCount; //keeps track of lives
	private int brickCount; //keeps track of bricks on screen
	private int score; //bricks broken
	
	//allows randomly generated numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//allows sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setup();
		addMouseListeners();
		ballCount = 0;
		brickCount = NBRICK_ROWS*NBRICK_COLUMNS;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		GLabel scoreCounter = new GLabel ("Score: " + score);
		add(scoreCounter, 0, getHeight());
		while (ballCount < 3 && brickCount != 0) {
			if (ball==null) { //for turns 2 and 3
				ball = createBall();
			}
			waitForClick();
			while (ball!=null) {
				// update visualization
				scoreCounter.setLabel("Score: " + score);
				ball.move(vx, vy);
				// update velocity
				pause(DELAY);
				//delay
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
					bounceClip.play();
				}
				if(hitTopWall(ball)) {
					vy = -vy;
					bounceClip.play();
				}
				GObject collider = getCollidingObject(ball.getX(), ball.getY());
				//changes velocity of ball if hits object
				if (collider != null) {
					vy=-vy;
					if (sideCollision(collider, ball.getX(), ball.getY())) {
						vx = -vx;
						vy = -vy;
					}
					bounceClip.play();
					//removes bricks
					if (collider != paddle && collider != scoreCounter) {
						remove(collider);
						brickCount--;
						score++;
					}
				}
				if(hitBottomWall(ball)) {
					remove(ball);
					ball = null;
					ballCount++;
				}
				if (brickCount == 0) {
					ballCount = 0; //prevents losing message from appearing after ball hits bottom wall
					GLabel label = new GLabel ("Congratulations! You win! :)");
					add(label, (getWidth()/2)-(label.getWidth()/2), (getHeight()/2)-(label.getHeight()/2));
				}
			}
		}	
		if (ballCount == 3) {
			GLabel label = new GLabel ("You lose :( Score = " + score);
			add(label, (getWidth()/2)-(label.getWidth()/2), (getHeight()/2)-(label.getHeight()/2));
		}
	}
	/* setup()
	 * ----------
	 * sets up game by adding paddle, bricks, and ball
	 */
	private void setup() {
		createBricks();
		paddle = createPaddle();
		ball = createBall();
	}
	
	/* createBricks()
	 * ---------------
	 * creates rows of bricks on screen
	 */
	private void createBricks() {
		double xStart = ((getWidth() - (NBRICK_COLUMNS*(BRICK_SEP+BRICK_WIDTH)))/2);
		double yStart = (BRICK_Y_OFFSET);
		int row = 1;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			createRow(row, xStart, yStart);
			row = row +1;
			yStart = yStart + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	/* createRow()
	 * ------------
	 * This method creates a single row of bricks
	 * according to input from the createBricks() method
	 * of row #, starting x point, and starting y point
	 */
	private void createRow(int row, double xStart, double yStart) {
		for (int i=0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			if (row == 1 || row == 2) {
				brick.setColor(Color.red);
			}
			if (row == 3 || row == 4) {
				brick.setColor(Color.orange);
			}
			else if (row == 5 || row == 6) {
				brick.setColor(Color.yellow);
			}
			else if (row == 7 || row == 8) {
				brick.setColor(Color.green);
			}
			else if (row==9 || row == 10) {
				brick.setColor(Color.cyan);
			}
			add (brick, (xStart+(i*BRICK_WIDTH)+(i*BRICK_SEP)), yStart); 
			}
		}
	
	/* createPaddle()
	 * ------------
	 * creates paddle and places in center of bottom of the screen
	 */
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		double paddleX = (getWidth()/2) -(PADDLE_WIDTH/2);
		add (paddle, paddleX, (getHeight()- PADDLE_Y_OFFSET));
		return paddle;
	}
	
	/* mouseMoved()
	 * ----------------
	 * makes paddle follow mouse
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = (getHeight()- PADDLE_Y_OFFSET);
		if (e.getX() < (getWidth()-PADDLE_WIDTH) && e.getX() > 0) {
			paddle.setLocation(x,y);
		}
	}
	
	/* createBall()
	 * ---------------
	 * creates ball and places on screen
	 */
	public GOval createBall() {
		GOval ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		//Center locations for ball to be launched from
		double CENTER_SCREEN_X = getWidth()/2 -BALL_RADIUS;
		double CENTER_SCREEN_Y = getHeight()/2 -BALL_RADIUS;
		add(ball, CENTER_SCREEN_X, CENTER_SCREEN_Y);
		return ball;
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	/* hitRightWall()
	 * ---------------
	 * checks if ball hits right wall
	 * returns true or false
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-ball.getWidth();
	}
	
	/* hitLeftWall()
	 * ---------------
	 * checks if ball hits left wall
	 * returns true or false
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/* hitTopWall()
	 * ---------------
	 * checks if ball hits top wall
	 * returns true or false
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/* hitBottomWall()
	 * ---------------
	 * checks if ball hits bottom wall
	 * returns true or false
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight()-ball.getHeight();
	}
	
	/* getCollidingObject()
	 * --------------------
	 * checks for collision with ball
	 * if collision returns object at that point
	 */
	private GObject getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x,y);
		if (collider == null) {
			collider = getElementAt(x, y+2*BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt(x+2*BALL_RADIUS, y+2*BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt(x+2*BALL_RADIUS, y);
		}
		return collider;
	}
	
	/* sideCollision()
	 * -------------
	 * attempts to check if ball is colliding with the
	 * side of an object by using the midpoint of the ball
	 * as a reference point
	 * 
	 * returns true or false
	 */
	private boolean sideCollision(GObject collider, double x, double y) {
		return (collider == getElementAt(x, (y + BALL_RADIUS)) || collider == getElementAt(x+(2*BALL_RADIUS), (y + BALL_RADIUS)));
	}
}


