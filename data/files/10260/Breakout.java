import java.applet.AudioClip;
import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.MediaTools;
import acm.util.RandomGenerator;

/* BASIC VERSION
 * Class: Breakout ------------------- Class creates and runs the Breakout game as described in Assignment 3.  In this game "a layer of bricks lines occupy the top ~third of the screen. A ball travels across the canvas, bouncing off the top and side walls of the canvas. When the ball hits a brick is hit, the ball bounces away and the brick is removed. The player loses a turn when the ball touches the bottom of the canvas.  The player has three turns. To prevent this from happening, the player uses the mouse to move a paddle at the bottom of the canvas to bounce the ball upward, keeping it in play.  Complete instructions and details can be found in the Assignment 3 Handout (external documentation).
 */

public class Breakout extends GraphicsProgram {

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;
	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	public static final double CANVAS_HEIGHT = 600;

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;
	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Number of turns
	public static final int NTURNS = 3;

	public static final double PADDLE_HEIGHT = 10;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;

	// Offset of the paddle up from the bottom
	public static final double PADDLE_Y_OFFSET = 30;
	public static final double VELOCITY_X_MAX = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// Instance variable to keeps track of # of bricks on canvas. Used in a while
	// loop such that when bricks == 0, game ends.
	private int bricks = NBRICK_COLUMNS * NBRICK_ROWS;

	// Starting value for instance variable GRect paddle.
	private GRect paddle = null;

	// Instance variable to track whether last ball collision occurred with paddle.
	// Used to prevent ball colliding with the paddle multiple times and becoming
	// "glued" to paddle.
	private boolean previousCollisionObjectPaddle = false;

	// Instance variable for random number generator.
	// ASK SEMIR: Why does need to be an instance variable? SHOULD I PUT THIS IN RUN
	// METHOD?
	private RandomGenerator

	rgen = RandomGenerator.getInstance();

	// Starting values for instance variables vx and xy, which control ball speed.
	private double vx = 0;
	private double vy = VELOCITY_Y;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		/* You fill this in, along with any subsidiary methods */
		addMouseListeners();
		buildBricks();
		paddle = makePaddle();
		int turns = NTURNS;
		while (turns > 0 && bricks > 0) {
			GOval ball = makeBall();
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5))
				vx = -vx;
			boolean ballHitBottomWall = false;
			while (bricks > 0 && ballHitBottomWall == false) {
				ball.move(vx, vy ); 
				if (ballOutsideXDim(ball)) {
					vx = -vx;
				}
				if (ballOutsideYDim(ball) == 1) {
					vy = -vy;
					previousCollisionObjectPaddle = false;
				} else if (ballOutsideYDim(ball) == 2) {
					remove(ball);
					turns--;
					ballHitBottomWall = true;
				}
				// Structure below allows for the removal of 2+ bricks if the ball touches 2+
				// bricks at the same time, while also preventing the ball from getting
				// "glued" to the paddle.
				int i = collidingObject(ball.getX(), ball.getY()) + collidingObject(ball.getX(), ball.getBottomY())
						+ collidingObject(ball.getRightX(), ball.getY())
						+ collidingObject(ball.getRightX(), ball.getBottomY());
				// If statement checks for paddle collision and treats paddle collision at
				// multiple corners on the ball as a single collision to prevent double
				// bouncing/"gluing".
				if (i > 0 & previousCollisionObjectPaddle == false) {
					vy = -vy;
					previousCollisionObjectPaddle = true;
				}
				pause(DELAY);
			}
		}
	}

	/*
	 * Method: Build Bricks ------------------- Adds colored rows of bricks to
	 * canvas based on constants NBRICK_COLUMNS, NBRICK_ROWS, CANVAS_WIDTH,
	 * NBRICK_COLUMNS, BRICK_WIDTH, BRICK_SEP, BRICK_Y_OFFSET. The color of the
	 * bricks rows changes every two rows (starting from the top) from RED to ORANGE
	 * to YELLOW to GREEN to CYAN.
	 */
	private void buildBricks() {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			for (int j = 0; j < NBRICK_ROWS; j++) {
				double xBrickCoordinate = (CANVAS_WIDTH - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP) * 0.5
						+ (BRICK_WIDTH + BRICK_SEP) * i;
				double yBrickCoordinate = BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * j;
				GRect rect = new GRect(xBrickCoordinate, yBrickCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				// i/2%5 operation allows for repetition of row colors if number of rows
				// increases above 10 (e.g. NBRICK_ROWS = 20)
				if (j / 2 % 5 == 0) {
					rect.setColor(Color.RED);
				} else if (j / 2 % 5 == 1) {
					rect.setColor(Color.ORANGE);
				} else if (j / 2 % 5 == 2) {
					rect.setColor(Color.YELLOW);
				} else if (j / 2 % 5 == 3) {
					rect.setColor(Color.GREEN);
				} else {
					rect.setColor(Color.CYAN);
				}
				add(rect);
			}

		}
	}

	/*
	 * Method: Make Paddle ------------------- Adds and returns a filled rectangle
	 * based on constants CANVAS_WIDTH, PADDLE_WIDTH, CANVAS_HEIGHT,
	 * PADDLE_Y_OFFSET, and PADDLE_HEIGHT.
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect((CANVAS_WIDTH - PADDLE_WIDTH) * 0.5, CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}

	/*
	 * Method: Make Ball ------------------- Adds and returns a filled circle based
	 * on constants BALL_RADIUS, CANVAS_WIDTH, and CANVAS_HEIGHT.
	 */
	private GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(CANVAS_WIDTH * 0.5 - BALL_RADIUS, CANVAS_HEIGHT * 0.5 + BALL_RADIUS, size, size);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	/*
	 * Method: Ball Outside of X Dimensions ------------------- Returns true if the
	 * ball moves to the edge of (or outside) the x-dimensions of the canvas.
	 */
	private boolean ballOutsideXDim(GOval ball) {
		return (ball.getX() <= 0 || ball.getRightX() >= CANVAS_WIDTH);
	}

	/*
	 * Method: Ball Outside of Y Dimensions ------------------- Returns 1 if the
	 * ball hits the lower y-dimensions of the canvas. Returns 2 if the the ball
	 * hits the upper y-dimension on the canvas. Returns 0 otherwise.
	 */
	private int ballOutsideYDim(GOval ball) {
		if (ball.getY() <= 0) {
			return 1;
		} else if (ball.getBottomY() >= CANVAS_HEIGHT) {
			return 2;
		} else {
			return 0;
		}
	}

	/*
	 * Method: Colliding Object ------------------- Takes
	 * ball corner coordinates as inputs. Checks for objects at ball coordinates.
	 * Removes and bounces off objects if they are not black (e.g., bricks). Returns
	 * 1 if object is paddle.
	 */
	private int collidingObject(double x, double y) {
		GObject collidingObject = getElementAt(x, y);
		if (collidingObject != null && collidingObject.getColor() != Color.BLACK) {
			remove(collidingObject);
			bricks--;
			vy = -vy;
			previousCollisionObjectPaddle = false;
			return 0;
		//Return ints instead of booleans here to to enable extension later (e.g., if ball collides with edge of the paddle it does a side bounce).
		} else if (collidingObject == paddle) {
				return 1;
		} else {
			return 0;
		}
	}

	/*
	 * Method: Move Paddle ------------------- Moves paddle, along the x-axis of
	 * canvas, based on the mouse location.
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() > PADDLE_WIDTH * 0.5 && e.getX() < CANVAS_WIDTH - PADDLE_WIDTH * 0.5) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH * 0.5, CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
}
