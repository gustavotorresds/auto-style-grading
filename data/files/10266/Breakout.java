/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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
	public static final int NBRICK_COLUMNS = 1;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 1;

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
	Color brickColor = Color.RED;

	// This is the paddle in the game.
	private GRect paddle; 
	//This is the ball.
	private GOval ball;
	//This sets vx to a random double between 1 and 3, and vy to 
	//the given velocity.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = rgen.nextDouble(1.0, 3.0);
	private double vy = VELOCITY_Y;
	//This sets the brick counter to the number of bricks on the screen at the start 
	//of the game.
	private int brickCounter = (NBRICK_ROWS * NBRICK_COLUMNS);
	private GLabel message;

	/* This sets the canvas up, sets up the game, and lets the user play the game. 
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		addMouseListeners();
		playGame();
	} 

	/* This gives the user 3 turns to break the bricks. Within each turn, if 
	 * there are still bricks on the screen, it creates the ball, displays an opening message 
	 * with instructions and number of tries left, waits for the user to click, removes the
	 * message and starts the game. If the ball exits the move ball method, 
	 * meaning it has hit the bottom wall or the last brick, it removes the ball. 
	 * If the user has hit all the bricks, it displays a victory message, and if
	 * the user has used all the turns in the loop without hitting them all,
	 * it displays a losing message.
	 */
	private void playGame() {
		for (int turn = 0; turn < 3; turn++){
			if (brickCounter!=0) {
				createBall();
				if (turn == 0){
					openingMessage("You have 3 Tries to Break the Bricks. Click to Start.");
				} else if (turn == 1){
					openingMessage ("2 Tries Left! Click to Start.");
				} else if (turn == 2) {
					openingMessage("1 Try Left! Click to Start.");
				}
				waitForClick();
				remove(message);
				moveBall();
				remove(ball);
			}
		}
		if (brickCounter == 0) {
			victoryMessage();
		} else {
			loserMessage();
		}
	}

	/* This sets up the game by adding bricks to the screen. It rotates through
	 * the colors red, orange, yellow, green, and cyan by changing the color
	 * after every two rows in the loop that creates the bricks. Finally,
	 * it creates the paddle.
	 */
	private void setUpGame(){
		double leftmostX = getWidth()/2 - NBRICK_COLUMNS/2 * (BRICK_WIDTH + BRICK_SEP);
		double xpos;
		for (int a = 0; a < NBRICK_ROWS; a++) {
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP) * a;
			for (int i = 0; i < NBRICK_COLUMNS; i++) {	                       
				xpos = leftmostX + i*(BRICK_WIDTH+BRICK_SEP);	
				makeBrick(xpos, y, brickColor);
			}
			if (a%2 != 0) {
				if (brickColor == Color.RED) {
					brickColor = Color.ORANGE;
				} else if (brickColor == Color.ORANGE) {
					brickColor = Color.YELLOW;
				} else if (brickColor == Color.YELLOW) {
					brickColor = Color.GREEN;
				} else if(brickColor == Color.GREEN) {
					brickColor = Color.CYAN;
				} else if (brickColor == Color.CYAN) {
					brickColor = Color.RED;
				}
			}
		}
		createPaddle(getWidth()/2-PADDLE_WIDTH/2);
	}

	/* This creates the ball for the game by setting ball equal to 
	 * a new filled circle and adding it to the screen.
	 */
	private void createBall() {
		ball = new GOval (getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/* This moves the ball while there are bricks on the screen and the ball is not at the 
	 * bottom wall. First, it sets the x and y velocities and makes it so that the x direction
	 * in which the ball moves is random. It changes either the x or y velocity each time the ball hits a wall 
	 * (excluding the bottom wall). If the ball hits an object, it tests to see if that 
	 * object is the paddle, in which case the ball simply bounces off of it, or if it is not, 
	 * in which case the program removes that object (which we can assume is a brick), and the ball
	 * bounces. It keeps track of the number of bricks on the screen by subtracting 1 from the original
	 * number every time one is hit, allowing it to exit the loop if there are none left. 
	 */
	private void moveBall() {	
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while(brickCounter!=0 && ball.getY() < getHeight()-ball.getWidth() && true) {
			ball.move(vx, vy);
			if (ball.getX() > (getWidth()-ball.getWidth())) {
				vx = -vx;
			}
			if (ball.getX() < 0) {
				vx = -vx;
			}
			if (ball.getY() < 0){
				vy = -vy;
			}
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -Math.abs(vy);
			} else if (collider != null){
				brickCounter = brickCounter - 1;
				vy = -vy;
				remove(collider);
			}
			pause(DELAY);
		}
	}

	/* This tests to see if any of the four corners of the ball hit an object,
	 * and if they do, it returns that object so that it can be used in the move ball
	 * method as the collider (either the brick or paddle). If no collision is found,
	 * it returns null. 
	 */
	private GObject getCollidingObject() {
		GObject object = null;
		if (getElementAt (ball.getX(), ball.getY()) != null){
			object = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt (ball.getX()+BALL_RADIUS*2, ball.getY()) != null) {
			object = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());
		} else if (getElementAt (ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2) != null) {
			object = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2);
		} else if (getElementAt (ball.getX(), ball.getY()+BALL_RADIUS*2) != null) {
			object = getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2);
		}
		return object;
	}

	/* This creates and adds a brick as a filled rectangle with coordinates and color 
	 * as parameters. This method is used in the game setup method when bricks are
	 * added to the canvas.
	 */
	private void makeBrick(double x, double y, Color color) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}

	/* This creates and adds the paddle as a rectangle on the screen.
	 */
	private void createPaddle(double xpos) {
		paddle = new GRect (xpos, getHeight()-(PADDLE_HEIGHT+PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);

	}

	/* This sets the paddle's x coordinate to the mouse's x coordinate. It 
	 * makes sure the paddle does not exit the screen by testing if the paddle's
	 * x coordinate is greater than the screen's width, and reseting it to the 
	 * right end of the screen if it is. 
	 */
	public void mouseMoved(MouseEvent e) {
		paddle.setX(e.getX());
		if (paddle.getX() > getWidth()-PADDLE_WIDTH) {
			paddle.setX(getWidth()-PADDLE_WIDTH);
		}
	}

	/* This creates a label on the center of the screen that displays the message
	 * "YOU WIN!!!" in red. This method is used if all the bricks are hit. 
	 */
	private void victoryMessage() {
		GLabel message = new GLabel("YOU WIN!!!");
		message.setFont("Courier-50");
		message.setColor(Color.RED);
		add (message, getWidth()/2 - message.getWidth()/2, getHeight()/2);
	}

	/* This creates a label that displays the message "You Lose :(" in red on the 
	 * center of the screen. It is used if the player goes through all 3
	 * turns without hitting all the bricks.
	 */
	private void loserMessage() {
		GLabel message = new GLabel("You Lose :(");
		message.setFont("Courier-50");
		message.setColor(Color.RED);
		add (message, getWidth()/2 - message.getWidth()/2, getHeight()/2);
	}

	/* This creates a red label above the ball on the screen that displays any 
	 * message that is input when the method is run. This is used for messages
	 * at the start of the game and after each turn.
	 */

	private void openingMessage(String x) {
		message = new GLabel(x);
		message.setFont("Courier-12");
		message.setColor(Color.RED);
		add (message, getWidth()/2 - message.getWidth()/2, getHeight()/2 - ball.getHeight() - message.getHeight());
	}
}
