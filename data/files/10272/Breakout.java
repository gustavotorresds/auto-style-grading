/*
 * File: Breakout.java
 * -------------------
 * Name: Aron Nunez
 * Section Leader: Akua McLeod
 * 
 * This file will eventually implement the game of Breakout. A game
 * where the user moves a paddle across a fixed x-axis and rebounds
 * a ball that removes bricks at it comes in contact with them. If 
 * after a fixed number of tries, the user does nor remove all bricks,
 * the game is lost. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-24";
	
	// Instance variables for velocities, bricks gone, and the 
	double vx, vy;
	int bricksGone;
	
	// Instance variable for the objects: paddle, ball, and stored collider
	GRect paddle;
	GOval ball;
	GObject collider;

	// Creates shared Random Generator instance variable used for velocities
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		/* Set up game: Constructs the bricks, adds the paddle,
		 * and adds the ball used in the game. Finally adding mouse 
		 * listeners lets the program read the user input.
		 */
		setRows(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN);
		paddle = makePaddle();
		addMouseListeners();
		ball = makeBall();
		
		/* Run game: For the instance variable, number of terms, continue to set
		 * the ball and it's path while counting how many bricks are removed. If 
		 * bricks are not removed by the variable, NTURNS, the game is over. 
		*/
		for(int i = 0; i < NTURNS; i++) {
			if (!wonGame()) {
				if (i == NTURNS -1 ) {
					setBallPath();
					gameOver();
					break;
				}
				setBallPath();
				tryAgain();
			} 
		}
	}
	
	/* This GLabel "win" prints out the statement, "You Won!", 
	 * on the screen after the user has removes all bricks. 
	 */
	private void gameWon() {
		GLabel win= new GLabel ("You Won!");
		add(win, CANVAS_WIDTH/2 - win.getWidth() , CANVAS_HEIGHT/2);
		win.setFont(SCREEN_FONT);
		add(win);
	}

	/* Although similarly labeled to gameWon, this boolean returns
	 * true when bricksGone equals the number of bricks in the game,
	 * or the number of rows multiplied by the number of columns. The
	 * ball is removed, and the gameWon label is added.
	 */
	private boolean wonGame() {
		if (bricksGone == (NBRICK_ROWS * NBRICK_COLUMNS)) {
			remove(ball);
			gameWon();
			return true;
		}
		return false;
	} 

	/* The method, "tryAgain", tests if the ball hits the bottom wall,
	 * once the ball hits the bottom wall, the ball is removed,
	 * and the label "Try Again" is momentarily added to the screen. 
	 * A new ball is also added to begin the next try.
	 */
	private void tryAgain() {
		if (hitBottomWall(ball)) {
			remove(ball);
			GLabel tryAgain = new GLabel ("Try Again");
			add(tryAgain, CANVAS_WIDTH/2 - tryAgain.getWidth(), CANVAS_HEIGHT/2);
			tryAgain.setFont(SCREEN_FONT);
			add(tryAgain);
			pause(2000);
			remove(tryAgain);
			ball = makeBall();
		}
	}

	/* This GLabel is used to let the user know they have ran out
	 * of turns. The ball is removed after their last try and a new
	 * label is created with the message, "You Lost!" in the center
	 * of the screen. 
	 */
	private void gameOver() {
			remove(ball);
			GLabel loss = new GLabel ("You Lost!");
			add(loss, CANVAS_WIDTH/2 - loss.getWidth(), CANVAS_HEIGHT/2);
			loss.setFont(SCREEN_FONT);
			add(loss);
			pause(2500);
		}
	
	/* This method, checkForObj(), is used to check for objects of collision.
	 * As the ball bounces around the screen, this method tests if the objects
	 * the ball interact with are the paddle (in which case velocity is changed
	 * with respect to an absolute value as to not make the paddle collide with the
	 * ball), the bricks (in which case the brick is removed and added to brick
	 * counter, which is printed on the console as a check of a functioning 
	 * program). The velocity of the ball is then changed to the other direction.
	 * 
	 */
	private void checkForObj() { 
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy); 
		} else if (collider != null) {
			bricksGone++;
			println(bricksGone);
			remove (collider);
			vy= -vy;
			}
	}		

	/* This method, getCollidingObject(), is used to directly store if an object 
	 * the ball has collided with is present. Because the ball is inscribed 
	 * within a box, we use the four corners to check for elements. If an object is
	 * present, we store the object as a collider in the checkForObj() method
	 * of our program.
	 * 
	 */
	private GObject getCollidingObject() {
		double checkX = ball.getX() + BALL_RADIUS*2;
		double checkY = ball.getY() + BALL_RADIUS*2;
		GObject colliderTopLeft = getElementAt(ball.getX(), ball.getY()); 
		GObject colliderTopRight = getElementAt(checkX, checkY);
		GObject colliderBottomLeft = getElementAt(ball.getX(), checkY);
		GObject colliderBottomRight = getElementAt(checkX, ball.getY());
		if (colliderTopLeft != null) {
			return colliderTopLeft;
		} else if (colliderTopRight != null) {
			return colliderTopRight;
		} else if (colliderBottomLeft != null) {
			return colliderBottomLeft;
		} else if (colliderBottomRight != null) {
			return colliderBottomRight;
		}
		return null;
	}

	/* This method sets the ball path. After waiting for a click from the user, 
	 * the ball is directed to move according to the velocities of the randomly
	 * generated horizontal velocity (vx), and set vertical velocity. While the 
	 * game has not been won or within a try, the ball checks for an object
	 * while bouncing off walls and changing velocities if it encounters a wall.
	 */
	private void setBallPath() {
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (!wonGame() && !hitBottomWall(ball)) {
			checkForObj();
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
		}

	// This boolean returns the bottom wall limit as the canvas height
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= CANVAS_HEIGHT;
	}
	
	// This boolean returns the top wall limit as a Y value of 0
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	// This boolean returns the right wall limit as the canvas width
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= CANVAS_WIDTH;
	}
	
	// This boolean returns the left wall limit as an X value of 0
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/* This method, GOval makeBall() makes the ball used in the game. 
	*  The ball is made to size of the instructions and added to the 
	*  center of the screen.
	*/
	private GOval makeBall() {
		double ballSize = BALL_RADIUS*2;
		GOval ball = new GOval (ballSize, ballSize);
		ball.setFilled (true);
		add(ball, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		return(ball);
	}

	/* This method mouseMoved, calls for the listening of 
	 * the mouse even when the mouse is moved across the screen. 
	 * By reading the x and y coordinates of the mouse, the
	 * paddle is moved directly to where the user allocates it.
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		double Y= CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		if (mouseX < CANVAS_WIDTH-PADDLE_WIDTH) {
			paddle.setLocation (mouseX, Y);
		}
	}
	
	/* This method makePaddle, makes our paddle using GRect
	 * and places it in the center of the screen with the 
	 * appropriate dimensions. The paddle is set in accordance to
	 * the desired offset of the paddle, or acsent from the bottom
	 * wall. 
	 */
	private GRect makePaddle() {
		double x = CANVAS_WIDTH/2 - PADDLE_WIDTH/2;
		double y = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, x, y);
		return(paddle);
	}

	/* This method is used to set the rows of bricks of the game. 
	 * Using a for loop, for the number of rows, a certain number
	 * of columns are made. If a row is a ratio of the number of rows, 
	 * then the row is given a specific color called in the run method
	 * of the program. In this particular case, every two rows are given a color,
	 * thus the "floating numbers" represent rows and increase by 2 after 
	 * every if/else statement. 
	 */
	private void setRows(Color color1, Color color2, Color color3, Color color4, Color color5) {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int brick = 0; brick < NBRICK_COLUMNS; brick++ ) {
				double x = BRICK_SEP + (BRICK_WIDTH*brick);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT*row);
				GRect rect = new GRect ((x + BRICK_SEP*brick), y + (BRICK_SEP*row), BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (row < 2) {
				rect.setColor(color1);
				} else if (row < 4) { 
				rect.setColor(color2);
				} else if (row < 6) {
				rect.setColor(color3);
				} else if (row < 8) {
				rect.setColor(color4);
				} else {
				rect.setColor(color5);
				}
				add (rect);
			}
		}
		
		
	}
}
