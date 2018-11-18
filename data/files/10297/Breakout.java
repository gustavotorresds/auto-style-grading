/*
 * File: Breakout.java
 * -------------------
 * Name: Matthias Abebe
 * Section Leader: Julia Truitt
 * This File will create the game breakout, the objective of the game 
 * is to use the bouncing ball and moveable paddle to "break" and remove
 * all bricks from the screen. If the moving ball is missed with the paddle,
 * the ball will fall and the player will lose a turn. The player gets 3 turns
 * to beat the game. There are 100 bricks, although the amount of bricks, and turns
 * are adjustable.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

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
	public static final double BALL_DIAMETER = 20;

	// The ball's vertical velocity.
	public static double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//total number of bricks 
	public static final int NBRICKS = NBRICK_ROWS*NBRICK_COLUMNS;

	public static final double CENTER_ROW = (BRICK_SEP/2 + (CANVAS_WIDTH/2 - (NBRICK_COLUMNS*BRICK_WIDTH + (BRICK_SEP*NBRICK_COLUMNS))/2));

	private RandomGenerator rgen = RandomGenerator.getInstance();	

	//instance variables created by programmer
	private double vx, vy;
	int turnCounter = 0;
	int brickCounter = NBRICKS;
	int colorCounter = 0;
	GRect paddle = makePaddle();
	GOval ball = makeBall();


	public void run() {
		addMouseListeners();
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		for (int turnCounter = 0; turnCounter < NTURNS; turnCounter++) {
			add(ball, CANVAS_WIDTH/2 - BALL_DIAMETER/4, CANVAS_HEIGHT/2 - BALL_DIAMETER/4);
			waitForClick();
			ballSpeed();
			while(true) {
				ball.move(vx,vy);
				pause(DELAY);
				changeDirection();
				if(ball.getY() >= CANVAS_HEIGHT - BALL_DIAMETER) {
					remove(ball);
					break;
				}
				remove();
				//Indicates the player won, so breaks out of animation loop
				if(brickCounter == 0) {
					break;
				}
			}
			//indicates the plater won, so breaks out of game play loop
			if(brickCounter == 0) {
				break;
			}
		}
		//displays messages depending on win or loss and removes paddle from screen.
		if(brickCounter == 0) {
			youWon();
			remove(paddle);
		}
		if(brickCounter > 0) {
			youLost();
			remove(paddle);
		}
	}

	/*
	 * This method uses a random generator to determine the initial x-velocity and x-direction in which the ball will travel when the user clicks
	 * the screen.
	 */
	private void ballSpeed() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	/*
	 * This method creates the bricks for the game, the number of rows is determined by the NBRICK_ROWS so that this constant is able to be changed,
	 * if the user would like a larger or smaller amount of rows they are able to change NBRICK_ROWS, this is able to do be done for "columns" as well,
	 * for every row made there are NBRICK_COLUMMNS, or number of bricks made. The color counter within the method counts how many rows are being made
	 * and alternates colors every two rows in a pattern. If colorCounter reaches 10 it will reset to zero to allow more rows to be made.
	 */
	public void createBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			if(colorCounter == 10) {
				colorCounter = 0;
			}
			colorCounter++;
			for(int brick = 0; brick < NBRICK_COLUMNS;  brick++) {
				double xPosition = (CENTER_ROW + brick*BRICK_WIDTH + brick*BRICK_SEP);
				double yPosition = (BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP);
				GRect bricks = new GRect(xPosition, yPosition, BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);
				if (colorCounter == 1 || colorCounter == 2) {
					bricks.setColor(Color.RED);
				}

				if (colorCounter == 3 || colorCounter == 4) {
					bricks.setColor(Color.ORANGE);
				}
				if (colorCounter == 5 || colorCounter == 6) {
					bricks.setColor(Color.YELLOW);
				}
				if (colorCounter == 7 || colorCounter == 8) {
					bricks.setColor(Color.GREEN);
				}
				if (colorCounter == 9 || colorCounter == 10) {
					bricks.setColor(Color.CYAN);
				}

				add(bricks);
			}
		}
	}

	/*
	 * This method creates a GOval which will serve as our ball and return it to the console so the scope of the ball is available to 
	 * all methods.
	 */
	public GOval makeBall() {
		GOval ball = new GOval(BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, CANVAS_WIDTH/2 - BALL_DIAMETER/4, CANVAS_HEIGHT/2 - BALL_DIAMETER/4);
		return ball;
	}

	/*
	 * This method creates a GRect which will serve as our paddle and return it to the console so the scope of the ball is available to 
	 * all methods.
	 */
	public GRect makePaddle() { 
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		return paddle;
	}

	/*
	 * This method is a mouse event which tracks the movement of the mouse and sets the xposition of the paddle to be where the mouse is, the
	 * mouse will be in the middle of the paddle. 
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		if (mouseX <= (CANVAS_WIDTH - PADDLE_WIDTH/2) && mouseX >= 0 + PADDLE_WIDTH/2) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, (CANVAS_HEIGHT- PADDLE_WIDTH -PADDLE_Y_OFFSET));
		}
	}

	/*
	 * This method promts the ball to change direction if it hits the "walls" of the canvas, setting boundaries for where the ball can travel. 
	 */
	private void changeDirection() {
		if(ball.getY() <= 0) {
			vy = -vy;
		}
		if(ball.getX() <= 0) {
			vx = -vx;
		}
		if(ball.getX() >= CANVAS_WIDTH - BALL_DIAMETER) {
			vx = -vx;
		}
	}

	/*
	 * This method will check each time the ball moves if it has collided with another object, it does this by checking collisions
	 * with each theoretical corner of the ball at each moment the ball moves with other GObjects. 
	 */
	private GObject checkBrickCollisions() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
		} else if (getElementAt(ball.getX() , ball.getY() + BALL_DIAMETER) != null) {
			return getElementAt(ball.getX() , ball.getY() + BALL_DIAMETER);
		} else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER) != null) {
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
		} else {
			return null;
		}
	}

	/*
	 * This method functions when the ball collides with another GObeject, checks if it is the paddle and if it is not the paddle it
	 * removes the brick, changes y direction and subtracts from the brick counter. If it is the paddle it simply changes y direction.
	 */
	private void remove() {
		GObject collisionObject = checkBrickCollisions();
		if (collisionObject == paddle) {
			vy =-vy;
		}
		else if (collisionObject != null) {
			remove(collisionObject); 
			brickCounter--;
			vy = -vy;
		}
	}

	/*
	 *  If the player succesfully removes all bricks, this method will be enacted and display the message "YOU WON!" on the screen.
	 */
	private void youWon() {
		blankScreen();
		GLabel won = new GLabel("YOU WON!"); 
		won.setColor(Color.BLACK);
		add(won, CANVAS_WIDTH/2 - won.getWidth()/2, CANVAS_HEIGHT/2);

	}

	/*
	 *  If the player does not succesfully remove all bricks in 3 turns, this method will be enacted and display the message "YOU LOST!" on the screen.
	 */
	private void youLost() {
		blankScreen();
		GLabel lost = new GLabel("YOU LOST!"); 
		lost.setColor(Color.BLACK);
		add(lost, CANVAS_WIDTH/2 - lost.getWidth()/2, CANVAS_HEIGHT/2 );

	}

	/*
	 * This creates a rectangle which covers the entirety of the screen when the game is over so a message is able to be displayed. 
	 */
	private void  blankScreen() {
		GRect whiteBox = new GRect(0,0, CANVAS_WIDTH, CANVAS_HEIGHT); 
		whiteBox.setFilled(true);
		whiteBox.setColor(Color.WHITE);
		add(whiteBox);
	}
}