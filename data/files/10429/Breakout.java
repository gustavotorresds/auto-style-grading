/*
 * File: Breakout.java
 * -------------------
 * Name: Tabitha Bandy-Vizcaino
 * Section Leader: Justin Xu
 * 
 * This file hopefully implements a proper game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import javafx.scene.control.Label;

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
	public static final int NBRICK_COLUMNS =10;

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

	// Paddle that will bounce the ball.
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// Ball, at center of canvas. Will move once clicked. Bounces off objects.
	private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);

	// The message at center of screen that gives direction to click,
	// gives the count down to play, and returns data on whether
	// the game was lost or won.
	private GLabel message = new GLabel("");

	// This will read as a life counter for player at top right of screen
	private GLabel lifeCounter = new GLabel("");

	// Helps generate a random velocity for x
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// The counter for how many bricks are left in play.
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;

	// At start of game, mouse will have been clicked 0 times.
	// Once mouse is clicked, then the play will ensue. If the
	// mouse is clicked more than one time, the program will
	// ignore the input.
	private int mouseClicked = 0;

	/*
	 *  This program will prompt the user to click so the
	 *  game can start. It will then count down from 3. A 
	 *  ball will begin to move and interact with the objects
	 *  in the game (bricks, the paddle, the life counter) and
	 *  bounce off the wall. If the ball falls past the paddle,
	 *  it will lose a life, and the game will restart. There are
	 *  3 lives. The object of the game is to remove all of the 
	 *  bricks (the bricks are removed by touching them). The
	 *  use moves the mouse to move the paddle to keep the ball
	 *  alive.
	 */
	public void run() {


		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Makes the bricks and ball
		createBricks();

		addMouseListeners();


		// The game will not be playable until the mouse is clicked
		while (mouseClicked == 0) {
			//Sets messages at center of screen
			double x = getWidth()/2 - message.getWidth()/2;
			double y = getHeight()/2 - message.getAscent()/2;

			// Gives user instruction
			message.setLabel("CLICK TO PLAY");
			add(message, x, y);

			// Mouse is clicked, the game will start.
			if (mouseClicked == 1) {
				remove(message); // This way the message does not interrupt gameplay.
				for (int lives = NTURNS; lives > 0; lives--) {	// Count lives.
					if (bricksLeft > 0) {
						lifeCounter.setLabel("lives: " + lives);

						// When label changes, it's position might
						// need to change slightly...
						x = getWidth() - lifeCounter.getWidth() * 2;
						y = (BRICK_Y_OFFSET - lifeCounter.getAscent()) / 2;

						add(lifeCounter, x, y);

						startSequence();
						createBall();
						ballMotion();
					}
				}
			}
		}
		endSequence();	
	}

	/*
	 * The center screen label notifies the player that 3
	 * seconds are left before the ball will start moving.
	 */
	private void startSequence() {
		for (int seconds = 3; seconds > 0; seconds--) {	// Loop counts down in 3 cycles
			message.setLabel("Ready? " + seconds);

			double x = getWidth()/2 - message.getWidth()/2;
			double y = getHeight()/2 - message.getAscent()/2;

			add(message, x, y);

			pause(1000);
		}
		remove(message);	// Message needs to be removed so the player can start the game.
	}


	/*
	 * This step creates bricks. It will make the leftmost
	 * column first from top to bottom. After completing 
	 * a column, the program will then fill out the columns 
	 * from left to right.
	 */
	private void createBricks(){
		// Set up the first column on left side
		double x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - (NBRICK_COLUMNS - 1) * BRICK_SEP) / 2;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			double y = BRICK_Y_OFFSET;	// Start first brick in column

			for (int j = 0; j < NBRICK_ROWS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(rainbow(j));
				add(brick, x, y);

				y = y + BRICK_SEP + BRICK_HEIGHT;	// Translate next brick down the column
			}
			x = x + BRICK_SEP + BRICK_WIDTH;	// Translate the next row one step to the right
		}
	}

	/*
	 * If the number of rows is 10, then there
	 * will be 2 red bricks on top, then 2 orange bricks, 2
	 * yellow bricks, 2 green bricks, and then 2 cyan bricks.
	 * If number of rows is not 10, then the program
	 * will do a rough estimate such that each color covers 1/5
	 * of the column. Colors from top to bottom.
	 */
	private Color rainbow(int j) {
		if (j < NBRICK_ROWS / 5) {
			return Color.RED;	//First fifth of bricks are red
		} else if (j < 2 * NBRICK_ROWS / 5) {
			return Color.ORANGE;	//Second fifth is orange
		} else if (j < 3 * NBRICK_ROWS / 5) {
			return Color.YELLOW;	//Third fifth is yellow
		} else if (j < 4 * NBRICK_ROWS / 5) {
			return Color.GREEN;	//Fourth fifth is green
		} else {
			return Color.CYAN;	//Last fifth is cyan
		}
	}

	// The ball is created and added to the center of the screen. 
	private void createBall() {
		double xBall = getWidth()/2 - BALL_RADIUS;
		double yBall = getHeight()/2 - BALL_RADIUS;

		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, xBall, yBall);
	}

	/*
	 * This method will animate the ball across the screen. It will
	 * set a random starting x velocity and a standard y velocity.
	 * It keeps the ball from going off the screen and makes the 
	 * ball bounce off certain walls and objects.
	 */
	private void ballMotion() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;

		while (bricksLeft > 0) {	// Game will only proceed if there are still bricks
			ball.move(vx, vy);
			pause(DELAY);

			if (ball.getY() <= 0) {	// Ball bounces off top wall
				vy = -vy;
			}
			if (ball.getX() <= 0) { // Ball bounces off left wall
				vx = -vx;
			}
			if (ball.getRightX() >= getWidth()) { // Ball bounces off right wall
				// First make sure ball is not stuck behind right wall
				while (ball.getRightX() > getWidth()) {	
					ball.move(-vx, vy);
				}
				vx = -vx;
			}
			// Ball will go past bottom wall, but will lose a life.
			if ( ball.getBottomY() >= getHeight()) {
				remove(ball);
				break;
			}
			// Ball will bounce of specific objects and change vy.
			vy = collisionChecker(vy);
			// If ball bounces off paddle, it's vx will become random again.
			if (collisionChecker(vx) == -vx) {
				vx = randomizeV(vx);
			}
		}
	}

	/*
	 * This method makes x velocity random after it hits an object.
	 * It makes sure that new velocity has the same direction.
	 */
	private double randomizeV(double vx) {
		if (vx > 0) {
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (vx < 0) {	// In case new velocity changed sign
				vx = -vx;
			}
		}
		if (vx < 0) {
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (vx > 0) {	// In case new velocity changed sign
				vx = -vx;
			}
		}
		return vx;
	}

	/*
	 * This checks to see if the ball is in contact with any
	 * of the other objects in the game. If it is touching
	 * any bricks, it will remove the brick and make remove
	 * a brick from its brick counter. If the ball is in 
	 * contact with other objects, it will just bounce off
	 * in a different y direction.
	 */
	private double collisionChecker(double v) {
		GObject collider = null;

		if (getElementAt(ball.getX(), ball.getY()) != null) {	// Objects at top left corner of ball?
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getRightX(), ball.getY()) != null) {	// Objects at top right corner?
			collider = getElementAt(ball.getRightX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getBottomY()) != null) {	// Objects at bottom left corner?
			collider = getElementAt(ball.getX(), ball.getBottomY());
		} else if (getElementAt(ball.getRightX(), ball.getBottomY()) != null) {	// Objects at bottom right corner?
			collider = getElementAt(ball.getRightX(), ball.getBottomY());
		}

		if (collider == paddle || collider == lifeCounter) {	// Is that object the paddle or counter?
			double greaterY = paddle.getBottomY();
			double lesserY = paddle.getY();

			while (ball.getBottomY() > lesserY & ball.getBottomY() < greaterY) { // Prevent ball from getting caught on paddle
				ball.move(0, -v);
				pause(DELAY);
			}
			v = -v;
		} else if (collider != null) { // Was there an object? Yes? It was a brick then.
			v = -v;
			remove(collider);
			bricksLeft--;
		}
		return v;
	}

	/*
	 * This method stores the paddle information.
	 * Can be easily changed to make paddle a different
	 * color or not filled.
	 */
	private void createPaddle(double x, double y) {
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, x, y);
	}

	/*
	 * This is what happens when the game is over.
	 * It will let you know if you win...
	 */
	private void endSequence() {
		// Place message in center of screen.
		double x = (getWidth() - message.getWidth()) / 2;
		double y = (getHeight() - message.getAscent()) / 2;

		remove(ball);	// Remove the ball because no more play

		if (bricksLeft == 0) {	// Did the player get all the bricks to beat the game?
			message.setLabel("YOU WIN");
		} else {	// The player lost all their lives before beating the game.
			message.setLabel("GAME OVER");
		}
		add(message, x, y);
	}

	// This allows the paddle to follow the mouse.
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;

		if (x + PADDLE_WIDTH <= getWidth() & x >= 0) {	// The paddle cannot leave screen.
			createPaddle(x, y);
		}
	}

	// Count mouse clicks so that the first time the mouse
	// clicks, the game will start.
	public void  mouseClicked(MouseEvent e) {
		mouseClicked++;
	}
}
