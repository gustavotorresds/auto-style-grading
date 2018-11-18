/*
 * File: Breakout.java
 * -------------------
 * Name: Courtney Gao
 * Section Leader: Avery Wang
 * 
 * This file implements the game of Breakout. Player has 3 attempts to break 
 * all the bricks on the screen with the bouncing ball. If the ball hits the 
 * bottom of the screen before all the bricks are gone, an attempt is spent. 
 * Player controls the game by moving the paddle with player's mouse movements. 
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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Random generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private static final GLabel START_PROMPT = new GLabel("Click to start!"); 
	private static final String NEW_TURN = "Click for next turn!";
	private static final GLabel LOSE_GAME = new GLabel("Game over, you lose!");
	private static final GLabel WIN_GAME = new GLabel("You win!");

	
	// Ball's horizontal velocity
	private double vx; 
	
	// Ball's vertical velocity
	private double vy = VELOCITY_Y; 
	
	// Displays number of turns left
	private GLabel turnsDisplay; 
	
	// Message displayed at end of turn
	private GLabel turnOverMsg = new GLabel(""); 
	
	private GRect paddle;
	private GOval ball;
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;
	private int turnsLeft = NTURNS;
	
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setup();
		while(turnsLeft!= 0) {		
			while(turnIsRunning()){
				moveBall();
				pause(DELAY);
			}	
		}
	}
	
	/*
	 * setup()
	 * This method sets up the screen and prompts the player
	 * to press start. 
	 */
	private void setup() {
		setUpBricks();
		makePaddle();
		addBall();
		setSpeed();
		addCenteredText(START_PROMPT);
		addTurnsDisplay();
		addMouseListeners();
		waitForClick();
		remove(START_PROMPT);
	}
	
	/*
	 * setUpBricks()
	 * This method sets up a grid of bricks based on the number of bricks in each
	 * column and row, the height and width of each brick, the space between two 
	 * bricks and the space between the top of the screen and the top row.
	 */
	private void setUpBricks() {
		for (int row = 0; row < NBRICK_ROWS; row ++) {
			for (int col = 0; col < NBRICK_COLUMNS; col ++) {
				double x = rowStart() + (col * (BRICK_WIDTH + BRICK_SEP));
				double y = BRICK_Y_OFFSET + (row * (BRICK_HEIGHT + BRICK_SEP));
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(setRowColor(row));
				add(brick);
			}
		}
	}
	
	/*
	 * setRowColor
	 * This method returns what the color of the new brick 
	 * should be, based on the row it's in.
	 * 
	 * @param row: the row of bricks that is being created.
	 */
	private Color setRowColor(int row) {
		if (row < 2) {
			return Color.RED;
		} else if (row < 4) {
			return Color.ORANGE;
		} else if (row < 6) {
			return Color.YELLOW;
		} else if (row < 8) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	/*
	 * makePaddle()
	 * This method makes the GRect paddle and adds it to the screen.
	 */
	private void makePaddle() {
		paddle = new GRect(getWidth()/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.DARK_GRAY);
		add(paddle);
	}

	/*
	 * addBall()
	 * This method creates the GOval ball and adds it to the screen.
	 */
	private void addBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.DARK_GRAY);
		add(ball);
	}
	
	/*
	 * setSpeed()
	 * This method sets the horizontal speed of the ball 
	 * at the beginning of each turn. 
	 * 
	 * Precondition: either the game has not begun, or the
	 * last turn has completed. 
	 */
	private void setSpeed() {
		vx = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}		
	}

	/*
	 * addStartPrompt()
	 * This method adds 2 GLabels onto the screen that tell the player:
	 * 1) Click to start the game.
	 * 2) Number of turns remaining.
	 * 
	 * Precondition: game has not started yet.
	 */
	private void addTurnsDisplay() {
		turnsDisplay = new GLabel("You have " + NTURNS + " turns left");
		double display_x = getWidth() - turnsDisplay.getWidth() - BRICK_SEP;
		double display_y = 2 * turnsDisplay.getAscent();
		add(turnsDisplay, display_x, display_y);
	}
	
	/*
	 * moveBall()
	 * This method moves the ball based on
	 * the appropriate vertical and 
	 * horizontal speed, then checks for 
	 * collisions and redirects the ball's
	 * movement if needed.
	 * 
	 * Postcondition: ball is within the 
	 * screen.
	 */
	private void moveBall() {
		ball.move(vx, vy);
		checkForCollisions();
		redirectBall();
	}

	/*
	 * checkForCollisions()
	 * This method reverses the ball's vertical
	 * trajectory if the ball hits an object, removing
	 * it if it's a brick. If the ball hits the last 
	 * brick, the game ends and the closing screen is 
	 * displayed.
	 */
	private void checkForCollisions() {
		if(checkBallCorner()!=null) {
			if (checkBallCorner() == paddle) {
				vy = -Math.abs(vy); // prevents ball sticking to paddle
			} else {
				remove(checkBallCorner());
				bricksLeft --;
				vy = -vy;
				if(bricksLeft == 0) {
					turnsLeft = 0;
					addCenteredText(WIN_GAME);
				}
			} 
		}
	}
	
	/*
	 * checkBallCorner
	 * If there's an object that overlaps with the bounding box of the ball,
	 * this method returns this object. Otherwise, the method returns null.
	 * 
	 * @return GObject - an object at one of the corners of ball, or null.
	 */
	private GObject checkBallCorner() {
		if(getElementAt(ball.getX(), ball.getY())!= null){
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getRightX(), ball.getY())!= null) {
			return getElementAt(ball.getRightX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getBottomY())!= null) {
			return getElementAt(ball.getRightX(), ball.getY());
		} else if (getElementAt(ball.getRightX(), ball.getBottomY())!= null) {
			return getElementAt(ball.getRightX(), ball.getBottomY());
		} else {
			return null;
		}
	}
	
	/*
	 * turnIsRunning()
	 * If the ball has reached the bottom of the 
	 * screen, this method displays the message 
	 * signaling a turn has completed, and resets 
	 * the ball. 
	 * 
	 *  @return boolean - returns false if ball
	 *  reaches the bottom of the screen
	 */

	private boolean turnIsRunning() {
		if(ball.getBottomY() >= getHeight()) {
			turnsLeft --;
			endOfTurnScreen();
			reset();
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * reset()
	 * This method returns the ball to 
	 * the center of the screen and resets
	 * the horizontal velocity. 
	 * 
	 * Precondition: previous turn has
	 * just completed.
	 */
	
	private void reset() {
		ball.setCenterX(getWidth()/2);
		ball.setCenterY(getHeight()/2);
		setSpeed();
		remove(turnOverMsg);
	}
	
	/*
	 * redirectBall()
	 * This method redirects the ball if it hits the top, right or
	 * left side of the screen.
	 * 
	 * Postcondition: ball's velocity will move it away from the
	 * side of the screen.
	 */
	private void redirectBall() {
		if (ball.getY() <= 0) {
			vy = Math.abs(vy);
		}
		if (ball.getX() <= 0 || ball.getRightX() >= getWidth()) {
			vx = -vx;
		}		
	}

	/*
	 * mouseMoved(MouseEvent e)
	 * This method updates the paddle location based on the
	 * x-coordinate of the mouse. 
	 * 
	 * @param e - the MouseEvent at which the mouse is moved.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		if (x < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, y);
		} else {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		}
	}	

	/*
	 * endOfTurnScreen()
	 * Displays the result of the turn that just completed. If there are
	 * turns remaining, it prompts the player to click for the next
	 * turn. Otherwise, it notifies the player that the game is over and
	 * removes the paddle and ball.  
	 */
	private void endOfTurnScreen() {
		turnsDisplay.setLabel("You have " + turnsLeft + " turns left");
		if(turnsLeft > 0) {
			turnOverMsg.setLabel(NEW_TURN);
			addCenteredText(turnOverMsg);
			waitForClick();
		} else {
			addCenteredText(LOSE_GAME);
			remove(paddle);
			remove(ball);
		}
	}
	
	/*
	 * addCenteredText(GLabel message)
	 * Adds a GLabel, centered on the screen.
	 * 
	 * @param message - the GLabel to be added to the screen.
	 */

	private void addCenteredText(GLabel message) {
		double msg_x = getWidth()/2 - message.getWidth()/2;
		double msg_y = getHeight()/2 - message.getAscent()/2;
		add(message, msg_x, msg_y);
	}
	
	/*
	 * rowStart()
	 * This method calculates where the left side of the brick grid should be located.
	 * 
	 * @return double - the leftmost x-coordinate of the brick grid.
	 */
	
	private double rowStart() {
		if (NBRICK_ROWS % 2 == 0) {
			return getWidth()/2 - (NBRICK_ROWS/2 * (BRICK_WIDTH + BRICK_SEP)) + (BRICK_SEP/2);
		} else {
			return getWidth()/2 - (NBRICK_ROWS/2 * (BRICK_WIDTH + BRICK_SEP)) + (BRICK_WIDTH/2);
		}
	}

}
