/*
 * File: Breakout.java
 * -------------------
 * Name: Avery McCall
 * Section Leader: Cat Xu 
 * 
 * This program executes the game of Breakout. The screen is set with 100 bricks, 10 bricks in each row and 10 rows of bricks that change colors 
 * every two rows. There is a paddle at the bottom of the screen. When the player clicks their mouse, a ball is launched from the middle of the screen. 
 * The goal is for the ball to hit the paddle which will then send it upwards towards the bricks. When the ball collides with a brick, that brick is removed
 * from the screen. The ultimate goal is for the player to clear all of the bricks. The player has three chances to do this and loses a life every time the 
 * ball does not hit the paddle and disappears past the bottom of the screen. 
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

	//calculates how much space is between the rows of bricks and the wall 
	public static final double BRICK_X_OFFSET = (CANVAS_WIDTH - NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP))/2;
	
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
	public static final double DELAY = 750.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//instance variables for the paddle and ball 
	private GRect paddle = null;
	private GOval ball = null;
	
	//random generator to make the velocity be unpredictable and instance variables for the 
	//velocity in both directions
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy = VELOCITY_Y; 
	
	//instance variable that counts how many bricks are left in the game, initially starting at 100
	//and altering every time a brick is removed
	private int bricksLeft = 100;
	
	/*
	 *Sets the screen dimensions and runs the game Breakout. 
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//One method to set up the bricks and paddle. Another to start and play the game. 
		setUpGame();
		playGame();
	}

	/*
	 * This method establishes the ball in the game, generates a random horizontal velocity within the proper range and then commences play once the user
	 * has clicked the mouse. This is nested within a for loop that runs for the number of turns that the player is given. Once the mouse is clicked, 
	 * the ball is created and it enters the animation loop that moves it and proceeds to check for collisions (and responds to collisions appropriately in a 
	 * separate method). In the case that the ball goes below the bottom of the screen, it is removed and given a null value which stops the while loop 
	 * and and moves on to the next turn. If they lose all of their turns, the for loop ends and they are told they have lost the game. 
	 * In the case that the player eliminates all of the bricks and wins the game, the ball is also removed from the screen and given a null value, and 
	 * the player is told that they won the game. 
	 */
	private void playGame() {
		//gives the horizontal velocity random values. 
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		//for loop keeps track of how many turns the player uses   
		for (int i=0; i<NTURNS;i++) {
	
			//the ball is created and moved only when the user clicks the screen 
			addMouseListeners();
			waitForClick();
			if(bricksLeft!=0) {
				addBall();
			}
			
			//The animation loop for the ball. It moves it, pauses, and checks for collisions then ends according to how the user does. 
			while(ball!=null) {
				moveBall();
				pause(DELAY);
				
				//Check for collisions in a separate method and removes bricks or bounces off the paddle accordingly. 
				checkForCollisions();
				
				//ends the while loop and moves on to next turn if the ball falls off the screen 
				if(ball.getY() > getHeight() - 2*BALL_RADIUS) {
					remove(ball);
					ball=null;
				} 
				
				//Ends the while and for loop if the player has successfully removed all the bricks and tells the player they have won 
				if(bricksLeft ==0) {
					remove(ball);
					ball=null;
					youWinBanner(); 
				}
			}
		}
		
		//tells the player they have lost if they have used all of their turns 
		youLoseBanner();
	}

	/*
	 * This creates a centered banner telling the player they have lost. 
	 */
	private void youLoseBanner() {
		GLabel youLose = new GLabel ("YOU LOST");
		double labelX = (getWidth() - youLose.getWidth())/2;
		double labelY = (getHeight() + youLose.getAscent())/2;
		youLose.setLocation(labelX, labelY);
		add(youLose);
		
	}

	/*
	 * This creates a centered banner telling the player they have won. 
	 */
	private void youWinBanner() {
		GLabel youWin = new GLabel ("YOU WIN!");
		double labelX = (getWidth() - youWin.getWidth())/2;
		double labelY = (getHeight() + youWin.getAscent())/2;
		youWin.setLocation(labelX, labelY);
		add(youWin);
	}

	/*
	 * This creates a GObject called collider that is defined by the value that is returned from the function getCollidingObject. 
	 * If the collider is the paddle, the vertical velocity changes signs to bounce the ball off of it. If the collider is not the 
	 * paddle and is not null, it is a brick, thus the collider is removed from the screen and the number of bricks left goes down
	 * by one. The ball then reverses vertical directions. 
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy=-vy;
		}else if (collider !=null) {
			remove(collider);
			bricksLeft = bricksLeft-1; 
			vy=-vy;
		}
	}

	/*
	 * This checks the four corners of the ball for collisions after every movement and then returns the nature of the collision, 
	 * or lack of a collision, to the checkForCollisions method. 
	 */
	private GObject getCollidingObject() {
		//variables that track the x and y coordinates of the upper left "corner" of the ball. 
		double x = ball.getX();
		double y = ball.getY();
		
		//The for loop moves from one corner to the next of the ball checking if any elements are found there and defining these 
		//elements as the GObject "testing". It moves from corner to corner by first adding the ball's diameter to the 
		// y value and subsequently adding the ball's diameter to the x-value. If testing does not equal null, meaning the ball
		// has collided with something, it returns what that object is to the checkForCollisions method, otherwise it tells that 
		// method there has been no collision. 
		for (int i=0; i<2; i++) {
			for(int j=0; j<2; j++) {
				GObject testing = getElementAt(x+i*2*BALL_RADIUS, y+j*2*BALL_RADIUS);
				if (testing != null) {
					return testing;
				}
			}
		}
		return null;
	}
	
	/*
	 * This tells the ball how to move with every passage through the while loop. It ensures first that there is a ball to move, 
	 * and then tells the ball how to bounce off of the right and left walls (by reversing vx), or the top wall (by reversing vy). 
	 */
	private void moveBall() {
		if (ball != null) {
			ball.move(vx, vy);
			if(ball.getX() > getWidth()-2*BALL_RADIUS || ball.getX()<0) {
				vx=-vx;
			}
			if(ball.getY() <0) {
				vy=-vy;
			}
		}
	}

	/*
	 * This adds the ball to the center of the screen by GOval with the ball's dimensions and color. 
	 */
	private void addBall() {
		ball = new GOval (getWidth()/2-BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}
	
	/*
	 * This method consists of adding the bricks to the screen and adding the paddle to the screen so that it can be tracked by the mouse. 
	 */
	private void setUpGame() {
		addBricks();
		addPaddle();
	}

	/*
	 * This mouse event links the paddle to the movement of the mouse. A constant is created to track the updating location of the mouse and 
	 * link that X location to the X location of the paddle. The Y location of the paddle remains fixed. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX < getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mouseX, getHeight()-PADDLE_Y_OFFSET);
		}
	}
	
	/*
	 * This creates a GRect that becomes the paddle. It is initially put in the center of the width of the screen when the game first starts,
	 * and it later moves laterally with the mouse. 
	 */
	private void addPaddle() {
		//puts it on the center of the screen when the game first starts 
		paddle = new GRect ((getWidth()-PADDLE_WIDTH)/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/*
	 * This takes the input of what number row the bricks are being created in and returns the corresponding color for that row into the 
	 * addBricks function to create the proper rainbow order. 
	 */
	private Color createRainbow(int row) {
		if(row<2) {
			return(Color.RED);
		}
		if(row>1 && row<4) {
			return(Color.ORANGE);
		}
		if(row>3 && row<6) {
			return(Color.YELLOW);
		}
		if(row>5 && row<8) {
			return(Color.GREEN);
		
		//else statement ensure that it returns something for the only two rows that are left  	
		}else {
			return(Color.CYAN);
		}
	}
	
	/*
	 * This nested for loop creates 10 rows of bricks with 10 bricks in each row, changing the color of the bricks every two rows. 
	 * The bricks are spaced evenly and evenly offset from the wall to make them symmetrical. 
	 */
	private void addBricks() {
		//row = the number of rows that are being created and impacts to the vertical (y) location of the start of each brick
		for(int row=0; row<NBRICK_ROWS; row++) {
		
			//col = the number of bricks in each row and impacts the hortizontal (x) location of the start of each brick. 
			for(int col=0; col<NBRICK_COLUMNS; col++) {
				
				//The x-location moves over the value of the brick width and the separation between each brick for every new brick created because 
				// it is multiplied by col (the number of bricks currently in the row). It is also moved over the necessary BRICK_X_OFFSET to ensure
				// the bricks are evenly spaced from the left and right walls. 
				// The y-location follows a similar principle, but instead in a vertical manner. 
				//Then the brick dimensions are set. 
				GRect brick = new GRect(BRICK_X_OFFSET + col*(BRICK_WIDTH+BRICK_SEP), BRICK_Y_OFFSET + row*(BRICK_HEIGHT+BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				
				//The bricks are set to be filled and the creation of the rainbow pattern is decomposed into the createRainbow function which returns the 
				//proper color for each row. 
				brick.setFilled(true);
				brick.setColor(createRainbow(row));
				add(brick);
			}
		}
	}
}
