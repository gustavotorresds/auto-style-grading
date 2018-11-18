/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Hikaru Hotta
 * Section Leader: Meng Zhnag
 * File purpose: Implements the game of Breakout with extensions:
 * - Paddle that shoots bullets
 * - Click to Start label
 * - Bounce sound
 * - Improved user control over bounces
 * - "Kicker"
 * 
 * Sources: BouncingBallSoln, UFOSoln
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BreakoutExtention extends GraphicsProgram {

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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// diameter of bullet
	private static final double BULLET_DIAM = 5;

	// speed of bullet
	private static final double BULLET_SPEED = 10;

	// leverage on paddle in x direction to improve control over paddle in 
	// pixels
	private static final double X_DIR_LEVERAGE = 2;

	// speed increase after certain number of bricks removed
	private static final double SPEED_INCREASE = 1.5;

	// increase speed after how many bricks left
	private static final double SPEED_INCREASE_BRICK_COUNT = 90;

	// instance variable for bricks
	private GRect bricks = null;

	// instance variable for paddle
	private GRect paddle = null;

	// instance variable for ball
	private GOval ball = null;

	// instance variable label "Click To Start" that dissapears upon initial user click
	private GLabel clickToStart = null;

	// instance variable for bullet
	private GOval bullet = null;

	// random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// initial y velocity of ball
	private double vy = 3.0; 

	// random generator for initial x direction of ball
	private double vx = rgen.nextDouble(1.0, 3.0);

	// loads bounce sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/*
	 * Runs Breakout extension
	 * 
	 * Pre: Empty window
	 * Post: Breakout extension
	 */
	public void run() {
		// sets up breakout extension world
		setUp();
		// allows the ball to bounce around the canvas
		waitForClick();
		remove(clickToStart);
		// counter keeps track of how many bricks are left
		int counter = 100;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (!gameOver() && !win(counter)) {
			moveBall(counter);
			counter = checkForBallCollisions(counter);
			if (bullet != null) {
				moveBullet();
				counter = collideWithBullet(counter);
			}
			bulletOffScreen();
		}
		wonLostCondition();
	}

	/*
	 * Sets up breakout extension world
	 * 
	 * Pre: Empty window
	 * Post: breakout extension world 
	 */
	private void setUp() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		addKeyListeners();
		// sets up colored bricks
		createClickToStartLabel();
		setUpBricks();
		createPaddle();
		createBall();
	}

	/*
	 * Sets up bricks in 10 rows and 10 columns in five different colors in the window
	 * 
	 * Pre: Empty window
	 * Post: 10 rows and columns of bricks laid out in upper half of window
	 */
	private void setUpBricks() {
		// assign column number
		for (int colNum = 0; colNum < NBRICK_COLUMNS; colNum ++) {
			// assign row number
			for (int rowNum = 0; rowNum < NBRICK_ROWS; rowNum ++) {
				// x coordinate of top left brick
				double referenceBrickXCor = getWidth()/2 - BRICK_SEP*(NBRICK_COLUMNS/2 - 0.5) - 5*BRICK_WIDTH;
				double x = referenceBrickXCor + colNum*(BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + rowNum*(BRICK_HEIGHT + BRICK_SEP);
				drawBricks(x,y,rowNum);
			}
		}
	}

	/*
	 * Draws a brick and assigns a color depending on row number
	 * 
	 * Pre: Empty world
	 * Post: Brick is drawn of specified color, width and height
	 */
	private void drawBricks(double x, double y, int rowNum) {
		bricks = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		bricks.setFilled(true);
		// assign colors to bricks rows
		if (rowNum == 0 || rowNum == 1) {
			bricks.setColor(Color.RED);
		}
		if (rowNum == 2 || rowNum == 3) {
			bricks.setColor(Color.ORANGE);
		}
		if (rowNum == 4 || rowNum == 5) {
			bricks.setColor(Color.YELLOW);
		}
		if (rowNum == 6 || rowNum == 7) {
			bricks.setColor(Color.GREEN);
		}
		if (rowNum == 8 || rowNum == 9) {
			bricks.setColor(Color.CYAN);
		}
		add(bricks);
	}

	/*
	 * Paddle is drawn
	 * 
	 * Pre: Empty world
	 * Post: Paddle of specified width and height is drawn
	 */
	private void createPaddle() {
		paddle = new GRect (getWidth() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Makes paddle follow the mouse along x axis within boundary of window
	 * 
	 * Pre: Stationary paddle
	 * Post: Paddle that follows movement of mouse along x axis
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		// sets right boundary of paddle on right wall
		if (mouseX >= getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		// makes paddle follow mouse
		paddle.setLocation (mouseX, getHeight() - PADDLE_Y_OFFSET);
	}

	/*
	 * Makes bullet fire from paddle when space bar is pressed
	 * 
	 * Pre: bullet does not fire from paddle 
	 * Post: bullet fires from paddle when space bar pressed
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (bullet == null) {
				bullet = new GOval(BULLET_DIAM, BULLET_DIAM);
				bullet.setFilled(true);
				bullet.setColor(Color.BLUE);
				add(bullet, paddle.getX() + 0.5*PADDLE_WIDTH, paddle.getY() - 1);
			}
		}

	}
	/*
	 * Draws and adds ball to center of window
	 * 
	 * Pre: Empty window
	 * Post: Ball draw at center of window
	 */
	private void createBall() {
		double ballWidth = BALL_RADIUS*2;
		ball = new GOval(ballWidth, ballWidth);
		ball.setFilled(true);
		// add ball to center of canvas
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}

	/*
	 * Makes ball bounce off top wall, right wall, left wall. Ball increases speed
	 * after reaching SPEED_INCREASE_BRICK_COUNT
	 * 
	 * Pre: Stationary ball
	 * Post: Ball that bounces off top wall, right wall, left wall and speeds up
	 */
	private void moveBall(int counter) {
		// makes ball bounce off left and right wall (adapted from BouncingBallSoln)
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		// makes ball bounce off top wall (adapted from BouncingBallSoln)
		if (hitTopWall(ball)) {
			vy = -vy;
		}
		ball.move(vx,  vy);
		// after 7 bricks gone, 
		if (counter <= SPEED_INCREASE_BRICK_COUNT) {
			ball.move(vx,  SPEED_INCREASE*vy);
		}
		// determines what object is at four corners of ball
		pause(DELAY);
	}

	/*
	 * Makes bullet move vertically upwards
	 * 
	 * Pre: stationary bullet
	 * Post: Bullet moves vertically upwards
	 */
	private void moveBullet() {
		if (bullet!= null) {
			bullet.move(0, -BULLET_SPEED);
		}
	}

	/*
	 * Provides conditions for ball collisions with bricks, paddle, bullet and null
	 * 
	 * Pre: Ball passes through bricks, paddle, bullet and null
	 * Post: Ball paased through bullet and null, bounces off of paddle, bounces off 
	 * and eliminates bricks
	 */
	private int checkForBallCollisions(int counter) {
		GObject collider = getCollidingObjectWithBall();
		if (collider != null) {
			if (collider != bullet) {
				// plays bouncing sound
				bounceClip.play();
				// ball bounces off paddle
				if (collider == paddle) {
					vy = -Math.abs(vy);
					// improves user control over bounces	
					if (Math.abs(paddle.getX() - ball.getX()) <= X_DIR_LEVERAGE) {
						vx = -vx;
					}
					// ball bounces off brick, brick disappears, counter records 
					// one less brick
				} else {
					remove(collider);
					vy = -vy;
					return counter - 1;
				}
			}
		}
		return counter;
	} 

	/*
	 * Removes bullet when it exits window
	 * 
	 * Pre: Bullets permanent and would often stop mid-screen when another is fired
	 * Post: Bullet is removed when it exits window so none would stop mid-screen
	 */
	private void bulletOffScreen() {
		if(bullet!= null) {
			// sets top wall of window as boundary
			if (bullet.getY() <= -BULLET_DIAM) {
				remove(bullet);
				bullet = null;
			}
		}
	}

	/*
	 * Returns whether or not ball should bounce off the top wall of the window 
	 * (adapted from BouncingBallSoln)
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Returns whether or not ball should ounce off the right wall of the window
	 * (adapted from BouncingBallSoln)
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Returns whether or not ball should ounce off the left wall of the window
	 * (adapted from BouncingBallSoln)
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* Returns getCollidingObject if there is either a paddle or brick at one of the
	 * four corners of the ball
	 * 
	 * Pre: Ball moves through paddle and bricks
	 * Post: Ball bounces off of paddle and bricks
	 */
	private  GObject getCollidingObjectWithBall() {
		// returns obj if object at one of the four corners of ball is not null
		GObject obj = getElementAt(ball.getX() ,ball.getY());
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(ball.getX() + 2*BALL_RADIUS ,ball.getY());
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(ball.getX() ,ball.getY() + 2*BALL_RADIUS);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(ball.getX() + 2*BALL_RADIUS ,ball.getY() + 2*BALL_RADIUS);
		if (obj != null) {
			return obj;
		} else {
			return null;
		}	
	}

	/*
	 * Sets conditons for bullet collisions with other objects
	 * 
	 * Pre: Bullet would go through all objects
	 * Post: Bullet passes through ball and null, collides and elimates bricks
	 */
	private int collideWithBullet(int count) {
		if (bullet != null) {
			GObject obj = getElementAt(bullet.getX(), bullet.getY());
			// if obj is a brick
			if (obj != paddle && obj != ball && obj != null) {
				remove(obj);
				remove(bullet);
				// plays bouncing sound
				bounceClip.play();
				bullet = null;
				return count - 1;
			}
		}
		return count;
	}

	/*
	 * Returns gameOver when the ball is null and the ball touches the bottom wall
	 */
	private boolean gameOver() {
		return (ball == null) || (ball.getY() >= getHeight() - ball.getHeight());	
	}

	/*
	 * Returns win when the brick counter reaches 0
	 */
	private boolean win(int counter) {
		return (counter == 0);
	}
	/*
	 * Creates a label that says "Click To Start" before mouse is clicked
	 * 
	 * Pre: No indication to click to start game
	 * Post: "Click to Start" label drawn before game starts that disappears when
	 * game starts
	 */
	private void createClickToStartLabel() {
		clickToStart = new GLabel("Click To Start");
		add(clickToStart, 0.5*getWidth() - 0.5*clickToStart.getWidth(), 0.4*getHeight());
	}

	/*
	 * Creates a label that says "GAMEOVER" once the game over condition is met
	 * 
	 * Pre: Game stops once gameOver condition is met
	 * Post: "GAMEOVER" label drawn when gameOver condition is met
	 */
	private void createGameOverLabel() {
		GLabel gameOver = new GLabel("GAME OVER");
		add(gameOver, 0.5*getWidth() - 0.5*gameOver.getWidth(), 0.5*getHeight() - 0.5*gameOver.getHeight());
	}

	/*
	 * Creates a label that says "YOU WON!!!" once the winning condition is met
	 * 
	 * Pre: Game stops once winning condition is met
	 * Post: "YOU WON!!!" label drawn when winning condition is met
	 */
	private void createYouWonLabel() {
		GLabel youWon = new GLabel("YOU WON!!!");
		add(youWon, 0.5*getWidth() - 0.5*youWon.getWidth(), 0.5*getHeight() - 0.5*youWon.getHeight());
	}

	/*
	 * Sets labels which indicate whether user won or lost
	 * 
	 * Pre: no labels to indicate win or loss
	 * Post: Label in center of window if win or game over condition is satisfied
	 */
	private void wonLostCondition() {
		// GAME OVER indication
		if (gameOver()) {
			createGameOverLabel();
		}
		// YOU WON!!! indication
		else {
			createYouWonLabel();
		}
	}
}
