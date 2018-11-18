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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	private GRect tile=null;
	double mouseX;
	private GRect paddle=null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	double bsize=BALL_RADIUS*2;
	private double bricksleft = 100;
	private GOval ball=null;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/*
	 * This method sets all the important elements of the game and runs the play game method. 
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setBricks();
		setPaddle();
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//This loop allows the player to play for amount of times specified in 
		//the constant above. Right now, the player has three lives. 
		for(int i=0;i<NTURNS;i++) {
			if(bricksleft!=0) {
				playGame();
			}
		}
		if(bricksleft!=0) {
			removeAll();
			printYouLose();	
		}
	}

	/*
	 * Creates the ball and activates the method for the animation. 
	 */
	private void playGame() {
		ball=makeBall(bsize);
		add (ball);
		moveBall();
	}

	/*
	 * The method creates the bricks, determined by the constants defined above. In this case
	 * 100 bricks are set in a 10x10 format. The colors create a type of rainbow, with one color
	 * per two rows. 
	 */
	private void setBricks() {
		for(int r=0;r<NBRICK_ROWS;r++) {
			for(int c=0; c<NBRICK_COLUMNS; c++) {
				double ix=((CANVAS_WIDTH/2)-((NBRICK_COLUMNS*BRICK_WIDTH)/2)-((NBRICK_COLUMNS-1)*BRICK_SEP)/2);
				double cx=(ix+(c*BRICK_WIDTH)+(c*BRICK_SEP));
				double cy=BRICK_Y_OFFSET+(r*BRICK_HEIGHT)+(r*BRICK_SEP);
				tile=new GRect(BRICK_WIDTH,BRICK_HEIGHT);
				add(tile,cx,cy);
				tile.setFilled(true);
				//This loop sets a different color for every two rows. 
				if(r==0||r==1) {
					tile.setColor(Color.RED);
				}
				if(r==2||r==3) {
					tile.setColor(Color.ORANGE);
				}
				if(r==4||r==5) {
					tile.setColor(Color.YELLOW);
				}
				if(r==6||r==7) {
					tile.setColor(Color.GREEN);
				}
				if(r==8||r==9) {
					tile.setColor(Color.CYAN);
				}
			}
		}
	}
	/*
	 * This method creates the paddle at the bottom of the screen and adds 
	 * mouse listeners in order to have the paddle track mouse motion. 
	 */
	private void setPaddle() {
		double x = CANVAS_WIDTH/2-PADDLE_WIDTH/ 2;
		double y = CANVAS_HEIGHT-PADDLE_Y_OFFSET;
		paddle=new GRect (x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	/*
	 * Makes the paddle move with the mouse along the x-axis but not the y. 
	 */
	public void mouseMoved(MouseEvent e) {
		if(e.getX()>=BRICK_SEP && e.getX()<=CANVAS_WIDTH-PADDLE_WIDTH){
			paddle.setLocation(e.getX(),CANVAS_HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	/*
	 * Returns the ball, with its radius specified by the constants above. 
	 */
	private GOval makeBall(double bsize) {
		double cx=(CANVAS_WIDTH-bsize)/2;
		double cy=((CANVAS_HEIGHT-bsize)/2);
		GOval ball=new GOval(cx,cy,bsize,bsize);
		ball.setFilled(true);
		return ball;
	}
	/*
	 * Sets the ball in motion with a random x speed. Every time it bounces off the 
	 * top wall, it changes y-speed to be in the opposite direction. Every time it 
	 * bounces off the right or left wall, it reverses x-speed. And every time it 
	 * goes beyond the bottom boundary, it breaks the loop, decreasing one of the 
	 * "lives". 
	 */
	private void moveBall() {
		double vy=VELOCITY_Y;
		double vx=rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5))vx=-vx;
		waitForClick();
		while(true) {
			ball.move(vx, vy);
			if(ball.getY()<=0) {
				vy=-vy;
			}
			if(ball.getX()<=0) {
				vx=-vx;
			}
			if(ball.getX()>=CANVAS_WIDTH-bsize) {
				vx=-vx;
			}
			//This section codes for the ball's response to collisions. It calls
			//a method that will return the object the ball is colliding with 
			//at each X,Y coordinate. If it's null nothing will happen, if 
			//it's the paddle, the ball will bounce (Y-speed will be reversed)
			//and if its a brick then the brick will be removed, the amount of
			//bricks left will be decreased by one, and y-speed will again be reversed. 
			GObject collider = getCollidingObject();
			if(collider==paddle) {
				//Fixes sticky paddle
				if(vy>=0) {
					vy=-vy;
					bounceClip.play();
				}
			}
			else if(collider!=null) {
				remove(collider);
				vy=-vy;	
				bounceClip.play();
				bricksleft--;
				if(bricksleft==0) {
					removeAll();
					printWinner();
				}
			}
			if(ball.getY()>=CANVAS_HEIGHT) {
				break;
			}
			pause(DELAY);
		}
	}
	/*
	 * Returns the object the ball is colliding with. Checks all four corners
	 * of the ball. Returns null if no object is colliding. 
	 */
	private GObject getCollidingObject() {
		double x=ball.getX();
		double y=ball.getY();
		if(getElementAt(x,y)!=null) {
			return(getElementAt(x,y));
		}
		else if(getElementAt(x+bsize,y)!=null){
			return(getElementAt(x+bsize,y));

		}
		else if(getElementAt(x,y+bsize)!=null) {
			return(getElementAt(x,y+bsize));
		}
		else if(getElementAt(x+bsize,y+bsize)!=null) {
			return(getElementAt(x+bsize,y+bsize));
		} else {
			return null;
		}
	}
	/*
	 * Sets the text "You Win!", called by the run method when the 
	 * amount of bricks left is 0. 
	 */
	private void printWinner() {
		GLabel winner = new GLabel("You win!");
		winner.setFont("Helvetica-24");
		winner.setColor(Color.PINK);
		double y=(CANVAS_HEIGHT+winner.getAscent())/2;
		double x=(CANVAS_WIDTH-winner.getWidth())/2;
		add(winner,x,y);
	}
	/*
	 * Sets the text "You lose :(", called by the run method when 
	 * the user has lost all three lives. 
	 */
	private void printYouLose() {
		GLabel lose = new GLabel("You lose :(");
		lose.setFont("Helvetica-24");
		lose.setColor(Color.BLUE);
		double y=(CANVAS_HEIGHT+lose.getAscent())/2;
		double x=(CANVAS_WIDTH-lose.getWidth())/2;
		add(lose,x,y);
	}
}
