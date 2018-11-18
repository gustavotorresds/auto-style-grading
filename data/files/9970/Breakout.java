/*
 * File: Breakout.java
 * -------------------
 * Name: Aris Konstantinidis
 * Section Leader: Brahm Capoor
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

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		int turns = NTURNS;
		while (turns>0) {
			setupGame(); // sets up the game
			playGame(); // plays the game

			if(brickTracker == 0) { //prints result depending on win or loss
				printYouWon();
				break;
			} else if(brickTracker>0){
				removeAll();
			}

			turns--;
		}
		if(turns==0) {	
			printYouLost();
		}

	}

	//sets up the game
	private void setupGame() {
		setUpBricks(); //sets up the brick structure
		createPaddle(); // creates the paddle
		createBall(); // creates the ball
	}

	// prints result in case of win
	private void printYouWon() {
		GRect result = new GRect(0,0,getWidth(), getHeight());
		result.setColor(Color.GREEN);
		result.setFilled(true);
		add(result);
		GLabel label = new GLabel("YOU WON!");
		label.setFont(new Font("Serif", Font.BOLD, 24));
		add(label,0,getHeight()/2+label.getAscent()/2);
	}

	//prints result in case of loss
	private void printYouLost() {
		GRect result = new GRect(0,0,getWidth(), getHeight());
		result.setColor(Color.RED);
		result.setFilled(true);
		add(result);
		GLabel label = new GLabel("YOU LOST HAHAHAHAHA!");
        label.setColor(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 24));
		add(label,0,getHeight()/2+label.getAscent()/2);
		
	}

	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickTracker=NBRICK_COLUMNS*NBRICK_ROWS; //creates brick counter
	
	//plays the game: user clicks and the ball moves. Stops if ball crosses bottom wall, or if no more bricks.
	private void playGame() {
		waitForClick();
		setBallVelocity();
		while(true) {
			moveBall();
			if(ball.getY()>=getHeight()) {
				break;
			}
			if(brickTracker == 0) {
				break;
			}

		}


	}

	//moves the ball within walls. Ball bounces on walls, when it hits the paddle and when it hits a brick.
	//when ball hits a brick, the brick is removed
	private void moveBall() {
			ball.move(vx, vy);
			if((ball.getX()-vx<=0 && vx<0) || (ball.getX()+vx>=getWidth()-BALL_RADIUS*2) && vx>0) {
				vx=-vx;
			}
			if(ball.getY()-vy<=0 && vy<0) {
				vy=-vy;
			}

			GObject collider = getCollidingObject();
			if(collider==paddle) {
				if(ball.getY()+BALL_RADIUS*2 >= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT && ball.getY()+BALL_RADIUS*2 < vy + getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT) {
					vy=-vy;
				}
			} else if(collider!=null) {
				remove(collider);
				brickTracker--;
				vy=-vy;
			}
			pause(DELAY);


	}

	// checks collision with an object (paddle or bricks)
	private GObject getCollidingObject() {

		if(getElementAt(ball.getX(), ball.getY()) !=null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if(getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) !=null) {
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		}
		else if(getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS) !=null) {
			return getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		}
		else if(getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) !=null) {
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		}
		else {
			return null;
		}
	}

	// Sets ball's velocity
	private void setBallVelocity() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	// Creates the ball and places it at its starting position
	private GOval ball;
	private void createBall() {
		double ballX = getWidth()/2-BALL_RADIUS; // X starting position of ball
		double ballY = getHeight()/2-BALL_RADIUS; // Y starting position of ball
		ball = new GOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	// Creates the paddle and places it at its starting position
	private GRect paddle;
	private void createPaddle() {
		double paddleX = (getWidth()-PADDLE_WIDTH)/2; // X starting position of paddle
		double paddleY = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT; // Y starting position of paddle
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

		addMouseListeners(); // Adds mouseListeners

	}

	// Makes paddle (its middle point) move left/right with the mouse within the walls. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); // get the x-coordinate of where the mouse moves to
		if(mouseX > PADDLE_WIDTH/2 && mouseX < getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(mouseX-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}


	}

	// Sets up the brick structure
	private void setUpBricks() {
		double indent = (getWidth()-NBRICK_ROWS*BRICK_WIDTH-(NBRICK_ROWS-1)*BRICK_SEP)/2; // Calculates the indent
		for(int row=0; row<NBRICK_ROWS; row++) {
			for(int i=0; i<NBRICK_COLUMNS; i++) {
				GRect brick = new GRect(i*(BRICK_WIDTH+BRICK_SEP)+indent, BRICK_Y_OFFSET+row*(BRICK_HEIGHT+BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				setBrickColor(row, brick);
				add(brick);
			}

		}
	}

	// Sets the color of the bricks in the row
	private void setBrickColor(int row, GRect brick) {
		if(row<2) {
			brick.setColor(Color.RED);
		}
		if(row==2 || row==3) {
			brick.setColor(Color.ORANGE);
		}
		if(row==4 || row==5) {
			brick.setColor(Color.YELLOW);
		}
		if(row==6 || row==7) {
			brick.setColor(Color.GREEN);
		}
		if(row==8 || row==9) {
			brick.setColor(Color.CYAN);
		}
		brick.setFilled(true);
	}

}
