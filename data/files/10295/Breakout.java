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

	//Instance variables: the paddle, ball, random generator, velocity, total number of bricks, and
	//number of tries need to be visible to the entire program
	GRect paddle = null;
	GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vy = VELOCITY_Y;
	private double vx; 
	private double numberOfBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	int numberOfTries = 3;

	public void run() {
		//SETUP: these commands set up the screen for gameplay
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//build the bricks
		buildRows();

		//set up the paddle
		paddle = createPaddle();
		add(paddle);
		addMouseListeners();

		//create the ball
		ball = createBall();
		add(ball);
		

		//GAMEPLAY: these commands start and end the game
		//initiate and continue movement of the ball until the conditions to end the game are met 
		moveBall();

		//display win/loss after game ends
		gameOver();
	}
	
	/*Method: buildRows(); 
	 * 
	 *This method creates the rows of bricks in the game of the appropriate colors. It does this 
	 *by running a nested for loop that fills the row with a number of bricks designated by a constant
	 *inside of a for loop that fills the screen with a number of rows designated by a constant. The
	 *color of the rows changes based on the number of iterations of the outer for loop, which deturmines
	 *the number of rows.  
	 */
	
	private void buildRows() {
		double multiplier = 1;
		Color c = Color.RED;
		int dividend = NBRICK_ROWS / 5; 
		for(int j = 0; j < NBRICK_ROWS; j++) {
			if (j == 0) { 
				c = Color.RED;
			} 
			else if(j == dividend) { 
				c = Color.ORANGE;
			} 
			else if(j == dividend * 2) { 
				c = Color.YELLOW;
			} 
			else if(j == dividend * 3) { 
				c = Color.GREEN;
			} 
			else if(j == dividend * 4) { 
				c = Color.CYAN;
			}
			for(int i = 0; i < NBRICK_COLUMNS; i++) {
				//double x centers the rows of bricks
				double x = (BRICK_SEP + (CANVAS_WIDTH - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS - BRICK_SEP))/ 2);
				//double y makes sure each row is below the previous row
				double y = BRICK_Y_OFFSET + NBRICK_ROWS * multiplier;
				//the value added to x makes sure each brick is placed next to the previous one
				//instead of on top of it.
				placeBrick(x + (BRICK_WIDTH * i + BRICK_SEP * (i - 1)), y, c);
			}
			multiplier ++; //the number of "rows down" the rows are placed after each previous row
		}
	}
	
	/*Method: placeBrick(); 
	 * 
	 *This method constructs filled rectangular bricks at a given (x, y) and color location and 
	 *a predetermined width and height.  
	 */

	private void placeBrick(double x, double y, Color c) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(c);
		brick.setFilled(true);
		add(brick);
	}
	
	/*Method: createPaddle(); 
	 * 
	 *This method creates and returns a filled rectangular paddle of a predetermined width and height 
	 *at an x of 0 and a predetermined height.
	 */

	private GRect createPaddle() {
		double paddleY = (getHeight() - PADDLE_Y_OFFSET);
		GRect paddle = new GRect(0, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	/*Method: mouseMoved(); 
	 * 
	 *This method allows the user to move the paddle with the mouse by changing the X value of the
	 *paddle to whatever the X value of the mouse is.
	 */
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		//makes sure the paddle doesn't go off the screen
		if (mouseX + PADDLE_WIDTH >= CANVAS_WIDTH) { 
			mouseX = CANVAS_WIDTH - PADDLE_WIDTH;
		}
		double paddleY = (getHeight() - PADDLE_Y_OFFSET);
		paddle.setLocation(mouseX, paddleY);
	} 
	
	/*Method: createBall(); 
	 * 
	 *This method creates and returns an oval to represent the ball at the center of the screen.
	 */

	private GOval createBall() {
		double centerX = ((getWidth() - 2 * BALL_RADIUS) / 2);
		double centerY = ((getHeight() - 2 * BALL_RADIUS) / 2);
		GOval ball = new GOval(centerX, centerY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		return ball;
	}

	/*Method: moveBall(); 
	 * 
	 *This method initiates the game play by allowing the ball to move when the user clicks on the 
	 *screen. It randomly generates a value for velocity between 1.0 and 3.0, and randomly generates
	 *a direction for the ball to start moving. It then runs a while loop that is set to continue until
	 *the user either runs out of tries (i.e. the ball hits the bottom of the screen 3 times), or until
	 *they get rid of all the bricks on the screen. In the while loop, the ball is set to move according
	 *to the x and y velocity, and tests whether or not the ball is hitting the walls. If it hits any of 
	 *the walls, it will bounce off of them. If it hits the bottom wall, it will lose one "try" of 3. It
	 *will then test whether or not it is hitting a brick or the paddle, done through the "removeCollider"
	 *condition
	 */
	
	private void moveBall() {
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5) == true) {
			vx = -vx;
		}
		while(numberOfTries != 0 && numberOfBricks != 0) {
			ball.move(vx, vy); 
			pause(DELAY);
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			else if (hitTopWall(ball)) {
				vy = -vy;
			} 
			else if (hitBottomWall(ball)) { 
				vy = -vy;
				numberOfTries --;
				ball.setLocation((getWidth() - 2 * BALL_RADIUS) / 2, ((getHeight() - 2 * BALL_RADIUS) / 2));
			} else { 
				removeCollider();
			}
		}
	}

	//Returns a boolean for whether or not the ball hits the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	//Returns a boolean for whether or not the ball hits the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	//Returns a boolean for whether or not the ball hits the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//Returns a boolean for whether or not the ball hits the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/*Method: removeCollider(); 
	 * 
	 * This method tests whether or not an object identified as a "collider" is either the paddle
	 * or a brick, and determines whether or not the ball should remove it and bounce off of it (brick)
	 * or simply bounce off of it (paddle).
	*/
	
	private void removeCollider() {
		GObject collider = getCollidingObject();	
		if (collider == paddle) {
			vy = -vy; 
		} 
		else if (collider != null) {
			remove(collider);
			vy = -vy;
			numberOfBricks --; //subtracts from total brick count when a brick is hit
		} 
	}
	
	/*Method: getCollidingObject(); 
	 * 
	 * This method identifies whether or not the ball has run into an object and identifies it and 
	 * returns it as a collider. It does this by checking whether each of the four corners of the 
	 * rectangle that binds the oval that represents the ball are coming into contact with an object, 
	 * or returning null as a value.
	*/
	
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider == null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return collider;
	}
	
	/*Method: gameOver(); 
	 * 
	 * This method displays whether or not the user won or lost based on whether they used up
	 * all three tries or removed all of the bricks. 
	*/

	private void gameOver() {
		if (numberOfTries == 0) {
			println("You Lose.");
		} else {
			println("You Win!");
		}
	}
}


