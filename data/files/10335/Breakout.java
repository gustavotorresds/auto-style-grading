/*
 * File: Breakout.java
 * -------------------
 * Name: Ana Saavedra
 * Section Leader: Nidhi Manoj
 * 
 * This program implements the Breakout game, it has three key objects:the
 * ball, the paddle, and a set of bricks. The user has three turns to delete
 * all bricks on the screen. Every time the ball falls off the paddle a new
 * turn begins. The game game is over when there are not bricks on the screen.
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

	/*Instance variables*/
	private GRect rect;
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private double movingBall;
	private double turntrack;
	private double totalbricks = NBRICK_COLUMNS * NBRICK_ROWS;
	private GObject collider;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GLabel label;


	/*Run method: this method set up the game, it sets up the bricks, add mouse 
	 * listeners, creates the paddle, and plays the game. It has a loop to keep track 
	 * of the number of turns.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		
		brickColor();
		addMouseListeners();
		createPaddle();
		for (int t = 0; t < NTURNS; t++) {
			turntrack = NTURNS - t;
			ball();
			waitForClick();
			setUpBall ();
			movingBall ();
			checkWall();
			playGame();
		}
	}

	
	//Big Method 1: 1. set up bricks, 2. create the paddle and 3. create a ball;
	
	//Milestone 1: set up the bricks

	/*Method brickColor: this method establishes the number of bricks
	 * per row and column, as well as the location. It also defines a sequence
	 * to change colors.
	 * Precondition: none.
	 * Postcondition: bricks on canvas.
	 */
	private void brickColor () {
		for (double row = 0; row < NBRICK_ROWS; row++) {
			Color referenceColor = getNewColor(row);	
			for (int column=0; column < NBRICK_COLUMNS; column++) {
				double x = column * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP;
				double y = row * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				rect.setColor(referenceColor); 
				add (rect);
			}
		}
	}
	
	/*Method getNewColor: this method defines the sequence of colors for
	 * the set of bricks.
	 * Precondition: bricks have to be created.
	 * Postcondition: sequence of colors applied.
	 */
	private Color getNewColor (double row) {
		if (row % 10 == 0 || row % 10 == 1) {
			return Color.RED;
		} else if (row % 10 == 2 || row % 10 == 3) {
			return Color.ORANGE;
		} else if (row % 10  == 4 || row % 10 == 5) {
			return Color.YELLOW;
		} else if (row %10 == 6 || row % 10 == 7){
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	//Milestone 2: Create the paddle
	

	/*Method create Paddle: this method add the paddle into the Canvas, setting 
	 * the specific location on the screen and color.
	 * Precondition: none
	 * Postcondition: paddle on the screen.
	 */
	private void createPaddle () {
		paddle = new GRect(0, (getHeight()-PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add (paddle);
	}

	/*Method mouseMoved: this method creates the mouse moved method that 
	 * interacts with the paddle when the mouse is moved. Includes the 
	 * physical space for the interaction.
	 * Precondition: paddle in the canvas.
	 * Postcondition: paddle moves in synchrony with the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		paddle.setLocation(mouseX - PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); {
			if(paddle.getX() <= 0) {
				paddle.setLocation(0, (getHeight()-PADDLE_Y_OFFSET));
			}
			else {
				if(paddle.getX() >= getWidth() - PADDLE_WIDTH) {
					paddle.setLocation((getWidth() - PADDLE_WIDTH), (getHeight()-PADDLE_Y_OFFSET));
				}
			}
		}
	}


	//Milestone 3: create ball
	
	/*Method ball: this method creates the ball for the game, it sets size 
	 * and color.
	 * Precondition: none.
	 * Postcondition: ball on the screen.
	 */
	private void ball () {
		ball = new GOval((getWidth()/2 - BALL_RADIUS), (getHeight()/2 - BALL_RADIUS), BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	//Big Method 2: plays the game

	//Milestone 4: Get ball to bounce off the walls


	/*Method setUpBall: this method sets up the range for the horizontal
	 * velocity for the ball. It also establishes, the random generation of
	 * velocity during the game.
	 * Precondition: ball on the screen. 
	 * Postcondition: ball's random velocity assigned.
	 */
	private void setUpBall () {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean (0.5)) vx = -vx;
	}


	/*Method moving the Ball: this method establishes the movement of the
	 * ball. It also includes a pause
	 * Precondition: velocity of the ball defined.
	 * Postcondition: ball randomly moved.
	 */
	private void movingBall () {
		ball.move(vx, vy);
		pause(DELAY);
	}

	/*Method checkWall: this method checks the walls horizontally and changes the
	 * direction of the ball's velocity. It also establishes the vertical limits 
	 * for the ball. If the ball reaches the top wall it changes direction, if it
	 * touches the bottom it does not change the direction nor returns ball. 
	 * Precondition: ball moving on the screen.
	 * Postcondition: depends on x or y axis.
	 */
	private boolean checkWall () {
		if(ball.getX() <= 0 || ball.getX() + BALL_RADIUS * 2 >= getWidth()) {
			vx = -vx;
		}
		if(ball.getY() <= 0) {
			vy = -vy;
		}
		if(ball.getY() >= getHeight()) {
			return false;
		}
		return true;
	}

	/*Method colliderCheck: this method checks for for collisions when one of the ball's
	 * corners is not available.
	 * Precondition: ball moving.
	 * Postcondition: if corners are not visible GObject otherwise ball keeps moving.
	 */
	public GObject colliderCheck () {
		double ballx = ball.getX();
		double bally = ball.getY();
		if (getElementAt(ballx, bally) != null) {
			return getElementAt(ballx, bally);
		} else if (getElementAt(ballx + BALL_RADIUS * 2, bally) != null) {
			return getElementAt(ballx + BALL_RADIUS * 2, bally);
		} else if (getElementAt(ballx, bally + BALL_RADIUS * 2) != null) {
			return getElementAt(ballx, bally + BALL_RADIUS * 2);
		} else if (getElementAt(ballx + BALL_RADIUS * 2, bally + BALL_RADIUS * 2) != null) {
			return getElementAt(ballx + BALL_RADIUS * 2, bally + BALL_RADIUS * 2);
		}
		return null;
	}


	/*Method removeBricks: this method checks if there are objects on the screen. First, it
	 * checks for the paddle, if the object is the paddle it changes the direction of the ball.
	 * Second, if the object is a brick, it removes the brick and keeps track of the bricks.
	 * Precondition: colliderCheck method.
	 * Postcondition: colliding object dependent. 
	 */
	public void removeBricks () {
		GObject collider = colliderCheck();
		if (collider != null) {
			if (collider == paddle) {
				vy = -Math.abs(vy);
			} else if (collider != ball) {
				remove (collider); 
				vy = -vy;
				totalbricks --;
			}
		}
	}

	/*Method Play the game: this method establishes the steps for playing the game. 
	 * It calls previously defined methods to play the game
	 * Precondition: big method 1 implemented.
	 * Postcondition: game running or finished.
	 */
	public void playGame () {		
		while (true) {
			movingBall();
			if (!checkWall()) {
				remove (ball);
				break;
			}
			removeBricks();
			if (totalbricks == 0) {
				vy = 0.0;	
				remove (ball);
				break;
			}
			pause(DELAY);
		}	
	}
}