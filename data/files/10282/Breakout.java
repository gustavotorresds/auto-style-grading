/*
 * File: Breakout.java
 * -------------------
 * Name:Shawn Filer 
 * Section Leader: Chase Davis 
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle = null;
	private GOval ball = null;
	private double paddleX = 0;
	private double paddleY = 0;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vy = -VELOCITY_Y;
	private double vx = 0;
	
	
	public void run() {
		buildGameBoard();
		breakOut();
	}

private void breakOut() {
	vx = rgen.nextDouble(1.0, 3.0);
	GObject collider = null;
	int brickAmount = 100;
	for (int i = 0; i < NTURNS; i++) {
		waitForClick();
		if (ball == null) {
			ball = new GOval (getWidth()/2 - BALL_RADIUS, getHeight()/2 + BALL_RADIUS, BALL_RADIUS,BALL_RADIUS);
			ball.setFilled(true);
			add(ball);
			if (rgen.nextBoolean(.5)) vx = -vx;
		}
		moveBall(collider, brickAmount);
	}
	//lose message 
	if (brickAmount != 0) {
		GLabel gameOver = new GLabel ("GAME OVER");
		gameOver.setLocation(getWidth()/2 - gameOver.getWidth()/2,getHeight()/2+ gameOver.getHeight());
		add(gameOver);
	}
		else {
	 //win message
		GLabel dubCity = new GLabel ("CONGRADULATIONS YOU WON");
			dubCity.setLocation(getWidth()/2 - dubCity.getWidth()/2,getHeight()/2+ dubCity.getHeight());
			add(dubCity);
	}
}
		
/**moves the ball and checks for collisions, keeps track of amount of bricks left
 * and keeps track of the amount of turns 	
 * @param collider
 * @param brickAmount
 */
private void moveBall (GObject collider, int brickAmount) {
	while(true) {
		// update visualization
		ball.move(vx, vy);
		double ballX = ball.getX();
		double ballY = ball.getY();
		
		// update velocity
		if (ball!=null) {
			if(hitLeftWall(ball) || hitRightWall(ball)) 
				vx = -vx;
		}
		if (ball!=null) {
			if(hitCeiling(ball)) 
				vy = -vy;
		}
		if (ball != null) {
			if(hitFloor(ball)) {
				remove(ball);
				ball = null;
				break;
			}
		}	

		// pause
		pause(DELAY);
			
		//check for collision
		collider = getCollidingObject(collider, ballX, ballY);
		if (collider != paddle && collider != null) {
			remove(collider);
			brickAmount --;
			if (collider != null)
				vy = -vy;
		}
		if(collider == paddle)
			vy = -Math.abs(vy);
		if(brickAmount == 0){
			remove(ball);
			break;
		} 
	}
	
}
		
/** Checks for collision on the four corners of the ball
 * @param collider
 * @param x
 * @param y
 * @return
 */
private GObject  getCollidingObject(GObject collider, double x, double y) { 
	collider = getElementAt(x, y);
	if(collider == null) {
		collider = getElementAt(x + 2*BALL_RADIUS, y);
		if (collider == null) {
			collider = getElementAt(x, y + 2*BALL_RADIUS);
			if (collider == null) 
				collider = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
		}
	}
	return collider;

}

/**Tracks motion of the mouse and syncs the 
 * mouse x coordinate to that of the mouse 
 */
public void mouseMoved(MouseEvent e){
	double mouseX = e.getX();
	paddle.setLocation(mouseX, paddleY);
	paddleX = mouseX;
}

/**All of the hit booleans check for collision of walls
 * @param ball2
 * @return
 */
private boolean hitFloor(GOval ball2) {
	return ball2.getY() > getHeight() - 2 * BALL_RADIUS;
}
private boolean hitCeiling(GOval ball2) {
	return ball2.getY() <= 0 + BALL_RADIUS;
}
private boolean hitRightWall(GOval ball2) {
	return ball2.getX() >= getWidth() - 2 * BALL_RADIUS;
}
private boolean hitLeftWall(GOval ball2) {
	return ball2.getX() <= 0 + 2 * BALL_RADIUS;
}

/**Builds the Game Board 
 */
private void buildGameBoard() {
	// Set the window's title bar text
	setTitle("CS 106A Breakout");

	// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
	// and getHeight() to get the screen dimensions, not these constants!
	setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

	//Build the colored bricks
	for(int stacks = 0; stacks < NBRICK_ROWS; stacks ++) {
		for(int bricks = 0; bricks < NBRICK_COLUMNS; bricks++) {

			//construction parameters 
			double startY = 70;
			double x = BRICK_SEP * 3/2 + (BRICK_WIDTH + BRICK_SEP)* bricks;
			double y = startY + (BRICK_HEIGHT + BRICK_SEP)* stacks;


			GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);

			fillColor(rect,stacks);		
			add(rect);
		}
	}

	//Build the paddle so that its position can be altered
	paddleX = getWidth()/2 - PADDLE_WIDTH/2;
	paddleY = getHeight() - PADDLE_Y_OFFSET;
	paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH,PADDLE_HEIGHT);
	paddle.setFilled(true);
	add(paddle);
	
	addMouseListeners();

}

/**Fills color of the rows of the game board 
 * based on their sequential order 
 */

/**Fills the bricks color dependent on the row that they are in*/
private void fillColor(GRect rect, int stacks) {
	if(stacks == 0 || stacks == 1) 
		rect.setColor(Color.RED);

	if(stacks == 2 || stacks == 3) 
		rect.setColor(Color.ORANGE);

	if(stacks == 4 || stacks == 5) 
		rect.setColor(Color.YELLOW);	

	if(stacks == 6 || stacks == 7) 
		rect.setColor(Color.GREEN);	

	if(stacks == 8 || stacks == 9) 
		rect.setColor(Color.CYAN);	

}
}
