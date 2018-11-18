/*
 * File: Breakout.java
 * -------------------
 * Name: Cassie Obel	
 * Section Leader: Drew Bassilakis
 * 
 * This file implements the game of Breakout.  The user must click to initiate
 * the movement of the ball.  The user knocks out individual bricks by hitting the
 * ball with the paddle. The user has three tries to knock out all of the bricks 
 * without letting the ball hit the bottom of the screen.
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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	GRect paddle = new GRect (0, 0, PADDLE_WIDTH, PADDLE_HEIGHT);

	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	boolean waitingForPlayer = true;

	//The main run method decomposes Breakout into three parts: 
	//setting up the game, playing the game, and announcing the
	//player's result.
	public void run() {
		setUpGame();
		boolean result = playGame();
		announceResult(result);
	}

//This method constructs and adds the messages telling
//whether the player is a winner or a loser.
	private void announceResult(boolean gameWon) {
		
		GLabel winner = new GLabel ("Congratulations!"); 
		winner.setLocation(CANVAS_WIDTH/2 - winner.getWidth()/2, CANVAS_HEIGHT/2);
		winner.setFont("getFont-bold-20");
		
		GLabel loser = new GLabel ("Better luck next time!");
		//loser.setLocation(getWidth()/2 - loser.getWidth()/2, getHeight()/2);
		loser.setLocation(getWidth()/2 - loser.getWidth(), CANVAS_HEIGHT/2);
		loser.setFont("getFont-bold-20");
		
		//This  calls the GLabel win if the boolean winner is true. Otherwise,
		//the GLabel lose is called.
		if (gameWon){
			add(winner);
		} else {
			add(loser);
		}
	}

	private boolean playGame() {
		int bricksRemaining = NBRICK_ROWS*NBRICK_COLUMNS;
		double livesRemaining = NTURNS;
		
		//This runs the game while there are both bricks and 
		//turns remaining. 
		while (bricksRemaining > 0 && livesRemaining > 0){
			livesRemaining--; 
			bricksRemaining = playBall(bricksRemaining);
		}
		if (bricksRemaining == 0) {
			return true; 
		} else { return false;
		}
	}
	
public void mouseClicked(MouseEvent a) {
	waitingForPlayer = false;
}

private int playBall(int bricksRemaining) {
	//This randomizes the x component of the ball's 
	//velocity by selecting a random velocity between
	//1 and 3 and making it negative half of the time. 
		double vx = rgen.nextDouble (1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;
		
		boolean ballInPlay = true; 
		GOval ball = placeBall(); 
		
		waitingForPlayer = true; 
		while(waitingForPlayer) {
			pause(DELAY);
		}
	
	//This gets the location of the ball.  If the ball is at the location
	//of the paddle, then the ball bounces off the paddle.  If the ball hits
	// a brick, then that brick will be removed.
		while (ballInPlay) {
			GPoint currentPos = new GPoint(ball.getLocation());
			double xPos = currentPos.getX();
			double yPos = currentPos.getY();
			
			GObject collider = getCollider(xPos, yPos); 
			if (collider == paddle) {
				if (vy > 0) {
				vy = -vy;
				} 
			}
				
			//If the ball hits a brick, that brick will be removed.
			else if (collider != null) {
				vy = -vy;
				remove(collider);
				bricksRemaining = bricksRemaining - 1;
				if (bricksRemaining == 0) {
					ballInPlay= false;
					remove(ball);
				}
			} 
			//The ball will bounce off the sides of the screen.
			else if (xPos < BALL_RADIUS*2 || xPos > getWidth()-BALL_RADIUS*2) {
				vx = -vx ;
			}
			//The ball will bounce off the top of the screen.
			else if (yPos < BALL_RADIUS*2) {
				vy = -vy;
			}
			//The ball will exit the bottom of the screen. 
			else if (yPos > getHeight()){
				ballInPlay = false;
				remove(ball);
			}
			ball.setLocation(xPos + vx, yPos + vy);
			pause(DELAY);
			}
		return bricksRemaining;
}
	
	private GObject getCollider(double x, double y) {
		
		//This checks for objects at each of the four corners
		//of the square inscribing the ball. 
		GObject collider = getElementAt(x, y);
		if (collider == null) {
			collider = getElementAt(x + BALL_RADIUS*2, y);
		}
		if (collider == null) {
			collider = getElementAt(x, y+ BALL_RADIUS*2);
		}
		if (collider == null) {
			collider = getElementAt(x +BALL_RADIUS*2, y + BALL_RADIUS*2);
		}
		return collider;
	}

	private void setUpGame() {
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
				// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		double xCord = (getWidth()/2-((NBRICK_COLUMNS * BRICK_WIDTH)/2) - ((NBRICK_COLUMNS- 1)*BRICK_SEP/2));
		
		//This creates columns of bricks by building one individual row and
		//then increasing increasing the y-coordinate to build the next row.
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double yCord = (BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP)*row);
			buildRow(xCord, yCord, row);
		}
		makePaddle();
	}
	
	private GOval placeBall() {
		GOval ball = new GOval (getWidth()/2, getHeight()-100, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
		return(ball);
	}
	 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX < 0){
			mouseX = PADDLE_WIDTH;
		}
		if (mouseX > getWidth()) {
			mouseX = getWidth()-2*PADDLE_WIDTH;
		}
		paddle.setLocation(mouseX - PADDLE_WIDTH/2, getHeight()-PADDLE_HEIGHT);
	}
	
	private void makePaddle() {
		addMouseListeners();
		paddle.setLocation(getWidth()/2, getHeight()-PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	//This builds individual rows of bricks.  It then sets the color of
	//each row depending on how many rows have already been built. 
	private void buildRow(double x, double y, int row) {
		for (double i = 0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect ((x+ i*(BRICK_WIDTH+BRICK_SEP)), y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			if (row/2 == 0){
				brick.setColor(Color.RED);
			}
			if (row/2 == 1) {
				brick.setColor(Color.ORANGE);
			}
			if (row/2 == 2) {
				brick.setColor(Color.YELLOW);
			}
			if (row/2 == 3) {
				brick.setColor(Color.GREEN);
			}
			if (row/2 >= 4) {
				brick.setColor(Color.CYAN);
			}
			add(brick);
		}
	} 
} 
	


