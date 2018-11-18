/*File: Breakout.java
 * Name:Drew  Young
 * Section Leader:Julia Daniel
 * ----------------------------
 * This class executes the game Breakout. The game involves
 * the use of 3 types of objects, a ball, a paddle, and bricks.
 * The object of the game is to get rid of all the bricks by 
 * using the paddle to aim the ball at them. When the ball hits a
 * brick it removes it. If the ball hits the bottom
 * wall the player loses a life. The player has 3 lives before the game ends.
 * If the player gets rid of all the bricks they win.
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

	//Variable for counting bricks
	private int brickCounter;

	//variable for the paddle
	private GRect paddle;

	//variable for the paddle
	private GOval ball;

	//variable for the color
	private Color color = Color.RED;

	//variables for the speed
	private double vx, vy;

	//creates variable for random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*Sets up the rows of bricks required to play the game*/
	private void setUpBricks() {
		double y = BRICK_Y_OFFSET;
		for (int i=0; i<NBRICK_ROWS; i++) {
			double x = BRICK_SEP;
			for (int v=0; v<NBRICK_COLUMNS; v++) {
				GRect rect = new GRect (x,y,BRICK_WIDTH,BRICK_HEIGHT);
				rowNumber(i + 1);
				rect.setFilled(true);
				rect.setFillColor(color);
				rect.setColor(color);
				add(rect);
				x+= BRICK_WIDTH + BRICK_SEP;
			}
			y+= BRICK_HEIGHT + BRICK_SEP;	
		}
	}

	/*Colors the bricks in pairs of rows. In the repeating order
	 * of Red, Orange, Yellow, Green, and Cyan 
	 */
	private void rowNumber(int row) {
		if (row % 2 == 1) {
			if ( row % 5 == 1) {
				color = Color.RED;
			} else if (row % 5 == 3){
				color = Color.ORANGE;
			} else if (row % 5 == 0) {
				color = Color.YELLOW;
			} else if (row % 5 == 2) {
				color = Color.GREEN;
			} else if (row % 5 == 4) {
				color = Color.CYAN;
			}
		}
	}

	/*Creates the paddle required to play the game*/
	private void createPaddle() {
		paddle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*Makes the paddle follow the movement of the mouse on the screen*/
	public void mouseMoved(MouseEvent e) {
		if(e.getX() < getWidth() - PADDLE_WIDTH/2 && e.getX() - PADDLE_WIDTH/2 >= 0) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2 );
		}
	}

	/*Creates the ball required to play the game*/
	private void createBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS/2, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/*Makes the ball move in the game after the user prompts it by 
	 * clicking the mouse. 
	 */
	private void moveBall() {
		waitForClick();
		while(true) {
			if (brickCounter == 0) {
				break;
			}
			ball.move(vx, vy);
			if (checkCollisions()) {
				break;
			}
			pause(DELAY);
		}
	}

	/*Makes the ball bounce off the paddle and bricks and change direction,
	 * while removing the bricks it hits. It also counts the number
	 * of bricks it removes.
	 */
	private void getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x, y);
		if (collider != null) {
			if (collider == paddle) {
				if(vy > 0) {
					vy = -vy;
				}
			} else {
				remove(collider);
				vy = -vy * 1.01;
				vx = vx * 1.01;
				brickCounter--;
			}
		}
	}

	/*Checks for collisions of the ball in the graphics window. 
	 * This allows the ball to appear to bounce off the walls of the 
	 * window, the paddle, and the bricks. 
	 */
	private boolean checkCollisions() {
		if (ball.getX() < 0) {
			vx = -vx;
		} 
		if (ball.getX() + 2 * BALL_RADIUS > getWidth()) {
			vx = -vx;
		}
		if (ball.getY() < 0) {
			vy = -vy;
		}
		if (ball.getY() + 2 * BALL_RADIUS> getHeight()) {
			remove(ball);
			add(ball);
			ball.setLocation(getWidth()/2 - BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS/2);
			return true;
		}
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			getCollidingObject(ball.getX(), ball.getY());
		}else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			getCollidingObject(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			getCollidingObject(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			getCollidingObject(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return false;
	}

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;
		setUpBricks();
		createPaddle();
		createBall();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y; 
		for(int i=0; i<NTURNS; i++) {
			moveBall();
			if (brickCounter == 0) {
				break;
			}
		}
		remove(ball);
		GLabel label;
		if (brickCounter == 0) {
			label = new GLabel("You Win!");
		} else {
			label = new GLabel("You Lose!");
		}
		label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getAscent()/2);
		add(label);
	}
}
