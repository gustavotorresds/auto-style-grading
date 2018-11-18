/*
 * File: Breakout.java
 * -------------------
 * Name: Deborah Gordon
 * Section Leader: Semir Shafi
 * 
 * This program implements the game breakout. On each turn, a ball is launched from the center 
 * of the window. The ball bounces off the paddle and the walls of the world. If it hits a brick,
 * the brick is removed and the ball bounces away. If the ball touches the bottom wall, the player
 * has lost the game. 
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

	private double vx, vy;
	private GRect paddle = null;
	private GOval ball = null;
	private int numOfBricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//remember edit starter file comments

		paddle =  makePaddle();
		addMouseListeners();
		setUpGame();

		for (int i = 0; i < NTURNS; i++) { //Allows 3 turns before game ends

			ball = makeBall();

			//initializes vx and vy
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			vy = VELOCITY_Y;

			while (numOfBricksLeft > 0) { //keeps track of the number of bricks left on screen

				if (hitLeftWall(ball) || hitRightWall(ball)) { 
					//hitting the left or right wall changes direction
					vx = -vx;
				} else if (hitTopWall(ball)) { //changes direction if ball hits the top wall
					vy = -vy;
				} else if (hitBottomWall(ball)) { 
					//If ball hits the bottom wall, the player loses, and a "you lost :(" message
					//is displayed. If turns are left, the player is given another turn.
					// If not, the game is over.
					youLost();
					break;
				}
				getCollidingObject(); //checks for bricks or paddle and removes object if it is a brick
				ball.move(vx, vy);
				pause (DELAY);
			} 

			if (numOfBricksLeft == 0) {
				//If the number of bricks left is 0, the player has won the game!
				gameWon();
				break;
			}	
		}
		gameOver(); //If all 3 turns have been used, the game is over.
	}


	private void setUpGame() {
		makeBricks();
		makePaddle();
	}

	private void makeBricks() {
		for (double row = 0; row < NBRICK_ROWS; row++) {
			for (double col = 0; col < NBRICK_COLUMNS; col++) {
				double numOfSpaces = NBRICK_ROWS - 1; //1 less number of spaces than there are number of rows
				double x = col*BRICK_WIDTH + col*BRICK_SEP + (getWidth()/2.0 - ((NBRICK_ROWS* BRICK_WIDTH + numOfSpaces * BRICK_SEP)/2.0));
				double y = row*BRICK_HEIGHT + row*BRICK_SEP + BRICK_Y_OFFSET;
				GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				//changes color based on row number
				if (row == 0 || row == 1) {
					rect.setColor(Color.RED);	
				} else if (row == 2 || row == 3){
					rect.setColor(Color.ORANGE);
				}else if (row == 4 || row == 5) {
					rect.setColor(Color.YELLOW);
				}else if (row == 6 || row == 7) {
					rect.setColor(Color.GREEN);
				}else if (row == 8 || row == 9) {
					rect.setColor(Color.CYAN);
				}
				rect.setFilled(true);
				add(rect);	
			}
		}
	}

	public void mouseMoved (MouseEvent e)  {//Mouse event allows paddle to move when mouse moves.
		double mouseX = e.getX();
		if (mouseX - PADDLE_WIDTH/2 <= 0) { //does not allow paddle to extend past left wall
			paddle.setLocation(0, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}else if (mouseX + PADDLE_WIDTH/2 >= getWidth()) {//does not allow paddle to extend past right wall
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}else {
			//Y value is constant. X value of paddle changes as mouse changes
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}
		add(paddle);
	}

	private GRect makePaddle () { //Makes initial paddle 
		double x = (getWidth()/2 - PADDLE_WIDTH/2);//Places paddle in the center of the screen
		double y = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET); 
		GRect paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		return paddle; //returns paddle to paddle = makePaddle(); function
	}

	private GOval makeBall() { //Makes initial ball
		double x = getWidth()/2 - BALL_RADIUS;//Places ball in the center of screen
		double y = getHeight()/2 - BALL_RADIUS;
		GOval ball = new GOval (x, y, BALL_RADIUS*2, BALL_RADIUS * 2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
		return ball;//returns ball to ball = makeBall() function.
	}

	private boolean hitLeftWall (GOval ball) {
		//The ball has hit the left wall if its X coordinate is less than or equal to 0.
		return ball.getX() <= 0; 
	}
	private boolean hitRightWall (GOval ball) {
		//The ball has hit the right wall if its X coordinate is greater than or equal to
		//the width of the screen minus the ball's width.
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitTopWall (GOval ball) {
		//The ball has hit the top wall if its Y coordinate is less than or equal to 0.
		return ball.getY() <= 0; 
	}
	private boolean hitBottomWall (GOval ball) {
		//The ball has hit the bottom wall if its Y coordinate is greater than or equal to
		//the height of the screen minus the ball's height.
		return ball.getY() >= getHeight() - ball.getHeight();
	}


	private void getCollidingObject() {
		GObject leftCorner = getElementAt (ball.getX(), ball.getY());
		GObject rightCorner = getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY());
		GObject bottomLeftCorner = getElementAt (ball.getX(), ball.getY() + BALL_RADIUS*2);
		GObject bottomRightCorner = getElementAt (ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		if (bottomLeftCorner == paddle || bottomRightCorner == paddle) {
			//If the ball collides with the paddle, it will bounce and make a bounce sound.
			bounceClip.play();
			vy = -vy; 
		//The ball is colliding with a brick, if not with the paddle.
		} else if (leftCorner != null) {
			interactWithBrick(leftCorner);
		} else if (rightCorner != null) {
			interactWithBrick(rightCorner);
		} else if (bottomLeftCorner != null) {
			interactWithBrick(bottomLeftCorner);	
		} else if (bottomRightCorner != null) {
			interactWithBrick (bottomRightCorner);
		}
	}

	private void interactWithBrick(GObject brick) {
		//If the ball collides with a brick, it'll remove the brick, bounce away (while making
		//a sound, and also keep track of the number of bricks remaining on the screen.
		bounceClip.play(); 
		remove(brick);
		vy = -vy;
		numOfBricksLeft -= 1;
	}

	private void youLost() {
		//Displays a 'you lost,' message and also removes the ball when it touches the bottom wall.
		GLabel over = new GLabel ("You lost :("); 
		over.setFont("SansSerif-bold-20");
		double x = getWidth()/2.0 - over.getWidth()/2.0;
		double y = getHeight()/2.0;
		add(over, x, y);
		remove(ball);
		pause(DELAY*120);
		remove(over);

	}

	private void gameWon() {
		//Displays a 'You won,' message when all bricks have been removed
		GLabel won = new GLabel ("You won!");
		won.setFont("SansSerif-bold-34");
		double x = getWidth()/2.0 - won.getWidth()/2.0;
		double y = getHeight()/2.0;
		add(won, x, y);

	}

	private void gameOver() {
		//Displays a 'game over,' message when all 3 turns have been used.
		GLabel over = new GLabel ("Game Over, dude!!");
		over.setFont("SansSerif-bold-40");
		double x = getWidth()/2.0 - over.getWidth()/2.0;
		double y = getHeight()/2.0;
		add(over, x, y);
	}
}


