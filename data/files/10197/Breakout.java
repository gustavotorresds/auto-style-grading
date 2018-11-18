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

	/*
	 * --------------------
	 * the run method is the core framework of this program by setting up the entire game and playing the game.
	 */
	public void run() {
		for(int i=0; i<4; i++) {
			setUpGame();
			playGame();
		}
	}
	/*
	 * ------------------------------
	 * setUpGame both sets up the bricks in the proper fashion as well as sets up the paddle to move with the mouse. This is essential
	 * to complete before the game can be played.
	 */

	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		setUpPaddle();
		setUpBall();
	}


	/*
	 * -----------------------
	 *setUpBricks sets up multi-colored bricks in 10 rows with 10 columns, evenly spaced apart, and centered horizontally. 
	 * The x coordinate consists of three equations to simplify reading the equation.
	 */

	private void setUpBricks() {
		for(int rowNumber = 0; rowNumber<10; rowNumber++) {
			for(int brickNumber=0; brickNumber<10; brickNumber++) {
				double centerX = CANVAS_WIDTH/2;
				double startingX = ((NBRICK_COLUMNS/2) * BRICK_WIDTH) + (((NBRICK_COLUMNS/2)-0.5)*BRICK_SEP);
				double placementX = (brickNumber*(BRICK_WIDTH+BRICK_SEP));
				double x = centerX - startingX + placementX;
				double y = (rowNumber*(BRICK_HEIGHT+BRICK_SEP))+PADDLE_Y_OFFSET;

				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(Color.RED);
				add(brick, x, y);

				if (rowNumber==2 || rowNumber==3) {
					brick.setColor(Color.ORANGE);
				}
				if (rowNumber==4 || rowNumber==5) {
					brick.setColor(Color.YELLOW);
				}
				if (rowNumber==6 || rowNumber==7) {
					brick.setColor(Color.GREEN);
				}
				if (rowNumber==8 || rowNumber==9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	private GRect paddle;

	/*
	 * ---------------------------------------
	 * setUpPaddle both places a centered paddle in the center of the screen before the game is played, but also tracks the mouse's movement and changes
	 * the paddle's x-coordinate according to the position of the mouse.
	 */
	private void setUpPaddle() {
		double x = CANVAS_WIDTH/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		if((0<e.getX()) && (e.getX()<CANVAS_WIDTH - PADDLE_WIDTH)) {
			paddle.setLocation(e.getX(), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	private GOval ball;

	/*
	 * ----------------------------------------
	 * setUpBall sets up the ball which will be utilized ot play the game.
	 * 
	 */
	private void setUpBall() {

		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;

		ball = new GOval (ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);

	}

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double vx;
	private double vy = 3.0;
	private int remainingBrickValue = 100;

	/*
	 * ------------------------------
	 * playGame has the ball move once the mouse is clicked, in accordance with the velocity of the ball which it tracks, and is sensitive to passing through the top wall.
	 */
	private void playGame() {
		waitForClick();
		trackVelocity();
		while(true) {
			moveBall();
			if(ball.getY() >= getHeight()) {
				break;
			}
			if(remainingBrickValue == 0) {
				break;
			}
		}
	}

	/* 
	 * ------------------------------------
	 * trackVelocity uses a random generator to create a randomized velocity for x. vy is fixed at 3.0 but is still susceptible to be reversed in later methods.
	 */
	private void trackVelocity() {
		vx = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;  
		}
		vy = 3.0;
	}
	/*
	 * -------------------------------
	 * moveBall uses an object variable "blocker" and the method "discriminateBlocker" to bounce of the walls and paddle by changing the velocity of 
	 * the ball as it hits a "blocker", or an obstacle such as the wall or paddle.
	 */

	private void moveBall() {
		ball.move(vx, vy);
		if((ball.getX() - vx <= 0 && vx < 0) || (ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx>0)) {
			vx = -vx;
		}

		if((ball.getY() - vy <= 0 && vy < 0)) {
			vy = -vy;
		}

		GObject blocker = discriminateBlocker();

		if(blocker == paddle) {
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 + 4) { 
				vy = -vy;
			}
		}

		else if (blocker != null) {
			remove(blocker);
			remainingBrickValue--;
			vy = -vy;
		}
		pause(DELAY);
	}

	/*
	 * ---------------------------------
	 * discriminateBlocker reads the x and y coordinates of the ball in it's current position and returns those coordinates to the moveBall method to determine
	 * how to "bounce" off the wall and then change velocity. 
	 */
	private GObject discriminateBlocker() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if((getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY())) != null) {
			return getElementAt((ball.getX() + BALL_RADIUS*2), ball.getY());
		}
		else if((getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2))) != null) {
			return getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2));
		}
		else if((getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2))) != null) {
			return getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2));
		}
		else {
			return null;
		}
	}

}
}