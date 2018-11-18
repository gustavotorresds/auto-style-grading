/*
 * File: Breakout.java
 * -------------------
 * Name:Eglen Galindo
 * Section Leader:Garrick Fernandez
 * 
 * This file will eventually implement the game of Breakout. The game allows the user
 * to move the paddle using their mouse in oder to keep the ball in play. Once the ball
 * touches a brick it breaks it and bounces back in the opposite direction. If the ball
 * crosses the lower boundary a life is used. The player has a total of three lives available
 * until the "GAME OVER!" message displays. 
 * 
 * Extension adds sound when a brick is broken while playing the game.
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
	
	/*
	 * Instance Variables
	 * ----------------
	 * The following instance variables were added so that they can be accessed by different
	 * methods in the program. They can't be kept within  a method with the program written.
	 */
	
	private GRect paddle = new GRect((CANVAS_WIDTH + PADDLE_WIDTH) / 2, 
			CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	private GOval ball = new GOval((CANVAS_WIDTH / 2) - BALL_RADIUS, (CANVAS_HEIGHT / 2) - BALL_RADIUS,
			2 * BALL_RADIUS, 2 * BALL_RADIUS);
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int BRICK_COUNTER = 0;
	private int NUMBER_BRICKS = (NBRICK_ROWS * NBRICK_COLUMNS);
	private int LOSS = 0;
	private GLabel LIVES_LEFT = new GLabel(""); //Left empty so that methods can add messages
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au"); //EXTENSION
	
	
	public void run() {
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpGame(); //Sets up bricks and ball on canvas.
		for(int i = 0 ; i < NTURNS; i++) { //Repeats the play game for NTURNS times
			playGame(); // MEthod to actually play the game in animation
		}
	}
	
	 /*
	  *  Method: SetUpGame
	  * ----------------
	  * Method includes the methods that create the bricks with it's different colors
	  * and positions on the canvas. It adds the ball that will be used in play and 
	  * adds the mouse listeners that allow for mouse use. 
	 */
	private void setUpGame() {
		createRows();
		addBall();
		addMouseListeners();
	}
	
	/*
	  *  Method: SetUpGame
	  * ----------------
	  * Method includes the methods that create the bricks with it's different colors
	  * and positions on the canvas. It adds the ball that will be used in play and 
	  * adds the mouse listeners that allow for mouse use. 
	 */
	private void createRows() {
		for (int n = 0; n < NBRICK_COLUMNS; n++) {
			for(int i = 0; i < NBRICK_ROWS; i++ ) {
				double initialX = ((CANVAS_WIDTH / 2) - ((NBRICK_ROWS /2) * (BRICK_WIDTH + BRICK_SEP) - (BRICK_SEP / 2)));
				GRect brick = new GRect(initialX + (i * (BRICK_WIDTH + BRICK_SEP)), 
						BRICK_Y_OFFSET + (n * (BRICK_HEIGHT + BRICK_SEP)), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				// n keeps track of the row and assigning color from the top
				if (n == 0 || n == 1) { // First and second row are red
					brick.setColor(Color.RED);
				} else if (n == 2 || n == 3) { //Third and fourth rows are orange
					brick.setColor(Color.ORANGE);
				} else if(n == 4 || n == 5) { // Fifth and sixth rows are yellow
					brick.setColor(Color.YELLOW);
				} else if(n == 6 || n == 7) { // Seventh and eight rows are green
					brick.setColor(Color.GREEN);
				} else if (n == 8 || n== 9) { // Ninth and tenth rows are cyan
					brick.setColor(Color.CYAN);
				}
				add(brick); // Brick to canvas
			}
		}
		
	}

	/*
	 * Method: addBall
	 * ----------------
	 * Places the ball created earlier as an instance variable in the position where the
	 * game begins. This works when the user has lost a life but still has more lives left
	 * to play the game. Constants for ball dimensions are used. 
	 */
	private void addBall() {
		ball.setLocation((CANVAS_WIDTH / 2) - BALL_RADIUS, (CANVAS_HEIGHT / 2) - BALL_RADIUS);
		ball.setFilled(true); //Will fill black.
		add(ball);
	}

	/*
	 * Method: mouseMoved
	 * ----------------
	 * Allows the paddle to be added to the screen and to follow the mouse's x position
	 * in oder for the user to interact with the screen. Also keeps the mouse from going
	 * out of bounds by limiting how far it can go on the X axis. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if((PADDLE_WIDTH / 2) <= mouseX && mouseX <= (CANVAS_WIDTH - (PADDLE_WIDTH /2))) {
			paddle.setLocation(mouseX - (PADDLE_WIDTH /2), CANVAS_HEIGHT - PADDLE_Y_OFFSET);
			paddle.setFilled(true); // Paddle is black
			add(paddle);
		}
	}
	
	private void playGame() {
		addMouseListeners();
		waitForClick();
		
		vx = rgen.nextDouble(1.0, 3.0); // Gives a random X direction for the ball to start game
		vy = VELOCITY_Y; // Y velocity determined by given constant
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		while(true) {
			remove(LIVES_LEFT); // This line ensure that the GLabel added at the end of game is removed
			ball.move(vx, vy);
			pause(DELAY); // pause in between loops
	
			if(hitLeftWall(ball) || hitRightWall(ball)) { // Change X direction when ball hits side walls
					vx = -vx;
			}
			if(hitTopWall(ball)) { // Change Y direction when ball hits top wall
					vy = -vy;
			}
			if(hitBottomWall(ball)) {
				LOSS++; // Keeps track of how many time the player has lost
				if(LOSS < NTURNS) { // Label letting player know how many lives they have left
					LIVES_LEFT = new GLabel((NTURNS - LOSS) +" lives left!");
					LIVES_LEFT.setFont("Courier-50");
					add(LIVES_LEFT, (CANVAS_WIDTH - LIVES_LEFT.getWidth()) / 2, 
							(CANVAS_HEIGHT - LIVES_LEFT.getAscent()) / 2 );
					addBall();
				} else { //Label added when player losses and no more lives are left
					LIVES_LEFT = new GLabel("GAME OVER!");
					LIVES_LEFT.setFont("Courier-50");
					add(LIVES_LEFT, (CANVAS_WIDTH - LIVES_LEFT.getWidth()) / 2, 
							(CANVAS_HEIGHT - LIVES_LEFT.getAscent()) / 2 );
				} 
				break; // Exit loop
			}
			
			GObject collider = getCollidingObject();
			if (collider != null) { //Checks if there is an object actually colliding with the ba;;
				checkCollidingObject(collider);
			}
			
			if(BRICK_COUNTER == NUMBER_BRICKS) { // Displays winning message if brick counter reaches 100
				GLabel win = new GLabel("YOU'VE WON!!!");
				win.setFont("Courier-50");
				add(win, (CANVAS_WIDTH - win.getWidth()) / 2, 
						(CANVAS_HEIGHT - win.getAscent()) / 2 );
				break;//Exit loop
			}
		}
	}


	/*
	 * Method: getCollidingObject
	 * ----------------
	 * Keeps track of the four corners of the back at all times and checks if the are 
	 * colliding with a GObject that must be removed in the case of a brick or not in 
	 * the case of the paddle.
	 */
	private GObject getCollidingObject() {
		// X and Y coordinates relevant to the ball made constants
		double leftBallX = ball.getX();
		double upperBallY = ball.getY();
		double rightBallX = ball.getX() + (2 * BALL_RADIUS);
		double lowerBallY = ball.getY() +  (2 * BALL_RADIUS);

		//If booleans bellow check if the object the ball is touching is not null
		// and is so return the object.
		GObject upperLeft = getElementAt(leftBallX, upperBallY);
		if(upperLeft != null) {
			return upperLeft;
		}
		
		GObject lowerLeft = getElementAt(leftBallX, lowerBallY);
		if(lowerLeft != null) {
			return lowerLeft;
		}
		
		GObject upperRight = getElementAt(rightBallX, upperBallY);
		if(upperRight != null) {
			return upperRight;
		}
		
		GObject lowerRight = getElementAt(rightBallX, lowerBallY);
		if(lowerRight != null) {
			return lowerRight;
		}
		return null; // If the ball is hitting nothing the method will return null
		
	}

	/*
	 * Methods: hitBottomWall, hitTopWall, hitRightWall, hitLeftWall
	 * ----------------
	 * These methods check if the ball x and y coordinates have touched the wall boundaries.
	 * These methods are used to change a and y velocities or tell if the player has
	 * lost when the ball ha crossed the lower boundary. 
	 */
	
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight(); 
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/*
	 * Method: checkCollidingObject
	 * ----------------
	 * This method takes the colliding object returned by booleans checking if ball
	 * has made contact with a GObject. If the object is not the paddle the object is removed
	 * and the counter keeping track of bricks broken goes up by 1. It changes the direction
	 * in the y direction at the end. 
	 */
	
	private void checkCollidingObject(GObject collider) {
		if (collider != paddle) {
			bounceClip.play(); //EXTENSION that adds sound to game
			remove(collider);
			BRICK_COUNTER++; // Brick counter goes up by 1
		}
		vy = -vy;
	}

}
