/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Avalon Wolfe
 * Section Leader: Julia Daniel
 * 
 * The Breakout class creates the game Breakout. It sets up the initial environment of the game,
 * with colored bricks, a paddle, and a ball and allows the user three rounds to attempt to win 
 * the game. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

	public static final double CANVAS_WIDTH = 420;

	public static final double CANVAS_HEIGHT = 600;

	public static final int NBRICK_COLUMNS = 10;

	public static final int NBRICK_ROWS = 10;

	public static final double BRICK_SEP = 4;

	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	public static final double BRICK_HEIGHT = 8;

	public static final double BRICK_Y_OFFSET = 70;

	public static final double PADDLE_WIDTH = 60;

	public static final double PADDLE_HEIGHT = 10;

	public static final double PADDLE_Y_OFFSET = 30;

	public static final double BALL_RADIUS = 10;

	public static final double VELOCITY_Y = 3.0;

	public static final double VELOCITY_X_MIN = 1.0;

	public static final double VELOCITY_X_MAX = 7.0;

	public static final double DELAY = 1000.0 / 60.0;

	public static final int NTURNS = 3;

	private GRect paddle;

	private GOval ball;

	private double vx = VELOCITY_X_MAX;

	private double vy = VELOCITY_Y;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		playBreakout();
	}

	/*
	 * The method setUp creates the initial environment of the game Breakout. It creates all of 
	 * the necessary entities required for the game: the bricks, the paddle, and the ball. 		
	 */
	private void setUp() {
		setBricks();
		addMouseListeners();
		setPaddle();
		setBall();
	}

	/*
	 * The method setBricks creates each filled, colored brick in accordance with the number of 
	 * rows and the number of columns. 
	 */
	private void setBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double yCoord = BRICK_Y_OFFSET + (row * BRICK_HEIGHT) + (row * BRICK_SEP);
			for (int brick = 0 ; brick < NBRICK_COLUMNS; brick++) {
				double xCoord = (getWidth() / 2) - ((BRICK_WIDTH + BRICK_SEP) * (NBRICK_COLUMNS / 2))
						+ (brick * BRICK_WIDTH) + (brick * BRICK_SEP) + (BRICK_SEP / 2);
				GRect newBrick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				newBrick.setLocation(xCoord, yCoord);
				newBrick.setFilled(true);
				setBrickColor(newBrick, row);
				add(newBrick);
			}
		}
	}

	/* 
	 * The method setPaddle creates the paddle near the bottom of the screen that is
	 * used to hit the ball.
	 */
	private void setPaddle() {  
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setLocation((getWidth() / 2) - (PADDLE_WIDTH / 2), (getHeight() - PADDLE_Y_OFFSET));
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * The method mouseMoved moves the paddle horizontally in accordance with the x coordinate
	 * of the mouse. 
	 */
	public void mouseMoved (MouseEvent e) {
		double xCoord = e.getX() - (PADDLE_WIDTH / 2);
		double yCoord = getHeight() - PADDLE_Y_OFFSET; 
		// the two if statements ensure the paddle doesn't go out of bounds
		if (e.getX() - (PADDLE_WIDTH / 2) < 0) {
			xCoord = 0;
		}
		if (e.getX() + (PADDLE_WIDTH / 2) > getWidth()) {
			xCoord = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(xCoord, yCoord);
	}

	/* 
	 * The method setBall creates the ball that is used to eliminate bricks.
	 */
	private void setBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setLocation((getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * The method setBrickColor sets the color of each brick based on which row the brick is in.
	 */
	private void setBrickColor(GRect newBrick, int row) {
		int num = 10;
		if (row % num < 2) {
			newBrick.setColor(Color.RED);
		}
		if (row % num >= 2 && row % num < 4) {
			newBrick.setColor(Color.ORANGE);
		}
		if (row % num >= 4 && row % num < 6) {
			newBrick.setColor(Color.YELLOW);
		}
		if (row % num >= 6 && row % num < 8) {
			newBrick.setColor(Color.GREEN);
		}
		if (row % num >= 8 && row % num < 10) {
			newBrick.setColor(Color.CYAN);
		}
	}

	/*
	 * The method playBreakout plays the game. It sets up everything required for game play, such as
	 * the reaction of the ball after hitting an object and the boundaries of the game.
	 */
	private void playBreakout() {
		int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS; 
		for (int round = 0; round < NTURNS; round++) {
			// makes ball initially bounce at a random angle 
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			while (bricksRemaining > 0) {
				ball.move(vx, vy);
				bounceOffWalls();
				// occurs when user fails to hit ball and sets up the ball to begin new round
				if (hitBottomWall( ball)) {
					remove(ball);
					setBall();
					break;  
				}
				pause (DELAY);
				GObject collider = getCollidingObject(); 
				respondToCollidingObject(collider, bricksRemaining);
			}
			// adds a delay between the end of one round and the beginning of the next round
			pause(500);
		}
		giveGameResult(bricksRemaining);
	}

	/*
	 * The method bounceOffWalls creates the boundaries of the game so that the ball bounces off
	 * (or changes velocity) when it hits the right, left, or top wall.
	 */
	private void bounceOffWalls() {
		if (hitRightWall(ball) || hitLeftWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			vy = -vy;
		}
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth() ;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		// this chunk of code tests to see if there is an object at each point of the
		// invisible "square" surrounding the circle to determine whether a collision occurs
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()); 
			if (collider != null) {
				return collider;
			} else {
				collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
				if (collider != null) {
					return collider;
				} else {
					collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
					if (collider != null) {
						return collider;
					} else {
						return null;
					}
				}
			}
		}
	}

	/*
	 * The method respondToCollidingObject removes the collider (the object the ball collided into)
	 * if it is a brick and does nothing if collider is the paddle. Upon collision, the ball makes
	 * a bounce noise and bounces off of collider.
	 */
	private void respondToCollidingObject(GObject collider, int bricksRemaining) {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		if (collider != null) {
			// when the ball hits collider, only changes velocity whenever moving downwards
			if (collider == paddle) {
				if (vy > 0) {
					vy = -vy;
					bounceClip.play();
				}
			} else {
				bounceClip.play();
				remove (collider);
				bricksRemaining = bricksRemaining - 1;
				vx = rgen.nextDouble(1.0, 3.0);
				if (rgen.nextBoolean(0.5)) vx = -vx;
				vy = -vy;
			}
		}
	}

	/*
	 * The method giveGameResult prints whether the loser won or lost Breakout on the screen, upon winning the game by 
	 * eliminating all bricks or upon losing the game by allowing the ball to exit bottom of 
	 * screen each round.
	 */
	private void giveGameResult(int bricksRemaining) {
		if (bricksRemaining == 0) {
			GLabel win = new GLabel("CONGRATS! YOU WON BREAKOUT!");
			win.setLocation(getWidth() / 2 - win.getWidth() / 2, 3 * getHeight() / 4 - win.getHeight() / 2);
			win.setColor(Color.BLUE);
			add(win);
		} else {
			GLabel lose = new GLabel("Sorry, try again!");
			lose.setLocation(getWidth() / 2 - lose.getWidth() / 2, 3 * getHeight() / 4 - lose.getHeight() / 2);
			lose.setColor(Color.RED);
			add(lose);
		}
	}
}
