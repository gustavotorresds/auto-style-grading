/*
 * File: Breakout.java
 * -------------------
 * Name: Shubhankar Deo
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
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static int NTURNS = 3;
	
	//Initializes the paddle
	private GRect p=null;
	
	//Initializes the ball
	private GOval b=null;
	
	//Keeps track of how many bricks are left on the screen
	private int bricksLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	
	//X and Y-components of the ball's velocity
	private double vx, vy;
	
	//Random-number generator for determining vx
	private RandomGenerator rgen=RandomGenerator.getInstance();
	
	//Initializes the label for how many bricks have yet to be hit
	private GLabel c=null;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpGame();
		addMouseListeners();
		playGame();
	}
	
	/* method setUpGame()
	 * ------------------
	 * Sets up the game before the 
	 * user begins playing.
	 * 
	 * Precondition: None
	 * Postcondition: The grid of bricks
	 * is created, complete with color sequencing.
	 * The paddle is also created at the bottom of 
	 * the canvas. In addition, the counter that tracks
	 * the number of bricks remaining and the method that
	 * displays the opening message are also called/used.
	 */
	private void setUpGame() {
		setUpBricks();
		setUpPaddle();
		setUpCounter();
		beginMessage();
	}

	/* method setUpBricks()
	 * --------------------
	 * Prints the specified number
	 * of bricks based on the number of
	 * columns and rows of bricks and fills
	 * them in based on the given pattern.
	 * 
	 * Precondition: None
	 * Postcondition: NBRICK_ROWS times 
	 * NBRICK_COLUMNS bricks are printed in
	 * a grid-like fashion, with even spacing
	 * between each brick. The grid is positioned
	 * BRICK_Y_OFFSET pixels below the top of the 
	 * canvas, and an equal amount of space is left
	 * to the left of the first column of bricks and
	 * to the right of the last column of bricks. Each
	 * set of twenty bricks, starting from the top left 
	 * and moving down and right, are colored red, orange,
	 * yellow, green, and cyan, respectively.
	 */
	private void setUpBricks() {
		int brickCount=0;
		for(double j=BRICK_Y_OFFSET; j<=BRICK_Y_OFFSET + (NBRICK_ROWS-1) * (BRICK_HEIGHT+BRICK_SEP); j+=BRICK_HEIGHT+BRICK_SEP) {
			for(double i=getWidth()/2 - (BRICK_SEP*((NBRICK_COLUMNS/2.0)-0.5)+BRICK_WIDTH *(NBRICK_COLUMNS/2.0)); i<=getWidth()/2 + BRICK_SEP*((NBRICK_COLUMNS/2.0)-0.5)+BRICK_WIDTH *((NBRICK_COLUMNS/2.0)-1); i+=BRICK_WIDTH+BRICK_SEP) {
				GRect b = new GRect(i,j,BRICK_WIDTH,BRICK_HEIGHT);
				add(b);
				b.setFilled(true);
				brickCount++;
				if(brickCount<=20) {
					b.setFillColor(Color.RED);
				}
				if(brickCount>20 && brickCount<=40) {
					b.setFillColor(Color.ORANGE);
				}
				if(brickCount>40 && brickCount<=60) {
					b.setFillColor(Color.YELLOW);
				}
				if(brickCount>60 && brickCount<=80) {
					b.setFillColor(Color.GREEN);
				}
				if(brickCount>80) {
					b.setFillColor(Color.CYAN);
				}
			}
		}
	}
	
	/* method setUpPaddle()
	 * --------------------
	 * Adds the paddle to the bottom
	 * of the screen and colors it in
	 * black.
	 * 
	 * Precondition: p has 
	 * been initialized.
	 * Postcondition: A rectangle with
	 * dimensions specified by constants
	 * PADDLE_WIDTH and PADDLE_HEIGHT is 
	 * added so that its bottom edge 
	 * touches the bottom of the canvas and 
	 * its horizontal midpoint aligns with the
	 * canvas's horizontal midpoint.
	 */
	private void setUpPaddle() {
		p = new GRect(getWidth()/2-PADDLE_WIDTH/2,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		p.setFilled(true);
		p.setFillColor(Color.BLACK);
		add(p);
	}
	
	/* method setUpCounter()
	 * ---------------------
	 * Initializes the label and sets its location to the top right corner
	 * of the canvas screen.
	 * 
	 * Precondition: None
	 * Postcondition: A counter has been initialized and is now visible on
	 * the canvas. It decreases by one every time a brick is hit.
	 */
	private void setUpCounter() {
		c=new GLabel(" " + bricksLeft);
		c.setLocation(getWidth()-c.getWidth(), c.getHeight());
		add(c);
	}
	
	/* method beginMessage()
	 * ---------------------
	 * Prints off a message that instructs the reader with what to do
	 * in order to begin playing the game.
	 * 
	 * Precondition: None
	 * Postcondition: The label is created in blue font in the center of
	 * the grid. Once the keypad picks up a click, the message disappears and is 
	 * replaced by a ball, waiting to be put into play by another click from the user.
	 */
	private void beginMessage() {
		GLabel beginGame=new GLabel("");
		beginGame.setLabel("Welcome to Breakout! Click to begin the game.");
		beginGame.setColor(Color.BLUE);
		beginGame.setLocation(getWidth()/2-beginGame.getWidth()/2, getHeight()/2-beginGame.getHeight()/2);
		add(beginGame);
		waitForClick();
		remove(beginGame);  
		}
	
	/* method mouseMoved()
	 * -------------------
	 * Executes when the mouse is moved and
	 * lets the user use the mouse to move the
	 * paddle horizontally along the screen.
	 * 
	 * Precondition: The mouse is moved and the paddle
	 * has been initialized.
	 * Postcondition: The paddle moves to the x-coordinate
	 * of the mouse, which is found by the call e.getX(). If
	 * the mouse moves off to the left or right of the canvas,
	 * the paddle remains in the leftmost or rightmost position.
	 * respectively, so that it is fully visible on the canvas. 
	 */
	public void mouseMoved(MouseEvent e) {
		double newX=e.getX();
		if(newX>=0 && newX<=getWidth()-PADDLE_WIDTH) {
			p.setLocation(newX, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
		else if(newX<0) {
			p.setLocation(0, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
		else {
			p.setLocation(getWidth()-PADDLE_WIDTH, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	
	/* method playGame()
	 * -----------------
	 * Over-arching method that allows the user
	 * to begin playing the game.
	 * 
	 * Precondition: setUpGame has been successfully
	 * called (all bricks have been laid out and the 
	 * paddle has been initialized).
	 * Postcondition: Until all the bricks are knocked 
	 * out or the user has lost all 3 lives, this method 
	 * continues executing.
	 */
	private void playGame() {
		while(NTURNS>0 && bricksLeft!=0) {
			createBall();
			setVelocity();
			moveBall();
		}
		if(NTURNS==0) {
			showLosingLabel();
		}
	}
	
	/* method showLosingLabel()
	 * ---------------------
	 * Displays a red-colored message informing the user that
	 * they've lost all 3 lives and lost the game.
	 * 
	 * Precondition: None
	 * Postcondition: A message centered on the canvas and in color
	 * red is displayed with the words "You lost! Better luck next time."
	 */
	private void showLosingLabel() {
		GLabel lossLabel=new GLabel("You lost! Better luck next time.");
		lossLabel.setColor(Color.RED);
		lossLabel.setLocation(getWidth()/2-lossLabel.getWidth()/2, getHeight()/2-lossLabel.getHeight()/2);
		add(lossLabel);
	}
	
	/* method showWinLabel()
	 * ---------------------
	 * Displays a green-colored message informing the user that
	 * they've knocked out all the blocks and won the game.
	 * 
	 * Precondition: None
	 * Postcondition: A message centered on the canvas and in color
	 * green is displayed with the words "Congratulations! You Won!"
	 */
	private void showWinLabel() {
		GLabel winLabel=new GLabel("Congratulations! You won!");
		winLabel.setColor(Color.GREEN);
		winLabel.setLocation(getWidth()/2-winLabel.getWidth()/2, getHeight()/2-winLabel.getHeight()/2);
		add(winLabel);
	}
	
	/* method createBall()
	 * -------------------
	 * Creates a ball for the game
	 * and adds it to the screen.
	 * 
	 * Precondition: b has been init-
	 * ialized.
	 * Postcondition: A black ball is 
	 * displayed with its center at the 
	 * center of the canvas.
	 */
	private void createBall() {
		b = new GOval(getWidth()/2-BALL_RADIUS/2, getHeight()/2-BALL_RADIUS/2, BALL_RADIUS, BALL_RADIUS);
		b.setFilled(true);
		b.setFillColor(Color.BLACK);
		add(b);
	}
	
	/* method setVelocity()
	 * ---------------------
	 * 
	 * Randomly selects the velocity
	 * of the ball in the x-direction and
	 * equates the velocity in the y-direction 
	 * to a constant.
	 * 
	 * Precondition: vx, vy and rgen have 
	 * been initialized.
	 * Postcondition: A random value between
	 * -VELOCITY_X_MAX and VELOCITY_X_MAX 
	 * (excluding -VELOCITY_X_MIN to VELOCITY_X_MIN)
	 * is assigned to vx, representing the velocity
	 * of the ball in the x-direction. The y-component
	 * of velocity is set to VELOCITY_Y.
	 */
	private void setVelocity() {
		vx=rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx=-vx;
		}
		vy=VELOCITY_Y;
	}
	
	/* method moveBall()
	 * -----------------
	 * Moves the ball around the screen 
	 * so that it rebounds off of walls, 
	 * bricks, or the paddle.
	 * 
	 * Precondition: The ball, vx, and vy
	 * have all been initialized and assigned
	 * non-null values.
	 * Postcondition: Until all the bricks are
	 * hit or the ball hits the bottom wall, the
	 * ball keeps moving around the canvas, rebound-
	 * ing off walls. If a brick is hit, the ball still
	 * bounces in the opposite y-direction, but the brick
	 * is eliminated from the screen.
	 */
	private void moveBall() {
		waitForClick();
		while(hitBottomWall()==false) {
			b.move(vx,vy);
			pause(DELAY);
			if(hitTopWall()) {
				vy=-vy;
			}
			if(hitLeftWall() || hitRightWall()) {
				vx=-vx;
			}
			GObject collider=getCollidingObject();
			if(collider!=null) {
				if(collider==p) {
					vy=-vy;
				}
				else if(collider==c) {
				}
				else {
					vy=-vy;
					remove(collider);
					bricksLeft--;
				}
			}
			c.setLabel("" + bricksLeft);
			if(bricksLeft==0) {
				showWinLabel();
				remove(b);
			}
		}
		NTURNS--;
		remove(b);
		if(NTURNS>0) {
			lostLifeMessage();
		}
	}
	
	/* method lostLifeMessage()
	 * ------------------------
	 * Displays a message informing the user of how many
	 * lives they have left.
	 * 
	 * Precondition: None
	 * Postcondition: A message centered on the screen is
	 * shown, informing the user of how many more lives they
	 * have left in the current round of Breakout. After the
	 * user clicks, the message disappears.
	 */
	private void lostLifeMessage() {
		GLabel lostLife=new GLabel("");
		if(NTURNS!=1) {
			lostLife.setLabel("You have " + NTURNS + " lives left. Click to continue.");
		}
		else {
			lostLife.setLabel("You have " + NTURNS + " life left. Only one more shot at winning!");
		}
		lostLife.setColor(Color.RED);
		lostLife.setLocation(getWidth()/2-lostLife.getWidth()/2, getHeight()/2-lostLife.getHeight()/2);
		add(lostLife);
		waitForClick();
		remove(lostLife);
	}
	
	/* method hitTopWall()
	 * -------------------
	 * Checks to see if the ball makes
	 * contact with the top wall of the 
	 * canvas.
	 * 
	 * Precondition: b is non-null
	 * Postcondition: A boolean is returned
	 * that represents whether the ball makes 
	 * contact with the top wall.
	 */
	private boolean hitTopWall() {
		return b.getY()<0;
	}
	
	/* method hitBottomWall()
	 * -------------------
	 * Checks to see if the ball makes
	 * contact with the bottom wall of the 
	 * canvas.
	 * 
	 * Precondition: b is non-null
	 * Postcondition: A boolean is returned
	 * that represents whether the ball makes 
	 * contact with the bottom wall.
	 */
	private boolean hitBottomWall() {
		return b.getY()>getHeight()-b.getHeight();
	}
	
	/* method hitLeftWall()
	 * -------------------
	 * Checks to see if the ball makes
	 * contact with the left wall of the 
	 * canvas.
	 * 
	 * Precondition: b is non-null
	 * Postcondition: A boolean is returned
	 * that represents whether the ball makes 
	 * contact with the left wall.
	 */
	private boolean hitLeftWall() {
		return b.getX()<0;
	}
	
	/* method hitRightWall()
	 * -------------------
	 * Checks to see if the ball makes
	 * contact with the right wall of the 
	 * canvas.
	 * 
	 * Precondition: b is non-null
	 * Postcondition: A boolean is returned
	 * that represents whether the ball makes 
	 * contact with the right wall.
	 */
	private boolean hitRightWall() {
		return b.getX()>getWidth()-b.getWidth();
	}
	
	/* method getCollidingObject()
	 * ---------------------------
	 * Returns the object with which the ball 
	 * collides, if any.
	 * 
	 * Precondition: b is non-null
	 * Postcondition: The object that b collides
	 * with is returned (and used in the moveBall()
	 * method later). If b does not collide with any
	 * object, then null is returned.
	 */
	private GObject getCollidingObject() {
		if(getElementAt(b.getX(),b.getY())!=null) {
			return getElementAt(b.getX(),b.getY());
		}
		else if(getElementAt(b.getX()+b.getWidth(),b.getY())!=null) {
			return getElementAt(b.getX()+b.getWidth(),b.getY());
		}
		else if(getElementAt(b.getX(),b.getY()+b.getHeight())!=null) {
			return getElementAt(b.getX(),b.getY()+b.getHeight());
		}
		else if(getElementAt(b.getX()+getWidth(),b.getY()+b.getHeight())!=null) {
			return getElementAt(b.getX()+getWidth(),b.getY()+b.getHeight());
		}
		else {
			return null;
		}
	}
}
