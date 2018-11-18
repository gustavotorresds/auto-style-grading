/*
 * Name: Michael Oduoza
 * Section Leader: Andrew Marshall
 * 
 * This program implements the game Breakout, in which the user
 * tries to repeatedly bounce a ball off a paddle in order to destroy 
 * a stack of bricks and "break out". The user must not allow the ball to 
 * fall beyond the paddle. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	private GRect paddle = null;
	private GOval ball = null; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int brickCount = NBRICK_COLUMNS * NBRICK_ROWS;


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

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		setUpGame();

		//A loop for the number of turns the user has
		for(int turnsLeft = NTURNS; turnsLeft > 0; turnsLeft--) {
			serveBall();
			addMouseListeners(); 

			while(!endGameCondition()) {
				waitForClick();
				playGame();
			}

			resetRound(turnsLeft);
		}
		//If we get here, the whole game must have ended (either the user won, or used all his/her turns)
		provideFinalMessage();
	}

	/*
	 * This method gives the user a message after they lose 1 turn, and then resets the game for the next round/turn (if any are left).
	 * It also waits for a user click before beginning the next round. 
	 */
	private void resetRound(int turnsLeft) {
		remove(ball);
		if (userLoses()) {
			//If we get here, the user has lost 1 turn
			if (turnsLeft > 1) {
				println("You have " + (turnsLeft - 1) + " turns left! Click to try again!");
				waitForClick();
			}
		}
	}


	//This method provides the appropriate closing message to the user after the whole game is over
	private void provideFinalMessage() {
		if (userLoses()) {
			println("You've used all your turns! You lose!");
		} else {
			println("Congrats, you've won!"); 
		}
	}

	/*
	 * Either the userWins or the userLoses condition will
	 * terminate the game/round. The userWins condition will immediately terminate the whole game
	 * while the userLoses condition will terminate the current round and give the user
	 * another try, if there are any left. Otherwise, it will terminate the whole game like
	 * the userWins condition. 
	 */
	private boolean endGameCondition(){
		return (userWins() || userLoses());
	}

	//The user loses if the ball falls below the paddle
	private boolean userLoses() {
		return ball.getY() > (getHeight() - PADDLE_Y_OFFSET);
	}

	//The user wins if all the bricks have been removed
	private boolean userWins() {
		return brickCount == 0;
	}

	//This mouseMoved method is here to make the paddle follow the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleXCoordinate = mouseX - PADDLE_WIDTH/2;
		double paddleYCoordinate = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		/*this boolean was defined inside the mouse event method 
		 * in order to avoid issues with the scope of the mouseX variable. 
		 * It prevents the paddle from going off the screen
		 */
		boolean paddleIsAtEdge = (mouseX < PADDLE_WIDTH/2 || mouseX > getWidth() - PADDLE_WIDTH/2);
		if(!paddleIsAtEdge) {
			//sometimes the user may move the mouse before the program begins, and a nullpointer exception will be thrown
			if(paddle!= null) { 
				paddle.setLocation(paddleXCoordinate, paddleYCoordinate); 
			} 
		}
	}


	// Performs all the necessary initial setup for the game before the game begins  
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		makePaddle();
	} 


	// Sets up the bricks at the top of the console
	private void setUpBricks() {
		setUpRows(Color.RED, NBRICK_ROWS);
		setUpRows(Color.ORANGE, NBRICK_ROWS - NBRICK_ROWS/5);
		setUpRows(Color.YELLOW, NBRICK_ROWS - 2 * NBRICK_ROWS/5);
		setUpRows(Color.GREEN, NBRICK_ROWS - 3 * NBRICK_ROWS/5);
		setUpRows(Color.CYAN, NBRICK_ROWS - 4 * NBRICK_ROWS/5);
	}

	/*Sets up the rows for each color (2 rows per color); the parameters it takes in are:
	 * a) the color 
	 * b) the number associated with the "top row" of each color. 
	 * In the game we are considering, there are 10 rows total, and I set the row at the very top of the brick stack 
	 * as 10 and the row at the bottom of the brick stack as 1. Since we have 2 colors per row, the top row number goes 10, 8,
	 * 6, 4, 2, for RED, ORANGE, YELLOW, GREEN, and CYAN.
	 * If there were 20 rows, it would go 20, 16, 12, 8, and 4 (because then we would have 4 rows per color instead of two). 
	 * As we move down the brick stack, the number associated with each row decreases by 1. We divide NBRICK_ROWS by 5 in the formula
	 * because there are 5 colors, each of which we want to have an equal no. of rows. 
	 */
	private void setUpRows(Color color, int topRowNumber) {
		double startingXCoordinate = (getWidth() - (NBRICK_COLUMNS - 1) * BRICK_SEP)/2.0 - NBRICK_COLUMNS/2.0 * BRICK_WIDTH;
		double startingYCoordinate = BRICK_Y_OFFSET;

		//A loop to repeatedly set up rows
		for(int rowNumber = topRowNumber; rowNumber > (topRowNumber - NBRICK_ROWS/5); rowNumber--) {

			//A loop to set up each row by repeatedly adding bricks
			for (int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				double xCoordinate = startingXCoordinate + (BRICK_WIDTH + BRICK_SEP) * columnNumber;
				double yCoordinate = startingYCoordinate + ((NBRICK_ROWS - rowNumber) * (BRICK_HEIGHT + BRICK_SEP));
				GRect brick = new GRect(xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
			}
		}	
	}


	/*Makes the paddle initially and places it in the middle
	 * before the game starts (i.e. the x coordinate of the paddle is in the middle)
	 * The y coordinate is set appropriate, according to PADDLE_Y_OFFSET
	 */
	private void makePaddle() {
		double initialXCoordinate = (getWidth() - PADDLE_WIDTH)/2;
		double initialYCoordinate = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle = new GRect(initialXCoordinate, initialYCoordinate, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}


	/*Makes the ball initially before the game starts
	 * and places it right in the middle of the space
	 * in between the bricks and the paddle 
	 */
	private void serveBall() {
		double ballSize = 2 * BALL_RADIUS; // The diameter of the ball

		double topOfPaddle = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double bottomOfBricks = (BRICK_Y_OFFSET + NBRICK_ROWS * (BRICK_HEIGHT + BRICK_SEP)); //fix this midpoint

		double initialXCoordinate = (getWidth() - ballSize)/2;
		double initialYCoordinate = (topOfPaddle + bottomOfBricks)/2 - ballSize/2;

		ball = new GOval(initialXCoordinate, initialYCoordinate, ballSize, ballSize);
		ball.setFilled(true);
		add(ball);
	}
	

	/*
	 * This method animates the ball and gets it moving the way it is supposed to
	 * It also makes the ball interact with the bricks in the manner that it is supposed to.  
	 */
	private void playGame() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		while(!endGameCondition()) {
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopBoundary(ball)) {
				vy = -vy;
			}
			getCollidingObject();
			resolveCollision();
			ball.move(vx,vy);
			pause(DELAY);
		}
	}


	//checks if the ball hit the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}


	//checks if the ball hit the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}


	//checks if the ball hit the top boundary 
	private boolean hitTopBoundary(GOval ball) {
		return ball.getY() < 0;
	}


	/*
	 * This method checks for the presence of a colliding object with respect to the ball
	 * in each frame. It checks the "four corners" of the ball and returns the colliding object 
	 * if it exists and returns null otherwise. 
	 */
	private GObject getCollidingObject() {
		double upperLeftX = ball.getX(); // the x-coordinate of the upper left corner
		double upperLeftY = ball.getY(); // the y-coordinate of the upper left corner

		double lowerLeftX = ball.getX(); // the x-coordinate of the lower left corner
		double lowerLeftY = ball.getY() + ball.getHeight(); // the y-coordinate of the lower left corner

		double upperRightX = ball.getX() + ball.getWidth(); // the x-coordinate of the upper right corner
		double upperRightY = ball.getY(); // the y-coordinate of the upper right corner

		double lowerRightX = ball.getX() + ball.getWidth(); // the x-coordinate of the lower right corner
		double lowerRightY = ball.getY() + ball.getHeight(); // the y-coordinate of the lower right corner

		//these are the booleans that define the existence of collisions at each corner
		boolean lowerLeftCollision = getElementAt(lowerLeftX, lowerLeftY) != null;
		boolean lowerRightCollision = getElementAt(lowerRightX, lowerRightY) != null;
		boolean upperLeftCollision = getElementAt(upperLeftX, upperLeftY) != null;
		boolean upperRightCollision = getElementAt(upperRightX, upperRightY) != null;

		if(upperLeftCollision) {
			return(getElementAt(upperLeftX, upperLeftY));

		} else if(upperRightCollision) {
			return(getElementAt(upperRightX, upperRightY));

		} else if(lowerLeftCollision) {
			return getElementAt(lowerLeftX, lowerLeftY);

		} else if(lowerRightCollision) {
			return getElementAt(lowerRightX, lowerRightY);

		} else {
			return null; 
		}
	}


	/*
	 * This method logically decides what the ball should do next after a collision 
	 * has been detected (i.e. remove the collider and bounce, or simply just bounce)
	 */
	private void resolveCollision() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy);

			//now, the collider must be a brick
		} else if (collider != null) {
			remove(collider);
			brickCount --; //Reduce the brick count by 1
			vy = -vy;
		}
	}
} 




