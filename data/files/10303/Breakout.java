/*
 * File: Breakout.java
 * -------------------
 * Name: Alejandro Salinas
 * Section Leader: Justin Xu
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
	public static final double PADDLE_Y_OFFSET = 80;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for the paddle 
	private GRect paddle = null;

	// Instance variable for the ball
	private GOval ball = null;

	// Instance variable for the bricks left label
	private GLabel bricks = null;
	
	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Instance variable for the ball's x-direction velocity
	private double vx;

	// Instance variable for the ball's y-direction velocity
	private double vy = VELOCITY_Y;

	// Instance variable that keeps count of the number 
	// of bricks on the screen at all times.
	private int brickCount = 0;

	// Instance variable that controls the multiplication
	// variable for the speed of the ball.
	int speedCounter = 1;
	
	// Instance variable that keeps count of the user's score
	int score = 0;

	// Loads audio clip of a ball bouncing
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/* Method: run()
	 * -------------
	 * Runs the Breakout program
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Places all of the bricks on the screen
		setBricks();

		// Creates the paddle
		createPaddle();
		addMouseListeners();

		// Plays the game.
		play();
		
		// Ends the game and tells the user if
		// they won or not and their score. 
		endGame();
	}

	/* Method: setBricks()
	 * -------------------
	 * Creates the bricks for 
	 * the game gives them a color.
	 */
	private void setBricks() {

		// nested for loops keep track of the row and column number
		for (int col = 1; col < NBRICK_COLUMNS + 1; col++) {
			for (int row = 1; row < NBRICK_ROWS + 1; row++) {

				// creates variables for the x and y location of the bricks
				double x = BRICK_SEP * (col) + BRICK_WIDTH * (col-1);
				double y = BRICK_Y_OFFSET + BRICK_SEP * row + BRICK_HEIGHT * row;

				// creates each brick
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				// sets the color of the bricks depending on the row number
				brick.setColor(rowColor(row));
				add(brick);

				// Counts the total number of bricks placed on the screen.
				brickCount++;
			}
		}
	}

	/* Method: rowColor()
	 * ---------------
	 * Assigns a color to the brick
	 * depending on its row number.
	 * Method takes the input of the
	 * row number. 
	 */
	private Color rowColor(int row) {
		if (row <= 2) {
			return Color.RED;
		} else if (row <= 4) {
			return Color.ORANGE;
		} else if (row <= 6) {
			return Color.YELLOW;
		} else if (row <= 8) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	/* Method: createPaddle()
	 * ----------------------
	 * Creates a black paddle and 
	 * adds it to the screen.
	 */
	private void createPaddle() {

		// creates variables to store the initial paddle location
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		// adds a black paddle at (paddleX, paddleY) to the screen
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/* Method: play()
	 * --------------
	 * Allows the user 3 turns to remove
	 * all of the bricks. If the user completes 3
	 * turns and leaves bricks on the screen,
	 * the user loses and a message is printed.
	 * If the user successfully removes all of the
	 * bricks, a win message is printed on the screen.
	 */
	private void play() {
		
		printBricks(brickCount);
		
		// Keeps track of the number of turns the user has played
		for (int i = 0; i < NTURNS; i++) {
			
			// adds a ball to the screen
			addBall();
			
			// randomly generates an initial vx velocity
			vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) { 
				vx = -vx;
			}
			
			// starts the game by prompting the user to click 
			startGame();
			
			// animates the ball and changes the ball's vx or vy 
			// values depending on the direction of the bounce
			watchBounces();
			
			// breaks the loop if all of the bricks have been removed
			if (brickCount == 0) {
				break;
			}
		}
	}
	
	/* Method: endGame()
	 * -----------------
	 * Removes the ball from
	 * the screen and prints
	 * either a win or loss 
	 * message to the user.
	 */
	private void endGame() {
		remove(ball);
		if (brickCount != 0) {
			printLost();
		} else {
			printWin();
		}
	}
	
	/* Method: addBall()
	 * -----------------
	 * Creates a ball and adds
	 * it to the screen
	 */
	private void addBall() {

		// creates variables to store the x and y starting location of the ball
		// the ball initially is located at the center of the screen
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;

		// adds a filled ball at (x,y) with the height/width equal to twice the radius length
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}

	
	/* Method: startGame()
	 * -------------------
	 * Creates a label that
	 * asks the user to click 
	 * to start the game. 
	 */
	private void startGame() {
		
		// Creates a label
		GLabel start = new GLabel ("Click to Start.");
		start.setFont("Courier-16");
		start.setColor(Color.BLACK);
		
		// Creates variables to store the centered location of the label
		double x = getWidth()/2 - start.getWidth()/2;
		
		// The y location is slightly above the center because the ball 
		// is located at the center.
		double y = getHeight()/2 - start.getAscent();
		add(start, x, y);
		
		// waits for user to click before removing the label
		waitForClick();
		remove(start);
	}
	
	/* Method: watchBounces()
	 * ----------------------
	 * Recognizes which wall the ball 
	 * bounces off of and appropriately 
	 * changes the ball's direction 
	 * by altering the velocity.
	 */
	private void watchBounces() {
		
		// Loop only continues if there are bricks on the screen
		while (brickCount>0) {
			
			// If the ball hits a side wall, only the vx changes
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			
			// If the ball hits the top wall, only the vy changes
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			
			// If the ball hits the bottom wall, the ball is removed 
			// and the while loop is broken.
			if (hitBottomWall(ball)) {
				remove(ball);
				break;
			}
			
			// The ball moves using the updated vx and vy velocities
			ball.move(vx, vy);
			
			// Checks to see what object the ball has collided with., if any. 
			collision();
			
			// Prints the bricks left onto the screen
			remove(bricks);
			printBricks(brickCount);
			
			// Animation delay
			pause(DELAY);	
		}
	}


	/* Method hitLeftWall()
	 * --------------------
	 * Returns true if the ball's x-location
	 * is less than the location of the left wall.
	 * False if it isn't.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/* Method hitRightWall()
	 * --------------------
	 * Returns true if the ball's x-location
	 * is greater than the location of the right 
	 * wall. False if it isn't.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	/* Method hitTopWall()
	 * --------------------
	 * Returns true if the ball's y-location
	 * is less than the location of the top wall.
	 * False if it isn't.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/* Method hitBottomWall()
	 * --------------------
	 * Returns true if the ball's y-location
	 * is greater than the location of the bottom wall.
	 * False if it isn't.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	
	/* Method: collision() 
	 * -------------------
	 * Incrementally increases the speed of 
	 * the ball when it hits the paddle and
	 * removes a brick if the ball collides
	 * with it.
	 */
	private void collision() {
		
		// assigns getCOllidingObject() to an object 
		GObject collider = getCollidingObject();
		
		// checks if the ball collided with the ball
		if (collider == paddle) {
			
			// increases the speed by 15% after every 10
			// paddle hits
			speedCounter++;
			if (speedCounter % 10 == 0) {
				vy = vy*1.15;
				vx = vx*1.15;
			}
			
			// avoids "glue" effect always setting vy to
			// a negative value when the ball hits the paddle
			vy = -Math.abs(vy);
			
			// plays the bouncing ball sound 
			bounceClip.play();
			
		// if there is an object at the collision
		// site, it can be assumed to be a brick
		} else if (collider != null && collider != bricks) {
			
			// changes direction of the ball
			vy = -vy;
			
			// removes the brick
			remove(collider);
			
			// decreases the brick counter
			brickCount--;
			
			// plays the bouncing ball sound
			bounceClip.play();
		}
	}
	
	/* Method: getCollidingObject()
	 * ----------------------------
	 * Returns the object at which
	 * a collision was detected.
	 */
	private GObject getCollidingObject() {
		
		// creates variables for the ball's
		// currently location and size
		double x = ball.getX();
		double y = ball.getY();
		double r = BALL_RADIUS;
		
		// Returns an object, if any, located 
		// at the top left corner of the ball.
		if (getElementAt(x,y) != null) {			
			return getElementAt(x, y);
			
		// Returns an object, if any, located 
		// at the top right corner of the ball.
		} else if (getElementAt(x + 2 *r, y) != null) {
			return getElementAt(x + 2 *r, y);
			
		// Returns an object, if any, located 
		// at the bottom left corner of the ball.	
		} else if (getElementAt(x, y + 2 *r) != null) {
			return getElementAt(x, y + 2 *r);
			
		// Returns an object, if any, located 
		// at the bottom right corner of the ball.	
		} else if (getElementAt(x + 2 *r, y + 2 *r) != null) {
			return getElementAt(x + 2 *r, y + 2 *r);
			
		// Returns null if no object was found at any corner.	
		} else {
			return null;
		}
	}
	
	/* Method: printBricks()
	 * ---------------------
	 * Prints the number of bricks
	 * left onto the screen 
	 */
	private void printBricks(int bricksLeft) {
		
		// Creates a label to print the score
		bricks = new GLabel ("Bricks left: " + bricksLeft);
		bricks.setFont("Courier-20");
		bricks.setColor(Color.BLACK);
		
		// Sets the location of the label
		double x = (getWidth() - bricks.getWidth())/2;
		double y = getHeight() - bricks.getAscent();
		add(bricks, x, y);
	}
	
	/* Method: printWin()
	 * -------------------
	 * Creates and prints a label
	 * congratulating the user for winning.
	 */
	private void printWin() {
		
		// Creates a congratulations label in red color
		GLabel win = new GLabel ("Congratulations! You won.");
		win.setFont("Courier-16");
		win.setColor(Color.RED);
		
		// Centers the label
		double x = getWidth()/2 - win.getWidth()/2;
		double y = getHeight()/2 - win.getAscent()/2;
		add(win, x, y);
	}

	/* Method: printLost()
	 * -------------------
	 * If there is one brick left,
	 * it prints a singular label
	 * else it prints a plural label. 
	 * Both labels break the news that
	 * the user has lost.
	 */
	private void printLost() {
		
		// Will only print if the user left 1 brick on the screen
		if (brickCount == 1) {
			singularLost(1);
			
		// Will print this label when the bricks 
		// on the screen is not 1.
		} else {
			pluralLost(brickCount);
		}
	}
	
	 /* Method: singularLost()
	  * ----------------------
	  * Prints a message with a 
	  * singular 'brick' in the 
	  * label. Should only be called
	  * if 1 brick is left on the screen.
	  */
	private void singularLost(int a) {
		
		// Creates a label with a singular 'brick'
		GLabel lost = new GLabel("You left " + a + " brick on the board.");
		lost.setFont("Courier-16");
		
		// Centers the label
		double x = getWidth()/2 - lost.getWidth()/2;
		double y = getHeight()/2 - lost.getAscent()/2;
		add(lost, x,y);
	}
	
	/* Method: pluralLost()
	 * -------------------
	 * Prints a message with a 
	 * plural 'bricks' in the 
	 * label. Should only be called if
	 * there is more than one brick
	 * left on the screen.
	 */
	private void pluralLost(int a) {
		
		// Creates a label with a plural 'bricks'
		GLabel lost = new GLabel("You left " + a + " bricks on the board.");
		lost.setFont("Courier-16");
		
		// Centers the label
		double x = getWidth()/2 - lost.getWidth()/2;
		double y = getHeight()/2 - lost.getAscent()/2;
		add(lost, x,y);
	}
	
	/* Method: mouseMoved()
	 * --------------------
	 * Centers the paddle on the
	 * x-directional movement of 
	 * the mouse. Keeps the paddle's
	 * y location constant.
	 */
	public void mouseMoved(MouseEvent e) {
		
		// creates variables for the center of the paddle
		// using the location of the moving mouse
		double mouseX = e.getX() - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		
		// checks if the paddle can be placed in the
		// boundary of the screen
		if (boundary(mouseX)) {
			
			// centers the paddle on the mouse location
			paddle.setLocation(mouseX,paddleY);
		
		// does not place the paddle on the mouse if any
		// part of the paddle would be outside the boundary
		// of the screen.	
		} else {
			e = null;
		}
	}	

	/* Method: boundary()
	 * ------------------
	 * Takes in the input of the 
	 * mouse's X-position with respect to
	 * the paddle and checks if the paddle,
	 * which is centered on the x-position,
	 * could be placed within the bounds of
	 * the screen.
	 */
	private boolean boundary(double mouseX) {
		if (mouseX > 0 && mouseX < getWidth()-PADDLE_WIDTH) {
			return true;
		} else {
			return false;
		}
	}
}
