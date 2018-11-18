/*
 * File: Breakout.java
 * -------------------
 * Name: Elyssa Hofgard
 * Section Leader: Shanon Reckinger
 * 
 * This program extends the basic version of the game Breakout.
 * It will play an audio clip every time the ball hits a brick or the paddle.
 * The user will be able to click to enter the game and to click to 
 * start each turn. Information will be displayed if the loser wins or 
 * loses. The number of turns left will be displayed, and the score will 
 * be displayed.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for the paddle
	private GRect paddle = null;

	// Instance variable for the ball.
	private GOval ball = null;

	// Instance variable for the score.
	private GLabel score = null;

	// Instance variable for the number of lives.
	private GLabel displaylives = null;

	// Instance variable for the score keeper.
	private int count = 0;

	// Instance variable for the number of lives.
	private int lives = NTURNS;

	// Instance variable for the random-number generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Instance variables for the x and y velocities.
	private double vx, vy;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Sets up the world for the game.
		setUpGame();

		// Allows the user to play the game.
		playGame();

		// Adds mouse listeners to respond to paddle movement.
		addMouseListeners();
	}

	/*
	 * Plays the game for three turns and waits for the player's click to start the next turn.
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			/*
			 * If the player still has turns remaining, this will display a label with the number of turns.
			 * The label will disappear if the player double clicks, and the next turn will start.
			 */
			if (i != 0) {
				GLabel turn = new GLabel("You have " + (NTURNS-i) +" turns remaining. Double click to try again.");
				double x = getWidth() / 2 - turn.getWidth() / 2;
				double y = getHeight()/2;
				turn.setColor(Color.BLUE);
				add(turn, x, y);
				waitForClick();
				remove(turn);
				waitForClick();
			}

			ball = makeBall();
			animateBall(ball);

			// Displays the number of turns or lives remaining as a counter in the bottom right corner.
			lives --;
			displaylives.setLabel("Lives:" + lives);
		}

		// Displays a label if the player does not remove all of the bricks by the end of three turns.
		displayLoss();
	}

	/*
	 * Sets up the game with the rows of bricks, the paddle,
	 * a label with the score of the player, the beginning message,
	 * and the number of lives remaining.
	 */
	private void setUpGame() {
		setUpRectangles();
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		addPaddletoScreen();
		scoreLabel();
		displayLives();
		displayBeginning();
	}

	/*
	 * Creates a label to display the number of turns remaining.
	 */
	private void displayLives() {
		displaylives = new GLabel ("Lives: " + NTURNS);
		displaylives.setColor(Color.RED);
		double x = getWidth() - displaylives.getWidth();
		double y = getHeight() - PADDLE_HEIGHT;
		add(displaylives, x, y);
	}

	/*
	 * Sets up and colors the rows of bricks at the top of the screen.
	 * Loops over the rows and the columns.
	 */
	private void setUpRectangles() {
		// Creates each row of bricks.
		for (int r = 0; r < NBRICK_ROWS; r++) {
			double y = BRICK_Y_OFFSET + BRICK_HEIGHT*r + BRICK_SEP*r;

			// Creates each column of bricks.
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = getWidth() / 2 - (NBRICK_COLUMNS / 2.0) * (BRICK_WIDTH + BRICK_SEP) + (BRICK_WIDTH + BRICK_SEP) * c + BRICK_SEP / 2;

				// Creates a rectangle with given coordinates, fills the rectangle, and adds it to the screen.
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				add(rect);

				// Fills the rows of bricks. This color pattern will repeat for a given number of bricks.
				if (r % 10 == 0 || r % 10 == 1) {
					rect.setColor(Color.RED);
				}
				else if (r % 10 == 2 || r % 10 == 3) {
					rect.setColor(Color.ORANGE);
				}
				else if (r % 10 == 4 || r % 10 == 5) {
					rect.setColor(Color.YELLOW);
				}
				else if (r % 10 == 6 || r % 10 == 7) {
					rect.setColor(Color.GREEN);
				}
				else {
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	/*
	 * The paddle will follow the movement of the mouse.
	 * The mouse must be moved for the program to enter this method.
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() < getWidth() && e.getX() > PADDLE_WIDTH) {
			double x = e.getX() - PADDLE_WIDTH;
			double y = getHeight() - PADDLE_Y_OFFSET;
			paddle.setLocation(x, y);
		}
	}

	/*
	 * Creates the label displayed at the beginning of the game.
	 * Waits for the click of the player to remove the label.
	 */
	private void displayBeginning() {
		GLabel beginning = new GLabel("Welcome to Breakout! Click to play. You will have three turns");
		double x = getWidth() / 2 - beginning.getWidth() / 2;
		double y = getHeight() / 2;
		beginning.setColor(Color.BLUE);
		add(beginning, x, y);
		waitForClick();
		remove(beginning);	
	}

	/*
	 * Creates the label displayed if the player wins.
	 */
	private void displayWin() {
		GLabel win = new GLabel("Congrats! You win! :)");
		double x = getWidth() / 2 - win.getWidth() / 2;
		double y = getHeight() / 2;
		win.setColor(Color.BLUE);
		add(win, x, y);
	}

	/*
	 * Creates the label displayed if the player loses.
	 */
	private void displayLoss() {
		GLabel loss = new GLabel("Sorry! Better luck next time! :(");
		double x = getWidth() / 2 - loss.getWidth() / 2;
		double y = getHeight() / 2;
		loss.setColor(Color.RED);
		add(loss, x, y);
	}

	/*
	 * Creates the label to keep track of the player's score.
	 */
	private void scoreLabel() {
		score = new GLabel ("Score: 0");
		score.setColor(Color.RED);
		double x = 0;
		double y = getHeight() - PADDLE_HEIGHT;
		score.setColor(Color.RED);
		add(score, x, y);
	}

	/*
	 * Adds the paddle to the screen with defined x and y coordinates.
	 */
	private void addPaddletoScreen() {
		double x = getWidth() / 2 - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	/*
	 * Makes the ball for the game.
	 */
	private GOval makeBall() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(x, y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	/*
	 * Returns a boolean for if the ball hits the bottom wall.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * Returns a boolean for if the ball hits the top wall.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Returns a boolean for if the ball hits the right wall.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-ball.getWidth();
	}

	/* 
	 * Returns a boolean for if the ball hits the left wall.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/*
	 * Updates the velocity of the ball. The ball must be moving.
	 * If the ball hits the left wall or the right wall, the direction of its x velocity will reverse.
	 * If the ball hits the top wall, the direction of its y velocity will reverse.
	 */
	private void updateVelocity(GOval ball) {
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}
	}

	/*
	 * Animates the ball with audio and updates the player's score.
	 */
	private void animateBall(GOval ball) {
		// Variable for the original number of bricks.
		int brickcount = NBRICK_COLUMNS * NBRICK_ROWS;

		// Loads the audio clip to play when the ball hits a brick or the paddle.
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		// Random x velocity for given bounds.
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		//The x velocity will be negative half of the time.
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}

		// The original y velocity.
		vy = VELOCITY_Y;

		/*
		 * If the ball is on the screen, will update the y velocity components.
		 * Depends if the ball hits a paddle or a brick.
		 */
		while(ball.getY()<=getHeight()) {
			// Updates the velocity.
			updateVelocity(ball);

			// Gets the object that the ball collided with.
			GObject collider = getCollidingObject();

			/*
			 * If the colliding object is the paddle, makes the ball bounce off of it.
			 * Accomplishes this by reversing direction of y velocity.
			 */
			if (collider == paddle) {
				// Plays the audio clip.
				bounceClip.play();
				if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS * 2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS * 2 + 3) {
					vy = -vy;
				}
			}

			/*
			 * If the collider is a brick, it will be removed.
			 * Needed to make sure that the score label and the label for
			 * the number of turns were not removed.
			 */
			else if (collider!=null && collider != score && collider != displaylives) {
				// Plays the audio for hitting an object.
				bounceClip.play();

				// Removes the brick if the ball hits it.
				remove(collider);

				// Reverses the direction of the y velocity.
				vy = -vy;

				// Updates the count for the number of bricks that have been hit by the ball.
				brickcount--;

				// Updates the count for the score label.
				count++;
				// Sets the label to display how many bricks have been hit.
				score.setLabel("Score: " + count);

				// Removes the ball and displays the message for a win if no bricks are left.
				if (brickcount == 0) {
					remove(ball);
					displayWin();
				}
			}

			// If the ball hits the bottom wall, it will appear to fall off of the screen.
			if (hitBottomWall(ball)) {
				remove(ball);
			}

			// Animates the ball from given x and y velocities.
			ball.move(vx, vy);	
			pause(DELAY);
		}
	}

	/*
	 * Returns the object that the ball has collided with.
	 * Uses the bounding rectangle of the ball to check different points of collision.
	 */
	private GObject getCollidingObject() {
		// Checks if there is an element at the upper left corner of the ball.
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return (getElementAt(ball.getX(), ball.getY()));
		}

		// Checks if there is an element at the upper right corner.
		else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
		}

		// Checks if there is an element at the lower left corner.
		else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS));
		}

		// Checks if there is an element at the lower right corner.
		else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			return (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));

		} else {
			return null;
		}
	}
}
