/*
 * File: Breakout.java
 * -------------------
 * Name: Grace Taylor
 * Section Leader: Julia Truitt
 * 
 * The goal of Breakout is to remove all of the coloured bricks. If the ball 
 * collides with a brick, the brick is removed. Move the paddle at the bottom
 * of the screen with your mouse to keep the ball bouncing - don't let it touch
 * the bottom of the screen! If the ball falls off the bottom of the screen,
 * you lose a life. You have 3 lives to remove all the bricks on the screen.
 * 
 * Good luck! 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 15;

	// Separation between neighbouring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of ball in pixels
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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//Instance variable for paddle
	private GRect paddle = null;

	//Instance variable for ball
	private GOval ball = null; 

	//Instance variables for velocity
	private double vx;
	private double vy;

	//Instance variable for random generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variable for # of bricks on screen
	private int n_brick = NBRICK_COLUMNS * NBRICK_ROWS;


	//Instance variable for # of lives on screen
	private GLabel lives;

	public void run() {
		setUpGame();
		playGame();
	}

	private void setUpGame() {
		setUpScreen();
		setUpBricks();
		setUpPaddle();
		setUpBall();
		addMouseListeners();
	}

	private void setUpScreen() {
		//Sets the window's title bar text 
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBackground(Color.PINK);	
	}

	private void setUpBricks() {
	//	For loop creates the # of bricks specified in the constant NBRICK_ROWS
		for (int i = 0; i < NBRICK_ROWS; i++) {

			//For loop creates the # of columns specified in NBRICK_COLUMNS
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				
				//Size and create the bricks 
				double x = (getWidth() / 2) - ((NBRICK_COLUMNS * BRICK_WIDTH) / 2) - ((BRICK_SEP * (NBRICK_COLUMNS -1)) / 2) + ((BRICK_WIDTH + BRICK_SEP) * j);
				double y = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * i);
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(brickColour(i));
				add(brick);
			}
		}
		
	}

	/** 
	 * Method: brickColour
	 * ---------------------
	 * This method uses the variable i (which increases with every row of bricks made
	 * in the method setUpBricks). i determines the colour of each brick row so that
	 * every 3rd brick row changes colours.
	 *  
	 * @param i : refers to the number of brick rows that have been created
	 * @return : returns the new colour of the brick
	 */
	private Color brickColour(int i) {
		if (i % 10 == 0 || i % 10 == 1) {
			return(Color.RED);
		} else if (i % 10 == 2 || i % 10 == 3) {
			return(Color.ORANGE);
		} else if (i % 10 == 4 || i % 10 == 5) {
			return(Color.YELLOW);
		} else if (i % 10 == 6 || i % 10 == 7) {
			return(Color.GREEN);
		} else {
			return(Color.CYAN);
		}
	}

	private void setUpPaddle() {
		paddle = new GRect ((getWidth() / 2) - (PADDLE_WIDTH / 2), (getHeight() - PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.WHITE);
		add(paddle);
	}

	private void setUpBall() {
		ball = new GOval (((getWidth() / 2) - BALL_RADIUS), ((getHeight() / 2) - BALL_RADIUS), BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	/**
	 * Method: Mouse Moved 
	 * ------------------
	 * This method ensures the paddle at the bottom of the screen
	 * follows the player's mouse movement. 
	 */
	public void mouseMoved (MouseEvent e) {
		double mouse_x = e.getX();
		double mouse_y = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET); 
		if (e.getX() <= getWidth() - PADDLE_WIDTH) {
			//Sets the location of the paddle to follow the mouse coordinates
			paddle.setLocation(mouse_x, mouse_y);
		}	
	}

	/** 
	 * Method: Play Game
	 * -----------------
	 * Now that the bricks, paddle, and ball have been set up, the player can
	 * begin playing Breakout! 
	 * 
	 * The player begins with 3 lives which decrease every time the ball falls
	 * off the screen. The game starts when the player clicks the mouse and the
	 * ball begins moving. The game will continue until either the player has no
	 * lives (losing) or there are no bricks left on the screen (winning). 
	 */
	private void playGame() {
		int turn = 0;
		while (turn < NTURNS) {
			//Print # of lives the player has left
			lives = new GLabel ("Lives left: " + (NTURNS - turn), ((getWidth() / 2.75)), 30);
			lives.setColor(Color.RED);
			lives.setFont(SCREEN_FONT);
			add(lives);

			//Ball begins moving. Game continues while player has lives left.
			startBall();

			//Checks if player has won the game. If so, the while loop breaks and 
			//congratulations message is printed.
			if (n_brick == 0) {
				GLabel win = new GLabel ("Congratulations. YOU WON!");
				double c = (getWidth() / 2) - (win.getWidth());
				double d = (getHeight() / 2 + win.getAscent() / 2);
				win.setLocation(c, d);
				win.setColor(Color.WHITE);
				win.setFont(SCREEN_FONT);
				add(win);
				GImage img = new GImage("You Win.gif"); //adds a gif
				add (img, 0,0);
				break;
			}
			//turn++ reduces the amount of lives the player has left
			turn++;
			remove(lives);
		}
		//If the player runs out of lives, a message informing them of their loss is
		//printed.
		if (n_brick > 0) {
			GLabel lose = new GLabel ("You lose...") ;
			double e = (getWidth() / 2) - (lose.getWidth() / 2);
			double f = (getHeight() / 2 + (lose.getAscent() / 2));
			lose.setLocation(e, f);
			lose.setColor(Color.BLUE);
			lose.setFont(SCREEN_FONT);
			add(lose);
			GImage img = new GImage("You Lose.gif"); //adds a gif 
			add (img, 0,0);
		}
	}

	/** 
	 * Method: Start Ball
	 * -----------------
	 * After the user clicks their mouse, this method gets the ball rolling. Quite literally.
	 * The ball always starts in the centre of the screen at the start of every 
	 * life, and is assigned a random velocity between 1 and 3. 
	 */
	private void startBall() {
		//Sets ball location to middle of the screen for the start of every life
		ball.setLocation ((getWidth() / 2) - BALL_RADIUS, ((getHeight() / 2) - BALL_RADIUS));
		waitForClick();
		//Assigns the ball a random speed
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 3;
		//While loop keeps the ball within the limits of the screen
		while (ball.getY() <= getHeight() - (2 * BALL_RADIUS) && n_brick > 0) {
			ball.move(vx, vy);
			hitsObject();
			pause(DELAY);
		}
	}

	/** 
	 * Method: Hits Object
	 * -----------------
	 * This method makes the ball change directions if it hits something.
	 */
	private void hitsObject() {
		//If the ball hits a wall it changes directions
		if (ball.getX() <= 0 || ball.getX() >= (getWidth() - (BALL_RADIUS * 2))) {
			vx = -vx; 
		}
		//The ball only changes bounces off the top wall, not the floor
		if ((ball.getY() <= 0)) {
			vy = -vy;
		}
		checkForCollisions();
	}

	/**
	 * Method: Check For Collisions
	 * ----------------------
	 * This method checks if the ball has hit the paddle, which it does not 
	 * remove, or a brick, which must be removed. After hitting either the 
	 * paddle or brick, the ball bounces off the object and changes direction.
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		//Ensures the ball doesn't remove the paddle or the GLabel lives
		if (collider != null) {
			if (collider == paddle) {				
				vy = -Math.abs(vy);
			} else {
				//If the counter is lives, then do nothing
				if (collider != lives) {
					//Ensures the ball removes the brick before changing directions
					remove(collider);
					vy = -vy;
					//Keeps count of the # of bricks on the screen
					n_brick = n_brick - 1;
				}
			}
		}
	}

	/** 
	 * Method: Get Colliding Object
	 * --------------
	 * This method ensures that all four corners of the ball are aware if they 
	 * collide with a GObject. If getElementAt returns null for a particular corner,
	 * the method checks if the next corner is also null. If the value returned is not
	 * null, the GObject value where collision occurred is returned.
	 * 
	 * @return : returns the value of the collision 
	 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider == null) {
			collider = getElementAt (ball.getX() + 2 * BALL_RADIUS, ball.getY());
		}
		if (collider == null) {
			collider = getElementAt (ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt (ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return(collider);
	}
}
