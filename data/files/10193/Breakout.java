/*
 * File: Breakout.java
 * -------------------
 * Name: Sophie Maguy
 * Section Leader: Esteban Rey
 * 
 * This program completes the game of Breakout. The user has 3 turns to break all of the bricks on the screen
 * with a ball and a paddle. If the ball falls below the paddle, the user loses a turn.
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
	int brickColumns= NBRICK_COLUMNS;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;
	int brickRows= NBRICK_ROWS;

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
	
	//the paddle
	GRect paddle= new GRect((getWidth()- PADDLE_WIDTH)/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	//the ball
	GOval ball= new GOval(BALL_RADIUS*2, BALL_RADIUS*2);

	//the changing x component of the paddle
	double xChangingPaddle=0;
	
	//the bricks remaining after each collision
	int bricksRemaining= brickColumns *brickRows;
	
	//instanciates variables to be used and modified throughout the game
	private double vx, vy;
	
	//creates the random generator to randomize the x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		int turns= NTURNS;
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); // Set the canvas size. 
		setUp(); //setting up the screen for the game
		addMouseListeners(); //used to track movement of the paddle and the click to start the game
		waitForClick(); //ball will move on click
		vx= randomizeX(vx, rgen); //randomizes x velocity
		vy = 4.0; //initial y velocity
		play(turns); //starts the ball's movements
	}

	private void play(int turns) {
		while(turns>0) {
			// initiates the ball's movement
			ball.move(vx,vy);
			//calls the method to check for collisions at each frame
			checkCollision(ball, turns);
			//if the ball hits the top of the frame, it will change direction and bounce back
			if(ball.getY()<0) {
				vy = -vy;
			}
			//if the ball goes below the frame, it will reset and lose a turn
			if(ball.getY() > getHeight() - ball.getHeight()) {
				turns--;
				reset();
			}
			//if the ball hits either side of the frame, it will bounce off and change x direction
			if(ball.getX() > getWidth() - ball.getHeight() || ball.getX()<0) {
				vx = -vx;
			}
			// pause
			pause(DELAY);
		}
		//if the user loses 3 turns, this losing statement will display
		GLabel loseStatement= new GLabel("You lose! :(");
		add(loseStatement, (getWidth()-loseStatement.getWidth())/2, getHeight()/2-loseStatement.getAscent());	
	}

	//this method will change the direction of the ball should it collide with an object and remove bricks if it collides with them. 
	//once there are zero bricks left, this method will display a winning statement
	private void checkCollision(GObject ball, int turns) {
		GObject collider = getCollidingObject(ball.getX(), ball.getY());
		//this tests for when the ball collides with the paddle. if it does, the ball will change direction
		if(collider==paddle) {
			vy=-vy;
		}
		//this tests for when the ball collides with a brick. if it does, the brick will be removed and the ball will change direction
		else if (collider!=null){
			remove(collider);
			vy=-vy;
			bricksRemaining--;
		}
		//if the user breaks all of the bricks, the system will reset and a winning statement will display
		if(bricksRemaining==0) {
			turns=0;
			reset();
			GLabel winStatement= new GLabel("You win! :)");
			add(winStatement, (getWidth()-winStatement.getWidth())/2, getHeight()/2-winStatement.getAscent());
		}
	}

	//method that will return the object with which the ball collides (or null if it doesn't collide)
	//checks all four corners of the ball for collisions
	private GObject getCollidingObject(double x, double y) {
		if(getElementAt(x, y)==null) {
			x+=(2*BALL_RADIUS);
		}
		if(getElementAt(x, y)==null) {
			y+=(2*BALL_RADIUS);
		}
		if(getElementAt(x, y)==null) {
			x-=(4*BALL_RADIUS);
		}
		return getElementAt(x, y);
	}

	//method to randomize the velocity in the x direction
	private double randomizeX(double vx, RandomGenerator rgen) {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		return vx;
	}

	//resets the game after the user misses the ball with the paddle
	private void reset() {
		ball.setLocation((getWidth()-BALL_RADIUS*2)/2, (getHeight()-BALL_RADIUS*2)/2);
		setUp();
		waitForClick();
		vx= randomizeX(vx, rgen);
		vy=4.0;
	}
	
	public void mouseMoved(MouseEvent event) { 
		//finds the mouse's xCoordinate and sets the paddle's xCoordinate to that value
		xChangingPaddle= event.getX();
		//sets the xCoordinate of the paddle to the edge if the mouse is beyond the boundaries of the frame
		if(xChangingPaddle>getWidth()-PADDLE_WIDTH) {
			xChangingPaddle=getWidth()-PADDLE_WIDTH;
		}
		paddle.setLocation(xChangingPaddle, getHeight()-PADDLE_Y_OFFSET);
	}	
	
	private void setUp() {
		double x= BRICK_SEP;
		double y= BRICK_Y_OFFSET;
		//while loop to create all 10 rows
		while(brickRows>0) {
			//add first brick in row
			int bricksInRow= brickColumns;
			//while loop to place bricks in each row
			while( bricksInRow > 0) {
				GRect rect= new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				x+= BRICK_WIDTH+ BRICK_SEP;
				rect.setFilled(true);
				//calls method to determine brick color
				whichColor(rect, brickRows);
				add(rect);
				bricksInRow--;
			}
			//reset xCoordinate and brick quantity
			x= BRICK_SEP;
			bricksInRow= brickColumns;
			//move down a row
			y+= BRICK_SEP + BRICK_HEIGHT;
			brickRows--;
		}
		//add paddle and ball to the frame
		paddle.setFilled(true);
		add(paddle);
		ball.setLocation((getWidth()-BALL_RADIUS*2)/2, (getHeight()-BALL_RADIUS*2)/2);
		ball.setFilled(true);
		add(ball);
	}

	//method to determine the specified color of the row of bricks
	private void whichColor(GRect rect, int row) {
		if (row>8) {
			rect.setColor(Color.RED);
		}
		else if (row>6) {
			rect.setColor(Color.ORANGE);
		}
		else if (row>4) {
			rect.setColor(Color.YELLOW);
		}
		else if (row>2) {
			rect.setColor(Color.GREEN);
		}
		else {
			rect.setColor(Color.CYAN);
		}
		
	}

}
