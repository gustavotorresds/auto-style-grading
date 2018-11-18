/*
 * File: Breakout.java
 * -------------------
 * Name: Longjie Chen
 * Section Leader: Ruiqi Chen
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
	
	// Number of the total bricks existing in the beginning of Game.
	private static final int BRICK_NUM = NBRICK_ROWS * NBRICK_COLUMNS;
	
	//making a random generator and velocity variables.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
		
	// setting the initial value for brick number at the beginning.	
	private int brickNumber = BRICK_NUM;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		for(int i = 0; i < NTURNS; i++) {
			// setup the world.
			setPaddle();
			setBricks();
			setBall();
			
			// process the game.
			playGame();
			if(brickNumber == 0) {
				// determine if win the game.
				removeAll();
				printWin();
				break;
			}	else if(brickNumber > 0) {
				// to the next turn, brick number should be reset.
				brickNumber = BRICK_NUM;
				removeAll();
			}
		}
		
		// determine if lose the Game.
			if(brickNumber > 0) {
				printLose();
			}
	}
		
		
	// it should make a value for paddle first, so it can be operated in response to mouse.
	private GRect paddle;
		
	// initial setup for paddle.
	private void setPaddle() {
			// setting up the initial position of the paddle at the middle point of its line.
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
			
		// creating the paddle, filling it, and adding it to window.
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
			
		// adding a response to mouse.
		addMouseListeners();
	}
		
	// defining a mouse behavior so paddle can move with mouse.
	public void mouseMoved(MouseEvent e) {
		// getting mouse's position, only X is needed.
		double mouseX = e.getX();
			
		// to make paddle follow mouse motion.
		if(mouseX > PADDLE_WIDTH/2 && mouseX < getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		}
	}
	
	//making the variable for one brick.
	private GRect brick;
	
	// setting up bricks.
	private void setBricks() {
		// getting the initial position of the first brick, which can determine other bricks.
		// put the bricks block in middle of window.
		double brickX0 = (getWidth() - BRICK_WIDTH * NBRICK_COLUMNS - BRICK_SEP * (NBRICK_COLUMNS - 1))/2;
		double brickY0 = BRICK_Y_OFFSET;
		
		// draw bricks using loop, first by column then by row.
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int col = 0; col < NBRICK_COLUMNS; col++) {
				double brickX = brickX0 + col * (BRICK_WIDTH + BRICK_SEP);
				double brickY = brickY0 + row * (BRICK_HEIGHT + BRICK_SEP);
				
				// create bricks.
				brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				
				// set up color for different rows.
				if(row <= 1) {
					brick.setColor(Color.RED);
				} else if(row > 1 && row <=3) {
					brick.setColor(Color.ORANGE);
				} else if(row > 3 && row <=5) {
					brick.setColor(Color.YELLOW);
				} else if(row > 5 && row <=7) {
					brick.setColor(Color.GREEN);
				} else if(row > 7) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
	
	//making the variable for the ball.
	private GOval ball;
	
	// set up ball at the initial position.
	private void setBall() {
		// get initial position of ball.
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;
		
		// create the ball.
		ball = new GOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	private void playGame() {
		waitForClick();
		
		// get initial velocity from random process.
		ballInitialV();
		while(true) {
			moveBall();
			if((ball.getY() > getHeight()) || (brickNumber == 0)) {
				// if the ball go down bottom line, or all the bricks have been removed.
				// the movement should be ended.
				break;
			}
			
		}
	}
	
	// set random value for initial X velocity.
	private void ballInitialV() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			// random direction of left or right with a possibility of 0.5.
			vx = -vx;
		}
	}
	
	private void moveBall() {
		//paddleConduct01 and paddleConduct02 are the contact of ball with paddle's upper side and bottom side.
		// to avoid adherence in some special situation.
		boolean paddleConduct01 = ball.getY() >= getHeight() - PADDLE_Y_OFFSET;
		boolean paddleConduct02 = ball.getY() <= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2;
		ball.move(vx, vy);
		
		// check for the collision with wall, the bottom wall need no check
		// since when ball meet bottom wall, the turn ends.
		if((ball.getX() - vx < 0 && vx < 0) || (ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx>0)) {
			vx = -vx;
		}
		if((ball.getY() - vy < 0 && vy < 0 )) {
			vy = -vy;
		}
		
		// collOject is the return value for the contact objects.
		// if ball contacts paddle, it should change Y velocity,
		// if meets brick, it should also remove the brick.
		GObject collObject = getCollObj();
		if(collObject == paddle) {
			if(paddleConduct01 || paddleConduct02) {
				vy = -vy;
			}
		} else if (collObject != null) {
			remove(collObject);
			brickNumber--;
			vy = -vy;
		}
		pause(DELAY);
	}
	
	// The method to check if ball meet any object.
	// the order for checking I set is left-upper, right-upper, left-bottom, right-bottom.
	// it is a reasonable approximation.
	// if ball meets nothing at four corner, it will return null.
	private GObject getCollObj() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
	         return getElementAt(ball.getX(), ball.getY());
	      }
		else if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
	         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
	      }
		else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
	         return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
	      }
		else if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
	         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
	      } else {
	    	  return null;
	      }
	}
	
	// print information for win.
	private void printWin() {
		GLabel win = new GLabel("Winner Winner, Chicken Dinner!");
		win.setFont("Courier-20");
		win.setColor(Color.RED);
		add(win, getWidth()/2 - win.getWidth()/2, getHeight()/2);
	}
	
	// print information for lose.
	private void printLose() {
		GLabel lose = new GLabel("You Lose!");
		lose.setFont("Courier-40");
		add(lose, getWidth()/2 - lose.getWidth()/2, getHeight()/2);
	}
	
}


