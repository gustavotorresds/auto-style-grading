
/*
 * File: Breakout2.java
 * -------------------
 * Name: Dan Shevchuk
 * Section Leader: Ella Tessier-Lavigne
 * 
 * Creates a game of Breakout where the player controls a paddle and tries to hit a ball
 * so that it removes all the bricks on the screen.
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout2 extends GraphicsProgram {

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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Font to use for on-screen text
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	// Instance variables for the paddle, ball, bricks, blankScreen, and number of turns.
	GRect brick = null;
	GRect paddle = null;
	GOval ball = null;
	GRect blankScreen = null;
	GLabel startMessage = null;
	GLabel finalScore = null;
	private int turns = NTURNS;

	// Creates a random generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Score represents how many bricks were hit.
	int score = 0;

	// Doubles for the x and y velocity of the ball.
	private double vx = VELOCITY_X_MIN;
	private double vy = VELOCITY_Y;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//Doubles for the center x and bottom y of the screen.
		double bottom = getHeight();
		double cx = getWidth() / 2;

		setUpGame(cx, bottom);
		runGame(cx, bottom);

		addMouseListeners();
	}

	// Makes the rows of GRect bricks and then creates a GRect paddle
	public void setUpGame(double cx, double bottom) {
		makeRows(cx, bottom);
		makePaddle(cx, bottom);
	}

	private void makeBlankScreen(double cx, double bottom) {
		blankScreen = new GRect (0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		blankScreen.setColor(Color.WHITE);
		blankScreen.setFilled(true);
		add (blankScreen);
	}
	
	private void makeStartMessage(double cx, double bottom) {
		GLabel startMessageTest = new GLabel ("Welcome to Breakout! Click to start!");
		startMessage = new GLabel ("Welcome to Breakout! Click to start!", cx - startMessageTest.getWidth()/2,
				(bottom/2) - (startMessageTest.getAscent()/2));
		startMessage.setFont("SCREEN_FONT");
		add(startMessage);
	}
	
	/* Creates the multicolored rows of bricks for the ball to remove. Does this
	 * through a nested for loop and then colors the first ten rows.
	 */
	private void makeRows(double cx, double bottom) {
		double xpos = cx - (BRICK_WIDTH * (NBRICK_COLUMNS / 2)) - (BRICK_WIDTH / 2);
		double ypos = BRICK_Y_OFFSET;
		for (int a = 0; a < NBRICK_COLUMNS; a++) {
			for (int b = 0; b < NBRICK_ROWS; b++) {
				brick = new GRect(xpos + BRICK_SEP * a + BRICK_WIDTH * a, ypos + BRICK_HEIGHT * b + BRICK_SEP * b,
						BRICK_WIDTH, BRICK_HEIGHT);
				if (b < 2) {
					brick.setColor(Color.RED);
				}
				if (b >= 2 && b < 4) {
					brick.setColor(Color.ORANGE);
				}
				if (b >= 4 && b < 6) {
					brick.setColor(Color.YELLOW);
				}

				if (b >= 6 && b < 8) {
					brick.setColor(Color.GREEN);
				}

				if (b >= 8 && b < 10) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add(brick);
			}
		}
	}

	// makes a GRect paddle near the bottom of the screen.
	private void makePaddle(double cx, double bottom) {
		paddle = new GRect(cx, bottom - 2 * PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/* When the user moves their mouse, the paddle changes its x position in
	 * response. The y position is not altered.*/
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double ypos = paddle.getY();
		movePaddle(x, ypos);
	}

	/* Part of the prior mouse event. If the mouse goes off the screen, the
	 * paddle is moved by twice its width to stop it from going off screen.
	 * 
	 * Pre: The x position of the paddle is beyond the right wall.
	 * Post: The paddle is moved back by twice its width away from the right wall. */
	private void movePaddle(double x, double ypos) {
		paddle.setLocation(x, ypos);
		if (x >= CANVAS_WIDTH - PADDLE_WIDTH) {
			double saveX = CANVAS_WIDTH - PADDLE_WIDTH;
			paddle.setLocation(saveX, ypos);
		}
	}

	// Creates a ball and starts the game.
	public void runGame(double cx, double bottom) {
		createBall(cx, bottom);
		makeBlankScreen(cx, bottom);
		makeStartMessage(cx,bottom);
		waitForClick();
		remove(blankScreen);
		remove(startMessage);
		startGame();
	}

	// Creates a GOval ball.
	private void createBall(double cx, double bottom) {
		ball = new GOval(cx - 2 * BALL_RADIUS, bottom / 2, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/* Starts the game by creating a display of the number of turns left,
	 * waiting for the user to click, and starting the animation loop.
	 * While the ball does not touch the bottom wall, the animation
	 * continues.
	 * 
	 *  Pre: The upper left corner of the ball is not touching the bottom wall
	 *  Post: The ball continues moving through an animation look*/
	private void startGame() {
		displayTurnsLeft();
		waitForClick();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (!touchBottomWall(ball, vy)) {
			ball.move(vx, vy);
			checkForCollisions();
			checkForWalls();
			pause(DELAY);
		}

	}

	//Instance variable for the GLabel that says how many turns are left.
	private GLabel turnsLeft = null;
	
	//Audio clip of ball bouncing.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	/* Creates a collider object when the ball hits an object.
	 * Then if the object is not null and is not
	 * the turnsLeft GLabel, the method checks if the collider is a paddle.
	 * If it is, the vy of the ball is reversed, simulating a bounce, and 
	 * the collider object returns to null again. If else, that means the 
	 * collider object is a brick and so the ball vy is reversed again while
	 * the collider object is removed and becomes null again. Finally,
	 * the score increases with each additional brick eliminated, and if 
	 * the score equals the total amount of
	 * bricks on the screen, that means the user has won and a win message is 
	 * put on screen.
	 * 
	 * Pre: The ball has hit an object and the collider object is not null and not the GLabel. 
	 * For the last if statement, the score is 100.
	 * Post: The ball has reversed its y direction and if the collider.
	 * is a brick then that said brick is removed. If the score equals the total amount of
	 * bricks on the screen, a win message is printed.*/
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null && collider != turnsLeft) {
			if (collider == paddle) {
				vy = -vy;
				collider = null;
			} else {
				bounceClip.play();
				vy = -vy;
				vx = vx + 0.2;
				remove(collider);
				collider = null;
				score++;
				if (score == NBRICK_COLUMNS * NBRICK_ROWS) {
					winOrLoseMessage();
				}
			}
		}
	}
	
	/*Checks to see if the ball has hit any of the walls. If it hits the top wall, 
	 * the y direction of the ball is reversed. If it hits the left or right wall,
	 * the x direction of the ball is reversed. But if the ball hits the bottom wall,
	 * the method lessens the number of turns left by 1. If turns equal 0, then 
	 * the player has lost and a lose message is printed. Otherwise,
	 * the game restarts by removing the ball and creating it again in its original 
	 * location and the player gets one more try.
	 * 
	 *  Pre: The ball has touched a wall of the screen.
	 *  Post: The ball direction is somehow reversed and depending on the 
	 *  amount of turns left, either prints a lose message and restarts the game
	 *  so the player has another chance.*/
	private void checkForWalls() {
		if (touchLeftWall(ball, vx) || touchRightWall(ball, vx))
			vx = -vx;

		if (touchTopWall(ball, vy))
			vy = -vy;

		if (touchBottomWall(ball, vy)) {
			turns = turns - 1;
			if (turns == 0) {
				remove(turnsLeft);
				makeBlankScreen(0,0);
				displayFinalScore();
				winOrLoseMessage();
			} else {
				remove(turnsLeft);
				remove(ball);
				createBall(getWidth() / 2, CANVAS_HEIGHT);
				startGame();
			}
		}
	}
	
	/*Four booleans that check if any of the four corners of the ball have 
	 * gone past the screen. 
	 * 
	 * Pre: either the x or the y coordinates (or both) of the ball have gone
	 * past the screen.
	 * Post: Returns false if not gone past the screen, returns true if yes.*/ 
		private boolean touchRightWall(GOval ball, double vx) {
			if (vx < 0)
				return false;
			return ball.getX() >= getWidth() - ball.getWidth();
		}

		private boolean touchLeftWall(GOval ball, double vx) {
			if (vx > 0)
				return false;
			return ball.getX() <= 0;
		}

		private boolean touchTopWall(GOval ball, double vy) {
			if (vy > 0)
				return false;
			return ball.getY() <= 0;
		}

		private boolean touchBottomWall(GOval ball, double vy) {
			if (vy < 0)
				return false;
			return ball.getY() >= getHeight() - ball.getHeight();
		}

	//Creates a GLabel near the bottom of the screen that says how many turns the player has left.
	private void displayTurnsLeft() {
		GLabel turnsLeftTest = new GLabel ("Turns left:" + turns);
		turnsLeft = new GLabel("Turns left: " + turns, (getWidth() / 2) - (turnsLeftTest.getWidth() / 2),
				getHeight() - (turnsLeftTest.getHeight()/2));
		turnsLeft.setFont("SCREEN_FONT");
		add(turnsLeft);
	}
	
	private void displayFinalScore() {
		GLabel finalScoreTest = new GLabel ("Turns left:" + turns);
		finalScore = new GLabel("Final score: " + score, (getWidth() / 2) - (finalScoreTest.getWidth() / 2),
				(getHeight()/2) - (finalScoreTest.getHeight()/2));
		finalScore.setFont("SCREEN_FONT");
		add(finalScore);
	}
	
	/*Prints a lose or win message on the screen depending on if the score 
	 * is equal to the number of bricks (win) on the screen or less (loss).
	 * 
	 * Pre: The score is equal to the number of on screen bricks or less.
	 * Post: A win or lose message is printed. */
	private void winOrLoseMessage() {
		GLabel winMessageTest = new GLabel("You won!");
		GLabel winMessage = new GLabel("You won!", (getWidth() / 2) - (winMessageTest.getWidth()/ 2), 
				getHeight() / 2 - (3 * winMessageTest.getHeight()));
		winMessage.setFont("SCREEN_FONT");

		GLabel loseMessageTest = new GLabel("You lose plebe!");
		GLabel loseMessage = new GLabel("You lose plebe!", (getWidth() / 2) - (loseMessageTest.getWidth() / 2), 
				getHeight() / 2 - (3 * loseMessageTest.getHeight()));
		loseMessage.setFont("SCREEN_FONT");

		GLabel message = null;

		if (score == NBRICK_COLUMNS * NBRICK_ROWS) {
			message = winMessage;
		} else {
			message = loseMessage;
		}
		add(message);
	}
	
	/*Checks the four corners of the ball and sees if any of them have touched 
	 * another object. If so, the method returns a GObject of the collider.*/
	private GObject getCollidingObject() {
		GObject maybeAnObject = getElementAt(ball.getX(), ball.getY());
		if (maybeAnObject != null)
			return maybeAnObject;
		maybeAnObject = getElementAt(ball.getX() + ball.getWidth(), ball.getY());
		if (maybeAnObject != null)
			return maybeAnObject;
		maybeAnObject = getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight());
		if (maybeAnObject != null)
			return maybeAnObject;
		maybeAnObject = getElementAt(ball.getX(), ball.getY() + ball.getHeight());
		if (maybeAnObject != null)
			return maybeAnObject;

		return null;
	}
}