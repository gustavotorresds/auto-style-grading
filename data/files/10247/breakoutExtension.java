/*
 * File: breakoutExtension.java
 * -------------------
 * Name: Sam Turchetta
 * Section Leader: Jordan Rosen-Kaplan
 * 
 * This file runs the breakout game with extension. It starts by setting up the game.
 * Next it begins play mode by waiting for the user to click. Then play mode starts
 * After that it allows for the results to show, whether you won or lost.
 * 1. Change speed of ball with each hit on paddle
 * 2. Reset Speed after loss of life
 * 3. Life Count Bottom Left
 * 4. Score Total
 * 5. Shoot ball off in different angles
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class breakoutExtension extends GraphicsProgram {

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

	// Paddle Y constant


	//instance variables
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	private double vy = VELOCITY_Y;
	private double life = 0;
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;
	private GLabel livesLeft;
	private int level = 0;
	private GLabel score;
	private int points = 0;

	//Run method begin by setting up the game
	//Next it begins game by going to that run method
	//Finally it displays the results of the game
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		beginGame();
		results();
	}
	//First it builds and colors the bricks, next it adds the paddle

	private void setUpGame() {
		buildBricks();
		addPaddle();
	}

	//This method allows for the game to be played
	//Starting with listening for the mouse, then the goes into the play game

	private void beginGame() {
		addMouseListeners();
		lifeCount();
		pointPrint();
		playGame();
	}

	// This method fills the screen with bricks, allowing for the empty screen to have the earlier specified rows and columns and color coordinated
	//Finds how far the x needs to be indented then it builds the one row after the next
	private void buildBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double indentX = ((getWidth() +BRICK_SEP) - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS))/2;
				double brickX = indentX + (col) * (BRICK_WIDTH + BRICK_SEP);
				double brickY = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;
				GRect brick = new GRect (brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				colorBricks (row, brick);
				add(brick);
			}
		}
	} 
	//This colors the bricks using a parameter from the buildBricks method. It uses the remainder so if more rows are added they are properly colored
	private void colorBricks (int row, GRect brick) {
		if (row % 10 <= 1) {
			brick.setColor(Color.RED);
		}
		else if (row % 10 <= 3) {
			brick.setColor(Color.ORANGE);
		}
		else if (row % 10 <= 5) {
			brick.setColor(Color.YELLOW);
		}
		else if (row % 10 <= 7) {
			brick.setColor(Color.GREEN);
		}
		else if (row % 10 <= 9) {
			brick.setColor(Color.CYAN);
		}
	}

	// This method builds the paddle at the middle/bottom of the screen, just above the very bottom of the screen
	private void addPaddle() {
		double paddleX = (getWidth() - PADDLE_WIDTH)/2;
		double PADDLE_Y = (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET)) ;
		paddle = new GRect (paddleX, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

	}
	//This method creates the ball in the center of the window
	private void createBall() {
		double ballX = (getWidth() - 2*BALL_RADIUS)/2;
		double ballY = ((getHeight() - 2*BALL_RADIUS)/2);
		ball = new GOval (ballX, ballY, 2* BALL_RADIUS, 2* BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	//This tells java to listen to the mouse and move the paddle depending on where the mouse is moved along the window
	//It begins whenever the player moves the mouse so they can position it wherever they like in the row

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double PADDLE_Y = (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET)) ;
		if (mouseX > (getWidth() - (PADDLE_WIDTH/2 + BRICK_SEP))) {
			paddle.setLocation(getWidth() - (PADDLE_WIDTH+ BRICK_SEP), PADDLE_Y);
		}
		else if (mouseX < (PADDLE_WIDTH/2 + BRICK_SEP)) {
			paddle.setLocation(BRICK_SEP, PADDLE_Y);
		}
		else {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, PADDLE_Y);
		}
	}
	//This method plays the game, it runs while lives is less than turns allowed. Also it ends when you run out of bricks
	//It uses a while loop to play the game
	//The ball moves in vx and vy directions and looks for bounces off the wall or side

	private void playGame() {
		while(life < NTURNS && brickCounter > 0) {
			createBall();
			remove(livesLeft);
			lifeCount();
			waitForClick();
			resetSpeed();
			if (rgen.nextBoolean(0.5)) vx = -vx;
			while(brickCounter > 0) {
				ball.move(vx, vy);
				pause(DELAY);
				wallBounce();
				ballCollision();
				if (ball.getY() >= getHeight() - ball.getHeight() ) {
					life = life+1;
					remove(ball);
					break;
				}
			}
		}
	}
	//Keeps track of the lives remaining

	private void lifeCount() {
		double left = NTURNS - life;
		livesLeft = new GLabel ("Lives: " + left);
		add(livesLeft, livesLeft.getWidth(), getHeight() - livesLeft.getAscent());
	}
	//Resets the speed after you lose a life
	private void resetSpeed() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	}
	//If the ball bounces off a side wall it changes the x direction of the ball
	//If the ball bounces off the top wall it changes the Y direction

	private void wallBounce() {
		if (ball.getY() <= ball.getHeight()) {
			vy = -vy;
		}
		if (ball.getX() <= ball.getWidth() || ball.getX() >= getWidth() -ball.getWidth()) {
			vx = -vx;
		}
	}
	//This uses the GObject defined as the collider to see what object the collider bounces off of
	//If it is a paddle or brick it changes the y direction 

	private void ballCollision() {
		double leftSideBall = ball.getX();
		double rightSideBall = ball.getX() + 2 * BALL_RADIUS;
		double middleY = ball.getY() + BALL_RADIUS;
		GObject collider =  getCollidingObject(leftSideBall, rightSideBall);
		if (collider == paddle) {
			//Accounts for sticky paddle, if player gets paddle to ball in time it will save them from losing a life and there won't be a sticky paddle
			if (vy > 0) {
				level = level + 1;
				vy = -vy;
				//Changes the speed on every seventh hit
				if (level % 7 == 0) {
					vy = 2*vy;
					vx = 2*vx;
				}
				//The Following if statements change the direction of the ball, if it hits in the middle it follows the normal laws of physics
				//The first one, if the ball is to the left side of the paddle it will reverse the vx if the ball is coming from that side
				//otherwise it would shoot it off in a more sideways velocity
				if (rightSideBall < (paddle.getX() + (0.5*PADDLE_WIDTH))) {
					if (vx > 0 ) {
						vx = -vx;
					} else {
						vx = 1.25*vy;
						vy = .66*vy;
					}
				} 
				//This one has the same process, but if the ball is on the right side of the paddle
				else if (leftSideBall > (paddle.getX() + (0.5*PADDLE_WIDTH))) {
					if (vx < 0) {
						vx = -vx;
					} else {
						vx = 1.25*vy;
						vy = .66*vy;
					}
				}
			}
		}
		else if (collider != null && collider != livesLeft && collider != score) {
			remove(score);
			scoreCounter(collider);
			brickCounter = brickCounter - 1;
			remove(collider);
			//This accounts for if the ball hits the side of a brick, if it hits side x direction will change
			if(getElementAt(rightSideBall, middleY) != null || getElementAt(leftSideBall, middleY) != null) {
				vx = -vx;
			}
			//Otherwise if it hits a brick normally, from the top or bottom of the ball it will bounce in y direction
			else {
				vy =- vy;
			}

		}
	}
	//Point calculator depedning on the color
	private void scoreCounter(GObject collider) {
		if (collider.getColor() == Color.CYAN) {
			points = points + 50;
		}
		else if (collider.getColor() == Color.GREEN) {
			points = points + 100;
		}
		else if (collider.getColor() == Color.YELLOW) {
			points = points + 200;
		}
		else if (collider.getColor() == Color.ORANGE) {
			points = points + 400;
		}
		else if (collider.getColor() == Color.RED) {
			points = points + 800;
		}
		pointPrint();
	}
	//Point label printer
	private void pointPrint() {
		score = new GLabel ("Points: " + points);
		add(score, getWidth() - 2*score.getWidth(), getHeight() - score.getAscent());
	}
	//Uses parameter to get numbers so they do not have to be repeated variables
	//Finds the collider object by returning the element found that the ball has made contact with
	//If it is not nothing and is not the ball this will return that object to the ballCollision method
	private GObject getCollidingObject(double leftSideBall, double rightSideBall) {
		double topY = ball.getY();
		double bottomY = ball.getY() + 2 * BALL_RADIUS; 
		//Top left corner of the square
		if (getElementAt(leftSideBall, topY) != null && (getElementAt(leftSideBall,topY)) != ball) {
			return(getElementAt(leftSideBall,topY));
		}
		//Top right corner of the square
		else if (getElementAt(rightSideBall, topY) != null && (getElementAt(leftSideBall,topY)) != ball) {
			return(getElementAt(rightSideBall, topY));
		}
		//Bottom left corner of the square
		else if (getElementAt(leftSideBall, bottomY) != null && (getElementAt(leftSideBall,topY)) != ball) {
			return(getElementAt(leftSideBall, bottomY));
		}
		//Bottom right corner of the square
		else if (getElementAt(rightSideBall, bottomY) != null && (getElementAt(leftSideBall,topY)) != ball) {
			return(getElementAt(rightSideBall, bottomY));
		} 
		//If no contact with an object it will continue to run
		else {
			return null;
		}
	}

	//Displays the users success in the game, if there are no bricks left, user gets winner message, if they lose their lives, they get the loser message
	private void results() {
		if (brickCounter == 0) {
			removeAll();
			GLabel winner = new GLabel ("You Won!");
			double centerXText = getWidth()/2 - winner.getWidth() / 2;
			double centerYText = getHeight()/2 - winner.getAscent()/2;
			add(winner, centerXText, centerYText);
		} else {
			removeAll();
			GLabel loser = new GLabel ("You Lost :(");
			double centerXText = getWidth()/2 - loser.getWidth() / 2;
			double centerYText = getHeight()/2 - loser.getAscent()/2;
			add(loser, centerXText, centerYText);		}
		add(score, (getWidth() - score.getWidth())/2 , getHeight()/2 + score.getAscent()/2); 
	}


}

