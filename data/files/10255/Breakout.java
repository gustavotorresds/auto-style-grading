/*
 * File: Breakout.java
 * -------------------
 * Name:Will Sweeney
 * Section Leader: Thariq Ridha
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for the mouse-controlled paddle at the bottom of the screen
	private GRect paddle = null;

	//Instance variable for the bouncing ball
	private GOval ball = null;

	//Instance variables for the horizontal and vertical velocities of the ball
	private double vx, vy;

	//Instance variable that generates a random number. Used for calculating a random y-velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//The total number of bricks at the start of a game
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;

	//Audioclip of a bouncing ball sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	
	//This method sets up and runs the Breakout game.
	//
	//Pre: The screen is blank.
	//Post: The user has either won or lost the game, and a message describing which is on the screen.
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBreakOutGame();
		addMouseListeners();
		playBreakOutGame();
	}

	
	//This method allows for the paddle to be controlled by the user with the mouse.
	//It keeps the paddle from moving off the screen.
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX()-PADDLE_WIDTH/2;
		paddle.setLocation(mouseX,getHeight()-PADDLE_Y_OFFSET);
		if (paddle.getX() > getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET);
		}
		else if (paddle.getX() < 0) {
			paddle.setLocation (0, getHeight()-PADDLE_Y_OFFSET);
		}
	}

	
	//This sets up the game by building the block of bricks and creating the paddle
	private void setUpBreakOutGame() {
		createBlockOfBricks();
		createPaddle();
	}
	
	
	//This creates the paddle as a new GRect and adds it to the screen
	private void createPaddle() {
		paddle = new GRect ((getWidth()/2)-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
	}
	
	
	//This creates the block of different colored bricks
	private void createBlockOfBricks() {
		for (int r = 0; r < NBRICK_ROWS; r++) {
			double y = BRICK_Y_OFFSET + ((BRICK_HEIGHT+BRICK_SEP)*r);
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (c * (BRICK_WIDTH+BRICK_SEP)); 
				double xoffset = (getWidth()-((BRICK_WIDTH*NBRICK_COLUMNS)+(BRICK_SEP*(NBRICK_COLUMNS-1))));
				GRect brick = new GRect (x+xoffset/2, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(Color.RED);
				//This makes the second two layers of bricks orange
				if (r == 2 || r==3) {
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
				//This makes these two layers of bricks yellow
				}else if (r == 4 || r == 5) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				//This makes these two rows of bricks green
				}else if (r == 6 || r == 7) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				//This makes the bottom two layers of bricks cyan
				}else if (r == 8 || r == 9) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	
	
	//This allows the user to have three turns. A turn is used up when the ball 
	//passes below the bottom of the screen.
	private void playBreakOutGame() {
		for (int i = 0; i < NTURNS; i++) {
			waitForClick();
			createBall();
			moveBall();
			remove(ball);
		}
		loseLabel();	
	}

	
	//This creates a GOval with the given size constants
	private void createBall() {
		double ballSize =  2 * BALL_RADIUS;
		ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, ballSize, ballSize);
		ball.setFilled(true);
		add(ball);
	}

	
	//This gives the ball its movement characteristics, allowing it to bounce off walls, bricks, and the
	//paddle. This also removes bricks once the ball has collided with them and keeps track of 
	//the number of bricks still left in the game.
	private void moveBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;	
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			//This is how the ball bounces off the left and right walls
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			//This is how the ball bounces off the top wall
			}else if(hitTopWall(ball)) {
				vy = -vy;
			//This stops the ball and exits this round of the game if the ball touches the bottom wall
			}else if (hitBottomWall(ball)) {
				vx = 0;
				vy = 0;
				break;
			}
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider != paddle) {
					vy = -vy;
					remove (collider);
					brickCounter--;
					if (brickCounter==0) {
						winLabel();
						break;	
					}
				} else {
					vy = -Math.abs(vy);
					bounceClip.play();
				}
			}
		}		
	}


	//This checks the four corners of the square surrounding the ball to see if the ball has collided
	//with a brick, wall, or paddle.
	private GObject getCollidingObject() {	
		//This checks the top left corner of ball
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		//This checks the top right corner of the ball
		collider = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		if (collider != null) {
			return collider;
		}
		//This checks the bottom left corner of the ball
		collider = getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		if (collider != null) {
			return collider;
		}
		//This checks the bottom right corner of the ball
		collider = getElementAt(ball.getX() + (2 * BALL_RADIUS) , ball.getY() + (2 * BALL_RADIUS));
		if (collider != null) {
			return collider;
		}
		return collider;
	}

	
	//These booleans return x and y locations when the ball has reached a wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	
	//This adds a label that is displayed when the user has completed the game by removing every brick
	private void winLabel() {
		GLabel winLabel = new GLabel("Congratulations, you win!");
		add (winLabel, getWidth()/2 - (winLabel.getWidth()/2), getHeight()/2 - (winLabel.getHeight()/2));
	}
	
	
	//this adds a label that is displayed when the user's three turns have been used up
	private void loseLabel() {
		GLabel winLabel = new GLabel("Bummer, you lose!");
		add (winLabel,getWidth()/2 - (winLabel.getWidth()/2), getHeight()/2 - (winLabel.getHeight()/2));
	}
}













