
/*
 * File: Breakout.java
 * -------------------
 * Name: An-Chih (Angel) Yang
 * Section Leader: Jonathan Kula
 * 
 * This file will eventually implement the game of Breakout. 
 * I have almost completed all the basic components, except that the ball
 * sometimes "glued" on the paddle. I also added some extra components:
 * (1) Keep score: Calculate the total score after the player eliminate the 
 *     bricks and shows the label of score on the top screen.
 * (2) Add messages: Show the label of "You Lose!", "You Win!", and the 
 *     number of turns left when the game is initialized. 
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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	private GRect paddle;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */

		/*
		 * Define variables: "NTurns": Number of turns that still left. "NBRICKS":
		 * Number of bricks that need to be eliminated. "score": Player's total score
		 * after eliminating bricks. "plusScore": The extra score player obtains after
		 * player eliminate bricks. Different bricks have different scores. The higher
		 * the brick, the higher score obtained.
		 */
		int NTurns = NTURNS;
		int NBRICKS = NBRICK_COLUMNS * NBRICK_ROWS;
		int score = 0;
		int plusScore;

		// Put all bricks on the screen.
		drawAllBricks();
		// Put the label of score on the screen.
		GLabel labelScoreOnScreen = labelScore(score);
		add(labelScoreOnScreen);

		// Put paddle and relates it to mouse movements.
		paddle = makePaddle();
		addPaddleToSreen(paddle);
		addMouseListeners();

		/*
		 * Put ball on the screen and initialize its speed. "vx" is the speed on
		 * x-direction. It is a random double in the allowed range ([1.0, 3.0]) and it
		 * is negative half the time. "vy" is the speed on y-direction. The absolute
		 * value is 3.0. After the user click the mouse, the ball starts moving.
		 */
		GOval ball = makeBall();
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		double vy = VELOCITY_Y;
		waitForClick();

		while (true) {
			/*
			 * The 1st part of the while loop: It defines the ball velocity after the ball
			 * hits the top/right/left walls and the paddle. The "vx" and "vy" will have
			 * opposite signs.
			 */
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			/*
			 * When the ball hits the bottom walls, the game will be initialized by removing
			 * the current ball and adding a new ball in the center of the screen. Every
			 * time when the game is initialized, the current bricks will be removed and new
			 * bricks will be re-loaded. Also, "NTurns" will be decreased by 1 and the score
			 * will be set to 0. I also made a label to show "NTurns" when the game is
			 * re-started.
			 */
			if (hitBottomWall(ball)) {
				if (NTurns > 0) {
					NTurns--;
					removeAllBricks();
					drawAllBricks();
					remove(ball);
					ball = makeBall();
					add(labelNumTurns(NTurns));
					score = 0;
					labelScoreOnScreen.setLabel("score = " + score);
					waitForClick();
					remove(labelNumTurns(NTurns));
					/*
					 * When "NTurns" becomes 0, the player lost the game and there will be a label
					 * appearing on the screen showing the lose.
					 */
				} else {
					putLabelLose();
					break;
				}
			}
			if (hitPaddle(ball, paddle)) {
				vy = -vy;
			}

			/*
			 * The 2nd part of the while loop: When the ball collides bricks, the ball will
			 * have different directions. Also, the object "collider" defines the brick the
			 * ball hits. When the ball really hits a brick, that brick will be eliminated,
			 * the score will be added based on the row number of the brick. The label of
			 * score will be updated. "NBRICKS" ( the number of bricks needed to be
			 * eliminated) is decreased by 1.
			 */

			GObject collider = getCollidingObject(ball);
			if (collider != null && collider != paddle) {
				remove(collider);
				NBRICKS--;
				plusScore = changeScore(collider);
				score = score + plusScore;
				labelScoreOnScreen.setLabel("score = " + score);
				/*
				 * When all the bricks are eliminated ("NBRICKS = 0"), the player wins the game
				 * and there will be a "You win!" label showing on the screen.
				 */
				if (NBRICKS == 0) {
					putLabelWin();
					break;
				}
				/*
				 * When the ball hits the top/bottom border of a brick, the sign of "vy" is
				 * changed. When the ball hits the left/right border of a brick, the sign of
				 * "vx" is changed.
				 */
				if (hitBrickBottom(ball, collider) || hitBrickTop(ball, collider)) {
					vy = -vy;
				} else if (hitBrickLeft(ball, collider) || hitBrickRight(ball, collider)) {
					vx = -vx;
				}
			}

			// Update visualization
			ball.move(vx, vy);
			// Pause
			pause(DELAY);
		}
	}

	// Paddle is moved by mouse.
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		if (mouseX > PADDLE_WIDTH / 2 && mouseX < getWidth() - PADDLE_WIDTH / 2)
			paddle.setLocation(mouseX - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	// Here generates the random number for the ball's initial vx speed.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Draw all the bricks.
	private void drawAllBricks() {
		double x = 0;
		double y = 0;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			for (int j = 0; j < NBRICK_ROWS; j++) {
				x = (getWidth() / 2 - BRICK_SEP / 2 - NBRICK_COLUMNS / 2 * BRICK_WIDTH
						- (NBRICK_COLUMNS / 2 - 1) * BRICK_SEP) + BRICK_SEP + i * (BRICK_WIDTH + BRICK_SEP);
				y = BRICK_Y_OFFSET + j * (BRICK_HEIGHT + BRICK_SEP);
				drawBrick(x, y, j);
			}
		}
	}

	// Remove all the bricks.
	private void removeAllBricks() {
		double x = 0;
		double y = 0;
		GObject obj = null;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			for (int j = 0; j < NBRICK_ROWS; j++) {
				x = (getWidth() / 2 - BRICK_SEP / 2 - NBRICK_COLUMNS / 2 * BRICK_WIDTH
						- (NBRICK_COLUMNS / 2 - 1) * BRICK_SEP) + BRICK_SEP + i * (BRICK_WIDTH + BRICK_SEP);
				y = BRICK_Y_OFFSET + j * (BRICK_HEIGHT + BRICK_SEP);
				obj = getElementAt(x, y);
				if (obj != null) {
					remove(obj);
				}
			}
		}
	}

	// The details of drawing bricks. Different rows of bricks have different
	// colors.
	private void drawBrick(double x, double y, int j) {
		GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		if (j == 0 || j == 1) {
			rect.setFillColor(Color.RED);
		} else if (j == 2 || j == 3) {
			rect.setFillColor(Color.ORANGE);
		} else if (j == 4 || j == 5) {
			rect.setFillColor(Color.YELLOW);
		} else if (j == 6 || j == 7) {
			rect.setFillColor(Color.GREEN);
		} else {
			rect.setFillColor(Color.CYAN);
		}
		add(rect);
	}

	// Here defines the GRect of the paddle. So the paddle can be called out in the
	// "run" module.
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}

	// Add the paddle on the screen.
	private void addPaddleToSreen(GRect paddle) {
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, paddleX, paddleY);
	}

	// Here defines the GOval of the ball.
	public GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
		return ball;
	}

	// Several boolean conditions when the ball hits the top/right/left/bottom
	// walls.
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * The boolean condition when the ball hits the paddle. Unfortunately I did not
	 * fix the problem why the ball sometimes "glued" on the paddle. I guess it has
	 * something to do with the condition defined here.
	 */
	private boolean hitPaddle(GOval ball, GRect paddle) {
		return ball.getY() + ball.getHeight() >= paddle.getY() && ball.getX() + ball.getWidth() >= paddle.getX()
				&& ball.getX() + ball.getWidth() <= paddle.getX() + paddle.getWidth();
	}

	/*
	 * Here is to use the 4 corners of the ball to find the collider. I defined 4
	 * GObject based on the 4 corners. When one of the GObject is not null, I say
	 * the collider is on the corresponding corner and return the GObject as the
	 * collider.
	 */
	private GObject getCollidingObject(GOval ball) {
		GObject obj1 = getElementAt(ball.getX(), ball.getY());
		GObject obj2 = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		GObject obj3 = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		GObject obj4 = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		GObject obj = null;
		if (obj1 != null) {
			obj = obj1;
		} else if (obj2 != null) {
			obj = obj2;
		} else if (obj3 != null) {
			obj = obj3;
		} else if (obj4 != null) {
			obj = obj4;
		}
		return obj;
	}

	/*
	 * Here is the boolean conditions to determine which part of the brick the ball
	 * collides.
	 */
	private boolean hitBrickBottom(GOval ball, GObject obj) {
		return ball.getY() <= obj.getY() + BRICK_HEIGHT;
	}

	private boolean hitBrickTop(GOval ball, GObject obj) {
		return ball.getY() + BALL_RADIUS * 2 >= obj.getY();
	}

	private boolean hitBrickLeft(GOval ball, GObject obj) {
		return ball.getX() + BALL_RADIUS * 2 >= obj.getX();
	}

	private boolean hitBrickRight(GOval ball, GObject obj) {
		return ball.getX() <= obj.getX() + BRICK_WIDTH;
	}

	// Create a "You Lose!" label on the screen.
	private void putLabelLose() {
		GLabel labelLose = new GLabel("You Lose!");
		labelLose.setFont(SCREEN_FONT);
		labelLose.setColor(Color.RED);
		labelLose.setLocation((getWidth() - labelLose.getWidth()) / 2, getHeight() / 2);
		add(labelLose);
	}

	// Create a "You Win!" label on the screen.
	private void putLabelWin() {
		GLabel labelWin = new GLabel("You Win!");
		labelWin.setFont(SCREEN_FONT);
		labelWin.setColor(Color.BLUE);
		labelWin.setLocation((getWidth() - labelWin.getWidth()) / 2, getHeight() / 2);
		add(labelWin);
	}

	// Create a label on the screen showing the number of turns left.
	private GObject labelNumTurns(int NTurns) {
		GLabel labelNum = new GLabel("You lost " + (NTURNS - NTurns) + " turn. Now " + NTurns + " turn left.");
		labelNum.setFont(SCREEN_FONT);
		labelNum.setColor(Color.ORANGE);
		labelNum.setLocation((getWidth() - labelNum.getWidth()) / 2, getHeight() / 2 + BALL_RADIUS * 2.5);
		return labelNum;
	}

	// Create a label on the screen showing the total score.
	private GLabel labelScore(int score) {
		GLabel labelScore = new GLabel("Score = " + score);
		labelScore.setFont(SCREEN_FONT);
		labelScore.setColor(Color.BLUE);
		labelScore.setLocation((getWidth() - labelScore.getWidth()) / 2, labelScore.getAscent());
		return labelScore;
	}

	// Here determines the value of "plusScore". "j" is the row number of the brick
	// (collider). Lower "j" (higher row) has higher "plusScore".
	private int changeScore(GObject collider) {
		int plusScore = 0;
		double j = (collider.getY() - BRICK_Y_OFFSET) / (BRICK_HEIGHT + BRICK_SEP);
		if (j == 0 || j == 1) {        // red bricks
			plusScore = 50;
		} else if (j == 2 || j == 3) { // orange bricks
			plusScore = 40;
		} else if (j == 4 || j == 5) { // yellow bricks
			plusScore = 30;
		} else if (j == 6 || j == 7) { // green bricks
			plusScore = 20;
		} else if (j == 8 || j == 9) { // cyan bricks
			plusScore = 10;
		}
		return plusScore;
	}
}
