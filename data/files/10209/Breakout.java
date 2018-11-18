/*
 * File: Breakout.java
 * -------------------
 * Name: Tanya Watarastaporn
 * Section Leader: Nidhi Manoj
 * 
 * This file will eventually implement the game of Breakout. In 
 * this file, the rainbow bricks for the game of Breakout will first be
 * set up. Then the player can play the game of Breakout for up to three turns
 * before the results of the game are displayed. Depending on whether the 
 * player manages to remove all the bricks before all of the turns are used, 
 * the player will either get a message stating he/she won or lost the game.
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

	// Declares the paddle as an instance variable
	private GRect paddle = null;

	// Declares the ball as an instance variable
	private GOval ball = null;

	// initializes the random generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// x velocity of the ball as an instance variable
	private double vx = 0;

	// y velocity of the ball as an instance variable
	private double vy = 0;

	// sets the bricksRemoved counter as initially 0
	private int bricksRemoved = 0;

	// total bricks equals to the number of bricks in each row
	// times number of rows
	private static final int TOTAL_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;


	/**
	 * Method: run
	 * -------------------------------
	 * This run method will set the title and canvas size of Breakout.
	 * The rainbow bricks will be set up first. Then the player can play 
	 * Breakout for a total of up to three turns until the results of the
	 * game are displayed, depending on if the number of bricks removed 
	 * equal the number of total bricks in the game.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpRainbowBricks();
		playBreakout();
		displayBreakoutResults(bricksRemoved, TOTAL_BRICKS);
	}

	/**
	 * Method: create Rainbow Bricks
	 * ------------------------------------
	 * This method will create the rainbow bricks to be used in Breakout.
	 * There will be two rows of bricks for each of the colors: red, 
	 * orange,yellow, green, and cyan. The bricks will be colored in that
	 * order and will be located in the center of the screen in the vertical 
	 * direction and will be offset from the top of the screen according to 
	 * the specified value of the BRICK_Y_OFFSET. The bricks will also be 
	 * separated from one another based on the specified constant BRICK_SEP.
	 */

	private void setUpRainbowBricks()	{
		for(int brickRow = 0; brickRow < NBRICK_ROWS; brickRow++)	{
			for(int brickNum = 0; brickNum < NBRICK_COLUMNS; brickNum++)	{
				double x = (getWidth() + BRICK_SEP)/2.0 - ((BRICK_WIDTH + BRICK_SEP)*(NBRICK_COLUMNS))/2.0 
						+ (BRICK_WIDTH + BRICK_SEP)*brickNum;
				double y = (BRICK_HEIGHT + BRICK_SEP)*(brickRow+1) + BRICK_Y_OFFSET - BRICK_SEP;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				setColoredBricks(brickRow, 0, 1, brick, Color.RED);
				setColoredBricks(brickRow, 2, 3, brick, Color.ORANGE);
				setColoredBricks(brickRow, 4, 5, brick, Color.YELLOW);
				setColoredBricks(brickRow, 6, 7, brick, Color.GREEN);
				setColoredBricks(brickRow, 8, 9, brick, Color.CYAN);
			}
		}
	}

	/**
	 * Method: set Colored Bricks
	 * -----------------------------------
	 * This method will take in several parameters to color the rows 
	 * of bricks to their respective colors. The method takes in rows, 
	 * two row numbers, the brick, and color. Whatever row values are 
	 * passed into  the two row numbers will result in the bricks 
	 * corresponding to those row numbers being colored the same color 
	 * that is passed into the color parameter.
	 */
	private void setColoredBricks(int rows, int rowNum1, int rowNum2, GRect brick, Color color)	{
		if(rows == rowNum1 || rows == rowNum2)	{
			brick.setColor(color);
		}
	}

	/**
	 * Method: play Breakout
	 * ----------------------------------
	 * This method will play the game of Breakout for a total of three 
	 * turns. For each turn, the ball and paddle is reset, and the player 
	 * must click for the ball to move and therefore play the game. If the
	 * player were to remove all the bricks before all three turns have been
	 * used, this method will end
	 */
	private void playBreakout()	{
		for(int turns = 0; turns < NTURNS; turns++)	{
			createBall();
			createPaddle();
			waitForClick();
			moveBall();
			if(bricksRemoved == TOTAL_BRICKS)	break;
		}
	}

	/**
	 * Method: create Paddle
	 * ----------------------------------
	 * This method creates the paddle that is used to play Breakout.
	 * The paddle is created with the specified constants and is 
	 * colored black.
	 */
	private void createPaddle()	{
		double x = (getWidth() - PADDLE_WIDTH)/2.0;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(x, y ,PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
	}

	/**
	 * Method: create Ball
	 * -----------------------------------
	 * This method creates the ball that is used to play Breakout.
	 * The ball is created with the specified constants and is 
	 * colored black.
	 */
	private void createBall()	{
		double x = (getWidth() - 2*BALL_RADIUS)/2.0;
		double y = (getHeight() - 2*BALL_RADIUS)/2.0;
		ball = new GOval(x, y ,2*BALL_RADIUS, 2*BALL_RADIUS);
		add(ball);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
	}

	/**
	 * Method: mouse Moved
	 * -----------------------------------
	 * This method will help allow the paddle to track the mouse 
	 * whenever the mouse is moved. If the mouse is moved to a 
	 * location that is outside the boundaries of the screen,
	 * the paddle will remain at the location where it touches the edge
	 * of the wall instead of moving beyond the walls
	 */
	public void mouseMoved(MouseEvent e)	{
		double mouseX = e.getX() - PADDLE_WIDTH/2.0;
		double mouseY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		if(withinRightSideWall(mouseX) && withinLeftSideWall(mouseX))	{
			paddle.setLocation(mouseX, mouseY);
		}
	}

	/**
	 * Method: within Right Side Wall
	 * ----------------------------------
	 * This method is a boolean that takes in a double as a parameter.
	 * In this case the parameter will be the location of the mouse.
	 * The boolean will return true if the location of the mouse
	 * is less than the width of the screen minus the paddle width
	 */
	private boolean withinRightSideWall(double mouseX)	{
		return mouseX < (getWidth() - PADDLE_WIDTH);
	}

	/**
	 * Method: within Left Side Wall
	 * ----------------------------------
	 * This method is a boolean that takes in a double as a parameter.
	 * In this case the parameter will be the location of the mouse.
	 * The boolean will return true if the location of the mouse
	 * is greater than 0 for its x-coordinate
	 */
	private boolean withinLeftSideWall(double mouseX)	{
		return mouseX > 0;
	}

	/**
	 * Method: move Ball
	 * ----------------------------------
	 * This method will set up the starting velocities for the ball
	 * and also keep changing the ball's x and y velocities according to
	 * its movements, which include collisions with certain objects
	 */
	private void moveBall()	{
		setInitialVelocities();
		updateBallMovements();
	}

	/**
	 * Method: update Ball Movements
	 * -----------------------------------
	 * This method will update the ball movements while the game is
	 * being played and the bricks have not yet all been removed.
	 * The boolean methods used for checking wall collisions are
	 * inspired by Chris's animation lecture.
	 */
	private void updateBallMovements()	{
		while(bricksRemoved != TOTAL_BRICKS)	{
			ball.move(vx, vy);
			pause(DELAY);
			checkAndUpdateForCollisions();
			if(collideWithRightWall(ball) || collideWithLeftWall(ball)) vx = -vx;
			if(collideWithTopWall(ball)) vy = -vy;	
			if(collideWithBottomWall(ball))	{
				remove(ball);
				remove(paddle);
				break;
			}
		}
	}

	/**
	 * Method: set Initial Velocities
	 * ----------------------------------
	 * This method will set the starting velocities of the ball.
	 * The value of the x-velocity will be randomized and range from
	 * the min and max values of the x-velocities 
	 */
	private void setInitialVelocities()	{
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}

	/**
	 * Method: check And Update For Collisions
	 * ----------------------------------
	 * This method will check for collisions and return a collider if 
	 * there is any and update the ball's movements accordingly. If the 
	 * collider is the paddle, the ball will move with the negative of the 
	 * absolute values of the vx and vy. If the collider is not null, and 
	 * therefore a brick, the ball will move in the negative vy direction 
	 * and the collider/brick will be removed. The number of bricks removed 
	 * will increase for each brick that is removed.
	 */
	private void checkAndUpdateForCollisions()	{
		GObject collider = getCollidingObject(ball);
		if(collider == paddle)	{
			vx = -(Math.abs(vx));
			vy = -(Math.abs(vy));
		} else if(collider != null) {
			remove(collider);
			bricksRemoved++;
			vy = -vy;
		}
	}

	/**
	 * Method: collide With Right Wall
	 * -----------------------------------
	 * This boolean is true if the ball's x-location is going to 
	 * be past the right wall
	 */
	private boolean collideWithRightWall(GOval ball)	{
		return ball.getX() > (getWidth() - 2*BALL_RADIUS);
	}

	/**
	 * Method: collide With Left Wall
	 * -----------------------------------
	 * This boolean is true if the ball's x-location is going to
	 * be past the left wall
	 */
	private boolean collideWithLeftWall(GOval ball)	{
		return ball.getX() < 0;
	}

	/**
	 * Method: collide With Bottom Wall
	 * ------------------------------------
	 * This boolean is true if the ball's y-location is going to
	 * be past the bottom wall
	 */
	private boolean collideWithBottomWall(GOval ball)	{
		return ball.getY() > (getHeight() - 2*BALL_RADIUS);
	}

	/**
	 * Method: collide With Top Wall
	 * ------------------------------------
	 * This boolean is true if the ball's y-location is going to 
	 * be past the top wall
	 */
	private boolean collideWithTopWall(GOval ball)	{
		return ball.getY() < 0;
	}

	/**
	 * Method: get Colliding Object
	 * -------------------------------------
	 * This method will return the object the ball is colliding with
	 * due to the method that checks for a collision for each of the 
	 * ball's four corners
	 */
	private GObject getCollidingObject(GOval ball)	{
		double cornerX = ball.getX();
		double cornerY = ball.getY();
		return objectCollidingWithCorner(cornerX, cornerY);
	}

	/**
	 * Method: object Colliding With Corner
	 * -------------------------------------
	 * This method will return the element or object that is found at 
	 * each specified x and y location which is represented by whatever
	 * values are passed into the parameters for the method. The method 
	 * will check for elements at each corner but if there is nothing 
	 * found at any of the locations, the method will return null.
	 */
	private GObject objectCollidingWithCorner(double cornerX, double cornerY)	{
		if(getElementAt(cornerX, cornerY) != null)	{
			return getElementAt(cornerX, cornerY);
		} else if(getElementAt(cornerX + 2*BALL_RADIUS, cornerY) != null) {
			return getElementAt(cornerX + 2*BALL_RADIUS, cornerY);
		} else if(getElementAt(cornerX, cornerY + 2*BALL_RADIUS) != null)		{
			return getElementAt(cornerX, cornerY + 2*BALL_RADIUS);
		} else if(getElementAt(cornerX + 2*BALL_RADIUS, cornerY + 2*BALL_RADIUS) != null)		{
			return getElementAt(cornerX + 2*BALL_RADIUS, cornerY + 2*BALL_RADIUS);
		} else {
			return null;
		}
	}

	/**
	 * Method: display Breakout Results
	 * ------------------------------------
	 * This method will remove all the of the objects left on the screen.
	 * If the number of bricks removed equal the total bricks, the screen
	 * will display an ending message that says the player has won. If not 
	 * all the bricks are removed by the end of the game, the screen will 
	 * display an ending message that says the player has lost
	 */
	private void displayBreakoutResults(int bricksRemoved, int totalBricks)	{
		removeAll();
		if(bricksRemoved == totalBricks)	{
			endingMessage("YOU WON!");
		} else	{
			endingMessage("YOU LOST!");
		}
	}

	/**
	 * Method: ending Message
	 * -------------------------------
	 * This method takes in a string as a parameter. The string, or
	 * message, that is passed into the method will be made into a 
	 * label that will be displayed on the screen. The message will
	 * be centered both vertically and horizontally.
	 */
	private void endingMessage(String message)	{
		GLabel label = new GLabel(message);
		label.setFont("Helvetica-36");
		double x = (getWidth() - label.getWidth())/2.0;
		double y = (getHeight() - label.getAscent())/2.0;
		label.setLocation(x, y);
		add(label);
	}

}
