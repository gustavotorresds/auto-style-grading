/*
 //
 * -------------------
 * Name: Benjamin Simon
 * Section Leader: Julia Truitt
 * 
 * This file will eventually implement the game of Breakout.
 * The goal of the game is to break all of the bricks
 * in the given arena by sliding the paddle to bounce the ball.
 * The program will set up the game in the canvas
 * window, creating bricks, paddle, and fixing the 
 * starting velocity of the ball. The game is then played,
 * and for a given number of turns, the user then attempts
 * to break all of the bricks in the game console by bouncing
 * the ball off the paddle. If he succeeds, he will be shown
 * a winning screen, if he loses, he will be shown
 * a losing screen (in both circumstances, the game is
 * over).
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

//import com.sun.prism.paint.Color;

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

	// Number of bricks in the game (that the user must break to win)
	public static final int TOTAL_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

	private GRect paddle = null;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//this variable is updated each time a brick is broken
	private int bricksBroken = 0;

	/* The run method first titles and sets the canvas size.
	 * It then sets up the game by creating the bricks, paddle
	 * and setting the ball velocity. Then, for a given number of turns
	 * (signified by the for loop), the game is played. If the user 
	 * breaks all of the bricks, then the game ends and the for loop
	 * exits since turn is set to equal the number of total turns.
	 * Otherwise, the game is played until the user does not have
	 * any turns left. At that point, if the user has not already
	 * broken all of the bricks, that means that he has lost,
	 * so he is shown a losing message.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		for(int turn = 0; turn < NTURNS; turn++) {
			playGame();
			if (bricksBroken == TOTAL_BRICKS) {
				addWinScreen();
				turn = NTURNS;
			}
		}
		if (bricksBroken != TOTAL_BRICKS) {
			addLoseScreen();
		}
	}

	/* This method sets up the game, first adding all
	 * of the bricks to the screen, then by
	 * creating the paddle, then, using the addMouseListeners method,
	 * the program tracks the location of the mouse's x value and 
	 * uses that to move the paddle. Lastly, the method sets up the
	 * initial velocities of the ball, which are updated
	 * in the playGame method.
	 */
	private void setUpGame() {
		addBricks();
		createPaddle();
		addMouseListeners();
		setVelocity();
	}

	/* This method uses a nested for loop to add
	 * all of the bricks to the console screen. The outer
	 * for loop adds each row of bricks, and the inner one
	 * fills each row by column. Two aspects of this method are
	 * noteworthy and require further elaboration: first, the
	 * xOffset variable is defined in order to make sure that 
	 * each row of bricks is centered in the console screen (this
	 * seemed necessary because, as the number of columns increased,
	 * the empty space on one of the sides of the brick columns also
	 * increased). 
	 * 
	 * The second noteworthy thing is the way the rows
	 * are colored. The method structures the coloring of the rows
	 * in such a way that allows the coloring pattern to repeat, if more
	 * than 10 rows are requested. It does this by making use of 
	 * the remainder operation.
	 */
	private void addBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				double x = 0;
				double xOffset = (getWidth() - ((BRICK_WIDTH * NBRICK_COLUMNS) + ((BRICK_SEP) * (NBRICK_COLUMNS - 1))))/2;
				if (column == 0) {
					x = xOffset;
				}
				else {
					x = (xOffset + column * (BRICK_SEP + BRICK_WIDTH));
				}
				double y = (BRICK_Y_OFFSET + row * (BRICK_SEP + BRICK_HEIGHT));
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (row % 10 == 0 || row % 10 == 1) {brick.setFillColor(Color.RED);}
				if (row % 10 == 2 || row % 10 == 3) {brick.setFillColor(Color.ORANGE);}
				if (row % 10 == 4 || row % 10 == 5) {brick.setFillColor(Color.YELLOW);}
				if (row % 10 == 6 || row % 10 == 7) {brick.setFillColor(Color.GREEN);}
				if (row % 10 == 8 || row % 10 == 9) {brick.setFillColor(Color.CYAN);}
				add(brick);
			}
		}
	}

	/* This method creates a paddle with height and width
	 * as constants. The location is initially set on the left
	 * side of the screen, on the same x-plane that the paddle 
	 * will remain even as the user's mouse moves. 
	 */
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		paddle.setLocation(0, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		add(paddle);
	}

	/* This method is called by mouseListeners,
	 * and it uses the MouseEvent "mouseMoved," which
	 * is updated each time the user moves the mouse.
	 * This method simply resets the value of the x location
	 * of the paddle, and it ensures that the paddle stays
	 * within the bounds of the canvas by only moving the paddle
	 * to the x location of the mouse if that x location is within
	 * a paddle's width distance from the right side of the canvas.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX <= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(mouseX, (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET));
		}
	}	

	/* This method sets the initial velocity of the
	 * ball, which is created in the playGame method.
	 * The downward velocity is set to a constant, and the 
	 * horizontal velocity is set to a random value (using
	 * the instance variable rgen which uses a random
	 * number generator) between two
	 * constants (min and max X velocity) and goes in
	 * either the left or right direction.
	 */
	private void setVelocity() {
		vy = VELOCITY_Y; 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/* This method first creates a ball. I chose to create
	 * the ball here, because the creation of the ball
	 * is directly linked to and contingent upon the number
	 * of turns the user has left (if he has none, then the ball
	 * should not be created). This is why I wanted to 
	 * put it inside the for loop, and by putting it inside
	 * playGame, I could avoid declaring it as an instance variable.
	 * The ball is initialized and then set to equal whatever GOval the
	 * createBall method returns.
	 * 
	 * The method then waits for a click, and creates a boolean called
	 * alive that keeps the while loop (for moving the ball) going 
	 * as long as alive=true. Alive is made to equal false in two instances,
	 * one if the ball goes past the paddle and hits the bottom of the
	 * canvas, and the other if the user breaks all of the bricks.
	 * 
	 * Inside the while(alive) animation loop, the ball moves using a
	 * pause of length that is a constant. The ball changes direction if it
	 * hits the upper wall or the two side walls--this is done by flipping 
	 * the signs of vy and vx, respectively.
	 * 
	 * The method then determines whether the ball has collided with
	 * either the brick or the paddle by calling a method to 
	 * get the colliding object (and plugging in the parameters of the
	 * ball's x and y location). If there is a colliding object, and it is 
	 * the paddle, the ball bounces off the paddle. This is done not by
	 * switching the signs of vy, but by making vy equal to the 
	 * negative absolute value of vy, such that, if the ball
	 * hits the paddle in such a way that getCollidingObject continuously
	 * registers the paddle as a colliding object (creating a vibrating effect)--if
	 * this happens, then the ball won't "stay" on the paddle because the velocity
	 * won't switch signs if the ball is already on its way up (if vy is negative).
	 * 
	 * If there is a collider (if not null), and that collider is not the paddle,
	 * it must be a brick. We then remove the brick, change the direction of the ball,
	 * and add one to our brick counter, "bricksBroken."
	 * 
	 * If, as is previously mentioned, the ball goes past the paddle and hits
	 * the bottom wall, or if the user breaks all of the bricks, the ball is removed
	 * and the loop is exited by making the while loop boolean "alive" = false.  
	 */
	private void playGame() {
		GOval ball = createBall();
		waitForClick();
		boolean alive = true;
		while(alive){
			ball.move(vx, vy);
			pause(DELAY);
			if(ball.getY() <= 0) {
				vy = -vy;
			}
			if(ball.getX() >= getWidth() - BALL_RADIUS * 2 || ball.getX() <= 0) {
				vx = -vx;
			}
			GObject collider = getCollidingObject(ball.getX(), ball.getY());
			if (collider == paddle) {
				vy = -(Math.abs(vy));
			}
			else if (collider != null){
				remove(collider);
				vy = -vy;
				bricksBroken++;
			}
			if (ball.getY() >= getHeight() - BALL_RADIUS * 2) {
				remove(ball);
				alive = false;
			}
			if (bricksBroken == TOTAL_BRICKS) {
				remove(ball);
				alive = false;
			}
		}
	}

	/* This method creates a ball with specified width and height and
	 * adds it to the center of the screen. This ball is then passed
	 * back to the playGame method using a return statement. 
	 */
	public GOval createBall() {
		GOval ball = new GOval ((((getWidth())/2) - BALL_RADIUS), (((getHeight()/2) - BALL_RADIUS)), 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return(ball);
	}

	/* This method checks for a colliding object around
	 * the ball. It takes as parameters the x and y value of 
	 * the ball at a given location, then checks the corner values
	 * of the "square" in which the ball is inscribed. If any of these
	 * points is touching an element, the method returns that 
	 * element (the "collider"). If not, the method returns null.
	 */
	private GObject getCollidingObject(double x, double y) {
		if (getElementAt(x, y) != null) {
			return getElementAt(x, y);
		}
		else if (getElementAt(x + (2 * BALL_RADIUS), y) != null) {
			return getElementAt(x + (2 * BALL_RADIUS), y);
		}
		else if (getElementAt(x, y + (2 * BALL_RADIUS)) != null) {
			return getElementAt(x, y + (2 * BALL_RADIUS));
		}
		else if (getElementAt(x + (2 * BALL_RADIUS), y + (2 * BALL_RADIUS)) != null) {
			return getElementAt(x + (2 * BALL_RADIUS), y + (2 * BALL_RADIUS));
		}
		else {
			return(null);
		}
	}

	/* This method adds a win message to the
	 * user if he has broken all of the bricks
	 * in the game. It displays the message in the center
	 * of the console in magenta.
	 */
	private void addWinScreen() {
		GLabel winMessage = new GLabel("Congratulations, you won!", 100, 100);
		winMessage.setLocation((getWidth()/2.0 - winMessage.getWidth()/2.0), (getHeight()/2.0 - winMessage.getAscent()/2.0));
		winMessage.setColor(Color.MAGENTA);
		add(winMessage);
	}

	/* This method adds a lose message to the
	 * user if he has exhausted all of his turns and
	 * failed to break all of the bricks in the game. 
	 * It displays the message in the center
	 * of the console in black.
	 */
	private void addLoseScreen() {
		GLabel loseMessage = new GLabel(("Sorry, you lost."), 100, 100);
		loseMessage.setLocation((getWidth()/2.0 - loseMessage.getWidth()/2.0), (getHeight()/2.0 - loseMessage.getAscent()/2.0));
		loseMessage.setColor(Color.black);
		add(loseMessage);
	}
}
