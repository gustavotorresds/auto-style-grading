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
	
	private RandomGenerator random = RandomGenerator.getInstance();  
	
	//two instance variables of objects
	private GRect paddle = null;
	private GOval ball = null; 
	
	//velocity of the ball in the y direction and x direction
	double ballY = VELOCITY_Y;
	double ballX = random.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	
	//counter to keep track of how many bricks have been removed
	int totalBricksRemoved = 0; 
	
	//sets the limit for how many lives the user has in the game.
	int turnsLeft = NTURNS;
	
	
	public void run() {
		setUpWorld();
		addMouseListeners();
		waitForClick();
		
		//creates an animation loop that lasts as long as the player has lives left.
		while(turnsLeft > 0) {
			ball.move(ballX, ballY);
			
			resetPlayer();
			
			contactDetection();
			
			if(gameWon()) {
				turnsLeft = 0; 
			}
			
			pause(DELAY); 
		}
		endGame();
	}
	
	/* Method endGame
	 * ------------
	 * Pre-Condition: The user has exited the animation loop. 
	 * Post-Condition: A line telling the user if they won or lost will be printed to the console.
	 */
	
	private void endGame() {
		if(gameWon()) {
			println("You Win!");
		} else { 
			println("Game Over!");
		}
	}
	
	/* Method gameWon 
	 * --------------
	 * Pre-Condition: The player is within the animation loop playing the game
	 * Post-Condition: If the player has broken all the bricks, true will be returned. Otherwise false. 
	 */
	
	private boolean gameWon() {
		//sets the amount of bricks that will be created in the world. 
		int totalBricks = NBRICK_COLUMNS * NBRICK_ROWS;
		
		if(totalBricksRemoved == totalBricks) {
			return true;
		}
		return false;
	}
	
	/* Method resetPlayer
	 * ------------------
	 * Pre-Condition: The player is within the animation loop playing the game.
	 * Post-Condition: If the player pisses the ball with the paddle. They lose a life.
	 */
	
	private void resetPlayer() {
		if(ball.getY() > getHeight() && turnsLeft >= 1) {
			turnsLeft--;
			remove(ball);
			if(turnsLeft > 1) {
				println("You have lost a life");
				setUpBall();
				ballY = VELOCITY_Y; 
				waitForClick(); 
			}else {
				println("Last Life!");
				setUpBall();
				ballY = VELOCITY_Y;
				waitForClick();					
			}
		}
	}
	
	/* Method contact
	 * --------------
	 * Pre-Condition: The ball has made contact with something.
	 * Post-Condition: The ball has changed its velocity in the X or Y-direction.
	 */
	
	private void contactDetection() {
		if(touchingBricks()) {
			totalBricksRemoved++; 
			ballY = -ballY;
		} 
		
		if(touchingWall()) {
			ballX = -ballX; 
		}
		
		GObject touchedPaddle = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + (BALL_RADIUS));
		if(touchedPaddle == paddle) {
			ballY = -(ballY);	
		}
		
		if(touchingTop()) {
			ballY = -ballY;
		}
	}
	
	/* Method touchingTop
	 * ------------------
	 * Pre-Condition: The ball is on the canvas.
	 * Post-Condition: If the ball is touching the top of the screen, this will return true.
	 */
	
	private boolean touchingTop() {
		if(ball.getY() < 0) {
			return true;
		}
		return false;
	}
	
	/*Method touchingBricks
	 * --------------------
	 * Pre-Condition: The ball is somewhere on the canvas. 
	 * Post-Condition: If the ball is touching any of the bricks on any of its sides, it will remove the brick and return true.
	 */
	
	private boolean touchingBricks() {
		GObject touchedBottom = getElementAt(ball.getX(), ball.getBottomY());
		GObject touchedTop = getElementAt(ball.getX() + BALL_RADIUS ,ball.getY());
		GObject touchedRight = getElementAt(ball.getX() + (BALL_RADIUS * 2), ball.getY() + BALL_RADIUS);
		GObject touchedLeft = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS);
		if(touchedTop != paddle && touchedBottom != paddle && touchedRight != paddle && touchedLeft !=paddle) {
			if(touchedBottom!= null) {
				remove(touchedBottom);
				return true;
			}
			if(touchedTop != null) {
				remove(touchedTop);
				return true;
			}
			if(touchedRight !=null) {
				remove(touchedRight);
				return true;
			}
			if(touchedLeft != null) {
				remove(touchedLeft);
				return true;
			}
		
		}
		return false;
	}
	
	/* Method touchingWall
	 * -------------------
	 * Pre-Condition: The ball is somewhere on the canvas.
	 * Post-Condition: If the ball is touching the wall of the canvas in the x-direction, this will return true.
	 */
	private boolean touchingWall() {
		if(ball.getX() < 0 || ball.getX() > getWidth() - (BALL_RADIUS * 2)) {
			return true;
		}
		return false;
	}
	
	/* Method setUpWorld
	 * -----------------
	 * Pre-Condition: The world is a blank canvas. 
	 * Post-Condition: The world has bricks, a paddle, and a ball created and added to the canvas. 
	 */
	private void setUpWorld() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricksInWorld();
		setUpPaddle();
		setUpBall();
	}
	
	/* Method setUpBall
	 * ----------------
	 * Pre-Condition: The world only has bricks and a paddle on it. 
	 * Post-Condition: A GOval is created in the middle of the canvas.
	 */
	
	private void setUpBall() {
		ball = new GOval(getWidth() /2, getHeight() / 2, BALL_RADIUS , BALL_RADIUS );
		ball.setFilled(true);
		ball.setColor(Color.GRAY);
		add(ball);
	}
	
	/* Method setUpPaddle
	 * ------------------
	 * Pre-Condition: The world only has bricks drawn on it.
	 * Post-Condition: A GRect is created at the bottom of the canvas. 
	 */

	private void setUpPaddle() {
		paddle = new GRect(getWidth()/2, getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	/* Method mouseMoved
	 * ---------------------
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 * Pre-Condition: A paddle has been created and added to the screen.
	 * Post-Condition: The paddle responds to the location of the user's mouse and moves there, within the bounds of the screen. 
	 */
	
	public void mouseMoved(MouseEvent e){
		double mouseX = e.getX();
		if(mouseX < getWidth() && mouseX > 0) {
			paddle.setLocation(mouseX - (PADDLE_WIDTH /2), getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT));
		}
	}
	
	/* Method setUpBricksInWorld 
	 * --------------------------
	 * Pre-Condition: The world is a blank canvas and no other methods have been called or lines of code executed.
	 * Post-Condition: A 10x10 stack of colored bricks is placed a set distance from the top of the Canvas and centered in the x-direction. 
	 */
	
	private void setUpBricksInWorld() {
		//a counter to record what color the bricks should be. int col could have been used, but for clarity's sake this was made
		int count = 0;
		//creates the starter bricks in the world through a loop. 
		for(int col = 0; col < NBRICK_COLUMNS; col++) {
			count++;
			for(int row = 0; row < NBRICK_ROWS; row++) {
				double halfBricks = NBRICK_COLUMNS /2; 
				double startingPointX = (getWidth()/2) - ((halfBricks * BRICK_SEP) + (halfBricks * BRICK_WIDTH));
				double x = startingPointX + (row * BRICK_WIDTH) + (row * BRICK_SEP);
				double y = BRICK_Y_OFFSET + (col * BRICK_HEIGHT) + (col * BRICK_SEP);
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				//need to be able to be fixed even if they aren't ten
				if(count % 5 == 0) {
					brick.setColor(Color.RED);
					add(brick);
				} else if (count % 5 ==1){
					brick.setColor(Color.ORANGE);
					add(brick);
				} else if(count % 5 == 2) {
					brick.setColor(Color.YELLOW);
					add(brick);
				} else if(count % 5 ==3 ) {
					brick.setColor(Color.GREEN);
					add(brick);
				} else if(count % 5 ==4 ) {
					brick.setColor(Color.CYAN);
					add(brick);
				}
//				if(count ==2 || count == 1) {
//					brick.setColor(Color.RED);
//					add(brick);
//				} else if (count > 2 && count < 5){
//					brick.setColor(Color.ORANGE);
//					add(brick);
//				} else if(count > 4 && count < 7) {
//					brick.setColor(Color.YELLOW);
//					add(brick);
//				} else if(count > 6 && count < 9) {
//					brick.setColor(Color.GREEN);
//					add(brick);
//				} else if(count > 8 && count < 11) {
//					brick.setColor(Color.CYAN);
//					add(brick);
//				}
			}
		}
	}
}
