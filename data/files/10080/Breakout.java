/*
 * File: Breakout.java
 * -------------------
 * Name: Shana Hadi
 * Section Leader: Julia Truitt
 * 
 * This file implements the game of Breakout!
 * It works, and it's awesome, and now I have literally made a game!
 * Note: I included 1 extension (the winner/loser messages).
 * Also worked on user control (moved paddle to middle of cursor.)
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

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
	
	//Number of rows of bricks per color row (use a multiple of 5).
	public static final int NBRICK_COLOR_ROWS = NBRICK_ROWS/5;
	
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
	public static final double VELOCITY_Y = 3.0; //Originally 3.0. Can be changed for fun.

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	
	//Instance variables
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int brickCount = NBRICK_ROWS * NBRICK_COLUMNS; //Initializes # of bricks in beginning (universal).
	
	//Paddle and ball declaration.
	private GRect paddle;
	private GOval ball;
	
	//This method contains the whole program (start/3 turns/game over).
	public void run() {
		//The player has 3 turns (each time resetting) to win Breakout; two conditions break the loop.
		startMessage();
		for (int turns = 0; turns < NTURNS; turns++) {
			setUp();
			playGame();
			
			//Winner!!!
			if (brickCount == 0) {
				removeAll();
				winnerMessage();
				break;
			}
			//Lose a turn; start over.
			if (brickCount > 0) {
				removeAll();
			}
		}
		//Game Over (just exit the loop -- assumes bricks remaining, and no turns left.)
		loserMessage();
	}
	
	//This is responsible for moving the paddle by tracking the cursor.
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		//I wanted the cursor to move the middle of the paddle (not just the left edge)
		//to make it easier for the user (hence PADDLE_WIDTH/2.0).
		if(x <= getWidth() - PADDLE_WIDTH/2.0 && x >= PADDLE_WIDTH/2.0) {
			paddle.setLocation(x - PADDLE_WIDTH/2.0, getHeight() - PADDLE_Y_OFFSET); //When cursor moves left or right, paddle follows.
		}
	}
	
	//This method holds 1 turn of the game, as the ball and paddle are the two factors that move.
	//It ends if the ball leaves the canvas or if there are no more bricks.
	private void playGame() {
		waitForClick();
		getBallVelocity();
		while (true) {
			moveBall();
			//End turn conditions (ball leaves the screen/run out of bricks).
			if (ball.getY() > getHeight()) {
				break;
			}
			if (brickCount == 0) {
				break;
			}
		}
	}
	
	//This chooses the ball's velocity (x,y) at the start of the round.
	//Declared instance variable, so it's defined here, but can be used throughout class.
	private void getBallVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}
	
	/*This contains the "special actions" to move the ball:
	 * 1) Testing wall cases to bounce properly
	 * 2) colliding with the paddle (and adjusting accordingly) / colliding with the bricks (and removing them)
	 */
	private void moveBall() {

		//Moves the ball (visually).
		ball.move(vx, vy);
		
		//Updates the velocity 
		checkWalls();
		collideWithSomething();
		
		//That special animation pause.
		pause(DELAY);
	}
	
	//Checks the wall (left/right/top) to bounce the ball; the bottom is checked in "game over" method.
	private void checkWalls() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)){
			vy = -vy;
		}
	}
	
	//The booleans (left/right/top wall) to flip the ball velocity.
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	//This tests for the ball colliding with the paddle or the brick.
	private void collideWithSomething() {
		GObject collObj = getCollidingObject();
		
		if(collObj == paddle) { //As in, finding the reference!
			//Checks for sticky paddle! I've never experienced it, but just in case!
			//Also checks for ball velocity increasing too much and missing the paddle entirely 
			//b/c it moves VELOCITY_Y pixel speed at a time, and may miss the 1 pixel boundary surface of the paddle!
			if(ball.getY() >= paddle.getY() - 2*BALL_RADIUS && ball.getY() < paddle.getY() - 2*BALL_RADIUS + VELOCITY_Y) {
				vy = -vy;
			}
		} 
		
		//The assumption is that if x/y of the ball does not equal null, or the paddle, then it must be a brick.
		//Using == "brick" would just work for the last brick made in the loop.
		else if(collObj != null) {
			remove(collObj);
			brickCount = brickCount - 1; //To keep track of the number of bricks in the game.
			vy = -vy; // adds the bounce
		}
	}
	
	//Testing out each of the four points surrounding the ball. (Use else if to return only 1 result; check again next round.)
	private GObject getCollidingObject() {
		
		if (getElementAt(ball.getX(), ball.getY()) != null) { //left upper corner
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY()) != null) { //right upper corner
			return getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY());
		}
		else if (getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS)) != null) { //left lower corner
			return getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS));
		}
		else if (getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS)) != null) { //right lower corner
			return getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS));
		}
		else {
			return null;
		}
	}
	
	//This method sets up the entire display at the beginning of the game (including mouse listeners).
	private void setUp() {
		setTitle("CS 106A Breakout!");//Sets the window's title bar text.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); //Sets the canvas size.
		setBricks();	
		setPaddle();
		setBall();
		addMouseListeners();
	}

	//This method sets up the rainbow bricks of the game.
	private void setBricks() {
		
		for (int rowsMadeSoFar = 0; rowsMadeSoFar < NBRICK_ROWS; rowsMadeSoFar++) {
			
			for (int bricksMadeSoFar = 0; bricksMadeSoFar < NBRICK_COLUMNS; bricksMadeSoFar++) {
				
				double x = getWidth()/2.0 - (BRICK_WIDTH * (NBRICK_COLUMNS +1))/2.0 + (BRICK_WIDTH + BRICK_SEP) * bricksMadeSoFar;
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * (rowsMadeSoFar+1); 
				
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true); 
				add(brick);
				
				//I wanted to make this as universal as possible, so if you change the constant NBRICK_ROWS 
				//(and thus the fraction of color rows) to a multiple of five, the statements would still apply.
				if(rowsMadeSoFar < NBRICK_COLOR_ROWS) {
					brick.setColor(Color.RED);
				}
				if(rowsMadeSoFar >= NBRICK_COLOR_ROWS) {
					brick.setColor(Color.ORANGE);
				}
				if(rowsMadeSoFar >= 2*NBRICK_COLOR_ROWS) {
					brick.setColor(Color.YELLOW);
				}
				if(rowsMadeSoFar >= 3*NBRICK_COLOR_ROWS) {
					brick.setColor(Color.GREEN);
				}
				if(rowsMadeSoFar >= 4*NBRICK_COLOR_ROWS) {
					brick.setColor(Color.CYAN);
				}		
			}
		}
	}
	
	//Adds dimensions to the paddle (defined as an instance variable above); only its looks!
	private void setPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK); 
		add(paddle, (getWidth()-PADDLE_WIDTH)/2.0, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
	}
	
	//Adds dimensions to the ball (defined as an instance variable above); only its looks!
	private void setBall() {
		ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.GRAY); //Personal preference in color (to distinguish from the ball).
		add(ball, getWidth()/2.0 - BALL_RADIUS, getHeight()/2.0 - BALL_RADIUS);
	}
	
	//The next three messages are "technically" extensions, but I included them in base game.
	
	//This creates the starter/explanation message in the beginning of the game.
	private void startMessage() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); //ONLY FOR BEGINNING MESSAGE
		GLabel startLabel = new GLabel("Hello! Click to play Shana's Breakout!");
		startLabel.setFont(SCREEN_FONT);
		add(startLabel, (getWidth() - startLabel.getWidth())/2, getHeight()/2);
		waitForClick();
		removeAll();
	}
	//This creates the winner message at the end of the game.
	private void winnerMessage() {
		GLabel winnerLabel = new GLabel("Congratulations! You win! Click to play again!");
		winnerLabel.setFont(SCREEN_FONT);
		add(winnerLabel, (getWidth() - winnerLabel.getWidth())/2, getHeight()/2);
		waitForClick(); //NEXT 3 LINES ARE ADDED FOR REPLAY VALUE.
		removeAll();
		run();
	}
	
	//This creates the loser message at the end of the game.
	private void loserMessage() {
		GLabel loserLabel = new GLabel("Unfortunately, you lost. Click to play again!");
		loserLabel.setFont(SCREEN_FONT);
		add(loserLabel, (getWidth() - loserLabel.getWidth())/2, getHeight()/2); 
		waitForClick(); //NEXT 3 LINES ARE ADDED FOR REPLAY VALUE.
		removeAll();
		run();
	}
}
