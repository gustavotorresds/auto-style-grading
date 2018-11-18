/*
 * File: Breakout.java
 * -------------------
 * Name: Kara Eng
 * Section Leader: Ben Allen
 * 
 * This file will eventually implement the game of Breakout.Breakout is a game where a ball is bounced around using a paddle. 
 * every time it hits a brick it deletes it and your goal is to get rid of all the bricks without dropping the ball. in this program
 * you have to click once to get through the instructions and then click again to get the ball going. you can hit the space bar to 
 * try again if you dropped the ball, but only if you have lives left. the ball gets reset to the middle and automatically goes again. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
//TODO: make it so that if it hits the label IT DOES NOTHING 
public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks, can edit it and it will just cycle through colors 
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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//gets the width of the row, including spacing on edges and in between 
	//then centers it in the console 
	private double initialX = CANVAS_WIDTH/2 - BRICK_SEP*(NBRICK_COLUMNS-1)/2 - BRICK_WIDTH*NBRICK_COLUMNS/2;
	//makes the first row like six bricks down 
	private double initialY = BRICK_HEIGHT * 6; 
	//making this an instance variable allows mouselistener to edit the paddle's location 
	private GRect paddle;
	//making this an instance variable allows you to see how it interacts with other objects in this game
	private GOval ball; 
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	
	//velocity of x 
	private double vx; 
	//velocity of y 
	private double vy = 4; 
	
	//number of lives left 
	private int livesLeft = 3;
	private GLabel label; 
	
	//counts how many bricks you had left before you lost 
	private double bricksKilled = 0; 
	
	//this boolean is so that you only take off one life everytime it touches the bottom 
	//because your ball continues to move below the getHeight() even after you take off a life
	//so the program would want to take off as many lives as the ball moved, which we don't want 
	private static boolean penalized = false; 
			
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//displays how many lives you have left 
		displayInstructions();
		showLives();
		setUpBricks();
		addKeyListeners();
		addMouseListeners();
		createPaddle();
		createBall();
		moveBall();
	}
	public void keyTyped (KeyEvent k) {
		if (k.getKeyChar() == KeyEvent.VK_SPACE && livesLeft>0) {
			ball.setLocation(getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2);
			//makes it so the player can lose a life again because they're back to trying again 
			penalized = false; 
		}
	}
	
	//cleans up all the bricks 
	private void cleanUp() {
		for (double i = 0; i < getWidth(); i++) {
			for (double j = 0; j < initialY; j++) {
				GRect dyingRect = getElementAt(i, j); 
				if (dyingRect != null) {
					remove(dyingRect);
				}
			}
		}
	}
	private void displayInstructions() {
		GLabel label1 = new GLabel ("Click to start the game.");
		label1.setLocation(getWidth()/2-label1.getWidth()/2, getHeight()/2 + label1.getAscent()/2);
		add(label1); 
		GLabel label2 = new GLabel ("Press the space bar to try again.");
		label2.setLocation(getWidth()/2-label2.getWidth()/2, getHeight()/2 + label2.getAscent()/2+ label1.getAscent());
		add(label2); 
		waitForClick();
		remove(label1); 
		remove(label2);
	}
	//TODO: figure out how to make it so the paddle can handle it when you go faster
	//TODO: figure out how to reset everything on the screen once you lose 
	
	private void showLives() {
		label = new GLabel("Lives Left: " + livesLeft);
		label.setFont("Courier-14");
		label.setColor(Color.BLUE);
		
		// add the label to an arbitrary place on the screen 
		add(label, BRICK_WIDTH, getHeight() - label.getAscent()/2);	
	}

	//this is a separate method that way when you lose and try again, you just move the ball back to the stop 
	private void moveBall() {
		waitForClick();
		ball.setLocation(getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2);
		
		//how fast it moves side to side 
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		//how fast it moves downward
		//need to fix it because it can't handle it when you go faster than 3.0 because it doesn't recognize the paddle 
		while (true) {
			ball.move(vx, vy);
			bounceBack();
			killBricks();
			pause(DELAY); 
			if (livesLeft == 0) {
				cleanUp(); 
				GLabel label5 = new GLabel ("Sorry you lost. You got " + bricksKilled + " bricks.");
				label5.setLocation(getWidth()/2-label5.getWidth()/2, getHeight()/2 + label5.getAscent()/2);
				add(label5); 
				
				break; 
			}
			if (bricksKilled == (NBRICK_ROWS * NBRICK_COLUMNS)) {
				GLabel label6 = new GLabel ("Congratulations! You won!");
				label6.setLocation(getWidth()/2-label6.getWidth()/2, getHeight()/2 + label6.getAscent()/2);
				add(label6); 
				break; 
			}
		}
	}
	
	//this creates the ball
	private void createBall() {
		//places ball in the middle of the screen
		ball = new GOval (getWidth()/2 - BALL_RADIUS*2, CANVAS_HEIGHT/2 - BALL_RADIUS*2, BALL_RADIUS*2, BALL_RADIUS*2); 
		ball.setFilled(true);
		add(ball); 
	}
	
	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		//checks first corner of ball 
		GObject mayhapsBrick = getElementAt(ballX, ballY);
		//checks top right corner of ball 
		if (mayhapsBrick == null) {
			mayhapsBrick = getElementAt(ballX+BALL_RADIUS*2, ballY);
		}
		//checks bottom right corner
		if (mayhapsBrick == null) {
			mayhapsBrick = getElementAt(ballX+BALL_RADIUS*2, ballY + BALL_RADIUS*2);
		}
		//checks bottom left corner
		if (mayhapsBrick == null) {
			mayhapsBrick = getElementAt(ballX, ballY + BALL_RADIUS*2);
		}
		//makes sure its not the paddle
		if (mayhapsBrick == paddle || mayhapsBrick == label) {
			return null; 
		}
		return mayhapsBrick; 
	}
	
	//takes out the bricks if you hit em 
	private void killBricks() {		
		//sees if there's anything there and makes sure it's not the paddle 
		if (getCollidingObject() != null) {
			remove(getCollidingObject());
			bricksKilled++; 
		}
		//checks to see if you got all of the bricks! 
		
		}

	
	//creates the paddle and adds it to the screen 
	private void createPaddle() {
		paddle = new GRect (getWidth()/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle); 
	}
	
	//method that makes ball bounce every time it runs into something
	//and deducts a life if you hit the bottom 
	private void bounceBack() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		double ballX2 = ball.getX() + BALL_RADIUS*2;

		//makes it bounce the other way when it hits a wall on the sides 
		if (ballX >= getWidth() - BALL_RADIUS*2 || ballX <=0) {
			vx = vx*-1;
		}
		//makes it bounce the other way when it hits the top wall 
		//Note: not else if statements in case you hit a corner 
		if (ballY <= 0) {
			vy = -vy;
		}
		//BUG: lives don't decrease once you pass vy = 4 because ballY never equals getHeight based on how much the ball moves
		//by, however couldn't figure out a way to fix that because the ball continues to move below getHeight even after you take off 
		//a life
		if (!penalized) {
			if (ballY + BALL_RADIUS*2 >= getHeight()) {
				livesLeft--;
				penalized = true; 
			}
		}
		//if the bottom of the ball hits the top of the paddle
		//and if ballX is in between paddles edges
		if (ballY + BALL_RADIUS*2 <= paddle.getY() +vy && ballY + BALL_RADIUS*2 >= paddle.getY() 
				&& (ballX >= paddle.getX() && ballX <= paddle.getX() + PADDLE_WIDTH)) {
			vy = -Math.abs(vy);
		}
		//checks to see if right side of the ball hit the paddle or not 
		else if (ballY + BALL_RADIUS*2 <= paddle.getY() +vy && ballY + BALL_RADIUS*2 >= paddle.getY() 
				&& (ballX2 >= paddle.getX() && ballX2 <= paddle.getX() + PADDLE_WIDTH)) {
			vy = -Math.abs(vy);
		}
		if (ballY <= paddle.getY() +vy && ballY >= paddle.getY() 
				&& (ballX >= paddle.getX() && ballX <= paddle.getX() + PADDLE_WIDTH)) {
			vy = -Math.abs(vy);
		}
		//checks to see if right side of the ball hit the paddle or not 
		else if (ballY <= paddle.getY() +vy && ballY >= paddle.getY() 
				&& (ballX2 >= paddle.getX() && ballX2 <= paddle.getX() + PADDLE_WIDTH)) {
			vy = -Math.abs(vy);
		}
		
		
		if (getCollidingObject() != null) {
			//this one is so that you bounce off of bricks too 
			double deadBrickY = getCollidingObject().getY();
			//has to check if you hit top or bottom
			if (deadBrickY <= ballY || deadBrickY + BRICK_HEIGHT >=ballY) {
				vy = -vy;
			}
		}
		
	}
	
	//this method makes it so that the paddle follows your mouse's x coordinates 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		//makes sure that the paddle stays in the console 
		if (mouseX>=0 && mouseX + PADDLE_WIDTH <= getWidth()) {
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
		}
		//makes sure that lives left display is up to date 
		label.setLabel("Lives left: " +livesLeft);
	}
	
	//sets up all the bricks, if you want more than ten bricks, it'll cycle through the colors again 
	private void setUpBricks() {
		for (int c = 1; c<=NBRICK_ROWS; c++) {
			buildRows(c); 
		}
		
	}
	//method that builds the row and sets the color. 
	//this also edits intialY so that it will be set up for the next rows 
	private void buildRows(int c) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			//this is so that it cycle through it again if you have more than ten rows of bricks 
			while (c>10) {
				c = c%10; 
			}
			double spacing = BRICK_WIDTH + BRICK_SEP; 
			GRect rect = new GRect (initialX + i*spacing, initialY, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			if (c == 1 || c == 2) {
				rect.setColor(Color.RED);
			} else if (c == 3 || c == 4) {
				rect.setColor(Color.ORANGE);
			} else if (c == 5 || c == 6) {
				rect.setColor(Color.YELLOW);
			} else if (c == 7 || c == 8) {
				rect.setColor(Color.GREEN);
				//this one includes c == 0 for when it equals 20! 
			} else if (c == 9 || c == 10 || c == 0){
				rect.setColor(Color.cyan);
			}
			add(rect); 
		}
		initialY = initialY + BRICK_HEIGHT + BRICK_SEP;
	}
}
