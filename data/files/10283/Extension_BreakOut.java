
/*
 * File: Extension_BreakOut.java
 * -------------------
 * Name: Jean-Akim Cameus
 * Section Leader: Maggie Davis
 * 
 * This file implements the game of Breakout, with the
 * addition of extensions. It sets the game up and 
 * the user can click the screen to start playing. This
 * game has an intro message and shows results at the end. 
 * It also keeps score and game turn number as the game
 * plays. THe horizontal velocity of the ball is double 
 * every 7th hit.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension_BreakOut extends GraphicsProgram {

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
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Initial number of bricks in the game
	public static final int TOTAL_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

	// Keeps track of the number of bricks that have been taken out
	double bricksTakenOut;

	// The gaming paddle and ball
	GRect paddle;
	GOval ball;

	// The rectangle that is returned when all four corners of the ball is void.
	GRect none;

	// Location of the mouse
	double mouseX;
	double mouseY;

	// Velocity of the ball
	private double vx, vy;

	// Location of the ball
	double ball_X, ball_Y;

	// Random generator for the velocity of the ball
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Label that the final result of the game to the users.
	private GLabel resultLabel = new GLabel("");

	// Bounce audio clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// Playing Music
	AudioClip playingMusic = MediaTools.loadAudioClip("IntroEyeoftheTiger1.wav");

	// intro Music
	AudioClip introMusic = MediaTools.loadAudioClip("intro music.mp3");

	// Background image
	GImage background;

	// label that shows score
	private GLabel labelScore = new GLabel("");

	// stores the score
	int score;

	// label that shows the turn out of three
	private GLabel labelTurn = new GLabel("");

	// stores the turn number
	int turn;

	// keeps track of number of paddle hits
	int paddleHits;

	/*
	 * This method sets up the game and allows the user to play and display the
	 * result afterwards.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		showIntro();
		setUpGame();
		gameInMotion();
		removeAll();
		addResult();
	}

	/*
	 * This shows the introductory message to the game and plays a chill music to
	 * begin with.
	 */
	private void showIntro() {
		introMusic.play();
		GLabel introMessage = new GLabel("Welcome to Akim's 'otherworldly' Game");
		// centers the introductory message
		double x = getWidth() / 2 - introMessage.getWidth() / 2;
		double y = getHeight() / 2 - introMessage.getHeight();
		add(introMessage, x, y);
		waitForClick();
		introMusic.stop(); // stops intro music, so it does not overlap with other music
		removeAll();
	}

	/*
	 * Sets the game up by adding the background, the bricks and adding the paddle
	 * to the screen.
	 */
	private void setUpGame() {
		score = 0;
		addBackground();
		createBricks();
		addPaddle();
	}

	/*
	 * Adds the background image on the entire screen.
	 */
	private void addBackground() {
		background = new GImage("45850.jpg");
		background.setBounds(0, 0, getWidth(), getHeight());
		add(background);
	}

	/*
	 * Creates the blocks of bricks row by row. It also centers the bricks on the
	 * screen. It builds rows until it reaches the preassigned constant N_ROWS
	 * value.
	 */
	private void createBricks() {
		double xCoordinate = ((getWidth() - (10 * BRICK_WIDTH + 9 * BRICK_SEP)) / 2);
		double yCoordinate = BRICK_Y_OFFSET;
		for (int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			buildRow(xCoordinate, yCoordinate, rowNumber);
			yCoordinate += (BRICK_HEIGHT + BRICK_SEP);
		}
	}

	/*
	 * Builds a row of brick. It determines the amount of bricks in the row by the
	 * preassigned value of the constant N_COLUMNS. It also determines the color of
	 * the row based on the number of the rows, and the particular row's fraction
	 * (position) to the others.
	 */
	private void buildRow(double xCoordinate, double yCoordinate, double rowNumber) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			if (rowNumber < NBRICK_ROWS / 5) {
				brick.setColor(Color.RED);
			} else if (rowNumber < 2 * NBRICK_ROWS / 5) {
				brick.setColor(Color.ORANGE);

			} else if (rowNumber < 3 * NBRICK_ROWS / 5) {
				brick.setColor(Color.YELLOW);
			} else if (rowNumber < 4 * NBRICK_ROWS / 5) {
				brick.setColor(Color.GREEN);
			} else {
				brick.setColor(Color.CYAN);
			}
			add(brick);
			xCoordinate += (BRICK_WIDTH + BRICK_SEP);
		}
	}

	/*
	 * Add the paddle to the screen. The width and height are predetermined
	 * constants. The X-Coordinate is taken from the mouse (moved) event method.
	 */
	private void addPaddle() {
		double y = getHeight() - BRICK_Y_OFFSET;
		paddle = new GRect(mouseX, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.WHITE);
		add(paddle);
	}

	/*
	 * Keeps track of the amount of turns the player has and stops playing after the
	 * number has been reached. It also plays the playing music, the first 40 sec of
	 * a popular song.
	 */
	private void gameInMotion() {
		addMouseListeners();
		playingMusic.play();
		for (int i = 0; i < NTURNS; i++) {
			if (bricksTakenOut < TOTAL_BRICKS) {
				playGame();
				add(paddle, mouseX, mouseY);
			}
		}
	}

	/*
	 * Keeps track of track of the mouse and sets the X-coordinate of the location
	 * where the mouse is. The Y-Coordinate of the paddle is fixed.
	 */
	public void mouseMoved(MouseEvent e) {
		mouseY = getHeight() - BRICK_Y_OFFSET;
		mouseX = e.getX();
		if (mouseX >= PADDLE_WIDTH / 2 && mouseX <= getWidth() - PADDLE_WIDTH / 2) {
			paddle.setCenterLocation(mouseX, mouseY);
		}
	}

	/*
	 * This creates the ball, sets it into motion, makes it bounce, and checks for
	 * collision.
	 */
	private void playGame() {
		GOval ball = drawBall();

		waitForClick();
		// The velocity of the ball
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = VELOCITY_X_MAX;
		// Doubles the horizontal velocity on the 7th hits of the paddle
		if (paddleHits != 0 && paddleHits % 7 == 0) {
			vx = 2 * vx;
		}
		turn++;
		updateMethod(ball);
	}

	/*
	 * Draws a ball centered on the screen. The Height and width are twice the
	 * preassigned radius constant.
	 */
	private GOval drawBall() {
		ball_X = getWidth() / 2 - BALL_RADIUS;
		ball_Y = getHeight() / 2 - BALL_RADIUS;

		ball = new GOval(ball_X, ball_Y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.WHITE);
		add(ball);
		return ball;
	}

	/*
	 * This is the update portion of the ball animation loop, which has velocity
	 * changes and checking for collision.
	 */
	private void updateMethod(GOval ball) {
		while (true) {
			if (bricksTakenOut == TOTAL_BRICKS) {
				break;
			}
			// Bouncing motion
			ball.move(vx, vy);
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitRightWall(ball) || hitLeftWall(ball)) {
				vx = -vx;
			}
			if (hitBottomWall(ball)) {
				remove(paddle);
				remove(ball);
				break;
			}
			pause(DELAY);
			checkForCollision();
			addScore_Turn();
		}
	}

	/*
	 * Checks if ball hits the top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	/*
	 * Checks if ball hits the right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getRightX() > getWidth();
	}

	/*
	 * Checks if ball hits the left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	/*
	 * Checks if ball hits the bottom wall
	 */
	private boolean hitBottomWall(GOval ball2) {
		return ball.getBottomY() > getHeight();
	}

	/*
	 * This methods adds the overall game score and the turn number. There are a
	 * total of 3 turns in the game
	 */
	private void addScore_Turn() {
		labelScore.setLabel("Score: " + score);
		labelScore.setColor(Color.WHITE);
		labelTurn.setLabel("Turn: " + turn);
		labelTurn.setColor(Color.WHITE);
		// adds score on bottom left
		add(labelScore, 0, getHeight() - labelScore.getHeight());
		// adds turn on bottom right
		add(labelTurn, getWidth() - labelTurn.getWidth(), getHeight() - labelScore.getHeight());
	}

	/*
	 * Checks if the collider is the paddle or the bottom wall. If the paddle the
	 * game continues and if not, they lose a turn out of the initial three.
	 */
	private void checkForCollision() {
		GObject collider = getCollidingObject();
		if (collider != background) {
			if (collider == paddle) {
				bounceClip.play();
				paddleHits++;
				if (vy < 0) {
					vy = -(-vy);
				} else {
					vy = -vy;
				}
			} else if (collider != null && collider != paddle && collider != labelTurn && collider != labelScore) {
				vy = -vy;
				bounceClip.play();
				remove(collider);
				determineScore(collider);
				bricksTakenOut++;
			}
		}
	}

	/*
	 * Determines what the score change should be, depending on the color of the
	 * brick being removed.
	 */
	private void determineScore(GObject collider) {
		if (collider.getColor() == Color.CYAN) {
			score += 1;
		} else if (collider.getColor() == Color.GREEN) {
			score += 2;
		} else if (collider.getColor() == Color.YELLOW) {
			score += 3;
		} else if (collider.getColor() == Color.ORANGE) {
			score += 4;
		} else {
			score += 5;
		}
	}

	/*
	 * Checks for collision at all of four corners of the ball (Top left, top right,
	 * bottom left, and bottom right). If they are not null, they are returned and
	 * the ball velocity is determined thereon.
	 */
	private GObject getCollidingObject() {
		GObject possibleCollider1 = getElementAt(ball.getX(), ball.getY());
		GObject possibleCollider2 = getElementAt(ball.getRightX(), ball.getY());
		GObject possibleCollider3 = getElementAt(ball.getX(), ball.getBottomY());
		GObject possibleCollider4 = getElementAt(ball.getRightX(), ball.getBottomY());
		if (possibleCollider1 != background) {
			return possibleCollider1;
		} else if (possibleCollider2 != background) {
			return possibleCollider2;
		} else if (possibleCollider3 != background) {
			return possibleCollider3;
		} else if (possibleCollider4 != background) {
			return possibleCollider4;
		} else {
			return none;
		}
	}

	/*
	 * Adds the result of the game after it is done playing. You either win or lose.
	 * You can only win by hitting all of the bricks
	 */
	private void addResult() {
		if (bricksTakenOut < TOTAL_BRICKS) {
			resultLabel.setLabel("Gameover!! Sorry, it sucks to lose!");
		} else {
			resultLabel.setLabel("Yay!! Congrats, you're a winner!!");
		}
		// Centers the label
		double x = (getWidth() / 2 - resultLabel.getWidth() / 2);
		double y = (getHeight() / 2 - resultLabel.getHeight() / 2);
		add(resultLabel, x, y);
	}
}