/*
 * File: Breakout.java
 * -------------------
 * Name: Miso Kim
 * Section Leader: Rachel Gardner
 * 
 * This program implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setup();
		playGame();
	}

	private void setup() {
		makeBricks();
		addPaddle();
	}

	private void playGame() {
		moveBall();
	}

	// Checks the number of row that it's currently adding and chooses appropriate 
	// color for the row of bricks. For every set of 2 rows, changes brick color.
	private void makeBricks() {
		double x = (getWidth() / 2) - (BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS/2);
		double y = BRICK_Y_OFFSET; 
		for(int r=0; r < NBRICK_ROWS; r++) {
			for(int k=0; k < NBRICK_COLUMNS; k++) {
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				if (r==0 || r==1) {
					brick.setColor(Color.RED);
				}
				if (r==2 || r==3 ) { 
					brick.setColor(Color.ORANGE);
				}
				if (r==4 || r==5) {
					brick.setColor(Color.YELLOW);
				}
				if (r==6 || r==7) {
					brick.setColor(Color.GREEN);
				}
				if (r==8 || r==9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add(brick);
				x = x + BRICK_WIDTH + BRICK_SEP;
			}
			x = (getWidth() / 2) - (BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS/2);
			y = y + BRICK_HEIGHT + BRICK_SEP;
		}
	}

	// Adds a paddle to the screen.
	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor((Color.BLACK));
		add(paddle);
	}

	// Sets the paddle location as the mouse location, allowing it
	// to move only within the screen and in a fixed y location.
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (e.getX() > PADDLE_WIDTH/2 && e.getX() < getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(x,y);
		}
	}

	// Animates the ball so that it bounces off the side walls and top wall.
	private void moveBall() {
		int turnCounter = NTURNS;
		int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
		addBall();
		waitForClick();
		vy = VELOCITY_Y;
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) { 
			vx = -vx;
		}
		while(true) {
			ball.move(vx, vy);
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball))  {
				vy = -vy;
			}
			// If ball hits the bottom wall, subtracts one from a set number of turns
			// until there is no turn left and the game ends as it displays "You lost."
			if(hitBottomWall(ball)) {
				remove(ball);
				turnCounter = turnCounter - 1;
				if (turnCounter > 0) {
					addBall();
				} else {
					GLabel label = new GLabel ("You lost.");
					add(label, getWidth()/2-label.getWidth()/2, getHeight()/2);
					break;
				}
			}
			// Makes the brick that ball hits disappear until no brick is left,
			// which then ends the game and displays "You win!". 
			// If ball hits paddle, it bounces off in a different y direction.
			pause(DELAY);
			GObject collider = getCollidingObject();
			if (collider == paddle && vy > 0) {
				vy = -vy;
			} else {
				if (collider != null && collider != paddle) {
					remove(collider);
					vy=-vy;
					brickCounter = brickCounter - 1;
					if (brickCounter == 0) {
						GLabel label = new GLabel ("You win!");
						add(label, getWidth()/2-label.getWidth()/2, getHeight()/2);
						break;
					}
				}
			}
		}
	}

	//Checks and signals when the ball touches any walls.
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// Checks if the ball is hitting any object with any of its corners.
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS));
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS));
		if (collider != null) {
			return collider;
		}
		return null;
	}

	// Adds a ball to the screen.
	private void addBall() {
		double size = BALL_RADIUS * 2;
		ball = new GOval(size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add (ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}

}