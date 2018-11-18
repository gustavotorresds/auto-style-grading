/*
 * File: Breakout.java
 * -------------------
 * Name: Ri
 * Section Leader: Rachel
 * 
 * This file codes the game of Breakout. Breakout is played by having a ball
 * bounce around the screen, removing specific objects, aka bricks and much more, 
 * it hits! Only problem is, the ball cannot hit the bottom of the screen or else
 * the player loses a turn. To have the ball avoid hitting the bottom, there is
 * a paddle located near the bottom that can only translate laterally, and the ball
 * can bounce off the paddle safely. Game is over when the player runs out of turns
 * or all the bricks are removed.
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
	public static final double VELOCITY_Y = 7.0;
	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	// Number of turns 
	public static final int NTURNS = 3;
	// Font for post game
	public static final String BIG_FONT = "Courier-58";

	//instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject colliders;
	private int turns;
	private int score;
	private GLabel data = null;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private GImage background = new GImage("5557ce5e35973aa.jpg");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		showImage();
		addMouseListeners();
		setUpGame();
		//game is over by exhausting the # of turns or removing all bricks
		while(turns != 0 && score != NBRICK_COLUMNS * NBRICK_ROWS) {
			playGame();
		}
		postGame();
	}

	// sets background
	private void showImage() {
		background.setLocation(getWidth() / 4, getHeight() / 3);
		background.setSize(background.getWidth() / 3, getHeight() / 3);
		add(background);
	}

	/* This method checks the location of the mouse, and uses the x coordinates
	 * of the mouse to move the paddle. The paddle only moves laterally, and it
	 * does not go off the screen when the mouse does. There is an offset
	 * that places the middle of the paddle on top of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH / 2;
		paddle.setLocation(mouseX, CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		if(paddle.getX() > CANVAS_WIDTH - PADDLE_WIDTH ) {
			paddle.setLocation(CANVAS_WIDTH - PADDLE_WIDTH, 
					CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		} else if(paddle.getX() < 0) {
			paddle.setLocation(0, CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}
	}

	/* This method sets up the game by placing all the bricks, paddle, ball,
	 * and stats that need to be on the canvas before the game can commence.
	 */
	private void setUpGame() {
		makeRows();
		makePaddle();
		makeBall();
		//data now includes score
		addData();
	}

	/* This method plays the game. It sets the x and y velocity by utilizing
	 * a random generator and a boolean.
	 * During the game, the while loop continues on until either of two scenarios happen:
	 * the ball touches the bottom wall or all of the bricks are removed.
	 * Also, this method encompasses two other methods that describe what happens
	 * when the ball collides with the wall or an object.
	 */
	private void playGame() {
		//sets x and y velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = -VELOCITY_Y;
		//sets location of ball before each click that initiates game
		ball.setLocation((CANVAS_WIDTH - 2 * BALL_RADIUS) / 2, 
				CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET - 2 * BALL_RADIUS);
		//game only starts after first click
		waitForClick();
		while(true) {
			ball.move(vx, vy);
			ballCollidesWithWall();
			ballCollidesWithObject();
			pause(DELAY);
			//criteria for while loop to break
			if(ball.getBottomY() >= getHeight()) {
				turns = turns - 1;
				data.setLabel("Balls Left: " + turns + " Score: " + score);
				break;
			} else if(score == NBRICK_COLUMNS * NBRICK_ROWS) {
				break;
			}
		}
	}

	/* This method runs what happens after the game is over, aka no balls/turns
	 * left or all the bricks are removed.
	 */
	private void postGame() {
		if(turns == 0) {
			remove(ball);
			GLabel youLose = new GLabel("YOU LOSE:(:(");
			//sets font
			youLose.setFont(BIG_FONT);
			youLose.setLocation((getWidth() - youLose.getWidth()) / 2,
					(getHeight() - youLose.getHeight()) / 2);
			youLose.setColor(Color.MAGENTA);
			add(youLose);
		} else if(score == NBRICK_COLUMNS * NBRICK_ROWS) {
			remove(ball);
			GLabel youWin = new GLabel("YOU WIN:):)");
			//sets font
			youWin.setFont(BIG_FONT);
			youWin.setLocation((getWidth() - youWin.getWidth()) / 2,
					(getHeight() - youWin.getHeight()) / 2);
			youWin.setColor(Color.BLUE);
			add(youWin);
		}
	}

	/* This method describes what happens when the ball collides with the wall by using
	 * booleans.
	 * If left or right wall is hit, then x velocity is flipped.
	 * If top wall is hit, then y velocity becomes positive.
	 */
	private void ballCollidesWithWall() {
		//velocity change when hitting wall
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
			//plays noise
			bounceClip.play();
		} else if(hitTopWall(ball)) {
			vy = Math.abs(vy);
			//plays noise
			bounceClip.play();
		}
	}

	/* This method describes what happens when the ball collides with an object.
	 * If the object is a paddle, the y direction is flipped.
	 * If the object is a brick, then it removes it, and the y direction is flipped.
	 * The variable score keeps track of how many bricks have been collected.
	 */
	private void ballCollidesWithObject() {
		colliders = getCollidingObject();
		if(colliders == paddle) {
			vy = -Math.abs(vy);
			//plays noise
			bounceClip.play();
		} else if(colliders != null && colliders != paddle && colliders != data && colliders != background) {
			remove(colliders);
			vy = -vy;
			score = score + 1;
			data.setLabel("Balls Left: " + turns + " Score: " + score);
			//plays noise
			bounceClip.play();
		}
	}

	/* This method uses the corners of the ball to get the element at that location. 
	 * Any element at that location is stored in the variable 'colliders' and it's
	 * returned to the 'ballCollidesWithObject()' method. If there is no element
	 * at that location, null is returned.
	 */
	private GObject getCollidingObject() {
		//these variables are the corners of the ball
		double TLCx = ball.getX();
		double TLCy = ball.getY();
		double TRCx = ball.getX() + 2 * BALL_RADIUS;
		double TRCy = ball.getY();
		double BRCx = ball.getX() + 2 * BALL_RADIUS;
		double BRCy = ball.getY() + 2 * BALL_RADIUS;
		double BLCx = ball.getX();
		double BLCy = ball.getY() + 2 * BALL_RADIUS;
		//checks to see if corners are touched
		colliders = getElementAt(TLCx,TLCy,TRCx,TRCy,BRCx,BRCy,BLCx,BLCy);
		if(colliders != null) {
			return colliders;
		} else {
			return null;
		}
	}

	/* This method makes the rows/columns of bricks. There are two for loops being used,
	 * similar to how Pyramid was made. One for loop is for the # of rows and the other
	 * is for the # of columns. Variables x and y determine the location of each brick,
	 * and the variable 'xOffset' moves the bricks so that they are in the middle of 
	 * the canvas.
	 * The if statements give color to two rows of bricks. If the row # when divided by
	 * 10 has a remainder of 0 or 1, then the bricks are red. Remainder of 2,3 gives the
	 * color orange..and so on. This if statement also works for more or less than 10
	 * rows, repeating the pattern if there are more. 
	 */
	private void makeRows() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = col * (BRICK_WIDTH + BRICK_SEP);
				double y = row * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
				double xOffset = (CANVAS_WIDTH - 
						(NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2;
				GRect rect = new GRect(x + xOffset, y, BRICK_WIDTH, BRICK_HEIGHT);
				//makes every two rows of bricks different colors
				if(row % 10 == 0 || row % 10 == 1) {
					rect.setFilled(true);
					rect.setColor(Color.RED);
					add(rect);
				} else if(row % 10 == 2 || row % 10 == 3) {
					rect.setFilled(true);
					rect.setColor(Color.ORANGE);
					add(rect); 
				} else if(row % 10 == 4 || row % 10 == 5) {
					rect.setFilled(true);
					rect.setColor(Color.YELLOW);
					add(rect);
				} else if(row % 10 == 6 || row % 10 == 7) {
					rect.setFilled(true);
					rect.setColor(Color.GREEN);
					add(rect);
				} else if(row % 10 == 8 || row % 10 == 9) {
					rect.setFilled(true);
					rect.setColor(Color.CYAN);
					add(rect);
				}
			}
		}
	}

	/* Part of the setup, this method creates the paddle, and places it in the bottom
	 * middle of the screen.
	 */
	private void makePaddle() {
		double x = (CANVAS_WIDTH - PADDLE_WIDTH) / 2;
		double y = CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT); 
		paddle.setFilled(true);
		paddle.setColor(Color.BLUE);
		add(paddle);
	}

	/* Part of setup, this method creates the ball and places it above the paddle.
	 */
	private void makeBall() {
		double x = (CANVAS_WIDTH - 2 * BALL_RADIUS) / 2;
		double y = CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET - 2 * BALL_RADIUS;
		ball = new GOval (x, y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.GRAY);
		add(ball);
	}

	/* Part of setup, this method adds the # of turns to the screen. It also
	 * starts the variable 'score' at 0.
	 */
	private void addData() {
		turns = NTURNS;
		score = 0;
		data = new GLabel("Balls Left: " + turns + " Score: " + score);
		data.setLocation(0, data.getHeight());
		add(data);
	}

	//boolean that tells method if ball is beyond the wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//boolean that tells method if ball is beyond the wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	//boolean that tells method if ball is beyond the wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
}
