/*
 * File: Breakout.java
 * -------------------
 * Name: Jassi Pannu
 * Section Leader: Luciano Gonzalez
 * 
 * This file implements an extended version of Breakout. 
 * It includes sounds, instructions, and a lives counter. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_extension extends GraphicsProgram {

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 10.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// MY CONSTANTS ------------------------------------------
	
	// The paddle for the game with initial placement 0,0
	private static final GRect PADDLE  = new GRect(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	// The bricks of the game. 
	private GRect brick = null; 
	
	// Bouncing ball velocities 
	private double vx; 
	private double vy; 
	
	// Randomgen accessible from anywhere in the program. 
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	
	// A message the user sees that keeps track of their remaining lives
	private GLabel lives = null;

	// Audioclip extension 
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// MY CODE ------------------------------------------
				
		createGameSetup(); 
		playGame(); 
		
	}

	
	/**
	 * Method: Create Game Setup
	 * -----------------------
	 * Returns void.
	 * This method creates the standard specified setup for the game Breakout.
	 * 
	 */
	private void createGameSetup() {
		
		makeBricks(); 
		makePaddle(); 
		addMouseListeners(); 
		
	}

	
	/**
	 * Method: Makes Bricks
	 * -----------------------
	 * Returns void.
	 * This method builds the bricks for the game. 
	 * The number of bricks created is specified by the constants NBRICK_ROWS
	 * and NBRICK_COLUMNS. The spacing is specified by BRICK_SEP and the offset 
	 * from the top of the screen is specified by BRICK_Y_OFFSET. 
	 * 
	 */
	private void makeBricks() {
		
		double currentRow = NBRICK_ROWS; 
		double placeY = (BRICK_Y_OFFSET); //we start building the bricks from the top row 
		
		for (;currentRow>0;currentRow=currentRow-1) {
			printRow(currentRow, placeY); 
			placeY=placeY+BRICK_SEP+BRICK_HEIGHT;
		}

	}


	/**
	 * Method: Print Row of Bricks
	 * -----------------------
	 * Returns void.
	 * This is a sub-method that builds the required number of bricks 
	 * row-by-row. Each row consists of filled rectangles. 
	 * The rows are initially set to be colored in rainbow shades, with 
	 * red at the top and cyan at the bottom.  
	 * 
	 */
	private void printRow(double currentRow, double placeY) {

		//Center the row in the middle of the screen 
		int screenWidth = getWidth(); 
		double tileRowLength = (NBRICK_ROWS*(BRICK_WIDTH+BRICK_SEP))-BRICK_SEP;
		int currentCol = NBRICK_COLUMNS; 
		double placeX = (0.5*screenWidth - 0.5*tileRowLength); 
		
		//Set every 2 rows to a different rainbow color 
		//Note this is specified to work with 10 rows only 
		Color fillColor = null; 		
		if (currentRow == NBRICK_ROWS || currentRow == NBRICK_ROWS-1) {
			fillColor = Color.RED; 
		} else if (currentRow == NBRICK_ROWS-2 || currentRow == NBRICK_ROWS-3) {
			fillColor = Color.ORANGE; 
		} else if (currentRow == NBRICK_ROWS-4 || currentRow == NBRICK_ROWS-5) {
			fillColor = Color.YELLOW; 
		} else if (currentRow == NBRICK_ROWS-6 || currentRow == NBRICK_ROWS-7) {
			fillColor = Color.GREEN; 
		} else if (currentRow == NBRICK_ROWS-8 || currentRow == NBRICK_ROWS-9) {
			fillColor = Color.CYAN; 
		}
		
		
		for (; currentCol>0; currentCol=currentCol-1) {

			brick = new GRect(placeX, placeY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true); 
			brick.setFillColor(fillColor);
			add(brick); 
			placeX=placeX+BRICK_SEP+BRICK_WIDTH; 
			 
		}
		
	}
	
	
	/**
	 * Method: Make Paddle
	 * -----------------------
	 * Returns void.
	 * This is a method that creates a rectangular shaped paddle of dimensions 
	 * PADDLE_WIDTH and PADDLE_HEIGHT and places it at PADDLE_Y_OFFSET pixels from the 
	 * top of the screen. 
	 * The paddle will be colored black. 
	 * 
	 */
	private void makePaddle() {
		// TODO Auto-generated method stub
	
		PADDLE.setFilled(true); 
		PADDLE.setColor(Color.BLACK);
		add(PADDLE, getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET); 
		
	}

	
	/**
	 * Method: Mouse Moved
	 * -----------------------
	 * Returns void.
	 * This method tracks mouse movement and aligns the paddle with the mouse 
	 * along the x axis. The y axis coordinate of the paddle is fixed. 
	 * 
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		PADDLE.setLocation(x, y);
		
	}

	/**
	 * Method: Make Ball
	 * -----------------------
	 * Returns GOval corresponding to ball.
	 * This method creates the bouncing ball for the game. 
	 * The ball is size BALL_RADIUS and colored black. 
	 * 
	 * It starts in the middle of the screen and remains static, in place
	 * until the player clicks the mouse. 
	 * 
	 */

	private GOval makeBall() {
		// TODO Auto-generated method stub
		
		double size = BALL_RADIUS * 2;
		GOval r = new GOval(size, size);
		r.setFilled(true);
		r.setColor(Color.BLACK);
		add(r, getCenterX()-BALL_RADIUS, getCenterY()-BALL_RADIUS);

		return r;
	}


	/**
	 * Method: Play Game
	 * -----------------------
	 * Returns void.
	 * This is the main animation method for the game. 
	 * It creates a loop that will continue as long as the player has lives remaining. 
	 * During the animation the ball will bounce around the screen, off of walls, 
	 * bricks and the paddle, but it will not interact with messages on the screen. 
	 * 
	 * Any bricks that collide with the ball will be removed from the screen. 
	 * 
	 * Lives are lost when the ball collides with the bottom wall of the screen. 
	 * 
	 * EXTENSION INFO: 
	 * Messages informing the player of instructions, if they won/lost, and lives 
	 * remaining have been added. 
	 */
	private void playGame() {

		
		//This creates the bouncing ball, 
		//and launches it in at a random speed/direction. 
		//vx and vy are previously declared instance variables. 
		GOval ball = makeBall(); 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if (rgen.nextBoolean(0.5)) vx=-vx; 
		vy = VELOCITY_Y; 
		
		
		//This creates a centered label that is used to display 
		//initial instructions "Click to begin."
		GLabel label = new GLabel("");
		label.setFont("Courier-24");
		label.setColor(Color.BLUE);
		label.setLabel("Click to begin.");
		add(label, getCenterX()-(label.getWidth()/2), getCenterY()+2*label.getHeight());
		
		//Keeps track of turns remaining and bricks remaining. 
		int turnsRem = NTURNS; 
		int bricksRem = NBRICK_COLUMNS*NBRICK_ROWS; 
		
		//Creates a message that displays the player's remaining lives/turns. 
		lives = new GLabel("");
		lives.setFont("Courier-14");
		lives.setColor(Color.BLACK);
		lives.setLabel("Lives remaining:" + NTURNS);
		add(lives, getCenterX()-(lives.getWidth()/2), lives.getHeight());
			
		//Waits for the player to click before beginning animation. 
		waitForClick();
		
		//Instruction message is removed. 
		remove(label); 
		
		
		//Main animation loop: continues until all lives are used. 
		while(turnsRem>0) {

			// Update velocity when bounces are made. 
			//EXTENSION: audio clip of bounce plays during a bounce. 
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				bounceClip.play(); 
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				bounceClip.play(); 
				vy = -vy;
			}
			if(hitPaddle(ball)) {
				bounceClip.play(); 
				vy = -vy; 
			}
			if(hitBrick(ball)) { 
				bounceClip.play(); 
				vy = -vy; 
				bricksRem=bricksRem-1; 
			
			}
			if(hitBottomWall(ball)) {
				//Hitting the bottom wall is a special case. 
				//One of the players "lives" will be removed.
				bounceClip.play(); 
				turnsRem=turnsRem-1;
				lives.setLabel("Lives remaining:" + turnsRem);
				
				//The ball will be removed and replaced in the center of the screen. 
				remove(ball); 
				add(ball, getCenterX()-BALL_RADIUS, getCenterY()-BALL_RADIUS); 
				

				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
				if (rgen.nextBoolean(0.5)) vx=-vx; 
				vy = VELOCITY_Y; 	
				
			}
			
			
			//If all bricks have been broken, displays the message "You won!"
			if(bricksRem==0) {
				label.setLabel("You won!");
				add(label, getCenterX()-(label.getWidth()/2), getCenterY()+2*label.getHeight());
				break;
			}
			
			// Update visualization
			ball.move(vx, vy);

			// Pause
			pause(DELAY);
		}
		
		//If there are no lives remaining, informs the player they have lost. 
		if (turnsRem==0) {
			label.setLabel("You lost. Try again!");
			add(label, getCenterX()-(label.getWidth()/2), getCenterY()+2*label.getHeight());

		}
		
	}


	/**
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	
	/**
	 * Method: Hit Paddle
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the paddle.
	 */
	private boolean hitPaddle(GOval ball) {
		double paddleXMin = PADDLE.getX(); 
		double paddleXMax = PADDLE.getX() + PADDLE_WIDTH; 
		
		if ((ball.getY()+BALL_RADIUS*2) >= PADDLE.getY()) { //check Y coordinate range
			if( paddleXMin < ball.getX() && ball.getX() < paddleXMax) { //check X coordinate range 
				return true; 
			}
		} return false;
	}
	

	/**
	 * Method: Hit Brick
	 * -----------------------
	 * Returns whether or not the given ball should bounce off 
	 * of a brick and remove that brick. 
	 */
	private boolean hitBrick(GOval ball) {
		// TODO Auto-generated method stub
		
		GObject obj = getElementAt(ball.getX(), ball.getY()); //top left corner of ball 
		GObject obj2 = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()); //top right corner of ball 
		GObject obj3 = getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2); //bottom left corner 
		GObject obj4 = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2); //bottom right corner 
		
		//If an object is found at any of the ball's corners, 
		//and if that object is not a paddle or a lives counter message, 
		//will return true. 
		if(obj != null && obj!=PADDLE && obj!=lives) {
			remove(obj);
			return true; 
		} else if(obj2 != null && obj2!=PADDLE && obj2!=lives) {
			remove(obj2);
			return true; 
		} else if(obj3 != null && obj3!=PADDLE && obj3!=lives) {
			remove(obj3);
			return true; 
		} else if(obj4 != null && obj4!=PADDLE && obj4!=lives) {
			remove(obj4);
			return true; 
		} 
		return false;
	}

	
}
