/*
 * File: Breakout.javav 
 * -------------------
 * Name: Avery Dekshenieks 
 * Section Leader: Thariq Ridha
 * 
 * Simple Breakout Game that has a paddle, a ball, and a fixed number of bricks
 * Bricks can be knocked out by hitting the ball with the paddle so that the ball collides with bricks
 * The game is won by clearing all the bricks
 * The game is lost by failing to hit the ball with the battle more than the number of lives the user is given.
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
	public static final double CANVAS_WIDTH = 420; //420
	public static final double CANVAS_HEIGHT = 600; //600
	

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
	public static final double VELOCITY_Y = 3.0; //3

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	public static final double DELAY_BETWEEN_LIVES = DELAY * 100;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Used in finding the coordinates of the first brick so that the stack is centered in the window
	public static final int HALF_NBRICKS_COLUMN = 5;
	
	// Begins with a centered paddle
	public static final double startingPaddleXCoord = (CANVAS_WIDTH / 2) - (PADDLE_WIDTH / 2);
	public static final double paddleYCoord = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
	
	// Begins with a ball centered in the window
	public static final double startingBallXCoord = (CANVAS_WIDTH / 2) - BALL_RADIUS;
	public static final double startingBallYCoord = (CANVAS_HEIGHT / 2) - BALL_RADIUS;
	
	// Constructs the paddle and ball based on above conditions
	public GRect paddle = new GRect(startingPaddleXCoord, paddleYCoord, PADDLE_WIDTH, PADDLE_HEIGHT);
	public GOval ball = new GOval(startingBallXCoord, startingBallYCoord, (2 * BALL_RADIUS), (2 * BALL_RADIUS));
	
	// Creates a random number generator used later to determine the ball's initial launch velocity
	private RandomGenerator rgen = new RandomGenerator();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout - Avery Dekshenieks");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Sets up the world of Breakout, to begin the game
		buildWorld();
		
		/* Plays the game by adding paddle, ball, and physics of the world
		 * Runs for a certain number of turns (or lives) and then ends
		 * Game can be ended midway through by colliding the ball with every brick
		 * Post-Condition: Displayed message of either a win or a loss
		 */
		for (int i = NTURNS; i > -1; i--) {
			playGame(i);
		}
	}
	
	/* Builds the world of bricks with desired color scheme
	 * Adds a paddle with appropriate mouse movements
	 * Adds a ball that will later follow physical laws laid out by this program
	 */
	private void buildWorld() {
		addBricks(NBRICK_ROWS, NBRICK_COLUMNS);
		addBallAndPaddle();
	}
	
	/* Uses a nested for loop to add bricks column by column
	 * Uses a buildBrick() method to build a brick given the desired location, dimensions, and row number
	 * Uses a brickColor() method to set the color of a given brick in a column
	 * Counts the number of bricks added to get the total number of bricks on the screen
	 * Will later use this variable to see if user has won
	 */
	private void addBricks(int r, int c) {
		// Begins by looping through each desired column
		for (int column = 0; column < c; column++) {
			double brickXStart = (getWidth() / 2) - (HALF_NBRICKS_COLUMN * BRICK_WIDTH) - ((HALF_NBRICKS_COLUMN - 1) * BRICK_SEP);
			double brickX = brickXStart + (BRICK_WIDTH + BRICK_SEP) * column;
			
			// Within each column, it loops through each row, adding a brick of desired color
			for (int row = 0; row < r; row++) {
				double brickY = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * row);
				buildBrick(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT, row);
			}
		}
	}
	
	/* Builds a brick based on given inputs, adds to screen with appropriate color
	 * Not much here, but used for decomposition
	 */
	private void buildBrick(double x, double y, double width, double height, int row) {
		GRect brick = new GRect(x, y, width, height);
		brick.setFilled(true);
		brickCount += 1;
		
		//Computes what color to build the brick based on the row it is in
		brick.setColor(brickColor(row));
		add(brick);
	}
	
	// Sets up a variable used to later check if the user has removed all bricks
		private int brickCount;
		
	// Chooses the color of the brick based on the row given
	private Color brickColor(int row) {
		if (row <= 1) {                  // Rows 1 & 2 are red
			return Color.RED;
		}
		else if (row >= 2 && row <= 3) { // Rows 3 & 4 are orange
			return Color.ORANGE;
		}
		else if (row >= 4 && row <= 5) { // Rows 5 & 6 are yellow
			return Color.YELLOW;
		}
		else if (row >= 6 && row <= 7) { // Rows 7 & 8 are green
			return Color.GREEN;
		}
		else {
			return Color.CYAN;           // Rows 9 & 10 are cyan
		}
	}
	
	//Adds ball and paddle to the world, and allows the program to pick up user mouse drags and clicks
	private void addBallAndPaddle() {
		addMouseListeners(); 
		paddle.setFilled(true);
		add(paddle);	
		ball.setFilled(true);
		add(ball);
	}
	
	/* Receives input from a dragged mouse, and moves the paddle along with it
	 * Paddle is centered on mouse
	 * Paddle is limited by the CANVAS_WIDTH
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleXCoord = mouseX - (PADDLE_WIDTH / 2); //Centers paddle on mouse
		
		//Sets bounds on the paddle
		double leftXBound = 0;
		double rightXBound = getWidth() - PADDLE_WIDTH;
		
		//Updates the paddle's location after checking if it has reached a bound
		if (paddleXCoord > leftXBound && paddleXCoord < rightXBound) {
			paddle.setLocation(paddleXCoord, paddleYCoord);
		}
	}
	
	// Now that the world is setup, we are ready to program how the ball, paddle, and bricks interact!
	
	// vx and vy doubles give the horizontal and vertical velocities of the ball; must be viewed by multiple methods
	private double vx;
	private double vy;
	
	/* Once the mouse is clicked, the ball will launch with a random downward velocity within the given mins and maxes
	 * The program will then move the ball based on physics parameters outlined in the motionOfBall() method
	 * This segment checks to see if the ball has crossed the bottom of the window, and if so, implements the loseLife() method
	 * It simultaneously checks if the user has cleared all the bricks, and the turn the user is on
	 * Given these conditions above, the program will decide whether or not to keep moving the ball
	 * If the ball does not keep moving, this segment will print out the appropriate message to the user
	 * This message is either life lost, game over, or you win!
	 */
	private void playGame(int turn) {
		vx = horizontalLaunchVelocity();
		vy = VELOCITY_Y;
		// Begins motion with a user click
		waitForClick();
		// While ball motion bouncing around world off the paddle, with bricks remaining
		while(ball.getY() < getHeight() && brickCount > 0) { 
			motionOfBall();
		}
		
		/* After the ball falls through bottom of the world on its last life
		 * The reason the condition uses "1" as opposed to "0", is because the user has three lives
		 * However, while using the variable turn, it is inclusive.
		 * Meaning, that on the first turn, it is still life 3. 
		 * Second turn, life 2; last turn, life 1. Never reaches 0.
		 */
		if (turn > 1 && brickCount > 0) {
			loseLife(turn);
			ball.setLocation(startingBallXCoord, startingBallYCoord);
		}
		
		// Since turn 1 is the final turn, this checks whether the user cleared all the bricks
		else if (turn == 1 && brickCount > 0){
			// If there are bricks remaining, the user has lost
			gameOver();
		}
		else if (brickCount == 0) {
			// If there are no bricks remaining, and the user has not run out of lives, user wins
			youWin();
		}
	}
	
	/* This method takes a random value between a given min and max and assigns it to the horizontal velocity component
	 * The magnitude of both horizontal and vertical velocity compnents remains constant throughout the entire game
	 * This method assigns a random positive or negative to the front of the vx
	 * This makes the total velocity (when combined with vy) of the ball move anywhere from 0 to -180 degrees, with a certain magnitude
	 */
	private double horizontalLaunchVelocity() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		return vx;
	}
	
	/* This method serves as the basis for the physics of this game
	 * This flips component velocities so the ball can bounce off walls and objects
	 * Will remove brick if ball collides with one
	 * Will allow ball to fall through the bottom of the window 
	 */
	private void motionOfBall() {
		ball.move(vx, vy);
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}
		
		/* Use this statement to test end game conditions. 
		 * You can turn the velocity up and/or the DELAY down, and let it run until the end with ease
		 * Ball will never need the paddle to bounce back up
		 * Just un-slash it!
		 */
		//else if (ball.getY() > 400) {
		//	vy = -vy;
		//}
		
		GObject collider = collidingObject(ball.getX(), ball.getY());
		if (collider == paddle) {
			/* Prevents the ball from appearing glued to the paddle
			 * Does so by ensuring that the ball will not continuously flip positive and negative
			 * But rather, remain negative of what the component velocity was before
			 */
			vy = -Math.abs(vy); 
		}
		else if (collider != null) {  // If not paddle, it must be a brick
			remove(collider);
			brickCount--;
			vy = - vy;
		}
		// Allows frame rate to be visible to a human eye
		pause(DELAY);
	}
	
	// Returns true if the ball hits the left wall, based on its left edge
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	// Returns true if the ball hits the right wall, based on its right edge
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= (getWidth() - ball.getWidth());
	}
	// Returns true if the ball hits the top wall, based on its top edge
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	//No need for hitBottomWall because it would be a lost life
	
	private GObject collidingObject(double x, double y) {
		/* Checks to see if the coordinates of the ball lie on another GObject such as a paddle or brick
		 * returns the GObject it is on, or null if it is not on one.
		 * By using an && != paddle condition, it ensures the top two points of the ball do not return the paddle
		 * This is so that the ball only bounces up off the paddle if the bottom of the ball hits the paddle
		 * This results in the paddle missing the ball if the paddle does not actually get underneath it
		 */
		if (getElementAt(x, y) != null && getElementAt(x, y) != paddle) {
			return getElementAt(x, y);
		}
		else if (getElementAt(x + ball.getWidth(), y) != null && getElementAt(x + ball.getWidth(), y) != paddle) {
			return getElementAt(x + ball.getWidth(), y);
		}
		else if (getElementAt(x, y + ball.getHeight()) != null) {
			return getElementAt(x, y + ball.getHeight());
		}
		else if (getElementAt(x + ball.getWidth(), y + ball.getHeight()) != null) {
			return getElementAt(x + ball.getWidth(), y + ball.getHeight());
		}
		else {
			return null;
		}
	}
	
	/* This method is called when the ball falls through the bottom of the window
	 * Prompts user with their remaining life count
	 * Removes the GLabel before the ball is launched again
	 */
	private void loseLife(int turn) {
		/* turn variable gets confusing
		 * (turn - 1) used here to get remaining lives, "subtracting" the life just used
		 */
		GLabel lostLife = new GLabel("Lives Remaining: " + Integer.toString(turn - 1));
		lostLife.setFont("Courier-24");
		lostLife.setColor(Color.PINK);
		double lostLifeCoord = getWidth() / 2 - lostLife.getWidth() / 2;
		add(lostLife,lostLifeCoord , 550);
		pause(DELAY_BETWEEN_LIVES);
		remove(lostLife);
	}
	
	/* This method is called when the ball falls through the bottom of the window, and the user is out of lives
	 * Prompts the user with a message that ends the game
	 */
	private void gameOver() {
		GLabel loss = new GLabel("Game Over: No More Lives");
		loss.setFont("Courier-24");
		loss.setColor(Color.PINK);
		double lossXCoord = getWidth() / 2 - loss.getWidth() / 2;
		double lossYCoord = getHeight() / 2 - loss.getHeight() / 2;
		add(loss, lossXCoord, lossYCoord);
	}
	
	/* This method is called when the user successfully removes all of the bricks from the world
	 * Prompts user with a winning message that ends the game
	 */
	private void youWin() {
		GLabel win = new GLabel("You Broke Out!");
		win.setFont("Courier-24");
		win.setColor(Color.PINK);
		double winXCoord = getWidth() / 2 - win.getWidth() / 2;
		double winYCoord = getHeight() / 2 - win.getHeight() / 2;
		add(win, winXCoord, winYCoord);
	}
}	