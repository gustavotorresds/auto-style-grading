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
	
	private RandomGenerator rgen = RandomGenerator.getInstance();

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
	
	// private instance variables
	private GRect paddle = null;
	private GOval ball = null;
	private GLabel centerText = new GLabel("");
	private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		double cx = getWidth() / 2;
		double cy = getHeight() / 2;
		int remainingLives = NTURNS;
		
		addMouseListeners();
		
		gameScreen(cx, cy);
		playGame(cx, cy);
		while(remainingLives > 0 && brickCounter > 0) {
			remainingLives--;
			add(centerText(cx, cy, 5 + remainingLives));
			pause(500);
			remove(centerText);
			playGame(cx, cy);
		}
		if (remainingLives == 0) {
			removeAll();
			add(centerText(cx, cy, 2));
		}
		if (brickCounter == 0 ) {
			add(centerText(cx, cy, 3));
		}
	}
	
	private void gameScreen(double cx, double cy) {
		createBricks(cx);
		paddle = createPaddle();
		addPaddleToCenter(cx);
	}
	
	private void playGame(double cx, double cy) {		
		ball = createBall(cx, cy);
		
		double paddleTop = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT + 5;
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;
		
		while(withinBottomWall(ball) && brickCounter > 0) {
			GObject collider = getCollidingObject();
			
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			// Check that ball isn't inside the paddle
			if(collider == paddle && (ball.getY() + BALL_RADIUS * 2) > paddleTop) {
				vy = -vy;
			}
			else if(collider != null) {
				remove(collider);
				vy = -vy;
				brickCounter--;
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
	}
	
	private void createBricks(double cx) {
		double xstart = cx - ((NBRICK_COLUMNS / 2) * (BRICK_WIDTH + BRICK_SEP) - (BRICK_SEP/2));

		// create NBRICK_ROWS rows of bricks
		for (int r = 0; r < NBRICK_ROWS; r++) {
			
			// create one row of bricks
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				GRect brick = new GRect(xstart + ((BRICK_WIDTH + BRICK_SEP) * c), BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * r), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				
				// set brick color based on row
				if(r < 2) {
					brick.setColor(Color.RED);
				} else if(r < 4) {
					brick.setColor(Color.ORANGE);
				} else if(r < 6) {
					brick.setColor(Color.YELLOW);
				} else if(r < 8) {
					brick.setColor(Color.GREEN);
				} else if(r < 10) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	
	private GRect createPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	private void addPaddleToCenter(double cx) {
		add(paddle, cx - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
	}	
	
	private GOval createBall(double cx, double cy) {
		GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, cx - BALL_RADIUS, cy - BALL_RADIUS);
		return ball;
	}
	
	private GLabel centerText (double cx, double cy, int x) {
		GLabel centerText = new GLabel("");
		if (x == 2) {
			centerText.setLabel("You Lose");
			centerText.setColor(Color.RED);			
		}
		else if (x == 3) {
			centerText.setLabel("You Win");
			centerText.setColor(Color.GREEN);
		}
		else if (x > 3) {
			centerText.setLabel("Remaining Lives: " + (x - 5));
			centerText.setColor(Color.BLACK);
		}
		centerText.setFont("SansSerif-20");
		centerText.setLocation(cx - centerText.getWidth()/2, cy + centerText.getAscent()/2);
		return centerText;
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH/2;
		double paddleY = paddle.getY();

		if(mouseX >= 0 && (e.getX() + PADDLE_WIDTH/2) < getWidth()) {
			paddle.setLocation(mouseX, paddleY);
		}
	}
	
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}
	
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	private boolean withinBottomWall(GOval ball) {
		return ball.getY() < getHeight();
	}
	
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} 
		else if(getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY() ) != null) {
			return getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY());
		}
		else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null) {
			return getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2));
		}
		else if(getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2)) != null) {
			return getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2));
		} 
		else {
			return null;
		}
	}
	
}
