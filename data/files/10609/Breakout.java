
/*
 * File: Breakout.java

 * NAME: Labib Tazwar Rahman
 * SECTION LEADER: Esteban Rey L
 * ----------------------
 * Program name: Breakout
 * 
 * This file will eventually implement the game of Breakout.
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

	// Offset to fix glued error
	private static final double GLUED_ERROR_OFFSET = 4;
	
	//stores the value of total number of bricks
	private int count = NBRICK_COLUMNS * NBRICK_ROWS;

	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	private GLabel bricksLeft;
	private GLabel life;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		play();

	}
	//creates the world before mouse is clicked
	private void setup() {
		paddle = addPaddle();
		addPaddleToDefaultLocation(paddle);
		layDownAllBricks();
		addPaddle();
		showHowManyBricksLeft();


	}
	//precondition: game setup done. ball stationary
	//postcondition: game starts. ball moves down the screen
	private void play() {
		addMouseListeners();
		playGame();
	}


	//we want to play the game for NTURNS number of turns. 
	private void playGame() {
		for (int p = 0; p < NTURNS; p++) {
			//we show how many lives are left at the bottom left of the screen
			showHowManyLivesLeft(p);
			//this is how we start the ball during each turn
			startBall();
			//if all bricks are removed before all turns end, then it displays a win message after removing everything else from screen.
			if (ifGameEndsWithNoBricks()) {
				removeAll();
				showWinMessage();
				break;
			}
		}
		//if turns end without removal of all bricks, then it displays a Loss message after removing everything else from screen. 
		if (ifGameOverWithBricksLeft()) {
			removeAll();
			showWLossMessage();
		}
	}

	//the ball is created and only after mouse click does it start to move.
	private void startBall() {
		createBall();
		waitForClick();
		makeBallMove();
	}


	private void showHowManyLivesLeft(int p) {
		GObject object = getElementAt(PADDLE_WIDTH*2, getHeight() - PADDLE_Y_OFFSET * 0.5);
		if (object != null) {
			remove(object);
		}
		life = new GLabel("Lives left: " + (NTURNS - p) + "/3");
		life.setColor(Color.magenta);
		add(life, PADDLE_WIDTH*0.5, getHeight() - PADDLE_Y_OFFSET * 0.5 + PADDLE_HEIGHT);
	}

	private void showHowManyBricksLeft() {
		GObject object = getElementAt(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET * 0.5);
		if (object != null) {
			remove(object);
		}

		bricksLeft = new GLabel("Bricks left: " + count + "/" +NBRICK_COLUMNS*NBRICK_ROWS);
		bricksLeft.setColor(Color.CYAN);
		add(bricksLeft, getWidth() - bricksLeft.getWidth() - PADDLE_WIDTH * 0.5, getHeight() - PADDLE_Y_OFFSET * 0.5 + PADDLE_HEIGHT);
	}

	//the case when game ends if number of bricks is zero
	private boolean ifGameEndsWithNoBricks() {
		return count == 0;
	}

	//the case when game ends if there are still bricks left
	private boolean ifGameOverWithBricksLeft() {
		return count > 0;
	}

	private void makeBallMove() {

		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		double vy = VELOCITY_Y;

		//so that the ball's probability of going left and right while moving down is 50:50
		if (rgen.nextBoolean(0.5))
			vx = -vx;

		//this while loop's condition is basically when the ball is in mid-air before it ends and the ball isn't colliding with paddle
		while (!ifGameEndsWithNoBricks() && !ifBallDoesNotTouchPaddle()) {

			ball.move(vx, vy);

			pause(DELAY);

			//the ball reflects/rebounds from the top edge
			vx = checkSideEdges(vx);

			//the ball reflects/rebounds from either wall on the left and right
			vy = checkTopEdge(vy);

			//the GObject collider checks what it is colliding with
			GObject collider = getCollidingObject();

			//if we have a ball-paddle collision, then we make the ball bounce with reflection
			vy = CheckForPaddleCollisions(vy, collider);

			//if we have a ball-paddle collision, then we make the ball bounce with reflection as well as remove the brick with which it collided
			vy = CheckForBrickCollisions(vy, collider);
		}
		remove(ball);
	}

	private double checkTopEdge(double vy) {
		if (ball.getY() < 0) {
			vy = -vy;
		}
		return vy;
	}

	private double checkSideEdges(double vx) {
		if (isRightWall() || isLeftWall()) {
			vx = -vx;
		}
		return vx;
	}

	private double CheckForPaddleCollisions(double vy, GObject collider) {

		if (collider == paddle) {
			//the condition of this if statement solves for the glued paddle error that happens sometimes
			if (rangeToFixGluedError()) {
				vy = -vy;
				//plays sound after collision
				bounceClip.play();
			}
		}
		return vy;
	}

	private double CheckForBrickCollisions(double vy, GObject collider) {

		//the condition inside this if statement essentially checks if the ball collides with the brick
		if (brickCollisionHappens(collider)) {
			vy = -vy;
			//plays sound after collision
			bounceClip.play();
			remove(collider);
			//brick count reduces by one after each ball-brick collision
			count--;
			showHowManyBricksLeft();
		}
		return vy;
	}

	//if the ball collides with either anything other than bricks, then it collides with bricks given that collision happens
	private boolean brickCollisionHappens(GObject collider) {
		return collider != paddle && collider != null && collider != bricksLeft && collider != life;
	}

	private boolean ifBallDoesNotTouchPaddle() {
		return ball.getY() > getHeight() - 2 * BALL_RADIUS;
	}

	private boolean rangeToFixGluedError() {
		//we make a range here so that the ball bounces just a LITTLE before it ACTUALLY collides with the paddle.
		return (ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2 * BALL_RADIUS + GLUED_ERROR_OFFSET)
				&& (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2 * BALL_RADIUS);
	}

	private GObject getCollidingObject() {
		double top = ball.getY();
		double bottom = ball.getY() + 2 * BALL_RADIUS;
		double left = ball.getX();
		double right = ball.getX() + 2 * BALL_RADIUS;

		if (getElementAt(left, top) != null) {
			return getElementAt(left, top);
		}
		if (getElementAt(right, top) != null) {
			return getElementAt(right, top);
		}
		if (getElementAt(left, bottom) != null) {
			return getElementAt(left, bottom);
		}
		if (getElementAt(right, bottom) != null) {
			return getElementAt(right, bottom);
		}
		return null;

	}

	private boolean isLeftWall() {
		return (ball.getX() < 0);
	}

	private boolean isRightWall() {
		return (ball.getX() > (getWidth() - ball.getWidth()));
	}

	private void createBall() {
		double size = BALL_RADIUS * 2;
		ball = new GOval(size, size);
		ball.setFilled(true);
		double centreX = (getWidth() - size) / 2.0;
		double centreY = (getHeight() - size) / 2.0;
		add(ball, centreX, centreY);
	}

	private void showWLossMessage() {
		GLabel lossMessage = new GLabel("*tears* You lost ");
		lossMessage.setColor(Color.RED);
		add(lossMessage, (getWidth() - lossMessage.getWidth()) / 2.0, getHeight() / 2.0);
	}

	private void showWinMessage() {
		GLabel winMessage = new GLabel("WOOHOO! you won!");
		winMessage.setColor(Color.BLUE);
		add(winMessage, (getWidth() - winMessage.getWidth()) / 2.0, (getHeight() - winMessage.getAscent()) / 2.0);
	}

	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (x <= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, y);
		}
	}
	//we set the paddle at a position when the game setup is done
	private void addPaddleToDefaultLocation(GRect paddle) {
		double x = (getWidth() - PADDLE_WIDTH) / 2.0;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, x, y);
	}

	private GRect addPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;

	}

	//we lay down all bricks by just creating just one brick and then changing the x and y positions of each brick
	private void layDownAllBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {

			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				//the color for each row is determined by the selectColor() method
				Color color = selectColor(row);

				double x = getWidth() / 2.0 - NBRICK_COLUMNS * BRICK_WIDTH / 2.0
						- (NBRICK_COLUMNS - 1) * BRICK_SEP / 2.0 + (BRICK_WIDTH + BRICK_SEP) * column;
				double y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);

				makeOneBrick(color, x, y);
			}
		}
	}
	//we make one brick here
	private void makeOneBrick(Color color, double x, double y) {
		GRect brick;
		brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick, x, y);
	}
	
	//we put appropriate range for the row % 10 such that the RED, ORANGE, YELLOW, GREEN, CYAN combination repeats every 10 rows
	//and also such that in one unit combination, each color appears in 2 adjacent rows
	private Color selectColor(int row) {
		//we paint red for rows that have 0 or 1 in ones place
		Color color = Color.red;
		if (row % 10 <= 1) {
			color = Color.red;
			//we paint orange for rows that have 2 or 3 in ones place
		} else if (row % 10 > 1 && row % 10 <= 3) {
			color = Color.orange;
			//we paint yellow for rows that have 4 or 5 in ones place
		} else if (row % 10 > 3 && row % 10 <= 5) {
			color = Color.yellow;
			//we paint green for rows that have 6 or 7 in ones place
		} else if (row % 10 > 5 && row % 10 <= 7) {
			color = Color.green;
			//we paint cyan for rows that have 8 or 9 in ones place
		} else if (row % 10 > 7 && row % 10 <= 9) {
			color = Color.cyan;
		}
		return color;
	}
}
