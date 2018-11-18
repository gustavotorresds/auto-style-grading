/*
 * File: Breakout.java


 * -------------------
 * Name:Shanna Trott
 * Section Leader: Julia Truitt
 * 
 * This program extends Breakout by prompting the player to click to start the game and adding
 * sounds when the ball hits the paddle or bricks.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extension extends GraphicsProgram {
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

	// Original X position of Ball
	public static final double BALL_ORIGIN = CANVAS_WIDTH/2.0 - BALL_RADIUS;

	public static double DIAMETER = BALL_RADIUS *2.0;

	// Y position of the paddle
	private double PADDLE_Y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;

	// generates random variable for the velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GRect paddle;

	private GOval ball;

	private GRect brick;

	private GLabel click;

	// number of bricks on the screen
	private int brickCount = NBRICK_ROWS * NBRICK_COLUMNS;

	public void run() {	
		addMouseListeners();
		setUpGame();
		playGame();	
	}

	/* Method: Play Game
	 * --------------------
	 * This method sets the ball in motion and resets the ball if it touches the bottom 
	 * row until the number of turns runs out and bricks still exist. If you run out of turns 
	 * the method prints "GAME OVER" and removes the ball.
	 * 
	 * pre: game is set up with bricks, paddle, and ball
	 * post: Either "GAME OVER" or "WINNER!" label is printed and game has been attempted. Ball
	 * has been removed.
	 */
	private void playGame() {
		for(int t = 0; t < NTURNS; t ++){
			moveBall();
			if(brickCount != 0) {
				add(ball, BALL_ORIGIN, getHeight()/2.0);
			}
		}	
		remove(ball);
		printGameOver();
	}

	/* method: Print Game Over
	 * ---------------------
	 * prints game over when called on the screen in large font
	 */
	private void printGameOver() {
		GLabel gameOver = new GLabel("GAME OVER");
		gameOver.setColor(Color.RED);
		gameOver.setFont("Courier-70");
		add(gameOver, getWidth()/2.0 - gameOver.getWidth()/2.0, getHeight()/2.0 - gameOver.getAscent());
	}

	/* This method tests for collisions between the ball and other objects on the canvas.
	 * It tests for objects at each corner of the ball and returns the colliding object.
	 * 
	 * pre: the ball bounces around but does not test of collisions with other objects.
	 * post: the programs recognizes if the ball is colliding with other objects.
	 */
	private GObject getCollidingObject() {
		GObject collider = null;
		double topLeftCornerX = ball.getX();
		double topLeftCornerY = ball.getY();
		double topRightCornerX = ball.getX() + DIAMETER;
		double topRightCornerY = ball.getY();	
		double bottomLeftCornerX = ball.getX();
		double bottomLeftCornerY = ball.getY() + DIAMETER;
		double bottomRightCornerX = ball.getX() + DIAMETER;
		double bottomRightCornerY = ball.getY() + DIAMETER;
		if(getElementAt(topLeftCornerX, topLeftCornerY) != null) {
			collider =getElementAt(topLeftCornerX, topLeftCornerY);
		}
		else if(getElementAt(topRightCornerX, topRightCornerY) != null) { 
			collider =getElementAt(topRightCornerX, topRightCornerY );

		}	
		else if(getElementAt(bottomLeftCornerX,bottomLeftCornerY) != null) {
			collider = getElementAt(bottomLeftCornerX,bottomLeftCornerY);
		}
		else if(getElementAt(bottomRightCornerX, bottomRightCornerY) != null) {
			collider = getElementAt(bottomRightCornerX, bottomRightCornerY);
		}
		return collider;
	}		

	/* Method: Set Up Game
	 * ----------------------
	 * This method sets up the game by setting the canvas size, creating the bricks,
	 * creating the ball, positioning the ball,  creating the paddle and making it move.
	 * 
	 * pre: canvas is blank
	 * post: Bricks, paddle, and ball are made and in the correct place on the canvas.	 
	 */
	private void setUpGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		ball = createBall();
		add(ball, BALL_ORIGIN, getHeight()/2.0);
		createPaddle();
	}

	/* Method: Move Ball
	 * ---------------------
	 * This method starts the ball from the center after the user clicks, makes it move, and varies the velocity.
	 * It also tests whether the ball touches the outside of the canvas and makes it bounce off
	 * the left, right, and top walls, and stops the game if it touches the bottom wall.
	 * If the ball hits the paddle it bounces off and if it hits a brick it removes the brick, reverses the
	 * velocity and takes it away from the brick count. It also make noise when the paddle/bricks are hit
	 *  
	 *  pre: ball does not move
	 *  post: prompts user to click and then game is live and ball moves, bounces, and removes bricks and 
	 *  makes a sound when collisions occur.
	 */
	private void moveBall() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx= -vx;
		double vy = VELOCITY_Y;
		clickToStartPrompt();
		waitForClick();
		while(true) {
			GObject collider = getCollidingObject();
			if(touchesLeftWall(ball) || touchesRightWall(ball)) {
				vx = -vx;
			}
			else if(touchesTopWall(ball)) {
				vy= -vy;
			}
			else if(touchesBottomWall(ball)) {
				remove(ball);
				break;
			}
			else if(collider == paddle) {
				vy = -vy;
				bounceClip.play();
			}
			else if(collider != paddle && collider != null) {
				brickCount = brickCount -1;	
				remove(collider);
				vy= -vy;
				bounceClip.play();
			}
			else if(brickCount <= 0) {
				remove(ball);
				printWinner();
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
	}	

	private boolean touchesBottomWall(GOval ball) {
		return ball.getY() > getHeight() - DIAMETER;
	}

	private boolean touchesTopWall(GOval ball) {	
		return ball.getY() <= 0;
	}

	private boolean touchesRightWall(GOval ball) {
		return ball.getX() >= getWidth() - DIAMETER ;
	}

	private boolean touchesLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* Method: Click to start prompt
	 * -------------------------------
	 * This method prompts the user to click to start the game with a GLabel
	 */
	private void clickToStartPrompt() {
		GLabel click = new GLabel("click to start");
		click.setColor(Color.BLUE);
		click.setFont("Font-18");
		add(click, getWidth()/2.0 - click.getWidth()/2.0, 20);	
	}

	/* Method: Create Ball
	 * -------------------
	 * creates the ball and places it in the center
	 */
	private GOval createBall() {
		GOval ball = new GOval (DIAMETER, DIAMETER);
		ball.setFilled(true);
		return ball;
	}

	/* Method: Print Winner
	 * ----------------------
	 * adds a GLabel after the brick count equals 0.
	 * 
	 * pre: no bricks left on screen, no label
	 * post: "WINNER!" label in large font
	 */
	private void printWinner() {
		GLabel winner = new GLabel("WINNER!");
		winner.setColor(Color.MAGENTA);
		winner.setFont("Courier-70");
		add(winner, getWidth()/2.0 - winner.getWidth()/2.0, getHeight()/2.0 - winner.getAscent());
	}

	/* Method: Create Paddle
	 * -------------------------
	 * creates the paddle, fills it, and places it at the bottom center
	 */
	private void createPaddle() {
		double centerCanvas= getWidth()/2.0 - PADDLE_WIDTH/2.0;
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setLocation(centerCanvas, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		add(paddle);
	}

	/* Method: Mouse Moved - paddle
	 * -------------------------------
	 * moves paddle with mouse without moving it off the screen
	 */
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2.0;
		if(e.getX() > PADDLE_WIDTH / 2.0 && e.getX() < getWidth() - PADDLE_WIDTH / 2.0) {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}	

	/* Method: Create Bricks
	 * --------------------------
	 * creates the bricks, places them at the top and colors them according to row
	 */
	private void createBricks() {
		for(int r = 0; r < NBRICK_ROWS; r++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = r * (BRICK_SEP + BRICK_WIDTH);
				double y = c * (BRICK_SEP + BRICK_HEIGHT);
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				if(c == 0 || c== 1) {
					brick.setFilled(true);
					brick.setColor(Color.RED);
				}
				else if(c== 2 || c == 3) {
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
				}
				else if(c == 4 || c == 5) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				}
				else if (c== 6 || c == 7) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				}
				else if (c == 8 || c == 9) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}
				add(brick, (2 *BRICK_SEP) + x, y + (BRICK_HEIGHT*NBRICK_ROWS));
			}
		}
	}	
}

