/*
 * File: Breakout.java
 * -------------------
 * Name: Adam Behrendt
 * Section Leader: Marilyn Zhang.
 * 
 * The Breakoout subclass extends graphics program to run the classic arcade
 * program Breakout. After running through initial instructions, the game proceeds such 
 * that a ball bounces around, obeying the principle "the angle of incidence equal the angle
 * of reflection," destroying bricks it comes in contact with. Contact with the bottom of the canvas
 * results in a lost ball, and the player can prevent this by moving a reflective paddle along 
 * the bottom of the screen. This iteration is relatively easy, and if you agree, I suggest you try
 * extension Breakout2, which is much more interesting.
 * 
 * 
 * Sources: (i) The Art & Science of Java, by Eric Roberts;
 * (ii) CS106A Style Guide, retrieved from: 
 * https://web.stanford.edu/class/cs106a/assn/style.html
 * and (iii) Assignment 3 Handout (#10), retrieved from:
 * https://web.stanford.edu/class/cs106a/handouts/10%20-%20Assignment%203.pdf
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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	// Creates instance variable tracking ball velocity components.
	private double vx, vy;

	// Creates instance variable for the paddle.
	private GRect paddle;

	// Creates instance variable to track bricks broken.
	private int brickCount;

	// Creates random values.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Add Mouse Listeners :)
		addMouseListeners();

		// Draws bricks and sets up paddle.
		setupWorld();

		// Runs through Start Screen and instructions.
		displayIntroMessaging();

		// Plays game.
		playBreakout();

		// Ends game.
		finish();
	}

	// Configures the mouse - paddle interface, 
	public void mouseMoved(MouseEvent e) {

		// Adjusts mouse x to correspond with center of paddle.
		double adjustedMouseX = e.getX() - PADDLE_WIDTH / 2;

		// Defines max on paddle x coordinate.
		double paddleMaxX = getWidth() - PADDLE_WIDTH;

		// Defines min on paddle x coordinate.
		double paddleMinX = 0;

		// Defines paddleX to keep paddle from falling off the right edge.
		double paddleX = Math.min(adjustedMouseX, paddleMaxX);

		// Defines paddleRangeX to keep paddle from falling off the left and right edge.
		double paddleRangeX = Math.max(paddleX, paddleMinX);

		// Sets paddle to track mouse X coordinate.
		paddle.setX(paddleRangeX);
	}

	// Breaks world setup into setting up the bricks and paddle.
	private void setupWorld() {
		setupBricks();
		createPaddle();
	}

	// Sets up colors and layout for NBRICK_ROWS and NBRICK_COLUMNS.
	private void setupBricks() {
		for (int i = 1; i <= NBRICK_COLUMNS; i++) {
			for (int j = 1; j <= NBRICK_ROWS; j++) {
				double brickX = BRICK_SEP + (i - 1) * (BRICK_WIDTH + BRICK_SEP);
				double brickY = BRICK_Y_OFFSET + (j - 1) * (BRICK_HEIGHT + BRICK_SEP);
				addBricksInPairedRowsOfRedOrangeYellowGreenCyanOrder(j, brickX, brickY);
			}
		}
	}

	// Adds rows of colored bricks in red, red, orange, orange, yellow, yellow, green, green, cyan, cyan order.
	private void addBricksInPairedRowsOfRedOrangeYellowGreenCyanOrder(int rowNum, double brickX, double brickY) {

		// Defines color variable based on row number.
		Color color = getRowColor(rowNum);

		// Sends coordinates and color to brick maker.
		createBrick(brickX, brickY, color);
	}

	// Takes row number and returns row color.
	private Color getRowColor(int rowNum) {

		// Defines color valiable.
		Color color = null;

		// Looks at rows in groups of 10.
		rowNum = rowNum % 10;

		// Returns proper color given a row number.
		if (cyanRow(rowNum)) {
			color = Color.CYAN;
		} else if (greenRow(rowNum)) {
			color = Color.GREEN;
		} else if (yellowRow(rowNum)) {
			color = Color.YELLOW;
		} else if (orangeRow(rowNum)) {
			color = Color.ORANGE;
		} else if (redRow(rowNum)) {
			color = Color.RED;
		}
		return color;
	}

	// Color booleans only work in combination with the if, else if, else if... order in getRowColor.
	// Returns true for rows that will be cyan.
	private boolean cyanRow(int rowNum) {
		return (rowNum % 10 == 9 || rowNum % 10 == 0);
	}

	// Returns true for rows that will be green.
	private boolean greenRow(int rowNum) {
		return (rowNum % 8 == 7 || rowNum % 8 == 0);
	}

	// Returns true for rows that will be yellow.
	private boolean yellowRow(int rowNum) {
		return (rowNum % 6 == 5 || rowNum % 6 == 0);
	}

	// Returns true for rows that will be orange.
	private boolean orangeRow(int rowNum) {
		return (rowNum % 4 == 3 || rowNum % 4 == 0);
	}

	// Returns true for rows that will be red.
	private boolean redRow(int rowNum) {
		return (rowNum % 2 == 1 || rowNum % 2 == 0);
	}

	// Receives x-y coordinates and colors and outputs a corresponding brick of standard dimensions.
	private void createBrick(double brickX, double brickY, Color color) {
		GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(color);
		brick.setFilled(true);
		brick.setFillColor(color);
		add(brick);
	}

	// Creates a paddle of standard dimensions in the middle of the x- coordinate plane.
	private void createPaddle() {
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.DARK_GRAY);
		paddle.setFilled(true);
		paddle.setFillColor(Color.DARK_GRAY);
		add(paddle);
	}

	// Calls introduction, instructions, and start message.
	private void displayIntroMessaging() {
		introMessage();
		instructions();
		startMessage();
	}

	// Displays welcome message, ball, and click instruction.
	// Removes messaging after click.
	private void introMessage() {

		// Displays welcome banner.
		GLabel welcome1 = new GLabel("Welcome to Breakout!");
		welcome1.setFont("Helvetica-26");
		double welcome1X = setMessageX(welcome1); 
		double welcome1Y = setAboveMessageY(welcome1);
		add(welcome1, welcome1X, welcome1Y);

		// Adds ball graphic to center of screen.
		addBall();

		// Displays continue instructions.
		GLabel welcome3 = new GLabel("[click to continue]");
		welcome3.setFont("Helvetica-18");
		double welcome3X = setMessageX(welcome3);
		double welcome3Y = setBelowMessageY(welcome3);
		add(welcome3, welcome3X, welcome3Y);	

		// Pauses until user clicks.
		waitForClick();

		// Clears messaging.
		remove(welcome1);
		remove(welcome3);
	}

	// Displays mouse control instructions and click instruction.
	// Removes messaging after click.
	private void instructions() {

		// Displays mouse control instructions for paddle.
		GLabel start1 = new GLabel("Use Mouse to Controll Paddle");
		start1.setFont("Helvetica-26");
		double start1X = setMessageX(start1);
		double start1Y = setAboveMessageY(start1);
		add(start1, start1X, start1Y);

		// Displays continue instructions.
		GLabel start3 = new GLabel("[click to continue]");
		start3.setFont("Helvetica-18");
		double start3X = setMessageX(start3);
		double start3Y = setBelowMessageY(start3);
		add(start3, start3X, start3Y);

		// Pauses until user clicks.
		waitForClick();

		// Clears messaging.
		remove(start1);
		remove(start3);
	}

	// Displays ready message and click instructions.
	// Removes messaging after click.
	private void startMessage() {

		// Adds ready banner.
		GLabel begin1 = new GLabel("Don't Let Balls Escape");
		begin1.setFont("Helvetica-26");
		double begin1X = setMessageX(begin1);
		double begin1Y = setAboveMessageY(begin1);
		add(begin1, begin1X, begin1Y);

		// Adds click instructions.
		GLabel begin2 = new GLabel("[click to begin]");
		begin2.setFont("Helvetica-18");
		double beginX = setMessageX(begin2);
		double beginY = setBelowMessageY(begin2);
		add(begin2, beginX, beginY);

		// Pauses until user clicks.
		waitForClick();

		// Clears ball and messaging.
		remove(begin1);
		remove(begin2);
	}

	// Takes GLabel input and returns X dimension to center on screen.
	private double setMessageX(GLabel name) {
		return getWidth() / 2 - name.getWidth() / 2; 
	}

	// Takes GLabel input and returns Y dimension for banner displayed between center of screen and lowest block.
	private double setAboveMessageY(GLabel name) {
		return getHeight() / 2 - 3 * name.getAscent();
	}

	// Takes GLabel input and returns Y dimension for middle banner.
	// If Above, Middle, and Below message are the same font, each will be equally spaced.
	private double setMiddleMessageY(GLabel name) {
		return getHeight() / 2 + 4 * name.getAscent();
	}

	// Takes GLabel input and returns Y dimension for lower banner.
	// If Above, Middle, and Below message are the same font, each will be equally spaced.
	private double setBelowMessageY(GLabel name) {
		return getHeight() - 5;
	}

	/*
	 * This method iterates gameplay for n turns or until the game is won.
	 * A turn is the gameplay accomplished with one ball.
	 */
	private void playBreakout() {

		// Counts balls remaining.
		int ballCount = NTURNS;

		// Plays n = NTURNS balls, displaying appropriate messages for winning, restarting, last ball, and losing.
		for (int i = 1; i <= NTURNS; i++) {

			// Ensures balls can only be played if the game has not been won.
			if (notYetWon()) {

				// Gameplay and messaging for first and remaining turns, excepting the last two.
				if (i <= NTURNS - 2) {
					playBall();
					if (winQuery()) { // win = true.
						displayWinScreen(ballCount);
						finish();
					} else {
						ballCount = reduceBallCountByOne(ballCount);
						displayReStartMessage(ballCount);
					}

					// Gameplay for second to last ball and messaging for last ball.
				} else if (i == NTURNS - 1) {
					playBall();
					if (winQuery()) { // win = true.
						displayWinScreen(ballCount);
						finish();
					} else {
						ballCount = reduceBallCountByOne(ballCount);
						displayLastBallMessage();
					}

					// Gameplay and messaging for last ball.
				} else if (i == NTURNS) {
					playBall();
					if (winQuery()) { // win = true.
						displayWinScreen(ballCount);
					} else {
						displayGameOverMessage();
					}
				}
			}
		}
	}

	/* 
	 * This method defines the core game play functions for breakout.
	 * 
	 * It defines the imagine of the ball on the start screen as a functional game ball,
	 * sets it in motion, and controls its interaction with the walls, bricks, and paddle.
	 * 
	 * The canvas into upper and lower sections, which allows the interactions to be broken
	 * down into "ball + brick + wall" in the upper half and
	 * "ball + paddle + wall" in the lower half.
	 * 
	 * Ball interactions with bricks delete bricks.
	 */
	private void playBall() {

		// Defines ball as gameBall.
		GObject gameBall = grabsCenterBall();

		// Starts ball in motion. 
		// Randomly defines initial x velocity and sets y velocity.
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		// Animates ball while a ball remains in play and there are tiles remaining.
		while(aboveBottom(gameBall) && gameNotWon())  {

			// update world.
			gameBall.move(vx,vy);

			// Runs interactions in the upper half of canvas.
			runUpperHalfOfScreen(gameBall);

			// Runs interactions in the lower half of canvas.
			runLowerHalfOfScreen(gameBall);

			// Pause.
			pause(DELAY);
		}

		// Removes game ball if game won or ball lost by touching bottom of canvas.
		remove(gameBall);
	}

	// Checks top, left, right, and bottom of ball while in upper half of canvas.
	private void runUpperHalfOfScreen(GObject gameBall) {
		// Defines interactions with walls and bricks (in the upper half of the canvas).
		if (inUpperHalf(gameBall)) {
			checkUpTop(gameBall);
			checkUpLeft(gameBall);
			checkUpRight(gameBall);
			checkUpBottom(gameBall);
		}
	}

	// Checks left, right, and bottom part of ball while in lower canvas.
	// (There is no reason to check the top, since there are no objects in the lower
	// half of the canvas to collide with on this aspect.
	private void runLowerHalfOfScreen(GObject gameBall) {
		// Defines interactions with reflective walls and paddle.
		if (inLowerHalf(gameBall)) {
			checkLeftWall(gameBall);
			checkRightWall(gameBall);
			checkPaddle(gameBall);
		}
	}

	// Instructs interactions with wall on left; reverses x component of velocity.
	private void checkLeftWall(GObject gameBall) {
		if (hitsLeftWall(gameBall)) {
			vx = reverseVelocity(vx);
		}
	}

	// Instructs interaction with wall on right; reverses x component of velocity.
	private void checkRightWall(GObject gameBall) {
		if (hitsRightWall(gameBall)) {
			vx = reverseVelocity(vx);
		}
	}
	
	// Ensures velocity is negative after impact with paddle.
	private void checkPaddle(GObject gameBall) {
		// Instructs interactions with the paddle.
		if (hitsPaddleOnBottom(gameBall)) {
			vy = makeNegativeVelocity(vy);
		}
	}

	// Checks for contact with paddle.
	private boolean hitsPaddleOnBottom(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallBottom(gameBall)) == paddle;
	}

	// Instructs interactions with objects on the top (of the ball).
	private void checkUpTop(GObject gameBall) {
		if (hitsCeiling(gameBall) || hitsBrickAbove(gameBall)) {
			vy = reverseVelocity(vy);
			if (hitsBrickAbove(gameBall)) {
				GObject brick = grabsBrickOnTop(gameBall);
				countAndDestroyBrick(brick);
			}
		}
	}
	
	// Instructs interactions with objects on left of the ball.
	private void checkUpLeft(GObject gameBall) {
		// Instructs interactions with objects on left.
		if (hitsLeftWall(gameBall) || hitsBrickOnLeft(gameBall)) {
			vx = reverseVelocity(vx);
			if (hitsBrickOnLeft(gameBall)) {
				GObject brick = grabsBrickOnLeft(gameBall);
				countAndDestroyBrick(brick);

			}
		}
	}
	
	// Instructs interactions with objects on right of the ball.
	private void checkUpRight(GObject gameBall) {
		// Instructs interactions with objects on the right.
		if (hitsRightWall(gameBall) || hitsBrickOnRight(gameBall)) {
			vx = reverseVelocity(vx);
			if (hitsBrickOnRight(gameBall)) {
				GObject brick = grabsBrickOnRight(gameBall);
				countAndDestroyBrick(brick);
			}
		}
	}

	// Instructs interactions with bricks on the bottom of the ball.
	private void checkUpBottom(GObject gameBall) {
		// Instructs interactions with objects on the bottom (of the ball).
		if (hitsObjectOnBottom(gameBall)) {
			vy = reverseVelocity(vy);
			GObject brick = grabsBrickOnBottom(gameBall);
			countAndDestroyBrick(brick);
		}
	}

	// Defines "winning" as deleting all of the bricks.
	private boolean winQuery() {
		return brickCount == NBRICK_ROWS * NBRICK_COLUMNS;
	}

	// Ensures game has not yet been won.
	private boolean notYetWon() {
		return winQuery() != true;
	}

	// Reduces ball count by one; called after each ball is lost.
	private int reduceBallCountByOne(int ballCount) {
		return ballCount - 1;
	}

	// Is ball in upper half of canvas?
	private boolean inUpperHalf(GObject gameBall) {
		return gameBall.getY() < midlineY();
	}

	// Is ball in lower half of canvas?
	private boolean inLowerHalf(GObject gameBall) {
		return gameBall.getY() > midlineY();
	}

	// Returns y coordinate of canvas mid-line.
	private double midlineY() {
		return getHeight() / 2;
	}

	// Has ball contacted ceiling?
	private boolean hitsCeiling (GObject gameBall) {
		return gameBall.getY() - 1 < 0;
	}

	// Has top of ball contacted brick?
	private boolean hitsBrickAbove(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallTop(gameBall)) != null;
	}

	// Returns x coordinate of ball center.
	private double gameBallCenterX(GObject gameBall) {
		return gameBall.getX() + BALL_RADIUS;
	}

	// Returns y coordinate of ball top.
	private double gameBallTop(GObject gameBall) {
		return gameBall.getY() - 1;
	}

	// Returns brick on top of ball.
	private GObject grabsBrickOnTop(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallTop(gameBall));
	}

	// Returns x coordinate of ball left.
	private double gameBallLeft(GObject gameBall) {
		return gameBall.getX() - 1;
	}

	// Returns y coordinate of ball center.
	private double gameBallCenterY(GObject gameBall) {
		return gameBall.getY() + BALL_RADIUS;
	}

	// Returns x coordinate of ball right.
	private double gameBallRight(GObject gameBall) {
		return gameBall.getX() + 2 * BALL_RADIUS + 1;
	}

	// Returns brick on left of ball.
	private GObject grabsBrickOnLeft(GObject gameBall) {
		return getElementAt(gameBallLeft(gameBall), gameBallCenterY(gameBall));
	}

	// Returns brick on right of ball.
	private GObject grabsBrickOnRight(GObject gameBall) {
		return getElementAt(gameBallRight(gameBall), gameBallCenterY(gameBall));
	}

	// Reverses velocity component vector.
	private double reverseVelocity(double v) {
		return -v;
	}

	// Increases brickCount by one for every brick destroyed.
	private void countAndDestroyBrick(GObject brick) {
		remove(brick);
		brickCount = brickCount + 1;
	}

	// Did ball hit left wall?
	private boolean hitsLeftWall(GObject gameBall) {
		return gameBall.getX() - 1 < 0;
	}

	// Did ball hit brick on left side?
	private boolean hitsBrickOnLeft(GObject gameBall) {
		return getElementAt(gameBallLeft(gameBall), gameBallCenterY(gameBall)) != null;
	}

	// Did ball hit right wall?
	private boolean hitsRightWall(GObject gameBall) {
		return gameBall.getX() + 2 * BALL_RADIUS + 1 > getWidth();
	}

	// Did ball hit brick on right side?
	private boolean hitsBrickOnRight(GObject gameBall) {
		return getElementAt(gameBallRight(gameBall), gameBallCenterY(gameBall)) != null;
	}

	// Did ball hit an object on bottom?
	private boolean hitsObjectOnBottom(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallBottom(gameBall)) != null;
	}

	// Returns y coordinate of ball bottom.
	private double gameBallBottom(GObject gameBall) {
		return gameBall.getY() + 2 * BALL_RADIUS + 1;
	}

	// Returns brick on ball bottom.
	private GObject grabsBrickOnBottom(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallBottom(gameBall));
	}

	// Returns negative velocity (to ensure ball doesn't "stick" to paddle).
	private double makeNegativeVelocity(double v) {
		return - Math.abs(v);
	}

	// Calculates total number of bricks.
	private int totalNumberOfBricks() {
		return NBRICK_ROWS * NBRICK_COLUMNS;
	}

	// Is ball above bottom of the canvas?
	private boolean aboveBottom(GObject gameBall) {
		return gameBall.getY() + 2 * BALL_RADIUS < getHeight();
	}

	// Are there bricks remaining to be destroyed?
	private boolean gameNotWon() {
		return brickCount < totalNumberOfBricks();
	}

	// Displays information after winning the game.
	private void displayWinScreen(int ballCount) {

		// Displays WIN banner.
		GLabel win1 = new GLabel("You Beat Breakout");
		win1.setFont("Helvetica-28");
		double win1X = setMessageX(win1); 
		double win1Y = setAboveMessageY(win1);
		add(win1, win1X, win1Y);

		// Adds golden ball displaying "winner."
		addGoldenWinnerBall();

		// Displays continue instructions and displays number of balls remaining.
		if (ballCount == 1) {
			GLabel win2 = new GLabel("With " + ballCount + " Ball Remaining");
			win2.setFont("Helvetica-28");
			double win2X = setMessageX(win2);
			double win2Y = setMiddleMessageY(win2);
			add(win2, win2X, win2Y);	
		} else {
			GLabel win2 = new GLabel("With " + ballCount + " Balls Remaining");
			win2.setFont("Helvetica-28");
			double win2X = setMessageX(win2);
			double win2Y = setMiddleMessageY(win2);
			add(win2, win2X, win2Y);	
		}

		// Pauses until user clicks.
		waitForClick();

		// Displays close to exit instructions.
		GLabel win3 = new GLabel("[close to exit]");
		win3.setFont("Helvetica-18");
		double win3X = setMessageX(win3);
		double win3Y = setBelowMessageY(win3);
		add(win3, win3X, win3Y);	
	}

	// Displays "ball lost" and "click to continue" message after ball lost.
	// Adds ball to center of the screen.
	private int displayReStartMessage(int ballCount) {

		// Displays restart banner.
		GLabel welcome1 = new GLabel("Ball Lost: " + ballCount + " Remaining");
		welcome1.setFont("Helvetica-28");
		double welcome1X = setMessageX(welcome1); 
		double welcome1Y = setAboveMessageY(welcome1);
		add(welcome1, welcome1X, welcome1Y);

		// Adds ball graphic to center of screen.
		addBall();

		// Displays continue instructions.
		GLabel welcome3 = new GLabel("[click to continue]");
		welcome3.setFont("Helvetica-18");
		double welcome3X = setMessageX(welcome3);
		double welcome3Y = setBelowMessageY(welcome3);
		add(welcome3, welcome3X, welcome3Y);	

		// Pauses until user clicks.
		waitForClick();

		// Clears messaging.
		remove(welcome1);
		remove(welcome3);

		// Returns number of balls played.
		return ballCount;
	}

	// Returns ball on the center of the screen.
	private GOval grabsCenterBall() {
		return getElementAt(canvasMiddleX(), canvasMiddleY());
	}

	// Returns x coordinate of canvas center.
	private double canvasMiddleX() {
		return getWidth() / 2;
	}

	// Returns y coordinate of canvas center.
	private double canvasMiddleY() {
		return getHeight() / 2;
	}

	// Displays last ball message and adds ball to the center of the screen.
	private void displayLastBallMessage() {

		// Displays restart banner.
		GLabel welcome1 = new GLabel("Last Ball");
		welcome1.setFont("Helvetica-28");
		double welcome1X = setMessageX(welcome1); 
		double welcome1Y = setAboveMessageY(welcome1);
		add(welcome1, welcome1X, welcome1Y);

		// Adds ball graphic to center of screen.
		addBall();
		GOval lastBall = grabsCenterBall();
		lastBall.setColor(Color.RED);
		lastBall.setFilled(true);
		lastBall.setFillColor(Color.RED);

		// Displays continue instructions.
		GLabel welcome3 = new GLabel("[click to continue]");
		welcome3.setFont("Helvetica-18");
		double welcome3X = setMessageX(welcome3);
		double welcome3Y = setBelowMessageY(welcome3);
		add(welcome3, welcome3X, welcome3Y);	

		// Pauses until user clicks.
		waitForClick();

		// Clears messaging.
		remove(welcome1);
		remove(welcome3);
	}

	// Returns number of bricks remaining (to be destroyed).
	private int numberOfBricksRemaining() {
		return totalNumberOfBricks() - brickCount;
	}

	// Displays game over method.
	private void displayGameOverMessage() {

		// Calculates number of bricks remaining.
		int bricksRemaining = numberOfBricksRemaining();

		// Displays restart banner.
		GLabel lose1 = new GLabel("Game Over");
		lose1.setFont("Helvetica-26");
		double lose1X = setMessageX(lose1); 
		double lose1Y = setAboveMessageY(lose1);
		add(lose1, lose1X, lose1Y);

		// Adds game over ball.
		addBlackGameOverBall();

		// Displays continue instructions.
		GLabel lose3 = new GLabel("[close to exit]");
		lose3.setFont("Helvetica-18");
		double lose3X = setMessageX(lose3);
		double lose3Y = setBelowMessageY(lose3);
		add(lose3, lose3X, lose3Y);	

		// Displays number of bricks remaining with proper (single) grammar.
		// Removes all displays after click.
		if (bricksRemaining == 1) {
			GLabel lose2 = new GLabel("With Only " + bricksRemaining + " Brick Remaining");
			lose2.setFont("Helvetica-26");
			double lose2X = setMessageX(lose2);
			double lose2Y = setMiddleMessageY(lose2);
			add(lose2, lose2X, lose2Y);	

			// Pause while user reads banners.
			waitForClick();

			// Clears banners from the screen after click.
			remove(lose1);
			remove(lose2);
			remove(lose3);

			// Displays number of bricks remaining with proper (plural) grammar.
			// Removes all displays after click.
		} else {
			GLabel lose2 = new GLabel("With " + bricksRemaining + " Bricks Remaining");
			lose2.setFont("Helvetica-26");
			double lose2X = setMessageX(lose2);
			double lose2Y = setMiddleMessageY(lose2);
			add(lose2, lose2X, lose2Y);	

			// Pause while user reads banners.
			waitForClick();

			// Clears banners from the screen after click.
			remove(lose1);
			remove(lose2);
			remove(lose3);
		}

		// Calls exit instructions.
		theEnd();
	}

	// Creates a ball in the middle of the (x,y) coordinate screen.
	private void addBall() {
		double ballX = canvasMiddleX() - BALL_RADIUS;
		double ballY = canvasMiddleY() - BALL_RADIUS;
		double diameter = 2 * BALL_RADIUS;
		GOval startBall = new GOval(ballX, ballY, diameter, diameter);
		startBall.setColor(Color.DARK_GRAY);
		startBall.setFilled(true);
		startBall.setFillColor(Color.DARK_GRAY);
		add(startBall);
	}

	// Scales radius by a given integer.
	private double scaleBallRadiusByInteger(int num) {
		return num * BALL_RADIUS;
	}

	// Doubles radius to returns diameter.
	private double radiusToDiameter(double radius) {
		return 2 * radius;
	}

	//Displays a centered golden ball with red "WINNER" printed over it.
	private void addGoldenWinnerBall() {

		// Enlarges ball in middle of the screen.
		double radius = scaleBallRadiusByInteger(5);
		double diameter = radiusToDiameter(radius);
		GOval goldBall = new GOval(canvasMiddleX() - radius, canvasMiddleY() - radius, diameter, diameter);
		goldBall.setColor(new Color(255,215,0));
		goldBall.setFilled(true);
		goldBall.setFillColor(new Color(255,215,0));
		add(goldBall);

		// Displays "WINNER" in the center of enlarged ball.
		GLabel winner = new GLabel("WINNER");
		winner.setColor(Color.RED);
		winner.setFont("Helvetica-18");
		double winnerX = canvasMiddleX() - halfLabelWidth(winner);
		double winnerY = canvasMiddleY() + halfLabelAscent(winner);
		add(winner, winnerX, winnerY);
	}

	// Returns half the width of a GLabel.
	private double halfLabelWidth(GLabel label) {
		return label.getWidth() / 2;
	}

	// Returns have the ascent height of a GLabel.
	private double halfLabelAscent(GLabel label) {
		return label.getAscent() / 2;
	}

	// Displays a centered black ball with yellow "Game Over" printed on it.
	private void addBlackGameOverBall() {

		// Enlarges ball in middle of the screen.
		double radius = scaleBallRadiusByInteger(5);
		double diameter = radiusToDiameter(radius);
		GOval blackBall = new GOval(canvasMiddleX() - radius, canvasMiddleY() - radius, diameter, diameter);
		blackBall.setColor(Color.BLACK);
		blackBall.setFilled(true);
		blackBall.setFillColor(Color.BLACK);
		add(blackBall);

		// Displays "game over" in center of enlarged ball.
		GLabel loser = new GLabel("GAME OVER");
		loser.setColor(Color.YELLOW);
		loser.setFont("Helvetica-14");
		double loserX = canvasMiddleX() - halfLabelWidth(loser);
		double loserY = canvasMiddleY() + halfLabelAscent(loser);
		add(loser, loserX, loserY);
	}

	// Displays last screen instructing player to close canvas.
	private void theEnd() {

		// Displays closing instructions.
		GLabel last3 = new GLabel("[close to exit]");
		last3.setFont("Helvetica-18");
		double last3X = setMessageX(last3);
		double last3Y = getHeight();
		add(last3, last3X, last3Y);	
	}

	//Prevents game from restarting with remaining balls.
	private void finish() {
	}
}