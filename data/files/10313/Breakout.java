/*
 * File: Breakout.java
 * -------------------
 * Name: Ramona Greene
 * Section Leader: Thariq Ridha
 * This program creates a game called Breakout that allows a player to potentially
 * break all bricks on a screen by using the paddle and the walls of 
 * the game to bounce the ball towards the bricks. The player is guaranteed 3 lives and
 * the object of the game is to break all bricks. 
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

	GOval ball;
	GRect paddle;
	double counterOfBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	double counterOfTurns = NTURNS;
	private double vx = VELOCITY_X_MIN;
	private double vy = VELOCITY_Y;
	private GLabel label = new GLabel("");
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setUpGame();
		while(counterOfBricks > 0 && counterOfTurns > 0) {
			playBall();
		}
		winOrGameOver();
	}

	/* This method allows the paddle to move by the movement of the user with the mouse
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		if(e.getX() <= (getWidth() - PADDLE_WIDTH)) {
			//set the bounds of the screen
			paddle.setLocation(e.getX(), paddleY);
		}
	}

	/*This method sets up the game by creating the bricks, paddle, adding Mouse Listeners, 
	 * creating the ball and creating a random generator for the velocity and direction of the ball
	 */
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		makeBricks();
		makePaddle();
		addMouseListeners();
		makeBall();
		velocityControl();
		clickStart();
		waitForClick();
	}

	/*sets up bricks for the game
	 */
	private void makeBricks() {
		//makes all rows of bricks
		for(int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			//makes one row of bricks
			for(int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				//this sets the middle of the canvas width equal with the middle of the bricks
				double xCoordinateCenter = ((getWidth()/2) - ((NBRICK_COLUMNS/2) * BRICK_WIDTH) - (NBRICK_COLUMNS/2 * BRICK_SEP) - BRICK_SEP);
				/* These variables create the x and y coordinates of the bricks. 
				 * There is an extra BRICK_SEP being subtracted from the centering 
				 * of the row to account for the BRICK_SEP from the
				 * dimensions of the canvas 
				 */
				double x = (BRICK_WIDTH * columnNumber) + (columnNumber * BRICK_SEP) + xCoordinateCenter; 
				double y = BRICK_SEP + BRICK_HEIGHT * rowNumber + (rowNumber * BRICK_SEP); 
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(false);
				add(rect);

				//modifies the rowNumber color 
				int groupsOfTenRows = (rowNumber/10);
				if(rowNumber <= 1 + groupsOfTenRows*10 && rowNumber >= 0 + groupsOfTenRows*10) {
					rect.setFilled(true);
					rect.setColor(Color.RED);
					add(rect);
				} else if(rowNumber <= 3 + groupsOfTenRows*10 && rowNumber > 1 + groupsOfTenRows*10 ) {
					rect.setFilled(true);
					rect.setColor(Color.ORANGE);
					add(rect);
				} else if(rowNumber <= 5 + groupsOfTenRows*10 && rowNumber > 3 + groupsOfTenRows*10) {
					rect.setFilled(true);
					rect.setColor(Color.YELLOW); 
					add(rect);
				} else if(rowNumber <= 7 + groupsOfTenRows*10 && rowNumber > 5 + groupsOfTenRows*10) {
					rect.setFilled(true);
					rect.setColor(Color.GREEN);
					add(rect);
				} else if(rowNumber <= 9 + groupsOfTenRows*10 && rowNumber > 7 + groupsOfTenRows*10) {
					rect.setFilled(true);
					rect.setColor(Color.CYAN);
					add(rect);
				}
			}
		}
	}

	/*makes the paddle
	 */
	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		makePaddleBlack(paddle);
		add(paddle, getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
	}

	/*makes paddle black
	 */
	private void makePaddleBlack(GRect object) {
		object.setColor(Color.BLACK);
		object.setFilled(true);
	}

	/*makes the ball
	 */
	private void makeBall() {
		double ballXCoordinate = getWidth()/2 - BALL_RADIUS;
		double ballYCoordinate = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(ballXCoordinate, ballYCoordinate,BALL_RADIUS * 2, BALL_RADIUS *2); 
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, ballXCoordinate, ballYCoordinate);
	}

	/*randomly  generates the velocity and direction of the ball for each game
	 */
	private void velocityControl() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/*This method displays the words for the player to know to click the ball 
	 * to start the game
	 */ 
	private void clickStart() {
		label.setFont("Courier-24");
		label.setColor(Color.BLACK);
		label.setLabel("CLICK THE BALL TO START");
		add(label, getWidth()/2 - label.getWidth()/2, getHeight() - label.getHeight() *2);
	}

	/* This method allows the ball to move around the canvas and breaks bricks
	 */
	private void playBall() {
		/*this sets the variable collider equal to the getColliding Object to differientiate between 
		 * whether the object the ball collided with was the paddle or the brick
		 */
		GObject collider = getCollidingObject();   
		if(collider != null) {
			if(collider == paddle) {
				vy = Math.abs(vy);
				vy = -vy;
			} else if(collider == label) { 
				remove(label);
			} else {
				remove(collider);
				vy= -vy;
				counterOfBricks--;
			}
		}

		//signals the ball to change direction if ball hits  wall 
		if(hitLeftWall() || hitRightWall()) {
			vx = -vx;
		}

		//signals the ball to change direction if ball hits top wall 
		if(hitTopWall()) {
			vy = Math.abs(vy);
		}
		//signals the game to restart each time the player loses a life
		if(hitBottomWall()) {
			remove(ball);
			counterOfTurns--;
			makeBall();
			waitForClick();
		}
		// update world
		ball.move(vx, vy);
		pause(DELAY);
	}

	/* This method detects whether the ball has objects on all corners of the ball and outputs that object
	 */
	private GObject getCollidingObject() {
		/*These variables account for the coordinates that make up the location of each of the corners
		 */
		double leftXCoordinate = ball.getX();
		double rightXCoordinate = ball.getX() + 2 * BALL_RADIUS;
		double topYCoordinate = ball.getY();
		double bottomYCoordinate = ball.getY() + 2 * BALL_RADIUS;
		/*These variables account for the location of each of the corners of the ball
		 */
		GObject objTopLeft = getElementAt(leftXCoordinate, topYCoordinate);
		GObject objTopRight = getElementAt(rightXCoordinate, topYCoordinate);
		GObject objBottomLeft = getElementAt(leftXCoordinate, bottomYCoordinate);
		GObject objBottomRight = getElementAt(rightXCoordinate, bottomYCoordinate);

		if (objTopLeft != null) {
			return objTopLeft;
		} else if(objBottomLeft != null) {
			return objBottomLeft;
		} else if(objTopRight != null) {
			return objTopRight;
		} else if(objBottomRight != null) {
			return objBottomRight;
		} else {
			return null;
		}
	}

	/* lets the program know if the ball has hit the right wall.
	 */
	private boolean hitBottomWall() {
		return ball.getY() >=  getHeight() - ball.getHeight();
	}

	/* lets the program know if the ball has hit the top wall.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/* lets the program know if the ball has hit the left wall.
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/* lets the program know if the ball has hit the right wall.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= (getWidth() - ball.getWidth());
	}	
	/* This method tells the player if they have won or lost by displaying a congratulatory 
	 * picture or a picture telling the user they have lost
	 */
	private void winOrGameOver() {
		if(counterOfBricks == 0) {
			GImage img = new GImage("file:///Users/ramonagreene/Downloads/0_congratulations_beyonce.gif");
			add(img, getWidth()/2 - img.getWidth()/2, BRICK_SEP);
		} else if(counterOfTurns == 0) {
			GImage img = new GImage("file:///Users/ramonagreene/Downloads/willy%20wonka%20lose.jpg");
			add(img, getWidth()/2 - img.getWidth()/2, BRICK_SEP);
		}
	}
}






