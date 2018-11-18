/*
 * File: BreakoutNOEXTENSION.java 
 * -------------------
 * Name: Liz Wallace
 * Section Leader: Cat Xu
 * 
 * This program creates the game, Breakout. It builds a game that starts with 100 
 * bricks and has the user attempt to delete all the bricks by hitting a ball against a
 *  paddle.  Pre-condition: blank white screen. Post-condition: Rainbow bricks line
 *   the top of the screen. Ball begins in center of the screen
 *    and bounces towards paddle. User wins by removing 100 blocks.
 * 
 */

import acm.graphics.*;  
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutNOEXTENSION extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	//Allows for easier centering of bricks by calculating the middle of columns.
	public static final int HALF_COLUMNS= NBRICK_COLUMNS/2;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	//Allows for centering of the bricks by calculating the number of separations in the center of the game
	public static final double HOW_MANY_SEP = HALF_COLUMNS-.5;

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

	// Centers mouse in relation to the middle of the paddle
	public static final double PADDLE_X_OFFSET = PADDLE_WIDTH/2;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 700.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Creates paddle to be used in multiple methods
	private GRect paddle = makePaddle();

	// Creates ball to be used in multiple methods
	private GOval ball = makeBall();

	// Allows for the ball to have a random path while bouncing
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Generates a random number for a change in velocity to keep player on their toes
	private double vx = rgen.nextDouble(1.0, 3.0);

	// Initial velocity of the ball
	private double vy = +3.0;

	// Creates brick as a rectangle instance variable so it can be accessed in multiple methods
	private GObject rect;

	// Creates instance variable to count the number of times the game has been run
	private int turns = 0;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();

		//This creates the pieces that will be needed to play the game.
		setUpGame();

		//This executes the actions to allow the user to play the game three times 
		for(int games = 0; games < 3; games++) {
			playGame(games);
		}

	}

	// Allows for mouse movements to be tracked so the paddle can follow the mouse.
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		double y = getHeight()-PADDLE_Y_OFFSET;
		// Prevents paddle from going off the screen
		if (mouseX < 0) {
			paddle.setLocation(0, y);
		} else if (mouseX > getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, y);
		} else {
			paddle.setLocation(mouseX-PADDLE_X_OFFSET, y);
		}

	}
	// Creates initial conditions needed for the game to function.
	private void setUpGame() {

		// Creates 100 bricks to be destroyed in game
		setUpBricks();

		// Adds paddle that will be controlled by the mouse 
		addPaddle(paddle);

		// Adds ball to center of the screen
		add(ball, getWidth()/2, getHeight()/2);
	}

	// Creates 100 bricks at the top of the screen
	private void setUpBricks() {
		for(int cols = 0; cols < NBRICK_COLUMNS; cols++) {
			for(int rows = 0; rows < NBRICK_ROWS; rows++) {
				double x = getWidth()/2- (HALF_COLUMNS*BRICK_WIDTH+(BRICK_SEP*HOW_MANY_SEP))+((cols*BRICK_WIDTH)+((cols*(BRICK_SEP))));
				double y = BRICK_Y_OFFSET+((rows*BRICK_HEIGHT)+(rows*BRICK_SEP));
				GRect rect= new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);

				//Sets color to rainbow rows of bricks
				if(turnRed(rows)) {
					rect.setColor(Color.RED);
				}
				if(turnOrange(rows)) {
					rect.setColor(Color.ORANGE);
				}
				if(turnYellow(rows)) {
					rect.setColor(Color.YELLOW);
				}
				if(turnGreen(rows)) {
					rect.setColor(Color.GREEN);
				}
				if(turnCyan(rows)) {
					rect.setColor(Color.CYAN);
				}

				// Adds bricks to the screen
				add(rect);
			}

		}
	}

	// Each of these booleans allows for the rows to be counted and turned colors based on how many rows are present
	private boolean turnRed (int a) {
		return a % 10 == 0 || a % 10 == 1;		
	}

	private boolean turnOrange (int a) {
		return a % 10 == 2 || a % 10 == 3;	
	}

	private boolean turnYellow (int a) {
		return a % 10 == 4 || a % 10 == 5;	
	}

	private boolean turnGreen (int a) {
		return a % 10 == 6 || a % 10 == 7;
	}

	private boolean turnCyan (int a) {
		return a % 10 == 8 || a % 10 == 9;	
	}

	// Creates paddle to be controlled by mouse
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	// Adds paddle to the bottom of the screen
	private void addPaddle(GRect paddle) {
		double x = 0;
		double y = (getHeight() - PADDLE_Y_OFFSET);
		add(paddle, x, y);
	}

	// Creates ball and sets the size to constants
	private GOval makeBall() {
		GOval ball = new GOval (2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}

	// This function begins to start the game by setting the ball at a random velocity and tracking the number of times the game has been played (max: 3 turns) 
	private void playGame(int games) {
		randomVelocity();
		moveBallAndCollide(games);

		// Ensures that a ball won't be added to the screen after the third game finishes
		if(games < 2) {
			add(ball, getWidth()/2, getHeight()/2);
		}
	}

	// Generates a random velocity for the ball to make the game more interesting 
	private double randomVelocity() {
		if (rgen.nextBoolean(0.5)) 
			vx=-vx;
		return vx;
	}

	// Major action of the game is created here. This sets the terms for colliding with bricks and removing them. It also keeps track of these bricks in order to know when 100 have been eliminated and the player has won the game. 
	private void moveBallAndCollide(int games) {
		int countBlocks = 0;
		while(ballIsActive()) {

			// If the ball hits the left or right wall the direction will reverse
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx=-vx;
			}
			// If the ball hits the top wall the direction will reverse
			if(hitTopWall(ball)) {
				vy=-vy;
			}
			ball.move(vx, vy);
			pause(DELAY);

			// brings in the collider instance variable 
			GObject collider = getCollidingObject();

			// When ball hits paddle, direction of ball is reversed
			if(collider == paddle) {
				vy=-vy;
			} else if(collider != null) {
				remove(collider);
				countBlocks ++;
				vy=-vy;

				// Defines conditions for a winning game (100 blocks have been removed)
			} else if(countBlocks == 100) {
				remove(ball);

			} 
		}

		// Keeps track to see if user has lost. 
		if (games == 2 && countBlocks < 100) {
			remove(ball);
		}
	}

	// Allows for keeping track of hitting the right wall to make the ball bounce off 
	private boolean hitRightWall (GOval ball) {
		return ball.getX() < 0;
	}

	// Allows for keeping track of hitting the left wall to make the ball bounce off
	private boolean hitLeftWall (GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	// Allows for keeping track of hitting the top wall to make the ball bounce off
	private boolean hitTopWall (GOval ball) {
		return ball.getY() < 0;
	}

	// Checks at four coordinates of the ball edges to see if the ball has collided with anything
	private GObject getCollidingObject() {
		GObject collider1 = getElementAt(ball.getX(), ball.getY());
		if(collider1 != null) {
			return(collider1);
		} 
		GObject collider2 = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if(collider2 != null) {
			return(collider2);
		} 
		GObject collider3 = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		if(collider3 != null) {
			return(collider3);
		} 
		GObject collider4 = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if(collider4 != null) {
			return(collider4);
		}
		return null;
	}

	// Determines whether the ball is still in play to see if it needs to keep the game going
	private boolean ballIsActive() {
		return ball.getY() < getHeight();
	}

}
