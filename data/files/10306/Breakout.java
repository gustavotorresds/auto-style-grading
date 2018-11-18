/*
 * File: Breakout.java
 * -------------------
 * Name: Dan Tram 
 * Section Leader: Maggie BSL Davis 
 * 
 * This programs plays 3 rounds of Breakout.
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
	public static final int NTURNS = 3;

	private GRect paddle = null; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int counterBricks = 0; 
	private int counterRounds = 0; 
	// private GOval ball = null; 

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//set up game with bricks and paddle
		setup();
		addMouseListeners(); 
		// play game
		// adds ball 
		GOval ball = makeBall();
		// initial velocity
		double vx = initialVx();
		double vy = VELOCITY_Y; 
		waitForClick(); 
		// determines if walls or paddle is hit
		while(true) {
			// update velocity
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy; 
			}
			// if bottom wall is hit, game ends
			if(hitBottomWall(ball)) {
				GLabel labelLoose = new GLabel ("L O O S E R !");
				double center = labelLoose.getWidth()/2;
				labelLoose.setLocation(getWidth()/2-center, getHeight()/2);
				add(labelLoose);
				break; 
			}
			// if ball hits object, object is named "collider"
			GObject collider = getCollidingObject(ball); 
			// if object is not paddle, remove object
			if (collider !=null && collider != paddle) {
				vy=-vy;
				remove(collider);
				counterBricks = counterBricks+1;
			} 
			if (collider == paddle) {
				vy=-vy;
			}
			// resets game if all bricks are gone 
			if (counterBricks == NBRICK_ROWS*NBRICK_COLUMNS) {
				counterBricks = 0;
				counterRounds = counterRounds+1;
				ball.setLocation(getWidth()/2-BALL_RADIUS, getWidth()/2-BALL_RADIUS);
				addBricks();
				// adds label
				GLabel labelNext = new GLabel ("Level "+counterRounds+ " Complete");
				double center2 = labelNext.getWidth()/2;
				labelNext.setLocation(getWidth()/2-center2, getHeight()/2);
				add(labelNext);
				waitForClick(); 
				remove(labelNext);
			}	
			// when NTURNS is met, game ends
			if (counterRounds == NTURNS) {
				remove(ball);
				// adds label 
				GLabel labelWin = new GLabel ("YOU WON!");
				double center3 = labelWin.getWidth()/2.0;
				labelWin.setLocation(getWidth()/2.0-center3, getHeight()/2.0);
				add(labelWin);
				break;
			}
			// code to check for bugs in console
			println(collider);
			double locationX = ball.getX();
			double locationY = ball.getY();
			println(locationX);
			println(locationY);
			// update visualization
			ball.move(vx, vy);
			//pause  
			pause(DELAY);
		}
	}

	private GObject getCollidingObject(GObject ball) {
		// checks for collision
		GObject corner1 = getElementAt(ball.getX(), ball.getY());
		GObject corner2 = getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY());
		GObject corner3 = getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY()+BALL_RADIUS*2.0);
		GObject corner4 = getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2.0);
		// sets up class variable "obj" to store collision point
		GObject obj = null; 
		// if object is at collision point, save as "obj" 
		if (corner1 !=null) {
			obj = corner1; 
		}
		if (corner2 !=null) {
			obj = corner2; 
		}
		if (corner3 !=null) {
			obj = corner3; 
		}
		if (corner4 !=null) {
			obj = corner4; 
		}
		return obj; 
	}

	private double initialVx() {
		// sets initial X velocity
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		return vx;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight()-BALL_RADIUS*2;
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-BALL_RADIUS*2;			
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <=0;	 
	}

	public GOval makeBall() {
		double ballX = getWidth()/2.0-BALL_RADIUS;
		double ballY = getHeight()/2.0-BALL_RADIUS; 
		// Question: ball seems big... 
		GOval r = new GOval (ballX,ballY, BALL_RADIUS*2.0,BALL_RADIUS*2.0);
		r.setFilled(true);
		r.setColor(Color.BLACK);
		add(r);
		return (r); 
	}

	private void setup() {
		addBricks(); 
		paddle = makePaddle();
	}

	private void addBricks() {
		//coordinates for top left brick
		double brickX = (getWidth()-(BRICK_WIDTH + BRICK_SEP)*NBRICK_COLUMNS)/2.0;
		double brickY = BRICK_Y_OFFSET;
		// add bricks
		for (int columns = 0; columns < NBRICK_COLUMNS; columns++) {
			for (int rows = 0; rows < NBRICK_ROWS; rows++) {
				GRect bricks = new GRect(brickX + (BRICK_WIDTH + BRICK_SEP)*rows, brickY + (BRICK_HEIGHT + BRICK_SEP)*columns, BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);
				// color rows
				if (columns%10 == 0 || columns%10 == 1) {
					bricks.setColor(Color.RED);
				}
				if (columns%10 == 2 || columns%10 == 3) {
					bricks.setColor(Color.ORANGE);
				}
				if (columns%10 == 4 || columns%10 == 5) {
					bricks.setColor(Color.YELLOW);
				}
				if (columns%10 == 6 || columns%10 == 7) {
					bricks.setColor(Color.GREEN);
				}
				if (columns%10 == 8 || columns%10 == 9) {
					bricks.setColor(Color.CYAN);
				}
				add(bricks);
			}
		}
	}

	private GRect makePaddle() {
		//coordinates of paddle
		double paddleXStart = getWidth()/2.0 - PADDLE_WIDTH/2.0; 
		double paddleYStart = getHeight() - PADDLE_Y_OFFSET; 
		GRect paddle = new GRect(paddleXStart, paddleYStart, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK); 
		add(paddle);
		return paddle; 
	}

	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		paddle.setCenterX(mouseX);
		// if paddle hits left wall
		if (e.getX()-PADDLE_WIDTH/2.0 < 0) { 
			// poor style in row below. How do I get Y location of paddle to transfer between scopes without instance variable			
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		}
		// if paddle hits right wall
		if (e.getX()+PADDLE_WIDTH/2 > getWidth()) {
			// poor style in row below. How do I get Y location of paddle to transfer between scopes without instance variable			
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
	}

}
