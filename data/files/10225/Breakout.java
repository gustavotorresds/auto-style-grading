/*
 * File: Breakout.java
 * -------------------
 * Name:Amy Hembree
 * Section Leader:Marilyn  Zhang
 * cite: lecture slides, lecture videos, assignment handout, YEAH hours, LAIR.
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	//creates the paddle moved to bounce the ball
	public static GRect PADDLE = null;
	//creates a the random generator used later to generate x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//the current horizontaland vertical velocity of the ball
	private double vx, vy;
	//the object of the ball
	public static GOval BALL = null;
	//the object the ball makes contact with
	private static  GObject COLLIDER = null;
	//the number of bricks remaining.  adjusted later by ball collisions caused by the user
	private static int BRICKS_REMAINING= NBRICK_ROWS*NBRICK_COLUMNS;
	//the box within which post turn messages are written
	private static GRect ENDBOX = null;
	//the dimensions of the box within which post turn messages are written
	private double ENDBOXWIDTH= 200;
	private double ENDBOXHEIGHT= 60;

	// Number of turns allowed per game
	public static final int NTURNS = 3;
	//conditions for ball collisions with edges of screen 
	private boolean hitLeftWall( GOval BALL) {
		return BALL.getX() <= BALL.getWidth();
	}
	private boolean hitRightWall( GOval BALL) {
		return BALL.getX() >= getWidth()-BALL.getWidth();
	}
	private boolean hitTopWall (GOval BALL) {
		return BALL.getY() <= BALL.getHeight();
	}
	private boolean hitBottomWall (GOval BALL) {
		return BALL.getY() >= getHeight()-BALL.getHeight();
	}

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		makePaddle();
		makeMovingBall();
		addMouseListeners();
	}
	private void setUpBricks(){
		//creates each new row
		for (int row=0; row< NBRICK_ROWS; row++) {
			//creates each brick in every column of each row
			for(int column=0; column<NBRICK_COLUMNS; column++) {
				//establishes an X startpoint that ensures there will be an equal space on each side of the block of bricks even if the constants are changed 
				//ensures that the x startpoint changes appropriately for each brick added
				double brickX= (getWidth()-NBRICK_COLUMNS*BRICK_WIDTH - (NBRICK_COLUMNS-1)*BRICK_SEP)/2 + column*BRICK_WIDTH + column*BRICK_SEP;
				//establishes the y startpoint and changes it appropriately as each row is added
				double brickY= BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP;
				GRect brick = new GRect (brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				//establishes the color change every 2 rows
				if(row % 10 < 2) {
					brick.setColor(Color.RED);
				} else {
					if (row % 10 < 4) {
						brick.setColor(Color.ORANGE);
					} else {
						if(row % 10 < 6) {
							brick.setColor(Color.YELLOW);
						} 
						else {
							if(row % 10 < 8) {
								brick.setColor(Color.GREEN);
							} else {
								if (row % 10 < 10) {
									brick.setColor(Color.CYAN);
								} 
							}
						}
					}
				}
				//adds bricks within the column for loop
				add(brick);
			}
		}
	}

	//Creates initial paddle in middle of screen (with respect to width) at the assigned height before mouse movement
	private void makePaddle() {
		//sets initial position of paddle in middle of screen
		double initialPaddleY= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		double initialPaddleX = (getWidth()-PADDLE_WIDTH)/2;
		PADDLE = new GRect (initialPaddleX, initialPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		//colors paddle black
		PADDLE.setFilled(true);
		PADDLE.setColor(Color.BLACK);
		add(PADDLE);	
	}
	private void makeMovingBall() {
		//Establishes a changeable variable for the number of turns
		int livesLeft= NTURNS;
		//repeats the process of creating the ball for each turn
		while (livesLeft > 0 && BRICKS_REMAINING !=0 ) {
			double ballX= getWidth()/2-BALL_RADIUS;
			double ballY= getHeight()/2-BALL_RADIUS;
			BALL= new GOval (ballX,ballY, 2*BALL_RADIUS, 2*BALL_RADIUS);
			//colors the ball black
			BALL.setFilled(true);
			BALL.setColor(Color.BLACK);
			//places the ball on the screen 
			add(BALL);
			//from section handout.  establishes a random x velocity in the 1-3 range
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			//makes random x velocity negative half the time
			if (rgen.nextBoolean(0.5)) vx = -vx;
			//sets the constant vy scalar value
			vy= VELOCITY_Y;
			//keeps the ball in the initial position until the user clicks to start the game
			waitForClick();
			//condition of animation loop that keeps the ball in continuous motion
			while(livesLeft > 0 && BRICKS_REMAINING !=0) {
				checkForCollision();
				//reverses direction of ball velocity when ball hits vertical walls
				if (hitLeftWall(BALL) || hitRightWall(BALL)) {
					vx=-vx;
				}
				//reverses direction of ball velocity when ball hits the top horizontal walls
				if(hitTopWall(BALL)) {
					vy=-vy;
				}
				//game termination condition
				if(hitBottomWall(BALL)) {
					//removes the ball when it hits the bottom wall because the game has ended
					remove(BALL);
					livesLeft= livesLeft-1;
					//creates a box to display an end of game message in 
					makeEndBox();
					//creates the label so it's dimensions can be used for centering calculations
					GLabel youLose= new GLabel("Lives Left: Click to Continue ");
					//centers the label with respect to the box
					double label_x_startpoint = (getWidth()-ENDBOXWIDTH)/2 + ENDBOXWIDTH/2 - youLose.getWidth()/2;
					double label_y_startpoint = (getHeight()-ENDBOXHEIGHT)/2 + ENDBOXHEIGHT/2 + youLose.getAscent()/2;
					youLose= new GLabel ("Lives left: " + livesLeft + " Click to Continue" ,label_x_startpoint, label_y_startpoint);
					//adds the label to the box
					add(youLose);
					//pauses the game until the user clicks to start next round
					waitForClick();
					//removes end of turn message
					remove(ENDBOX);
					remove(youLose);
					break;
				}

				//moves the ball.
				BALL.move (vx,vy);
				//makes ball movement visible to the user
				pause(DELAY);
			}
		}
		if (BRICKS_REMAINING == 0) {
			//removes ball when game ends
			remove (BALL);
			//creates winning message
			makeEndBox();
			makeWinMessage();

		}
		//removes bsll at the end of the game
		remove(BALL);
		//delivers game over message when no lives are left
		if (livesLeft == 0) {
			makeEndBox();
			makeGameOverMessage();
		}
	}

	private GObject getCollidingObject() {
		double x= BALL.getX();
		double y= BALL.getY();
		//establishes the collision variable
		//specifies top left corner location
		GObject collision = getElementAt (x, y);
		if (collision == null) {
			//specifies top right corner location
			collision = getElementAt (x, y + 2*BALL_RADIUS);
		}
		if (collision == null){
			//specified bottom left corner location
			collision = getElementAt (x + 2*BALL_RADIUS, y);
		}
		//specifies bottom right corner location
		if (collision == null) {
			collision= getElementAt ( x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
		}
		return collision;
	}
	private void makeEndBox() {
		// establishes dimensions for centering box
		double boxCenterX= (getWidth()-ENDBOXWIDTH)/2;
		double boxCenterY= (getHeight()-ENDBOXHEIGHT)/2;
		//places box in the center of the screen
		ENDBOX= new GRect (boxCenterX, boxCenterY, ENDBOXWIDTH, ENDBOXHEIGHT);
		add(ENDBOX);
	}
	private void makeWinMessage(){
		//creates the label so it's dimensions can be used for centering calculations
		GLabel youWin= new GLabel("YOU WIN!");
		//centers the label with respect to the box
		double label_x_startpoint = (getWidth()-ENDBOXWIDTH)/2+ ENDBOXWIDTH/2 - youWin.getWidth()/2;
		double label_y_startpoint = (getHeight()-ENDBOXHEIGHT)/2 + ENDBOXHEIGHT/2 + youWin.getAscent()/2;
		youWin= new GLabel ("YOU WIN!",label_x_startpoint, label_y_startpoint);
		//places winning label on box
		add(youWin);
	}
	private void makeGameOverMessage() {
		//creates the label so it's dimensions can be used for centering calculations
		GLabel gameOver = new GLabel("GAME OVER!");
		//centers the label with respect to the box
		double label_x_startpoint = (getWidth()-ENDBOXWIDTH)/2+ ENDBOXWIDTH/2 - gameOver.getWidth()/2;
		double label_y_startpoint = (getHeight()-ENDBOXHEIGHT)/2 + ENDBOXHEIGHT/2 + gameOver.getAscent()/2;
		gameOver= new GLabel ("GAME OVER!",label_x_startpoint, label_y_startpoint);
		//places game over label on box
		add(gameOver);
	}
	private void checkForCollision() {
		//sets new variable object 'collider' equal to object returned from previously written collision check method
		COLLIDER = getCollidingObject();
		//establishes changes to ball motion in response to collision with paddle
		if (COLLIDER == PADDLE) {
			//absolute value ensures that vy will always have a negative value and move up the screen after hitting paddle
			vy =-Math.abs(vy);
		}
		//establishes changes in ball's motion and brick removal when a brick is hit
		if (COLLIDER!= null && COLLIDER != PADDLE) {
			//removes brick that ball collided with
			remove (COLLIDER);
			//keeps track of the number of bricks remaining on the screen to later determine when the game is won
			BRICKS_REMAINING = BRICKS_REMAINING-1;
			//reverses vertical direction of ball following collision.  causes it to "bounce off".
			vy =-vy;
		} 
	}	
	//creates a paddle that follows the mouse
	public void  mouseMoved (MouseEvent e) {
		//removes the initial paddle that was placed or the paddle created by the previous mouse event
		remove(PADDLE);
		double paddleY= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		//instructs the new rectangle to be created with its horizontal center at the location of the mouse
		double maxX= getWidth()-PADDLE_WIDTH;
		double minX= 0;
		//determines the x location of the paddle if the mouse is within the boundaries of the screen
		if (e.getX() < maxX && e.getX() > minX) {
			PADDLE = new GRect (e.getX(), paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
			//colors paddle black
			PADDLE.setFilled(true);
			PADDLE.setColor(Color.BLACK);
			add(PADDLE);	
			//determines the x location of the paddle if the mouse is to the right of the screen
		} if (e.getX()> maxX) {
			PADDLE = new GRect (maxX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
			//colors paddle black
			PADDLE.setFilled(true);
			PADDLE.setColor(Color.BLACK);
			add(PADDLE);	
		}
		//determines the x location of the paddle if the mouse is to the left of the screen
		if (e.getX()< minX) {
			PADDLE = new GRect (minX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
			//colors paddle black
			PADDLE.setFilled(true);
			PADDLE.setColor(Color.BLACK);
			add(PADDLE);	
		}
	}
}