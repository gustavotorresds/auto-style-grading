/*
 * Name: Hannah Rasmussen
 * Section Leader: Andrew Marshall
 * CS106A
 * 2/7/18
 * This program creates a game where there is a canvas of bricks, and a ball that is released from the center of the screen.
 * The ball bounces off the left, right, and top walls, and the paddle. 
 * The player must use their cursor to move the paddle in the x direction to prevent the ball from hitting the 'ground' (bottom wall). 
 * The player's goal is to use the paddle to direct the ball toward the bricks. 
 * Every time the ball hits a brick, it will eliminate one brick. 
 * The player's goal is to eliminate all bricks from the board. 
 * The player will have three turns to accomplish this. 
 * Whenever the ball hits the 'ground' the player loses a turn. 
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
	
	//the total number of bricks on the board at the start of the game. 
	public static final int NBRICKS = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//keeps track of the number of bricks that are left on the board
	private int bricksLeft = NBRICKS;
	
	//keeps track of the number of turns that the player has left 
	private int turnsRemaining = NTURNS;

	//This instance variable is for the paddle will be visible to the whole program
	private GRect paddle = new GRect(((CANVAS_WIDTH - PADDLE_WIDTH)/2), (CANVAS_HEIGHT - PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
	
	//ball x coordinate
	private static final double BALL_X = ((CANVAS_WIDTH/2) - BALL_RADIUS);
	
	//ball y coordinate
	private static final double BALL_Y = ((CANVAS_HEIGHT/2) - BALL_RADIUS);
	
	//This instance variable is for the ball to be visible to the whole program
	private GOval ball = new GOval(BALL_X, BALL_Y, BALL_RADIUS, BALL_RADIUS);
	
	//These instance variables keep track of the ball's velocity in the x and y directions
	private double vx, vy;
	
	//This instance variable will be used for generating random numbers.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	
	//this code runs the program
	public void run() {				
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//create and set up the brick canvas
		setUpBrickCanvas();
		//create and set up the paddle
		setUpPaddle();
		//create and set up the ball
		setUpBall();
		//animation loop
		//runs while the player has turns left
		while(turnsRemaining > 0) {
			ball.move(vx, vy);
			//pause
			pause(DELAY);
			checkForObjectCollisions();
			checkForSideWalls();
			checkForBottomWall();
			checkTurnsRemaining();
			checkForWin();
		}				
	}

	//this method tells the program what to do if the player has won by eliminating all bricks. 
	//prints a label with "you win" 
	private void checkForWin() {
		if(bricksLeft < 1) {
			remove(ball);
			GLabel winGame = new GLabel("YOU WIN!", (getWidth()/2), (getHeight()/2));
			winGame.move(- winGame.getWidth()/2, - winGame.getHeight()/2);
			winGame.setColor(Color.BLACK);
			add(winGame);
		}		
	}

	//this method tells the program what to do if the player has no turns left and has therefore lost
	//prints a label with "game over" 
	private void checkTurnsRemaining() {
		if(turnsRemaining < 1) {
			remove(ball);
			GLabel loseGame = new GLabel("GAME OVER", (getWidth()/2), (getHeight()/2));
			loseGame.move(- loseGame.getWidth()/2, - loseGame.getHeight()/2);
			loseGame.setColor(Color.BLACK);
			add(loseGame);
		}		
	}
	
	//this method tells the ball what to do when it collides with an object
	//reverse velocity when hitting a paddle or brick
	//includes a line to track how many bricks are left after each collision with a brick
	//right now the ball appears to bounce off the paddle too early...there is white space in between ball and paddle
	//I am not sure why this is happening. I have been trying to figure this out, but haven't been able to solve it.
	//Does not appear to be happening for other collisions, just the paddle. 
	private void checkForObjectCollisions() {
		GObject collider = getCollidingObject();
		if(collider != null) {
			if(collider == paddle) {
				//only change the direction of the ball when it hits the paddle if it's already traveling down.
				//this is to try to avoid the paddle stickiness situation
				if(vy > 0) {
					vy = -vy;
				}
			} else {
				vy = -vy;
				remove(collider);
				//counts down by one brick for every brick hit. 
				bricksLeft = bricksLeft - 1;
			}
		}
	}

	//this method checks whether there are any objects present at the ball's location. 
	//returns value to the "check for collisions" method
	//I think this is where the 'sticky paddle' issue comes into play but I don't know how to fix it yet. 
	private GObject getCollidingObject() {
		//the 'get element at' code for the ball's top left corner
		GObject maybeAnObject = getElementAt(ball.getX(), ball.getY());
		if(maybeAnObject != null) {
			return(maybeAnObject);
		} else {
			//the 'get element at' code for the ball's top right corner
			maybeAnObject = getElementAt((ball.getX() + (2*BALL_RADIUS)), ball.getY());
			if(maybeAnObject != null) {
				return(maybeAnObject);
			} else {
				//the 'get element at' code for the ball's bottom left corner
				maybeAnObject = getElementAt(ball.getX(), (ball.getY() + (2*BALL_RADIUS)));
				if(maybeAnObject != null) {
					return(maybeAnObject);
				} else {
					//the 'get element at' code for the ball's bottom right corner
					maybeAnObject = getElementAt((ball.getX() + (2*BALL_RADIUS)), (ball.getY() + (2*BALL_RADIUS)));
					if(maybeAnObject != null) {
						return(maybeAnObject);
					} else {
						return(null);					
					}
				}
			}
		}
	}
	
	//this method tells the program what to do in the event that the ball hits the bottom wall 
	//removes old ball, sends a new ball, and subtracts one turn from the player's turns left. 
	private void checkForBottomWall() {
		if(hitsBottomWall()) {
			remove(ball);
			setUpBall();
			turnsRemaining = turnsRemaining - 1;
		}		
	}
	
	//this method tells the ball to reverse velocity whenever it hits the left, right, or top wall
	private void checkForSideWalls() {
		//this set of code will instruct the ball to bounce whenever it hits a wall --> maybe decompose into its own method?
		if(hitsRightWall() || hitsLeftWall()) {
			vx = -vx;
		} else {
			if(hitsTopWall()) {
				vy = -vy;
			}
		}
	}

	//this boolean is the condition for the ball hitting the top wall 
	private boolean hitsTopWall() {
		return ball.getY() < 0;
	}

	//this boolean is the condition for the ball hitting the bottom wall 
	private boolean hitsBottomWall() {
		return ball.getY() > (getHeight() - BALL_RADIUS);
	}

	//this boolean is the condition for the ball hitting the left wall 
	private boolean hitsLeftWall() {
		return ball.getX() < 0;
	}
	//this boolean is the condition for the ball hitting the right wall
	private boolean hitsRightWall() {
		return ball.getX() > (getWidth() - BALL_RADIUS);
	}

	//this method will set up the bouncing ball for the game. 
	//the ball is already created as an instance object
	private void setUpBall() {
		ball.setLocation(BALL_X, BALL_Y);
		//these two lines of code set parameters for the ball and add it to the screen
		ball.setFilled(true);
		//these two lines of code initialize the ball's x velocity
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 5.0;
		add(ball);

	}

	//this method will set up the initial paddle for the game. 
	//the paddle is already created as an instance object.
	private void setUpPaddle() {
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	
	//this method will keep track of variables related to the mouse movement of the player
	//it helps the paddle track the mouse in the x coordinate plane, but prevents the paddle from moving off screen.
	public void mouseMoved(MouseEvent e) {
		//find coordinates of the mouse
		double x = e.getX();
		double y = (CANVAS_HEIGHT - PADDLE_Y_OFFSET);	

		//calculate the maximum right-hand x coordinate you want paddle to have
		//i.e. the x coordinate when the paddle is at the right edge of the screen and mouse is off screen to right
		double rightEdgeCoordinate = (CANVAS_WIDTH - PADDLE_WIDTH);
		
		//set paddle x coordinates to follow the mouse only in x plane
		//BUT, make sure paddle doesn't leave the screen
		if(x > (rightEdgeCoordinate)) {
			paddle.setLocation(rightEdgeCoordinate, y);
		} else {
			paddle.setLocation(x, y);
		}
	}

	//this method will set up the initial brick canvas
	private void setUpBrickCanvas() {
		//this for loop is responsible for making all rows of bricks
		for(int rows = 0; rows < NBRICK_ROWS; rows++) {
			//this line calculates the initial x coordinate of every row of bricks
			double initialBrickXCoordinate = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH) - ((NBRICK_COLUMNS - 1) * BRICK_SEP))/2;
			
			//this line calculates the y coordinate of each row of bricks
			double brickYCoordinate = BRICK_Y_OFFSET + (rows * (BRICK_HEIGHT + BRICK_SEP));
			
			//this for loop is responsible for making each individual row of bricks 
			//with the appropriate number of bricks in a row
			for(int bricks = 0; bricks < NBRICK_COLUMNS; bricks++) {
				
				//this code calculates the updating x coordinate of each brick in a row
				double xCoordinateOfBrick = initialBrickXCoordinate + (bricks * (BRICK_WIDTH + BRICK_SEP));
				
				//this code creates each brick
				GRect brick = new GRect(xCoordinateOfBrick, brickYCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				
				//if else statements to set every 2 rows a different color...
				//currently the down-side is that this is not really generalizable code for any #rows except 10
				if(rows < 2) {
					brick.setColor(Color.RED);
				} else {
					if(1 < rows && rows < 4) {
						brick.setColor(Color.ORANGE);
					} else {
						if(3 < rows && rows < 6) {
							brick.setColor(Color.YELLOW);
						} else {
							if(5 < rows && rows < 8) {
								brick.setColor(Color.GREEN);
							} else {
								brick.setColor(Color.CYAN);
							}
						}
					}
				}
				
				//adds our bricks to the screen
				add(brick);
			}
		}
		
	}
}
