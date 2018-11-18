/*
 * File: Breakout.java
 * -------------------
 * Name: Gabrielle Candes
 * Section Leader: Rhea Karuturi
 * 
 * This file implements a more interesting version of the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	private static final double CANVAS_WIDTH = 420;
	private static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	private static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	private static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	private static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	private static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	private static final double BRICK_HEIGHT = 8;

	// Width of the entire block of bricks, in pixels
	private static final double WIDTH_OF_BRICKS = (NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP);

	// Offset of the top brick row from the top, in pixels
	private static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	private static final double PADDLE_WIDTH = 60;
	private static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	private static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	private static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	private static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	private static final double VELOCITY_X_MIN = 1.0;
	private static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	private static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	private static final int NLIVES = 3;

	// instance variable for the paddle
	private GRect paddle = null; 

	// instance variable for the horizontal velocity of the ball
	private double vx;

	// instance variable for the vertical velocity of the ball
	private double vy;

	// instance variable that generates random numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// instance variable for the ball
	private GOval ball = null;

	// instance variable for counter for the number of bricks remaining
	// starts at the total number of bricks created
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;

	// delay between lives
	private static final double BIG_DELAY = 300;

	// instance variable for label that displays how many lives (turns) the player has left
	private GLabel lifeAlert = null;

	// instance variable for counter for number of lives remaining
	private int livesRemaining = NLIVES;

	// instance variable for label that displays the score
	private GLabel scoreBoard = null;

	// instance variable for the counter for the points accumulated
	private int points = 0;

	// instance variable for the label that shows before the game starts that instructs the user to click to start
	GLabel startLabel;



	public void run() {

		// Set the window's title bar text
		setTitle("CS 106A Cool Breakout");

		// Sets the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// sets up the game
		setUp();

		// animates the ball to "play" the game
		play();

	}

	/** METHOD: Play
	 * --------------
	 * Actually plays the game! 
	 * In this method, the ball is animated so that it moves around the screen, bouncing off the top and side walls and the paddle.
	 * When it hits a brick, it removes the brick and bounces according to the direction of collision.
	 * The extended version includes sounds for bounces off the bricks and paddles, a ball that speeds up a little bit into the game, 
	 * labels that display the score (different colored bricks have different point values) and number of lives left, 
	 * and a slightly more user-friendly interface with instructions about how to start play at the beginning and 
	 * a message saying whether the game was won or lost at the end. 
	 */
	private void play() { 

		// waits for the player's click to start the game
		waitForClick();

		// removes the starting label once gameplay begins
		remove(startLabel);

		// taken from assignment page
		// loads audio clip
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");


		// this for loop allows the player a certain number of lives before the game ends for real
		for(int i = 0; i < NLIVES; i++) {

			// subtracts one from the number of lives remaining and updates the label accordingly
			livesRemaining--;
			lifeAlert.setLabel("Lives Remaining: " + livesRemaining);

			// initial y velocity is 3 (going downward)
			// resets at the start of the next life
			vy = VELOCITY_Y;

			// taken from assignment page
			// generates a random horizontal velocity between -3 and -1 or between 1 and 3
			// this is inside the for loop so the ball doesn't have the same direction for every life
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;

			// counter that keeps track of how many times the ball has hit the paddle
			// resets at the beginning of every new life
			int timesHitPaddle = 0;

			// changes to true when the speed increases such that once the speed increases, it doesn't happen again 
			// resets at every round
			boolean speedIncrease = false;

			// at each new life, the ball's location resets to the center of the screen
			ball.setLocation(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);

			pause(BIG_DELAY);

			while(true) {

				// these two if statements are taken from the bouncing ball solution from lecture
				// updates direction
				if(hitLeftWall() || hitRightWall()) {
					vx = -vx;
				}
				if(hitTopWall()) {
					vy = -vy;
				}

				// if the ball hits the paddle, it should perform a vertical bounce
				// also plays the bounce noise and increases the count of times that the ball hits the paddle
				if(hitPaddle()) {
					bounceClip.play();
					vy = -vy;
					timesHitPaddle++;
				}

				// ball's vertical speed is doubled after it has hit the paddle ten times
				if(timesHitPaddle == 10 && speedIncrease == false) {
					vy = vy * 2;
					speedIncrease = true;
				}

				// if the ball hits the bottom wall, that round is over and the ball should stop moving
				if(hitBottomWall()) {
					break;
				}

				// removes any bricks the ball collides with and adjusts its velocity accordingly
				breakBricks();

				scoreBoard.setLabel("Points: " + points);

				if(bricksRemaining <= 0) {
					break;
				}

				// updates visualization
				ball.move(vx, vy);

				// pause so user can track movement
				pause(DELAY);

			}


			// pause before the ball is reset to the center of the screen so it is easier for the player to follow what is happening
			pause(BIG_DELAY);

		}

		// determines whether or not you have won the game after the three turns
		// if there are bricks remaining, you lost :(
		boolean wonGame = true;
		if(bricksRemaining > 0) {
			wonGame = false;
		}

		// displays a label that tells the player whether they won or lost
		makeEndLabel(wonGame);

	}


	/** METHOD: Make Start Label
	 * -------------------
	 * Makes and displays a label (for the beginning of the game) that prompts the user to click to start the game.
	 */
	private void makeStartLabel() {

		startLabel = new GLabel("click to play");

		startLabel.setFont("Default-18");

		double x = (getWidth() - startLabel.getWidth()) / 2;

		// the "10" offsets the label a bit so it is not on top of or touching the ball at the beginning
		double y = (getHeight() - startLabel.getAscent()) / 2 - 10;

		// adds the label to (roughly) the center of the canvas
		add(startLabel, x, y);

	}


	/** METHOD: Make End Label
	 * -------------------
	 * Makes and displays a label (for the end of the game) to tell the user if they won or lost.
	 */
	private void makeEndLabel(boolean wonGame) {

		// creates a String with initial value of null
		String message;

		if(wonGame) {
			message = "You won!";
		} else {
			message = "You lost!";
		}

		// creates a GLabel with a message that indicates whether the game was won or lost
		// adds the label such that it is centered on the canvas
		GLabel endOfGame = new GLabel(message);
		endOfGame.setFont("Default-24");
		double x = (getWidth() - endOfGame.getWidth()) / 2;
		double y = (getHeight() - endOfGame.getAscent()) / 2;
		add(endOfGame, x, y);

	}


	/** METHOD: Break Bricks
	 * ----------------------
	 * Makes the ball respond appropriately when it encounters a brick. 
	 * This response involves changing direction (dependent on the direction the ball was going before it hit the brick),
	 * removing said brick, decreasing the count of remaining bricks by one, and increasing the number of points according to the color of the brick.
	 * This method treats the four different sides of the balls as separate, so that the ball velocity update makes sense.
	 */
	private void breakBricks() {

		// taken from assignment page
		// loads audio clip
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		// stores the results of the four methods that identify bricks as GObjects
		// some of these values may be null
		// stored as four different variables so we have information about which direction the ball is moving when it hits a brick
		GObject above = brickAbove();
		GObject below = brickBelow();
		GObject right = brickRight();
		GObject left = brickLeft();

		// if there is a brick above the ball, it does a vertical bounce and the rest of the stuff
		if(above != null) {
			bounceClip.play();
			vy = -vy;
			remove(above);
			bricksRemaining--;
			points += getPointValue(above);
		} 
		// if there is a brick below the ball, it does a vertical bounce and the rest of the stuff
		else if(below != null) {
			bounceClip.play();
			vy = -vy;
			remove(below);
			bricksRemaining--;
			points += getPointValue(below);
		} 
		// if there is a brick to the right of the ball, it does a horizontal bounce and the rest of the stuff
		else if(right != null) {
			bounceClip.play();
			vx = -vx;
			remove(right);
			bricksRemaining--;
			points += getPointValue(right);
		} 
		// if there is a brick to the left of the ball, it does a horizontal bounce and the rest of the stuff
		else if(left != null) {
			bounceClip.play();
			vx = -vx;
			remove(left);
			bricksRemaining--;
			points += getPointValue(left);
		}

	}


	/** METHOD: Get Point Value
	 * ------------------------
	 * Takes in a GObject (a brick) and returns an integer that is dependent on the color of the brick. 
	 * Cyan bricks are worth 10 points, green ones are worth 15, yellow ones are worth 20, orange ones are worth 25, and red ones are worth 30.
	 */
	private int getPointValue(GObject brick) {

		if(brick.getColor() == Color.CYAN) {
			return 10;
		} else if(brick.getColor() == Color.GREEN) {
			return 15;
		} else if(brick.getColor() == Color.YELLOW) {
			return 20;
		} else if(brick.getColor() == Color.ORANGE) {
			return 25;
		} else if(brick.getColor() == Color.RED){
			return 30;
		} else {
			return 0;
		}

	}


	/** METHOD: Brick Above
	 * --------------------
	 * Returns a GObject, which is the brick located immediately above (to the point where it is touching) the ball, if it exists.
	 * If an object does not exist there, or exists but is something other than a brick, this method returns null.
	 */
	private GObject brickAbove() {

		double leftX = ball.getX();
		double rightX = leftX + 2 * BALL_RADIUS;
		double y = ball.getY();

		// store the objects located at the top right corner and top left corner of the ball
		GObject left = getElementAt(leftX, y);
		GObject right = getElementAt(rightX, y);

		// these booleans are true if the object is a brick (not null, not the paddle, and not the score or life board) 
		boolean leftCollision = left != null && left != paddle && left != scoreBoard && left != lifeAlert;
		boolean rightCollision = right != null && right != paddle && right != scoreBoard && right != lifeAlert;

		if(leftCollision) {
			return left;
		} else if(rightCollision) {
			return right;
		} else {
			return null;
		}

	}


	/** METHOD: Brick Below
	 * --------------------
	 * Returns a GObject, which is the brick located immediately below (to the point where it is touching) the ball, if it exists.
	 * If an object does not exist there, or exists but is something other than a brick, this method returns null.
	 */
	private GObject brickBelow() {

		double leftX = ball.getX();
		double rightX = leftX + 2 * BALL_RADIUS;
		double y = ball.getY() + 2 * BALL_RADIUS;

		// store the objects located at the bottom right corner and bottom left corner of the ball
		GObject left = getElementAt(leftX, y);
		GObject right = getElementAt(rightX, y);

		// these booleans are true if the object is a brick (not null, not the paddle, and not the score or life board) 
		boolean leftCollision = left != null && left != paddle && left != scoreBoard && left != lifeAlert;
		boolean rightCollision = right != null && right != paddle && right != scoreBoard && right != lifeAlert;

		if(leftCollision) {
			return left;
		} else if(rightCollision) {
			return right;
		} else {
			return null;
		}

	}


	/** METHOD: Brick Right
	 * --------------------
	 * Returns a GObject, which is the brick located immediately to the right of (to the point where it is touching) the ball, if it exists.
	 * If an object does not exist there, or exists but is something other than a brick, this method returns null.
	 */
	private GObject brickRight() {

		double topY = ball.getY();
		double bottomY = ball.getY() + 2 * BALL_RADIUS;
		double x = ball.getX() + 2 * BALL_RADIUS;

		// store the objects located at the top right corner and bottom right corner of the ball
		GObject top = getElementAt(x, topY);
		GObject bottom = getElementAt(x, bottomY);

		// these booleans are true if the object is a brick (not null, not the paddle, and not the score or life board) 
		boolean topCollision = top != null && top != paddle && top != scoreBoard && top != lifeAlert;
		boolean bottomCollision = bottom != null && bottom != paddle && bottom != scoreBoard && bottom != lifeAlert;

		if(topCollision) {
			return top;
		} else if(bottomCollision) {
			return bottom;
		} else {
			return null;
		}

	}


	/** METHOD: Brick Left
	 * --------------------
	 * Returns a GObject, which is the brick located immediately to the left of (to the point where it is touching) the ball, if it exists.
	 * If an object does not exist there, or exists but is something other than a brick, this method returns null.
	 */
	private GObject brickLeft() {

		double topY = ball.getY();
		double bottomY = ball.getY() + 2 * BALL_RADIUS;
		double x = ball.getX();

		// store the objects located at the top left corner and bottom left corner of the ball
		GObject top = getElementAt(x, topY);
		GObject bottom = getElementAt(x, bottomY);

		// these booleans are true if the object is a brick (not null, not the paddle, and not the score or life board) 
		boolean topCollision = top != null && top != paddle && top != scoreBoard && top != lifeAlert;
		boolean bottomCollision = bottom != null && bottom != paddle && bottom != scoreBoard && bottom != lifeAlert;

		if(topCollision) {
			return top;
		} else if(bottomCollision) {
			return bottom;
		} else {
			return null;
		}

	}


	/** METHOD: Hit Paddle
	 * -------------------
	 * Returns true if the ball has hit the paddle, and false if not
	 */
	private boolean hitPaddle() {

		// stores coordinates used for bottom left and right corners of the ball
		double y = ball.getBottomY();
		double leftX = ball.getX();
		double rightX = ball.getRightX();

		// stores the objects located at the bottom left corner and bottom right corner of the rectangle that contains the ball
		GObject leftCorner = getElementAt(leftX, y);
		GObject rightCorner = getElementAt(rightX, y);

		// if either of these objects is the paddle, we say that the ball has hit the paddle
		// and the method returns true
		if(leftCorner == paddle || rightCorner == paddle) {
			return true;
		} 
		// otherwise, the method returns false
		else {
			return false;
		}

	}


	/** METHOD: Hit Left Wall
	 * ----------------------
	 * Returns true if the x-coordinate of the left edge of the ball is less than or equal to zero 
	 * (ball is going off the edge of the screen to the left).
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}


	/** METHOD: Hit Top Wall
	 * ----------------------
	 * Returns true if the y-coordinate of the top of the ball is less than or equal to zero 
	 * (ball is going off the edge of the screen to the top).
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}


	/** METHOD: Hit Right Wall
	 * ----------------------
	 * Returns true if the x-coordinate of the right edge of the ball is greater than or equal to the width of the screen 
	 * (ball is going off the edge of the screen to the right).
	 */
	private boolean hitRightWall() {

		// x value of ball that would mean the ball is going off the right edge of the screen
		double x = getWidth() - ball.getWidth();

		return ball.getX() >= x;
	}


	/** METHOD: Hit Bottom Wall
	 * ----------------------
	 * Returns true if the y-coordinate of the bottom of the ball is greater than or equal to the height of the screen 
	 * (ball is going off the edge of the screen to the bottom).
	 */
	private boolean hitBottomWall() {

		// y value of ball that would mean the ball is going off the bottom edge of the screen
		double y = getHeight() - ball.getHeight();

		return ball.getY() >= y;
	}


	/** METHOD: Make Ball
	 * ------------------
	 * Creates a new GOval that is the ball and adds it to the center of the screen.
	 * This ball does not move.
	 */
	private void makeBall() {

		// (x,y) coordinates of the top left corner of the ball such that the ball is centered on the canvas
		double x = (getWidth() / 2) - BALL_RADIUS;
		double y = (getHeight() / 2) - BALL_RADIUS;

		// creates a new circular GOval that is centered on the canvas, has the BALL_RADIUS as its radius, and is filled grey
		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.GRAY);

		// adds the ball to the screen
		add(ball);

	}


	/** METHOD: Set Up
	 * ----------------
	 * Quite simply, sets up the game.
	 * This involves laying down the bricks and creating a paddle that tracks the mouse.
	 */
	private void setUp() {

		// pretty self-explanatory
		setBricks();
		setPaddle();

		makeBall();

		makeLifeLabel();
		makeScoreBoard();

		makeStartLabel();

	}


	/** METHOD: Make Score Board
	 * ------------------------
	 * Creates a label that displays how many points the player has accumulated and adds it to the top right corner of the screen.
	 */
	private void makeScoreBoard() {

		scoreBoard = new GLabel("Points: 0");

		// "10" and "30" offset the label a bit so it does not touch the edge of the screen (which would be ugly)
		scoreBoard.setLocation(getWidth() - (scoreBoard.getWidth() + 30), scoreBoard.getAscent() + 10);

		add(scoreBoard);

	}


	/** METHOD: Make Life Label
	 * ------------------------
	 * Creates a label that displays how many lives the player has left and adds it to the top left corner of the screen.
	 */
	private void makeLifeLabel() {

		lifeAlert = new GLabel("Lives Remaining: " + livesRemaining);

		// "10" offsets the label a bit so it is not touching the edge of the screen (which would be ugly)
		lifeAlert.setLocation(10, lifeAlert.getAscent() + 10);

		add(lifeAlert);

	}


	/** METHOD: Set Paddle
	 * --------------------
	 * Creates a rectangular paddle. 
	 * The paddle's x position starts at the left edge of the screen but changes as the user moves the mouse.
	 * The paddle's y position is always PADDLE_Y_OFFSET above the bottom of the screen.
	 */
	private void setPaddle() {

		// y value of paddle
		double y = getHeight() - PADDLE_Y_OFFSET;

		// creates a new, filled paddle and adds it to the screen
		paddle = new GRect(0, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

		// allows for mouseMoved method that updates the location of the paddle
		addMouseListeners();

	}


	/** METHOD: Mouse Moved
	 * This MouseEvent method tracks when the mouse moves and uses its location to update the location of the paddle.
	 * The paddle always has the same x-position of the mouse (given that that x-position would not make the paddle go off the screen).
	 */
	public void mouseMoved(MouseEvent e) {

		// stores x location of mouse
		double x = e.getX();

		// maximum and minimum x value the paddle can be centered around and still be on the screen
		double maxX = getWidth() - PADDLE_WIDTH;
		double minX = (PADDLE_WIDTH / 2);

		// updates the paddle's x location so that it is centered around the mouse
		// but makes sure that the paddle stays on the screen
		if(x > maxX) {
			paddle.setX(maxX);
		} else if(x < minX) {
			paddle.setX(0);
		} else {
			paddle.setX(x - (PADDLE_WIDTH / 2));
		}


	}


	/** METHOD: Set Bricks
	 * -------------------
	 * Sets up all the bricks at the top of the screen. 
	 * There are NBRICK_COLUMNS number of columns and NBRICK_ROWS number of rows. 
	 * Every brick is separated from the next by BRICK_SEP.
	 * Every two rows of bricks have a different color: in descending order this is red, orange, yellow, green, and cyan.
	 * The entire clump of bricks is located BRICK_Y_OFFSET down from the top of the screen and centered in the x-direction.
	 */
	private void setBricks() {

		// x and y coordinates of the first (top left brick)
		double x = (getWidth() - WIDTH_OF_BRICKS) / 2;
		double y1 = BRICK_Y_OFFSET;
		double y = y1;


		// outer for loop makes new columns
		for(int i = 0; i < NBRICK_COLUMNS; i++) {

			// inner for loop fills a column with bricks
			for(int j = 0; j < NBRICK_ROWS; j++) {

				// creates a new filled brick
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				// sets different colors for the brick depending on which row it is in
				if(j >= 0 && j < 2) {
					brick.setColor(Color.RED);
				} else if(j >= 2 && j < 4) {
					brick.setColor(Color.ORANGE);
				} else if(j >= 4 && j < 6) {
					brick.setColor(Color.YELLOW);
				} else if(j >= 6 && j < 8) {
					brick.setColor(Color.GREEN);
				} else if(j >= 8 && j < 10) {
					brick.setColor(Color.CYAN);
				}

				// adds the brick to the canvas
				add(brick);

				// next brick starts one brick-height and separations space below the previous one
				y = y + BRICK_HEIGHT + BRICK_SEP;

			}

			// y resets at initial value
			y = y1;

			// next column starts one brick-width and one separation space over from the previous one
			x = x + BRICK_WIDTH + BRICK_SEP;

		}

	}

}
