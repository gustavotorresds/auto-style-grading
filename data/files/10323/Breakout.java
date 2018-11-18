/*
 * File: Breakout.java
 * -------------------
 * Name: Isabel Wang, izwang, 006177443
 * Section Leader: Ben Allen
 * 
 * This file implements the game of Breakout, which is a three-turn game.
 * The user deflects the moving ball with a paddle until all bricks have
 * been hit and removed by the moving ball.
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
	public static final double NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final double NBRICK_ROWS = 10;

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
	public static final double NTURNS = 3;
	
	// One life is 1.0
	public static final double LIFE = 1;

	// Number of bricks at the start of the game
	public static final double NBRICKS_START = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// Instance variables to work with
	private GLabel title1 = null;
	private GLabel title2 = null;
	private GLabel title3 = null;
	private GLabel continue1 = null;
	private GLabel continue2 = null;
	private GLabel win1 = null;
	private GLabel lose1 = null;
	private GRect paddle = null;
	private GOval ball = null;
	private boolean isMovingUp = false;
	private boolean notDead = true;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	private double vy = VELOCITY_Y;
	private double nTurns = NTURNS;
	private double bricksLeft = NBRICKS_START;
	
	public void run() {
		// Sets the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Runs code
		
		// This detects the user's mouse
		addMouseListeners();
		setUpWorld();
		// As long as the player still has lives, the game restarts
		// until the player removes all bricks
		while (notDead) {
			startOver();
			playWorld();
			if (bricksLeft == 0) break;
		}
		finalScreen();
	}

	public void mouseClicked(MouseEvent e) {
		// Empty because it has to be to prevent crashes
	}

	private void setUpWorld() {
		// Sets up the graphics for the game's world
		titleScreen();
		removeTitleScreen();
		setUpBricks();
		setUpPaddle();
		setUpBall();
	}
	
	private void titleScreen() {
		// Tells the reader how to play the game
		title1 = new GLabel("Welcome to Breakout.");
		double x1 = (getWidth()-title1.getWidth()) / 2;
		double y1 = (getHeight() / 3 - title1.getAscent() / 2);
		title2 = new GLabel("Click once to set up your world. Click twice to begin your game.");
		double x2 = (getWidth()-title2.getWidth()) / 2;
		double y2 = (getHeight() / 2 - title2.getAscent() / 2);
		title3 = new GLabel("You have " + nTurns + " lives left.");
		double x3 = (getWidth()-title3.getWidth()) / 2;
		double y3 = (getHeight() * 2 / 3 - title3.getAscent() / 2);

		add(title1, x1, y1);
		add(title2, x2, y2);
		add(title3, x3, y3);
		
		waitForClick();
	}

	private void removeTitleScreen() {
		// Gets rid of text objects
		remove(title1);
		remove(title2);
		remove(title3);
	}

	private void setUpBricks() {
		// Sets up the rows of bricks
		for (double i = 0; i < NBRICK_ROWS; i++) {
			setUpRow(NBRICK_COLUMNS, i);
		}
	}

	private void setUpRow(double numBricks, double rowNum) {
		// Sets up the columns of bricks
		for (double i = 0; i < numBricks; i++) {
			double x = (getWidth() - (BRICK_WIDTH * numBricks) - (BRICK_SEP * (numBricks - 1))) / 2 + (i * BRICK_WIDTH) + ((i) * BRICK_SEP);
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT * rowNum) + (BRICK_SEP * (rowNum));
			GRect bricks = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
			bricks.setFilled(true);
			
			// Sets up the colors of rows
			if ((rowNum + 2) % 10 == 0 || (rowNum + 2) % 11 == 0) {
				bricks.setColor(Color.CYAN);
			} else if ((rowNum + 2) % 8 == 0 || (rowNum + 2) % 9 == 0) {
				bricks.setColor(Color.GREEN);
			} else if ((rowNum + 2) % 6 == 0 || (rowNum + 2) % 7 == 0) {
				bricks.setColor(Color.YELLOW);
			} else if ((rowNum + 2) % 4 == 0 || (rowNum + 2) % 5 == 0) {
				bricks.setColor(Color.ORANGE);
			} else if ((rowNum + 2) % 2 == 0 || (rowNum + 2) % 3 == 0) {
				bricks.setColor(Color.RED);
			}
			
			add(bricks);		
		}
	}
	
	private void setUpPaddle() {
		// Sets up black paddle graphics
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	public void mouseMoved(MouseEvent e) {
		// Allows detection of mouse for paddle movement
		double mouseX = e.getX();
		double paddleX = mouseX - PADDLE_WIDTH / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (paddle != null) {
			paddle.setLocation(paddleX, paddleY);
			if (mouseX + PADDLE_WIDTH / 2 >= getWidth()) {
				paddleX = getWidth() - PADDLE_WIDTH;
				paddle.setLocation(paddleX, paddleY);
			} else if (mouseX - PADDLE_WIDTH / 2 <= 0) {
				paddleX = 0;
				paddle.setLocation(paddleX, paddleY);
			}
		}
	}
	
	
	private void setUpBall() {
		// Sets up ball graphics
			createBall();
			
			// Sets original random direction of ball movement
			if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	private void createBall() {
		// Draws the black ball
		double ballSize = BALL_RADIUS * 2;
		double x = (getWidth() - ballSize) / 2;
		double y = (getHeight() - ballSize) / 2;
		ball = new GOval (x, y, ballSize, ballSize);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	private void startOver() {
		// Starts a new turn
		if (nTurns < NTURNS) {
			remove(ball);
			continueScreen();
			waitForClick();
			removeContinueScreen();
			setUpBall();
			waitForClick();
		}
	}

	private void continueScreen() {
		// After a life is lost, this tells the user that in order 
		// to continue, the user must click to start again
		continue1 = new GLabel("If you would like to continue, click twice again.");
		double x1 = (getWidth()-continue1.getWidth()) / 2;
		double y1 = (getHeight() / 2 - continue1.getAscent() / 2);
		continue2 = new GLabel("You have " + nTurns + " lives left.");
		double x2 = (getWidth()-continue2.getWidth()) / 2;
		double y2 = (getHeight() / 3 * 2 - continue2.getAscent() / 2);
		add(continue1, x1, y1);
		add(continue2, x2, y2);
	}
	
	private void removeContinueScreen() {
		// Removes the continue text from canvas
		remove(continue1);
		remove(continue2);
	}

	private void playWorld() {
		// After setting up the world, this allows the player to click
		// the screen in order to begin the game
		waitForClick();
		while (notDead) {
			moveBall();
			// Animation delay
			pause(DELAY);
			if (bricksLeft == 0) break;
		}
	}
	
	private void moveBall() {
		// This is what happens as the ball begins animation
		startBall();
		changeX();
		changeY();
		checkCollisions();
		loseTurns();
	}

	private void startBall() {
		// Begins the game with the ball moving in the downward direction
		if (!isMovingUp) {
			ball.move(vx, vy);
		} else {
			ball.move(vx, -vy);
		}
	}

	private void changeX() {
		// If ball hits side walls, it bounces off in the opposite direction
		if (ball.getX() >= getWidth() - BALL_RADIUS * 2) {
			vx = -vx;
		} else if (ball.getX() <= 0) {
			vx = -vx;
		}
	}

	private void changeY() {
		// If ball hits ceiling, it bounces off in opposite y direction
		if (ball.getY() <= 0) {
			isMovingUp = false;
		}
	}
	
	private GObject getCollidingObject() {
		// Retrieves information about colliding graphics object
		double x = ball.getX();
		double y = ball.getY();
		double r = BALL_RADIUS;
		// Checks each corner of ball for collisions
		GObject collider = getElementAt(x, y);
		if (collider == null) {
			collider = getElementAt(x + 2 * r, y);
		} if (collider == null) {
			collider = getElementAt(x, y + 2 * r);
		} if (collider == null) {
			collider = getElementAt(x + 2 * r, y + 2 * r);
		}
		
		return collider;
	}
	
	private void checkCollisions() {
		// If ball hits an object, it is removed and the ball bounces off
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				isMovingUp = true;
			} else {
				if (isMovingUp) {
					isMovingUp = false;
					remove(collider);
					bricksLeft = bricksLeft - 1;
				} else {
					isMovingUp = true;
					remove(collider);
					bricksLeft = bricksLeft - 1;
				}
			}
		}
	}
	
	private void loseTurns() {
		// If paddle misses ball, the game starts over with a missing life
		if (ball.getY() > getHeight()) {
			remove(ball);
			nTurns = nTurns - LIFE;
			if (nTurns <= 0) {
				notDead = false;
			} else {
				startOver();
			}
		}
	}
	
	private void finalScreen() {
		// Shown when players lose or win the game
		if (bricksLeft == 0) {
			winScreen();
		} else {
			loseScreen();
		}
	}

	private void winScreen() {
		// Tells the player that the player has won
		clearScreen();
		win1 = new GLabel("Congratulations! You have won the game!");
		double x1 = (getWidth()-win1.getWidth()) / 2;
		double y1 = (getHeight() / 2 - win1.getAscent() / 2);
		add(win1, x1, y1);
	}

	private void clearScreen() {
		// After the game is over, the paddle and ball are removed
		remove(paddle);
		remove(ball);
	}

	private void loseScreen() {
		// Tells the player that the player has lost
		clearScreen();
		lose1 = new GLabel("I'm sorry. You have run out of lives. Try again next time!");
		double x1 = (getWidth()-lose1.getWidth()) / 2;
		double y1 = (getHeight() / 2 - lose1.getAscent() / 2);
		add(lose1, x1, y1);
	}
}
