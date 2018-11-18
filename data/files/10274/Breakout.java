/*
 * File: Breakout.java
 * 
 * Name: Jack Nichols
 * 
 * Section Leader: Marilyn Zhang
 * 
 * Sources: CS106A Style Guide (Assignment 2); The Art & Science of Java by Eric S. Roberts
 * 
 * Description: This program creates N rows of N bricks, one moving ball, and one paddle
 * (whose x-position changes with the mouse). The ball bounces off both the paddle and the
 * bricks, but it only removes the bricks. A life is expended when the ball misses the paddle
 * and hits the lower boundary. The object of the game is to remove all bricks, given the three
 * lives which each user is afforded. This game is called "Breakout". 
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
	
	// The paddle is referred to in both the run method and the method "createPaddle()".
	GRect paddle = null;
	// The ball is referred to in many methods and, therefore, must be established as an instance variable.
	GOval ball = null;
	// This variable generates a random number. It is used in determining the x-velocity of the ball.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// The speeds (x and y) of the velocity are changed throughout the program. 
	private double vx, vy;
	// MouseX is needed to allow the user to move the paddle. It is referred to several times in different methods. 
	private double mouseX;
	// The variable hitCount is referred to in the run method and in the winning condition boolean. 
	private int hitCount = 0;

	public void run() {
		setUpGame();
		addMouseListeners();
		/*
		 *  "turnNumber" starts at 0 and is then incremented each time the ball fulfills the
		 *  losing condition, which is falling below the lower boundary. 
		 */
		int turnNumber = 0;
		/*
		 * The "while" loop should only be entered when the user is prepared; therefore, my
		 * program requires that the user clicks the screen. 
		 */
		waitForClick();
		/*
		 * This "while" loop essentially plays the game. It animates the ball, reverses the ball's
		 * velocity when it hits a wall, removes contacted bricks, and causes the ball to bounce off
		 * the paddle. The conditions of this loop are that the the "turnNumber" must be less than 
		 * the maximum number of lives alloted and the winning condition cannot be true.
		 */
		while (turnNumber < NTURNS && winningConditionsMet() != true) {
			ball.move (vx, vy);
			pause(DELAY);
			/*
			 *  If the ball hits a wall on the right or left, a bounce is shown by a simple
			 *  reverse in the x-velocity.
			 */
			if (ballHitsXWall()) {
				vx = - vx; 
			}
			/*
			 * Likewise, if the ball hits a wall on the bottom, a bounce is shown by a simple
			 * reverse in the y-velocity.
			 */
			if (ballHitsYWall()) {
				vy = - vy;
			}
			// This method calls collider to check for collisions.
			GRect Collider = findCollider();
			// If the ball hits the paddle, it must reverse directions.
			if (Collider == paddle) {
				/*
				 * Any time that the ball hits the paddle, it must be moving from a positive 
				 * (downwards) y-velocity to a negative (upwards). If the ball's velocity is not
				 * specified to always be changing to negative, then the ball may experience a glitch
				 * in which it appears to "stick" to the paddle. This happens because the lower sensor
				 * on the ball hits the paddle (changing the direction). Then, the upper sensor hits the
				 * paddle and changes the direction, and a loop is formed.
				 */
				vy = - Math.abs(vy); 
			}
			if (Collider != paddle && Collider != null) {
				/*
				 * If the collider is not the paddle and it is not null, then the collider must
				 * be a brick. Each brick should be removed, the velocity must be reversed, and 
				 * one must be added to the tally of hits. 
				 */
				remove(Collider);
				vy = - vy;
				hitCount++;
			}
			if (losingConditionsMet()) {
				/*
				 * This condition occurs when the ball falls off the screen and the losing
				 * condition is satisfied. A turn/life is lost. The ball is reset, and, if
				 * another life remains, then the program must wait for a click to re-initiate.
				 */
				turnNumber++;
				resetBall();
				if (turnNumber < NTURNS) {
					waitForClick();
				}
			}
		}
		// Once the while loop is exited, either the winning condition has been met
		// or the turnNumber has become equal to the maximum allowed.
		if (winningConditionsMet()) {
			// In this case, the positive label is displayed. 
			winGame();
		}
		if (turnNumber == NTURNS) {
			// In this case, the negative label is displayed.
			loseGame();
		}
	}
	
	/*
	 * This method sets up the game by creating the three fundamental elements -- the ball, 
	 * the bricks, and the paddle. It also takes care of simple things such as setting 
	 * the appropriate size of the canvas and setting a title.
	 */
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle();
		createBall();
	}
	
	/*
	 * The creation of the bricks uses a method which creates two rows at a time. 
	 * The parameters that are passed through for each pair of rows are the appropriate 
	 * color and the correct Y-coordinate for the bottom row.
	 */
	private void createBricks() {
		createTwoRows(Color.CYAN, (BRICK_Y_OFFSET + (10*BRICK_HEIGHT) + (9*BRICK_SEP)));
		createTwoRows(Color.GREEN, (BRICK_Y_OFFSET + (8*BRICK_HEIGHT) + (7*BRICK_SEP)));
		createTwoRows(Color.YELLOW, (BRICK_Y_OFFSET + (6*BRICK_HEIGHT) + (5*BRICK_SEP)));
		createTwoRows(Color.ORANGE, (BRICK_Y_OFFSET + (4*BRICK_HEIGHT) + (3*BRICK_SEP)));
		createTwoRows(Color.RED, (BRICK_Y_OFFSET + (2*BRICK_HEIGHT) + (1*BRICK_SEP)));
	}
	
	/*
	 * This method utilizes a double "for" loop to create two identical rows of bricks. The 
	 * y-coordinate which was passed through as a parameter is henceforth called the begginingY
	 * variable.
	 */
	private void createTwoRows(Color color, double beginningY) {
		for (int row = 0; row < 2; row++) {
			for (int brickNumber = 0; brickNumber < NBRICK_COLUMNS; brickNumber++) {
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				// The x-coordinate starts at the leftmost part of the first brick and then 
				// moves one brick width and one brick separation length for each additional brick.
				double x = (getWidth() / 2) - (BRICK_SEP /2) - (BRICK_WIDTH * 5) - (BRICK_SEP * 4) + ((BRICK_WIDTH + BRICK_SEP) * brickNumber);
				// For the first row, the y-coordinate was passed through the method.
				// For the second row, the y-coordinate is the same with one additional brick height
				// and an additional brick separation.
				double y = beginningY - ((BRICK_HEIGHT + BRICK_SEP) * row);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick, x, y); 	
			}
		}
	}
	
	/*
	 * The paddle is a rectangle that must begin in the exact x-center of the screen and 
	 * an appropriate distance away from the lower y-boundary.
	 */
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT); 
		paddle.setFilled(true); 
		add(paddle, ((getWidth() / 2) - (PADDLE_WIDTH / 2)), (getHeight() - PADDLE_Y_OFFSET));
	}
	
	/*
	 * Since we added MouseListeners in the run method, we can now employ MouseMoved 
	 * to adhere the paddle to whichever x-coordinate the user places the mouse.
	 */
	public void mouseMoved(MouseEvent e) { 
		mouseX = e.getX();
		if (paddleInPlay()) {
			// By setting location, the paddle never moves away from the x-position of the mouse.
			// Meanwhile, the y-coordinate does not change.
			paddle.setLocation(mouseX - (PADDLE_WIDTH/2), (getHeight() - PADDLE_Y_OFFSET));
		}
	}
	
	/*
	 * The ball is a simple GOval with diameter twice the size of the radius. It should be placed
	 * in the exact center of the screen, which means that it needs to be adjusted because GOval
	 * tracks the top left corner of the ball.
	 */
	private void createBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS / 2);
		// The y-velocity is a constant which can be easily manipulated. 
		vy = VELOCITY_Y;
		// The x-velocity is randomly generated between two numbers (a min and a max). 
		// Additionally, it is set to be negative approximately half the time. 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}
	}
	
	/*
	 * Each time the ball falls through the bottom boundary, it must be reset and placed
	 * in the middle. This method removes the ball (which we understand to be falling somewhere
	 * off the screen) and adds a new ball with the same conditions. The reason a new ball must be 
	 * created (rather than the old ball's location simply reset) is that we need a new x-velocity
	 * in order to add some variety to the game. 
	 */
	public void resetBall() {
		remove(ball);
		// The new ball is put in the same location (the center of the canvas).
		add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS / 2);
	}
	
	/*
	 * This method labels whichever object the ball comes into contact with as the "collider". 
	 * To do this, it checks for collisions at the four "corners" (labeled NECornerObj, 
	 * NWCornerObj, SECornerObj, and SWCornerObj). Using the "getElementAt" command, the object
	 * involved in the collision is labeled as the "collider". If no object can be found at 
	 * the sensor, then a value of null is returned. 
	 */
	private GRect findCollider() {
		// the top right corner
		GRect NWCornerObj = getElementAt (ball.getX(), ball.getY()); 
		if (NWCornerObj != null) {
			GRect collider = NWCornerObj; 
			return(collider);
		}
		// the top left corner
		GRect NECornerObj = getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY());
		if (NECornerObj != null) {
			GRect collider = NECornerObj; 
			return (collider);
		}
		// the bottom left corner
		GRect SECornerObj = getElementAt (ball.getX(), ball.getY() + (BALL_RADIUS * 2));
		if (SECornerObj != null) {
			GRect collider = SECornerObj; 
			return (collider); 
		}
		// the bottom right corner
		GRect SWCornerObj = getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY() + (BALL_RADIUS * 2));
		if (SWCornerObj != null) {
			GRect collider = SWCornerObj; 
			return (collider); 
		// If no object is found at any of these sensors, then a value of null must be returned. 
		} else { 
			return(null);
		}	
	}
	
	// This boolean checks if the mouse is within the canvas. If it is not in the canvas,
	// then the location of the paddle should be left at either boundary. 
	private boolean paddleInPlay() { 
		return (mouseX < (getWidth() - PADDLE_WIDTH/2) && mouseX > (PADDLE_WIDTH/2));
	}
	
	// This boolean checks to see if the ball hits either the left or right wall.
	// It is used to check if a change in velocity (bounce) is needed. 
	private boolean ballHitsXWall() {
		return (ball.getX() < 0 || ball.getX() > getWidth() - 2 * BALL_RADIUS);
	}
	
	// This boolean checks to see if the ball hits the top wall. It is used to check if 
	// a change in velocity (bounce) is needed. 
	private boolean ballHitsYWall() {
		return (ball.getY() < 0);
	}
	
	// If the ball removes every brick, then the game should be concluded. The total number
	// of bricks is equal to the number of rows times the number of columns.
	private boolean winningConditionsMet() { 
		return (hitCount == NBRICK_ROWS * NBRICK_COLUMNS);
	}
	
	// If the ball falls below the lower boundary, this situation is referred to as 
	// a losing condition. A life is lost or, if all lives are already exhausted, 
	// then the game must be concluded. 
	private boolean losingConditionsMet() { 
		return (ball.getY() > getHeight());
	}
	
	/*
	 * When all bricks are destroyed, the game is understood to be won. This method 
	 * creates a label which reads "CONGRATS, YOU WIN!" in the center of the screen.
	 */
	public void winGame() {
		GLabel YouWin = new GLabel ("CONGRATS, YOU WIN!"); 
		YouWin.setFont("Helvetica-24");
		YouWin.setLocation(getWidth() / 2 - YouWin.getWidth() / 2, getHeight() / 2);
		add(YouWin);
	}
	
	/*
	 * When all turns/lives are exhausted, the game is lost, the ball is removed, and 
	 * a label which reads "GAME OVER." is placed in the center of the screen. 
	 */
	public void loseGame() {
		remove(ball);
		GLabel YouLose = new GLabel ("GAME OVER."); 
		YouLose.setFont("Helvetica-24");
		YouLose.setLocation(getWidth() / 2 - YouLose.getWidth() / 2, getHeight() / 2);
		add(YouLose);
	}
}