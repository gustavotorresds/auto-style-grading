/*
 * File: Breakout.java
 * -------------------
 * Name: Annie Minondo
 * Section Leader: James Zhuang 
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
	public static final double VELOCITY_Y = 8.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	//Create Paddle as an instance variable so it can be tracked
	GRect paddle = null;
	GOval ball = null;
	
	//Instance Variable for Random Generation of velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Instance Variable to track velocity of ball
	private double vx;
	private double vy;
	
	//Instance variable to count the total number of bricks / lives. 
	private int livesCounter = 1;
	private double brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
	
	
	/* This program takes in the mouse's location (x, y) and displays it on the left side of the screen. When the mouse hovers over the label, it turns from blue to red.
	 * PRE: Blue label, at (0 , 0) at the left, center of screen.
	 * POST: Blue label (turns red if hovered over) displays (x, y) of mouse at any location. 
	 */

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Builds and colors bricks and paddle. 
		setUp();
		addMouseListeners();
		//Starts animation loop and allows player to begin the game. 
		play();
	}
	
	/*This method sets up the world for the Breakout game. It displays a starter message and begins after the player clicks.
	 * PRE: Blank canvas
	 * POST: Set of Bricks, rainbow colored of dimensions provided above on screen. A paddle is centered at the bottom of the screen, and a ball is placed in the center of the screen. 
	 * */
	private void setUp() {
		double x = (getWidth() / 2) - ((NBRICK_COLUMNS / 2.0) * (BRICK_WIDTH)) - ((NBRICK_COLUMNS - 1)/2.0 * BRICK_SEP);
		setUpRows(x);
		buildPaddle();
		starterMessage();
		ball = buildBall();
	}
	
	//This Method displays the number of lives the player has left out of the number provided above (NTURNS). Then it prompts the player to "click" to start. 
	private void starterMessage() {
		GLabel label = makeLabel("Lives: " + livesCounter + " out of " + NTURNS);
		add(label);
		pause(DELAY * 100);
		remove(label);
		GLabel label2 = makeLabel("Click to Play");
		add(label2);
		waitForClick();
		remove(label2);
	}
	
	/*This method commands the entire "playing" part of the game. It animates the ball and makes sure that it 
	 * 1. Bounces off walls
	 * 2. Bounces off the paddle
	 * 3. Destroys Bricks
	 * 4. Runs only for the amount of turns decided
	 * 
	 * PRE: Game's world is built
	 * POST: Stops when the player wins by destroying all bricks or if they run out of turns. 
	 *  */
	private void play() {
		//determines an x - velocity between the maximum and minimum and sets it equal to vx
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		//sets vy equal to velocity decided above
		vy = VELOCITY_Y;
		//Determines if original vx will be positive or negative, randomly. This determines which direction the ball will move in after the player clicks to start 
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		//Animation Loop: moves ball, makes it bounce off walls, and regulates collisions and responses 
		while(true) {
			ball.move(vx, vy);
			pause(DELAY);
			bounceOffWalls();
			GObject collider = getCollidingObject();
			collideCheck(collider);
		}
	}
	
	/* This method makes sure the ball responds appropriately to different objects it collides with:
	 * 1. If it collides with the paddle, it changes direction (or bounces)
	 * 2. If it collides with a brick (designated as not nothing, or != null) it removes the brick, and changes direction
	 * 3. It maintains count of the number of bricks remaining, and if there are no more bricks, it displays the message "CONGRATS! YOU WON" to the player.
	 * 
	 * PRE: Ball bouncing off of either the paddle, nothing, or a brick. 
	 * POST: Either a ball bouncing off a paddle, destroying a brick, or the player winning (eventually). 
	 * */
	private void collideCheck(GObject collider) {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (collider == paddle) {
			bounceClip.play();
			//This if statement tests that the ball is not "sticking" to the paddle. Makes sure that if a top sensor (of the ball) bounces off the paddle a second time and tries to go up, it does not respond.
			if(vy > 0) {
				vy = -vy;
			}
		} else if (collider != null) {
			bounceClip.play();
			remove(collider);
			brickCounter --;
			vy = -vy;
		} else if (brickCounter == 0) {
			remove(ball);
			livesCounter = NTURNS + 1;
			GLabel label = makeLabel("CONGRATS! YOU WON");
			add(label);
			remove(paddle);
		}
	}
	
	//This method makes labels, takin in a String
	private GLabel makeLabel(String str) {
		GLabel label = new GLabel(str);
		double xLabel = (getWidth() - label.getWidth())/2;
		double yLabel = (getHeight() + label.getAscent())/2;
		label.move(xLabel, yLabel);
		return(label);
	}
	
	/*This method makes the ball bounce off the top, and side walls. It also makes the ball not bounce off the bottom wall, but rather the player lose a life. These lives are counted down from NTURNS through a method called 'livesCheck'
	 * PRE: Ball is animated and bouncing
	 * POST: Ball bounces off top and side walls, but not off bottom wall 
	 * */
	private void bounceOffWalls() {
		double leftWall = 0;
		double rightWall = getWidth() - (BALL_RADIUS * 2);
		double bottomWall = getHeight() - (BALL_RADIUS * 2);
		double topWall = 0;
		if (ball.getX() <= leftWall || ball.getX() >= rightWall) {
			vx = -vx;
		}
		if (ball.getY() <= topWall) {
			vy = -vy;
		} else if (ball.getY() >= bottomWall) {
			remove(ball);
			livesCheck();
		}		
	}

	/*This method tells the player what number life they are on out of NTURNS. 
	 * Then, it prompts them to play again by clicking. 
	 * 
	 * However, if the number of lives used (livesCounter) equals NTURNS, it displays the message "GAME OVER" and ends the game. 
	 * */
	private void livesCheck() {
		if(livesCounter < NTURNS) {
			livesCounter ++;
			GLabel label = makeLabel("Lives: " + livesCounter + " out of " + NTURNS);
			add(label);
			pause(DELAY * 100);
			remove(label);
			ball = buildBall();
			waitForClick();
			ball.move(vx, vy);
		} else if (livesCounter == NTURNS) {
			GLabel label = makeLabel("GAME OVER");
			add(label);
			remove(paddle);
		}		
	}

	/* This method uses the ball's (x, y) at all of its four corners to determine if there is an object it in its way. 
	 * It then returns the object, so that it can be manipulated in the previous method. 
	 * */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		double diameter = BALL_RADIUS * 2;
		if (getElementAt(x, y) != null) {
			return getElementAt(x, y);
		} else if (getElementAt(x + diameter, y) != null) {
			return getElementAt(x + diameter, y);
		} else if (getElementAt(x, y + diameter) != null) {
			return getElementAt(x, y + diameter);
		} else if (getElementAt(x + diameter, y + diameter) != null) {
			return getElementAt(x + diameter, y + diameter);
		} 
		return null;
	}
	
	/*This method uses the method "buildRow" and runs it through a for loop that builds NBRICK_ROWS amount of rows.  
	 * */
	private void setUpRows(double x) {
		for(int i = 0; i < NBRICK_ROWS; i ++) {
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * i;
			Color color = getColor(i);
			buildRow(x, y, color);
		}
	}
	
	/*This method determines the color of the row of bricks, as called by the setUpRows method. It then returns the color to the previous method. 
	 */
	private Color getColor(int i) {
		if(i <= 1) {
			Color color = Color.RED;
			return color;
		} else if(i <= 3) {
			Color color = Color.ORANGE;
			return color;
		} else if(i <= 5) {
			Color color = Color.YELLOW;
			return color;
		} else if(i <= 7) {
			Color color = Color.GREEN;
			return color;
		} else if(i <= 9) {
			Color color = Color.CYAN;
			return color;
		}
		return null;
	}
	
	/*This method builds a row of NBRICK_COLUMNS number of bricks, by calling on the drawRectangle method. 
	 * */
	private void buildRow(double x, double y, Color color) {
		for(int i = 0; i < NBRICK_COLUMNS; i ++) {
			double newX = x + (BRICK_WIDTH + BRICK_SEP) * i;
			drawRectangle(newX, y, color);
		}
	}
	
	//Draws a rectangle with the x, y, color values inputed
	private void drawRectangle(double x, double y, Color color) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}

	/*Mouse Event: Mouse Moved 
	 * gets X from Mouse, and Y is static
	 * 
	 * Makes paddle move with the mouse, but uses parameters to ensure that the paddle does not go off the canvas of the screen. 
	 * */
	public void mouseMoved (MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		if(x < PADDLE_WIDTH/2) {
			paddle.setLocation(0, y);
		} else if(x > getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		} else {
			
			paddle.setLocation(x  - PADDLE_WIDTH/2, y);
		}
	}
	
	//Calls a method that builds a rectangle that will serve as the paddle
	private void buildPaddle() {
		paddle = drawPaddle();
		add(paddle);
	}
	
	//Method that actually builds the paddle, and returns it to commanding method. 
	private GRect drawPaddle() {
		double y = getHeight() - PADDLE_Y_OFFSET;
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		GRect paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	//Method that builds the ball, using set parameters and adding it to the screen, while also returning it to previous method. 
	private GOval buildBall() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval (size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, x, y);		
		return(ball);
	}
	
}
