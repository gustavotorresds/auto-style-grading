/*
 * File: Breakout.java

 * -------------------
 * Name: Madeleine Chang
 * Section Leader: Maggie Davis
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

	// Offset of top-left brick from the left side of the canvas, in pixels. 
	//Effectively centers the bricks on the canvas (thinking about the negative space!).
	public static final double BRICK_X_OFFSET = (CANVAS_WIDTH-((BRICK_WIDTH * NBRICK_COLUMNS) + (BRICK_SEP * 9)))/2;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	//Instance variable for the paddle to be tracked
	private GRect paddle = null;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	//Instance variable for the ball 
	private GOval ball = null;

	//Instance variable for the ball's velocity 
	private double vx, vy;

	//Instance variable to generate a random number, which will serve as the ball's initial velocity 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variable for total number of bricks
	private int bricksTotal = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 120.0;

	// Number of turns 
	public static final int NTURNS = 3;
		
	//Instance variable for lives 
	private int lives = NTURNS;
	
	public void run() {
		setUp();
		playTheGame();
	}


	public void setUp() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		makeBricks();
		makePaddle();
		addMouseListeners();
	}

	
	/*
	 * Method: Mouse Moved
	 * Called when mouse moves. Moves paddle across x-axis. 
	 */
	public void mouseMoved (MouseEvent e) {
		if (e.getX() < getWidth()-PADDLE_WIDTH) {
			double px = e.getX();
			double py = getHeight()-PADDLE_Y_OFFSET;
			paddle.setLocation(px,py);
		}
	}	


	/*
	 * Method: makePaddle
	 * Makes the little black paddle and add it to the bottom of the screen, and adds to the center bottom. 
	 */
	private void makePaddle() {
		double x = (getWidth()-PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		paddle.setLocation(x,y);
		add(paddle);
	}

	/*
	 * Method: makeBricks
	 * Makes the whole chunk of bricks at the top of the screen.
	 */
	private void makeBricks() {
		for(int r = 0; r < NBRICK_ROWS; r++) {
			for(int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (c * (BRICK_WIDTH + BRICK_SEP)) + BRICK_X_OFFSET;
				double y = (r * (BRICK_HEIGHT+ BRICK_SEP)) + BRICK_Y_OFFSET; 
				GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				Color brick_color = chooseTheColor(r);
				rect.setColor(brick_color);	
				add (rect, x, y);
			}
		}
	}
	/*
	 * Method: chooseTheColor
	 * Sets the appropriate color of the row. Takes in the parameter "r" (which row are we on) and returns a color to the color variable "brick_color."
	 * I realize setting the r to specific values is not the most legit way to do it, but tried to use the mod for some of them!
	 */
	private Color chooseTheColor(int r) {
		Color brick_color = Color.RED;
		if (r == 0 || r==1) {
			brick_color = Color.RED;
		}
		else if (r == 2 || r== 3) {
			brick_color = Color.ORANGE;
		}
		else if (r==4 || r%5==0) {
			brick_color = Color.YELLOW;
		}
		else if (r%5==1 || r%5==2) {
			brick_color = Color.GREEN;
		}
		else if (r%5==3 || r%5==4) {
			brick_color = Color.CYAN;
		}
		return brick_color;
	}	


	/*
	 * 
	 * ALERT ALERT NOW GOING TO CALL A NEW PUB VOID TO PLAY THE GAME 
	 * 
	 */


	public void playTheGame() {
		makeBall();
		moveTheBall();	
	}

	/*
	 * Method:	moveTheBall
	 * Makes the ball move from the center of screen in a randomly generated direction. 
	 * The animation loop that checks for walls was inspired by the "Bouncing Ball" problem we did in lecture.
	 */
	private void moveTheBall() {
		double vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = 3;

		waitForClick();
		while (true) {
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
			
			//checks for walls
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx; //reverses direction 
			}
			if(hitTopWall(ball)) {
				vy=-vy; // reverses direction 
			}
			
			//checks for collision 
			GObject collider = getCollidingObject();
			if(collider != null) {
				if (collider == paddle) {
					vy = -1*(Math.abs(vy)); // accounts for the sticky paddle issue!!
					bounceClip.play();
				}
				
				else {
					vy=-vy; 
					bounceClip.play();
					remove(collider);
					bricksTotal = bricksTotal-1;
				}
			}
			
			//losing conditions
			 if (hitBottomWall(ball)) {
				 lives = lives-1;
				 remove(ball);
				 makeBall();
				 waitForClick();
			 }
			 
			 if (lives == 0) {
				 addGameOverLabel();
				 remove(ball);
				 return;
			 }
			
			 //winning conditions 
			 if (bricksTotal == 0) {
				 addWonGameLabel();
				 addWonGamePicture();
				 remove(ball);
				 return;
			 }
			 
			//update world
			ball.move(vx,vy);
			//pause
			pause(DELAY);
		}	
	}
	
/*
 * Method: addWonGmePicture 
 * Adds a picture of a cute cat with glasses along with the GLabel telling the player they have won the game.	
 */
	private void addWonGamePicture() {
		GImage img = new GImage("cat.jpg");
		add(img, getWidth()/2-img.getWidth()/2, getHeight()/2+BRICK_HEIGHT*3);
	}


/*
 * Method: addWonGameLabel 
 * Makes a label when you win the game!!! 
 */
	private void addWonGameLabel() {
		GLabel label = new GLabel("Congrats, friend! You won!!!!");
		label.setFont("Courier-16");
		label.setColor(Color.BLACK);
		add(label, getWidth()/2-label.getWidth()/2, getHeight()/2);	
	}
	
/*
 * Method: addGameOverLabel
 * Makes a label when you lose. 
 */
	private void addGameOverLabel(){
		GLabel label = new GLabel("Sorry friend, game over!");
		label.setFont("Courier-16");
		label.setColor(Color.BLACK);
		add(label, getWidth()/2-label.getWidth()/2, getHeight()/2);
	}

/*
 * Method: getCollidingObject
 * Tests to see if there is an GObject at any of the four corners of the ball as it bounces around the screen. 
 * There probably is a more concise way to do this, but for now it gets the job done! 
 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY()); 
		if (collider !=null) {
			return(collider);
		}
		collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if (collider !=null) {
			return(collider);
		}
		collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS); 
		if (collider !=null) {
			return(collider);
		}	
		collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);	
		if (collider !=null) {
			return(collider);
		}	
		return(null);
	}

	/*
	 * Method: hitBottomWall
	 * Let's us know if the ball has hit the floor. 
	 * Returns whether or not the ball needs to switch directions accordingly. 
	 * Later will tell us if we have to lose a life. 	
	 * Will change the test above in the while loop to say: if hit bottom wall, then lose a life. 
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - BALL_RADIUS*2;
	}

	/*
	 * Method: hitTopWall
	 * Let's us know if the ball has hit the ceiling. 
	 * Returns whether or not the ball needs to switch directions accordingly.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Method: hitRightWall
	 * Let's us know if the ball has hit the right wall, as measured by the width of the screen.
	 * Returns whether or not the ball needs to switch directions accordingly.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >=getWidth() - BALL_RADIUS*2;
	}

	/*
	 * Method: hitLeftWall
	 * Let's us know if the ball has hit the left wall. Returns whether or not the ball needs to switch directions accordingly.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	
	/*Method: makeBall
	 * Make ball makes the little black ball and adds it to the center of the screen.
	 */
	private void makeBall() {
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS;
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setLocation(x,y);
		ball.setFilled(true);
		ball.setColor(Color.MAGENTA);
		add(ball);
	}

}





