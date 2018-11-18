/*
 * File: Breakout.java
 * -------------------
 * Name: Hillary Umphrey
 * Section Leader: Luciano Gonzalez
 * 
 * This file will  implement the game of Breakout with extensions.
 * These extensions include: printing the winning or losing message
 * with a fitting font, a bounce noise, a starting screen with brick background, and a "click 
 * to start" action at the beginning and at the start of each new turn.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extended extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle = null;
	private GOval ball = null;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double bricknum;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		startingScreen();
		removeAll();
		setUpBricks();
		setUpPaddle();
		bricknum = NBRICK_ROWS*NBRICK_COLUMNS;
		//this four loop determines the number of chances the player gets
		for (int i = 0; i < NTURNS; i++) {
			setUpBall();
			waitForClick(); //the ball only starts moving when the player clicks
			bounceBall();
			remove(ball);
		}
		//prints the loss statement
		if (bricknum != 0) {
			removeAll();
			GLabel loss = new GLabel("YOU LOSE :(");
			Font font = new Font("Impact", Font.BOLD,50);
			loss.setFont(font);
			double w = loss.getWidth();
			double h = loss.getAscent();
			add (loss, getCenterX()-.5*w, getCenterY()-.5*h);
		}
	}

	//makes paddle track the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX < (getWidth()-PADDLE_WIDTH)) {
			paddle.setLocation(mouseX,(getHeight()-PADDLE_Y_OFFSET));
		}
	}

	private void startingScreen() {
		GImage brick = new GImage("Red_Brick_Wall.jpg");
		double brickw = brick.getWidth();
		double brickh = brick.getHeight(); 
		add(brick, getCenterX()-.5*brickw, getCenterY()-.5*brickh);
		
		GLabel welcome = new GLabel("WELCOME TO BREAKOUT");
		Font welcomeFont = new Font("Impact", Font.BOLD,40);
		welcome.setFont(welcomeFont);
		double welcomew = welcome.getWidth();
		double welcomeh = welcome.getAscent();
		add (welcome, getCenterX()-.5*welcomew, getCenterY()-.5*welcomeh);
	
		GLabel click = new GLabel("click to begin");
		Font clickFont = new Font("Impact", Font.BOLD,40);
		click.setFont(clickFont);
		double clickw = click.getWidth();
		double clickh = click.getAscent();
		add (click, getCenterX()-.5*clickw, getCenterY()-.5*clickh+welcomeh);
		
		waitForClick();
		removeAll();
	}
	//the method sets up the bricks according to the constants.  If there are more than ten
	//rows of bricks then the rainbow order will begin again
	private void setUpBricks() {
		double x = getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH+(NBRICK_COLUMNS - 2)*BRICK_SEP);
		int numy = 0;
		int n = 0;
		int colorNum = 1;
		while (true) {
			if (n == NBRICK_ROWS) {
				break;
			}
			int numx = 0;
			for (int s = 0; s < NBRICK_COLUMNS; s++) {
				GRect rect = new GRect(x/2+numx*(BRICK_WIDTH+BRICK_SEP),BRICK_Y_OFFSET+numy*(BRICK_HEIGHT+BRICK_SEP),BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);
				if (colorNum == 1 || colorNum == 2) {
					rect.setColor(Color.RED);
				}
				if (colorNum == 3 || colorNum == 4) {
					rect.setColor(Color.ORANGE);
				}
				if (colorNum == 5 || colorNum == 6) {
					rect.setColor(Color.YELLOW);
				}
				if (colorNum == 7 || colorNum == 8) {
					rect.setColor(Color.GREEN);
				}
				if (colorNum == 9 || colorNum == 10) {
					rect.setColor(Color.CYAN);
				}
				add (rect);
				numx = numx+1;
			}
			n = n+1;
			colorNum = colorNum +1;
			if (colorNum > 10) {
				colorNum = 1; 
			}
			numy = numy+1;
		}
	}
	//this method adds a rectangular paddle to the center of the screen
	private void setUpPaddle() {
		paddle = new GRect (PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, getCenterX()-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET);
	}
	//this method adds a filled circle to the center of the screen
	private void setUpBall() {
		ball = new GOval (2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball,getCenterX()-BALL_RADIUS/2,getCenterY()-BALL_RADIUS/2);
	}
	//this method allows the user to play the game as it adds animation to 
	//the ball and adds information for collisions
	private void bounceBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (true) {
			//makes the ball move
			ball.move(vx, vy);
			pause(DELAY);
			double y = ball.getY();
			double x = ball.getX();
			//checks for left wall
			if (bricknum == 0) {
				break;
			}
			if (x <= 0) {
				vx = -vx;
			}
			//checks for right wall
			if (x >= getWidth()-2*BALL_RADIUS) {
				vx = -vx;
			}
			//checks for bottom
			if (y >= getHeight()) {
				remove (ball);
				break;
			}
			//check for top
			if (y <= 0) {
				vy = -vy;
			}
			//deals with collisions
			GObject collider = getCollidingObject(x,y);
			if (collider == null) {
			} 
			//makes ball bounce off paddle with sound
			else if (collider == paddle) {
				bounceClip.play();
				vy = -Math.abs(vy);
			} 
			//makes brick disappear with sound
			else {
				bricknum = bricknum-1;
				//prints winning statement if all bricks are gone
				if (bricknum == 0) {
					removeAll();
					GLabel win = new GLabel("YOU WIN :)");
					Font font = new Font("Impact", Font.BOLD,50);
					win.setFont(font);
					double w = win.getWidth();
					double h = win.getAscent();
					add (win, getCenterX()-.5*w, getCenterY()-.5*h);
				}
				bounceClip.play();
				remove (collider);
				vy = -vy;
			}
		}
	}
	//this method checks to see if any of the four corners of the ball have touched
	//an object
	private GObject getCollidingObject(double x, double y) {
		GObject collision1 = getElementAt(x, y);
		if (collision1 != null) {
			return (collision1);
		}
		GObject collision2 = getElementAt(x+2*BALL_RADIUS, y);
		if (collision2 != null) {
			return (collision2);
		}
		GObject collision3 = getElementAt(x+2*BALL_RADIUS, y+2*BALL_RADIUS);
		if (collision3 != null) {
			return (collision3);
		}
		GObject collision4 = getElementAt(x, y+2*BALL_RADIUS);
		if (collision4 != null) {
			return (collision4);
		}
		return (null);
	}
}
