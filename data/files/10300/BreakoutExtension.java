/*
 * File: Breakout.java   
 * -------------------
 * Name: Oscar Ambrocio-Ramirez
 * Section Leader: Andrew Davis
 * 
 * This file will eventually implement the game of Breakout.
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	/**private instance variables**/
	// This keeps count of the amount of bricks left.
	private int bricksLeft = (NBRICK_ROWS * NBRICK_COLUMNS);
	// This allows for the use of number of turns to be subtracted
	// to keep track of how many turns are left.
	private int turnsLeft = NTURNS;
	// Allows for the tracking of the paddle.
	GRect paddle = null;
	private double vX, vY;
	// Serves as random number generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// Allows for the tracking of the ever changing ball.
	private GOval ball1;

	public void run() {
		buildGame();
		playGame();
	}

	/*
	 * Method: buildGame
	 * ---------------
	 * This method builds the builds the game.
	 */
	private void buildGame() {
		// Set the window's title bar text.
		setTitle("CS 106A Breakout");
		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Creates Blocks for the game.
		buildBlocks();
		// Creates a ball for the player.
		ball1 = makeBall();
		// Creates a Paddle for the player.
		paddle = buildPaddle();
		// Adds the Paddle near the bottom of the screen.
		addPaddleToBottom();
	}

	/*
	 * Method: playGame
	 * ---------------
	 * This method allows the player to click and play the game.
	 */
	private void playGame() {
		addMouseListeners();
		// Waits for player to click the screen
		waitForClick();
		startClick.play();
		// Allows the ball to bounce around the world.
		bouncingBall();
	}

	/*
	 * Method: buildBlocks
	 * ---------------
	 * This method builds the blocks that the player must 
	 * eliminate to win.
	 */
	private void buildBlocks() {
		// Tracks the amount of rows.
		for (int row = 0; row < NBRICK_ROWS; row++) {
			// Tracks the amount of columns.
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				double xCoordinate = ((getWidth()/2) - ((NBRICK_ROWS *BRICK_WIDTH) + ((NBRICK_ROWS-1) * BRICK_SEP))/2) + ((BRICK_WIDTH + BRICK_SEP) * column);
				double yCoordinate = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * row);
				//Creates a block with the correct x and y coordinate.
				GRect block = new GRect (xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				// sets the colors for the blocks.
				block.setFilled(true);
				if (row <= 1) {
					block.setColor(Color.RED);
				} else if (row <= 3&& row > 1) {
					block.setColor(Color.ORANGE);
				} else if (row <=5 && row > 3) {
					block.setColor(Color.YELLOW);
				} else if (row <=7 && row > 5) {
					block.setColor(Color.GREEN);
				} else {
					block.setColor(Color.CYAN);
				}
				add (block,xCoordinate,yCoordinate);
			}
		}
	}

	/*
	 * Method: mouseMoved
	 * ---------------
	 * This method allows the player to control the paddle
	 * and also keeps the paddle in the paddle offset position
	 * while not allowing the paddle to leave the bottom of the screen.
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH/2;
		double mouseY = getHeight() - PADDLE_Y_OFFSET;
		if (mouseX > 0 && mouseX < (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(mouseX, mouseY);
		}
	}

	/*
	 * Method: addPaddleToBottom
	 * ---------------
	 * This method sets the paddle to be at the bottom at the beginning.
	 */
	private void addPaddleToBottom() {
		double x = getWidth();
		double y = getHeight();
		add(paddle, x, y);
	}

	/*
	 * Method: buildPaddle
	 * ---------------
	 * This method creates the shape and color of the paddle.
	 */
	private GRect buildPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}

	/*
	 * Method: bouncingBall
	 * ---------------
	 * This method takes into account all collisions of paddle,bricks, and walls.
	 * This method also contains the code for winning and losing a game.
	 */
	private void bouncingBall() {
		while (true) {
			ball1.move(vX, vY);
			if(bricksLeft == 0) {
				winGame();
				hoorayClip.play();
				break;
			}
			if (hitBottomWall (ball1)) {
				turnsLeft -= 1;
				remove (ball1);
				if(turnsLeft != 0) {
					loseOneLife.play();
				}
				ball1 = makeBall();
				if(turnsLeft == 0) {
					loseGame();
					gameOver.play();
					break;
				}
				waitForClick();
				startClick.play();
			}
			if (hitTopWall (ball1)) {
				hitWall.play();
				vY = -vY;
			}
			if (hitLeftWall (ball1) || hitRightWall (ball1)) {
				hitWall.play();
				vX = -vX;
			}
			GObject collider = getCollidingObject();
			if (collider != null) {
				// Allows for the paddle to be hit without fear of 
				// removing it.
				if (collider == paddle) {
					paddleJump.play();
					vY=-Math.abs(vY);
				} else {
					// Removes one brick as well as removes the item it has collided
					// with.
					vY=-vY;
					bounceClip.play();
					bricksLeft --;
					remove(collider);
				} 
			}
			pause(DELAY);
		}
	}
	// This boolean checks to see if the bottom wall has been hit.
	private boolean hitBottomWall (GOval ball1) {
		return ball1.getY() > getHeight();
	}
	// This boolean checks to see if the top wall has been hit.
	private boolean hitTopWall (GOval ball1) {
		return ball1.getY() <= 0;
	}
	// This boolean checks to see if the left wall has been hit.
	private boolean hitLeftWall (GOval ball1) {
		return ball1.getX() <= 0;
	}
	// This boolean checks to see if the right wall has been hit.
	private boolean hitRightWall (GOval ball1) {
		return ball1.getX() >= getWidth() - ball1.getWidth();
	}

	/*
	 * Method: makeBall
	 * ---------------
	 * This method creates the ball, as well as,
	 * sets the velocity of the X and Y component.
	 * Precondition: The ball is placed in the center of the screen.  
	 */
	public GOval makeBall() {
		vX = rgen.nextDouble(VELOCITY_X_MIN, 20);
		if (rgen.nextBoolean(0.5)) {
			vX = -vX;
		}
		vY = VELOCITY_Y;
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);	
		add(ball,getWidth()/2, getHeight()/2);
		return ball;
	}

	/*
	 * Method: getCollidingObject
	 * ---------------
	 * This method checks the four corners of the ball to see if a collision has 
	 * occured. Therefore, returning null or result.
	 */
	private GObject getCollidingObject() {
		for(int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				double checkX = (ball1.getX() +( i* (BALL_RADIUS * 2 )));
				double checkY = (ball1.getY() + ( j * (BALL_RADIUS * 2 )));
				GObject result = getElementAt(checkX, checkY); 
				if (result != null) {
					return result;
				} 
			}
		}
		return null;
	}

	/*
	 * Method: loseGame
	 * ---------------
	 * This method creates a label when the player loses the game.
	 */
	private void loseGame() {
		GLabel loser = new GLabel (" GAME OVER!", (getWidth()/2) - (BRICK_WIDTH/2), getHeight()/2);
		loser.setColor(Color.RED);
		add (loser);
	}

	/*
	 * Method: winGame
	 * ---------------
	 * This method creates a label when the player wins the game.
	 */
	private void winGame() {
		GLabel winner = new GLabel ("CONGRATULATIONS!", getWidth()/2 - BRICK_WIDTH/2, getHeight()/2);
		winner.setColor(Color.BLUE);
		add (winner);
	}

	// Makes a sound every time the ball hits a block.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	// Makes a sound when the player wins the game.
	AudioClip hoorayClip = MediaTools.loadAudioClip("hooray.au");
	// Makes a sound when the player clicks the screen to start a round.
	AudioClip startClick = MediaTools.loadAudioClip("startClick.au");
	// Makes a sound when the ball hits the paddle.
	AudioClip paddleJump = MediaTools.loadAudioClip("paddleJump.au");
	// Makes a sound when the player loses the game.
	AudioClip gameOver = MediaTools.loadAudioClip("gameOver.au");
	// Makes a sound when the player loses a turn.
	AudioClip loseOneLife = MediaTools.loadAudioClip("loseOneLife.au");
	// Makes a sound when the ball hits the wall.
	AudioClip hitWall = MediaTools.loadAudioClip("hitWall.au");
}