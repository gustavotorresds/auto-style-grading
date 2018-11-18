/*
 * File: Breakout.java
 * -------------------
 * Name:Emily Kaperst
 * Section Leader:Meng Zhang 
 * 
 * This program creates the game of breakout, which the ultimate goal
 * is to destory all of the bricks in the program
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

	// Number of bricks in each row.
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks.
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels.
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels.
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels.
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

	// Number of turns. 
	public static final int NTURNS = 3;

	//Brick x posistion
	private double xPosistion;

	//Brick y posistion.
	private double yPosistion;

	// Paddle x position.
	private double paddleX;

	// Paddle y position
	private double paddleY;
	GRect paddle;

	// creates random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Velocity in x direction
	private double vx; 

	// Velocity in y direction.
	private double vy;

	// Ball x position.
	private double ballX;

	// Ball y position.
	private double ballY;

	// Creates ball.
	GOval ball;

	// Finds the total number of bricks in the game.
	int brickCounter = NBRICK_ROWS*NBRICK_COLUMNS; 

	// Creates collider object.
	GObject collider;
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Play the game until all of the turns are used or the winner wins the game.
		for(int n=0; n<NTURNS && brickCounter>0; n++) {
			setUpGame();
			addMouseListeners();
			playGame();	
		}

		// Display at the end of the game. 
		GLabel thanks = new GLabel("Thanks for playing!");
		add(thanks, getWidth()/2-thanks.getWidth()/2, getHeight()/2-thanks.getHeight()/2);
	}

	// Sets up the game adding bricks and the paddle.
	private void setUpGame() {
		createBricks();
		createPaddle();
	}

	// Plays and executes the game.
	private void playGame() { 

		// Set the velocity in the x direction using a random number generator.
		vx = rgen.nextDouble(1.0, 3.0);

		// Set the velocity in the y direction.
		vy = 3.0;
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}

		// Determines number of bricks in game that have to be removed.
		makeBall();
		waitForClick();
		while(true) {
			changeBallPosistion();
			collider = getCollidingObject();

			// If the ball hits paddle, change direction.
			if (collider == paddle) { 
				vy = -Math.abs(vy);
			}

			// Check if ball is hitting bricks.
			else if (collider != null && collider != paddle) { 
				remove(collider);
				brickCounter--;
				vy = -vy;
			}

			// If all the bricks are removed, user wins game and game stops.
			if (brickCounter == 0) { 
				remove(ball);
				message(" Congratulations! You Won The Game ");
				break;
			}

			// If the user hits the bottom wall, they lose and the game stops.
			if (hitBottomWall()) { 
				message(" Sorry! You Lost The Game ");
				break;
			}
			pause(DELAY);
		}
	}

	// Draws the number of bricks on the screen, positioning them in the center. 
	private void createBricks() {
		for (int i=0; i<NBRICK_ROWS; i++) {
			for (int j=0; j<NBRICK_COLUMNS; j++) {

				// The x position of the grid of bricks is centered and has the same amount of white space on both sides.
				xPosistion = getWidth()/2 - (NBRICK_COLUMNS *BRICK_WIDTH)/2 - (NBRICK_COLUMNS*BRICK_SEP)/2 + ((BRICK_WIDTH+ BRICK_SEP)* j +BRICK_SEP/2);
				yPosistion = BRICK_Y_OFFSET + ((BRICK_HEIGHT+BRICK_SEP)*i);
				drawBrick(xPosistion,yPosistion,i);
			}
		}
	}

	// Draws the paddle for the game.
	private void createPaddle() {

		//Place paddle in center x direction.
		paddleX = (getWidth()-PADDLE_WIDTH)/2; 

		//Place paddle above bottom wall according to offset.
		paddleY = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET); 
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	// Tracks when the mouse is moved and the paddle thus follows the direction of the mouse in the x direction.
	public void  mouseMoved (MouseEvent e) {
		boolean leftBound = e.getX()>=0; 
		boolean rightBound = e.getX()<=getWidth()-PADDLE_WIDTH;
		if(leftBound==true &&rightBound==true ) { 

			// Sets location of paddle so it does not go off left/right wall.
			paddle.setLocation(e.getX(), paddleY); 
		}
	}

	// Creates the ball used in the game, beginning in the center of the screen.
	public GOval makeBall() {
		double size=BALL_RADIUS*2;

		ballX = getWidth()/2-BALL_RADIUS;
		ballY = getHeight()/2;
		ball = new GOval(size, size);
		ball.setFilled(true);
		add(ball, ballX, ballY);
		return ball;
	}

	// Find if the ball collides with an object at all 4 corners.
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(),ball.getY())!=null) { 

			// Returns the element at which the ball's top left corner collides 
			return getElementAt(ball.getX(),ball.getY()); 
		}
		else if(getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY())!=null) { 

			// Returns the element at which the ball's top right corner collides 
			return getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY());
		}
		else if (getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS))!=null) { 

			// Returns the element at which the ball's bottom right corner collides 
			return getElementAt(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS));
		}
		else if (getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS))!=null) { 

			// Returns the element at which the ball's bottom left corner collides.
			return getElementAt(ball.getX(), ball.getY()+(2*BALL_RADIUS));
		}
		else {

			// Return null if the ball doesn't collide at any corner.
			return null;
		}
	}

	// Change the direction of the ball depending on which wall it hits.
	private void changeBallPosistion() {
		ball.move(vx, vy);
		if(hitLeftWall() || hitRightWall()) {

			// Switch the x direction if the ball hits either left or right wall.
			vx = -vx; 

		}
		if(hitTopWall()) { 

			// Switch the y direction if the ball hits the top wall.
			vy = -vy; 
		}
	}

	// Adds a message to the middle of the screen 
	private void message(String text) {
		GLabel winnerMessage= new GLabel (text);
		double xW = (getWidth() - winnerMessage.getWidth())/2;
		double yW = (getHeight() - winnerMessage.getHeight())/2;
		add(winnerMessage, xW, yW);

		// Allows user to see the message and then move on once they click.
		waitForClick();
		removeAll();
	}

	// Check to see if the ball hits bottom wall
	private boolean hitBottomWall() {
		return ball.getY()> getHeight() - ball.getHeight(); 
	}

	// Check to see if the ball hits top wall.
	private boolean hitTopWall() {
		return ball.getY()<=0; 
	}

	// Check to see if the ball hits right wall
	private boolean hitRightWall() {
		return ball.getX()>= getWidth()-ball.getWidth(); 
	}

	// Check to see if the ball hits left wall.
	private boolean hitLeftWall() {
		return ball.getX()<=0;
	}

	// Creates the brick and dimensions.
	private void drawBrick(double x, double y, int row) {
		GRect brick = new GRect(x,y,BRICK_WIDTH, BRICK_HEIGHT); 
		add(brick);
		brick.setFilled(true);
		if (row/2 == 0) {

			// Set the first two rows of bricks to the color red.
			brick.setColor(Color.RED); 
		}
		else if (row/2 == 1) {

			// Set the third and fourth rows of bricks to the color orange.
			brick.setColor(Color.ORANGE); 
		}
		else if (row/2 == 2) {

			// Set the fifth and sixth rows of bricks to the color yellow.
			brick.setColor(Color.YELLOW); 
		}
		else if (row/2 == 3) {

			// Set the seventh and eighth rows of bricks to the color green.
			brick.setColor(Color.GREEN); 
		}
		else if (row/2 == 4) {

			// Set the ninth and tenth rows of bricks to the color cyan.
			brick.setColor(Color.CYAN); 
		}
	}
}
