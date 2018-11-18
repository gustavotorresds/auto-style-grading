/*
 * File: Breakout.java

 * -------------------
 * Name:Caleb Perry
 * Section Leader:Meng Zhang
 * 
 * This file will eventually implement the game of Breakout.
 * where a ball will bounce off a paddle to hit bricks until no
 * bricks are left
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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Ball velocity
	public double vx,vy;

	/*
	 * Creates a new random generator.
	 * Returns a RandomGenerator instance that can be shared among several classes.
	 */
	public RandomGenerator rgen= RandomGenerator.getInstance();

	public void run() {
		setUpGame();
		for(int round =0; round <NTURNS; round++) {
			playGame();
			if(brickCounter ==0) {
				ball.setVisible(false);
				printWinner();
				break;
			}
			if(brickCounter>0){
				remove(ball);
				makeBall();
			}
		}
		if(brickCounter>0) {
			removeAll();
			printGameOver();
		}
	}

	/*
	 * Sets up the game by creating all of the necessary components so 
	 * bricks,the paddle,the ball, etc..
	 */
	private void setUpGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeBricks();
		makePaddle();
		makeBall();
	}

	/*
	 * Creates the layout of multicolored rows of bricks 
	 * for the game 
	 */
	private void makeBricks() {
		//Printing the bricks similar to pyramid
		for(int row= 0; row < NBRICK_COLUMNS; row++) {
			for(int col = 0; col< NBRICK_COLUMNS; col++ ) {
				int cx = getWidth()/2;
				double x = cx - (NBRICK_ROWS*BRICK_WIDTH)/2 -((NBRICK_ROWS-1)*BRICK_SEP)/2 +col*BRICK_WIDTH +col*BRICK_SEP;
				double y = BRICK_Y_OFFSET +row*BRICK_HEIGHT +row*BRICK_SEP;
				GRect brick = new GRect (x,y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(Color.BLACK);
				add(brick);
				//now the color scheme needs to be set
				if (row<2) {
					brick.setFillColor(Color.RED);
				}
				if( row==2 || row ==3) {
					brick.setFillColor(Color.ORANGE);
				}
				if (row==4 || row==5) {
					brick.setFillColor(Color.YELLOW);
				}
				if (row==6 || row==7) {
					brick.setFillColor(Color.GREEN);
				}
				if (row==8 || row== 9) {
					brick.setFillColor(Color.CYAN);
				}
			}
		}
	}

	//create a paddle
	private GRect paddle;

	//centers paddle
	private void makePaddle() {
		int cx = getWidth()/2;
		double x = cx-(PADDLE_WIDTH/2);
		double y = getHeight()-PADDLE_Y_OFFSET;
		paddle = new GRect (x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}

	//allows mouse to track the paddle
	public void mouseMoved(MouseEvent e) {
		if(e.getX() < getWidth() - PADDLE_WIDTH/2 && e.getX() > PADDLE_WIDTH/2) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	//make a ball
	private GOval ball;

	//center that same ball
	private void makeBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 -BALL_RADIUS;
		ball = new GOval(x,y,BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		addMouseListeners();
	}

	// allows for game-play
	private void playGame() {
		waitForClick();
		getBallVelocity();
		while(true) {
			moveBall();
			if(ball.getY() >= getHeight()) {
				break;
			}
			if(brickCounter==0) {
				break;
			}
		}
	}

	//ball's velocity
	private void getBallVelocity() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0,3.0);
		if(rgen.nextBoolean(0.5)) {
			vx=-vx;
		}
	}

	//ball has to move
	private void moveBall() {
		ball.move(vx, vy);
		if((ball.getX() - vx<= 0 && vx< 0) || (ball.getX() +vx >= (getWidth()- BALL_RADIUS*2)&& vx>0)){
			vx=-vx;
		}
		if((ball.getY()-vy<=0 && vy< 0)) {
			vy=-vy;
		}
		//allows the ball to hit the paddle
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			//takes care of ball hitting the side
			if(ball.getY() + BALL_RADIUS*2 > paddle.getY()) {
				vx=-vx;
			}
			if(ball.getY() >= paddle.getY() - BALL_RADIUS*2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET-PADDLE_HEIGHT-BALL_RADIUS*2+4) {
				if(paddle.getX() <= ball.getX() && ball.getX() <= paddle.getX() + PADDLE_WIDTH) {
					vy=-vy;
					vx=-vx;
				}
			}
		}else if (collider!= null) {
			remove(collider);
			brickCounter--;
			vy = -vy;
		}
		pause(DELAY);
	}

	/*When the ball collides with an object this allows it last topmost location to be known and returned to a different location
	that would mirror its path.
	 */
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(), ball.getY()))!=null) {
			return getElementAt(ball.getX(),ball.getY());
		}else if (getElementAt((ball.getX() + BALL_RADIUS*2), ball.getY())!= null) {
			return getElementAt(ball.getX()+ BALL_RADIUS*2, ball.getY());
		}else if( getElementAt(ball.getX(), (ball.getY()+ BALL_RADIUS*2)) !=null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		}else if(getElementAt((ball.getX() +BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		}else {
			return null;
		}
	}


	//makes a game-over screen
	private void printGameOver() {
		GLabel gameOver = new GLabel( "Game Over", getWidth()/2 , getHeight()/2);
		gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
		gameOver.setColor(Color.RED);
		add(gameOver);
	}

	private int brickCounter= 100;

	//makes a winner screen
	private void printWinner() {
		GLabel Winner = new GLabel ("Winner!!", getWidth()/2, getHeight()/2);
		Winner.move(-Winner.getWidth()/2,  -Winner.getHeight());
		Winner.setColor(Color.RED);
		add(Winner);
	}
}









