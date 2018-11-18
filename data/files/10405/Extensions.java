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

public class Extensions extends GraphicsProgram {

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

	// loads the audio 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	private GRect paddle;
	private GOval ball; 
	private double vx, vy = 0;  
	private double bricksleft = NBRICK_ROWS * NBRICK_COLUMNS;
	// variable to keep track of score
	private double score = 0;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/**Ellie Chen. Section Leader: Kait Lagattuta. 
	 * This program allows the user to play the Breakout game!
	 * User is given three lives to keep the ball bouncing off 
	 * the paddle and breaking bricks. Breakout is won when no bricks are left
	 * and lost when the user has let the ball hit the ground 3 times. */

	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		// waits for user prompting to start game
		startGame();
		buildBricks();
		createPaddle();
		createBall();
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!


		// continues playing game for up to three lives
		// after using three lives, they've ever won or lost
		for (int i = 0; i< NTURNS ; i++) {
			remove(ball);
			createBall();
			playGame();
			if(bricksleft == 0) {
				break;
			}
		}
		// winning condition
		if (bricksleft == 0) {
			gameWon();	
			// losing condition 
		} else { 
			gameLost();
		}
	}
	// welcome message + user click to begin the game 
	private void startGame() {
		GLabel welcome = new GLabel ("Welcome! Click the screen when you're "
				+ "ready to start playing! Good luck!");
		double x = (getWidth()/2-welcome.getWidth()/2);
		double y = (getHeight()/2-welcome.getHeight()/2);
		add (welcome, x, y); 
		waitForClick();
		remove (welcome);
	}

	// creates centered paddle
	private void createPaddle () {
		paddle = new GRect (getWidth()/2-(PADDLE_WIDTH/2),getHeight()-PADDLE_Y_OFFSET,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	// creates centered ball 
	private void createBall () {
		ball = new GOval (0, 0, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, (getWidth()/2)-BALL_RADIUS, (getHeight()/2)-BALL_RADIUS);
	}


	// builds the bricks 
	private void buildBricks() {
		for (int ROW=0; ROW<NBRICK_ROWS; ROW++) {
			for (int COLUMN = 0; COLUMN<NBRICK_COLUMNS; COLUMN++) {
				// center x cord., subtract width*number of bricks. 1 fewer space than brick number 
				// divide remaining space by 2 to make space equal
				double x = (getWidth()-(BRICK_WIDTH*NBRICK_ROWS)-(BRICK_SEP)*(NBRICK_ROWS-1))/2;
				double y = BRICK_Y_OFFSET; 
				// builds correct number of bricks
				GRect brick = new GRect (x+(ROW*(BRICK_SEP+BRICK_WIDTH)), y+(COLUMN*(BRICK_SEP+BRICK_HEIGHT)), BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);

				// bricks in multiples of 10, ex: 11%10=1, 21%10=1 
				// brick 11 and 21 will be same color, so group by column %10
				int remainder = COLUMN%10; 
				if (remainder == 0 || remainder == 1) {
					brick.setColor(Color.RED);
				} else if (remainder == 2 || remainder == 3) {
					brick.setColor(Color.ORANGE);
				} else if (remainder == 4 || remainder == 5) {
					brick.setColor(Color.YELLOW);
				} else if (remainder == 6 || remainder == 7) {
					brick.setColor(Color.GREEN);
				} else if (remainder == 8 || remainder == 9) {
					brick.setColor(Color.CYAN);
				}

			}
		}
	}
	// method for playing game
	private void playGame() {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) { 
			vx = -1*(vx);
		}

		while (true) {
			ball.move(vx,vy); 
			pause(DELAY);
			// if ball hits the top wall 
			if (ball.getY() <= 0) {
				vy = -vy;
				// if ball hits bottom wall, lose one life
			} else if (ball.getY() >= getHeight()-2*BALL_RADIUS) { 
				remove(ball);
				break;
				// if ball hits the right or left wall 
			} else if (ball.getX()+2*BALL_RADIUS > getWidth() ||(ball.getX() <= 0+2*BALL_RADIUS ) ) {
				vx = -vx;
			}		

			GObject collider = checkFourCorners();
			if (collider != null ){
				// if ball hits paddle
				if (collider == paddle) {
					vy = -1*(Math.abs(vy)); 
					// plays sound
					bounceClip.play();
				} else { //if not null or paddle, ball hits brick
					// deletes brick and plays sound
					remove(collider);
					vy = -vy; 
					bricksleft = bricksleft-1;
					// each brick is worth one point
					score = score +1; 
					println ("score:"+score); 
				}
				// plays sound 
				bounceClip.play();

				if (bricksleft == 0) {
					// winning condition, returns null
					// breaks out of method, displays win msg
					return;
				}
			}

		}
	}
	/** checks each corner of square to see if paddle or ball is present
	 * if it is, it returns the paddle or ball to the collider= checkFourCorners method
	 * if not, it returns nothing and the and the ball continues moving **/
	private GObject checkFourCorners() {
		GObject collider = getElementAt(ball.getX(),ball.getY());
		if (collider != null) {
			return(collider);
		}
		collider = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY());
		if (collider != null) {
			return(collider);
		}
		collider = getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS);
		if (collider != null) {
			return(collider);
		}
		collider = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS);
		if (collider != null) {
			return(collider);
		}
		return(null);
	}
	// moves the Paddle
	public void mouseMoved (MouseEvent e) { 
		double paddleX = e.getX()-PADDLE_WIDTH/2;
		if (paddleX<=0) {
			paddleX= 0;
		} else if (paddleX>=getWidth()-PADDLE_WIDTH) {
			paddleX = getWidth()-PADDLE_WIDTH;
		}
		paddle.setX(paddleX);
	}
	// message displayed to congratulate user 
	private void gameWon() {
		GLabel congrats  = new GLabel ("YOU WON!!!!! CONGRATULATIONS :)");
		// centers congratulations label
		double x = (getWidth()/2-congrats.getWidth()/2);
		double y = getHeight()/2-congrats.getHeight()/2; 
		add (congrats, x, y);	
	}
	// message displayed when user loses
	private void gameLost () {
		GLabel gameover = new GLabel ("You died. Game over! Better luck next time!");
		// centers losing label
		double x = (getWidth()/2-gameover.getWidth()/2);
		double y = (getHeight()/2-gameover.getHeight()/2);
		add (gameover, x, y); 
	}
}








