/*
 * Name:Coco Ramgopal
 * Section Leader: Niki Agrawal
 * 
 * File: Breakout.java
 * -------------------
 * This file implements Breakout, a game where the goal is to have the ball
 * bounce off the paddle to hit the bricks and remove them from the screen. 
 * The game ends when the player destroys all bricks.
 * 
 * Extensions:
 * -----------
 *     - More realistic ball movement for brick and paddle collisions
 *     - Pause on 'p' key press
 *     - Lives and score counters
 *     - Score varies based on brick color
 *     - Display instructions before game
 *     - Play again feature
 * 
 * Note:
 * -----
 * In order to deal with various bugs from the event listeners and loops,
 * I created a buffer function that pauses for 0 ms.
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

	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Game objects.
	private GRect paddle;
	private GOval ball;
	private GRect yesButton, noButton;
	
	// Game values.
	private double vx, vy;
	private int bricksRemaining;
	private int lives;
	private int score;

	// Text labels
	private GLabel mainText = new GLabel ("");
	private GLabel livesLabel = new GLabel("");
	private GLabel scoreLabel = new GLabel("");

	// Keeps track of current game state to enable and disable certain functionality.
	private int gameState;
	
	// Game states
	private final int START 	 = 0;
	private final int PLAYING    = 1;
	private final int PAUSED     = 2;
	private final int PLAY_AGAIN = 3;
	private final int END        = 4; 

	
	public void run() {
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); // Set the canvas size.  In your code, remember to ALWAYS use getWidth() and getHeight() to get the screen dimensions, not these constants!
		introduction();
		
		while (gameState != END) {
			buffer();

			if (gameState == START) {
				removeAll();
				setup();
				boolean gameResult = playGame();
				endGame(gameResult);
			}
		}
		
		exitGame();
	}
	
	// Cleans up window and displays exit message.
	private void exitGame() {
		removeAll();
		GLabel bye = new GLabel("Play again soon! (Check++ please)");
		bye.setFont("SanSerif-italic-18");
		bye.setCenterLocation(getWidth()/2, getHeight()/2);
		add(bye);
	}
		
	// Sets up game objects and values for new game.
	private void setup() {
		gameState = START;
		score = 0;
		lives = NTURNS;
		bricksRemaining = NBRICK_ROWS * NBRICK_COLUMNS;

		addBricks();
		addPaddle();
		addBall();
		addLabels();
		addMouseListeners();
	}
	
	// Adds labels to graphics window.
	private void addLabels() {
		// Add main text.
		updateMainText("Click Mouse To Start.");
		add(mainText);
		
		// Add lives.
		livesLabel.setLabel("Lives Remaining: " + lives);
		livesLabel.setX(10);
		livesLabel.setBottomY(getHeight() - 10);
		livesLabel.setFont("SanSerif-plain-11");
		add(livesLabel);
		
		// Add score.
		scoreLabel.setLabel("Score: " + score);
		scoreLabel.setRightX(getWidth() - 20);
		scoreLabel.setBottomY(getHeight() - 10);
		scoreLabel.setFont("SanSerif-plain-11");
		add(scoreLabel);
	}

	// Displays welcome message and instructions.
	private void introduction() {
		add(mainText);

		updateMainText ("Welcome to Breakout!");
		pause(2500);

		updateMainText ("Use your mouse to move the paddle.");
		pause (2500);

		updateMainText ("Press \"P\" to pause game.");
		pause(2500);	
	}
	
	// Creates the entire block of bricks.
	private  void addBricks() {

		// Initial y-coordinate.
		double y = BRICK_Y_OFFSET;
		for (int row = 0; row < NBRICK_ROWS; row++) {
			
			// Initial x-coordinate.
			double x = getWidth()/2 - (double) NBRICK_COLUMNS/2 * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP/2;
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				add(createBrick(col, row, x, y));
				
				// Changes x coordinate per column. 
				x += BRICK_WIDTH + BRICK_SEP;
			}
			
			//Changes y coordinate per row.
			y += (BRICK_HEIGHT + BRICK_SEP);  
		}
	}
	
	// Returns brick color based on row number.
	private Color getColor(int row){
		if (row < 2)  return Color.RED;
		if (row < 4)  return Color.ORANGE;
		if (row < 6)  return Color.YELLOW;
		if (row < 8)  return Color.GREEN;
		if (row < 10) return Color.CYAN;
		return null;
	}
	
	// Returns a brick.
	private GRect createBrick (int bricksInRow, int row, double x, double y) {
		GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(getColor((row) % NBRICK_ROWS));
		return rect;
	}
		
	// Creates paddle and adds to graphics window.
	private void addPaddle() {
		double paddleX = (getWidth()/2 - PADDLE_WIDTH/2);
		double paddleY = (getHeight() - PADDLE_Y_OFFSET);
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled (true);
		add (paddle);
	}
	
	// Creates ball and adds to graphics window.
	private void addBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add (ball, ((getWidth()/2 - BALL_RADIUS)), (getHeight()/2 +BALL_RADIUS));
	}
	
	// Runs game for NTURNS and returns the game result.
	private boolean playGame() {
		for (int lives = NTURNS; lives > 0; lives--) {
			resetTurn();
			if (playTurn()) return true;
		}
		return false;
	}
	
	// Resets the game objects for the next turn.
	private void resetTurn() {
		gameState = PAUSED;
		ball.setCenterLocation(getWidth()/2, getHeight()/2);
		paddle.setCenterLocation(getWidth()/2, getHeight() - PADDLE_Y_OFFSET);

		waitForClick();

		mainText.setVisible(false);
		gameState = PLAYING;
	}
	
	// Plays a turn of the game and returns the result of the turn.
	private boolean playTurn() {
		// Set initial ball velocities.
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		while (bricksRemaining > 0) {

			// Don't move the ball while the game is paused.
			if (gameState == PAUSED) {
				buffer();
				continue;
			}
			
			checkCollisions();
			ball.move(vx, vy);

			if (lifeLost()) {
				livesLabel.setLabel("Lives Remaining: " + --lives);
				return false;
			}

			pause(DELAY);
		}

		return true;
	}
	
	// Checks if ball collides with any boundary or object, and changes velocity accordingly.
	private void checkCollisions() {

		// Horizontal walls
		if (ball.getX() <=0 || ball.getRightX() >= getWidth())
			vx = -vx;
		
		// Vertical walls 
		if (ball.getY() <= 0)
			vy = -vy;

		objectCollision(); 
	}
	
	// Handles collision with GObjects.
	private void objectCollision () {

		// Check corners around ball for element.
		for (int dx = 0; dx <= 2*BALL_RADIUS; dx += 2*BALL_RADIUS) {
			for (int dy = 0; dy <= 2*BALL_RADIUS; dy += 2*BALL_RADIUS) {
				GObject collider = getElementAt(ball.getX() + dx, ball.getY() + dy);
				
				// Ball ignores all labels.
				if (collider != null && collider != mainText && collider != scoreLabel && collider != livesLabel) {
					if (collider == paddle) {
						paddlePhysics(collider);
					}
					
					// We hit a brick.
					else {
						brickPhysics(collider);
						remove (collider);
						bricksRemaining--;
						addToScore(collider.getColor());
					}
					
					return;  // Will stop program from checking the other corners if one of the corners has already detected a collision. 
				} 
			}
		}
	}
	
	// Adds to score based on brick color.
	private void addToScore(Color color) {
		if (color == Color.RED) {
			score += 20;
		} else if (color == Color.ORANGE) {
			score += 15;
		} else if (color == Color.YELLOW) {
			score += 10;
		} else if (color == Color.GREEN) {
			score += 5;
		} else if (color == Color.CYAN) {
			score += 1;
		}
		scoreLabel.setLabel("Score: " + score);
	}
	
	// Handles ball movement after hitting paddle.
	private void paddlePhysics(GObject paddle) {
		vy = vy > 0 ? -vy :vy;  // only move up.

		// Change x and y velocity when ball hits side of paddle.
		if (ball.getCenterY() >= paddle.getY()) {
			vx = -vx;
			vy = -vy;
		}
	}
			
	// Handles ball movement after hitting a brick.
	private void brickPhysics(GObject brick) { 
		
		// Hits from above or below.
		if (ball.getCenterY() >= brick.getBottomY() || ball.getCenterY() <= brick.getY())
			vy = -vy;

		// Hits from the sides.
		else vx = -vx;
	}
	
	// Checks if the ball exited bottom screen.
	private boolean lifeLost() {
		return ball.getY() > getHeight();
	}
	
	// Display end game message and options.
	private void endGame(boolean gameResult) {
		if (gameResult == true) {
			updateMainText("Congratulations, you won!");
		} else {
			updateMainText("Sorry, you lost :(");
		}
		add(mainText);
		pause(2500);
		
		playAgainOption();
	}	
	
	// Displays play again options.
	private void playAgainOption() {
		gameState = PLAY_AGAIN;
		updateMainText("Do you want to play again?");
		yesButton = createButton(getWidth()/4, getHeight()/2, 80, 40, "YES");
		noButton = createButton(3*getWidth()/4, getHeight()/2, 80, 40, "NO");
	}
	
	// Creates button using GRect with centered label.
	private GRect createButton(double x, double y, double width, double height, String text) {
		// Rectangle.
		GRect button = new GRect(width, height);
		button.setCenterLocation(x, y);
		add(button);
		
		// Label.
		GLabel label = new GLabel(text);
		label.setFont("SanSerif-bold-12");
		label.setCenterLocation(x, y);
		add(label);
	
		return button;
	}
	
	// Changes text and re-centers main label.
	private void updateMainText(String text) {
		mainText.setVisible(true);
		mainText.setLabel(text);
		mainText.setFont("SanSerif-bold-18");
		mainText.setCenterLocation(getWidth()/2, getHeight()/2 - 50);
	}
	
	// Buffer for bugs with event listeners and loops.
	private void buffer() {
		pause(0);
	}
	
	public void mouseMoved(MouseEvent e) {
		if (gameState != PLAYING) return;

		// Stop at right wall.
		if (e.getX() > getWidth() - PADDLE_WIDTH/2)
			paddle.setCenterX(getWidth() - PADDLE_WIDTH/2);

		// Stop at left wall.
		else if (e.getX() < PADDLE_WIDTH/2)
			paddle.setCenterX(PADDLE_WIDTH/2);

		// Move paddle.
		else paddle.setCenterX(e.getX());
	}
	
	
	public void mouseClicked(MouseEvent e) {

		// Only enable mouse click functionality on play again screen.
		if (gameState != PLAY_AGAIN) return;

		if (yesButton.contains(e.getX(), e.getY())) {
			gameState = START;
		}

		else if (noButton.contains(e.getX(), e.getY())) {
			gameState = END;
		}
	}
	

	public void keyPressed (KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_P) { 

			// Pause game.
			if (gameState == PLAYING) {
				gameState = PAUSED;
				updateMainText("GAME PAUSED. PRESS \"P\" TO RESUME");
			}

			// Resume.
			else if (gameState == PAUSED) {
				mainText.setVisible(false);
				gameState = PLAYING;
			}
		}
	}
}
	
	

	

