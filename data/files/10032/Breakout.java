/*
 * File: Breakout.java
 * -------------------
 * Name:Maddie
 * Section Leader:Andy
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
	
	//Offset of the bricksLeft label, in pixels, from the top left corner.
	public static final int BRICKS_LEFT_OFFSET = 20;
	
	//factor by which the ball's speed picks up after 10 bricks are removed
	public static final double SPEED_UP_FACTOR = 1.5;
	
	//the amount of time, in milliseconds, that the program pauses before speeding
	//up the game.
	public static final double SPEED_UP_PAUSE = 2000;

	public void run() {
		setup();
		addMouseListeners();
		for (int i = NTURNS; i > 0; i--){
			startMessage(i);
			drawBall();
			vy = VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);				
			if (rgen.nextBoolean(0.5)) vx = -vx;
			gameplay(nBricksLeft);
			if (nBricksLeft == 0) break;
				
		}
		printFinishMessage();
	}
	/*method drawBricks
	 * ***************
	 * draws NBRICK_ROWS number of rows of bricks in the graphics window.
	 * 
	 */
	private void drawBricks(){
		double x = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH) 
				- ((NBRICK_COLUMNS - 1) * BRICK_SEP))/2.0;
		double y = BRICK_Y_OFFSET;
		for (int i = 0; i < NBRICK_ROWS; i++){
			Color color = brickColor(i);
			drawBrickRow (x, y, color);
			y += (BRICK_HEIGHT + BRICK_SEP);
		}
	}
	/* method drawBrickRow
	 * ******************
	 * draws one row of bricks, with NBRICK_COLUMNS number of bricks, on the screen.
	 * Parameters:
	 * double x: the x value of the leftmost brick in the row.
	 * double y: the y value of all bricks in the row.
	 * Color color: the color of all the bricks in the row.
	 * 
	 */
	private void drawBrickRow (double x, double y, Color color){
		for (int i = 0; i < NBRICK_COLUMNS; i++){
			GRect brick = new GRect (x , y,  BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
			x += (BRICK_WIDTH + BRICK_SEP);
		}
	}
	/*method brickColor
	 * ***************
	 * determines the color of a row of bricks
	 * parameter: int i: the number of the row to be colored. Counting
	 * starts at 0 with the topmost row.
	 * 
	 */
	private Color brickColor(int i){
		switch (i){
		case 0:
		case 1: return Color.RED;
		case 2:
		case 3: return Color.ORANGE;
		case 4:
		case 5: return Color.YELLOW;
		case 6:
		case 7: return Color.GREEN;
		case 8:
		case 9: return Color.CYAN;
		default:return null;
		}
	}
	/*method drawPaddle
	 * **************
	 * draws the paddle in the graphics window, with y value
	 * PADDLE_Y_OFFSET, and centered in the x
	 * 
	 */
	private void drawPaddle(){
		paddle = new GRect ((getWidth()-PADDLE_WIDTH)/2.0, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	/*method mouseMoved
	 * ***************
	 * allows the user to control the paddle with the mouse.
	 * While the mouse is in the graphics window, the x coordinate of the mouse
	 * matches that of the paddle.
	 * 
	 */
	public void mouseMoved(MouseEvent e){
		double mouseX = e.getX();
		if (mouseX > 0 && mouseX < (getWidth()-PADDLE_WIDTH)){
			paddle.setX(mouseX);
		}
	}
	/*method drawBall
	 * **************
	 * Draws the ball in the middle of the canvas.
	 * 
	 */
	private void drawBall(){
		ball = new GOval ((getWidth()/2.0 - BALL_RADIUS) , (getHeight()/2.0 - BALL_RADIUS),
				2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	/*method gameplay
	 * ***************
	 * Animates the ball so that it moves around the screen and bounces off both
	 * 	the walls and objects on the screen. Bricks are removed once they are hit.
	 * Method ends in two ways: 1), the ball falls below the bottom edge of the screen
	 * 		2) all of the bricks are cleared.
	 * 
	 */
	private void gameplay (int startBricks){
		while (nBricksLeft > 0 && ball.getY() + 2 * BALL_RADIUS < getHeight()){
			ball.move(vx, vy);
			pause(DELAY);
			reactToWalls();
			reactToCollider(startBricks);
		}
	}
	/*method getCollidingObject
	 * **********************
	 * if the ball collides with (touches) another object on the screen, 
	 * getCollidingObject returns that object, whether it be a brick or the
	 * paddle. Otherwise, it returns null. Method functions by checking each corner
	 * of the ball for an object.
	 * 
	 */
	private GObject getCollidingObject(){
		if (getElementAt(ball.getX(), ball.getY()) != null){
			return (getElementAt(ball.getX(), ball.getY()));
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null){
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
		}
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null){
			return (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null){
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS));
		} else return null;
	}
	/*method reactToWalls
	 * ******************
	 * If the ball touches the side or top walls, it bounces off of the wall by 
	 * reversing its bounce direction. 
	 * If the ball touches the bottom wall, it is removed from the screen.
	 * 
	 */
	private void reactToWalls(){
		if (ball.getX() <= 0 || ball.getX() >= (getWidth() - (2 * BALL_RADIUS))){
			vx = -vx;
			bounceClip.play();
		}
		if (ball.getY() <= 0){
			vy = -vy;
			bounceClip.play();
		}
		if (ball.getY() + 2 * BALL_RADIUS >= getHeight()){
			remove(ball);
		}
	}
	/*method printFinishMessage
	 * **********************
	 * If the user has cleared all of the bricks, they receive a message saying that
	 * they win. Otherwise, (if they have run out of lives) they receive a message saying
	 * that they lose.
	 * 
	 */
	private void printFinishMessage(){
		GLabel finish = new GLabel("");
		if (nBricksLeft == 0){
			finish.setLabel("You win!!!! :D");
		} else {
			finish.setLabel("You lose :(");
		}
		finish.setLocation((getWidth()-finish.getWidth())/2, (getHeight()-finish.getAscent())/2);
		add (finish);
	}
	/*method startMessage
	 * ****************
	 * Displays a message before each round of the game instructing the
	 * user to click to start the round. Also displays how many lives the 
	 * player has left. Removes both messages after the user clicks the mouse.
	 * 
	 */
	private void startMessage(int livesLeft){
		GLabel start = new GLabel ("Click to start");
		GLabel lives =  new GLabel ("Lives left: " + livesLeft);
		start.setLocation((getWidth()-start.getWidth())/2, (getHeight()/2 - start.getAscent()));
		lives.setLocation((getWidth()-lives.getWidth())/2, (getHeight()/2 + lives.getAscent()));
		add(start);
		add(lives);
		waitForClick();
		remove(start);
		remove(lives);
	}
	/*method setup
	 * ************
	 * initializes the gameplay screen. Sets up the canvas and adds the bricks and 
	 * paddle to the screen.
	 * 
	 */
	private void setup(){
		// Set the window's title bar text
			setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
			setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

			drawBricks();
			drawPaddle();
			add(bricksLeft, BRICKS_LEFT_OFFSET, BRICKS_LEFT_OFFSET);
	}
	/*method reactToCollider
	 * ******************
	 * checks to see if the ball is colliding with an object, and, if it is,
	 * bounces off of it. If the colliding object is a brick it is removed 
	 * from the screen.
	 * 
	 */
	private void reactToCollider(int startBricks){
		GObject collider = getCollidingObject();
		if (collider != null && collider != bricksLeft){
			vy = -vy;
			bounceClip.play();
			if (collider != paddle){
				remove(collider);
				nBricksLeft --;
				bricksLeft.setLabel("Bricks left: " + nBricksLeft);
				if ((nBricksLeft == startBricks - 10) || (nBricksLeft == startBricks - 50)){
					speedUp();
				}
			}
		}
	}
	/*method speedUp
	 * ****************
	 * increases the ball's speed by a factor of 1.5 after the user clears 10 bricks.
	 * 
	 */
	private void speedUp(){
		GLabel fast = new GLabel ("Uh oh! Things are getting faster!!!");
		add (fast, (getWidth()-fast.getWidth())/2, (getHeight()-fast.getAscent())/2);
		vx *= SPEED_UP_FACTOR;
		vy *= SPEED_UP_FACTOR;
		pause(SPEED_UP_PAUSE);
		remove (fast);
	}

	
	private GOval ball;
	private GRect paddle;
	private double vx, vy;
	private RandomGenerator rgen= RandomGenerator.getInstance();
	private int nBricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au"); 
	private GLabel bricksLeft = new GLabel ("Bricks left: " + nBricksLeft);
}
