/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
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
	
	// Random-number generator for vx.
	//vx is the distance moved by the ball in the x-direction and vy is the distance moved by the ball in the y-direction.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = 0;
	private double vy = 3;
	
	//Game ball 
	private static GOval BALL = null;
	
	//Game paddle
	private static GRect PADDLE = null;
	
	// Label showing number of lives left
	private GLabel liveLabel = null;
	
	
	
	/* This method runs the breakout program*/

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		createGame();
		startGame();
	}
	/* The mouseMoves method moves the paddle when the mouse is moved*/
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = PADDLE.getY();
		makePaddleMove(x,y);
		
	}
	// The makePaddleMove method makes the paddles move when the mouse is moved. The paddle is kept centered at (u,v) where u represents the x-coordinate of the mouse's position and v represents the y-coordinate of the mouse's position at any point in time.
	private void  makePaddleMove(double u, double v) {
		double x = u - PADDLE_WIDTH/2.0;
		add(PADDLE, u,v);	
	}
	// This method creates the game's setup.
	private void createGame() {
		layBricks();
		displayLivesLabel();
		makePaddle();
	}
/* This method lays a specific number of bricks according to specific dimensions, and spacing. The bricks are laid at a specific distance from the top of the window to the first line of bricks.
	 The color of the bricks remain constant for two rows and run in the following rainbow-like sequence: RED, ORANGE, YELLOW, GREEN, CYAN.*/
	private void layBricks() {
		for(int i=0; i < NBRICK_ROWS; i++) {
			for (int j=0; j< NBRICK_COLUMNS; j++) {
				GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				double x = getWidth()/2.0 - NBRICK_COLUMNS * BRICK_WIDTH/2.0 - (NBRICK_COLUMNS -1)* BRICK_SEP/2.0 *BRICK_WIDTH/2.0;
				double y = BRICK_Y_OFFSET + i*BRICK_HEIGHT + BRICK_SEP;
				rect.setFilled(true);
				colorBricks(i,rect);
				add(rect,x,y);
			}
		}
	}
	/* This method fills the bricks in each row with color,with the color remaining constant for two rows and run in the following rainbow-like sequence: RED, ORANGE, YELLOW, GREEN, CYAN. i specifies the row to be colored, and rect specifies the brick to be colored.*/
	private void colorBricks(int i, GRect rect) {
	if((i==0)||(i==1)) {
		rect.setColor(Color.RED);
	}
	else if ((1==2)||(i==3)) {
		rect.setColor(Color.ORANGE);
	}
	else if ((1==4)||(i==5)) {
		rect.setColor(Color.YELLOW);
	}
	else if ((1==6)||(i==7)) {
		rect.setColor(Color.GREEN);
}
	else if ((1==8)||(i==9)) {
		rect.setColor(Color.CYAN);
	}
	}
	
	/* This method creates a label to display the number of lives a player has before they "die" the game ends*/
private void displayLivesLabel() {
	GLabel livesLabel = new GLabel("You have:" + NTURNS + "lives left.");
	livesLabel.setFont("Courier-20");
	add(livesLabel, getWidth()-livesLabel.getWidth(),livesLabel.getHeight());
}

/*This method creates the paddle used in playing the game according to a specific shape and position (distance relative to the bottom of the window.*/
private void makePaddle() {
	GRect PADDLE = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
	double y = getHeight() - PADDLE_HEIGHT-PADDLE_Y_OFFSET;
	PADDLE.setFilled(true);
	PADDLE.setColor(Color.BLACK);
	add(PADDLE, x,y);	
}

/*This method creates the ball, enables it to move*/
private void startGame() {
	livesLabel.setLabel("Lives Left:" + NTURNS );
	for (int i = 0; i < NTURNS; i++) {
	livesLabel.setLabel("Lives Left:" + (NTURNS - i));
	createBall();
	makeBallMove();
	if(allBricksGone()) {
		GLabel gameWinner = new GLabel ("CONGRATULATIONS! GAME WON!!");// message displayed when player wins game
		add(gameWinner, getWidth()/2.0 - gameWinner.getWidth()/2.0, getHeight()/2.0 + gameWinner.getAscent()/2.0);
	}
}
livesLabel.setLabel("You have no lives left"); // message displayed when player runs out of lives.
GLabel gameOver = new GLabel (" You've ran outta lives. Game over! :-(");
gameOver.setColor(Color.RED);
add(gameOver, getWidth()/2.0 - gameOver.getWidth()/2.0, getHeight()/2.0 + gameOver.getAscent()/2.0);
	}

	/*This method returns true only if there aren't any bricks left on the screen. Otherwise, it returns false*/
private boolean allBricksGone() {
	for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
			double x = getWidth()/2.0 - NBRICK_COLUMNS * BRICK_WIDTH/2.0 - (NBRICK_COLUMNS-1)*BRICK_SEP/2.0  + j * (BRICK_WIDTH + BRICK_SEP);
			double y = BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP);
			GObject brick = getElementAt(x,y);
			if(brick!= null) {
				return false;
			}
}
	}
return true;
}
/* Creates the ball according to a specific color and radius and then centers it on the screen.*/
private void createBall() {
	GOval BALL = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
	double x = getWidth()/2.0 - BALL_RADIUS;
	double y = getHeight()/2.0 - BALL_RADIUS;
	BALL.setColor(Color.BLACK);
	add(BALL, x,y);
}

/*This method makes the ball move in both a randomly generated x-direction and a specified y-direction. It checks for collisions.*/
private void makeBallMove() {
	vx = rgen.nextDouble(1.0, 3.0);
	if(rgen.nextBoolean(0.5)) vx = -vx;
	while(!performDeathCheck()) {
		BALL.move(vx, vy);
		performCollisionsCheck();
		pause(10); //creates a pause
	}
	}
	/*this method returns true any time the ball goes beyond the lower end of the window.*/
private  boolean performDeathCheck() {
	if(ballHitsLowerWall()) {
		return true;
	}
	else {
		return false;
	}			
	}
	/*This method checks for collisions and changes the ball's direction.*/
private void performCollisionsCheck() {
	if(ballHitsUpperWall()) {
		playSound();
		vy = -vy;	// changing the y-direction if ball hits top wall
	}
	if(ballHitsPaddle()) {
		playSound();
		vy = -Math.abs(vy);//making sure the ball's velocity is always negative after collision with paddle
	}
	if(ballHitsLeftWall()) {
		playSound();
		vx = -vx; //making the ball go in the direction opposite to itd direction before impact.
	}
	if (ballHitsRightWall()) {
		playSound();
		vx = -vx;
	}
	if(ballHitsBrick()) {
		playSound();
		vy=-vy; // changes the ball's y-direction after collision with brick.
	}
	if (ballHitsLowerWall());
	remove(BALL);// makes the game restart whenever the ball goes beyond the window.
	return;
}

/*This method removes brick when the ball collides with the brick and returns true */
private boolean ballHitsBrick() {
	GObject i = pickCollidingObjects();
	if(i != PADDLE && i != livesLabel && i != null) {
		remove(i);
		return true;
	}
	else {
		return false;
	}
	}
/*This method returns true if whenever the ball hits the window's left wall*/
private boolean ballHitsLeftWall() {
	GObject i = pickCollidingObjects();
	if(i==PADDLE) {
		return true;
	}
	else {
		return false;
	}
	}
/*This method returns true whenever the ball hits the paddle. Otherwise, it returns false*/
private boolean ballHitsPaddle() {
	GObject i = pickCollidingObjects();
	if(i==PADDLE) {
		return true;
	}
	else {
		return false;
	}
}

/*This methd returns true whenever the ball hits the window's upper wall.*/
private boolean ballHitsUpperWall() {
	return BALL.getY()<=0;
}

/*This method returns true whenever the ball hits the window's right wall*/
private boolean ballHitsRightWall() {
	return BALL.getX() > (getWidth()-BALL.getWidth());
}

/*This method returns true whenever the ball hits the window's lower wall */
private boolean ballHitsLowerWall() {
	return BALL.getY()>(getHeight()-BALL.getHeight());
}

/*This method checks for objects at the wall's corners and returns true if there's an object or false if otherwise*/
private GObject pickCollidingObjects() {
	double X_left = BALL.getX();
	double X_right = BALL.getX() + BALL.getWidth();
	double Y_upper = BALL.getY();
	double Y_lower = BALL.getY() + BALL.getHeight();
	
	GObject collision_1 = getElementAt(X_left,Y_upper); // for the upper left corner
	if(collision_1 != null) {
		return collision_1;	
	}
	GObject collision_2 = getElementAt(X_left,Y_lower); // for the lower left corner
	if(collision_2 != null) {
		return collision_2;	
	}
	GObject collision_3 = getElementAt(X_right,Y_upper); // for the upper right corner
	if(collision_3 != null) {
		return collision_3;	
	}
	GObject collision_4 = getElementAt(X_right,Y_lower); // for the lower right corner
	if(collision_4 != null) {
		return collision_4;	
}
	return null;
}//returns false if there's no object at the four corners.
	/* This method plays a sound*/
	private void playSound() {
		AudioClip bouncClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}
}


	

