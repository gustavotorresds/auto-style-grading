/*
 * File: Breakout.java
 * -------------------
 * Name: Claire Womack
 * Section Leader: Adam Mosharrafa
 * 
 * This file will implements the game of Breakout WITH extensions.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutEXT extends GraphicsProgram {

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
	public static int NTURNS = 3;

	//INSTANCE VARIABLES
	private GRect paddle = null;
	private GOval ball = null;
	private GLabel scoreboard = null;
	int nHits = 0;
	int livesRemaining = NTURNS; 
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int BRICK_NUMBER = NBRICK_ROWS * NBRICK_COLUMNS;
	private int points;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();
		while (true) {
			wallCollisions();
			getCollidingObject();
			checkCollidingObject();
			loseGame();
		}
	}
	//This if statement creates a losing game condition that accounts for loss of life when ball drops below paddle.
	private void loseGame() {
		if(hitBottomWall(ball)) { 
			livesRemaining--;
			GLabel lifeCounter = new GLabel("LIVES:" + (livesRemaining));
			lifeCounter.setFont("SansSerif-24");
			double lx = getWidth()/ 2 - lifeCounter.getWidth()/ 2;
			double ly = getHeight()/ 2 - lifeCounter.getHeight();
			lifeCounter.setLocation(lx, ly);
			if (livesRemaining == 0) {
				gameOver();
				return;
			}
			if (livesRemaining > 0) {
				add(lifeCounter);
				remove(ball);
				makeBall();
				remove(lifeCounter);
			}
		}
	}

	//This method endures movement off of the left, right, and top walls.
	private void wallCollisions() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy = -vy;
		}
		ball.move(vx, vy);
		pause(DELAY);
	}
	//EXT: Creates a GLabel below the paddle displaying the number of points accumulated.
	private void makeScoreboard() {
		scoreboard = new GLabel("Score: " + (points));
		scoreboard.setFont("SansSerif-14");
		double x = getWidth()/ 2 - (scoreboard.getWidth()/ 2);
		double y = getHeight() - (PADDLE_Y_OFFSET/ 2) + (PADDLE_HEIGHT);
		scoreboard.setLocation(x, y);
		add(scoreboard);
	}
	

	private void checkCollidingObject() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {	
			vy = -Math.abs(vy);	
			//EXT: As the ball hits the paddle, the speed will increase every 2 hits.
			nHits++;
			if (nHits % 2 == 0) {
				vx++;
			}
			
		}
		//This if statement that deletes bricks and accounts for the number of bricks on the screen by subtracting one for each brick removed.
		if (collider != paddle && collider != null) {
			BRICK_NUMBER--; 
			 //SCOREKEEPER - assigns different values for each color of brick that is hit and records them in the scoreboard
			if (collider.getColor() == Color.CYAN) {
				points += 10;
				scoreboard.setLabel("Score: " + points);
			}
			if (collider.getColor() == Color.GREEN) {
				points += 15;
				scoreboard.setLabel("Score: " + points);
			}
			if (collider.getColor() == Color.YELLOW) {
				points += 25;
				scoreboard.setLabel("Score: " + points);
			}
			if (collider.getColor() == Color.ORANGE) {
				points += 30;
				scoreboard.setLabel("Score: " + points);
			}
			if (collider.getColor() == Color.RED) {
				points += 50;
				scoreboard.setLabel("Score: " + points);
			}
			
			vy = -vy;
			remove(collider);

			//This if statement checks if all the bricks have been deleted and if that condition is true, finishes the game with a win statement.
			if(BRICK_NUMBER == 0) {
				winGame();
				return;
			}
		}

	}
	//This method is called if livesRemaining = 0 when the ball falls below the paddle line.
	//It creates a label that reads Game Over and removes the ball from the screen.
	private void gameOver() {
		remove(ball);
		GLabel gameOver = new GLabel("GAME OVER");
		gameOver.setFont("SansSerif-bold-24");
		double lx = getWidth()/ 2 - gameOver.getWidth()/ 2;
		double ly = getHeight()/ 2 - gameOver.getHeight()/ 2;
		gameOver.setLocation(lx, ly);
		add(gameOver);
	}

	//This method is called when all the bricks have been cleared. 
	//It removes the ball and displays a label that reads "You Won"
	private void winGame() {
		remove(ball);
		GLabel winStatement = new GLabel("YOU WON!");
		winStatement.setFont("SansSerif-bold-24");
		double lx = getWidth()/ 2 - winStatement.getWidth()/ 2;
		double ly = getHeight()/ 2 - winStatement.getHeight()/ 2;
		winStatement.setLocation(lx, ly);
		add(winStatement);
	}

	// The following hit wall methods return false condition
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= (getWidth() - 2 * BALL_RADIUS);
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > (getHeight() - 2 * BALL_RADIUS);
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/* 
	 * The setUp method starts to lay out the bricks.
	 * The for loop keeps track of row number with the variable n.
	 * The variable bY starts at the offset and builds proceeding rows accounting for separation between bricks.
	 * It also defines the instance variables for the start of the game such as points and initial x velocity, randomizing the direction of the ball at the start of the game. 
	 * setUp then adds elements such as the scoreboard, ball, paddle, and mouselisteners. 
	 */ 
	private void setUp() {
		for (int n = NBRICK_ROWS; n>=1; n--) {
			double bY = (BRICK_Y_OFFSET + ((NBRICK_ROWS - n + 1) * (BRICK_SEP + BRICK_HEIGHT)));
			buildRow(n, bY); 
		}
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if(rgen.nextBoolean(0.5)) vx = -vx; 
		points = 0;
		makeScoreboard();
		makePaddle();
		makeBall();
		add(ball);
		addMouseListeners();

	}
	//the buildRow method depends on the row number and the column number
	private void buildRow(int n, double bY) {
		double bX = getWidth()/2 - ((NBRICK_COLUMNS*(BRICK_WIDTH + BRICK_SEP))/ 2 - (BRICK_SEP/ 2));
		for (int i = NBRICK_COLUMNS; i >= 1; i--) {
			GRect brick = new GRect(bX, bY, BRICK_WIDTH, BRICK_HEIGHT); 
			bX += (BRICK_WIDTH + BRICK_SEP);
			brick.setFilled(true);
			brick.setColor(getRowColor(n));
			add(brick);
		}	

	}
	//Uses remainder operator to divide row number in order to assign colors.
	private Color getRowColor(int n) {
		int x = n % 10;
		if (x == 1 || x == 2) {
			return Color.CYAN;
		}
		if (x == 3 || x == 4) {
			return Color.GREEN;
		}
		if (x == 5 || x == 6) {
			return Color.YELLOW;
		}
		if (x == 7 || x == 8) {
			return Color.ORANGE;
		} else {
			return Color.RED;
		}	
	}

	//Defines instance variable "paddle" and adds it to center of screen.
	private void makePaddle() {
		double pX = (getWidth()/2) - (PADDLE_WIDTH/ 2);
		double pY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect (pX, pY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	//Controls movement of paddle by moving it in the x direction and maintaining the
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		double pY = getHeight() - PADDLE_Y_OFFSET;
		if (x + PADDLE_WIDTH <= getWidth()) {
			paddle.setLocation(x, pY);
		}

	}
	//Defines instance variable "ball" and can be used to implement the ball at the beginning of game and after loss of life.
	public void makeBall() {
		double bX = ((getWidth()/ 2) - BALL_RADIUS);
		double bY = ((getHeight()/ 2) - BALL_RADIUS);
		ball = new GOval (bX, bY, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		waitForClick();
		vy = VELOCITY_Y;

	}
	//This method checks all corners of the ball for other objects and allows it to move around if there are no objects. 
	private GObject getCollidingObject() {
		GObject collTopLeft = getElementAt (ball.getX(), ball.getY());
		GObject collTopRight = getElementAt ((ball.getX() + (2*BALL_RADIUS)), ball.getY());
		GObject collBottomLeft = getElementAt (ball.getX(), (ball.getY() + (2*BALL_RADIUS)));
		GObject collBottomRight = getElementAt ((ball.getX() + (2*BALL_RADIUS)), (ball.getY() + (2*BALL_RADIUS)));
		if (collTopLeft != null) {
			return collTopLeft;
		} 
		if (collTopRight != null) {
			return collTopRight;
		}
		if (collBottomLeft != null) {
			return collBottomLeft;
		}
		if (collBottomRight != null) {
			return collBottomRight;
		}
		return null;
	}


}




