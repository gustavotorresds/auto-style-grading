/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Joseph Matan
 * Section Leader: Shanon
 * 
 * This file plays Breakout, with a few additions
 * You can now change which direction the ball goes by hitting it with 
 * different parts of the paddle
 * Also the game keeps score now and will go on
 * until you lose
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {
	//Dimensions of the canvas, in pixels
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
	public static final double BRICK_SEP = 8;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	private double paddleX = new Double (40.0);
	private int brickcount = new Integer (NBRICK_ROWS * NBRICK_COLUMNS);
	GRect middlepaddle;
	GRect leftpaddle;
	GRect rightpaddle;
	int brickscore = 0;
	int levelscore = 1;
	private GLabel score = new GLabel("");
	private GLabel level = new GLabel("");

	GOval ball;
	double vy = 5.0;
	double vx = 0.0;
	int paddlehits = 1;
	boolean gamecondition = true;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	public void run() {

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		addMouseListeners();
		setUpGame();
		startAndMoveBall();
	}


	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			return getElementAt(ball.getX(),ball.getY());
		} else if (getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		} else if (getElementAt(ball.getX() + (2* BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX() + (2* BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		} else if (getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS));
		} else {
			return null;
		}
	}

	private void startAndMoveBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (gamecondition = true) {
			addlabels();
			ball.move(vx, vy);
			if ((ball.getY() < 0)) {
				vy = -vy;
			}
			if ((ball.getY() > getHeight() - ball.getHeight())) {
				ball.setFillColor(Color.RED);
				GLabel loseText = new GLabel("YOU LOSE");
				loseText.setFont("Courier-34");
				loseText.setColor(Color.RED);
				add(loseText, (getWidth()/2) - (loseText.getWidth() / 2), (getHeight()/2) - (loseText.getHeight() / 2));
				remove(ball);
				break;
			}
			if ((ball.getX() > getWidth() - ball.getWidth()) || (ball.getX() < 0)) {
				vx = -vx;
			}
			//getCollidingObject();
			GObject collider = getCollidingObject();
			if (collider == middlepaddle) {
				vy = -(Math.sqrt(vy * vy));
				paddlehits++;
			} else if (collider == rightpaddle) {
				vy = -(Math.sqrt(vy * vy));
				paddlehits++;
				if (vx < 0) {
					vx = -vx;
				}
			} else if (collider == leftpaddle) {
				vy = -(Math.sqrt(vy * vy));
				paddlehits++;
				if (vx > 0) {
					vx = -vx;
				}
			} else if (collider == level || collider == score) {
				vy = vy;
				vx = vx;
			} else if (collider != null) {
				vy = -vy;
				remove(collider);
				brickscore++;
				brickcount--;
				if (brickcount == 0 ) {
					levelscore++;
					remove(ball);
					remove(rightpaddle);
					remove(middlepaddle);
					remove(leftpaddle);
					brickcount = (NBRICK_ROWS * NBRICK_COLUMNS);
					setUpGame();
					startAndMoveBall();
				}
			}
			pause(DELAY);
		}
	}

	private void addlabels() {
		score.setFont("Courier-20");
		level.setFont("Courier-20");
		String bricktext = new String("score: " + (brickscore * levelscore)); // score multiplied by level
		String leveltext = new String("level: " + levelscore);
		score.setLabel(bricktext);
		level.setLabel(leveltext);
		add(score, getWidth() - 150, getHeight() -10);
		add(level, getWidth() - 400, getHeight() - 10);
	}


	private void setUpGame() {
		buildBlocks();
		buildPaddle();
		buildBall();
	}

	private void buildBall() {
		ball = new GOval (BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add (ball, (getWidth() / 2) - (BALL_RADIUS / 2), ((getHeight() / 2) - (BALL_RADIUS / 2)));
	}

	private void buildPaddle() {
		middlepaddle = makePaddle();
		addPaddle(middlepaddle, paddleX);
		rightpaddle = makePaddle();
		addPaddle(rightpaddle, paddleX + (PADDLE_WIDTH / 3));
		leftpaddle = makePaddle();
		addPaddle(leftpaddle, paddleX - (PADDLE_WIDTH / 3));
	}

	private void addPaddle(GObject whichPaddle, double xOfPaddle) {
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add (whichPaddle, xOfPaddle, y);

	}

	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH / 3, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		return paddle;
	}

	private void buildBlocks() {
		for (int j = 0; j < NBRICK_ROWS + 1; j++) {
			for (int i = NBRICK_COLUMNS; i > 0; i--) { //how many bricks will print for a given layer
				double x = (i * (BRICK_WIDTH + BRICK_SEP) - BRICK_WIDTH + (BRICK_SEP / 2));
				double y = ((BRICK_HEIGHT + BRICK_SEP) * j) + BRICK_SEP + BRICK_Y_OFFSET;
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(colorbricks(j));
				add(brick);
			}
		}
	}

	private Color colorbricks(int j) {
		if (j == 0 || j == 1) {
			return Color.RED;
		} else if (j == 2 || j== 3) {
			return Color.ORANGE;
		} else if (j == 4 || j == 5) {
			return Color.YELLOW;
		} else if (j == 6 || j == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x < getWidth() - (PADDLE_WIDTH / 2) + (PADDLE_WIDTH / 3) && x > (PADDLE_WIDTH / 2) + (PADDLE_WIDTH / 3)) {
			middlepaddle.setLocation(x - (PADDLE_WIDTH / 2), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
			rightpaddle.setLocation(middlepaddle.getX() + (PADDLE_WIDTH / 3), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT );
			leftpaddle.setLocation(middlepaddle.getX() - (PADDLE_WIDTH / 3), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT );
		}
	}
}