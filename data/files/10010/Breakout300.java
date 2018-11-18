/*
 * File: Breakout.java
 * -------------------
 * Name: Adam Behrendt
 * Section Leader: Marilyn Zhang.
 * 
 * The Breakoout300 subclass extends graphics program to run the classic arcade
 * program Breakout, but with several extensions. After running through initial instructions, 
 * the basic game proceeds such that a ball bounces around, obeying the principle "the angle of incidence
 * equal the angle of reflection," destroying bricks it comes in contact with. 
 * 
 * Contact with the bottom of the canvas results in a lost ball, and the player can prevent this by moving
 * a reflective paddle along the bottom of the screen to return the ball to the top.
 * 
 * Breakout 300 is not as easy as Breakout, and it contains the following extensions:
 * (i) SOUNDS! -- the game plays a noise when the ball impacts something;
 * (ii) 3 levels -- game play includes 3 progressively more difficult levels;
 * (iii) Speed bonuses -- game play includes a rapid acceleration after 7 bricks have been broken,
 * and a more graduate speed bonus with every other brick broken. Velocity in y caps out at 7;
 * (iv) Magic Line -- Halfway through the second level, balls take on a different vx while traveling
 * up towards the bricks (this keeps users from bouncing straight up and down);
 * (v) Inverted paddle -- Halfway through the third level, the paddle controls invert;
 * (vi) Score -- the score is kept in the bottom right hand corner; and
 * (vii) Color blocks -- the color blocks have been changed from 2 x 2 x 2 x... to large 
 * red, orange, yellow, green, and cyan; try NBRICK_ROWS = 15 to see this.
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

public class Breakout300 extends GraphicsProgram {

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

	// Creates instance variable to track bricks broken per ball.
	private int bricksThisBall;

	// Keeps score.
	private double score;

	// Reports score.
	GLabel scoreLabel;

	// Creates random values.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Loads sound from sound library.
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

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

		// Defines paddleRangeInvert to keep paddle from falling off the left and right edge while inverted.
		double paddleRangeInvert = CANVAS_WIDTH - paddleRangeX - PADDLE_WIDTH;

		// Inverts the paddle controls halfway through the third level.
		if (brickCount < 5 * totalNumberOfBricks() / 2)  {
			// Sets paddle to track mouse X coordinate.
			paddle.setX(paddleRangeX);
		} else {
			// Inverts paddle controls.
			paddle.setX(paddleRangeInvert);
		}
	}

	// Breaks world setup into setting up the bricks and paddle.
	private void setupWorld() {
		setupBricks();
		createPaddle();
		setupScoreLabel();
	}

	// Sets up score label in lower right hand corner.
	private void setupScoreLabel() {
		scoreLabel = new GLabel("score = " + 0 + "          ");
		scoreLabel.setFont("Helvetica-18");
		double scoreX = getWidth() - scoreLabel.getWidth() - 5;
		double scoreY = setBelowMessageY(scoreLabel);
		add(scoreLabel, scoreX, scoreY);
	}

	// Sets up colors and layout for NBRICK_ROWS and NBRICK_COLUMNS.
	// Sets of blocks of rows in red, orange, yellow, green, cyan order.
	private void setupBricks() {

		// Calculates the base number of rows each color can have.
		// Calculates the remainder rows, to be distributed from the top down.
		int rowsPerColor = (int) NBRICK_ROWS / 5;
		int extraRowsToDistribute = NBRICK_ROWS % 5;

		// Creates rows and columns of colored bricks.
		for (int i = 1; i <= NBRICK_COLUMNS; i++) {
			for (int j = 1; j <= NBRICK_ROWS; j++) {
				double brickX = BRICK_SEP + (i - 1) * (BRICK_WIDTH + BRICK_SEP);
				double brickY = BRICK_Y_OFFSET + (j - 1) * (BRICK_HEIGHT + BRICK_SEP);
				Color color = getColor(j, rowsPerColor, extraRowsToDistribute);
				createBrick(brickX, brickY, color);
			}
		}
	}

	// Returns proper color for each brick depending on row number, rows per color, and remainder to distribute.
	private Color getColor(int rowNum, int rowsPerColor, int extraRowsToDistribute) {

		// Defines color variable.
		Color color = null;

		// Calls appropriate method depending on the remainder.
		if (extraRowsToDistribute == 0) {
			color = colorsForZero(rowNum, rowsPerColor);
		} else if (extraRowsToDistribute == 1) {
			color = colorsForOne(rowNum, rowsPerColor);
		} else if (extraRowsToDistribute == 2) {
			color = colorsForTwo(rowNum, rowsPerColor);
		} else if (extraRowsToDistribute == 3) {
			color = colorsForThree(rowNum, rowsPerColor);
		} else if (extraRowsToDistribute == 4) {
			color = colorsForFour(rowNum, rowsPerColor);
		}

		// Returns color for each brick.
		return color;
	}

	// Returns proper color brick for remainder = 0.
	private Color colorsForZero(int rowNum, int rowsPerColor) {

		Color color = null;

		if (rowNum <= rowsPerColor) {
			color = Color.RED;
		} else if (rowNum > rowsPerColor && rowNum <= 2 * rowsPerColor) {
			color = Color.ORANGE;
		} else if (rowNum > 2 * rowsPerColor && rowNum <= 3 * rowsPerColor) {
			color = Color.YELLOW;
		} else if (rowNum > 3 * rowsPerColor && rowNum <= 4 * rowsPerColor) {
			color = Color.GREEN;
		} else if (rowNum > 4 * rowsPerColor && rowNum <= 5 * rowsPerColor) {
			color = Color.CYAN;
		}

		return color;
	}

	// Returns proper color brick for remainder = 1.
	private Color colorsForOne(int rowNum, int rowsPerColor) {

		Color color = null;

		if (rowNum <= rowsPerColor + 1) {
			color = Color.RED;
		} else if (rowNum > rowsPerColor + 1 && rowNum <= 2 * rowsPerColor + 1) {
			color = Color.ORANGE;
		} else if (rowNum > 2 * rowsPerColor + 1 && rowNum <= 3 * rowsPerColor + 1) {
			color = Color.YELLOW;
		} else if (rowNum > 3 * rowsPerColor + 1 && rowNum <= 4 * rowsPerColor + 1) {
			color = Color.GREEN;
		} else if (rowNum > 4 * rowsPerColor + 1 && rowNum <= 5 * rowsPerColor + 1) {
			color = Color.CYAN;
		}

		return color;
	}

	// Returns proper color brick for remainder = 2.
	private Color colorsForTwo(int rowNum, int rowsPerColor) {

		Color color = null;

		if (rowNum <= rowsPerColor + 1) {
			color = Color.RED;
		} else if (rowNum > rowsPerColor + 1 && rowNum <= 2 * rowsPerColor + 2) {
			color = Color.ORANGE;
		} else if (rowNum > 2 * rowsPerColor + 2 && rowNum <= 3 * rowsPerColor + 2) {
			color = Color.YELLOW;
		} else if (rowNum > 3 * rowsPerColor + 2 && rowNum <= 4 * rowsPerColor + 2) {
			color = Color.GREEN;
		} else if (rowNum > 4 * rowsPerColor + 2 && rowNum <= 5 * rowsPerColor + 2) {
			color = Color.CYAN;
		}

		return color;
	}

	// Returns proper color brick for remainder = 3.
	private Color colorsForThree(int rowNum, int rowsPerColor) {

		Color color = null;

		if (rowNum <= rowsPerColor + 1) {
			color = Color.RED;
		} else if (rowNum > rowsPerColor + 1 && rowNum <= 2 * rowsPerColor + 2) {
			color = Color.ORANGE;
		} else if (rowNum > 2 * rowsPerColor + 2 && rowNum <= 3 * rowsPerColor + 3) {
			color = Color.YELLOW;
		} else if (rowNum > 3 * rowsPerColor + 3 && rowNum <= 4 * rowsPerColor + 3) {
			color = Color.GREEN;
		} else if (rowNum > 4 * rowsPerColor + 3 && rowNum <= 5 * rowsPerColor + 3) {
			color = Color.CYAN;
		}

		return color;
	}

	// Returns proper color brick for remainder = 4.
	private Color colorsForFour(int rowNum, int rowsPerColor) {

		Color color = null;

		if (rowNum <= rowsPerColor + 1) {
			color = Color.RED;
		} else if (rowNum > rowsPerColor + 1 && rowNum <= 2 * rowsPerColor + 2) {
			color = Color.ORANGE;
		} else if (rowNum > 2 * rowsPerColor + 2 && rowNum <= 3 * rowsPerColor + 3) {
			color = Color.YELLOW;
		} else if (rowNum > 3 * rowsPerColor + 3 && rowNum <= 4 * rowsPerColor + 4) {
			color = Color.GREEN;
		} else if (rowNum > 4 * rowsPerColor + 4 && rowNum <= 5 * rowsPerColor + 4) {
			color = Color.CYAN;
		}

		return color;
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

	// Calls introduction, instructions, and start message methods.
	private void displayIntroMessaging() {
		introMessage();
		instructions();
		startMessage();
	}

	// Displays welcome message, ball, and click instruction.
	// Removes messaging after click.
	private void introMessage() {

		// Displays welcome banner.
		GLabel welcome1 = new GLabel("Welcome to Breakout300!");
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

		// Sets score = 0 for beginning of game.
		score = 0;

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

	// Progressively speeds up y component of velocity until a ceiling of 7 is reached.
	private void speedUp() {

		// Increase y speed component by 0.02 every other brick per ball, until a ceiling of 7 is reached.
		if (bricksThisBall % 2 == 0 && Math.abs(vy) <= 7) {
			if (Math.abs(vy) > 0) {
				vy = vy + 0.02;
			} else {
				vy = vy - 0.02;
			}
		}
	}

	/* 
	 * This method defines the core game play functions for breakout.
	 * 
	 * It defines the imagine of the ball on the start screen as a functional game ball,
	 * sets it in motion, and controls its interaction with the walls, bricks, and paddle.
	 * 
	 * Key methods divide the canvas into upper and lower sections, which allows
	 * the interactions to be broken down into "ball + brick + wall" in the upper half and
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

		// Starts bricksThisBall at zero for each ball.
		bricksThisBall = 0;

		// Animates ball while a ball remains in play and there are tiles remaining.
		while(notYetWon() && hitsBottomWall(gameBall) == false) {

			// update world.
			gameBall.move(vx,vy);

			// Runs gameplay in upper half of screen.
			runUpperHalfOfScreen(gameBall);

			// Accelerates the ball horizontally in a narrow band after 150 bricks have been broken.
			magicLine(gameBall);

			// Runs gameplay in lower half of screen.
			runLowerHalfOfScreen(gameBall);

			// Pause.
			pause(DELAY);
		}	

		// Removes game ball if touches bottom all or game is won.
		remove(gameBall);
	}

	// Directs ball surrounding checks while it is in the upper half of the screen.
	private void runUpperHalfOfScreen(GObject gameBall) {

		// Defines interactions with walls and bricks (in the upper half of the canvas).
		if (inUpperHalf(gameBall)) {
			checkUpTop(gameBall);
			checkUpLeft(gameBall);
			checkUpRight(gameBall);
			checkUpBottom(gameBall);
		}
	}

	// Rapidly accelerates ball to speed (absolute) of 4.5 after the seventh brick is broken.
	private void sevenSpeedBonus() {
		if (brickCount > 7 && vy > -4.5) {
			vy = vy - 0.1;
		}
	}

	// Calls messages for level seven speed bonus, magic line, and inverted paddle at appropriate times.
	private void midLevelMessage() {
		if (brickCount == 7) {
			displaySpeedUp();
		} else if (brickCount == 3 * totalNumberOfBricks() / 2) {
			displayMagicLine();
		} else if (brickCount == 5 * totalNumberOfBricks() / 2 - 1) {
			displayReversePaddle();
		}
	}

	// Displays and removes bonus speed message displayed after 7th brick.
	private void displaySpeedUp() {

		GLabel speedUp = new GLabel("Bonus Speed");
		speedUp.setFont("Helvetica-28");
		double speedUpX = setMessageX(speedUp); 
		double speedUpY = setAboveMessageY(speedUp);
		add(speedUp, speedUpX, speedUpY);

		GLabel click = new GLabel("[click to continue]");
		click.setFont("Helvetica-18");
		double clickX = setMessageX(click);
		double clickY = setBelowMessageY(click);
		add(click, clickX, clickY);

		// Pause for user to read.
		waitForClick();

		// Clears message for return to gameplay.
		remove(speedUp);
		remove(click);
	}

	// Displays and removes magicLine message displayed after 150th brick.
	private void displayMagicLine() {

		GLabel magicLine = new GLabel("Magic Deflector");
		magicLine.setFont("Helvetica-28");
		double magicLineX = setMessageX(magicLine); 
		double magicLineY = setAboveMessageY(magicLine);
		add(magicLine, magicLineX, magicLineY);

		GLabel click = new GLabel("[click to continue]");
		click.setFont("Helvetica-18");
		double clickX = setMessageX(click);
		double clickY = setBelowMessageY(click);
		add(click, clickX, clickY);

		// Pause for user to read.
		waitForClick();

		// Clears message for return to gameplay.
		remove(magicLine);
		remove(click);
	}

	// Displays and removes inverted paddle message displayed after 249th brick.
	private void displayReversePaddle() {

		GLabel reversePaddle = new GLabel("Inverted Paddle");
		reversePaddle.setFont("Helvetica-28");
		double revPadX = setMessageX(reversePaddle); 
		double revPadY = setAboveMessageY(reversePaddle);
		add(reversePaddle, revPadX, revPadY);

		GLabel click = new GLabel("[click to continue]");
		click.setFont("Helvetica-18");
		double clickX = setMessageX(click);
		double clickY = setBelowMessageY(click);
		add(click, clickX, clickY);

		// Pause for user to read message.
		waitForClick();

		// Clears messages to return to gameplay.
		remove(reversePaddle);
		remove(click);
	}

	// Directs ball surrounding checks while it is in the lower half of the screen.
	private void runLowerHalfOfScreen(GObject gameBall) {

		// Directs interactions with reflective walls and paddle if in lower half of screen.
		if (inLowerHalf(gameBall)) {
			checkLeftWall(gameBall);
			checkRightWall(gameBall);
			checkPaddle(gameBall);
		}
	}

	/*
	 * Creates a "magic line" between y = 250 and y = 350.
	 * Within this band, the ball is accelerated horizontally with variations depending on
	 * whether the brickCOunt is even or odd, and with magnitude based upon the original
	 * x velocity and without ever exceeding an absolute magnitude of vx = 3.
	 * 
	 * This line is activated in the middle of level 2.
	 */
	private void magicLine(GObject gameBall) {
		if (timeForMagicLine() && ballBelow350(gameBall) && ballAbove250(gameBall)) {
			if (vy < 0) {
				if (brickCount % 2 == 0) {
					if (Math.abs(vx) < 0.5) {
						vx = vx + .1; 
					} else if (Math.abs(vx) < 1.5 && Math.abs(vx) > 0.5) {
						vx = vx + .075;
					} else if (Math.abs(vx) < 2.5 && Math.abs(vx) > 1.5) {
						vx = vx + 0.05;
					} else if (Math.abs(vx) < 3 && Math.abs(vx) > 2.5) {
						vx = vx + 0.01;
					} else {
						vx = vx;
					}
				} else {
					if (Math.abs(vx) < 0.5) {
						vx = vx - .1; 
					} else if (Math.abs(vx) < 1.5 && Math.abs(vx) > 0.5) {
						vx = vx - .075;
					} else if (Math.abs(vx) < 2.5 && Math.abs(vx) > 1.5) {
						vx = vx - 0.05;
					} else if (Math.abs(vx) < 3 && Math.abs(vx) > 2.5) {
						vx = vx - 0.01;
					} else {
						vx = vx;
					}
				}
			}
		}
	}

	// Returns yes if at least 150 bricks have been broken.
	private boolean timeForMagicLine() {
		return brickCount > 3 * totalNumberOfBricks() / 2;
	}

	// Returns yes if ball below y = 350.
	private boolean ballBelow350(GObject gameBall) {
		return gameBall.getY() < 350;
	}

	// Returns yes if ball above y = 250.
	private boolean ballAbove250(GObject gameBall) {
		return gameBall.getY() > 250;
	}

	// Directs interactions with left wall.
	// Ensures ball doesn't get  stuck to wall at higher velocities.
	private void checkLeftWall(GObject gameBall) {
		// Instructs interactions with wall on left.
		if (hitsLeftWall(gameBall)) {
			bounceClip.play();
			vx = Math.abs(vx);
		}
	}

	//  Directions interactions with right wall.
	private void checkRightWall(GObject gameBall) {

		// Instructs interactions with wall on the right.
		// Ensures ball doesn't get stuck to wall at higher velocities.
		if (hitsRightWall(gameBall)) {
			bounceClip.play();
			vx = - Math.abs(vx);
		}
	}

	// Directs interactions with "reactive" paddle after finding paddle below ball.
	private void checkPaddle(GObject gameBall) {

		// Instructs interactions with the paddle.
		// Also, calls methods for building next level, mid level messages, and seven speed bonus.
		if (hitsPaddleOnBottom(gameBall)) {
			double X1 = paddle.getX();
			double X2 = (gameBall.getX() + BALL_RADIUS);
			vx = returnVxByPointOfImpact(X1, X2);
			vy = makeNegativeVelocityAndPlaySound(vy);
			buildsNextLevel();
			midLevelMessage();
			sevenSpeedBonus();
		}
	}

	// Directs interactions with top of the ball in top of the canvas.
	private void checkUpTop(GObject gameBall) {

		// Instructs interactions with objects on the top (of the ball).
		if (hitsCeiling(gameBall) || hitsBrickAbove(gameBall)) {
			vy = reverseVelocityAndPlaySound(vy);
			if (hitsBrickAbove(gameBall)) {
				GObject brick = grabsBrickOnTop(gameBall);
				countAndDestroyBrick(brick);
				bricksThisBall++;
				speedUp();
			}
		}
	}

	// Directs interactions with left of the ball in top of the canvas.
	private void checkUpLeft(GObject gameBall) {

		// Instructs interactions with objects on left.
		if (hitsLeftWall(gameBall) || hitsBrickOnLeft(gameBall)) {
			vx = reverseVelocityAndPlaySound(vx);
			if (hitsBrickOnLeft(gameBall)) {
				GObject brick = grabsBrickOnLeft(gameBall);
				countAndDestroyBrick(brick);
				bricksThisBall++;
				speedUp();
			}
		}
	}

	// Directs interactions with right of the ball in top of the canvas.
	private void checkUpRight(GObject gameBall) {

		// Instructs interactions with objects on the right.
		if (hitsRightWall(gameBall) || hitsBrickOnRight(gameBall)) {
			vx = reverseVelocityAndPlaySound(vx);
			if (hitsBrickOnRight(gameBall)) {
				GObject brick = grabsBrickOnRight(gameBall);
				countAndDestroyBrick(brick);
				bricksThisBall++;
				speedUp();
			}
		}
	}

	// Directs interactions with bottom of the ball in top of the canvas.
	private void checkUpBottom(GObject gameBall) {

		// Instructs interactions with objects on the bottom (of the ball).
		if (hitsObjectOnBottom(gameBall)) {
			vy = reverseVelocityAndPlaySound(vy);
			GObject brick = grabsBrickOnBottom(gameBall);
			countAndDestroyBrick(brick);
			bricksThisBall++;
			speedUp();
		}
	}

	// Builds new levels after the previous is completed, unless the game is won.
	private void buildsNextLevel() {

		// Builds second level.
		if (brickCount == totalNumberOfBricks()) {
			setupBricks();
			pauseForWarning();
		}

		// Builds third level.
		if (brickCount == 2 * totalNumberOfBricks()) {
			setupBricks();
			pauseForWarning();
		}
	}

	private boolean hitsBottomWall(GObject ball) {
		return ball.getY() > 600 - 2 * BALL_RADIUS;
	}

	// Returns reactive x component of velocity for reactive paddle.
	private double returnVxByPointOfImpact(double X1, double X2) {
		return -3 + (int) ((X2 - X1) / 10);
	}

	// Defines "winning" as deleting all of the bricks in three levels.
	private boolean winQuery() {
		return brickCount == 3 * NBRICK_ROWS * NBRICK_COLUMNS;
	}

	// Returns true if game is not yet won.
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
	private double reverseVelocityAndPlaySound(double v) {
		bounceClip.play();
		return -v;
	}

	// Increases brickCount by one for every brick destroyed.
	// Displays score every time brick is destroyed.
	private void countAndDestroyBrick(GObject brick) {
		remove(brick);
		score = score + 7 + vy;
		int intScore = (int) score;
		scoreLabel.setLabel("score = " + intScore);
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

	private boolean hitsPaddleOnBottom(GObject gameBall) {
		return getElementAt(gameBallCenterX(gameBall), gameBallBottom(gameBall)) == paddle;
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
	private double makeNegativeVelocityAndPlaySound(double v) {
		bounceClip.play();
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

	// Displays messaging after game has been won.
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
		double last3Y = setBelowMessageY(last3);
		add(last3, last3X, last3Y);	
	}

	// Displays warning between levels between levels.
	private void pauseForWarning() {

		// Sets up and defines level messages for 2 and 3.
		GLabel level = new GLabel("Level 2");
		GLabel click = new GLabel("[click to continue]");
		click.setFont("Helvetica-18");
		double clickX = setMessageX(click);
		double clickY = setBelowMessageY(click);
		add(click, clickX, clickY);

		// Decides if message for level 2 or 3.
		if (brickCount == totalNumberOfBricks()) {

			// Displays message for level 2.
			level.setFont("Helvetica-28");
			double levelX = setMessageX(level); 
			double levelY = setAboveMessageY(level);
			add(level, levelX, levelY);
		} else {
			
			// Displays message for level 3.
			level = new GLabel("Level 3");
			level.setFont("Helvetica-28");
			double levelX = setMessageX(level); 
			double levelY = setAboveMessageY(level);
			add(level, levelX, levelY);
		}

		// Pauses for user to read message.
		waitForClick();

		// Clears message for gameplay.
		remove(click);
		remove(level);
	}

	//Prevents game from restarting with remaining balls.
	private void finish() {
	}
}