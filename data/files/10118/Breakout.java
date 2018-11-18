/*
 * File: Breakout.java


 * -------------------
 * Name: Anya Petersohn
 * Section Leader: Vineet Kosaraju
 * 
 * Implements the game of Breakout.
 * A player has 3 tries to hit and remove all of the bricks at the top of the screen. If they do so, they win.
 * They hit and remove the bricks by moving a paddle, from which the ball bounces off (and potentially hits bricks).
 * When the ball hits a brick, the brick disappears.
 * The ball can bounce off the left, right, and top wall. 
 * Once the ball touches a wall, brick, or the paddle it reverses its direction. 
 * If the player fails to bounce the ball off the paddle, and it hits the bottom of the screen, it counts as a try.
 * If the three tries are up, the player looses. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double DELAY = 800.0 / 60.0; //I changed this to 800.0/60.0 to get better play action.

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle = null;

	private GOval ball = null;

	private GRect brick = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		gameSetUp();
		for (int i = 0; i < 3; i++) { //the player has 3 turn to win so i < 3
			if (gamePlayOneTurn()) {
				break;
			}
		}
	}

	/*
	 * Method: (gamePlayOneTurn);
	 * -------------------
	 * Plays one turn of the game. The ball is released and bounces of the paddle & walls until it collides with a brick.
	 * When the ball collides with a brick, the brick disappears. 
	 * When the ball hits the left, right, and top wall, it reverses its direction.
	 * If the ball misses the paddle and hits the bottom wall, the play terminates.
	 * If the ball hits the last brick, the play terminates, (and the player wins).
	 */	
	private boolean gamePlayOneTurn() {
		int countBricks = NBRICK_COLUMNS * NBRICK_ROWS; //variable that counts bricks.
		vx = rgen.nextDouble(1.0, 3.0);
		vy = 3.0;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		ball = createBall();
		while (true) {
			//update velocity
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			ball.move(vx, vy); //update visualization
			pause(DELAY); //pause
			GObject collider = getCollidingObject();
			if (collider != null && collider != paddle) { //remove bricks but not the paddle
				remove(collider);
				vy = -vy;
				countBricks = countBricks - 1; 
			}
			if (collider == paddle && vy > 0) { //vy > 0 prevents the ball from getting stuck to the paddle. 
				vy = -vy;
			}
			if (hitBottomWall(ball)) { //If ball hits bottom wall, this is a terminating condition. 
				remove(ball);
				return false;
			}
			if (countBricks == 0) { // If brick count = 0 then the player has won.
				remove(ball);
				return true;
			}
		}
	}


	/*
	 * Method: (getCollidingObject);
	 * -------------------
	 * Checks to see if the ball is colliding with any objects.
	 * Checks the four corner points on the square around the ball. 
	 */	
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject brickPresent = getElementAt(x, y); //could use a double forloop
		if (brickPresent != null) {
			return brickPresent;
		}
		brickPresent = getElementAt(x + BALL_RADIUS * 2, y);
		if (brickPresent != null) {
			return brickPresent;
		}
		brickPresent = getElementAt(x, y + BALL_RADIUS *2);
		if (brickPresent != null) {
			return brickPresent;
		}
		brickPresent = getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
		return brickPresent;
	}

	/*
	 * Method: (hitBottomWall);
	 * -------------------
	 * Returns whether the ball should bounce off the bottom wall of the canvas.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * Method: (hitTopWall);
	 * -------------------
	 * Returns whether the ball should bounce off the top wall of the canvas.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Method: (hitRightWall);
	 * -------------------
	 * Returns whether the ball should bounce off the right wall of the canvas.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Method: (hitLeftWall);
	 * -------------------
	 * Returns whether the ball should bounce off the left wall of the canvas.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0; 
	}

	/*
	 * Method: (createBall);
	 * -------------------
	 * Constructs a black graphics oval on the screen.
	 * The oval will function as the ball in the breakout game.
	 */
	private GOval createBall() {
		GOval ball = new GOval (BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add (ball, (getWidth()-WIDTH)/2 ,(getHeight()-HEIGHT)/2 );	
		return ball;
	}

	/*
	 * Method: (gameSetUp);
	 * -------------------
	 * Sets up the game. Constructs and sets a rectangle of bricks at the top of the screen 
	 * (with a y offset from the top of the canvas).The bricks are colored in a rainbow sequence.
	 * RED - ORANGE - YELLOW - GREEN - CYAN.
	 * Creates a paddle (centered on the width of the screen but set with a y offset from the bottom).
	 * Sets the mouse to move the paddle left to right but not up and down.  
	 */
	private void gameSetUp() {
		makeColoredBricks();
		makePaddle(); 
		putPaddleInMiddleofX();
		addMouseListeners(); //works without it --someone tell Chris!
	}

	/*
	 * Method: (mouseMoved);
	 * -------------------
	 * Sets the paddle to follow the mouse left and right across the screen.
	 * The y coordinate of the paddle is set so the mouse does not move the paddle up and down.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH / 2;
		if (mouseX > getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		if (mouseX < 0) {
			mouseX = 0;
		}
		double mouseY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle.setLocation(mouseX, mouseY);
	}


	/*
	 * Method: (makePaddle);
	 * -------------------
	 * Constructs the paddle and fills it.
	 */
	private void makePaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
	}

	/*
	 * Method: (putPaddleInMiddleOfX);
	 * -------------------
	 * Adds the instance variable paddle to the middle x coordinate of the screen. 
	 * The y coordinate position is set relative to the bottom of the window. 
	 */
	private void putPaddleInMiddleofX() {
		double x = (getWidth()- PADDLE_WIDTH) /2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, x, y);
	}

	/*
	 * Method: (makeColoredBricks);
	 * -------------------
	 * Creates rows of bricks forming a rectangle of bricks using nested for loops. 
	 * The bricks are colored and remain constant for 2 rows. 
	 * After two rows, they follow the sequence: RED - ORANGE - YELLOW - GREEN - CYAN.
	 * 
	 */
	private void makeColoredBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double x = (getWidth()-((NBRICK_COLUMNS * BRICK_WIDTH) + (NBRICK_COLUMNS * BRICK_SEP)- BRICK_SEP)) / 2 + (j * (BRICK_WIDTH + BRICK_SEP));
				double y = BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * i;
				Color color = null;
				int mod = (i % 10) / 2; //Uses the mod operator to provide the remainder.The color sequence will now work if the number NBRICK_ROWS changes.
				if (mod == 0) {
					color = Color.RED;
				} else if (mod == 1) {
					color = Color.ORANGE; 
				} else if (mod == 2) {
					color = Color.YELLOW;
				} else if (mod == 3) {
					color = Color.GREEN;
				} else if (mod == 4) {
					color = Color.CYAN;
				} 
				brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT); //constructs a new brick 
				brick.setColor(color);
				brick.setFilled(true);
				add (brick, x, y);
			}
		}
	}
}




