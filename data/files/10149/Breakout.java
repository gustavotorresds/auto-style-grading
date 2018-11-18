/*
 * File: Breakout.java
 * -------------------
 * Name:Cameron Haynesworth
 * Section Leader:Adam Mosharrafa
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
	// Instance Variables
	private GRect paddle=null;
	private GOval ball=null;
	private GRect brick=null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double  speed_y,speed_x;
	private int bricksHit=0;
	private int paddleHits=0;
	private int turn=3;
	
	//This run method adds the bricks and paddle to the field before allowing the game to be played
	//until all the bricks are hit or the player runs out of balls. Then the game ends. 
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		printBricks();
		printPaddle();
		addMouseListeners();
		while(turn>0 && bricksHit<100) {
			printBall();
			waitForClick();
			moveBall();
		}
		endGame();
	}
	//If the game ends because the user runs out of turns, this method tells the user they lost. 
	//If the game ends because all of the bricks are gone, it tells the user that they won. 
	private void endGame() {
		remove(ball);
		remove(paddle);
		GLabel winMessage= new GLabel("You win!");
		winMessage= new GLabel("You win!",(getWidth()-winMessage.getWidth())/2,(getHeight()-winMessage.getAscent())/2);
		GLabel lossMessage= new GLabel("You lose");
		lossMessage= new GLabel("You lose",(getWidth()-lossMessage.getWidth())/2,(getHeight()-lossMessage.getAscent())/2);
		if (turn==0) {
			add(lossMessage);
		}
		else {
			add(winMessage);
		}
	}
	/*This method sets the ball on an initial course in the negative y-direction, and a random x-direction.
	 *It checks for a collision before moving the ball again through the game. If the ball has hit the paddle 7 times,
	 *the ball's x-velocity is doubled for the rest of the game. If the ball hits the bottom of the world, the user
	 *loses a turn.
	 */
	private void moveBall() {
		speed_x=rgen.nextDouble(1.0, 3.0);
		speed_y = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) { 
			speed_x = -speed_x;
		}
		while(bricksHit<100) {
			checkForCollision();
			if(paddleHits<7) {
				ball.move(speed_x,speed_y);
			}
			else {
				ball.move(2*speed_x,speed_y);
			}
			pause(DELAY);
			if (ball.getY()>getHeight()-3) {
				remove(ball);
				turn--;
				break;
			}
		}
	/*
	 * This method checks to see if the ball is colliding with another entity in the game and makes the ball respond 
	 * accordingly. 
	 */
	}
	private void checkForCollision() {
		double ball_x = ball.getX();
		double ball_y = ball.getY();
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		GObject collObj1 = getElementAt(ball_x, ball_y);
		GObject collObj2 = getElementAt(ball_x, ball_y+2*BALL_RADIUS);
		GObject collObj3 = getElementAt(ball_x+2*BALL_RADIUS, ball_y);
		GObject collObj4 = getElementAt(ball_x+2*BALL_RADIUS, ball_y+2*BALL_RADIUS);
		//If the ball hits the paddle, a sound is played and it is reflected in the y-direction.
		if (collObj2 == paddle ||collObj4 == paddle) {
			bounceClip.play();
			speed_y= -1*speed_y;
			paddleHits++;
		}
		//If the ball contacts a brick, it removes the brick, a sound is played, and the ball is reflected in
		//the y direction.
		else {
			if (collObj1!=null) {
				speed_y= -1*speed_y;
				remove(collObj1);
				bounceClip.play();
				bricksHit++;
			}
			if (collObj2!=null && collObj2!=collObj1) {
				speed_y= -1*speed_y;
				remove(collObj2);
				bounceClip.play();
				bricksHit++;
			}
			if (collObj3!=null && collObj3!=collObj2 && collObj3 != collObj1) {
				speed_y= -1*speed_y;
				remove(collObj3);
				bounceClip.play();
				bricksHit++;
			}
			if (collObj4!=null && collObj4!=collObj3 && collObj4!=collObj2 && collObj4!=collObj1) {
				speed_y= -1*speed_y;
				remove(collObj4);
				bounceClip.play();
				bricksHit++;
			}
		}
		//If the ball hits the side wall of the world, it is reflected in the x direction.
		if(ball_x<=3 || ball_x>=(getWidth()-BALL_RADIUS*2)-3) {
			speed_x= -1*speed_x;
		}
		//If the ball hits the top of the world, it is reflected in the y-direction.
		if (ball.getY()<=3) {
			speed_y=-1*speed_y;
		}
	}
	//This method adds the game ball to the center of the screen.
	private void printBall() {
		double center_x = (getWidth()/2)-BALL_RADIUS;
		double center_y = (getHeight()/2)-BALL_RADIUS;
		ball = new GOval (center_x,center_y,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball,center_x,center_y);
	}
	//When the mouse is moved, this method sets the x position of the paddle to 
	//the x position of the mouse. 
	public void mouseMoved(MouseEvent e) {
		double paddle_x = (e.getX()-PADDLE_WIDTH/2);
		if (paddle_x>0 && paddle_x<getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(paddle_x, getHeight()-PADDLE_Y_OFFSET);
		}
	}
	//This method adds the paddle to the center of the world. 
	private void printPaddle() {
		double x= (getWidth()-PADDLE_WIDTH)/2;
		double y=(getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		paddle = new GRect (x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle,x,y);
	}

	// This method prints the cluster bricks for the game. 
	private void printBricks() {
		for (int row=0 ; row<(NBRICK_ROWS); row++ ) {
			for (int col=0 ; col<(NBRICK_COLUMNS); col++ ) {
				double x =(getWidth()/2)-((NBRICK_COLUMNS*BRICK_WIDTH)/2)-(BRICK_SEP*(NBRICK_COLUMNS-1)/2)+(BRICK_WIDTH+BRICK_SEP)*col;
				double y =(BRICK_Y_OFFSET+((BRICK_HEIGHT+BRICK_SEP)*row));
				brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				colorBrick(row);
				add(brick);
			}
		}
	}
	//Given the row that the brick is in, this method colors the brick the appropriate color. 
	private void colorBrick(int row ) {
		if (row==0 || row==1 ) {
			brick.setColor(Color.RED);
		}
		if (row==2 || row==3) {
			brick.setColor(Color.ORANGE);
		}
		if (row==4 || row==5) {
			brick.setColor(Color.YELLOW);
		}
		if (row==6 || row==7) {
			brick.setColor(Color.GREEN);
		}
		if (row>=8) {
			brick.setColor(Color.CYAN);
		}
	}
}
