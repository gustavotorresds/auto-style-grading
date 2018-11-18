/*
 /* File: Breakout.java
 * -------------------
 * Name: Tyler Abramson
 * Section Leader: Jordan
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.prism.paint.Color;

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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public static final int Start = 0;

	public static final double Yloc = CANVAS_HEIGHT - PADDLE_Y_OFFSET;

	private double Xloc = CANVAS_WIDTH/2;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GRect paddle = new GRect(CANVAS_WIDTH/2, CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);

	private GOval ball = new GOval(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, BALL_RADIUS*2, 2*BALL_RADIUS);

	private double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);

	private double vy = VELOCITY_Y;

	private boolean GameOn = true;

	private double BallX = CANVAS_WIDTH/2;

	private double BallY = CANVAS_HEIGHT/2;

	private GLabel startLabel = new GLabel ("CLick the mouse to Start", CANVAS_WIDTH/2 - 70, CANVAS_HEIGHT/2);

	private int lives = NTURNS;

	GLabel Lives = new GLabel ("lives" + lives, CANVAS_WIDTH - 40, 20);

	private int numBricks;
	/*
	 * The main run method is the hub, it basically sets the name of the tab and sets the dimensions of it
	 * I also generate the map in here IE the rows and the mouse listeners. 
	 * 
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		drawRows();
		add(startLabel);
		addMouseListeners();
		startGame();		
	}
	/*
	 * Start game is what gets the ball rolling, litterly and figurativly
	 * direction sets the direction whcih the ball is going to take
	 * Add Ball puts the first ball into the field
	 * wait for click allows me to control when the gsme starts. when the user clicks it starts the game
	 * the final remove ball only is there to get rid of the last ball if the user wins the game it makes it clesner
	 */
	private void startGame() {
		direction();
		ball.setFilled(true);
		add(ball);
		waitForClick();
		remove(startLabel);
		remove(Lives);
		RunGame();
		remove(ball);
	}
	/*
	 * RunGame is what handles the actual ball movement, It allows me to control how the ball works
	 * It is the main method, it calls a bunch of other methods which compute where the ball is going and what it is doing
	 * 
	 */
	private void RunGame() {
		while(GameOn == true) {
			checkPaddle();
			checkBricks();
			checkSides();
			remove(ball);
			makeBallMove();
			pause(DELAY);
		}

	}
	/*
	 * CheckLives is where I see how many lives the user has left if you are out of lives it prints the lose screen and if you 
	 * are not it resets the game so you can continue playing
	 * it tells you how many bricks you had left if you lost 
	 */
	private void checkLives() {
		if(lives == 0) {
			GameOn = false;
			GLabel lose = new GLabel("YOU LOSE :(", CANVAS_WIDTH/2 - 50, CANVAS_HEIGHT/2);
			lives = 0;
			GLabel bricksbroke = new GLabel("You Broke all but " + numBricks + " Bricks", CANVAS_WIDTH/2 - 100, CANVAS_HEIGHT/2 + 20);
			lives = 0;
			Lives = new GLabel ("Lives "+ lives, (CANVAS_WIDTH - Lives.getWidth())/2, (CANVAS_HEIGHT - 2*Lives.getHeight())/2);
			add(Lives);
			add(lose);
			add(bricksbroke);
		}
		else {
			GameOn = true; 
			BallX = CANVAS_WIDTH/2;
			BallY = CANVAS_HEIGHT/2;
			ball = new GOval (BallX, BallY, BALL_RADIUS*2, BALL_RADIUS*2);
			ball.setFilled(true);
			add(startLabel);
			Lives = new GLabel ("Lives "+ lives, (CANVAS_WIDTH - Lives.getWidth())/2, (CANVAS_HEIGHT - 2*Lives.getHeight())/2);
			add(Lives);
			startGame();
		}

	}
	/*
	 * check bricks is the method which determines if the ball has collided with a brick
	 * I make sure the paddel is excluded by having an if statement that makes sure the ball is in the upper 2/3 of the map
	 * check win is called to see if the player has destroyed all of the bricks
	 */
	private void checkBricks() {
		if(BallY < (CANVAS_HEIGHT*2)/3 && getElementAt(BallX, BallY) != Lives) {
			if(getElementAt(BallX, BallY) != null) {
				remove(getElementAt(BallX, BallY));
				numBricks--;
				vy = -vy;
			}
			else if(getElementAt(BallX + BALL_RADIUS*2, BallY) != null) {
				remove(getElementAt(BallX + BALL_RADIUS*2, BallY));
				numBricks--;
				vy = -vy;
			}
			else if(getElementAt(BallX, BallY + BALL_RADIUS*2) != null) {
				remove(getElementAt(BallX, BallY + BALL_RADIUS*2));
				numBricks--;
				vy = -vy;
			}
			else if(getElementAt(BallX + BALL_RADIUS*2, BallY + BALL_RADIUS*2) != null) {
				remove(getElementAt(BallX + BALL_RADIUS*2, BallY + BALL_RADIUS*2));
				numBricks--;
				vy = -vy;
			}
		}


		checkWin();
	}
	/*
	 * chdeckWin is designed to see if the number of bricks remainins is equal rto zero, it is it is ends the game and prints the win statement
	 */
	private void checkWin() {
		if(numBricks == 0) {
			GameOn = false;
			GLabel win = new GLabel("YOU WIN", CANVAS_WIDTH/2 - 10, CANVAS_HEIGHT/2);
			add(win);
		}

	}
	/*
	 * checkPaddel is there to see if the ball hits the oaddel, if it does it multiplies the absilute value of vx by -1. this gets rid
	 * of the stick paddel problem
	 */
	private void checkPaddle() {
		if(BallY > Yloc - BALL_RADIUS*2 && BallX < Xloc + PADDLE_WIDTH && BallX + BALL_RADIUS*2 > Xloc) {
			vy = - Math.abs(vy);
		}
	}
	/*
	 * direction helps to find the direction of the ball. it uses a random number generator to descide if the ball is going to start off goin
	 * left or right, than it uses another random number generator to descide the starting velocity.
	 */
	private void direction() {
		int OorE = rgen.nextInt(1, 2);
		if(OorE == 1) {
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		}
		if(OorE == 2) {
			vx = rgen.nextDouble(-VELOCITY_X_MIN, -VELOCITY_X_MAX);
		}
	}
	/*
	 * check sides has the goal of seeing if the ball hits the sides or the top of the canvas, if it does it reverses either the 
	 * vx or vy depending on the axis it hits. 
	 */
	private void checkSides() {
		if(BallX < 0  || BallX > CANVAS_WIDTH - BALL_RADIUS*2) {
			vx = -vx;
		}
		if(BallY < 0) {
			vy = -vy;
		}
		if(BallY == CANVAS_HEIGHT) {
			lives --;
			GameOn = false;
			remove(ball);
			checkLives();
		}
	}
	/*
	 * make ball move is written to make the ball move, 
	 * it sets the Ballx and Bally to wherever the ball is plus the Vx and Vy it is traveling at
	 */
	private void makeBallMove() {
		BallX = ball.getX() + vx;
		BallY = ball.getY() + vy;
		ball = new GOval (BallX, BallY, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	/*
	 * Mouse moved sees where the mouse is moving and move the paddel accordingly, it does this by removing the old position of the 
	 * paddel and drawing a new paddel at the new mouse position so the mouse is in the middle of the paddel
	 * 
	 */
	public void mouseMoved(MouseEvent e) {
		remove(paddle);
		Xloc = e.getX() - PADDLE_WIDTH/2;
		if(Xloc < 0) {
			Xloc =  0;
		}
		if(Xloc >= CANVAS_WIDTH - PADDLE_WIDTH) {
			Xloc = CANVAS_WIDTH - PADDLE_WIDTH;
		}
		paddle = new GRect (Xloc,Yloc, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	/*
	 * this is taken straight from my pyramid code and slightly modified to work for this.
	 * IT uses a for loop to descide how many rows and than calls a draw aolumnm function
	 */
	private void drawRows() {
		for(int i = 0; i < NBRICK_ROWS; i++) { //width
			drawRowBrick(i);
		}
	}
	/*
	 * This draws the columns of bricks by using a for loop and setting coordinates Xcord and Y cord equal to the placement of the next brick
	 */
	private void drawRowBrick(int i) {
		/*
		 * This draws the individual bricks 
		 * it recieves the slant to tell it where to start and the int i and j to locate where to draw the bricks
		 */
		double brickWidth = BRICK_WIDTH + BRICK_SEP;
		for(int j = 0; j < NBRICK_COLUMNS; j ++) {
			double xCord = brickWidth*(j); 
			double yCord = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP)* i;
			drawBrick(xCord, yCord, i);
		}

	}
	/*
	 * drawBrick is passed xcord ycord and the number brick it is,
	 * the goal is to create each brick at x cord and y cord with a width of brick width and a height of brickheight
	 * it calls set brick color to actually set the color of the brick and draw the real brick
	 */
	private void drawBrick(double xCord, double yCord, int i) {
		GRect rect = new GRect(xCord + BRICK_SEP, yCord , BRICK_WIDTH, BRICK_HEIGHT);
		setBrickColor(i, rect);
	}
	/*
	 * set brick color is the main function which finds out what color to draw the brick and than drawd each brick. 
	 * it is passed rect which is the actual dimensions and pkalcement of the brick, sets the color and than draws it on the canvas
	 * it also keeps track of the number of bricks so i can subtract later on for my win function
	 */
	private void setBrickColor(int i, GRect rect) {
		if(i < 2) {
			rect.setFilled(true);
			rect.setColor(java.awt.Color.RED);
		}else if(i < 4) {
			rect.setFilled(true);
			rect.setColor(java.awt.Color.ORANGE);
		}else if(i < 6) {
			rect.setFilled(true);
			rect.setColor(java.awt.Color.YELLOW);
		}else if(i < 8) {
			rect.setFilled(true);
			rect.setColor(java.awt.Color.GREEN);
		}else if(i < 10) {
			rect.setFilled(true);
			rect.setColor(java.awt.Color.CYAN);
		}
		add(rect);
		numBricks++;
	}

}
