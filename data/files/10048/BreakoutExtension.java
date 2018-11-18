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

public class BreakoutExtension extends GraphicsProgram {

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

	// Y-coordinate of the paddle
	public static final double PADDLE_Y_COORD = CANVAS_HEIGHT - PADDLE_Y_OFFSET;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 7.0;
	public static final double VELOCITY_X_MAX = 10.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Animation delay between spawning of a new ball
	public static final double NEW_BALL_DELAY = 750;

	// Computes diameter of ball
	public static final double BALL_DIAMETER = BALL_RADIUS * 2;

	// Number of turns 
	public static final int NTURNS = 3;

	// Creates a paddle as an instance variable
	GRect paddle = null;

	// Creates a ball as an instance variable
	GOval ball = null;

	// Creates a brick as an instance variable
	GRect brick = null;

	// Creates instance variables that keep track of the ball's velocity	
	private double vx, vy;

	// Creates an instance variable that will randomly generate numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Creates the visual setup of the game, with the bricks, a moving
		// paddle, and a ball in the center of the screen
		setUpBricks();
		createPaddle();
		addMouseListeners();

		// Randomly generates a value for the ball's x velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;

		// Sets the value for the ball's y velocity with a constant
		vy = VELOCITY_Y;

		generateBalls();
	}

	/* Method: generateBalls
	 * ---------------------
	 * Creates the initial ball, controls its motion, and generates a new
	 * ball in the case that a turn has ended up up until no more turns are
	 * left.
	 */
	private void generateBalls() {
		createBall();
		int brickCount = setBallInMotion(ball);
		for (int turns = 0; turns < NTURNS - 1; turns ++) {
			// finishes the game if the last brick is removed
			if (brickCount <= 0) {
				youWon();
				break;
			}
			brickCount = makeBallForNewTurn();
		}
		if (brickCount > 0) {
			gameOver();
		}
	}

	/* Method: youWon
	 * --------------
	 * Displays a "YOU WON!" message in the event that all of the bricks are
	 * cleared from the screen.
	 */
	private void youWon() {
		GLabel youWon = new GLabel ("YOU WON!");
		youWon.setFont("Serif-50");
		youWon.setColor(Color.PINK);
		double youWonWidth = youWon.getWidth();
		add(youWon, (getWidth()/2) - (youWonWidth/2), getHeight()/2);
	}

	/* Method: gameOver
	 * --------------
	 * Displays a "GAME OVER" message in the event that the player doesn't
	 * clear all of the bricks on the screen and all of the turns run out.
	 */
	private void gameOver() {
		GLabel gameOver = new GLabel ("GAME OVER");
		gameOver.setFont("Serif-50");
		gameOver.setColor(Color.RED);
		double youWonWidth = gameOver.getWidth();
		add(gameOver, (getWidth()/2) - (youWonWidth/2), getHeight()/2);
	}

	/* Method: setBallInMotion
	 * -----------------------
	 * Controls the ball's motion across the screen and when coming into
	 * contact with other on-screen objects.
	 */
	private int setBallInMotion(GOval ball) {
		int brickCount = NBRICK_COLUMNS * NBRICK_ROWS;
		while (true) {
			double ballY = ball.getY();
			brickCount = adjustForObjectCollisions(ball, brickCount);
			adjustForConsoleCollisions(ball);
			ball.move(vx, vy);
			// stops the loop if the ball falls off screen or the last brick is removed
			if (ballY > getHeight() || brickCount <= 0) {
				remove(ball);
				break;
			}
			pause(DELAY);
		}
		return brickCount;
	}

	/* Method: makeBallForNewTurns
	 * ---------------------------
	 * Spawns a new ball every time a new turn starts.
	 */
	private int makeBallForNewTurn() {
		pause(NEW_BALL_DELAY);
		createBall();
		int brickCount = setBallInMotion(ball);
		return brickCount;
	}

	/* Method: adjustForConsoleCollisions
	 * ----------------------------------
	 * Changes the ball's velocity when the ball collides with another object;
	 * removes the object if it is a brick.
	 */
	private int adjustForObjectCollisions(GOval ball, int brickCount) {
		GObject collider = getCollidingObject();
		// loads audio file that will later be called to produce bouncing noise
		// whenever ball collides with paddle or bricks
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		double ballY = ball.getY();
		// ensures that the ball will only be bounced back if it is above a certain
		// portion of the screen
		if (ballY <= PADDLE_Y_COORD) {
			if (collider == paddle && (ballY + BALL_DIAMETER) == PADDLE_Y_COORD) {
				vy = -vy;
				bounceClip.play();
			}
			if (collider == paddle && (ballY + BALL_DIAMETER) > PADDLE_Y_COORD) {
				vx = -vx;
				bounceClip.play();
			} if (collider == brick) {
				vy = -vy;
				GObject object = findBrickToRemove(ball);
				brickCount --;
				bounceClip.play();
				remove(object);
			}
		}
		return brickCount;
	}

	/* Method: findBrickToRemove
	 * -------------------------
	 * Identifies the brick with which the ball has collided with by location
	 * in order to allow for its removal.
	 */
	private GObject findBrickToRemove(GOval ball) {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject TopL = getElementAt(ballX, ballY);
		GObject TopR = getElementAt(ballX + BALL_DIAMETER, ballY);
		GObject BottomL = getElementAt(ballX, ballY + BALL_DIAMETER);
		GObject BottomR = getElementAt(ballX + BALL_DIAMETER, ballY + BALL_DIAMETER);
		if (TopL != null) {
			return TopL;
		} else if (TopR != null) {
			return TopR;
		} else if (BottomL != null) {
			return BottomL; 
		} else if (BottomR != null) {
			return BottomR;
		} else {
			return null;
		}
	}

	/* Method: getCollidingObject
	 * --------------------------
	 * Checks ball's location to see if any collisions with objects occur; if they do occur,
	 * the method will return the object with which the ball collided.
	 */
	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		double yBallCoord = (getHeight() / 2) - (BALL_RADIUS / 2);
		GObject TopL = getElementAt(ballX, ballY);
		GObject TopR = getElementAt(ballX + BALL_DIAMETER, ballY);
		GObject BottomL = getElementAt(ballX, ballY + BALL_DIAMETER);
		GObject BottomR = getElementAt(ballX + BALL_DIAMETER, ballY + BALL_DIAMETER);
		if (TopL != null || TopR != null) {
			return brick;
		} else if (BottomL != null || BottomR != null) {
			// checks, in the case that there's an object touching the ball on its 
			// bottom side, whether that object is a brick or paddle
			if (ballY > yBallCoord) {
				return paddle;
			} else {
				return brick;
			}
		} else {
			return null;
		}
	}

	/* Method: adjustForConsoleCollisions
	 * ----------------------------------
	 * Changes the ball's velocity depending on where the ball collides with
	 * edges of the console.
	 */
	private void adjustForConsoleCollisions(GOval ball) {
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}
	}

	/* Method: hitTopWall
	 * --------------------
	 * Returns a boolean depending on whether or not the ball has hit the
	 * top wall of the screen.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/* Method: hitRightWall
	 * --------------------
	 * Returns a boolean depending on whether or not the ball has hit the
	 * right wall of the screen.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	/* Method: hitLeftWall
	 * --------------------
	 * Returns a boolean depending on whether or not the ball has hit the
	 * left wall of the screen.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* Method: mouseMoved
	 * ------------------
	 * Allows the mouse's horizontal movements to control the location of
	 * the paddle along the bottom of the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();

		// has paddle move as mouse moves across screen
		// "if" statement makes sure that paddle doesn't move off screen
		if (mouseX < getWidth() - PADDLE_WIDTH) {
			add(paddle, mouseX, PADDLE_Y_COORD);
		}
	}

	/* Method: createBall
	 * ------------------
	 * Creates a ball in the center of the screen.
	 */
	private void createBall() {
		double xBallCoord = (getWidth() / 2) - (BALL_RADIUS / 2);
		double yBallCoord = (getHeight() / 2) - (BALL_RADIUS / 2);
		ball = new GOval(xBallCoord, yBallCoord, BALL_RADIUS * 2, BALL_RADIUS * 2); 
		ball.setFilled(true);
		add(ball);
	}

	/* Method: createPaddle
	 * --------------------
	 * Sets up a black paddle centered at the middle of the screen horizontally,
	 * near the bottom of the screen vertically.
	 */
	private void createPaddle() {
		double xPaddleCoord = (getWidth() - PADDLE_WIDTH) / 2;
		paddle = new GRect(xPaddleCoord, PADDLE_Y_COORD, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/* Method: setUpBricks
	 * -------------------
	 * Places the bricks in the correct amount of columns and rows.
	 */
	private void setUpBricks() {
		for (int row = 0; row < NBRICK_ROWS; row ++) {
			for (int column = 0; column < NBRICK_COLUMNS; column ++) {
				createColoredBrick(column, row);
			}
		}
	}

	/* Method: createColoredBrick
	 * --------------------------
	 * Determines the color of each brick depending on which column of
	 * bricks has been built.
	 */
	private void createColoredBrick(int column, int row) {
		if (row < 2) {
			createBrick(Color.RED, column, row);
		} else if (row < 4) {
			createBrick(Color.ORANGE, column, row);
		} else if (row < 6) {
			createBrick(Color.YELLOW, column, row);
		} else if (row < 8) {
			createBrick(Color.GREEN, column, row);
		} else {
			createBrick(Color.CYAN, column, row);
		}
	}

	/* Method: createBrick
	 * -------------------
	 * Creates a single brick which takes in a color parameter and the amount of
	 * bricks that have already been built in a row and column.
	 */
	private void createBrick(Color brickColor, int column, int row) {
		// determines the x-coordinate of the first brick in each row
		double startingX = (getWidth() - ((NBRICK_COLUMNS - 1) * BRICK_SEP) - (NBRICK_COLUMNS * BRICK_WIDTH)) / 2;

		// determines the x and y coordinates of the bricks
		double xBrickCoord = startingX + (column * BRICK_SEP) + (column * BRICK_WIDTH);
		double yBrickCoord = BRICK_Y_OFFSET + (row * BRICK_HEIGHT) + (row * BRICK_SEP);

		// creates each brick
		brick = new GRect(xBrickCoord, yBrickCoord, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(brickColor);
		add(brick);
	}
}