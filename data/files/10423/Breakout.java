/*
 * File: Breakout.java
 * -------------------
 * Name: Sophia Beauvoir
 * Section Leader: Peter Maldonado
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

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row and column
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

	//number of separations between each brick
	public static final int NSEP = 9;

	// instance variable for paddle
	private GRect paddle;

	//instance variable for ball
	private GOval ball;

	//instance variable for velocity of ball
	private double vx, vy;

	//instance variable for random generator for vx
	private RandomGenerator rgen = RandomGenerator.getInstance();

	int brickCounter;
	int livesCounter;
	GObject collider;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//creates bricks
		for(int i = 0; i < NBRICK_ROWS; i ++) {
			createRow((getWidth()/2) - (BRICK_SEP*9 + NBRICK_COLUMNS*BRICK_WIDTH)/2, BRICK_Y_OFFSET + i*BRICK_HEIGHT + i*BRICK_SEP, i + 1);
		}
		createPaddle();
		createBall();

		//sets vx to be random double between 1 and 3 and makes it negative half the time.
		ballVelocity();
		vy = VELOCITY_Y;
		brickCounter = NBRICK_COLUMNS*NBRICK_ROWS;
		livesCounter = NTURNS;
		addMouseListeners();

		while(livesCounter != 0 && brickCounter != 0) {
			waitForClick();
			while(brickCounter != 0) {
				ball.move(vx, vy);
				pause(DELAY);
				handleCollisions();

				//check wall collisions
				if(hitsLeftOrRightWall()) {
					vx = -vx;
				}
				if(hitsTopWall()) {
					vy=-vy;
				}
				if(hitsBottomWall()) {
					livesCounter --;
					ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
					break;
				}
			}
		}
		if(brickCounter == 0) {
			youWin();
		}
		if(livesCounter == 0) {
			youLose();
		}
	}

	private void createRow(double x, double y, int rowNum) {
		Color color = setColor(rowNum);
		for(int i=0; i < 10; i ++) {
			GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			rect.setColor(color);
			add(rect);
			x += BRICK_SEP + BRICK_WIDTH;
		}
	}
	// sets color for each row
	private Color setColor(int rowNum) {
		Color color;
		if (rowNum % 10  == 1 || rowNum % 10 == 2) {
			color = Color.RED;
		}else if(rowNum % 10 == 3 || rowNum % 10 == 4) {
			color = Color.ORANGE;
		}else if(rowNum % 10 == 5 || rowNum % 10 == 6) {
			color = Color.YELLOW;
		}else if(rowNum % 10 == 7 || rowNum % 10 == 8) {
			color = Color.GREEN;
		} else {
			color = Color.CYAN;
		}
		return color;
	}
	private void createBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	//sets vx to be random double between 1 and 3 and makes it negative half the time.
	private void ballVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setLocation((getWidth()/2 - (PADDLE_WIDTH/2)), (getHeight() - PADDLE_Y_OFFSET));
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK); 
		add(paddle);
	}

//when the mouse is moved, the paddle follows it's x position and keeps it's y position
public void mouseMoved(MouseEvent e) {
	double mouseX = e.getX() - PADDLE_WIDTH;
	double mouseY = e.getX();
	if(mouseX > 0 && mouseX < getWidth()) {
		paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
	}
}

private void handleCollisions() {
	GObject collider = getCollidingObject();
	if(collider != null) {
		vy = -vy;
		if(collider != paddle) {
			remove(collider);
			brickCounter --;
		} else {
			ballVelocity();
		}
	}
}
private GObject getCollidingObject() {
	double x = ball.getX();
	double y = ball.getY();
	GObject collider = getElementAt(x,y);
	if(collider != null) {
		return collider;
	}
	x += BALL_RADIUS*2;
	collider = getElementAt(x,y);
	if(collider != null) {
		return collider;
	}
	x = ball.getX();
	y += BALL_RADIUS*2;
	collider = getElementAt(x,y);
	if(collider != null) {
		return collider;
	}
	x += BALL_RADIUS*2;
	collider = getElementAt(x,y);
	if(collider != null) {
		return collider;
	}
	return null;
}

private boolean hitsLeftOrRightWall() {
	if((ball.getX() <= 0.0 || ball.getX() >= getWidth() - BALL_RADIUS*2)) {
		return true;
	}
	return false;
}
// check to see if the ball is outside the window and if it is to move in the opposite direction.
private boolean hitsTopWall() {
	if ((ball.getY() < 0.0)) {
		return true;
	}
	return false;
}

private boolean hitsBottomWall() {
	if(ball.getY() > getHeight() - BALL_RADIUS*2) {
		return true;
	}
	return false;
}
private void youLose() {
	GLabel youlose = new GLabel ("YOU LOSE!");
	youlose.setFont("SansSerif-28");
	youlose.setLocation((getWidth() - youlose.getWidth())/2, (getHeight()/2 - youlose.getAscent())/2);
	add(youlose);
}

private void youWin() {
	GLabel youWin = new GLabel("YOU WIN!");
	youWin.setFont("SansSerif-28");
	youWin.setLocation((getWidth() - youWin.getWidth())/2, (getHeight()/2 - youWin.getAscent())/2);
	add(youWin);
}
}

