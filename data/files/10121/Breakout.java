/*
 * File: Breakout.java

 * -------------------
 * Name: Ryan Crowley
 * Section Leader: Esteban Rey
 * 
 * The code here allows one to play the classic game of Breakout where one progressively gets rid of bricks using a paddle and 
 * a ball to win. The current extensions include: click to start, text shown for winning and losing 
 * the game, sounds, a simple points system, and a super ball powerup.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	//constants

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks, note: number of bricks in columns must equal number of bricks in rows
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
	public static final double PADDLE_HEIGHT = 10;
	private double PADDLE_WIDTH = 60;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	private static final double VELOCITY_Y = 1.9;

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = .60;
	public static final double VELOCITY_X_MAX = .70;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 2;

	//number of pixels neccesary to center labels
	public static final double CENTERING_VALUE = 40;

	//number of bricks that one must hit before powerup is available
	public static final int powerUpAvailable = 80;

	//number of bricks for which the powerup is available
	public static final int powerUpGone = 20;


	//Instance Variables below 

	private int powerUpCounter;

	private int nTurns = 3;

	private int points;

	private GRect paddle = null;

	private GOval ball = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double vx;

	private double vy;

	private boolean powerUp = false;

	private int counter = NBRICK_ROWS*NBRICK_COLUMNS;

	//note: The labels are instance variables so that I can create them when I need them, and so that the ball won't treat them
	//as an object to hit

	private GLabel pointsLabel = null;

	private GLabel beginningLabel = null;


	private GLabel endLabel = null;

	private GLabel  powerUpLabel = null;

	/**
	 * method: run
	 * This contains the majority of the game including the setup and the general game play. The only aspects not 
	 * included are the user aspects of the game such as clicking to start and moving the paddle.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addKeyListeners();
		addMouseListeners();
		setUpGame();

		while(nTurns > 0) {
			playGame();
		}
		endGame();
	}

	/**
	 * method: playGame
	 * This contains the animation loop that actually runs the game, including the collisions between the
	 * the ball and the walls, blocks, and paddle.
	 */

	private void playGame() {
		prepGame();
		nTurns = nTurns -1;
		while(ball.getBottomY() - 2*BALL_RADIUS < getHeight() - PADDLE_Y_OFFSET/2 && counter != 0) {
			if(paddle.isFilled() == true) {
				remove(beginningLabel);
				ball.move(vx, vy);
				pause(DELAY);

				//adds powerUpLabel when appropriate
				if(counter < powerUpAvailable && powerUpCounter ==0) {
					add(powerUpLabel, getWidth()/2 - powerUpLabel.getWidth()/2, 2*powerUpLabel.getAscent());
				}

				//points are calculated and displayed on the screen
				points = ((NBRICK_ROWS *NBRICK_COLUMNS)- counter)*(NBRICK_ROWS *NBRICK_COLUMNS);
				pointsLabel.setLabel("Points: " + points);

				ballHitsWall();

				//checks different endpoints to see if the ball has hit the paddle
				collidingPaddle(ball.getX(), ball.getY());
				collidingPaddle(ball.getX() + 2*BALL_RADIUS, ball.getY());
				collidingPaddle(ball.getX(), ball.getY()+ BALL_RADIUS);
				collidingPaddle(ball.getX() + BALL_RADIUS, ball.getY());
				collidingPaddle(ball.getX(), ball.getY()+ 2*BALL_RADIUS);
				collidingPaddle(ball.getX() + 2*BALL_RADIUS, ball.getY()+ 2*BALL_RADIUS);
				collidingPaddle(ball.getX() +2*BALL_RADIUS, ball.getY()+ BALL_RADIUS);
				collidingPaddle(ball.getX() +BALL_RADIUS, ball.getY()+ BALL_RADIUS);

				//checks different endpoints to see if the ball has hit the blocks
				collidingBlock(ball.getX() + BALL_RADIUS, ball.getY());
				collidingBlock(ball.getX()+ BALL_RADIUS, ball.getY()+ 2*BALL_RADIUS);
				collidingBlock(ball.getX(), ball.getY()+ BALL_RADIUS);
				collidingBlock(ball.getX()+ 2*BALL_RADIUS, ball.getY()+ BALL_RADIUS);	
				collidingBlock(ball.getCenterX() + Math.sqrt(2.0) *BALL_RADIUS *(1/2), ball.getCenterY()+ Math.sqrt(2.0) *BALL_RADIUS *(1/2));
				collidingBlock(ball.getCenterX() - Math.sqrt(2.0) *BALL_RADIUS *(1/2), ball.getCenterY()+ Math.sqrt(2.0) *BALL_RADIUS *(1/2));
				collidingBlock(ball.getCenterX() + Math.sqrt(2.0) *BALL_RADIUS *(1/2), ball.getCenterY() - Math.sqrt(2.0) *BALL_RADIUS *(1/2));
				collidingBlock(ball.getCenterX() - Math.sqrt(2.0) *BALL_RADIUS *(1/2), ball.getCenterY() - Math.sqrt(2.0) *BALL_RADIUS *(1/2));
			}
		}
		remove(ball);
		paddle.setFilled(false);
	}

	/**
	 * method: prepGame
	 * This method prepares the beginning label and ball for each run through of the game.
	 */
	private void prepGame() {
		beginningLabel = new GLabel("Click to Play!    " + "Balls Left:" + nTurns);
		beginningLabel.setFont("Courier-15");
		add(beginningLabel, getWidth()/2 - beginningLabel.getWidth()/2, getHeight()/2 - 2*beginningLabel.getAscent());

		ball = new GOval(getWidth()/2 -BALL_RADIUS, getHeight()/2 - BALL_RADIUS,BALL_RADIUS *2, BALL_RADIUS*2);
		ball.setColor(Color.black);
		ball.setFilled(true);
		add(ball);

		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/**
	 * method: ballHitsWall() 
	 * checks to see if the ball has hit the wall and then adjusts the velocity by either negating y or
	 * x to make a realistic bounce off the wall.
	 */
	private void ballHitsWall() {
		//checks if ball has hit the top side
		if(ball.getY() <0) {
			vy= -vy;
		}

		//checks if ball has hit the left side
		else if(ball.getX() <0) {
			vx= -vx;
		}

		//checks if ball has hit the right side
		else if(ball.getX() > getWidth() - BALL_RADIUS*2) {
			vx= -vx;
		}
	}

	/**
	 * method: setUpGame
	 * This method sets up the basic pieces that are necessary to get the game going including paddle and labels.
	 */
	private void setUpGame() {
		blocks();
		 points = 0;
		powerUpCounter = 0;
		
		paddle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.black);
		paddle.setFilled(false);
		add(paddle);

		pointsLabel = new GLabel("Points: " + points);
		pointsLabel.setFont("Courier-12");
		add(pointsLabel, getWidth()/2 - pointsLabel.getWidth()/2, getHeight() - PADDLE_Y_OFFSET/2);

		powerUpLabel = new GLabel("Press Spacebar for Powerup!");
		powerUpLabel.setFont("Courier-15");

		//ensures that ball goes left half the time and right half the time to start the game
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/**
	 * method: blocks
	 * This method constructs the blocks necessary to play the game, creating boxes with the specified 
	 * number of rows and columns. It then sets the color of the blocks based on their position. 
	 */
	private void blocks() {
		for(int row=0; row < NBRICK_ROWS; row++) {
			for(int col=0; col < NBRICK_COLUMNS; col++) {
				double x = (getWidth() - NBRICK_ROWS*BRICK_WIDTH -NBRICK_ROWS*BRICK_SEP)/2 + col*(BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP;
				GRect rect = new GRect(x, y, BRICK_WIDTH , BRICK_HEIGHT);
				rect.setFilled(true);
				if(row<2) {
					rect.setColor(Color.red);
				}
				else if(row<4){
					rect.setColor(Color.orange);
				}
				else if(row<6){
					rect.setColor(Color.yellow);
				}
				else if(row<8){
					rect.setColor(Color.green);
				}
				else if(row<10){
					rect.setColor(Color.cyan);
				}
				add(rect);
			}
		}
	}

	/**
	 * method: collidingPaddle
	 * @param x, this is the x coordinate of the ball that is tested.  I chose three different x values
	 * to input as parameters.
	 * @param y, this is the y coordinate of the ball that is tested.  I chose three different y values
	 * to input as parameters
	 * This method tests to see if there is the paddle is touching the ball at the moment and if it is,
	 * it reverses the y velocity of the ball. A sound is also played when the ball collides with 
	 * the paddle.
	 */
	private void collidingPaddle(double x, double y) {
		GObject collider = getElementAt(x, y);
		if(collider!=null && collider == paddle && collider!=ball) {
			if(ball.getY() < getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET- BALL_RADIUS - vy) {
				vy=-VELOCITY_Y;
			}
			else {
				vy = 3*VELOCITY_Y;
			}
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			bounceClip.play();
		}
	}

	/**
	 * method: collidingBlock
	 * @param x, this is the x coordinate of the ball that is tested.  I chose three different x values
	 * to input as parameters.
	 * @param y, this is the y coordinate of the ball that is tested.  I chose three different y values
	 * to input as parameters
	 * This method tests to see if there is a block touching the ball at the moment and if there is,
	 * it reverses the y velocity of the ball and removes the block. It also subtracts one from the 
	 * counter that is used for points and determining if the game is won. A sound is also played
	 * when the ball collides with the block.
	 */
	private void collidingBlock(double x, double y) {

		GObject collider = getElementAt(x, y);
		if(collider !=null && collider != paddle && collider !=ball && collider !=pointsLabel && collider != endLabel && collider !=powerUpLabel) {
			remove(collider);
			if(powerUp == false) {
				vy = -vy;
			}

			else if(powerUpCounter <20) {
				powerUpCounter +=1;
				remove(powerUpLabel);
			}
			else if (powerUpCounter == powerUpGone){
				powerUp = false;
				ball.setColor(Color.black);
			}
			counter = counter -1;
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			bounceClip.play();
		}
	}

	/**
	 * method: mouseMoved
	 * This method moves the position of the paddle when the user moves the mouse. This allows the user to 
	 * effectively utilize the paddle. It moves the x, but keeps the y value fixed.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		if(e.getX()>PADDLE_WIDTH/2 && x< getWidth() - PADDLE_WIDTH) { 
			add(paddle, x, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}
	}



	/**
	 * method: mouseClicked
	 * This sets the paddle to be filled when the mouse is clicked.  We learned in class that it is possible 
	 * to use WaitForClick instead, but I figured I could use the problem solving/mouseEvent practice. When
	 * the user clicks, this method satisfies the condition in the run method by changing the paddle to 
	 * be filled.
	 */
	public void mouseClicked(MouseEvent f) {
		//Note: I made this stylistic choice to have filling the paddle be the starting point of the game
		//as it gives the user a definite way to check whether the game is being played
		paddle.setFilled(true);
	}

	/**
	 * method: keyPressed
	 * This method runs the action of the powerUp, which is a super ball that goes right through blocks.  It also sets the ball red.
	 */
	public void keyPressed(KeyEvent g) {
		if(g.getKeyCode() == KeyEvent.VK_SPACE && counter < powerUpAvailable) {

			powerUp = true;
			powerUpCounter = 0;
			ball.setColor(Color.red);
		}
	}

	/**
	 * method: endGame
	 * This method tests the value of the counter. If the counter is zero, all of the blocks have been hit
	 * and a label saying congratulations is shown. If the counter isn't zero, all of the blocks have
	 * not been hit, and a label saying better luck next time is shown.
	 */
	private void endGame() {
		if(counter == 0) {
			removeAll();
			endLabel = new GLabel("Congratulations, you won!");
			add(endLabel, getWidth()/2 - endLabel.getWidth()/2, getHeight()/2 - endLabel.getAscent());
			return;
		}
		else{
			removeAll();
			endLabel = new GLabel("Better Luck Next Time! Your score was " + points);
			add(endLabel, getWidth()/2 - endLabel.getWidth()/2, getHeight()/2 - endLabel.getAscent());
			return;
		}
	}
}
	
