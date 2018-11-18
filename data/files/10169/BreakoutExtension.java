/*
 * File: BreakoutExtension.java  
 * -------------------
 * Name: Ildemaro Gonzalez
 * Section Leader: Rachel Gardner
 * 
 * This file will implements a Michael Jackson themed extension of the game
 * Breakout. The game generates random Michael Jackson sounds (turn up your volume!) when the ball
 * hits the paddle. This extension also plays distinct Michael Jackson songs depending on if the player
 * wins or loses. Additionally, this extension of Breakout has a super cool "try again" function 
 * that allows the player to reset the board and play again after a loss or victory without having 
 * to re-open the file. This extension also displays a lives counter so the player can keep track
 * of how many lives they have remaining.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class BreakoutExtension extends GraphicsProgram {

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
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	/**
	 * Added constants and instance variables:
	 */
	
	// Y coordinate of try again message
	public static final double TRYLABEL_Y_VALUE = 500;
		
	// X coordinate of lives left display
	public static final double LIVES_LABEL_X = 10;

	// Makes paddle visible across methods
	private GRect paddle = null;

	// Makes ball visible across methods
	private GOval ball = null;
	
	private GLabel livesLabel = null;

	// Makes total bricks left visible across methods
	private int totalBricks = NBRICK_ROWS * NBRICK_COLUMNS;

	// Makes lives left visible across methods
	private int lives = NTURNS;

	// Makes ball x and y velocity visible across methods
	private double vx, vy;

	// Makes random generator visible across methods
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Boolean sets game to loop and allows it to restart
	private boolean go = true;

	// Initializes audio clips to be used in game
	AudioClip clipMJ2 = MediaTools.loadAudioClip("MJ2.wav"); 
	AudioClip clipMJ3 = MediaTools.loadAudioClip("MJ3.wav");
	AudioClip clipMJ4 = MediaTools.loadAudioClip("MJ4.wav"); 
	AudioClip clipMJ5 = MediaTools.loadAudioClip("MJ5.wav");
	AudioClip clipMJ6 = MediaTools.loadAudioClip("MJ6.wav"); 
	AudioClip clipMJ7 = MediaTools.loadAudioClip("MJ7.wav");
	AudioClip clipMJ8 = MediaTools.loadAudioClip("MJ8.wav"); 
	AudioClip clipMJ9 = MediaTools.loadAudioClip("MJ9.wav");
	AudioClip clipMJ10 = MediaTools.loadAudioClip("MJ10.wav"); 
	AudioClip clipMJ11 = MediaTools.loadAudioClip("MJ11.wav");
	AudioClip clipMJ12 = MediaTools.loadAudioClip("MJ12.wav"); 
	AudioClip clipMJ13 = MediaTools.loadAudioClip("MJ13.wav");
	AudioClip clipMJ14 = MediaTools.loadAudioClip("MJ14.wav");
	AudioClip clipImBad = MediaTools.loadAudioClip("ImBad.wav");
	AudioClip clipJustBeatIt = MediaTools.loadAudioClip("JustBeatIt.wav");

	public void run() {
		// Set the window's title bar text
		setTitle("MJ Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// While loop allows game to be reset and replayed
		while(go) {
			addMouseListeners();
			setup();
			play();
		}
	}

	/**
	 * Method: Set Up
	 * ____________
	 * Sets up the screen for play. Creates all the bricks and the paddle.
	 */
	private void setup() {
		setBricks();
		setPaddle();
	}

	/**
	 * Method: Set Bricks
	 * _______________
	 * Creates layers of bricks on screen according to constants set for number of rows
	 * and columns of bricks. Bricks are colored in a Michael Jackson Thriller jacket colors.
	 */
	private void setBricks() {

		// Variable establishes left edge of first column of bricks
		double firstColumnXValue = getWidth()/2.0 - 5 * BRICK_WIDTH - 4.5 * BRICK_SEP;

		// Variable establishes y distance separating rows
		double brickYSeparation = BRICK_SEP + BRICK_HEIGHT;

		// Loop creates number of rows according to constant
		for(int j = 0; j < NBRICK_ROWS; j++) {
			Color color = Color.RED;

			// Makes every third row is dark gray
			if(j % 3 == 0) {
				color = Color.DARK_GRAY;
			} 
			addRow(firstColumnXValue, BRICK_Y_OFFSET + brickYSeparation * j, color);
		}
	}

	/**
	 * Creates row of bricks, taking in parameters of initial x and y coordinates,
	 * and brick color. Number of bricks in row is equal to constant for number of 
	 * columns.
	 */
	private void addRow(double x, double y, Color color) {
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(x + i * BRICK_WIDTH + i * BRICK_SEP , y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
		}
	}

	/**
	 * Creates paddle and centers it near bottom of screen.
	 */
	private void setPaddle() {
		double paddleCenterX = getWidth()/2.0 -PADDLE_WIDTH/2.0;
		paddle = new GRect(paddleCenterX, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/**
	 * Calls mouse location to make the paddle track the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();

		// Paddle will only move in the x direction and only within the bounds of the canvas
		if(mouseX < getWidth() - PADDLE_WIDTH/2.0 && mouseX > PADDLE_WIDTH/2.0) {
			paddle.setCenterX(mouseX);
		}
	}

	/**
	 * Method: Play
	 * -----------
	 * This method creates a ball and allows the player to click to launch the game
	 * with all its rules and conditions. After the player wins or loses this method
	 * allows the player to click to try again and reset the board.
	 * 
	 * Pre: The bricks and paddle in the world are set up, but there is no ball and 
	 * the game has not started.
	 * Post: The ball is created, the game can be played and repeated after a win or loss. 
	 */
	private void play() {
		createBall();

		// Waits for player to click before launching the ball and starting the game
		waitForClick();
		displayLives();
		playBall();
		tryAgain();
	}
	
	/**
	 * Creates and displays a lives counter on screen so the player can keep track of their life count.
	 */
	private void displayLives() {
		livesLabel = new GLabel("Lives: " + lives);
		livesLabel.setFont("SansSerif-16");
		livesLabel.setLocation(LIVES_LABEL_X, BRICK_Y_OFFSET/2.0 + livesLabel.getAscent());
		livesLabel.setColor(Color.BLACK);
		add(livesLabel);
	}

	/**
	 * Creates ball used to play the game and places it at center of screen.
	 */
	private void createBall() {
		double centerX = getWidth()/2.0;
		double centerY = getHeight()/2.0;

		ball = new GOval (centerX - BALL_RADIUS, centerY - BALL_RADIUS, 2.0 * BALL_RADIUS, 2.0 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/**
	 * Method: Play Ball
	 * -----------
	 * Launches the ball towards the bottom wall at a random angle and begins the 
	 * game while implementing its rules and conditions. 
	 * 
	 * Rules: ball bounces off walls, the paddle, and bricks; the ball deletes any bricks it 
	 * hits; if the ball crosses the bottom wall, the player loses a life and the ball is reset;
	 * if the player loses all their lives, the game ends and losing music plays; if the player 
	 * deletes all the bricks, the player wins and winning music plays. When the ball bounces off
	 * the paddle, a random Michael Jackson noise plays.
	 */
	private void playBall() {

		// Launches ball towards bottom wall with y velocity
		vy = VELOCITY_Y;

		// Gives ball random x velocity, launching it at random angle
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);

		// Ensures x velocity is negative half the time
		if (rgen.nextBoolean(0.5)) vx = -vx;

		while(ball != null) {

			// Ball continues to move after it's launched
			ball.move(vx, vy);
			pause(DELAY);

			// Ball bounces with correct trajectory when it hits side walls
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}

			// Ball bounces with correct trajectory when it hits top wall
			if(hitTopWall(ball)) {
				vy = -vy;
			}

			// Checks if ball has collided with any object and declares it as "collider"
			GObject collider = checkForCollision();

			// If ball collides with paddle, ball bounces up (preventing sticky paddle bug)
			if(collider == paddle) {
				vy = Math.abs(vy) * -1;

				// When ball hits paddle, a random Michael Jackson noise is played
				playRandomMJ();

				// If the ball hits a brick, it bounces off and deletes the brick
			} else if(collider != null && collider != livesLabel) {
				vy = -vy;
				remove(collider);

				// Keeps track of number of bricks remaining 
				totalBricks = totalBricks - 1;
			}

			// The following occurs if player wins the game
			if(playerWins(totalBricks)) {

				// Ball is removed from screen
				remove(ball);

				// Message informs player they have won
				winMessage();

				// Michael Jackson's "Beat It" plays
				clipJustBeatIt.play();

				// Game stops playing
				break;
			}

			// Player loses lives if ball hits bottom wall
			if(hitBottomWall(ball)) {

				// Ball is removed after hitting bottom wall
				remove(ball);

				// Player loses a life
				lives = lives - 1;
				ball = null;

				// Creates new ball if player has lives remaining
				if(lives > 0) {
					createBall();

					// Upon click, ball launches with new random x velocity, resuming play
					waitForClick();
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
					if (rgen.nextBoolean(0.5)) vx = -vx;

					// If player has no lives remaining, they lose
				} else {

					// Message informs player they have won
					loseMessage();

					// If player loses, Michael Jackson's "Bad" plays
					clipImBad.play();

					// Game stops playing
					break;
				}
			}
		}
	}

	/**
	 * Method: Check For Collision
	 * __________________
	 * Checks if ball has collided with any object. This method looks for 
	 * objects at the four corners of the square that inscribes the ball
	 * and returns whatever object exists there. 
	 */
	private GObject checkForCollision() {
		GObject object = getElementAt(ball.getX(), ball.getY());
		if(object != null) {
			return object;
		} else { 
			object = getElementAt(ball.getRightX(), ball.getY());
			if(object != null) {
				return object;
			} else {
				object = getElementAt(ball.getRightX(), ball.getBottomY());
				if(object != null) {
					return object;
				} else {
					object = getElementAt(ball.getX(), ball.getBottomY());
					if(object != null) {
						return object;
					} else {
						return null;
					}
				}
			}
		}
	}

	/**
	 * Randomly plays one of the available Michael Jackson sound bites. 
	 */
	private void playRandomMJ() {

		// Generates random integer, each corresponding to a different audio file
		int clip = rgen.nextInt(2,14); 
		if(clip == 2) {
			clipMJ2.play();
		} else if(clip == 3) {
			clipMJ3.play();
		} else if (clip == 4) {
			clipMJ4.play();
		}else if(clip == 5) {
			clipMJ5.play();
		} else if (clip == 6) {
			clipMJ6.play();
		}else if(clip == 7) {
			clipMJ7.play();
		}else if(clip == 8) {
			clipMJ8.play();
		} else if (clip == 9) {
			clipMJ9.play();
		}else if(clip == 10) {
			clipMJ10.play();
		} else if (clip == 11) {
			clipMJ11.play();
		}else if(clip == 12) {
			clipMJ12.play();
		}else if (clip == 13) {
			clipMJ13.play();
		}else if(clip == 14) {
			clipMJ14.play();
		}
	}

	/**
	 * Player wins the game if they have successfully deleted all the bricks on the screen. 
	 */
	private boolean playerWins(int totalBricks) {
		return totalBricks == 0;
	}

	/**
	 * Displays message informing player they won the game. 
	 */
	private void winMessage() {
		GLabel winLabel = new GLabel("You Beat It!");
		winLabel.setFont("SansSerif-40");
		winLabel.setColor(Color.MAGENTA);
		winLabel.setLocation(getWidth()/2.0 - winLabel.getWidth()/2.0, getHeight()/2.0 - winLabel.getHeight()/2.0);
		add(winLabel);
	}

	/**
	 * Displays message informing player they lost the game. 
	 */
	private void loseMessage() {
		GLabel loseLabel = new GLabel("You're Bad!");
		loseLabel.setFont("SansSerif-40");
		loseLabel.setColor(Color.GRAY);
		loseLabel.setLocation(getWidth()/2.0 - loseLabel.getWidth()/2.0, getHeight()/2.0 - loseLabel.getHeight()/2.0);
		add(loseLabel);
	}

	/**
	 * Method: Try Again
	 * -----------
	 * Displays message prompting player to click if they want to start the game over again.
	 * If player clicks, the victory or loss music stops playing, and all objects on screen are removed.
	 * Full number of lives and original settings are restored. After this method finishes, the run method
	 * while loop resets, restarting the game with new bricks.
	 */
	private void tryAgain() {

		// Displays message prompting player to try again
		tryMessage();

		// After click, all objects on screen are removed
		waitForClick();
		removeAll();

		// Stops the win music or loss music that is currently playing
		if(totalBricks == 0) {
			clipJustBeatIt.stop();
		} else if(lives == 0) {
			clipImBad.stop();
		}

		// Restores lives and original count of total bricks
		lives = NTURNS;
		totalBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	}

	/**
	 * Creates and centers message prompting player to click if they want 
	 * to start the game over again.
	 */
	private void tryMessage() {
		GLabel tryLabel = new GLabel("Click to try again.");
		tryLabel.setFont("SansSerif-14");
		tryLabel.setLocation(getWidth()/2.0 - tryLabel.getWidth()/2.0, TRYLABEL_Y_VALUE);
		add(tryLabel);
	}

	/**
	 * (Hit Right Wall) condition is met when right edge of ball reaches right wall of the canvas
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	/**
	 * (Hit Left Wall) condition is met when left edge of ball reaches left wall of the canvas
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	/**
	 * (Hit Top Wall) condition is met when top edge of ball reaches top wall of the canvas
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	/**
	 * (Hit Bottom Wall) condition is met when bottom edge of ball reaches bottom wall of the canvas
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
}
